package domcast.finalprojbackend.dto;

import domcast.finalprojbackend.enums.SkillTypeEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Data Transfer Object (DTO) class for Skill.
 * This class is used to transfer skill data between different parts of the application.
 * It includes the name and type of the skill.
 *
 * @see SkillTypeEnum
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class SkillDto {
    @XmlElement
    private String name;  // Name of the skill

    @XmlElement
    private SkillTypeEnum type;  // Type of the skill

    /**
     * Default constructor for SkillDto.
     */
    public SkillDto() {
    }

    /**
     * Constructor for SkillDto with name and type parameters.
     *
     * @param name the name of the skill
     * @param type the type of the skill
     */
    public SkillDto(String name, SkillTypeEnum type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Getter for the name of the skill.
     *
     * @return the name of the skill
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the skill.
     *
     * @param name the new name of the skill
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the type of the skill.
     *
     * @return the type of the skill
     */
    public SkillTypeEnum getType() {
        return type;
    }

    /**
     * Setter for the type of the skill.
     *
     * @param type the new type of the skill
     */
    public void setType(SkillTypeEnum type) {
        this.type = type;
    }
}