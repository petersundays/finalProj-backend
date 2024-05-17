package domcast.finalprojbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "projectMessage")
public class ProjectMessageEntity extends MessageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "projectID")
    private int projectId;

    public ProjectMessageEntity() {
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectID(int projectId) {
        this.projectId = projectId;
    }
}
