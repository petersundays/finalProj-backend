package domcast.finalprojbackend.dto.skillDto;

import domcast.finalprojbackend.enums.SkillTypeEnum;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class for Skill.
 * This class is used to transfer skill data between different parts of the application,
 * specifically to list skills in frontend.
 * It includes the name and type of the skill.
 *
 * @see SkillTypeEnum
 * @author José Castro
 * @author Pedro Domingos
 */
public class SkillToList extends SkillDto implements Serializable {

    private int id;

    /**
     * Default constructor for SkillToList.
     */
    public SkillToList() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
