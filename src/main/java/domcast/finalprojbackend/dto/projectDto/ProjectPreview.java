package domcast.finalprojbackend.dto.projectDto;

import domcast.finalprojbackend.dto.userDto.ProjectUser;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Set;

@XmlRootElement
public class ProjectPreview extends ProjectDto implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private int state;

    @XmlElement
    private Set<ProjectUser> projectUsers;

    @XmlElement
    private int vacancies;


    /**
     * Empty constructor
     */
    public ProjectPreview() {
    }

    /**
     * Constructor with all the attributes
     * @param id the id of the project
     * @param name the name of the project
     * @param description the description of the project
     * @param labId the lab that the project belongs to
     * @param projectUsers the users that are part of the project's other executors
     * @param state the state of the project
     * @param vacancies the number of vacancies in the project
     */
    public ProjectPreview(int id, String name, String description, int labId, Set<ProjectUser> projectUsers, int state, int vacancies) {
        super(name, description, labId);
        this.id = id;
        this.state = state;
        this.projectUsers = projectUsers;
        this.vacancies = vacancies;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Set<ProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Set<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public int getVacancies() {
        return vacancies;
    }

    public void setVacancies(int vacancies) {
        this.vacancies = vacancies;
    }
}
