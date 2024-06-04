package domcast.finalprojbackend.dto.UserDto;

import domcast.finalprojbackend.dto.InterestDto;
import domcast.finalprojbackend.dto.SkillDto;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;

/**
 * UpdateUserDto is a Data Transfer Object (DTO) class used for updating user information.
 * It contains all the necessary information for a user to update their information.
 * This includes the user's unique identifier, first name, last name, nickname, photo, biography, workplace, interests, and skills.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class UpdateUserDto {
    @XmlElement
    private int id;  // The unique identifier of the user

    @XmlElement
    private String firstName;  // The first name of the user

    @XmlElement
    private String lastName;  // The last name of the user

    @XmlElement
    private String nickname;  // The nickname of the user

    @XmlElement
    private String biography;  // A short biography of the user

    @XmlElement
    private String workplace;  // The user's current workplace

    @XmlElement
    private ArrayList<String> interests;  // A list of the user's interests

    @XmlElement
    private ArrayList<String> skills;  // A list of the user's skills

    @XmlElement
    private ArrayList<InterestDto> interestDtos;

    @XmlElement
    private ArrayList<SkillDto> skillDtos;
    /**
     * Default constructor for UpdateUserDto.
     */
    public UpdateUserDto() {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public ArrayList<InterestDto> getInterestDtos() {
        return interestDtos;
    }

    public void setInterestDtos(ArrayList<InterestDto> interestDtos) {
        this.interestDtos = interestDtos;
    }

    public ArrayList<SkillDto> getSkillDtos() {
        return skillDtos;
    }

    public void setSkillDtos(ArrayList<SkillDto> skillDtos) {
        this.skillDtos = skillDtos;
    }
}