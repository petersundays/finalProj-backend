package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.InterestEnum;
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
 *  @author José Castro
 *  @author Pedro Domingos
 */

@Entity
@Table(name = "interest")

@NamedQuery(name = "Interest.findInterestByName", query = "SELECT i FROM InterestEntity i WHERE i.name = :name")
@NamedQuery(name = "Interest.findInterestsByListOfNames", query = "SELECT i FROM InterestEntity i WHERE i.name IN :names")

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
    private InterestEnum type;

    // Users that have the interest
    @OneToMany(mappedBy = "interest",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MUserInterest> userInterests = new HashSet<>();

    // Projects that have the interest
    @OneToMany(mappedBy = "interest",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MKeyword> projects = new HashSet<>();

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

    public InterestEnum getType() {
        return type;
    }

    public void setType(InterestEnum type) {
        this.type = type;
    }

    public Set<M2MUserInterest> getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(Set<M2MUserInterest> userInterests) {
        this.userInterests = userInterests;
    }

    public Set<M2MKeyword> getProjects() {
        return projects;
    }

    public void setProjects(Set<M2MKeyword> projects) {
        this.projects = projects;
    }
}
