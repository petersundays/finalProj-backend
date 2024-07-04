package domcast.finalprojbackend.dto.projectDto;

import domcast.finalprojbackend.entity.ProjectEntity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

/**
 * Data transfer object to return a list of projects found in the database.
 * Contains a list of ProjectPreview objects and the total number of projects.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class ProjectEntitiesList implements Serializable {

    @XmlElement
    private List<ProjectEntity> projects;

    @XmlElement
    private Long totalProjects;

    /**
     * Default constructor
     */
    public ProjectEntitiesList() {
    }

    /**
     * Constructor with all parameters
     * @param projects the list of projects
     * @param totalProjects the total number of projects
     */
    public ProjectEntitiesList(List<ProjectEntity> projects, Long totalProjects) {
        this.projects = projects;
        this.totalProjects = totalProjects;
    }

    // Getters and setters

    public List<ProjectEntity> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectEntity> projects) {
        this.projects = projects;
    }

    public Long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(Long totalProjects) {
        this.totalProjects = totalProjects;
    }

}
