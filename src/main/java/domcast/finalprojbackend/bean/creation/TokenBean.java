package domcast.finalprojbackend.bean.creation;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.entity.ValidationTokenEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Stateless
public class TokenBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

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

}
