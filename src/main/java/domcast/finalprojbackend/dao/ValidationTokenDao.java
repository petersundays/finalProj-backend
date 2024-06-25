package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.ValidationTokenEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for handling operations related to validation tokens.
 * It extends the AbstractDao class to inherit common database operations.
 * It is annotated as a Stateless EJB, meaning it does not maintain any state for a specific client.
 */
@Stateless
public class ValidationTokenDao extends AbstractDao<ValidationTokenEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(ValidationTokenDao.class);

    /**
     * Default constructor.
     * Calls the parent constructor with the ValidationTokenEntity class as the parameter.
     */
    public ValidationTokenDao() {
        super(ValidationTokenEntity.class);
    }

    /**
     * Sets a validation token as inactive.
     * @param token The token to be set as inactive.
     * @return A boolean indicating whether the operation was successful.
     */
    public boolean setTokenInactive(String token) {
        logger.info("Setting token {} as inactive", token);
        try {
            int updatedEntities = em.createNamedQuery("Token.setTokenInactive")
                    .setParameter("token", token)
                    .executeUpdate();
            return updatedEntities > 0;
        } catch (Exception e) {
            logger.error("Error setting token {} as inactive", token, e);
            return false;
        }
    }

    /**
     * Checks if a validation token is expired.
     * @param token The token to be checked.
     * @return A boolean indicating whether the token is expired.
     */
    public boolean isTokenExpired(String token) {
        logger.info("Checking if token {} is expired", token);
        try {
            long count = em.createNamedQuery("Token.isTokenValid", Long.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return count == 0;
        } catch (NoResultException e) {
            logger.error("No token found with the provided token: {}", token, e);
            return true;
        } catch (NonUniqueResultException e) {
            logger.error("Multiple tokens found with the provided token: {}", token, e);
            return true;
        }
    }

    /**
     * Checks if a validation token is active and the user is not confirmed.
     * @param token The token to be checked.
     * @return A boolean indicating whether the token is active and the user is not confirmed.
     */
    public boolean isTokenActiveAndUserNotConfirmed(String token) {
        logger.info("Checking if token {} is active and the user is not confirmed", token);
        try {
            long count = em.createNamedQuery("Token.isTokenActiveAndUserNotConfirmed", Long.class)
                    .setParameter("token", token)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            logger.error("No token found with the provided token: {}", token, e);
            return false;
        } catch (NonUniqueResultException e) {
            logger.error("Multiple tokens found with the provided token: {}", token, e);
            return false;
        }
    }
}