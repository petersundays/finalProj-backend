package domcast.finalprojbackend.enums.intefarce;

import domcast.finalprojbackend.dto.userDto.EnumDTO;

/**
 * Interface for converting an enum to an EnumDTO.
 */
public interface ConvertibleToEnumDTO {
    EnumDTO toEnumDTO();
}
