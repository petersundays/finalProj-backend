package domcast.finalprojbackend.bean.user;

import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.dto.UserDto.FullRegistration;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

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
        return pattern.matcher(email).matches();
    }

    /**
     * Checks if the input is valid
     * @param firstRegistration the input to be checked
     * @return boolean value indicating if the input is valid
     */
    public boolean isInputValid(FirstRegistration firstRegistration) {
        logger.info("Checking if input is valid");

        return firstRegistration.getEmail() != null && !firstRegistration.getEmail().isBlank() &&
                firstRegistration.getPassword() != null && !firstRegistration.getPassword().isBlank() &&
                isEmailValid(firstRegistration.getEmail()) && isPasswordValid(firstRegistration.getPassword());
    }

    public boolean isMandatoryDataValid(FullRegistration fullRegistration) {

        logger.info("Checking if mandatory data is valid");

        System.out.println("************* " + fullRegistration.getFirstName() + " " + fullRegistration.getLastName() + " " + fullRegistration.getWorkplace() + " *************");

        return fullRegistration.getFirstName() != null && !fullRegistration.getFirstName().isBlank() &&
                fullRegistration.getLastName() != null && !fullRegistration.getLastName().isBlank() &&
                fullRegistration.getWorkplace() != null && !fullRegistration.getWorkplace().isBlank();
    }

    /**
     * Checks if the password is valid
     * @param password the password to be checked
     * @return boolean value indicating if the password is valid
     */
    public boolean isPasswordValid(String password) {
        logger.info("Checking if password is valid");

        // Regex for a strong password
        // Minimum twelve characters, at least one uppercase letter, one lowercase letter, one number and one special character
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return password.length() >= 12 && matcher.matches();
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