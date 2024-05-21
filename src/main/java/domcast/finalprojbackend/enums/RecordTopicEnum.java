package domcast.finalprojbackend.enums;

public enum RecordTopicEnum {

    // The values are the topics of the records.
    TEAM ("Team"),
    PROJECT_DATA ("Project Data"),
    EXECUTION_PLAN ("Execution Plan"),
    PROJECT_STATE ("Project State"),
    TASK_STATE ("Task State"),
    ANNOTATION ("Annotation");

    // The value of the topic of the record.
    private final String value;

    // Constructor of the enum class.
    RecordTopicEnum(String value) {
        this.value = value;
    }

    // Getter of the value of the topic of the record.
    public String getValue() {
        return value;
    }
}
