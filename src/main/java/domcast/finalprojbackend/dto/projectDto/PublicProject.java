package domcast.finalprojbackend.dto.projectDto;

import domcast.finalprojbackend.dto.KeywordDto;
import domcast.finalprojbackend.dto.componentResourceDto.CRPreview;
import domcast.finalprojbackend.dto.skillDto.SkillToProject;
import domcast.finalprojbackend.dto.userDto.ProjectUser;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@XmlRootElement
public class PublicProject extends ProjectPreview implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private int state;

    @XmlElement
    private LocalDateTime projectedStartDate;

    @XmlElement
    private LocalDateTime deadline;

    @XmlElement
    private Set<KeywordDto> keywords;

    @XmlElement
    private Set<SkillToProject> skills;

    @XmlElement
    private Set<CRPreview> resources;

    @XmlElement
    private ProjectUser mainManager;

    /**
     * Empty constructor
     */
    public PublicProject() {
    }

    /**
     * Constructor with all the attributes
     * @param id the id of the project
     * @param name the name of the project
     * @param description the description of the project
     * @param labId the lab that the project belongs to
     * @param state the state of the project
     * @param projectedStartDate the projected start date of the project
     * @param deadline the deadline of the project
     * @param keywords the keywords of the project
     * @param skills the skills of the project
     * @param resources the resources of the project
     * @param mainManager the project's main manager
     * @param projectUsers the users that are part of the project's other executors
     */
    public PublicProject(int id, String name, String description, int labId, int state, LocalDateTime projectedStartDate, LocalDateTime deadline, Set<KeywordDto> keywords, Set<SkillToProject> skills, Set<CRPreview> resources, ProjectUser mainManager, Set<ProjectUser> projectUsers) {
        super(id, name, description, labId, projectUsers, state);
        this.projectedStartDate = projectedStartDate;
        this.deadline = deadline;
        this.keywords = keywords;
        this.skills = skills;
        this.resources = resources;
        this.mainManager = mainManager;
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

    public LocalDateTime getProjectedStartDate() {
        return projectedStartDate;
    }

    public void setProjectedStartDate(LocalDateTime projectedStartDate) {
        this.projectedStartDate = projectedStartDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Set<KeywordDto> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<KeywordDto> keywords) {
        this.keywords = keywords;
    }

    public Set<SkillToProject> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillToProject> skills) {
        this.skills = skills;
    }

    public Set<CRPreview> getResources() {
        return resources;
    }

    public void setResources(Set<CRPreview> resources) {
        this.resources = resources;
    }

    public ProjectUser getMainManager() {
        return mainManager;
    }

    public void setMainManager(ProjectUser mainManager) {
        this.mainManager = mainManager;
    }

}
