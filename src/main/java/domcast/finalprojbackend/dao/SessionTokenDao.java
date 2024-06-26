package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.SessionTokenEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Sets the session token logout time to now
     * @param token the session token to be updated
     * @return boolean value indicating if the operation was successful
     */
    public boolean setSessionTokenLogoutToNow(String token) {
        logger.info("Setting session token {} logout time to now", token);
        try {
            int updatedEntities = em.createNamedQuery("SessionToken.setSessionTokenLogoutToNow")
                    .setParameter("token", token)
                    .executeUpdate();
            return updatedEntities > 0;
        } catch (Exception e) {
            logger.error("Error setting session token {} logout time to now", token, e);
            return false;
        }
    }

    /**
     * Finds the active sessions that have exceeded the timeout
     * @param timeout the timeout to be checked in seconds
     * @return the list of session tokens that have exceeded the timeout
     */
    public List<SessionTokenEntity> findActiveSessionsExceededTimeout(int timeout, LocalDateTime currentTime) {
        logger.info("Finding active sessions that have exceeded the timeout");
        LocalDateTime timeBeforeNow = currentTime.minusMinutes(timeout);
        try {
            return em.createNamedQuery("SessionToken.findActiveSessionsExceededTimeout", SessionTokenEntity.class)
                    .setParameter("timeBeforeNow", timeBeforeNow)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding active sessions that have exceeded the timeout", e);
            return new ArrayList<>();
        }
    }

    /**
     * Checks if the token is active and from the user id
     * @param token the token to be checked
     * @param userId the user id to be checked
     * @return boolean value indicating if the token is active and from the user id
     */
    public boolean isTokenActiveAndFromUserId(String token, int userId) {
        logger.info("Checking if token {} is active and from user id {}", token, userId);
        try {
            return (Long) em.createNamedQuery("SessionToken.isTokenActiveAndFromUserId")
                    .setParameter("token", token)
                    .setParameter("userId", userId)
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

    /**
     * Finds the user associated with a session token
     * @param token the token to be checked
     * @return the user associated with the session token
     */
    public int findUserIdByToken(String token) {
        logger.info("Finding user id by token {}", token);
        try {
            return (int) em.createNamedQuery("SessionToken.findUserIdByToken")
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error("No session token found for token {}", token, e);
            return -1;
        } catch (NonUniqueResultException e) {
            logger.error("Multiple session tokens found for token {}", token, e);
            return -1;
        } catch (Exception e) {
            logger.error("Error finding user id by token", e);
            return -1;
        }
    }
}

