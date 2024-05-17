package domcast.finalprojbackend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name="message")
//@NamedQuery(name="Message.findMessagesBetweenUsers", query="SELECT m FROM MessageEntity m WHERE m.sender.id = :sender AND m.receiver.id = :receiver OR m.sender.id = :receiver AND m.receiver.id = :sender ORDER BY m.timestamp ASC")
@NamedQuery(name="Message.findMessageById", query="SELECT m FROM MessageEntity m WHERE m.id = :id")
//@NamedQuery(name="Message.findMessagesUnreadForUser", query="SELECT m FROM MessageEntity m WHERE m.receiver.id = :username AND m.read = false")

public abstract class MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column (name="content", nullable = false, length = 20000, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private UserEntity sender;

    @Column (name="timestamp", nullable = false, updatable = false)
    private Timestamp timestamp;

    @Column (name="'read'", nullable = false)
    private boolean read;

    @OneToOne(mappedBy = "message")
    private NotificationEntity notification;

    public MessageEntity() {
    }

    public MessageEntity(String content, UserEntity sender, Timestamp timestamp) {
        this.content = content;
        this.sender = sender;
        this.timestamp = timestamp;
        this.read = false;
    }

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
