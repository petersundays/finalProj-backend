package domcast.finalprojbackend.enums.util;

import domcast.finalprojbackend.dto.EnumDTO;
import domcast.finalprojbackend.enums.intefarce.ConvertibleToEnumDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * EnumUtil is a utility class that provides methods for working with enums and converting them to DTOs.
 */
public class EnumUtil {

    public static <T extends Enum<?> & ConvertibleToEnumDTO> List<EnumDTO> getAllEnumDTOs(Class<T> enumClass) {
        List<EnumDTO> enumDTOs = new ArrayList<>();
        for (T enumValue : enumClass.getEnumConstants()) {
            enumDTOs.add(enumValue.toEnumDTO());
        }
        return enumDTOs;
    }
}