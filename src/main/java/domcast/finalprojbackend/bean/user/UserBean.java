package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.bean.validationAndEncryption.EmailAndPassword;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.entity.UserEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;

@Stateless
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @EJB
    private UserDao userDao;
    @EJB
    private EmailAndPassword emailAndPassword;


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
        if (!emailAndPassword.isInputValid(firstRegistration)) {
            logger.error("Email or password are invalid");
            return false;
        }

        logger.info("Email and password are valid");

        try {
            // Encrypts the password using BCrypt
            String hashedPassword;
            try {
                hashedPassword = emailAndPassword.hashPassword(firstRegistration.getPassword());
            } catch (Exception e) {
                logger.error("Error while hashing password: {}", e.getMessage());
                return false;
            }

            // Defines the encrypted password
            firstRegistration.setPassword(hashedPassword);

            // Converts the firstRegistration to a user entity
            UserEntity userEntity = convertFirstRegistrationToUserEntity(firstRegistration);

            // Persists the user entity
            userDao.persist(userEntity);

            logger.info("Email registered: {}", firstRegistration.getEmail());
            return true;

        } catch (PersistenceException e) {
            logger.error("Error while persisting user: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return false;
        }

    }


   /* public boolean register(User user) {

        if (user != null) {
            logger.info("Registering user: {}", user.getUsername());

            if (user.getUsername().equalsIgnoreCase("notAssigned")) {
                user.setUsername(user.getUsername().toUpperCase());
                user.setTypeOfUser(User.NOTASSIGNED);
                user.setVisible(false);
                user.setConfirmed(true);

                //Encripta a password usando BCrypt
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                //Define a password encriptada
                user.setPassword(hashedPassword);

                //Persist o user
                userDao.persist(convertUserDtotoUserEntity(user));

                logger.info("User registered: {}", user.getUsername());

                return true;
            } else {
                if (user.getUsername().equals("admin")){
                    user.setTypeOfUser(300);
                    user.setVisible(true);
                    user.setConfirmed(true);

                } else {
                    if (user.getTypeOfUser() != 100 && user.getTypeOfUser() != 200 && user.getTypeOfUser() != 300) {
                        user.setInitialTypeOfUser();

                    } else {
                        if (user.getTypeOfUser() == 100) {
                            user.setTypeOfUser(User.DEVELOPER);
                        } else if (user.getTypeOfUser() == 200) {
                            user.setTypeOfUser(User.SCRUMMASTER);
                        } else if (user.getTypeOfUser() == 300) {
                            user.setTypeOfUser(User.PRODUCTOWNER);
                        }
                    }

                    user.setVisible(true);
                    user.setConfirmed(false);
                }

                logger.info("User type set to: {}", user.getTypeOfUser());

                if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().isBlank()) {
                    user.setPassword("");
                    logger.info("User {} has no password defined", user.getUsername());

                } else {

                    //Encripta a password usando BCrypt
                    String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

                    //Define a password encriptada
                    user.setPassword(hashedPassword);
                    logger.info("Password defined for user: {}", user.getUsername());
                }

                UserEntity userEntity = convertUserDtotoUserEntity(user);
                if (userEntity.getUsername().equalsIgnoreCase("admin")) {
                    userDao.persist(userEntity);
                    logger.info("Admin user registered: {}", user.getUsername());

                } else {
                    userEntity.setValidationToken(generateValidationToken(48 * 60));

                    if (emailBean.sendConfirmationEmail(user, userEntity.getValidationToken())) {
                        userDao.persist(userEntity);
                        logger.info("Confirmation email sent and user registered: {}", user.getUsername());
                        return true;
                    } else {
                        logger.error("Confirmation email not sent and user not registered: {}", user.getUsername());
                    }
                }
            }
        } else {
            return false;
        }
        logger.error("User not registered: {}", user.getUsername());
        return false;
    }*/


    public UserEntity convertFirstRegistrationToUserEntity(FirstRegistration firstRegistration) {
        UserEntity userEntity = new UserEntity();

        userEntity.setEmail(firstRegistration.getEmail());
        userEntity.setPassword(firstRegistration.getPassword());

        return userEntity;
    }



}
