package domcast.finalprojbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessionToken")
public class SessionTokenEntity extends TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "lastAccess")
    private LocalDateTime lastAccess;

    public SessionTokenEntity() {
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }
}