package domcast.finalprojbackend.enums;

import domcast.finalprojbackend.dto.EnumDTO;
import domcast.finalprojbackend.enums.intefarce.ConvertibleToEnumDTO;

/**
 * Enum class that represents the lab of the project.
 * The labs are the following:
 * - LISBOA
 * - COIMBRA
 * - PORTO
 * - TOMAR
 * - VISEU
 * - VILA_REAL
 * @author José Castro
 * @author Pedro Domingos
 */

public enum LabEnum implements ConvertibleToEnumDTO {

        // The values are the labs of the project.

        COIMBRA(1, "Coimbra"),
        LISBOA(2, "Lisboa"),
        PORTO(3, "Porto"),
        TOMAR(4, "Tomar"),
        VILA_REAL(5, "Vila Real"),
        VISEU(6, "Viseu");

        // The id of the lab of the project.
        private final int id;
        // The value of the lab of the project.
        private final String value;

        // Constructor with parameters
        LabEnum(int id, String value) {
                this.id = id;
                this.value = value;
        }

        // Getters

        public int getId() {
                return id;
        }

        public String getValue() {
                return value;
        }

        // Method that returns the lab of the project by its id.
        public static LabEnum fromId(int id) {
                for (LabEnum lab : LabEnum.values()) {
                        if (lab.getId() == id) {
                                return lab;
                        }
                }
                throw new IllegalArgumentException("Invalid LabEnum id: " + id);
        }

        /**
         * Method that returns the lab of the project by its value.
         * @param value the lab's value
         * @return the lab of the project
         */
        public static LabEnum fromValue(String value) {
                for (LabEnum lab : LabEnum.values()) {
                        if (lab.getValue().equalsIgnoreCase(value)) {
                                return lab;
                        }
                }
                throw new IllegalArgumentException("Invalid LabEnum value: " + value);
        }

        /**
         * Method that checks if the lab id is valid.
         * @param id the lab id
         * @return a boolean value indicating if the lab id is valid
         */
        public static boolean isValidLabId(int id) {
                for (LabEnum lab : LabEnum.values()) {
                        if (lab.getId() == id) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Method that checks if a given string matches (ignoring case) any value from the enum.
         * @param str the string to check
         * @return a boolean value indicating if the string matches any value from the enum
         */
        public static boolean isValidLabValue(String str) {
                for (LabEnum lab : LabEnum.values()) {
                        if (lab.getValue().equalsIgnoreCase(str)) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * Method that returns the EnumDTO of the lab.
         * @return the EnumDTO of the lab
         */
        @Override
        public EnumDTO toEnumDTO() {
                // if value is a String, pass it to the forth parameter and leave the third parameter as 0
                // if value is an int, pass it to the third parameter and leave the forth parameter as null
                return new EnumDTO(id, name(), 0, value);
        }
}
