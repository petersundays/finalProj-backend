package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the keyword table in the database.
 * Creates a many-to-many relationship between the project and the interest, that in the project are discriminated as 'keywords'.
 * Contains all the attributes of the keyword table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the keyword.
 * - project: the project that uses the keyword.
 * - interest: the interest used by the project as a keyword.
 * - active: the status of the project-keyword relationship
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "project_keyword")
public class M2MKeyword implements Serializable {

    private static final long serialVersionUID = 1L;

    // Unique identifier for the keyword
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Project that uses the keyword
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;

    // Keyword used by the project
    @ManyToOne
    @JoinColumn(name = "keyword_id", referencedColumnName = "id")
    private KeywordEntity keyword;

    // Status of the project-keyword relationship
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Empty constructor
    public M2MKeyword() {
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

    public KeywordEntity getKeyword() {
        return keyword;
    }

    public void setKeyword(KeywordEntity keyword) {
        this.keyword = keyword;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
