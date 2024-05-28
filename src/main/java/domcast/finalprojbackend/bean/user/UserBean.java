package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.dto.UserDto.FullRegistration;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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

    // Default constructor
    public UserBean() {}

    /*public void createDefaultUsersIfNotExistent() {
        logger.info("Creating default users if not existent");

        UserEntity userEntity = userDao.findUserByUsername("admin");
        if (userEntity == null) {
            logger.info("Creating admin user");

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setEmail("admin@admin.com");
            admin.setFirstName("admin");
            admin.setLastName("admin");
            admin.setPhone("123456789");
            admin.setPhotoURL("https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png");
            admin.setVisible(false);
            admin.setConfirmed(true);

            register(admin);
            logger.info("Admin user created");
        }

        UserEntity userEntity2 = userDao.findUserByUsername("NOTASSIGNED");
        if (userEntity2 == null) {
            logger.info("Creating NOTASSIGNED user");

            User deletedUser = new User();
            deletedUser.setUsername("NOTASSIGNED");
            deletedUser.setPassword("123");
            deletedUser.setEmail("deleted@user.com");
            deletedUser.setFirstName("Deleted");
            deletedUser.setLastName("User");
            deletedUser.setPhone("123456788");
            deletedUser.setPhotoURL("https://www.pngitem.com/pimgs/m/146-1468479_my-profile-icon-blank-profile-picture-circle-hd.png");
            deletedUser.setTypeOfUser(400);
            deletedUser.setVisible(false);
            deletedUser.setConfirmed(true);

            register(deletedUser);
            logger.info("NOTASSIGNED user created");

        }
    }*/

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



    }
