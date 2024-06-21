package domcast.finalprojbackend.dto.userDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Map;

/**
 * Data transfer object for the project team.
 * Contains all the attributes of the project's other executors, their getters and setters.
 * The attributes are the following:
 * - projectUsers: the users that are part of the project's team.
 * The key is the user's id and the value is the user's role in the project.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class ProjectTeam implements Serializable {

    @XmlElement
    private Map<Integer, Integer> projectUsers;

    /**
     * Empty constructor
     */
    public ProjectTeam() {
    }

    /**
     * Constructor with all the attributes
     * @param projectUsers the users that are part of the project
     */
    public ProjectTeam(Map<Integer, Integer> projectUsers) {
        this.projectUsers = projectUsers;
    }

    // Getters and setters


    public Map<Integer, Integer> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Map<Integer, Integer> projectUsers) {
        this.projectUsers = projectUsers;
    }
}
