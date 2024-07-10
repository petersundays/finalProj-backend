package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.MessageAndLogEnum;
import domcast.finalprojbackend.enums.converters.MessageAndLogEnumConverter;
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
 * @author José Castro
 * @author Pedro Domingos
 */
@Entity
@Table(name = "record")

@NamedQuery(name = "Record.DoesRecordExist",
            query = "SELECT r FROM RecordEntity r WHERE r.project.id = :projectId AND r.author.id = :authorId AND r.type = :type AND r.timestamp BETWEEN :startTimestamp AND :endTimestamp")
@NamedQuery(name = "Record.getRecordsByProject",
            query = "SELECT r FROM RecordEntity r WHERE r.project.id = :projectId")

public class RecordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the record
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "content", nullable = false)
    private String content;

    @Convert(converter = MessageAndLogEnumConverter.class)
    @Column(name = "type", nullable = false)
    private MessageAndLogEnum type;

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

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
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

    public MessageAndLogEnum getType() {
        return type;
    }

    public void setType(MessageAndLogEnum type) {
        this.type = type;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }
}
