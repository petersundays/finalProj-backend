package domcast.finalprojbackend.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import domcast.finalprojbackend.enums.ComponentResourceEnum;

/**
 * Converter class for the ComponentResourceEnum.
 * This class is used to convert the ComponentResourceEnum to an Integer and vice versa.
 * The class contains the necessary annotations to work with the database.
 * The class implements the AttributeConverter interface.
 * The class contains the convertToDatabaseColumn and convertToEntityAttribute methods.
 * The convertToDatabaseColumn method converts the ComponentResourceEnum to an Integer.
 */

// The @Converter annotation is used to specify that this class is a converter.
// The class implements the AttributeConverter interface with the ComponentResourceEnum and Integer types.
@Converter(autoApply = true)
public class ComponentResourceEnumConverter implements AttributeConverter<ComponentResourceEnum, Integer> {

    // The convertToDatabaseColumn method converts the ComponentResourceEnum to an Integer.
    @Override
    public Integer convertToDatabaseColumn(ComponentResourceEnum type) {
        if (type == null) {
            return null;
        }
        return type.getId();
    }

    // The convertToEntityAttribute method converts the Integer to a ComponentResourceEnum.
    @Override
    public ComponentResourceEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return ComponentResourceEnum.fromId(id);
    }
}