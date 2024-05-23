package domcast.finalprojbackend.enums;

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

public enum RecordTopicEnum {

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

    // Method that returns the topic by its id
    public static RecordTopicEnum fromId(int id) {
        for (RecordTopicEnum topic : RecordTopicEnum.values()) {
            if (topic.getId() == id) {
                return topic;
            }
        }
        throw new IllegalArgumentException("Invalid RecordTopicEnum id: " + id);
    }
}