package domcast.finalprojbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serializable;

@Entity
public class ProjectMessageEntity extends MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;


    public ProjectMessageEntity() {
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject (ProjectEntity project) {
        this.project = project;
    }
}
