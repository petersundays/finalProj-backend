package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity class for the record table in the database.
 * Contains all the attributes of the record table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the record.
 * - projectId: the project id of the record.
 * - authorId: the author id of the record.
 * - timestamp: the timestamp of the record.
 * - content: the content of the record.
 * - type: the type of the record.
 * - task: the task of the record.
 * The class also contains the necessary annotations to work with the database.
 */

@Entity
@Table(name = "record")

public class RecordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Id of the project to which the record belongs
    @Column(name = "project_id", nullable = false)
    private int projectId;

    // Id of the author of the record
    @Column(name = "author_id", nullable = false)
    private int authorId;

    // Timestamp of the record
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Content of the record
    @Column(name = "content", nullable = false)
    private String content;

    // Type of the record
    @Column(name = "type", nullable = false)
    private int type;

    // Task to which the record belongs
    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private TaskEntity task;

    // Default constructor
    public RecordEntity() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }
}
