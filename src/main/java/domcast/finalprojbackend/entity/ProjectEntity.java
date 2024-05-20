package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project")

public class ProjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "lab", referencedColumnName = "id")
    private LabEntity lab;

    @Column(name = "description_motivation", nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToMany
    @JoinTable(
            name = "keywords",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<InterestEntity> keywords = new HashSet<>();

    @Column(name = "state", nullable = false)
    private int state;

    @Column(name = "max_members", nullable = false)
    private int maxMembers;

    @ManyToMany
    @JoinTable(
            name = "project_skills",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<SkillEntity> skills = new HashSet<>();

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

    @OneToMany(mappedBy = "project")
    private Set<ProjectMessageEntity> groupMessages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "project_component_resource",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "component_resource_id")
    )
    private Set<ComponentResourceEntity> componentResources = new HashSet<>();

    @OneToMany(mappedBy = "project_id")
    private Set<TaskEntity> tasks = new HashSet<>();


    // FALTAM RECURSOS E PLANO EXECUÇÃO //


    public ProjectEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LabEntity getLab() {
        return lab;
    }

    public void setLab(LabEntity lab) {
        this.lab = lab;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<InterestEntity> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<InterestEntity> keywords) {
        this.keywords = keywords;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Set<SkillEntity> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillEntity> skills) {
        this.skills = skills;
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

    public Set<ProjectMessageEntity> getGroupMessages() {
        return groupMessages;
    }

    public void setGroupMessages(Set<ProjectMessageEntity> groupMessages) {
        this.groupMessages = groupMessages;
    }

    public Set<ComponentResourceEntity> getComponentResources() {
        return componentResources;
    }

    public void setComponentResources(Set<ComponentResourceEntity> componentResources) {
        this.componentResources = componentResources;
    }

    public Set<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(Set<TaskEntity> tasks) {
        this.tasks = tasks;
    }
}
