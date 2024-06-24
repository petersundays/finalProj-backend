package domcast.finalprojbackend.dto.projectDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@XmlRootElement
public class EditProject extends ProjectDto implements Serializable {

    @XmlElement
    private LocalDateTime projectedStartDate;

    @XmlElement
    private LocalDateTime deadline;

    @XmlElement
    private Set<Integer> keywords;

    @XmlElement
    private Set<Integer> skills;

    @XmlElement
    private Set<Integer> resources;

    /**
     * Empty constructor
     */
    public EditProject() {}

    /**
     * Constructor with all the attributes
     * @param name the name of the project
     * @param description the description of the project
     * @param labId the lab that the project belongs to
     * @param projectedStartDate the projected start date of the project
     * @param deadline the deadline of the project
     * @param keywords the keywords of the project
     * @param skills the skills of the project
     * @param resources the resources of the project
     */

    public EditProject(String name, String description, int labId, LocalDateTime projectedStartDate, LocalDateTime deadline, Set<Integer> keywords, Set<Integer> skills, Set<Integer> resources) {
        super(name, description, labId);
        this.projectedStartDate = projectedStartDate;
        this.deadline = deadline;
        this.keywords = keywords;
        this.skills = skills;
        this.resources = resources;
    }

    // Getters and setters

    public LocalDateTime getProjectedStartDate() {
        return projectedStartDate;
    }

    public void setProjectedStartDate(LocalDateTime projectedStartDate) {
        this.projectedStartDate = projectedStartDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Set<Integer> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<Integer> keywords) {
        this.keywords = keywords;
    }

    public Set<Integer> getSkills() {
        return skills;
    }

    public void setSkills(Set<Integer> skills) {
        this.skills = skills;
    }

    public Set<Integer> getResources() {
        return resources;
    }

    public void setResources(Set<Integer> resources) {
        this.resources = resources;
    }
}
