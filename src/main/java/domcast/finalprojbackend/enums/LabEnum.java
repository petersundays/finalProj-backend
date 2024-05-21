package domcast.finalprojbackend.enums;

/**
 * Enum class that represents the lab of the project.
 */

public enum LabEnum {

        // The values are the labs of the project.

        LISBOA("Lisboa"),
        COIMBRA("Coimbra"),
        PORTO("Porto"),
        TOMAR("Tomar"),
        VISEU("Viseu"),
        VILA_REAL("Vila Real");

        // The value of the lab of the project.
        private final String value;

        // Constructor of the enum class.
        LabEnum(String value) {
            this.value = value;
        }

        // Getter of the value of the lab of the project.
        public String getValue() {
            return value;
        }
}
