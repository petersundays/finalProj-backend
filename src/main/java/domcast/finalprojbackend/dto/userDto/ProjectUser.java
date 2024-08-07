package domcast.finalprojbackend.dto.userDto;

import domcast.finalprojbackend.enums.ProjectUserEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class to send user information to the project in the frontend.
 * It includes the first name, last name, role and the id of the user.
 *
 * @see ProjectUserEnum
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class ProjectUser extends InvitedOrCandidate implements Serializable {

    @XmlElement
    private int role;

    /**
     * Empty constructor
     */
    public ProjectUser() {
    }

    /**
     * Constructor with the id, first name, last name and role of the user.
     * @param id ID of the user.
     * @param firstName First name of the user.
     * @param lastName Last name of the user.
     * @param role Role of the user.
     */
    public ProjectUser(int id, String firstName, String lastName, int role) {
        super(id, firstName, lastName);
        this.role = role;
    }

    // Getters and setters

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
