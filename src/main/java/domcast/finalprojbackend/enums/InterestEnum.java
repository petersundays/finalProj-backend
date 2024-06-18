package domcast.finalprojbackend.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Enum that represents the subjects of interest of the user.
 * The subjects of interest are the themes, causes and knowledge areas.
 * @author José Castro
 * @author Pedro Domingos
 */

public enum InterestEnum {

    // The values are the subjects of interest of the user.

    THEME(1, "theme"),
    CAUSE(2, "cause"),
    KNOWLEDGE_AREA(3, "knowledge_area");

    // The id of the subject of interest.
    private final int id;
    // The value of the subject of interest.
    private final String value;

    // Constructor of the enum.
    InterestEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    // Getters of the enum.

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    // Method that returns the subject of interest by its id.
    public static InterestEnum fromId(int id) {
        for (InterestEnum e : values()) {
            if (e.id == id) {
                return e;
            }
        }
        return null;
    }

    /**
     * Method that checks if the InterestEnum contains a type with the given id.
     * @param id the id to check
     * @return true if the InterestEnum contains the id, false otherwise
     */
    public static boolean containsId(int id) {
        for (InterestEnum interestEnum : InterestEnum.values()) {
            if (interestEnum.getId() == id) {
                return true;
            }
        }
        return false;
    }
/*
    @JsonCreator
    public static InterestEnum forValue(String value) {
        for (InterestEnum interestEnum : InterestEnum.values()) {
            if (interestEnum.getValue().equals(value)) {
                return interestEnum;
            }
        }
        throw new IllegalArgumentException("Invalid InterestEnum value: " + value);
    }*/
}
