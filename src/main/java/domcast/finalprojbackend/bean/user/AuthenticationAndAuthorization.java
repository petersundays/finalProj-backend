package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.dao.SessionTokenDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dao.ValidationTokenDao;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class AuthenticationAndAuthorization {

    private static final Logger logger = LogManager.getLogger(DataValidator.class);

    @EJB
    private SessionTokenDao sessionTokenDao;
    @EJB
    private UserDao userDao;
    @EJB
    private ProjectBean projectBean;
    @EJB
    private ValidationTokenDao validationTokenDao;

    /**
     * Checks if the password is correct
     * @param plainPassword the plain password
     * @param hashedPassword the hashed password
     * @return boolean value indicating if the password is correct
     */
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        logger.info("Checking if password is correct");
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            logger.error("Error while checking password: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the token is active and from a user id
     * @param token the token to be checked
     * @param userId the user id to be checked
     * @return boolean value indicating if the token is active and from the user id
     */
    public boolean isTokenActiveAndFromUserId(String token, int userId) {
        logger.info("Checking if token is active and from user id");
        try {
            return sessionTokenDao.isTokenActiveAndFromUserId(token, userId);
        } catch (Exception e) {
            logger.error("Error while checking if token is active and from user id: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the user is an admin
     * @param sessionToken the session token of the user
     * @return boolean value indicating if the user is an admin
     */
    public boolean isUserAdmin(String sessionToken) {
        logger.info("Checking if user is admin");
        try {
            return userDao.isUserAdmin(sessionToken);
        } catch (Exception e) {
            logger.error("Error while checking if user is admin: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the user is a member of the project
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is a member of the project
     */
    public boolean isUserMemberOfTheProject(int userId, int projectId) {
        logger.info("Checking if user with ID {} is a member of project with ID {}", userId, projectId);

        boolean isMember;
        try {
            isMember = projectBean.isUserActiveAndApprovedInProject(userId, projectId);
        } catch (Exception e) {
            logger.error("Error checking if user with ID {} is a member of project with ID {}", userId, projectId, e);
            throw new RuntimeException(e);
        }

        logger.info("User with ID {} is a member of project with ID {}: {}", userId, projectId, isMember);
        return isMember;
    }

    /**
     * Checks if the user is a member of the project and active
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is a member of the project
     */
    public boolean isUserMemberOfTheProjectAndActive(int userId, int projectId) {
        logger.info("Checking if user with ID {} is a member of project and active with ID {}", userId, projectId);

        boolean isMember;
        try {
            isMember = projectBean.isUserPartOfProjectAndActive(userId, projectId);
        } catch (Exception e) {
            logger.error("Error checking if user with ID {} is a member of project with ID {}", userId, projectId, e);
            throw new RuntimeException(e);
        }

        logger.info("User with ID {} is a member of project with ID {} and active: {}", userId, projectId, isMember);
        return isMember;
    }

    /**
     * Checks if the user's validation token is active and the user is not confirmed
     * @param token the validation token
     * @return boolean value indicating if the user's validation token is active and the user is not confirmed
     */
    public boolean isMemberNotConfirmedAndValTokenActive(String token) {
        logger.info("Checking if member is not confirmed and validation token is active");

        boolean authorized;

        try {
            authorized = validationTokenDao.isTokenActiveAndUserNotConfirmed(token);
            logger.info("Member is not confirmed and validation token is active: {}", authorized);
        } catch (Exception e) {
            logger.error("Error checking if member is not confirmed and validation token is active: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return authorized;
    }

    /**
     * Checks if the user is a manager in the project
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is a manager in the project
     */
    public boolean isUserManagerInProject(int userId, int projectId) {
        logger.info("Checking if user with ID {} is a manager in project with ID {}", userId, projectId);

        boolean isManager;
        try {
            isManager = projectBean.isUserManagerInProject(userId, projectId);
        } catch (Exception e) {
            logger.error("Error checking if user with ID {} is a manager in project with ID {}", userId, projectId, e);
            throw new RuntimeException(e);
        }

        logger.info("User with ID {} is a manager in project with ID {}: {}", userId, projectId, isManager);
        return isManager;
    }

    /**
     * Checks if the user is an admin by id
     * @param userId the id of the user
     * @return boolean value indicating if the user is an admin
     */
    public boolean isUserAdminById(int userId) {
        logger.info("Checking if user with ID {} is an admin", userId);
        boolean isAdmin;
        try {
            isAdmin = userDao.isUserAdminById(userId);
        } catch (Exception e) {
            logger.error("Error checking if user with ID {} is an admin", userId, e);
            throw new RuntimeException(e);
        }
        logger.info("User with ID {} is an admin: {}", userId, isAdmin);
        return isAdmin;
    }
}
