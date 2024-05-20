package domcast.finalprojbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity class for the sessionToken table in the database.
 * This class extends the TokenEntity class.
 * Contains all the attributes of the sessionToken table and their getters and setters.
 * The attributes are the following:
 * - lastAccess: the last access of the session token.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name = "sessionToken")
public class SessionTokenEntity extends TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Last access of the user with this session token
    @Column(name = "lastAccess")
    private LocalDateTime lastAccess;

    // Default constructor
    public SessionTokenEntity() {
    }

    // Getters and setters

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }
}