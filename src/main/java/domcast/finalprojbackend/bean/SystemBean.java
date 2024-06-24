package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.SystemDao;
import domcast.finalprojbackend.entity.SessionTokenEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.List;

/**
 * SystemBean is a stateless EJB that provides an interface for interacting with system settings.
 * It uses the SystemDao to perform database operations.
 */
@Stateless
public class SystemBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SystemBean.class);

    // Inject the SystemDao EJB
    @EJB
    private SystemDao systemDao;
    @EJB
    private TokenBean tokenBean;
    @EJB
    private UserBean userBean;

    /**
     * Default constructor for SystemBean.
     */
    public SystemBean() {
        super();
    }

    /**
     * Retrieves the session timeout from the database.
     * @return the session timeout.
     */
    public int getSessionTimeout() {
        try {
            return systemDao.getSessionTimeout();
        } catch (Exception e) {
            logger.error("Error getting session timeout", e);
            throw new RuntimeException("Error getting session timeout", e);
        }
    }

    /**
     * Retrieves the maximum number of users that can be part of a project from the database.
     * @return the maximum number of users.
     */
    public int getProjectMaxUsers() {
        try {
            return systemDao.getProjectMaxUsers();
        } catch (Exception e) {
            logger.error("Error getting project max users", e);
            throw new RuntimeException("Error getting project max users", e);
        }
    }

    /**
     * Updates the session timeout in the database.
     * @param timeout the new session timeout to set.
     * @return boolean indicating if the operation was successful
     */
    public boolean setSessionTimeout(int timeout) {
        try {
            logger.info("Setting session timeout to {}", timeout);
            systemDao.setSessionTimeout(timeout);
            return true;
        } catch (Exception e) {
            logger.error("Error setting session timeout", e);
            return false;
        }
    }

    /**
     * Updates the maximum number of users that can be part of a project in the database.
     * @param maxMembers the new maximum number of members to set.
     * @return boolean indicating if the operation was successful
     */
    public boolean setProjectMaxMembers(int maxMembers) {
        try {
            logger.info("Setting project max users to {}", maxMembers);
            systemDao.setProjectMaxMembers(maxMembers);
            return true;
        } catch (Exception e) {
            logger.error("Error setting project max users", e);
            return false;
        }
    }

    /**
     * Retrieves the number of system variables from the database.
     * @return the number of system variables.
     */
    public int numberOfSystemVariables() {
        try {
            return systemDao.numberOfSystemVariables();
        } catch (Exception e) {
            logger.error("Error getting number of system variables", e);
            throw new RuntimeException("Error getting number of system variables", e);
        }
    }

    /**
     * Session timer that checks for active sessions that have exceeded the timeout every 30 seconds and logs them out.
     */
    @Schedule(second="*/30", minute="*", hour="*") // this automatic timer is set to expire every 30 seconds
    public void sessionTimer() throws Exception {
        logger.info("Session timer started");

        try {
            // Find active sessions that have exceeded the timeout
            List<SessionTokenEntity> activeSessions = tokenBean.findActiveSessionsExceededTimeout(getSessionTimeout());

            // Log out the active sessions that have exceeded the timeout
            for (SessionTokenEntity session : activeSessions) {
                logger.info("Session token {} has exceeded the timeout", session.getToken());

                try {
                    userBean.logout(session.getToken());
                    logger.info("Session token {} has been logged out", session.getToken());
                } catch (Exception e) {
                    logger.error("Error setting session token {} logout time to now", session.getToken(), e);
                    throw e;
                }
            }
        } catch (Exception e) {
            logger.error("Error in session timer", e);
            throw e;
        } finally {
            logger.info("Session timer ended");
        }
    }


}