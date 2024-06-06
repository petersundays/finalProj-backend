package domcast.finalprojbackend.bean.user;

import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class PasswordBean {

    private static final Logger logger = LogManager.getLogger(ValidatorAndHasher.class);


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

}
