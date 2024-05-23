package domcast.finalprojbackend.enums;

/**
 * Enum class that represents if an object is a component or a resource.
 * Contains the possible types of object.
 * The values are the types of object.
 * The id of the type of object.
 * The value of the type of object.
 * The constructor of the enum class.
 * The getter of the id of the type of object.
 * The getter of the value of the type of object.
 * The method that returns the type of object by its id.
 * @author José Castro
 * @author Pedro Domingos
 */

public enum ComponentResourceEnum {

    // The values are the types of object.
    COMPONENT (1, "component"),
    RESOURCE (2, "resource");

    // The id of the type of object.
    private final int id;
    // The value of the type of object.
    private final String value;

    // Constructor with parameters
    ComponentResourceEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    // Method that returns the type of object by its id.
    public static ComponentResourceEnum fromId(int id) {
        for (ComponentResourceEnum type : ComponentResourceEnum.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ComponentResourceEnum id: " + id);
    }
}
