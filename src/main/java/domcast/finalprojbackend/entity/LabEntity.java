package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the lab table in the database.
 * Contains all the attributes of the lab table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the lab.
 * - city: the city of the lab.
 * - users: the users that work in the lab.
 * The class also contains the necessary annotations to work with the database.
 */
@Entity
@Table(name = "lab")

public class LabEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the lab
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // City of the lab
    @Column(name = "city", nullable = false, unique = true)
    private String city;

    // Users that work in the lab
    // mappedBy = "workplace" is used to specify the inverse side of the relationship
    @OneToMany(mappedBy = "workplace")
    private Set<UserEntity> users = new HashSet<>();

    // Default constructor
    public LabEntity() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

}
