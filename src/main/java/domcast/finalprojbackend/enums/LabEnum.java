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
 */

public enum LabEnum {

        // The values are the labs of the project.

        LISBOA(1, "Lisboa"),
        COIMBRA(2, "Coimbra"),
        PORTO(3, "Porto"),
        TOMAR(4, "Tomar"),
        VISEU(5, "Viseu"),
        VILA_REAL(6, "Vila Real");

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
}
