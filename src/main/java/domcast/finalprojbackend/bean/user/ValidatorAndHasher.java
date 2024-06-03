package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.dto.UserDto.FullRegistration;
import domcast.finalprojbackend.dto.UserDto.Login;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Stateless
public class ValidatorAndHasher {

    private static final Logger logger = LogManager.getLogger(ValidatorAndHasher.class);
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Checks if the email is valid
     * @param email the email to be checked
     * @return boolean value indicating if the email is valid
     */
    public boolean isEmailValid(String email) {
        logger.info("Checking if email is valid");

        if (email == null) {
            logger.error("Email cannot be null");
            throw new IllegalArgumentException("Email cannot be null");
        }

        if (email.isBlank()) {
            logger.error("Email cannot be blank");
            throw new IllegalArgumentException("Email cannot be blank");
        }

        return pattern.matcher(email).matches();
    }

    /**
     * Checks if the input is valid
     * @param firstRegistration the input to be checked
     * @return boolean value indicating if the input is valid
     */
    public boolean isInputValid(FirstRegistration firstRegistration) {
        logger.info("Checking if input is valid");

        if (firstRegistration == null) {
            logger.error("FirstRegistration object cannot be null");
            throw new IllegalArgumentException("FirstRegistration object cannot be null");
        }

        return isEmailValid(firstRegistration.getEmail()) && isPasswordValid(firstRegistration.getPassword());
    }

    public boolean isMandatoryDataValid(FullRegistration fullRegistration) {

        logger.info("Checking if mandatory data is valid");

        return fullRegistration.getFirstName() != null && !fullRegistration.getFirstName().isBlank() &&
                fullRegistration.getLastName() != null && !fullRegistration.getLastName().isBlank() &&
                fullRegistration.getWorkplace() != null && !fullRegistration.getWorkplace().isBlank();
    }

    /**
     * Checks if the login is valid
     * @param login the login to be checked
     * @return boolean value indicating if the login is valid
     */
    public boolean isLoginValid (Login login) {
        logger.info("Checking if login is valid");

        return login != null && login.getEmail() != null && !login.getEmail().isBlank() &&
                login.getPassword() != null && !login.getPassword().isBlank();
    }

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
     * Checks if the image is valid
     * @param bytes the image to be checked
     * @return boolean value indicating if the image is valid
     */
    public boolean isValidImage(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes)) != null;
        } catch (IOException e) {
            logger.error("Error while checking image: {}", e.getMessage());
            return false;
        }
    }
}