package domcast.finalprojbackend.dto.userDto;

import domcast.finalprojbackend.enums.ProjectUserEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class to send user information to the frontend.
 * It includes the first name, last name and the id of the user.
 *
 * @see ProjectUserEnum
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class InvitedOrCandidate extends RecordAuthor implements Serializable {

    @XmlElement
    private int id;

    /**
     * Empty constructor
     */
    public InvitedOrCandidate() {
    }

    /**
     * Constructor with the id, first name, last name and role of the user.
     * @param id ID of the user.
     * @param firstName First name of the user.
     * @param lastName Last name of the user.
     */
    public InvitedOrCandidate(int id, String firstName, String lastName) {
        super(firstName, lastName);
        this.id = id;
    }

    // Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
