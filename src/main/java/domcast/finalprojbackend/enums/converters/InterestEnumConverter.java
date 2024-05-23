package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.InterestEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the InterestEnum.
 * This class is used to convert the InterestEnum to an Integer and vice versa.
 * The class contains the necessary annotations to work with the database.
 * The class implements the AttributeConverter interface.
 * The class contains the convertToDatabaseColumn and convertToEntityAttribute methods.
 * The convertToDatabaseColumn method converts the InterestEnum to an Integer.
 * @author José Castro
 * @author Pedro Domingos
 */

// The @Converter annotation is used to specify that this class is a converter.
// The class implements the AttributeConverter interface with the InterestEnum and Integer types.
@Converter(autoApply = true)
public class InterestEnumConverter implements AttributeConverter<InterestEnum, Integer> {

    // The convertToDatabaseColumn method converts the InterestEnum to an Integer.
    @Override
    public Integer convertToDatabaseColumn(InterestEnum interest) {
        if (interest == null) {
            return null;
        }
        return interest.getId();
    }

    // The convertToEntityAttribute method converts the Integer to an InterestEnum.
    @Override
    public InterestEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return InterestEnum.fromId(id);
    }
}