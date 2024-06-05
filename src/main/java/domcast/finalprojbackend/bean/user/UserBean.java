package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.SystemBean;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.UserDto.*;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
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
    private static final String PHOTO_STORAGE_PATH = "C:\\wildfly-30.0.1.Final\\bin";

    @EJB
    private UserDao userDao;
    @EJB
    private ValidatorAndHasher validatorAndHasher;
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
        if (!validatorAndHasher.isInputValid(firstRegistration)) {
            logger.error("Email or password are invalid");
            return false;
        }

        logger.info("Email and password are valid");

        String hashedPassword = validatorAndHasher.hashPassword(firstRegistration.getPassword());

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
     * @return boolean value indicating if the registration was completed
     */
    public boolean fullRegistration(FullRegistration user) {

        // Checks if the user is null or if the validation token is null
        if (user == null || user.getValidationToken() == null) {
            logger.error("User is null or validation token is null");
            return false;
        }

        // Checks if the validation token is still valid and deletes the registration if it is not
        if (tokenBean.isValidationTokenExpired(user.getValidationToken())) {
            logger.error("Validation token is expired: {}", user.getValidationToken());
            // Retrieve the user associated with the expired token
            UserEntity userEntityToDelete = userDao.findUserByValidationToken(user.getValidationToken());
            if (userEntityToDelete != null) {
                logger.info("Deleting user with email: {}", userEntityToDelete.getEmail());
                // Delete the user associated with the expired token
                if (!delete(userEntityToDelete.getEmail())) {
                    logger.error("Error deleting user with email: {}", userEntityToDelete.getEmail());
                }
            }
            return false;
        }

        logger.info("Completing registration for user with validation token: {}", user.getValidationToken());

        // Checks if the mandatory data is valid
        if (!validatorAndHasher.isMandatoryDataValid(user)) {
            logger.error("Mandatory data is invalid");
            return false;
        }

        logger.info("Mandatory data is valid");

        // Finds the user by the validation token
        UserEntity userEntity = userDao.findUserByValidationToken(user.getValidationToken());

        // Checks if the user is null
        if (userEntity == null) {
            logger.error("User not found with validation token: {}", user.getValidationToken());
            return false;
        }

        logger.info("User found with validation token: {}", user.getValidationToken());

        // Finds the lab by the city
        LabEntity labEntity = labDao.findLabByCity(user.getWorkplace());

        // Checks if the lab is null
        if (labEntity == null) {
            logger.error("Lab not found with city: {}", user.getWorkplace());
            return false;
        }

        logger.info("Lab found with city: {}", user.getWorkplace());

        // Sets the user's attributes
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setWorkplace(labEntity);

        // Checks if the user has a biography
        if (user.getBiography() != null) {
            userEntity.setBiography(user.getBiography());
        }

        // Checks if the user has a photo
        if (user.getPhoto() != null) {
            userEntity.setPhoto(user.getPhoto());
        }

        // Checks if the user has a nickname
        if (user.getNickname() != null) {
            userEntity.setNickname(user.getNickname());
        }

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
            logger.error("Error while inactivating validation token: {}", user.getValidationToken());
            return false;
        }

        // Sets the user as confirmed
        userEntity.setType(TypeOfUserEnum.STANDARD);

        // Merges the user entity
        // If the merge fails, returns false
        if (!userDao.merge(userEntity)) {
            logger.error("Error while completing registration for user with validation token: {}", user.getValidationToken());
            return false;
        }

        logger.info("Registration completed for user with validation token: {}", user.getValidationToken());
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
        if (!validatorAndHasher.isLoginValid(login)) {
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
        if (!validatorAndHasher.isPasswordValid(password)) {
            logger.error("Password is invalid");
            return false;
        }

        logger.info("Password is valid");

        // Hashes the password
        String hashedPassword = validatorAndHasher.hashPassword(password);

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
        UserEntity user = userDao.findUserByActiveValidationOrSessionToken(token);
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
            String defaultPhotoPath = PHOTO_STORAGE_PATH + File.separator + "photos" + File.separator + "default" + File.separator + "default_user.jpg";
            File defaultPhoto = new File(defaultPhotoPath);
            if (defaultPhoto.exists()) {
                user.setPhoto(defaultPhotoPath);
                userDao.merge(user);
            } else {
                logger.error("Default photo not found at path: {}", defaultPhotoPath);
            }
            return defaultPhotoPath;
        }

        // Process each input part
        for (InputPart inputPart : inputParts) {
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);

                // Validate the input photo
                if (!validatorAndHasher.isValidImage(bytes)) {
                    logger.error("Invalid image file");
                    throw new Exception("Invalid image file");
                }

                // Create the necessary directories
                String photosDirPath = PHOTO_STORAGE_PATH + File.separator + "photos";
                File photosDir = new File(photosDirPath);
                if (!photosDir.exists() && !photosDir.mkdirs()) {
                    logger.error("Failed to create directory: {}", photosDirPath);
                    throw new Exception("Failed to create directory: " + photosDirPath);
                }

                String userDirPath = photosDirPath + File.separator + user.getId();
                File userDir = new File(userDirPath);
                if (!userDir.exists() && !userDir.mkdirs()) {
                    logger.error("Failed to create directory: {}", userDirPath);
                    throw new Exception("Failed to create directory: " + userDirPath);
                }

                // Write the photo to the appropriate location
                String path = userDirPath + File.separator + "profile_pic_" + user.getId() + ".jpg";
                File file = new File(path);

                // If the file already exists, delete it
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    logger.info("Writing photo to file: {}", path);
                    fos.write(bytes);
                }

                // Update the user's photo attribute in the database
                try {
                    user.setPhoto(path);
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
            return updatePhoto(userEntity, photoPath, token);
        }

        // If the photo path is null, it means that the user is updating the basic info only
        // If the user is updating both the basic info and the photo,
        // the photo path will be updated in the next step
        try {
            userEntity = updateBasicInfoIfChanged(userEntity, user, photoPath);

            userEntity = updateUserInterestsIfChanged(userEntity, user);

            userEntity = updateUserSkillsIfChanged(userEntity, user);

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
    public LoggedUser updatePhoto (UserEntity userEntity, String photoPath, String token) {
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
     * Updates the user's interests if they have changed.
     * @param userEntity The user entity to update.
     * @param user The user DTO containing the new information.
     * @return The updated user entity.
     */
    public UserEntity updateUserInterestsIfChanged (UserEntity userEntity, UpdateUserDto user) {
        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
            userEntity.getInterests().clear();
            interestBean.addInterestToUser(userEntity.getId(), user.getInterests());
        }
        return userEntity;
    }

    /**
     * Updates the user's skills if they have changed.
     * @param userEntity The user entity to update.
     * @param user The user DTO containing the new information.
     * @return The updated user entity.
     */
    public UserEntity updateUserSkillsIfChanged (UserEntity userEntity, UpdateUserDto user) {
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            userEntity.getUserSkills().clear();
            skillBean.addSkillToUser(userEntity.getId(), user.getSkills());
        }
        return userEntity;
    }
}
