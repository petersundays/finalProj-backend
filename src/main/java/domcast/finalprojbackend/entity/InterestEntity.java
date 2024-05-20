package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the interest table in the database.
 * Contains all the attributes of the interest table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the interest.
 * - name: the name of the interest.
 * - type: the type of the interest.
 * - users: the users that have the interest.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name = "interest")

public class InterestEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the interest
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Name of the interest
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Type of the interest
    @Column(name = "type", nullable = false)
    private int type;

    // Users that have the interest
    @ManyToMany(mappedBy = "interests")
    private Set<UserEntity> users = new HashSet<>();

    // Default constructor
    public InterestEntity() {
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
