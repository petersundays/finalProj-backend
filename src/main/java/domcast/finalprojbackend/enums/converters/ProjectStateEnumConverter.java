package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.ProjectStateEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the ProjectStateEnum.
 * Contains methods to convert between ProjectStateEnum and Integer.
 * The class is used to convert the ProjectStateEnum to Integer when storing it in the database.
 * The class is used to convert the Integer to ProjectStateEnum when retrieving it from the database.
 * @author José Castro
 * @author Pedro Domingos
 */

// The annotation is used to specify that the class is a converter.
// The autoApply attribute is set to true to specify that the converter should be applied to all attributes of the ProjectStateEnum type.
@Converter(autoApply = true)
public class ProjectStateEnumConverter implements AttributeConverter<ProjectStateEnum, Integer> {

    // Method to convert from ProjectStateEnum to Integer.
    @Override
    public Integer convertToDatabaseColumn(ProjectStateEnum state) {
        if (state == null) {
            return null;
        }
        return state.getValue();
    }

    // Method to convert from Integer to ProjectStateEnum.
    @Override
    public ProjectStateEnum convertToEntityAttribute(Integer stateValue) {
        if (stateValue == null) {
            return null;
        }
        return ProjectStateEnum.fromValue(stateValue);
    }
}