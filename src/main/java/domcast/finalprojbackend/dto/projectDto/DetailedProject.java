package domcast.finalprojbackend.dto.projectDto;

import domcast.finalprojbackend.dto.KeywordDto;
import domcast.finalprojbackend.dto.componentResourceDto.CRPreview;
import domcast.finalprojbackend.dto.skillDto.SkillToProject;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.userDto.ProjectUser;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data transfer object for the project table in the database.
 * Contains all the attributes of the project table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the project.
 * - name: the name of the project.
 * - description: the description of the project.
 * - state: the state of the project.
 * - projectedStartDate: the projected start date of the project.
 * - deadline: the deadline of the project.
 * - keywords: the keywords of the project.
 * - skills: the skills of the project.
 * - resources: the resources of the project.
 * - responsible: the project's responsible.
 * - collaborators: the collaborators of the project.
 * - tasks: the tasks of the project.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class DetailedProject extends ProjectDto implements Serializable {

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

    @XmlElement
    private Set<ProjectUser> collaborators;

    @XmlElement
    private Set<ChartTask> tasks;

    /**
     * Empty constructor
     */
    public DetailedProject() {
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

    public Set<ProjectUser> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Set<ProjectUser> collaborators) {
        this.collaborators = collaborators;
    }

    public Set<ChartTask> getTasks() {
        return tasks;
    }

    public void setTasks(Set<ChartTask> tasks) {
        this.tasks = tasks;
    }
}
