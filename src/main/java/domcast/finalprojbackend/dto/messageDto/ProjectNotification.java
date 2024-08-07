package domcast.finalprojbackend.dto.messageDto;

import domcast.finalprojbackend.dto.userDto.MessageUser;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data transfer object to notify a user about a project.
 * Contains all the attributes of the project notification table in the database and their getters and setters.
 * The attributes are the following:
 * - projectId: the ID of the project.
 * The class also contains the necessary annotations to work with the database.
 * Extends the PersonalMessage class.
 * @see PersonalMessage
 * @see Message
 * @see MessageUser
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class ProjectNotification extends PersonalMessage implements Serializable {

    @XmlElement
    private int projectId;

    /**
     * Default constructor
     */
    public ProjectNotification() {
    }

    /**
     * Constructor with all parameters
     * @param id the ID of the message
     * @param subject the subject of the message
     * @param content the content of the message
     * @param sender the sender of the message
     * @param receiver the receiver of the message
     * @param timestamp the timestamp of the message
     * @param projectId the ID of the project
     */
    public ProjectNotification(int id, String subject, String content, MessageUser sender, MessageUser receiver, LocalDateTime timestamp, int projectId, int invitedTo) {
        super(id, subject, content, sender, receiver, timestamp, invitedTo);
        this.projectId = projectId;
    }

    // Getters and setters

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

}
