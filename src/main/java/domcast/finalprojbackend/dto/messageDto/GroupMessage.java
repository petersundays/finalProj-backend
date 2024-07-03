package domcast.finalprojbackend.dto.messageDto;

import domcast.finalprojbackend.entity.ProjectEntity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

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
public class GroupMessage extends Message implements Serializable {

    @XmlElement
    private ProjectEntity project;

    /**
     * Default constructor
     */
    public GroupMessage() {
    }

    /**
     * Constructor with parameters
     *
     * @param project  the project to which the message is being sent
     */
    public GroupMessage(ProjectEntity project) {
        super();
        this.project = project;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

}