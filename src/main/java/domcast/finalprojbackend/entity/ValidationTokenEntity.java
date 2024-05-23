package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract class for the token table in the database.
 * This class is extended by the SessionTokenEntity and the ValidationTokenEntity classes.
 * Contains all the attributes of the token table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the token.
 * - token: the token.
 * - email: the email of the user.
 * - active: the status of the token.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@MappedSuperclass
public class ValidationTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the token
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    protected int id;

    // The generated token for the user
    @Column(name = "token")
    protected String token;

    // The user associated with the token
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Creation time of the validation token
    @Column(name = "creationTime")
    private LocalDateTime creationTime;

    // The status of the token
    @Column(name = "active")
    protected boolean active;

    // Default constructor
    public ValidationTokenEntity() {
        // active is true by default
        this.active = true;
    }

    // Getters and setters

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        // If the current value of active is false, do not change it
        if (!this.active) {
            return;
        }

        this.active = active;
    }
}