package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.RecordTopicEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the RecordTopicEnum.
 * Contains methods to convert between RecordTopicEnum and Integer.
 * The class is used to convert the RecordTopicEnum to Integer when storing it in the database.
 * The class is used to convert the Integer to RecordTopicEnum when retrieving it from the database.
 * @author José Castro
 * @author Pedro Domingos
 */

// The annotation is used to specify that the class is a converter.
// The autoApply attribute is set to true to specify that the converter should be applied to all attributes of the RecordTopicEnum type.
@Converter(autoApply = true)
public class RecordTopicEnumConverter implements AttributeConverter<RecordTopicEnum, Integer> {

    // Method to convert from RecordTopicEnum to Integer.
    @Override
    public Integer convertToDatabaseColumn(RecordTopicEnum topic) {
        if (topic == null) {
            return null;
        }
        return topic.getId();
    }

    // Method to convert from Integer to RecordTopicEnum.
    @Override
    public RecordTopicEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return RecordTopicEnum.fromId(id);
    }
}