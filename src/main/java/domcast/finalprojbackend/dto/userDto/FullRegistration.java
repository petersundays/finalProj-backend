package domcast.finalprojbackend.dto.userDto;

import domcast.finalprojbackend.dto.InterestDto;
import domcast.finalprojbackend.dto.SkillDto;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

/**
 * FullRegistration is a Data Transfer Object (DTO) class used for user registration.
 * It contains all the necessary information for a user to register.
 * This includes validation token, first name, last name, nickname, photo, biography, workplace, interests, and skills.
 */
@XmlRootElement
public class FullRegistration {
    @XmlElement
    private String validationToken;  // The validation token for the user

    @XmlElement
    private String firstName;  // The first name of the user

    @XmlElement
    private String lastName;  // The last name of the user

    @XmlElement
    private String nickname;  // The nickname of the user

    @XmlElement
    private String photo;  // The photo of the user

    @XmlElement
    private String biography;  // The biography of the user

    @XmlElement
    private String workplace;  // The workplace of the user

    @XmlElement
    private ArrayList<String> interests;  // The list of user's interests

    @XmlElement
    private ArrayList<String> skills;  // The list of user's skills

    @XmlElement
    private ArrayList<InterestDto> interestDtos;

    @XmlElement
    private ArrayList<SkillDto> skillDtos;

    /**
     * Default constructor for FullRegistration.
     */
    public FullRegistration() {
    }

    /**
     * Getter for the validation token of the user.
     *
     * @return the validation token of the user
     */
    public String getValidationToken() {
        return validationToken;
    }

    /**
     * Setter for the validation token of the user.
     *
     * @param validationToken the new validation token of the user
     */
    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

    /**
     * Getter for the first name of the user.
     *
     * @return the first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter for the first name of the user.
     *
     * @param firstName the new first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter for the last name of the user.
     *
     * @return the last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter for the last name of the user.
     *
     * @param lastName the new last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter for the nickname of the user.
     *
     * @return the nickname of the user
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Setter for the nickname of the user.
     *
     * @param nickname the new nickname of the user
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Getter for the photo of the user.
     *
     * @return the photo of the user
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Setter for the photo of the user.
     *
     * @param photo the new photo of the user
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Getter for the biography of the user.
     *
     * @return the biography of the user
     */
    public String getBiography() {
        return biography;
    }

    /**
     * Setter for the biography of the user.
     *
     * @param biography the new biography of the user
     */
    public void setBiography(String biography) {
        this.biography = biography;
    }

    /**
     * Getter for the workplace of the user.
     *
     * @return the workplace of the user
     */
    public String getWorkplace() {
        return workplace;
    }

    /**
     * Setter for the workplace of the user.
     *
     * @param workplace the new workplace of the user
     */
    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    /**
     * Getter for the list of interests of the user.
     *
     * @return the list of interests of the user
     */
    public ArrayList<String> getInterests() {
        return interests;
    }

    /**
     * Setter for the list of interests of the user.
     *
     * @param interests the new list of interests of the user
     */
    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    /**
     * Getter for the list of skills of the user.
     *
     * @return the list of skills of the user
     */
    public ArrayList<String> getSkills() {
        return skills;
    }

    /**
     * Setter for the list of skills of the user.
     *
     * @param skills the new list of skills of the user
     */
    public void setSkills(ArrayList<String> skills) {
        this.skills = skills;
    }

    /**
     * Getter for the list of interest DTOs of the user.
     *
     * @return the list of interest DTOs of the user
     */
    public ArrayList<InterestDto> getInterestDtos() {
        return interestDtos;
    }

    /**
     * Setter for the list of interest DTOs of the user.
     *
     * @param interestDtos the new list of interest DTOs of the user
     */
    public void setInterestDtos(ArrayList<InterestDto> interestDtos) {
        this.interestDtos = interestDtos;
    }

    /**
     * Getter for the list of skill DTOs of the user.
     *
     * @return the list of skill DTOs of the user
     */
    public ArrayList<SkillDto> getSkillDtos() {
        return skillDtos;
    }

    /**
     * Setter for the list of skill DTOs of the user.
     *
     * @param skillDtos the new list of skill DTOs of the user
     */
    public void setSkillDtos(ArrayList<SkillDto> skillDtos) {
        this.skillDtos = skillDtos;
    }
}