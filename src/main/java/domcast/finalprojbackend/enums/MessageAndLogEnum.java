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

public enum MessageAndLogEnum implements ConvertibleToEnumDTO {

    ADDED (1, "added to"),
    REMOVED (2, "removed from"),
    INVITED (3, "invited to"),
    STATUS_CHANGED (4, "status changed"),
    PROJECT_APPROVAL (5, "project approval"),
    APPROVED (6, "approved"),
    CANCELLED (7, "canceled"),
    LEFT_PROJECT (8, "left the project"),
    APPLIED (9, "applied to"),
    APPLICATION_REJECTED (10, "rejected"),
    APPLICATION_ACCEPTED (11, "accepted"),
    REJECTED_INVITATION (12, "rejected the invitation"),
    ACCEPTED_INVITATION (13, "accepted the invitation"),
    NEW_TASK (14, "new task"),
    TASK_STATUS_CHANGED (15, "task status changed"),
    TASK_EDITED (16, "task edited"),
    TASK_DELETED (17, "task deleted"),
    EMAIL (18, "email"),
    ANNOTATION (19, "annotation");

    // The id of the topic
    private final int id;

    // The value of the topic
    private final String value;

    MessageAndLogEnum(int id, String value) {
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
    public static MessageAndLogEnum fromId(int id) {
        for (MessageAndLogEnum topic : MessageAndLogEnum.values()) {
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
        for (MessageAndLogEnum topic : MessageAndLogEnum.values()) {
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