package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.RecordTopicEnum;
import domcast.finalprojbackend.enums.converters.RecordTopicEnumConverter;
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
    private M2MProjectUser author;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "content", nullable = false)
    private String content;

    @Convert(converter = RecordTopicEnumConverter.class)
    @Column(name = "type", nullable = false)
    private RecordTopicEnum type;

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

    public M2MProjectUser getAuthor() {
        return author;
    }

    public void setAuthor(M2MProjectUser author) {
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

    public RecordTopicEnum getType() {
        return type;
    }

    public void setType(RecordTopicEnum type) {
        this.type = type;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }
}
