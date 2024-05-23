package domcast.finalprojbackend.enums;

/**
 * Enum class that represents the type of user in the system.
 * Each user type has an associated id and value.
 */

public enum TypeOfUserEnum {

    // The values are the types of users that can exist in the system.
    ADMIN (1, 300),
    STANDARD (2, 200),
    NOT_CONFIRMED (3, 100);

    // The id of the type of user.
    private final int id;
    // The value of the type of user.
    private final int value;

    // Constructor of the enum class.
    TypeOfUserEnum(int id, int value) {
        this.id = id;
        this.value = value;
    }

    // Getter of the id of the type of user.
    public int getId() {
        return id;
    }

    // Getter of the value of the type of user.
    public int getValue() {
        return value;
    }

    // Method that returns the type of user by its id.
    public static TypeOfUserEnum fromId(int id) {
        for (TypeOfUserEnum userType : TypeOfUserEnum.values()) {
            if (userType.getId() == id) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Invalid TypeOfUserEnum id: " + id);
    }
}