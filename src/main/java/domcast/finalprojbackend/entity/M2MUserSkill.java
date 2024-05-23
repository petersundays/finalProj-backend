package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the user_skill table in the database.
 * Creates a join table for the many-to-many relationship between UserEntity and SkillEntity.
 * Contains all the attributes of the user_skill table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the user-skill relationship.
 * - user: the user of the relationship.
 * - skill: the skill of the relationship.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "user_skill")
public class M2MUserSkill implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the user-skill relationship
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Many-to-one relationship to UserEntity
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Many-to-one relationship to SkillEntity
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    // Active state of the user-skill relationship
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
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