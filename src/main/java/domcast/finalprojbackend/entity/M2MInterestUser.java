package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the user_interest table in the database.
 * Creates a many-to-many relationship between the user and the interests.
 * Contains all the attributes of the user_interest table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the user-interest relationship.
 * - user: the user that has the interest.
 * - interest: the interest of the user.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name = "user_interest")

public class M2MInterestUser implements Serializable {

    private static final long serialVersionUID = 1L;

    // Unique identifier for the user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // User that has the interest
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    // Interest of the user
    @ManyToOne
    @JoinColumn(name = "interest_id", referencedColumnName = "id")
    private InterestEntity interest;

    // Status of the user-interest relationship
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Empty constructor
    public M2MInterestUser() {
    }

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

    public InterestEntity getInterest() {
        return interest;
    }

    public void setInterest(InterestEntity interest) {
        this.interest = interest;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
