package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "personalMessage")
public class PersonalMessageEntity extends MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "receiverUserId", referencedColumnName = "id")
    private UserEntity receiver;

    public PersonalMessageEntity() {
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }
}