package domcast.finalprojbackend.entity;


import domcast.finalprojbackend.enums.ProjectUserEnum;
import domcast.finalprojbackend.enums.converters.ProjectUserEnumConverter;
import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the project_user table in the database.
 * The project_user table is a join table between the project and user tables.
 * Contains all the attributes of the project_user table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the project user.
 * - project: the project of the project user.
 * - user: the user of the project user.
 * - role: the role of the project user.
 * - approved: the approval status of the project user.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "project_user")

public class M2MProjectUser implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the project user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Project with which the user is associated
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;

    // User with which the project is associated
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    // Role of the user in the project
    @Convert(converter = ProjectUserEnumConverter.class)
    @Column(name = "role", nullable = false)
    private ProjectUserEnum role;

    // Approval status of the user in the project
    @Column(name = "approved", nullable = false)
    private int approved;

    // Defines wether the user is a member of the project or not
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Default constructor
    public M2MProjectUser() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ProjectUserEnum getRole() {
        return role;
    }

    public void setRole(ProjectUserEnum role) {
        this.role = role;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
