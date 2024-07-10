package domcast.finalprojbackend.enums;

import domcast.finalprojbackend.dto.userDto.EnumDTO;
import domcast.finalprojbackend.enums.intefarce.ConvertibleToEnumDTO;

/**
 * Enum class that represents the type of user in the system.
 * Each user type has an associated id and value.
 * The values are the types of users that can exist in the system.
 * The id of the type of user.
 * The value of the type of user.
 * The constructor of the enum class.
 * The getter of the id of the type of user.
 * The getter of the value of the type of user.
 * The method that returns the type of user by its id.
 * @author José Castro
 * @author Pedro Domingos
 */

public enum TypeOfUserEnum implements ConvertibleToEnumDTO {

    // The values are the types of users that can exist in the system.
    ADMIN(1, 300),
    STANDARD(2, 200),
    NOT_CONFIRMED(3, 100);

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

    /**
     * Method that returns the type of Enum by its id.
     *
     * @param id the id of the Enum
     * @return the TypeOfUserEnum object if found, null otherwise
     */
    public static TypeOfUserEnum fromId(int id) {
        for (TypeOfUserEnum userType : TypeOfUserEnum.values()) {
            if (userType.getId() == id) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Invalid TypeOfUserEnum id: " + id);
    }

    /**
     * Method that returns the type of Enum by its value.
     *
     * @param value the value of the Enum
     * @return the TypeOfUserEnum object if found, null otherwise
     */
    public static TypeOfUserEnum fromValue(int value) {
        for (TypeOfUserEnum userType : TypeOfUserEnum.values()) {
            if (userType.getValue() == value) {
                return userType;
            }
        }
        throw new IllegalArgumentException("Invalid TypeOfUserEnum value: " + value);
    }

    /**
     * Method that checks if the id is valid.
     *
     * @param id the id of the Enum
     * @return true if the id is valid, false otherwise
     */
    public static boolean isValidId(int id) {
        for (TypeOfUserEnum userType : TypeOfUserEnum.values()) {
            if (userType.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method that checks if the id is NOT_CONFIRMED.
     *
     * @param id the id of the Enum
     *           true if the id is NOT_CONFIRMED, false otherwise
     */
    public static boolean isNotConfirmed(int id) {
        return id == NOT_CONFIRMED.getId();
    }

    /**
     * Converts the Enum to an EnumDTO object.
     * @return the EnumDTO object
     */
    @Override
    public EnumDTO toEnumDTO() {
        // if value is a String, pass it to the forth parameter and leave the third parameter as 0
        // if value is an int, pass it to the third parameter and leave the forth parameter as null
        return new EnumDTO(id, name(), value, null);
    }
}