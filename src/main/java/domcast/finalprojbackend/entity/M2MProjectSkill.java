package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the project_skill table in the database.
 * Creates a join table for the many-to-many relationship between ProjectEntity and SkillEntity.
 * Contains all the attributes of the project_skill table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the project-skill relationship.
 * - project: the project of the relationship.
 * - skill: the skill of the relationship.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "project_skill")

@NamedQuery(name = "M2MProjectSkill.findAllActiveForProject", query = "SELECT ps FROM M2MProjectSkill ps WHERE ps.project.id = :projectId AND ps.active = true")
@NamedQuery(name = "M2MProjectSkill.findAllInactiveForProject", query = "SELECT ps FROM M2MProjectSkill ps WHERE ps.project.id = :projectId AND ps.active = false")
@NamedQuery(name = "M2MProjectSkill.findAllforProject", query = "SELECT ps FROM M2MProjectSkill ps WHERE ps.project.id = :projectId")
@NamedQuery(name = "M2MProjectSkill.setSkillInactiveForProject", query = "UPDATE M2MProjectSkill ps SET ps.active = false WHERE ps.project.id = :projectId AND ps.skill.id = :skillId")
@NamedQuery(name = "M2MProjectSkill.setSkillActiveForProject", query = "UPDATE M2MProjectSkill ps SET ps.active = true WHERE ps.project.id = :projectId AND ps.skill.id = :skillId")

public class M2MProjectSkill implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the project-skill relationship
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Many-to-one relationship to ProjectEntity
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;

    // Many-to-one relationship to SkillEntity
    @ManyToOne
    @JoinColumn(name = "skill_id", referencedColumnName = "id")
    private SkillEntity skill;

    // Active state of the project-skill relationship
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Empty constructor
    public M2MProjectSkill() {
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

    public SkillEntity getSkill() {
        return skill;
    }

    public void setSkill(SkillEntity skill) {
        this.skill = skill;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
