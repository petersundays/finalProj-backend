package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class for the notification table in the database.
 * Contains all the attributes of the notification table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the notification.
 * - message: the message of the notification.
 * - receiver: the receiver of the notification.
 * - sender: the sender of the notification.
 * - timestamp: the timestamp of the notification.
 * - read: the read status of the notification.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name="notification")

@NamedQuery(name="Notification.findNotificationById", query="SELECT n FROM NotificationEntity n WHERE n.id = :id")
@NamedQuery(name="Notification.findNotificationsForUser", query="SELECT n FROM NotificationEntity n WHERE n.receiver.id = :username")
@NamedQuery(name="Notification.findNotificationBySenderAndReceiver", query="SELECT n FROM NotificationEntity n WHERE n.sender.id = :sender AND n.receiver.id = :receiver")
@NamedQuery(name="Notification.findLatestNotificationForSender", query="SELECT n FROM NotificationEntity n WHERE n.sender.id = :username ORDER BY n.id DESC")
@NamedQuery(name="Notification.findUnreadNotificationsForUser", query="SELECT n FROM NotificationEntity n WHERE n.receiver.id = :username AND n.read = false")

public class NotificationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the notification
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="id", nullable = false, unique = true, updatable = false)
    private int id;

    // Message associated with the notification
    @OneToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    private MessageEntity message;

    // Receiver of the notification
    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    private UserEntity receiver;

    // Sender of the notification
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private UserEntity sender;

    // Timestamp of when the notification was sent
    @Column (name="timestamp", nullable = false, updatable = false)
    private Timestamp timestamp;

    // Read status of the notification
    @Column (name="'read'", nullable = false)
    private boolean read;

    // Default constructor
    public NotificationEntity() {
    }

    // Constructor with parameters
    public NotificationEntity(UserEntity receiver, UserEntity sender, MessageEntity message) {
        this.receiver = receiver;
        this.sender = sender;
        this.read = false;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.message = message;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MessageEntity getMessage() {
        return message;
    }

    public void setMessage(MessageEntity message) {
        this.message = message;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
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

}