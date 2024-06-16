package domcast.finalprojbackend.enums;

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

public enum LabEnum {

        // The values are the labs of the project.

        COIMBRA(1, "Coimbra"),
        LISBOA(1, "Lisboa"),
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

        // Method that returns the lab of the project by its value.
        public static LabEnum fromValue(String value) {
                for (LabEnum lab : LabEnum.values()) {
                        if (lab.getValue().equalsIgnoreCase(value)) {
                                return lab;
                        }
                }
                throw new IllegalArgumentException("Invalid LabEnum value: " + value);
        }

        // Method that returns the String representation of the lab of the project by its id.
        public static String fromIdToString(int id) {
                for (LabEnum lab : LabEnum.values()) {
                        if (lab.getId() == id) {
                                return lab.getValue();
                        }
                }
                throw new IllegalArgumentException("Invalid LabEnum id: " + id);
        }
}
