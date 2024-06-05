package domcast.finalprojbackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

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

public enum SkillTypeEnum {

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

/*    @JsonCreator
    public static SkillTypeEnum forValue(String value) {
        for (SkillTypeEnum skillTypeEnum : SkillTypeEnum.values()) {
            if (skillTypeEnum.getValue().equals(value)) {
                return skillTypeEnum;
            }
        }
        throw new IllegalArgumentException("Invalid SkillTypeEnum value: " + value);
    }*/
}