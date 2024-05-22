package domcast.finalprojbackend.enums;

/**
 * Enum class that represents the type of skill in the system.
 */

public enum SkillTypeEnum {

    // The values are the types of skills that the user can have or a project can require.

    KNOWLEDGE ("Knowledge"),
    SOFTWARE ("Software"),
    HARDWARE ("Hardware"),
    TOOLS ("Tools");

    // The value of the type of skill.
    private final String value;

    // Constructor of the enum class.
    SkillTypeEnum(String value) {
        this.value = value;
    }

    // Getter of the value of the type of skill.
    public String getValue() {
        return value;
    }
}
