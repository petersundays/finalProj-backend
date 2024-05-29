package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.dao.SessionTokenDao;
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
import java.util.Base64;

@Stateless
public class TokenBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @EJB
    private ValidationTokenDao validationTokenDao;
    private SessionTokenDao sessionTokenDao;

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
    public ValidationTokenEntity generateValidationToken(UserEntity user, int expirationMinutes) {
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

    public boolean setTokenInactive(String token) {
        return validationTokenDao.setTokenInactive(token);
    }

    public boolean setSessionTokenInactive(String token) {
        return sessionTokenDao.setTokenInactive(token);
    }
}
