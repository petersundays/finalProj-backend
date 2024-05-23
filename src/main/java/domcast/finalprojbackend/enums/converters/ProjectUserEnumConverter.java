package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.ProjectUserEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the ProjectUserEnum.
 * Contains methods to convert between ProjectUserEnum and Integer.
 * The class is used to convert the ProjectUserEnum to Integer when storing it in the database.
 * The class is used to convert the Integer to ProjectUserEnum when retrieving it from the database.
 * @author José Castro
 * @author Pedro Domingos
 */

// The annotation is used to specify that the class is a converter.
// The autoApply attribute is set to true to specify that the converter should be applied to all attributes of the ProjectUserEnum type.
@Converter(autoApply = true)
public class ProjectUserEnumConverter implements AttributeConverter<ProjectUserEnum, Integer> {

    // Method to convert from ProjectUserEnum to Integer.
    @Override
    public Integer convertToDatabaseColumn(ProjectUserEnum role) {
        if (role == null) {
            return null;
        }
        return role.getId();
    }

    // Method to convert from Integer to ProjectUserEnum.
    @Override
    public ProjectUserEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return ProjectUserEnum.fromId(id);
    }
}