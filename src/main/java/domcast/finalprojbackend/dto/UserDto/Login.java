package domcast.finalprojbackend.dto.UserDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Login is a DTO (Data Transfer Object) class that represents the login credentials of a user.
 * It contains the user's email and password.
 * The class is annotated with @XmlRootElement to indicate that it can be the root element in an XML document.
 * The fields are annotated with @XmlElement to indicate that they will be represented as XML elements in an XML document.
 *
 * @author petersundays
 */
@XmlRootElement
public class Login {
    // The email of the user
    @XmlElement
    private String email;

    // The password of the user
    @XmlElement
    private String password;

    /**
     * Default constructor for Login.
     */
    public Login() {}

    /**
     * Returns the email of the user.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}