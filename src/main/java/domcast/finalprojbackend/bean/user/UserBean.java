package domcast.finalprojbackend.bean.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.SystemBean;
import domcast.finalprojbackend.dao.*;
import domcast.finalprojbackend.dto.userDto.*;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.ProjectUserEnum;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.*;
import java.util.*;

/**
 * Bean responsible for the user operations
 * @author José Castro
 * @author Pedro Domingos
 */

@Stateless
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);
    private static final String SERVER_URL = "https://localhost:8443/";
    private static final String BASE_DIR = "domcast/";
    private static final String PHOTOS_DIR = BASE_DIR + "photos/";
    private static final String DEFAULT_PHOTO = "default/default_user.jpg";
    private static final String PROFILE_PIC = SERVER_URL + PHOTOS_DIR;

    @EJB
    private UserDao userDao;
    @EJB
    private DataValidator dataValidator;
    @EJB
    private TokenBean tokenBean;
    @EJB
    private EmailBean emailBean;
    @EJB
    private LabDao labDao;
    @EJB
    private InterestDao interestDao;
    @EJB
    private SkillDao skillDao;
    @EJB
    private SystemBean systemBean;
    @EJB
    private AuthenticationAndAuthorization authenticationAndAuthorization;
    @EJB
    private InterestBean interestBean;
    @EJB
    private SkillBean skillBean;
    @EJB
    private PasswordBean passwordBean;
    @EJB
    private M2MProjectUserDao m2MProjectUserDao;
    @EJB
    private ValidationTokenDao validationTokenDao;
    @EJB
    private SessionTokenDao sessionTokenDao;

    // Default constructor
    public UserBean() {}

    /**
     * Registers an email in the database, when does the first registration
     * @param firstRegistration the email and password to be registered
     * @return boolean value indicating if the email was registered
     */
    public boolean registerEmail (FirstRegistration firstRegistration, String ipAddress) {
        logger.info("Registering email: {}", firstRegistration.getEmail());

        // Checks if the email and password are valid
        if (!dataValidator.isInputValid(firstRegistration)) {
            logger.error("Email or password are invalid");
            return false;
        }

        logger.info("Email and password are valid");

        String hashedPassword = passwordBean.hashPassword(firstRegistration.getPassword());

        if (hashedPassword == null) {
            logger.error("Error while hashing password");
            return false;
        }

        logger.info("Password hashed");

        // Defines the encrypted password
        firstRegistration.setPassword(hashedPassword);

        // Converts the firstRegistration to a user entity
        UserEntity userEntity = convertFirstRegistrationToUserEntity(firstRegistration);

        // Generates a validation token
        ValidationTokenEntity validationToken = tokenBean.generateValidationToken(userEntity, 48 * 60, ipAddress);
        if (validationToken == null) {
            logger.error("Error while generating validation token");
            return false;
        }

        // Adds the validation token to the user entity
        userEntity.addValidationToken(validationToken);

        // Persists the user entity
        if (!userDao.persist(userEntity)) {
            logger.error("Error while persisting user");
            return false;
        }

        logger.info("Email registered: {}", firstRegistration.getEmail());

        // Sends the confirmation email
        if (!emailBean.sendConfirmationEmail(firstRegistration.getEmail(), validationToken.getToken())) {
            logger.error("Confirmation email not sent to: {}", firstRegistration.getEmail());
            return false;
        }

        logger.info("Confirmation email sent to: {}", firstRegistration.getEmail());
        return true;
    }

    /**
     * Completes the registration of a user
     * @param user the user to be registered
     * @param photoPath the path to the user's photo
     * @return the logged user
     */
    public boolean fullRegistration(FullRegistration user, String photoPath, String ipAddress) {
        // Checks if the user is null or if the validation token is null
        if (user == null || user.getValidationToken() == null) {
            logger.error("User is null or validation token is null");
            throw new IllegalArgumentException("User is null or validation token is null");
        }

        // Checks if the validation token is still valid and deletes the registration if it is not
        if (tokenBean.isValidationTokenExpired(user.getValidationToken())) {
            logger.error("Validation token is expired when trying to complete registration: {}", user.getValidationToken());
            // Retrieve the user associated with the expired token
            UserEntity userEntityToDelete = userDao.findUserByValidationToken(user.getValidationToken());
            if (userEntityToDelete != null) {
                logger.info("Deleting user with email, after validation token for registration has expired: {}", userEntityToDelete.getEmail());
                // Delete the user associated with the expired token
                if (!delete(userEntityToDelete.getEmail())) {
                    logger.error("Error deleting user with email: {}", userEntityToDelete.getEmail());
                }
            }
            return false;
        }

        logger.info("Completing registration for user with validation token: {}", user.getValidationToken());

        // Checks if the mandatory data is valid
        if (!dataValidator.isUserMandatoryDataValid(user)) {
            logger.error("Mandatory data is invalid");
            return false;
        }

        logger.info("Mandatory data is valid");

        // Finds the user by the validation token
        UserEntity userEntity = userDao.findUserByValidationToken(user.getValidationToken());

        // Checks if the user is null
        if (userEntity == null) {
            logger.error("User not found with validation token, when trying to complete registration: {}", user.getValidationToken());
            return false;
        }

        logger.info("User found with validation token, when trying to complete registration: {}", user.getValidationToken());

        // Registers the user's profile information for the first time
        userEntity = registerProfileInfo(userEntity, user, photoPath);

        // Checks if the user has interests
        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
            interestBean.addInterestToUser(userEntity.getId(), user.getInterests());
        }

        // Checks if the user has skills
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            skillBean.addSkillToUser(userEntity.getId(), user.getSkills());
        }

        // Tries to set the validation token as inactive
        if (!tokenBean.setTokenInactive(user.getValidationToken())) {
            logger.error("Error while inactivating validation token, after completing profile information, on register: {}", user.getValidationToken());
            return false;
        }

        logger.info("Registration completed for user with validation token: {}", user.getValidationToken());

        // Merges the user
        if (!userDao.merge(userEntity)) {
            logger.error("Error while merging in user, during registration process: {}", userEntity.getEmail());
            return false;
        }

        return true;
    }

    /**
     * Logs in a user
     * @param login the email and password to be logged in
     * @param ipAddress the IP address of the user
     * @return the logged user
     */
    public LoggedUser login (Login login, String ipAddress) {
        logger.info("Logging in user with email: {}", login.getEmail());

        // Checks if the login is null
        if (!dataValidator.isLoginValid(login)) {
            logger.error("Login is invalid");
            throw new IllegalArgumentException("Login is invalid");
        }

        logger.info("User input is valid");
        logger.info("Checking if login is valid");

        // Check if the login is valid, if not, throws an exception
        UserEntity user = userDao.findUserByEmail(login.getEmail());

        if (user == null || user.getType() == TypeOfUserEnum.NOT_CONFIRMED) {
            logger.error("User not found or not confirmed: {}", login.getEmail());
            throw new IllegalArgumentException("User not found or not confirmed");
        }

        logger.info("User found: {}", login.getEmail());

        // Check if the password is valid, if not, throws an exception
        if (!authenticationAndAuthorization.checkPassword(login.getPassword(), user.getPassword())) {
            logger.error("Invalid password for user: {}", login.getEmail());
            throw new IllegalArgumentException("Invalid password");
        }

        logger.info("Login attempt: Email and password are valid");

        // Generate a session token for user
        SessionTokenEntity sessionToken = tokenBean.generateSessionToken(user, ipAddress);

        // Checks if the session token is null, if so, throws an exception
        if (sessionToken == null) {
            logger.error("Error while generating session token");
            throw new IllegalArgumentException("Error while generating session token");
        }

        // Adds the session token to the user
        user.addSessionToken(sessionToken);

        logger.info("Session token generated");

        // Merges the user
        if (!userDao.merge(user)) {
            logger.error("Error while logging in user: {}", login.getEmail());
            throw new IllegalArgumentException("Error while logging in user");
        }

        logger.info("User logged in: {}", login.getEmail());

        // Converts the user entity to a logged user
        LoggedUser loggedUser = convertUserEntityToLoggedUser(user, sessionToken.getToken());

        // Checks if the logged user is null, if so, throws an exception
        if (loggedUser == null) {
            logger.error("Error while converting user to logged user, during login process");
            throw new IllegalArgumentException("Error while converting user to logged user");
        }

        // Returns the logged user
        return loggedUser;
    }


    public boolean logout (String sessionToken){
        logger.info("Logging out user with session token: {}", sessionToken);

        // Checks if the session token is null
        if (sessionToken == null) {
            logger.error("Session token is null");
            return false;
        }

        logger.info("Session token is not null");

        // Tries to inactivate the session token and set the session token logout time to the current time
        if (!tokenBean.setTokenInactive(sessionToken) || !tokenBean.setSessionTokenLogoutToNow(sessionToken)) {
            logger.error("Error while inactivating session token: {}", sessionToken);
            return false;
        }

        logger.info("Session token inactivated: {}", sessionToken);
        return true;
    }

    /**
     * Deletes a user from the database
     * @param email the email of the user to be deleted
     * @return boolean value indicating if the user was deleted
     */

    // This method is used to delete a user from the database in case the confirmation email is not sent
    public boolean delete(String email) {
        logger.info("Deleting user with email: {}", email);

        UserEntity user = userDao.findUserByEmail(email);

        if (user == null) {
            logger.error("User not found: {}", email);
            return false;
        }

        if (!userDao.remove(user)) {
            logger.error("Error while deleting user: {}", email);
            return false;
        } else {
            logger.info("User deleted: {}", email);
            return true;
        }
    }

    public UserEntity convertFirstRegistrationToUserEntity(FirstRegistration firstRegistration) {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(firstRegistration.getEmail());
        userEntity.setPassword(firstRegistration.getPassword());

        return userEntity;
    }

    /**
     * Converts a UserEntity object to a LoggedUser object.
     * This method is used when a user logs in and we need to return a LoggedUser object with the user's details.
     *
     * @param user The UserEntity object that represents the user who is logging in.
     * @param sessionToken The session token generated for the user's current session.
     * @return A LoggedUser object that contains the user's details and session token.
     */
    public LoggedUser convertUserEntityToLoggedUser(UserEntity user, String sessionToken) {
        // Create a new LoggedUser object
        LoggedUser loggedUser = new LoggedUser();

        // Create lists to store the user's interests and skills
        ArrayList<String> interests = new ArrayList<>();
        ArrayList<String> skills = new ArrayList<>();

        // Add each of the user's interests to the interest's list
        for(M2MUserInterest userInterest : user.getInterests()) {
            interests.add(userInterest.getInterest().getName());
        }

        // Add each of the user's skills to the skill's list
        for(M2MUserSkill userSkill : user.getUserSkills()) {
            skills.add(userSkill.getSkill().getName());
        }

        // Set the LoggedUser's attributes using the UserEntity's attributes
        //loggedUser.setEmail(user.getEmail());
        loggedUser.setId(user.getId());
        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());
        loggedUser.setWorkplace(user.getWorkplace().getCity().getValue());
        loggedUser.setBiography(user.getBiography());
        loggedUser.setVisible(user.isVisible());
        loggedUser.setPhoto(user.getPhoto());
        loggedUser.setNickname(user.getNickname());
        loggedUser.setSessionToken(sessionToken);
        loggedUser.setInterests(interests);
        loggedUser.setSkills(skills);

        // Return the LoggedUser object
        return loggedUser;
    }

    /*public void addInterestToUser(UserEntity user, ArrayList<String> interestsList) {
        if (user == null || interestsList == null) {
            logger.error("User and interests list must not be null");
            throw new IllegalArgumentException("User and interests list must not be null");
        }

        if (interestsList.isEmpty()) {
            logger.info("No interests to add to user");
            return;
        }

        logger.info("Adding interests to user");

        try {
            Set<InterestEntity> interests = interestDao.findInterestsByListOfNames(interestsList);

            if (interests.isEmpty()) {
                logger.info("No matching interests found in database");
                return;
            }

            for (InterestEntity interest : interests) {
                M2MUserInterest userInterest = new M2MUserInterest();
                userInterest.setUser(user);
                userInterest.setInterest(interest);
                user.addInterest(userInterest);
            }

            userDao.merge(user);

            logger.info("Interests added to user");

        } catch (NoResultException e) {
            logger.error("Error while finding interests: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error while adding interests to user: {}", e.getMessage());
            throw e;
        }
    }*/

    /*public void addSkillToUser(UserEntity user, ArrayList<String> skillsList) {
        if (user == null || skillsList == null) {
            logger.error("User and skills list must not be null");
            throw new IllegalArgumentException("User and skills list must not be null");
        }

        if (skillsList.isEmpty()) {
            logger.info("No skills to add to user");
            return;
        }

        logger.info("Adding skills to user");

        try {
            Set<SkillEntity> skills = skillDao.findSkillsByListOfNames(skillsList);

            if (skills.isEmpty()) {
                logger.info("No matching skills found in database");
                return;
            }

            for (SkillEntity skill : skills) {
                M2MUserSkill userSkill = new M2MUserSkill();
                userSkill.setUser(user);
                userSkill.setSkill(skill);
                user.addUserSkill(userSkill);
            }

            userDao.merge(user);

            logger.info("Skills added to user");

        } catch (NoResultException e) {
            logger.error("Error while finding skills: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error while adding skills to user: {}", e.getMessage());
            throw e;
        }

    }*/

    public boolean recoverPassword (String email, String ipAddress){
        logger.info("Recovering password for user with email: {}", email);

        // Checks if the email is null
        if (email == null) {
            logger.error("Email is null");
            return false;
        }

        logger.info("Email is not null");

        // Finds the user by the email
        UserEntity user = userDao.findUserByEmail(email);

        // Checks if the user is null
        if (user == null) {
            logger.error("User not found with email: {}", email);
            return false;
        }

        if (user.getType() == TypeOfUserEnum.NOT_CONFIRMED) {
            logger.error("User not confirmed: {}", email);
            return false;
        }

        logger.info("User found with email: {}", email);

        // Generates a validation token
        ValidationTokenEntity validationToken = tokenBean.generateValidationToken(user, 5, ipAddress);

        // Checks if the validation token is null
        if (validationToken == null) {
            logger.error("Error while generating validation token for password recovery");
            return false;
        }

        logger.info("Validation token generated for password recovery");

        // Adds the validation token to the user
        user.addValidationToken(validationToken);

        // Merges the user
        if (!userDao.merge(user)) {
            logger.error("Error while recovering password for user with email: {}", email);
            return false;
        }

        logger.info("Password recovery initiated for user with email: {}", email);

        // Sends the password recovery email
        if (!emailBean.sendPasswordResetEmail(email, user.getFirstName(), validationToken.getToken())) {
            logger.error("Password recovery email not sent to: {}", email);
            return false;
        }

        logger.info("Password recovery email sent to: {}", email);
        return true;
    }

    public boolean resetPassword (String validationToken, String password) {
        logger.info("Changing password for user with validation token: {}", validationToken);

        // Checks if the validation token is null
        if (validationToken == null) {
            logger.error("Validation token is null");
            return false;
        }

        logger.info("Validation token is not null");

        // Checks if the validation token is still valid
        if (tokenBean.isValidationTokenExpired(validationToken)) {
            logger.error("Validation token is expired: {}", validationToken);
            return false;
        }

        // Finds the user by the validation token
        UserEntity user = userDao.findUserByValidationToken(validationToken);

        // Checks if the user is null
        if (user == null) {
            logger.error("User not found with validation token: {}", validationToken);
            return false;
        }

        logger.info("User found with validation token: {}", validationToken);

        // Checks if the password is valid
        if (!passwordBean.isPasswordValid(password)) {
            logger.error("Password is invalid");
            return false;
        }

        logger.info("Password is valid");

        // Hashes the password
        String hashedPassword = passwordBean.hashPassword(password);

        // Checks if the hashed password is null
        if (hashedPassword == null) {
            logger.error("Error while hashing password");
            return false;
        }

        logger.info("Password hashed");

        // Sets the user's password to the hashed password
        user.setPassword(hashedPassword);

        // Tries to set the validation token as inactive
        if (!tokenBean.setTokenInactive(validationToken)) {
            logger.error("Error while inactivating validation token: {}", validationToken);
            return false;
        }

        // Merges the user
        if (!userDao.merge(user)) {
            logger.error("Error while changing password for user with validation token: {}", validationToken);
            return false;
        }

        logger.info("Password changed for user with validation token: {}", validationToken);
        return true;
    }

    /**
     * This method is responsible for uploading a user's photo.
     * It first verifies the user's existence using the provided token.
     * If the user exists, it reads the photo from the input, creates necessary directories,
     * and writes the photo to the appropriate location.
     * If the photo upload is successful, it updates the user's photo attribute in the database.
     *
     * @param token The token used to identify the user.
     * @param input The multipart form data input containing the photo.
     * @throws Exception If any error occurs during the photo upload process.
     */
    public String uploadPhoto(String token, MultipartFormDataInput input) throws Exception {
        logger.info("Uploading photo for user with token: {}", token);

        if (token == null || token.trim().isEmpty()) {
            logger.error("Token must not be null or empty");
            throw new IllegalArgumentException("Token must not be null or empty");
        }

        UserEntity user;
        try {
            user = validationTokenDao.findUserByToken(token);
        } catch (Exception e) {
            logger.error("Error while finding user with validation token: {}", token);
            throw new Exception("Error while finding user with token: " + token, e);
        }

        if (user != null) {
            logger.info("User found with token: {}", token);
        } else {
            try {
                user = sessionTokenDao.findUserByToken(token);
            } catch (Exception e) {
                logger.error("Error while finding user with session token: {}", token);
                throw new Exception("Error while finding user with token: " + token, e);
            }
        }

        if (user == null) {
            logger.error("User not found with token: {}", token);
            throw new Exception("User not found");
        }

        logger.info("User found with token: {}", token);

        // Get the photo from the input
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("photo");

        // If no photo is found in the input, set the default photo
        if (inputParts == null || inputParts.isEmpty()) {
            logger.info("No photo found in input, setting default photo");
            File defaultPhoto = new File(PROFILE_PIC + DEFAULT_PHOTO);
            if (defaultPhoto.exists()) {
                user.setPhoto(PROFILE_PIC + DEFAULT_PHOTO);
                userDao.merge(user);
            } else {
                logger.error("Default photo not found at path: {}", PROFILE_PIC + DEFAULT_PHOTO);
            }
            return PROFILE_PIC + DEFAULT_PHOTO;
        }

        // Process each input part
        for (InputPart inputPart : inputParts) {
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);

                // Validate the input photo
                if (!dataValidator.isValidImage(bytes)) {
                    logger.error("Invalid image file");
                    throw new Exception("Invalid image file");
                }

                // Create the necessary directories
                String photosDirPath = PHOTOS_DIR;
                File photosDir = new File(photosDirPath);
                if (!photosDir.exists() && !photosDir.mkdirs()) {
                    logger.error("Failed to create directory: {}", photosDirPath);
                    throw new Exception("Failed to create directory: " + photosDirPath);
                }

                String userDirPath = photosDirPath + user.getId();
                File userDir = new File(userDirPath);
                if (!userDir.exists() && !userDir.mkdirs()) {
                    logger.error("Failed to create directory: {}", userDirPath);
                    throw new Exception("Failed to create directory: " + userDirPath);
                }

                // Write the photo to the appropriate location
                String path = userDirPath + "/profile_pic_" + user.getId() + ".jpg";
                File file = new File(path);

                // If the file already exists, delete it
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    logger.info("Writing photo to file: {}", path);
                    fos.write(bytes);
                }

                // Update the user's photo attribute in the database
                try {
                    user.setPhoto(SERVER_URL + path);
                    userDao.merge(user);
                    logger.info("Photo uploaded for user with token: {}", token);
                } catch (Exception e) {
                    logger.error("Error while updating user's photo: {}", e.getMessage());
                    throw new Exception("Error while updating user's photo: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                throw new Exception("Error in uploadPhoto: " + e.getMessage(), e);
            }
        }

        return user.getPhoto();
    }

    /**
     * Updates the user's profile.
     * @param user The user DTO containing the new information.
     * @param userId The ID of the user to update.
     * @param photoPath The path to the new photo.
     * @param token The user's session token.
     * @return The updated logged user.
     */
    public LoggedUser updateUserProfile (UpdateUserDto user, int userId, String photoPath, String token) {
        logger.info("Updating user profile");

        UserEntity userEntity = userDao.findUserById(userId);

        // Checks if the user returned from the database is null
        if (userEntity == null) {
            throw new NoSuchElementException("User not found with id: " + user.getId());
        }

        // Checks if the user and photo path are null
        if (user == null && photoPath == null) {
            logger.error("User and photo path must not be null");
            throw new IllegalArgumentException("User and photo path must not be null");
        }

        // If the user is null, it means that the user is updating the photo only
        if (user == null) {
            return updateUserPhoto(userEntity, photoPath, token);
        }

        // If the photo path is null, it means that the user is updating the basic info only
        // If the user is updating both the basic info and the photo,
        // the photo path will be updated in the next step
        try {
            userEntity = updateBasicInfoIfChanged(userEntity, user, photoPath);

            userEntity = interestBean.updateUserInterestsIfChanged(userEntity, user);

            userEntity = skillBean.updateUserSkillsIfChanged(userEntity, user);

            userDao.merge(userEntity);

            LoggedUser loggedUser = convertUserEntityToLoggedUser(userEntity, token);

            if (loggedUser == null) {
                logger.error("Error while converting user to logged user");
                throw new IllegalArgumentException("Error while converting user to logged user");
            }

            // Returns the logged user
            return loggedUser;

        } catch (Exception e) {
            logger.error("Error while updating user profile: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Updates the user's photo.
     * @param userEntity The user entity to update.
     * @param photoPath The path to the new photo.
     * @param token The user's session token.
     * @return The updated logged user.
     */
    public LoggedUser updateUserPhoto(UserEntity userEntity, String photoPath, String token) {
        try {
            userEntity.setPhoto(photoPath);
            userDao.merge(userEntity);
            LoggedUser loggedUser = convertUserEntityToLoggedUser(userEntity, token);

            if (loggedUser == null) {
                logger.error("Error while converting user to logged user, after only updating photo");
                throw new IllegalArgumentException("Error while converting user to logged user");
            }

            // Returns the logged user
            return loggedUser;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers the user's profile information.
     * @param user The user entity to update.
     * @param userInfo The user DTO containing the new information.
     * @param photoPath The path to the new photo.
     * @return The updated user entity.
     */
    public UserEntity registerProfileInfo (UserEntity user, FullRegistration userInfo, String photoPath) {

        if (user == null) {
            throw new IllegalArgumentException("UserEntity must not be null");
        }

        // Finds the lab by the city
        LabEntity labEntity = labDao.findLabByCity(userInfo.getWorkplace());

        // Checks if the lab is null
        if (labEntity == null) {
            logger.error("Lab not found with city: {}", user.getWorkplace());
            throw new IllegalArgumentException("Lab not found with city: " + user.getWorkplace());
        }

        logger.info("Lab found with city: {}", user.getWorkplace());

        // Sets the user's attributes
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setWorkplace(labEntity);

        // Checks if the user has a biography
        if (userInfo.getBiography() != null) {
            user.setBiography(userInfo.getBiography());
        }

        // Checks if the user has a photo
        if (photoPath != null) {
            user.setPhoto(photoPath);
        }

        // Checks if the user has a nickname
        if (userInfo.getNickname() != null) {
            user.setNickname(userInfo.getNickname());
        }

        // Checks if the user is visible
        if (userInfo.isVisible()) {
            user.setVisible(userInfo.isVisible());
        }

        // Sets the user as confirmed
        user.setType(TypeOfUserEnum.STANDARD);

        return user;
    }
    /**
     * Updates the user's basic information if it has changed.
     * @param userEntity The user entity to update.
     * @param user The user DTO containing the new information.
     * @param photoPath The path to the new photo.
     * @return The updated user entity.
     */
    public UserEntity updateBasicInfoIfChanged (UserEntity userEntity, UpdateUserDto user, String photoPath) {

        if (userEntity == null) {
            throw new IllegalArgumentException("UserEntity must not be null");
        }

        logger.info("Updating basic info for user with id: {}", userEntity.getId());

        try {
            if (user.getFirstName() != null) {
                userEntity.setFirstName(user.getFirstName());
            }

            if (user.getLastName() != null) {
                userEntity.setLastName(user.getLastName());
            }

            if (user.getNickname() != null) {
                userEntity.setNickname(user.getNickname());
            }

            if (photoPath != null) {
                userEntity.setPhoto(photoPath);
            }

            if (user.getBiography() != null) {
                userEntity.setBiography(user.getBiography());
            }

            if (user.isVisible() != null) {
                userEntity.setVisible(user.isVisible());
            }

            if (user.getWorkplace() != null) {
                LabEntity labEntity = labDao.findLabByCity(user.getWorkplace());
                if (labEntity != null) {
                    userEntity.setWorkplace(labEntity);
                }
            }

            return userEntity;
        } catch (Exception e) {
            logger.error("Error while updating basic info: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts the user DTO from the input.
     * @param input The multipart form data input containing the user DTO.
     * @return The user DTO extracted from the input.
     * @throws IOException If an error occurs while extracting the user DTO.
     */
    public UpdateUserDto extractUpdateUserDto(MultipartFormDataInput input) throws IOException {
        InputPart part = input.getFormDataMap().get("user").get(0);
        String userString = part.getBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(userString, UpdateUserDto.class);
    }

    /**
     * Extracts the full registration DTO from the input.
     * @param input The multipart form data input containing the full registration DTO.
     * @return The full registration DTO extracted from the input.
     * @throws IOException If an error occurs while extracting the full registration DTO.
     */
    public FullRegistration extractFullRegistrationDto(MultipartFormDataInput input) throws IOException {
        InputPart part = input.getFormDataMap().get("user").get(0);
        String userString = part.getBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(userString, FullRegistration.class);
    }

    /**
     * Creates interests and skills when registering a user.
     * @param user The user DTO containing the interests and skills.
     * @return True if the interests and skills were created successfully, false otherwise.
     */
    public boolean createInterestsAndSkillsForRegistration(FullRegistration user) {
        boolean interestsCreated = interestBean.createInterests(user.getInterestDtos());
        boolean skillsCreated = skillBean.createSkills(user.getSkillDtos());
        return interestsCreated && skillsCreated;
    }

    /**
     * Creates interests and skills when updating a user.
     * @param user The user DTO containing the interests and skills.
     * @return True if the interests and skills were created successfully, false otherwise.
     */
    public boolean createInterestsAndSkillsForUpdate(UpdateUserDto user) {
        boolean interestsCreated = interestBean.createInterests(user.getInterestDtos());
        boolean skillsCreated = skillBean.createSkills(user.getSkillDtos());
        return interestsCreated && skillsCreated;
    }

    /**
     * Converts a user to a JSON string.
     * @param updatedUser The user to convert.
     * @return The user as a JSON string.
     * @throws JsonProcessingException If an error occurs while converting the user to JSON.
     */
    public String convertUserToJson(LoggedUser updatedUser) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(updatedUser);
    }

    /**
     * Returns a list of users based on the search criteria.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param nickname The nickname of the user.
     * @param workplace The workplace of the user.
     * @param orderBy The field to order the results by.
     * @param orderAsc True if the results should be ordered in ascending order, false otherwise.
     * @param pageNumber The page number of the results.
     * @param pageSize The number of results per page.
     * @return A list of users based on the search criteria.
     */
    public List<SearchedUser> getUsersByCriteria(String firstName, String lastName, String nickname, String workplace, String orderBy, boolean orderAsc, int pageNumber, int pageSize) {

        // Validate page size and page number
        if (!dataValidator.isPageSizeValid(pageSize) || !dataValidator.isPageNumberValid(pageNumber)) {
            logger.error("Invalid page size or page number");
            return new ArrayList<>();
        }

        // Validate orderBy field
        List<String> allowedOrderByFields = Arrays.asList("firstName", "lastName", "nickname", "workplace");
        if (!allowedOrderByFields.contains(orderBy)) {
            logger.error("Invalid orderBy field");
            return new ArrayList<>();
        }

        List<SearchedUser> searchedUsers = new ArrayList<>();
        try {
            List<UserEntity> users = userDao.getUsersByCriteria(firstName, lastName, nickname, workplace, orderBy, orderAsc, pageNumber, pageSize);
            if (users != null && !users.isEmpty()) {
                logger.info("Users found in DAO: {}", users.size());
                for (UserEntity user : users) {
                    if (user != null) {
                        SearchedUser searchedUser = entityToSearchedUser(user);
                        searchedUsers.add(searchedUser);
                    }
                }
                logger.info("Users found: {}", searchedUsers.size());
            }
        } catch (Exception e) {
            logger.error("Error occurred while getting users by criteria", e);
        }
        return searchedUsers;
    }

    /**
     * Converts a UserEntity object to a SearchedUser object.
     * @param user The UserEntity object to convert.
     * @return The SearchedUser object.
     */
    private static SearchedUser entityToSearchedUser(UserEntity user) {
        SearchedUser searchedUser = new SearchedUser();
        searchedUser.setId(user.getId());
        searchedUser.setFirstName(user.getFirstName());
        searchedUser.setLastName(user.getLastName());
        searchedUser.setNickname(user.getNickname());
        searchedUser.setWorkplace(user.getWorkplace().getCity().getValue());
        searchedUser.setPhoto(user.getPhoto());
        searchedUser.setVisible(user.isVisible());
        return searchedUser;
    }

    /**
     * Updates the user type.
     * @param id The ID of the user.
     * @param type The new type of the user.
     * @return A message indicating the result of the operation.
     */
    public String updateUserType(int loggedId, int id, int type) {
        String message = "";
        logger.info("Updating user type for user with id: {}", id);

        if (loggedId == id) {
            message = "User cannot change its own type";
            logger.error(message);
            return message;
        }

        try {
            int actualTypeOfUser = userDao.getUserType(id);
            if (TypeOfUserEnum.isNotConfirmed(actualTypeOfUser)) {
                message = "The user is not confirmed, please confirm the user first";
                logger.error(message);
                return message;
            }

            if (actualTypeOfUser == type) {
                message = "User type is already set to: " + type;
                logger.info(message);
                return message;
            }

        } catch (Exception e) {
            message = "Error while getting user type for user with id: " + id;
            logger.error(message);
            return message;
        }

        if (!TypeOfUserEnum.isValidId(type)) {
            message = "Invalid user type: " + type;
            logger.error(message);
            return message;
        }

        if (TypeOfUserEnum.isNotConfirmed(type)) {
            message = "User type cannot be set to NOT_CONFIRMED";
            logger.error(message);
            return message;
        }

        try {
            if (userDao.setUserType(id, type)) {
                message = "User type updated for user with id: " + id;
                logger.info(message);
            } else {
                message = "Error while updating user type for user with id: " + id;
                logger.error(message);
            }
        } catch (Exception e) {
            message = "Something went wrong while updating user type for user with id: " + id;
            logger.error(message);
        }
        return message;
    }

    /**
     * Returns the public profile of a user.
     * This method is used when a user's public profile is requested.
     * @param id The ID of the user.
     *           If the ID is null, an IllegalArgumentException is thrown.
     * @return The public profile of the user.
     */
    public PublicProfileUser returnPublicProfile (Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        logger.info("Getting public profile for user with id: {}", id);

        try {
            UserEntity user = userDao.findUserById(id);

            if (user == null) {
                logger.error("User not found with id: {}", id);
                throw new NoSuchElementException("User not found with id: " + id);
            }

            if (user.getType() == TypeOfUserEnum.NOT_CONFIRMED) {
                logger.error("Cannot get public profile for user with id: {}. User not confirmed", id);
                throw new IllegalArgumentException("User not confirmed");
            }

            if (!user.isVisible()) {
                logger.error("User is not visible: {}", id);
                throw new IllegalArgumentException("User's profile is private");
            }

            logger.info("User found with id: {}", id);

            return convertUserEntityToPublicProfile(user);
        } catch (Exception e) {
            logger.error("Error while getting public profile for user with id: {}. Error: {}", id, e.getMessage());
            throw e;
        }
    }

    /**
     * Converts a UserEntity object to a PublicProfileUser object.
     * This method is used when a user's public profile is requested.
     *
     * @param user The UserEntity object that represents the user whose public profile is requested.
     *             If the user is null, an IllegalArgumentException is thrown.
     * @return A PublicProfileUser object that contains the user's public profile details.
     */
    public PublicProfileUser convertUserEntityToPublicProfile(UserEntity user) {
        if (user == null) {
            throw new IllegalArgumentException("UserEntity cannot be null");
        }

        logger.info("Converting user entity to public profile user for user with id: {}", user.getId());

        Set<String> interests = new HashSet<>();
        Set<String> skills = new HashSet<>();

        PublicProfileUser publicProfileUser = new PublicProfileUser();
        publicProfileUser.setId(user.getId());
        publicProfileUser.setFirstName(user.getFirstName());
        publicProfileUser.setLastName(user.getLastName());
        publicProfileUser.setWorkplace(user.getWorkplace() != null ? user.getWorkplace().getCity().getValue() : "N/A");
        publicProfileUser.setBiography(user.getBiography() != null ? user.getBiography() : "N/A");
        publicProfileUser.setPhoto(user.getPhoto() != null ? user.getPhoto() : "N/A");
        publicProfileUser.setNickname(user.getNickname() != null ? user.getNickname() : "N/A");

        // Add each of the user's interests to the interest's list
        if (user.getInterests() != null) {
            for (M2MUserInterest userInterest : user.getInterests()) {
                interests.add(userInterest.getInterest().getName());
            }
        }

        // Add each of the user's skills to the skill's list
        if (user.getUserSkills() != null) {
            for (M2MUserSkill userSkill : user.getUserSkills()) {
                skills.add(userSkill.getSkill().getName());
            }
        }

        publicProfileUser.setInterests(interests);
        publicProfileUser.setSkills(skills);

        return publicProfileUser;
    }

    public ProjectUser projectUserToProjectUserDto(M2MProjectUser m2mProjectUser) {
        logger.info("Converting M2MProjectUser to ProjectUser");

        if (m2mProjectUser == null) {
            logger.error("M2MProjectUser is null");
            throw new IllegalArgumentException("M2MProjectUser cannot be null");
        }

        UserEntity userEntity = m2mProjectUser.getUser();
        if (userEntity == null) {
            logger.error("UserEntity in M2MProjectUser is null");
            throw new IllegalArgumentException("UserEntity in M2MProjectUser cannot be null");
        }

        ProjectUserEnum role = m2mProjectUser.getRole();
        if (role == null) {
            logger.error("Role in M2MProjectUser is null");
            throw new IllegalArgumentException("Role in M2MProjectUser cannot be null");
        }

        ProjectUser projectUser = new ProjectUser();
        projectUser.setId(userEntity.getId());
        projectUser.setFirstName(userEntity.getFirstName());
        projectUser.setLastName(userEntity.getLastName());
        projectUser.setRole(role.getId());

        logger.info("Successfully converted M2MProjectUser to ProjectUser");

        return projectUser;
    }

    /**
     * Converts a set of M2MProjectUser objects to a set of ProjectUser objects.
     * This method is used when a project's team members are requested.
     *
     * @param m2mProjectUsers The set of M2MProjectUser objects that represent the project's team members.
     *                        If the set is null, an IllegalArgumentException is thrown.
     * @return A set of ProjectUser objects that contain the project's team members' details.
     */
    public Set<ProjectUser> projectUsersToListOfProjectUser(Set<M2MProjectUser> m2mProjectUsers) {
        logger.info("Converting project team members from M2MProjectUser to ProjectUser");

        if (m2mProjectUsers == null) {
            logger.error("Project team members are null");
            throw new IllegalArgumentException("M2MProjectUser cannot be null");
        }

        Set<ProjectUser> projectUsers = new HashSet<>();
        for (M2MProjectUser m2mProjectUser : m2mProjectUsers) {
            if (m2mProjectUser == null) {
                logger.warn("Encountered null M2MProjectUser in set, skipping this entry");
                continue;
            }
            projectUsers.add(projectUserToProjectUserDto(m2mProjectUser));
        }

        logger.info("Successfully converted project team members from M2MProjectUser to ProjectUser");

        return projectUsers;
    }

    /**
     * Extracts the project team from the input.
     * @param input The multipart form data input containing the project team.
     * @return The project team extracted from the input.
     * @throws IOException If an error occurs while extracting the project team.
     */
    public ProjectTeam extractProjectTeam(MultipartFormDataInput input) throws IOException {
        InputPart part = input.getFormDataMap().get("team").get(0);
        String userString = part.getBodyAsString();
        ObjectMapper mapper = new ObjectMapper();

        // Convert the JSON string into a Map<String, Integer>
        Map<String, Integer> stringMap = mapper.readValue(userString, new TypeReference<Map<String, Integer>>() {});

        // Use the convertKeysToList method to get a List of the keys
        List<String> keys = convertKeysToList(stringMap);

        // Create a new Map<Integer, Integer> and populate it with the entries from stringMap,
        // converting the keys from strings to integers
        Map<Integer, Integer> integerMap = new HashMap<>();
        for (String key : keys) {
            integerMap.put(Integer.parseInt(key), stringMap.get(key));
        }

        // Create a new ProjectTeam object and set the projectUsers field to integerMap
        ProjectTeam projectTeam = new ProjectTeam();
        projectTeam.setProjectUsers(integerMap);

        return projectTeam;
    }

    public List<String> convertKeysToList(Map<String, ?> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }
        return new ArrayList<>(map.keySet());
    }
}
