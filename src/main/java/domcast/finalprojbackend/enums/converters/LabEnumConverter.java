package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.LabEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the LabEnum.
 * This class is used to convert the LabEnum to an Integer and vice versa.
 * The class contains the necessary annotations to work with the database.
 * The class implements the AttributeConverter interface.
 * The class contains the convertToDatabaseColumn and convertToEntityAttribute methods.
 * The convertToDatabaseColumn method converts the LabEnum to an Integer.
 * @author José Castro
 * @author Pedro Domingos
 */

// The @Converter annotation is used to specify that this class is a converter.
// The class implements the AttributeConverter interface with the LabEnum and Integer types.
@Converter(autoApply = true)
public class LabEnumConverter implements AttributeConverter<LabEnum, Integer> {

    // The convertToDatabaseColumn method converts the LabEnum to an Integer.
    @Override
    public Integer convertToDatabaseColumn(LabEnum lab) {
        if (lab == null) {
            return null;
        }
        return lab.getId();
    }

    // The convertToEntityAttribute method converts the Integer to a LabEnum.
    @Override
    public LabEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return LabEnum.fromId(id);
    }
}