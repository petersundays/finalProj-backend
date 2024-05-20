package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task")

public class TaskEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "projected_start_date", nullable = false)
    private LocalDateTime projectedStartDate;

    @Column(name = "real_start_date")
    private LocalDateTime realStartDate;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "real_end_date")
    private LocalDateTime realEndDate;

    @Column(name = "responsible", nullable = false)
    private int responsibleId;

    @ElementCollection
    @CollectionTable(name = "task_other_executors", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "user_id")
    private Set<Integer> otherExecutors = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project_id;

    @ManyToMany
    @JoinTable(
            name = "dependencies",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dependent_task_id")
    )
    private Set<TaskEntity> dependencies = new HashSet<>();


    public TaskEntity() {
    }

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
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

    public int getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(int responsibleId) {
        this.responsibleId = responsibleId;
    }

    public Set<Integer> getOtherExecutors() {
        return otherExecutors;
    }

    public void setOtherExecutors(Set<Integer> otherExecutors) {
        this.otherExecutors = otherExecutors;
    }

    public ProjectEntity getProject_id() {
        return project_id;
    }

    public void setProject_id(ProjectEntity project_id) {
        this.project_id = project_id;
    }

    public Set<TaskEntity> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<TaskEntity> dependencies) {
        this.dependencies = dependencies;
    }
}
