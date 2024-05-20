package domcast.finalprojbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * Entity class for the validationToken table in the database.
 * This class extends the TokenEntity class.
 * Contains all the attributes of the validationToken table and their getters and setters.
 * The attributes are the following:
 * - creationTime: the creation time of the validation token.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name = "validationToken")
public class ValidationTokenEntity extends TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Creation time of the validation token
    @Column(name = "creationTime")
    private LocalDateTime creationTime;

    // Default constructor
    public ValidationTokenEntity() {
    }

    // Getters and setters

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}