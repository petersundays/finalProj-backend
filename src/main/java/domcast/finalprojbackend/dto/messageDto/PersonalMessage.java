package domcast.finalprojbackend.dto.messageDto;

import domcast.finalprojbackend.dto.userDto.MessageUser;
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
public class PersonalMessage extends Message implements Serializable {

    @XmlElement
    private String subject;

    @XmlElement
    private MessageUser receiver;

    @XmlElement
    private Integer invitedTo;

    /**
     * Default constructor
     */
    public PersonalMessage() {
    }

    /**
     * Constructor with all parameters
     * @param id the ID of the message
     * @param subject the subject of the message
     * @param content the content of the message
     * @param sender the sender of the message
     * @param receiver the receiver of the message
     * @param timestamp the timestamp of the message
     * @param invitedTo the ID of the project the message is related to
     */
    public PersonalMessage(int id, String subject, String content, MessageUser sender, MessageUser receiver, LocalDateTime timestamp, Integer invitedTo) {
        super(id, content, sender, timestamp);
        this.subject = subject;
        this.receiver = receiver;
        this.invitedTo = invitedTo;
    }

    // Getters and setters

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MessageUser getReceiver() {
        return receiver;
    }

    public void setReceiver(MessageUser receiver) {
        this.receiver = receiver;
    }

    public Integer getInvitedTo() {
        return invitedTo;
    }

    public void setInvitedTo(Integer invitedTo) {
        this.invitedTo = invitedTo;
    }

}
