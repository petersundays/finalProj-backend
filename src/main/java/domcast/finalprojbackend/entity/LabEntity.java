package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.LabEnum;
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
 * - Users: the users that work in the lab.
 * The class also contains the necessary annotations to work with the database.
 *  @author José Castro
 *  @author Pedro Domingos
 */
@Entity
@Table(name = "lab")

@NamedQuery(name = "Lab.findLabByCity", query = "SELECT l FROM LabEntity l WHERE l.city = :city")

public class LabEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the lab
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // City of the lab
    @Column(name = "city", nullable = false, unique = true)
    private LabEnum city;

    // Users that work in the lab
    // mappedBy = "workplace" is used to specify the inverse side of the relationship
    @OneToMany(mappedBy = "workplace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserEntity> users = new HashSet<>();

    // Projects that use lab
    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjectEntity> projects = new HashSet<>();

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

    public LabEnum getCity() {
        return city;
    }

    public void setCity(LabEnum city) {
        this.city = city;
    }

    public Set<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(Set<UserEntity> users) {
        this.users = users;
    }

    public Set<ProjectEntity> getProjects() {
        return projects;
    }

    public void setProjects(Set<ProjectEntity> projects) {
        this.projects = projects;
    }
}
