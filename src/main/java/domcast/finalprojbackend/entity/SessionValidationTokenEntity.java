package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity class for the sessionToken table in the database.
 * This class extends the TokenEntity class.
 * Contains all the attributes of the sessionToken table and their getters and setters.
 * It ignores the user attribute, because the cardinality is one-to-many, and it is not necessary to have it in the sessionToken table.
 * The attributes are the following:
 * - lastAccess: the last access of the session token.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name = "sessionToken")
public class SessionValidationTokenEntity extends ValidationTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Login timestamp
    @Column(name = "login_time")
    private LocalDateTime loginTime;

    // Logout timestamp
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    // This field will not be persisted
    @Transient
    private UserEntity user;

    // User associated with the session token
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false) // This field is not insertable or updatable because it is a transient field, but it is still accessible for queries
    private UserEntity sessionUser;

    // IP from which the session was created
    @Column(name = "ip_address")
    private String ipAddress;

    // Last access of the user with this session token
    @Column(name = "lastAccess")
    private LocalDateTime lastAccess;

    // Active state of the session token
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Default constructor
    public SessionValidationTokenEntity() {
    }

    // Getters and setters

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public UserEntity getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(UserEntity sessionUser) {
        this.sessionUser = sessionUser;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}