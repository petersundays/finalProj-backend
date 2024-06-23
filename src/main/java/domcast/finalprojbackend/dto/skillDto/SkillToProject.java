package domcast.finalprojbackend.dto.skillDto;

import domcast.finalprojbackend.enums.SkillTypeEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class to send skill information to the project in the frontend.
 * It includes the name, type and the id of the skill.
 *
 * @see SkillTypeEnum
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class SkillToProject extends SkillDto implements Serializable {

    @XmlElement
    private int id;

    /**
     * Empty constructor
     */

    public SkillToProject() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
