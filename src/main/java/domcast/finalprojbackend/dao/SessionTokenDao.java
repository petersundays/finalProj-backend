package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.SessionTokenEntity;
import domcast.finalprojbackend.entity.ValidationTokenEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Data access object for session token entity
 * @see SessionTokenEntity
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class SessionTokenDao extends ValidationTokenDao {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SessionTokenDao.class);

    /**
     * Default constructor for the session token dao
     */
    public SessionTokenDao() {
         super();
    }

    /**
     * Checks if the session token is from an admin type user
     * @param token the session token to be checked
     * @return boolean value indicating if the session token is from an admin type user
     */
    public boolean isSessionTokenFromAdminTypeUser(String token) {
        logger.info("Checking if session token {} is from admin type user", token);
        try {
            return (Long) em.createNamedQuery("SessionToken.isSessionTokenFromAdminTypeUser")
                    .setParameter("token", token)
                    .getSingleResult() > 0;
        } catch (NoResultException e) {
            logger.error("No session token found for token {}", token, e);
            return false;
        } catch (NonUniqueResultException e) {
            logger.error("Multiple session tokens found for token {}", token, e);
            return false;
        } catch (Exception e) {
            logger.error("Error checking if session token is from admin type user", e);
            return false;
        }
    }

}

