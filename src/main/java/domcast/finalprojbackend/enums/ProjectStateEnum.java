package domcast.finalprojbackend.enums;

/**
 * Enum class for the project state.
 * Contains the possible states of a project.
 * The values are the possible states of a project.
 * The project's state id.
 * The project's state value.
 * The constructor of the enum class.
 * The getter of the project's state id.
 * The getter of the project's state value.
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
    CANCELED(5, 500),
    FINISHED (6, 600);

    // The id of the project's state.
    private final int id;
    // The value of the project's state.
    private final int value;

    /**
     * Constructor of the enum class.
     * @param id the project's state id
     * @param value the project's state value
     */
    ProjectStateEnum(int id, int value) {
        this.id = id;
        this.value = value;
    }

    // Getter of the project's state id.
    public int getId() {
        return id;
    }

    // Getter of the project's state value.
    public int getValue() {
        return value;
    }

    /**
     * Method that returns the state of the project by its id.
     * @param id the project's state id
     * @return the state of the project
     */
    public static ProjectStateEnum fromId(int id) {
        for (ProjectStateEnum state : ProjectStateEnum.values()) {
            if (state.getId() == id) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectStateEnum id: " + id);
    }

    /**
     * Method that returns the state of the project by its value.
     * @param value the project's state value
     * @return the state of the project
     */
    public static ProjectStateEnum fromValue(int value) {
        for (ProjectStateEnum state : ProjectStateEnum.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectStateEnum value: " + value);
    }

    /**
     * Method that returns the id of the project's state.
     * @param state the project's state
     * @return the id of the project's state
     */
    public int getIdFromState(TaskStateEnum state) {
        return state.getId();
    }
}