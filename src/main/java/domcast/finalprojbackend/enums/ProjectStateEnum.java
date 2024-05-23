package domcast.finalprojbackend.enums;

/**
 * Enum class for the project state.
 * Contains the possible states of a project.
 * The values are the possible states of a project.
 * The id of the state of the project.
 * The value of the state of the project.
 * The constructor of the enum class.
 * The getter of the id of the state of the project.
 * The getter of the value of the state of the project.
 * The method that returns the state of the project by its id.
 * @author José Castro
 * @author Pedro Domingos
 */

public enum ProjectStateEnum {
    // The values are the possible states of a project.
    PLANNING (1, 100),
    READY (2, 200),
    APPROVED (3, 300),
    IN_PROGRESS (4, 400),
    CANCLED (5, 500),
    FINISHED (6, 600);

    // The id of the state of the project.
    private final int id;
    // The value of the state of the project.
    private final int value;

    // Constructor of the enum class.
    ProjectStateEnum(int id, int value) {
        this.id = id;
        this.value = value;
    }

    // Getter of the id of the state of the project.
    public int getId() {
        return id;
    }

    // Getter of the value of the state of the project.
    public int getValue() {
        return value;
    }

    // Method that returns the state of the project by its id.
    public static ProjectStateEnum fromId(int id) {
        for (ProjectStateEnum state : ProjectStateEnum.values()) {
            if (state.getId() == id) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectStateEnum id: " + id);
    }
}