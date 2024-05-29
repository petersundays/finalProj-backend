package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.SystemEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * SystemDao class is responsible for performing database operations related to the SystemEntity.
 * It extends the AbstractDao class and inherits its methods.
 */
@Stateless
public class SystemDao extends AbstractDao<SystemEntity>{

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SkillDao.class);

    /**
     * Default constructor for SystemDao.
     * Calls the super constructor with the SystemEntity class as the parameter.
     */
    public SystemDao() {
        super(SystemEntity.class);
    }

    /**
     * Retrieves the session timeout from the database.
     * @return the session timeout if successful, null if an error occurs or no result found.
     */
    public Integer getSessionTimeout() {
        logger.info("Getting session timeout");
        try {
            return (Integer) em.createNamedQuery("System.getSessionTimeout").getSingleResult();
        } catch (NoResultException e) {
            logger.error("No session timeout found", e);
            return null;
        } catch (Exception e) {
            logger.error("Error getting session timeout", e);
            throw new RuntimeException("Error getting session timeout", e);
        }
    }

    /**
     * Retrieves the maximum number of members that can be part of a project from the database.
     * @return the maximum number of members if successful, null if an error occurs or no result found.
     */
    public Integer getProjectMaxUsers()
    {
        logger.info("Getting project max members");
        try {
            return (Integer) em.createNamedQuery("System.getProjectMaxMembers").getSingleResult();
        } catch (NoResultException e) {
            logger.error("No max users found", e);
            return null;
        } catch (Exception e) {
            logger.error("Error getting project max members", e);
            throw new RuntimeException("Error getting project max members", e);
        }
    }

    /**
     * Updates the session timeout in the database.
     * @param timeout the new session timeout to set.
     */
    public void setSessionTimeout(int timeout) {
        logger.info("Setting session timeout to {}", timeout);
        try {
            em.createNamedQuery("System.updateTimeout").setParameter("timeout", timeout).executeUpdate();
        } catch (Exception e) {
            logger.error("Error setting session timeout", e);
            throw new RuntimeException("Error setting session timeout", e);
        }
    }

    /**
     * Updates the maximum number of members that can be part of a project in the database.
     * @param maxMembers the new maximum number of members to set.
     */
    public void setProjectMaxMembers(int maxMembers) {
        logger.info("Setting project max members to {}", maxMembers);
        try {
            em.createNamedQuery("System.updateMaxMembers").setParameter("maxMembers", maxMembers).executeUpdate();
        } catch (Exception e) {
            logger.error("Error setting project max users", e);
            throw new RuntimeException("Error setting project max members", e);
        }
    }

    /**
     * Retrieves the number of system variables in the database.
     * @return the number of system variables if successful, 0 if an error occurs.
     */
    public int numberOfSystemVariables() {
        try {
            return ((int) em.createNamedQuery("System.numberOfSystemVariables").getSingleResult());
        } catch (Exception e) {
            return 0;
        }
    }
}