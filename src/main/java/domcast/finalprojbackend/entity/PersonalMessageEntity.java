package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the personal_message table in the database.
 * This class extends the MessageEntity class.
 * Contains all the attributes of the personal_message table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the personal message.
 * - sender: the sender of the personal message.
 * - receiver: the receiver of the personal message.
 * - message: the message of the personal message.
 * - timestamp: the timestamp of the personal message.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
public class PersonalMessageEntity extends MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Receiver of the personal message
    @ManyToOne
    @JoinColumn(name = "receiverUserId", referencedColumnName = "id")
    private UserEntity receiver;

    // Default constructor
    public PersonalMessageEntity() {
    }

    // Getters and setters

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }
}