package domcast.finalprojbackend.dto.userDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Set;

@XmlRootElement
public class PublicProfileUser extends SearchedUser {

    @XmlElement
    private String biography; // The user's biography

    @XmlElement
    private Set<String> interests; // The user's interests

    @XmlElement
    private Set<String> skills; // The user's skills

//    @XmlElement
//    private Set<ProjectDto> projects; // The user's projects

    /**
     * Default constructor for PublicProfileUser.
     */
    public PublicProfileUser() {
        super();
    }

    // Getters and setters for all fields

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Set<String> getInterests() {
        return interests;
    }

    public void setInterests(Set<String> interests) {
        this.interests = interests;
    }

    public Set<String> getSkills() {
        return skills;
    }

    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }

}
