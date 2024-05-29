package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.bean.SystemBean;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.dto.UserDto.FullRegistration;
import domcast.finalprojbackend.dto.UserDto.LoggedUser;
import domcast.finalprojbackend.dto.UserDto.Login;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * Bean responsible for the user operations
 * @author José Castro
 * @author Pedro Domingos
 */

@Stateless
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

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

    // Default constructor
    public UserBean() {}

    /**
     * Registers an email in the database, when does the first registration
     * @param firstRegistration the email and password to be registered
     * @return boolean value indicating if the email was registered
     */
    public boolean registerEmail (FirstRegistration firstRegistration) {
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
        ValidationTokenEntity validationToken = tokenBean.generateValidationToken(userEntity, 48 * 60);
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
            logger.error("User is null");
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

        // Checks if the user has a phone
        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
            addInterestToUser(userEntity, user.getInterests());
        }

        // Checks if the user has a phone
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            addSkillToUser(userEntity, user.getSkills());
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
        if (!validatorAndHasher.checkPassword(login.getPassword(), user.getPassword())) {
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
            logger.error("Error while converting user to logged user");
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

        // Tries to inactivate the session token
        if (!tokenBean.setTokenInactive(sessionToken)) {
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

        // Add each of the user's interests to the interests list
        for(M2MUserInterest userInterest : user.getInterests()) {
            interests.add(userInterest.getInterest().getName());
        }

        // Add each of the user's skills to the skills list
        for(M2MUserSkill userSkill : user.getUserSkills()) {
            skills.add(userSkill.getSkill().getName());
        }

        // Set the LoggedUser's attributes using the UserEntity's attributes
        loggedUser.setEmail(user.getEmail());
        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());
        loggedUser.setWorkplace(user.getWorkplace().getCity().getValue());
        loggedUser.setBiography(user.getBiography());
        loggedUser.setPhoto(user.getPhoto());
        loggedUser.setNickname(user.getNickname());
        loggedUser.setSessionToken(sessionToken);
        loggedUser.setInterests(interests);
        loggedUser.setSkills(skills);

        // Return the LoggedUser object
        return loggedUser;
    }

    public void addInterestToUser(UserEntity user, ArrayList<String> interestsList) {
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
    }

    public void addSkillToUser(UserEntity user, ArrayList<String> skillsList) {
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

    }

    public boolean setSessionTimeout(int sessionTimeout) {
        try {
            systemBean.setSessionTimeout(sessionTimeout);
            return true;
        } catch (Exception e) {
            logger.error("Error while setting session timeout: {}", e.getMessage());
            return false;
        }
    }

    public boolean setProjectMaxUsers(int projectMaxUsers) {
        try {
            systemBean.setProjectMaxMembers(projectMaxUsers);
            return true;
        } catch (Exception e) {
            logger.error("Error while setting project max members: {}", e.getMessage());
            return false;
        }
    }


}
