package domcast.finalprojbackend.enums;

/**
 * Enum class for the project state.
 * Contains the possible states of a project.
 */

public enum ProjectStateEnum {
    // The values are the possible states of a project.
    PLANNING (100),
    READY (200),
    APPROVED (300),
    IN_PROGRESS (400),
    CANCLED (500),
    FINISHED (600);

    // The value of the state of the project.
    private final int value;

    // Constructor of the enum class.
    ProjectStateEnum(int value) {
        this.value = value;
    }

    // Getter of the value of the state of the project.
    public int getValue() {
        return value;
    }
}
