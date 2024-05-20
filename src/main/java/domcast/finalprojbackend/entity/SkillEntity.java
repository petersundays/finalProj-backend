package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the skill table in the database.
 * Contains all the attributes of the skill table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the skill.
 * - name: the name of the skill.
 * - type: the type of the skill.
 * - users: the users that have the skill.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name = "skill")

public class SkillEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the skill
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Name of the skill
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Type of the skill
    @Column(name = "type", nullable = false)
    private int type;

    // Users that have the skill
    @ManyToMany(mappedBy = "skills")
    private Set<UserEntity> users = new HashSet<>();

    // Default constructor
    public SkillEntity() {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }
}

