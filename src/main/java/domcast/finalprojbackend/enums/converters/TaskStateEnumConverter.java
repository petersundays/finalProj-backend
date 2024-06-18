package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.TaskStateEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the TaskStateEnum.
 * Contains methods to convert between TaskStateEnum and Integer.
 * The class is used to convert the TaskStateEnum to Integer when storing it in the database.
 * The class is used to convert the Integer to TaskStateEnum when retrieving it from the database.
 * @author José Castro
 * @author Pedro Domingos
 */

// The annotation is used to specify that the class is a converter.
// The autoApply attribute is set to true to specify that the converter should be applied to all attributes of the TaskStateEnum type.
@Converter(autoApply = true)
public class TaskStateEnumConverter implements AttributeConverter<TaskStateEnum, Integer> {

    // Method to convert from TaskStateEnum to Integer.
    @Override
    public Integer convertToDatabaseColumn(TaskStateEnum state) {
        if (state == null) {
            return null;
        }
        return state.getValue();
    }

    // Method to convert from Integer to TaskStateEnum.
    @Override
    public TaskStateEnum convertToEntityAttribute(Integer stateValue) {
        if (stateValue == null) {
            return null;
        }
        return TaskStateEnum.fromValue(stateValue);
    }
}