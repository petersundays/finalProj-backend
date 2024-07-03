
package domcast.finalprojbackend.dto.messageDto;

import domcast.finalprojbackend.entity.UserEntity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data transfer object for the message table in the database.
 * Contains all the attributes of the message table and their getters and setters.
 * The attributes are the following:
 * - content: the content of the message.
 * - sender: the sender of the message.
 * - receiver: the receiver of the message.
 * - timestamp: the timestamp of the message.
 * - read: a boolean that indicates if the message has been read.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class Message implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private String content;

    @XmlElement
    private UserEntity sender;

    @XmlElement
    private LocalDateTime timestamp;

    @XmlElement
    private boolean read;

    /**
     * Default constructor
     */
    public Message() {
    }

    /**
     * Constructor with parameters
     * @param id the id of the message
     * @param content the content of the message
     * @param sender the sender of the message
     * @param timestamp the timestamp of the message
     */
    public Message(int id, String content, UserEntity sender, LocalDateTime timestamp) {
        this.id = id;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}
