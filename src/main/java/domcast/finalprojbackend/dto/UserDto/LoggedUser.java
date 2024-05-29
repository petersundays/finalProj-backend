package domcast.finalprojbackend.dto.UserDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

/**
 * Represents a logged in user in the system.
 * Contains user's personal details, interests, skills, and session token.
 */
@XmlRootElement
public class LoggedUser {
    @XmlElement
    private int id;  // The unique identifier of the user

    @XmlElement
    private String email;  // The email address of the user

    @XmlElement
    private String sessionToken;  // The session token for the user, used for authentication

    @XmlElement
    private String firstName;  // The first name of the user

    @XmlElement
    private String lastName;  // The last name of the user

    @XmlElement
    private String nickname;  // The nickname of the user

    @XmlElement
    private String photo;  // The URL of the user's profile photo

    @XmlElement
    private String biography;  // A short biography of the user

    @XmlElement
    private String workplace;  // The user's current workplace

    @XmlElement
    private ArrayList<String> interests;  // A list of the user's interests

    @XmlElement
    private ArrayList<String> skills;  // A list of the user's skills

    /**
     * Default constructor for LoggedUser.
     */
    public LoggedUser() {
    }

    // Getters and setters for all fields

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }
}