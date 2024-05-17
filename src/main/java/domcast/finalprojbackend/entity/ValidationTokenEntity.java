package domcast.finalprojbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "validationToken")
public class ValidationTokenEntity extends TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "creationTime")
    private LocalDateTime creationTime;


    public ValidationTokenEntity() {
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}