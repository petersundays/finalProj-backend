package domcast.finalprojbackend.bean.validationAndEncryption;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public class EmailAndPassword {

    private static final Logger logger = LogManager.getLogger(EmailAndPassword.class);
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Checks if the email is valid
     * @param email
     * @return
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
                isEmailValid(firstRegistration.getEmail());
    }

    /**
     * Hashes the password
     * @param password the password to be hashed
     * @return the hashed password as a string
     */
    public String hashPassword (String password) {
        logger.info("Hashing password");

        // Encrypts the password using BCrypt
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}