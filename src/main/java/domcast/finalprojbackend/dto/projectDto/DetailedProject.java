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
public class DetailedProject extends PublicProject implements Serializable {

    @XmlElement
    private Set<ChartTask> tasks;

    /**
     * Empty constructor
     */
    public DetailedProject() {
    }

    /**
     * Constructor with all the attributes
     * @param id the id of the project
     * @param name the name of the project
     * @param description the description of the project
     * @param state the state of the project
     * @param projectedStartDate the projected start date of the project
     * @param deadline the deadline of the project
     * @param keywords the keywords of the project
     * @param skills the skills of the project
     * @param resources the resources of the project
     * @param mainManager the project's main manager
     * @param collaborators the collaborators of the project
     * @param tasks the tasks of the project
     */
    public DetailedProject(int id, String name, String description, int labId, int state, LocalDateTime projectedStartDate, LocalDateTime deadline, Set<KeywordDto> keywords, Set<SkillToProject> skills, Set<CRPreview> resources, ProjectUser mainManager, Set<ProjectUser> collaborators, Set<ChartTask> tasks) {
        super(id, name, description, labId, state, projectedStartDate,deadline, keywords, skills, resources, mainManager, collaborators);
        this.tasks = tasks;
    }

    // Getters and setters

    public Set<ChartTask> getTasks() {
        return tasks;
    }

    public void setTasks(Set<ChartTask> tasks) {
        this.tasks = tasks;
    }
}
