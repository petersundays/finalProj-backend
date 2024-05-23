package domcast.finalprojbackend.enums.converters;

import domcast.finalprojbackend.enums.SkillTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter class for the SkillTypeEnum.
 * Contains methods to convert between SkillTypeEnum and Integer.
 * The class is used to convert the SkillTypeEnum to Integer when storing it in the database.
 * The class is used to convert the Integer to SkillTypeEnum when retrieving it from the database.
 * @author José Castro
 * @author Pedro Domingos
 */

// The annotation is used to specify that the class is a converter.
// The autoApply attribute is set to true to specify that the converter should be applied to all attributes of the SkillTypeEnum type.
@Converter(autoApply = true)
public class SkillTypeEnumConverter implements AttributeConverter<SkillTypeEnum, Integer> {

    // Method to convert from SkillTypeEnum to Integer.
    @Override
    public Integer convertToDatabaseColumn(SkillTypeEnum skillType) {
        if (skillType == null) {
            return null;
        }
        return skillType.getId();
    }

    // Method to convert from Integer to SkillTypeEnum.
    @Override
    public SkillTypeEnum convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return SkillTypeEnum.fromId(id);
    }
}