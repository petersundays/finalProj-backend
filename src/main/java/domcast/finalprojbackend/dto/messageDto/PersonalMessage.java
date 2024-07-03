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
    private MessageUser receiver;

    /**
     * Default constructor
     */
    public PersonalMessage() {
    }

    /**
     * Constructor with all parameters
     * @param content the content of the message
     * @param sender the sender of the message
     * @param receiver the receiver of the message
     */
    public PersonalMessage(int id, String content, MessageUser sender, MessageUser receiver, LocalDateTime timestamp) {
        super(id, content, sender, timestamp);
        this.receiver = receiver;
    }

    // Getters and setters

    public MessageUser getReceiver() {
        return receiver;
    }

    public void setReceiver(MessageUser receiver) {
        this.receiver = receiver;
    }

}
