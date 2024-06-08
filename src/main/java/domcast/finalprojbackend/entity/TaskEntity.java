package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.TaskStateEnum;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the task table in the database.
 * Contains all the attributes of the task table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the task.
 * - title: the title of the task.
 * - description: the description of the task.
 * - state: the state of the task.
 * - creationDate: the creation date of the task.
 * - projectedStartDate: the projected start date of the task.
 * - realStartDate: the real start date of the task.
 * - deadline: the deadline of the task.
 * - realEndDate: the real end date of the task.
 * - responsibleId: the responsible for the task.
 * - otherExecutors: the other executors of the task.
 * - project_id: the project of the task.
 * - dependencies: the dependencies of the task.
 * - records: the records of the task.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "task")

public class TaskEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the task
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Title of the task
    @Column(name = "title", nullable = false)
    private String title;

    // Description of the task
    @Column(name = "description", nullable = false)
    private String description;

    // State of the task
    @Column(name = "state", nullable = false)
    private TaskStateEnum state = TaskStateEnum.PLANNED;

    // Date in which the task was created
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    // Projected date in which the task should start
    @Column(name = "projected_start_date", nullable = false)
    private LocalDateTime projectedStartDate;

    // Date in which the task really started
    @Column(name = "real_start_date")
    private LocalDateTime realStartDate;

    // Date in which the task should end
    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    // Date in which the task really ended
    @Column(name = "real_end_date")
    private LocalDateTime realEndDate;

    // User responsible for the task
    @ManyToOne
    @JoinColumn(name = "responsible", nullable = false)
    private UserEntity responsible;

    // Other executors of the task
    @ElementCollection
    @CollectionTable(name = "task_executors", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "executor")
    private Set<String> otherExecutors = new HashSet<>();

    // Project to which the task belongs
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project_id;

    // Dependencies of the task
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MTaskDependencies> dependencies = new HashSet<>();

    // Records of the task
    @OneToMany(mappedBy = "task")
    private Set<RecordEntity> records = new HashSet<>();

    // Default constructor
    public TaskEntity() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public TaskStateEnum getState() {
        return state;
    }

    public void setState(TaskStateEnum state) {
        this.state = state;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getProjectedStartDate() {
        return projectedStartDate;
    }

    public void setProjectedStartDate(LocalDateTime projectedStartDate) {
        this.projectedStartDate = projectedStartDate;
    }

    public LocalDateTime getRealStartDate() {
        return realStartDate;
    }

    public void setRealStartDate(LocalDateTime realStartDate) {
        this.realStartDate = realStartDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(LocalDateTime realEndDate) {
        this.realEndDate = realEndDate;
    }

    public UserEntity getResponsible() {
        return responsible;
    }

    public void setResponsibleId(UserEntity responsible) {
        this.responsible = responsible;
    }

    public Set<String> getOtherExecutors() {
        return otherExecutors;
    }

    public void setOtherExecutors(Set<String> otherExecutors) {
        this.otherExecutors = otherExecutors;
    }

    public ProjectEntity getProject_id() {
        return project_id;
    }

    public void setProject_id(ProjectEntity project_id) {
        this.project_id = project_id;
    }

    public Set<M2MTaskDependencies> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<M2MTaskDependencies> dependencies) {
        this.dependencies = dependencies;
    }

    public Set<RecordEntity> getRecords() {
        return records;
    }

    public void setRecords(Set<RecordEntity> records) {
        this.records = records;
    }
}
