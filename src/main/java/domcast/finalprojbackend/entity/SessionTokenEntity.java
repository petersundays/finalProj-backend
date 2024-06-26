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
 *  * @author José Castro
 *  * @author Pedro Domingos
 */

@Entity

@NamedQuery(name = "SessionToken.isSessionTokenFromAdminTypeUser", query = "SELECT COUNT(s) FROM SessionTokenEntity s WHERE s.user.type = 300 AND s.token = :token")
@NamedQuery(name = "SessionToken.setSessionTokenLogoutToNow", query = "UPDATE SessionTokenEntity s SET s.logoutTime = CURRENT_TIMESTAMP WHERE s.token = :token")
@NamedQuery(name = "SessionToken.findActiveSessionsExceededTimeout",
        query = "SELECT s FROM SessionTokenEntity s WHERE s.active = true AND s.lastAccess < :timeBeforeNow")
@NamedQuery(name = "SessionToken.isTokenActiveAndFromUserId",
        query = "SELECT COUNT(s) FROM SessionTokenEntity s WHERE s.active = true AND s.user.id = :userId AND s.token = :token")
@NamedQuery(name = "SessionToken.findUserByToken",
        query = "SELECT s.user FROM SessionTokenEntity s WHERE s.token = :token AND s.active = true")

public class SessionTokenEntity extends ValidationTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Logout timestamp
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    // Last access of the user with this session token
    @Column(name = "lastAccess")
    private LocalDateTime lastAccess = LocalDateTime.now();

    // Default constructor
    public SessionTokenEntity() {
    }

    /**
     * Method to validate the session token entity.
     * It checks if the last access is not null.
     * If it is null, it throws an IllegalArgumentException.
     * This is necessary because the last access must not be null for a session token entity, but it can be null for a validation token entity.
     */
    @PrePersist
    @PreUpdate
    public void validate() {
        if (lastAccess == null) {
            throw new IllegalArgumentException("lastAccess must not be null for SessionTokenEntity");
        }
    }

    // Getters and setters

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }
}