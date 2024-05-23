package domcast.finalprojbackend.enums;

/**
 * Enum that represents the subjects of interest of the user.
 * The subjects of interest are the themes, causes and knowledge areas.
 */

public enum InterestEnum {

    // The values are the subjects of interest of the user.

    THEME(1, "theme"),
    CAUSE(2, "cause"),
    KNOWLEDGE_AREA(3, "knowledge_area");

    // The id of the subject of interest.
    private final int id;
    // The value of the subject of interest.
    private final String value;

    // Constructor of the enum.
    InterestEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    // Getters of the enum.

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    // Method that returns the subject of interest by its id.
    public static InterestEnum fromId(int id) {
        for (InterestEnum e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        return null;
    }
}
