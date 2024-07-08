package domcast.finalprojbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;

import java.io.Serializable;

/**
 * Entity class for the project_message table in the database.
 * This class extends the MessageEntity class.
 * Contains all the attributes of the project_message table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the project message.
 * - project: the project of the project message.
 * - message: the message of the project message.
 * - timestamp: the timestamp of the project message.
 * The class also contains the necessary annotations to work with the database.
 *  @author José Castro
 *  @author Pedro Domingos
 */
@Entity
@NamedQuery(name="Message.countUnreadProjectMessagesForUser",
        query="SELECT COUNT(m) FROM ProjectMessageEntity m WHERE m.project.id = :projectId AND m.read = false")
@NamedQuery(name="Message.getAllProjectMessagesWhereProjectIs",
        query="SELECT m FROM ProjectMessageEntity m WHERE m.project.id = :projectId ORDER BY m.timestamp DESC")

public class ProjectMessageEntity extends MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Project of the project message
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;

    /**
     * Default constructor
     */
    public ProjectMessageEntity() {
    }

    /**
     * Constructor with parameters
     * @param project the project of the project message
     */
    public ProjectMessageEntity(ProjectEntity project) {
        super();
        this.project = project;
    }

    // Getters and setters

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject (ProjectEntity project) {
        this.project = project;
    }

}
