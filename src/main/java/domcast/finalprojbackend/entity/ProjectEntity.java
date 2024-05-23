package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.ProjectStateEnum;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the project table in the database.
 * Contains all the attributes of the project table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the project.
 * - name: the name of the project.
 * - lab: the lab that the project belongs to.
 * - description: the description of the project.
 * - keywords: the keywords of the project.
 * - state: the state of the project.
 * - maxMembers: the maximum number of members of the project.
 * - skills: the skills of the project.
 * - creationDate: the creation date of the project.
 * - projectedStartDate: the projected start date of the project.
 * - realStartDate: the real start date of the project.
 * - deadline: the deadline of the project.
 * - realEndDate: the real end date of the project.
 * - groupMessages: the group messages of the project.
 * - componentResources: the component resources of the project.
 * - tasks: the tasks take part of the execution plan of the project.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@Entity
@Table(name = "project")

public class ProjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // Unique identifier for the project
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Name of the project
    @Column(name = "name", nullable = false)
    private String name;

    // Lab that the project belongs to
    @ManyToOne
    @JoinColumn(name = "lab", referencedColumnName = "id")
    private LabEntity lab;

    // Description of the project
    @Column(name = "description_motivation", nullable = false, columnDefinition = "TEXT")
    private String description;

    // Keywords of the project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MKeyword> keywords = new HashSet<>();

    // State of the project
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "state", nullable = false)
    private ProjectStateEnum state;

    // Maximum number of members of the project
    @Column(name = "max_members", nullable = false)
    private int maxMembers;

    // Users associated with the project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MProjectUser> projectUsers = new HashSet<>();

    // Skills required for the project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MProjectSkill> skills = new HashSet<>();

    // Creation date of the project
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    // Projected start date of the project
    @Column(name = "projected_start_date", nullable = false)
    private LocalDateTime projectedStartDate;

    // Real start date of the project
    @Column(name = "real_start_date")
    private LocalDateTime realStartDate;

    // Deadline of the project
    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    // Real end date of the project
    @Column(name = "real_end_date")
    private LocalDateTime realEndDate;

    // Group messages of the project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjectMessageEntity> groupMessages = new HashSet<>();

    // Components and resources used in the project
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MComponentProject> componentResources = new HashSet<>();

    // Tasks take part of the execution plan of the project
    @OneToMany(mappedBy = "project_id", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskEntity> tasks = new HashSet<>();

    // Default constructor
    public ProjectEntity() {
    }

    // Getters and setters

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

    public Set<M2MKeyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<M2MKeyword> keywords) {
        this.keywords = keywords;
    }

    public ProjectStateEnum getState() {
        return state;
    }

    public void setState(ProjectStateEnum state) {
        this.state = state;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Set<M2MProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Set<M2MProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public Set<M2MProjectSkill> getSkills() {
        return skills;
    }

    public void setSkills(Set<M2MProjectSkill> skills) {
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

    public Set<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(Set<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public Set<M2MComponentProject> getComponentResources() {
        return componentResources;
    }

    public void setComponentResources(Set<M2MComponentProject> componentResources) {
        this.componentResources = componentResources;
    }
}
