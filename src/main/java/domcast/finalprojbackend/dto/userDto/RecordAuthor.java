package domcast.finalprojbackend.dto.userDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Data Transfer Object (DTO) class to send author information to the record in the frontend.
 * It includes the first name and last name of the author.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class RecordAuthor {

    @XmlElement
    private String firstName;

    @XmlElement
    private String lastName;

    /**
     * Empty constructor
     */
    public RecordAuthor() {
    }

    /**
     * Constructor with the first name and last name of the author.
     * @param firstName First name of the author.
     * @param lastName Last name of the author.
     */
    public RecordAuthor(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
