package domcast.finalprojbackend.dto.taskDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO class for creating a new task.
 * Contains all the attributes of the task table and their getters and setters.
 * The attributes are the following:
 * - title: the title of the task.
 * - description: the description of the task.
 * - projectedStartDate: the projected start date of the task.
 * - deadline: the deadline of the task.
 * - responsibleId: the responsible for the task.
 * - otherExecutors: the other users that are executors of the task.
 * - projectId: the project of the task.
 * - Dependencies: the id's of other tasks that this task depends on.
 * The class also contains the necessary annotations to work with the database.
 *
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class NewTask<T> implements Serializable {
    @XmlElement
    private String title;

    @XmlElement
    private String description;

    @XmlElement
    private LocalDateTime projectedStartDate;

    @XmlElement
    private LocalDateTime deadline;

    @XmlElement
    private int responsibleId;

    @XmlElement
    private int projectId;

    @XmlElement
    private Set<String> otherExecutors;

    @XmlElement
    private Set<T> dependencies;

    @XmlElement
    private Set<T> dependentTasks;


    // Default constructor
    public NewTask() {
    }

    // Constructor with all parameters
    public NewTask(String title, String description, LocalDateTime projectedStartDate, LocalDateTime deadline, int responsibleId, int projectId, Set<String> otherExecutors, Set<T> dependencies, Set<T> dependentTasks) {
        this.title = title;
        this.description = description;
        this.projectedStartDate = projectedStartDate;
        this.deadline = deadline;
        this.responsibleId = responsibleId;
        this.projectId = projectId;
        this.otherExecutors = otherExecutors;
        this.dependencies = dependencies;
        this.dependentTasks = dependentTasks;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(int responsibleId) {
        this.responsibleId = responsibleId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Set<String> getOtherExecutors() {
        return otherExecutors;
    }

    public void setOtherExecutors(Set<String> otherExecutors) {
        this.otherExecutors = otherExecutors;
    }

    public Set<T> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<T> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<T> getDependentTasks() {
        return dependentTasks;
    }

    public void setDependentTasks(Set<T> dependentTasks) {
        this.dependentTasks = dependentTasks;
    }
}
