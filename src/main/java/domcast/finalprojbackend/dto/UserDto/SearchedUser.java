package domcast.finalprojbackend.dto.UserDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * SearchedUser is a Data Transfer Object (DTO) class used for searching users.
 * It contains all the necessary information for a user to be searched.
 * This includes the user's unique identifier, first name, last name, and workplace.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class SearchedUser implements Serializable {
    @XmlElement
    private int id;  // The unique identifier of the user

    @XmlElement
    private String firstName;  // The first name of the user

    @XmlElement
    private String lastName;  // The last name of the user

    @XmlElement
    private String workplace;  // The user's current workplace

    @XmlElement
    private String photo;  // The user's photo

    /**
     * Default constructor for SearchedUser.
     */
    public SearchedUser() {

    }

    // Getters and setters for all fields

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

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
