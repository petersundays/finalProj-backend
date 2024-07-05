package domcast.finalprojbackend.dto.projectDto;

import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * Data transfer object for creating a new project.
 * Contains all the attributes of the project table and their getters and setters.
 * The attributes are the following:
 * - name: the name of the project.
 * - description: the description of the project.
 * - labId: the lab that the project belongs to.
 * - keywords: the keywords of the project.
 * - projectUsers: the users that are part of the project.
 * - existentSkills: the skills of the project that already exist in the database.
 * - existentResources: the resources of the project that already exist in the database.
 * - newSkills: the new skills of the project.
 * - newResources: the new resources of the project.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class NewProjectDto extends ProjectDto implements Serializable {

    @XmlElement
    private LocalDateTime projectedStartDate;

    @XmlElement
    private LocalDateTime deadline;

    @XmlElement
    private Set<String> keywords;

    @XmlElement
    private Set<Integer> existentSkills;

    @XmlElement
    private Map<Integer, Integer> existentResources;

    @XmlElement
    private int maxMembers;

    /**
     * Empty constructor
     */
    public NewProjectDto() {
    }

    /**
     * Constructor with all the attributes
     * @param name the name of the project
     * @param description the description of the project
     * @param labId the lab that the project belongs to
     * @param keywords the keywords of the project
     * @param existentSkills the skills of the project that already exist in the database
     * @param existentResources the resources of the project that already exist in the database
     * @param newSkills the new skills of the project
     * @param newResources the new resources of the project
     *
     */
    public NewProjectDto(String name, String description, int labId, Set<String> keywords, Set<Integer> existentSkills, Map<Integer, Integer> existentResources, Set<SkillDto> newSkills, Set<DetailedCR> newResources) {
        super(name, description, labId);
        this.keywords = keywords;
        this.existentSkills = existentSkills;
        this.existentResources = existentResources;
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

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public Set<Integer> getExistentSkills() {
        return existentSkills;
    }

    public void setExistentSkills(Set<Integer> existentSkills) {
        this.existentSkills = existentSkills;
    }

    public Map<Integer, Integer> getExistentResources() {
        return existentResources;
    }

    public void setExistentResources(Map<Integer, Integer> existentResources) {
        this.existentResources = existentResources;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }
}
