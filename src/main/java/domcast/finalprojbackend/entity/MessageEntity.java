package domcast.finalprojbackend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class for the message table in the database.
 * This is an abstract class that is extended by the PersonalMessageEntity and the GroupMessageEntity.
 * Contains all the attributes of the message table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the message.
 * - content: the content of the message.
 * - sender: the sender of the message.
 * - timestamp: the timestamp of the message.
 * - read: the read status of the message.
 * - notification: the notification of the message.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name="message")
//@NamedQuery(name="Message.findMessagesBetweenUsers", query="SELECT m FROM MessageEntity m WHERE m.sender.id = :sender AND m.receiver.id = :receiver OR m.sender.id = :receiver AND m.receiver.id = :sender ORDER BY m.timestamp ASC")
@NamedQuery(name="Message.findMessageById", query="SELECT m FROM MessageEntity m WHERE m.id = :id")
//@NamedQuery(name="Message.findMessagesUnreadForUser", query="SELECT m FROM MessageEntity m WHERE m.receiver.id = :username AND m.read = false")

public abstract class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the message
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="id", nullable = false, unique = true, updatable = false)
    private int id;

    // Content of the message
    @Column (name="content", nullable = false, length = 20000, columnDefinition = "TEXT")
    private String content;

    // Sender of the message
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private UserEntity sender;

    // Timestamp of when the message was sent
    @Column (name="timestamp", nullable = false, updatable = false)
    private Timestamp timestamp;

    // Read status of the message
    @Column (name="'read'", nullable = false)
    private boolean read;

    // Notification associated with the message
    @OneToOne(mappedBy = "message")
    private NotificationEntity notification;

    // Default constructor
    public MessageEntity() {
    }

    // Constructor with parameters
    public MessageEntity(String content, UserEntity sender, Timestamp timestamp) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
        this.read = false;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public NotificationEntity getNotification() {
        return notification;
    }

    public void setNotification(NotificationEntity notification) {
            this.notification = notification;
        }

}
