package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the TypeOfUserEnum.
 * Contains methods to convert between TypeOfUserEnum and Integer.
 * The class is used to convert the TypeOfUserEnum to Integer when storing it in the database.
 * The class is used to convert the Integer to TypeOfUserEnum when retrieving it from the database.
 * @author José Castro
 * @author Pedro Domingos
 */

// The annotation is used to specify that the class is a converter.
// The autoApply attribute is set to true to specify that the converter should be applied to all attributes of the TypeOfUserEnum type.
@Converter(autoApply = true)
public class TypeOfUserEnumConverter implements AttributeConverter<TypeOfUserEnum, Integer> {

    // Method to convert from TypeOfUserEnum to Integer.
    @Override
    public Integer convertToDatabaseColumn(TypeOfUserEnum userType) {
        if (userType == null) {
            return null;
        }
        return userType.getId();
    }

    // Method to convert from Integer to TypeOfUserEnum.
    @Override
    public TypeOfUserEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return TypeOfUserEnum.fromId(id);
    }
}