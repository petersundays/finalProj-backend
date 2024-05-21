package domcast.finalprojbackend.enums;

/**
 * Enum class that represents the subject of interest of the user.
 */

public enum InterestEnum {

    // The values are the subjects of interest of the user.

    THEME("theme"),
    CAUSE("cause"),
    KNOWLEDGE_AREA("knowledge_area");

    // The value of the subject of interest.
    private final String value;

    // Constructor of the enum class.
    InterestEnum(String value) {
        this.value = value;
    }

    // Getter of the value of the subject of interest.
    public String getValue() {
        return value;
    }
}
