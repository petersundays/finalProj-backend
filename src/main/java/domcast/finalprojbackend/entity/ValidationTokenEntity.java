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


@Entity
@Table(name = "tokens")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "token_type")

@NamedQuery(name = "Token.setTokenInactive", query = "UPDATE ValidationTokenEntity t SET t.active = false WHERE t.token = :token")
@NamedQuery(name = "Token.isTokenValid", query = "SELECT COUNT(t) FROM ValidationTokenEntity t WHERE t.token = :token AND t.expirationTime > CURRENT_TIMESTAMP")
@NamedQuery(name = "Token.isTokenActiveAndUserNotConfirmed", query = "SELECT COUNT(t) FROM ValidationTokenEntity t WHERE t.token = :token AND t.active = true AND t.user.type = 100")

public class ValidationTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the token
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    protected int id;

    // IP from which the session was created
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    // The generated token for the user
    @Column(name = "token", nullable = false, unique = true, updatable = false)
    protected String token;

    // The user associated with the token
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Represents the creation time of the token if it's a validation token or the login time if it's a session token
    @Column(name = "creationTime", nullable = false, updatable = false)
    private LocalDateTime creationTime = LocalDateTime.now();

    @Column(name = "expirationTime")
    private LocalDateTime expirationTime;

    // The status of the token
    @Column(name = "active")
    protected boolean active = true;

    // Default constructor
    public ValidationTokenEntity() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
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