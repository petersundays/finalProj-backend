package domcast.finalprojbackend.dto.projectDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class to send project information to the frontend.
 * It includes the name, description and the lab id of the project.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public abstract class ProjectDto implements Serializable {

    @XmlElement
    private String name;

    @XmlElement
    private String description;

    @XmlElement
    private int labId;

    /**
     * Empty constructor
     */
    public ProjectDto() {
    }

    /**
     * Constructor with all the attributes
     * @param name the name of the project
     * @param description the description of the project
     * @param labId the lab that the project belongs to
     */
    public ProjectDto(String name, String description, int labId) {
        this.name = name;
        this.description = description;
        this.labId = labId;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLabId() {
        return labId;
    }

    public void setLabId(int labId) {
        this.labId = labId;
    }
}
