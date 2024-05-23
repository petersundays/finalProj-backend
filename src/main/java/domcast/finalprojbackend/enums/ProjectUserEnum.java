package domcast.finalprojbackend.enums;

/**
 * Enum class for the project_user table in the database.
 * Contains all the possible values for the role attribute of the project_user table.
 */

public enum ProjectUserEnum {

    // The user is a member of the project and can have one of the following roles
    MAIN_MANAGER (1, 300),
    MANAGER (2, 200),
    PARTICIPANT(3, 100);

    // The id of the role
    private final int id;
    // The value of the role
    private final int value;

    // Constructor
    ProjectUserEnum(int id, int value) {
        this.id = id;
        this.value = value;
    }

    // Getter of the id of the role
    public int getId() {
        return id;
    }

    // Getter of the value of the role
    public int getValue() {
        return value;
    }

    // Method that returns the role by its id
    public static ProjectUserEnum fromId(int id) {
        for (ProjectUserEnum role : ProjectUserEnum.values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectUserEnum id: " + id);
    }
}