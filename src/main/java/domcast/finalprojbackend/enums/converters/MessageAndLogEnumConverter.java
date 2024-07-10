package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.MessageAndLogEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the MessageEnum.
 * Contains methods to convert between MessageEnum and Integer.
 * The class is used to convert the MessageEnum to Integer when storing it in the database.
 * The class is used to convert the Integer to MessageEnum when retrieving it from the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Converter(autoApply = true)
public class MessageAndLogEnumConverter implements AttributeConverter<MessageAndLogEnum, Integer> {

    // Method to convert from MessageEnum to Integer.
    @Override
    public Integer convertToDatabaseColumn(MessageAndLogEnum messageAndLogEnum) {
        if (messageAndLogEnum == null) {
            return null;
        }
        return messageAndLogEnum.getId();
    }

    // Method to convert from Integer to MessageEnum.
    @Override
    public MessageAndLogEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return MessageAndLogEnum.fromId(id);
    }
}
