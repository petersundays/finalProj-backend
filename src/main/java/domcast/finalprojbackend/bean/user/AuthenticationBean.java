package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.dao.SessionTokenDao;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

@Stateless
public class AuthenticationBean {

    private static final Logger logger = LogManager.getLogger(ValidatorAndHasher.class);
    @EJB
    private SessionTokenDao sessionTokenDao;

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
}
