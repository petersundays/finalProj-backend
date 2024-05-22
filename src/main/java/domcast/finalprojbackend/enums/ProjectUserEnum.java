package domcast.finalprojbackend.enums;

/**
 * Enum class for the project_user table in the database.
 * Contains all the possible values for the role attribute of the project_user table.
 */

public enum ProjectUserEnum {

    // The user is a member of the project and can have one of the following roles
    MAIN_MANAGER (300),
    MANAGER (200),
    PARTICIPANT(100);

    // The value of the role
    private final int value;

    // Constructor
    ProjectUserEnum(int value) {
        this.value = value;
    }
}
