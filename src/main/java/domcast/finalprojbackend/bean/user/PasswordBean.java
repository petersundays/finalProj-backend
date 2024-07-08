package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.project.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dao.UserDao;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PasswordBean is a Stateless Enterprise Java Bean (EJB) class for password operations.
 * It provides methods to hash and validate passwords, as well as update the password for a user.
 *
 * @author José Castro
 * @author Pedro Domingos
 *
 */
@Stateless
public class PasswordBean {

    private static final Logger logger = LogManager.getLogger(DataValidator.class);

    @EJB
    private UserDao userDao;
    @EJB
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    /**
     * Checks if the password is valid, according to the following rules:
     * - At least 12 characters long
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     * - At least one special character
     * @throws IllegalArgumentException if the password is not valid, null or blank
     * @param password the password to be checked
     * @return boolean value indicating if the password is valid
     */
    public boolean isPasswordValid(String password) throws IllegalArgumentException {
        logger.info("Checking if password is valid");
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        if (password.length() < 12) {
            throw new IllegalArgumentException("Password must be at least 12 characters long");
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        return true;
    }

    /**
     * Hashes the password
     * @param password the password to be hashed
     * @return the hashed password as a string
     */
    public String hashPassword (String password) {
        logger.info("Hashing password");
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
            logger.error("Error while hashing password: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Updates the password for a user
     * @param id the id of the user
     * @param oldPassword the old password
     * @param newPassword the new password
     * @return boolean value indicating if the password was updated successfully
     */
    public String updatePassword(int id, String oldPassword, String newPassword) {
        logger.info("Updating password for user id: {}", id);
        try {
            String hashedOldPassword = userDao.getUserPassword(id);
            if (authenticationAndAuthorization.checkPassword(oldPassword, hashedOldPassword)) {
                try {
                    if (isPasswordValid(newPassword)) {
                        String hashedNewPassword = hashPassword(newPassword);
                        userDao.setUserPassword(id, hashedNewPassword);
                        return "Password updated successfully";
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("New password is not valid for user id: {}. Error message: {}", id, e.getMessage());
                    return e.getMessage();
                }
            } else {
                logger.error("Old password is not correct for user id: {}", id);
                return "Old password is not correct";
            }
        } catch (Exception e) {
            logger.error("Error while updating password for user id: {}. Error message: {}", id, e.getMessage());
            return "Error while updating password: " + e.getMessage();
        }
        return "Error updating password";
    }
}
