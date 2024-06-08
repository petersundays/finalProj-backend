package domcast.finalprojbackend.enums;

/**
 * Enum class for the task state.
 * Contains the possible states of a task.
 * The values are the possible states of a task.
 * The id of the state of the task.
 * The value of the state of the task.
 * The constructor of the enum class.
 * The getter of the id of the state of the task.
 * The getter of the value of the state of the task.
 * The method that returns the state of the task by its id.
 * @author José Castro
 * @author Pedro Domingos
 */

public enum TaskStateEnum {
    // The values are the possible states of a task.
    PLANNED (1, 100),
    IN_PROGRESS (2, 200),
    FINISHED (3, 300);

    // The id of the state of the task.
    private final int id;
    // The value of the state of the task.
    private final int value;

    // Constructor of the enum class.
    TaskStateEnum(int id, int value) {
        this.id = id;
        this.value = value;
    }

    // Getter of the id of the state of the task.
    public int getId() {
        return id;
    }

    // Getter of the value of the state of the task.
    public int getValue() {
        return value;
    }

    // Method that returns the state of the task by its id.
    public static TaskStateEnum fromId(int id) {
        for (TaskStateEnum state : TaskStateEnum.values()) {
            if (state.getId() == id) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid TaskStateEnum id: " + id);
    }

    // Method that returns the state of the project by its value.
    public static TaskStateEnum fromValue(int value) {
        for (TaskStateEnum state : TaskStateEnum.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid TaskStateEnum value: " + value);
    }
}