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
public class SessionTokenEntity extends ValidationTokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Logout timestamp
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    // IP from which the session was created
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    // Last access of the user with this session token
    @Column(name = "lastAccess", nullable = false)
    private LocalDateTime lastAccess = LocalDateTime.now();

    // Default constructor
    public SessionTokenEntity() {
    }

    // Getters and setters

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
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
}