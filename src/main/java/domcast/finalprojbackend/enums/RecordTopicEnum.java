package domcast.finalprojbackend.enums;

import domcast.finalprojbackend.dto.EnumDTO;
import domcast.finalprojbackend.enums.intefarce.ConvertibleToEnumDTO;

/**
 * Enum class for the record_topic table in the database.
 * Contains all the possible values for the topic attribute of the record_topic table.
 * The values are the possible topics of a record.
 * The id of the topic.
 * The value of the topic.
 * The constructor of the enum class.
 * The getter of the id of the topic.
 * The getter of the value of the topic.
 * The method that returns the topic by its id.
 * @author José Castro
 * @author Pedro Domingos
 */

public enum RecordTopicEnum implements ConvertibleToEnumDTO {

    // The record can have one of the following topics
    TEAM (1, "Team"),
    PROJECT_DATA (2, "Project Data"),
    EXECUTION_PLAN (3, "Execution Plan"),
    PROJECT_STATE (4, "Project State"),
    TASK_STATE (5, "Task State"),
    ANNOTATION (6, "Annotation");

    // The id of the topic
    private final int id;
    // The value of the topic
    private final String value;

    // Constructor
    RecordTopicEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    // Getter of the id of the topic
    public int getId() {
        return id;
    }

    // Getter of the value of the topic
    public String getValue() {
        return value;
    }

    /**
     * Returns the topic by its id.
     * @param id the id of the topic.
     * @return the topic.
     */
    public static RecordTopicEnum fromId(int id) {
        for (RecordTopicEnum topic : RecordTopicEnum.values()) {
            if (topic.getId() == id) {
                return topic;
            }
        }
        throw new IllegalArgumentException("Invalid RecordTopicEnum id: " + id);
    }

    /**
     * Checks if the enum is valid.
     * @param id the id of the enum.
     * @return true if the enum is valid, false otherwise.
     */
    public static boolean isValidEnum(int id) {
        for (RecordTopicEnum topic : RecordTopicEnum.values()) {
            if (topic.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Converts the enum to an EnumDTO object.
     * @return the EnumDTO object.
     */
    @Override
    public EnumDTO toEnumDTO() {
        // if value is a String, pass it to the forth parameter and leave the third parameter as 0
        // if value is an int, pass it to the third parameter and leave the forth parameter as null
        return new EnumDTO(id, name(), 0, value);
    }
}