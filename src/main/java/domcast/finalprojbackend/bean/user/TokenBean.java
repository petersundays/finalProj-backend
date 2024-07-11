package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.dao.SessionTokenDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dao.ValidationTokenDao;
import domcast.finalprojbackend.entity.SessionTokenEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.entity.ValidationTokenEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * Bean for token operations
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class TokenBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @EJB
    private ValidationTokenDao validationTokenDao;
    @EJB
    private SessionTokenDao sessionTokenDao;
    @EJB
    UserDao userDao;
    @EJB
    UserBean userBean;

    // Default constructor
    public TokenBean() {
    }

    /**
     * Generates a validation token for the user
     * Validation token can be used both for account validation and password reset
     * @param user the user for which the token is generated
     * @param expirationMinutes the number of minutes the token is valid for
     * @return the generated validation token
     */
    public ValidationTokenEntity generateValidationToken(UserEntity user, int expirationMinutes, String ip_address) {
        logger.info("Generating validation token");

        ValidationTokenEntity validationTokenEntity = new ValidationTokenEntity();
        try {
            SecureRandom secureRandom = new SecureRandom(); //threadsafe instance of SecureRandom class for generating random numbers
            Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe instance of Base64.Encoder class for encoding byte data
            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            String token = base64Encoder.encodeToString(randomBytes);

            validationTokenEntity.setToken(token);
            validationTokenEntity.setUser(user);
            validationTokenEntity.setExpirationTime(validationTokenEntity.getCreationTime().plusMinutes(expirationMinutes));
            validationTokenEntity.setIpAddress(ip_address);

            logger.info("Validation token generated");
        } catch (NullPointerException e) {
            logger.error("An error occurred while generating the validation token", e);
        }

        return validationTokenEntity;
    }

    /**
     * Generates a session token for the user
     * @param user the user for which the token is generated
     * @param ipAddress the IP address from which the session was created
     * @return the generated session token
     */
    public SessionTokenEntity generateSessionToken(UserEntity user, String ipAddress) {
        logger.info("Generating session token");

        SessionTokenEntity sessionTokenEntity = new SessionTokenEntity();
        try {
            // Generate a random token
            SecureRandom secureRandom = new SecureRandom(); //threadsafe instance of SecureRandom class for generating random numbers
            Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe instance of Base64.Encoder class for encoding byte data
            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            String token = base64Encoder.encodeToString(randomBytes);

            // Set the session token entity properties
            sessionTokenEntity.setToken(token);
            sessionTokenEntity.setUser(user);
            sessionTokenEntity.setIpAddress(ipAddress);

            logger.info("Session token generated");
        } catch (NullPointerException e) {
            logger.error("An error occurred while generating the session token", e);
        }

        return sessionTokenEntity;
    }

    /**
     * Sets the validation token as inactive
     * @param token the token to be set as inactive
     * @return boolean value indicating if the token was set as inactive
     */
    public boolean setTokenInactive(String token) {
        return validationTokenDao.setTokenInactive(token);
    }

    /**
     * Checks if the session token is from an admin type user
     * @param token the session token to be checked
     * @return boolean value indicating if the session token is from an admin type user
     */
    public boolean isSessionTokenFromAdminTypeUser(String token) {
        return sessionTokenDao.isSessionTokenFromAdminTypeUser(token);
    }

    /**
     * Sets the session token logout time to the current time
     * @param token the session token to be updated
     * @return boolean value indicating if the operation was successful
     */
    public boolean setSessionTokenLogoutToNow(String token) {
        return sessionTokenDao.setSessionTokenLogoutToNow(token);
    }

    /**
     * Finds active sessions that have exceeded the timeout
     * @param timeout the session timeout
     * @return the list of session tokens that have exceeded the timeout
     */
    public List<SessionTokenEntity> findActiveSessionsExceededTimeout(int timeout) {
        return sessionTokenDao.findActiveSessionsExceededTimeout(timeout, LocalDateTime.now());
    }

    /**
     * Checks if the validation token is expired
     * @param token the validation token to be checked
     * @return boolean value indicating if the validation token is expired
     */
    public boolean isValidationTokenExpired(String token) {
        boolean expired = validationTokenDao.isTokenExpired(token);

        if (expired) {
            logger.info("Validation token is expired: {}", token);
            try {
                setTokenInactive(token);
                logger.info("Validation token set as inactive: {}", token);
            } catch (Exception e) {
                logger.error("Error setting validation token as inactive", e);
            }
        }

        return expired;
    }

    /**
     * Finds the user by the session token
     * @param token the session token
     * @return the user found by the session token
     */
    public UserEntity findUserByToken(String token) {

        logger.info("Finding user by token");

        try {
            return sessionTokenDao.findUserByToken(token);
        } catch (Exception e) {
            logger.error("Error finding user by token", e);
            return null;
        }
    }

    /**
     * Sets the last access of the session token to the current time
     * @param token the session token to be updated
     */
    public void setLastAccessToNow(String token) {

        if (token == null || token.isEmpty()) {
            logger.error("Token is null or empty. Cannot set last access to now");
        }

        try {
            logger.info("Last access updated to now for token: {}", token);
        } catch (Exception e) {
            logger.error("Error setting last access to now", e);
        }
    }
}
