package domcast.finalprojbackend.enums;

/**
 * Enum class that represents if an object is a component or a resource.
 */
public enum ComponentResourceEnum {

    // The values are the types of object.
    COMPONENT ("component"),
    RESOURCE ("resource");

    // The value of the type of object.
    private final String value;

    // Constructor of the enum class.
    ComponentResourceEnum(String value) {
        this.value = value;
    }

    // Getter of the value of the type of object.
    public String getValue() {
        return value;
    }
}
