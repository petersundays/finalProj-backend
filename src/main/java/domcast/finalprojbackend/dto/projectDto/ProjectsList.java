package domcast.finalprojbackend.dto.projectDto;

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
public class ProjectsList implements Serializable {

    @XmlElement
    private List<ProjectPreview> projects;

    @XmlElement
    private int totalProjects;

    /**
     * Default constructor
     */
    public ProjectsList() {
    }

    /**
     * Constructor with all parameters
     * @param projects the list of projects
     * @param totalProjects the total number of projects
     */
    public ProjectsList(List<ProjectPreview> projects, int totalProjects) {
        this.projects = projects;
        this.totalProjects = totalProjects;
    }

    // Getters and setters

    public List<ProjectPreview> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectPreview> projects) {
        this.projects = projects;
    }

    public int getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }

}
