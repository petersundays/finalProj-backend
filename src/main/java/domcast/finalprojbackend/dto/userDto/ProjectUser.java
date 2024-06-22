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
public class ProjectUser implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private String firstName;

    @XmlElement
    private String lastName;

    @XmlElement
    private int role;

    /**
     * Empty constructor
     */
    public ProjectUser() {
    }

    // Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
