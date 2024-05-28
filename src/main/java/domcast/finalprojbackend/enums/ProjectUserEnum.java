package domcast.finalprojbackend.enums;

/**
 * Enum class for the project_user table in the database.
 * Contains all the possible values for the role attribute of the project_user table.
 * The values are the possible roles of a user in a project.
 * The id of the role.
 * The value of the role.
 * The constructor of the enum class.
 * The getter of the id of the role.
 * The getter of the value of the role.
 * The method that returns the role by its id.
 * @author José Castro
 * @author Pedro Domingos
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

    // Method that returns the role by its value
    public static ProjectUserEnum fromValue(int value) {
        for (ProjectUserEnum role : ProjectUserEnum.values()) {
            if (role.getValue() == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid ProjectUserEnum value: " + value);
    }
}