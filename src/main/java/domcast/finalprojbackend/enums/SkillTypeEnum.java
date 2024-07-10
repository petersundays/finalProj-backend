package domcast.finalprojbackend.enums;

import domcast.finalprojbackend.dto.userDto.EnumDTO;
import domcast.finalprojbackend.enums.intefarce.ConvertibleToEnumDTO;

/**
 * Enum class that represents the type of skill in the system.
 * Each skill type has an associated id and value.
 * The values are the types of skills that the user can have or a project can require.
 * The id of the type of skill.
 * The value of the type of skill.
 * The constructor of the enum class.
 * The getter of the id of the type of skill.
 * The getter of the value of the type of skill.
 * The method that returns the type of skill by its id.
 * @author José Castro
 * @author Pedro Domingos
 */

public enum SkillTypeEnum implements ConvertibleToEnumDTO {

    // The values are the types of skills that the user can have or a project can require.
    KNOWLEDGE (1, "Knowledge"),
    SOFTWARE (2, "Software"),
    HARDWARE (3, "Hardware"),
    TOOLS (4, "Tools");

    // The id of the type of skill.
    private final int id;
    // The value of the type of skill.
    private final String value;

    // Constructor of the enum class.
    SkillTypeEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    // Getter of the id of the type of skill.
    public int getId() {
        return id;
    }

    // Getter of the value of the type of skill.
    public String getValue() {
        return value;
    }

    /**
     * Returns the type of skill by its id.
     *
     * @param id the id of skill's type
     * @return the type of skill if found, throws an exception otherwise
     */
    public static SkillTypeEnum fromId(int id) {
        for (SkillTypeEnum skillType : SkillTypeEnum.values()) {
            if (skillType.getId() == id) {
                return skillType;
            }
        }
        throw new IllegalArgumentException("Invalid SkillTypeEnum id: " + id);
    }

    /**
     * Method that checks if the SkillTypeEnum contains a type with the given id.
     * @param id the id to check
     * @return true if the SkillTypeEnum contains the id, false otherwise
     */
    public static boolean containsId(int id) {
        for (SkillTypeEnum skillTypeEnum : SkillTypeEnum.values()) {
            if (skillTypeEnum.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the enum to an EnumDTO object.
     * @return the EnumDTO object
     */
    @Override
    public EnumDTO toEnumDTO() {
        // if value is a String, pass it to the forth parameter and leave the third parameter as 0
        // if value is an int, pass it to the third parameter and leave the forth parameter as null
        return new EnumDTO(id, name(), 0, value);
    }
}