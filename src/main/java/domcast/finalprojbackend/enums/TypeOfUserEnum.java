package domcast.finalprojbackend.enums;

/**
 * Enum class that represents the type of user in the system.
 */

public enum TypeOfUserEnum {

    // The values are the permissions that the user has.
    // The higher the value, the more permissions the user has.

    ADMIN(300),
    STANDARD(200),
    NOT_CONFIRMED(100);

    // The value of the type of user.
    private final int value;

    // Constructor of the enum class.
    TypeOfUserEnum(int value) {
        this.value = value;
    }

    // Getter of the value of the type of user.
    public int getValue() {
        return value;
    }
}