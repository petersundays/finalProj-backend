package domcast.finalprojbackend.dto;

import domcast.finalprojbackend.enums.InterestEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Data Transfer Object (DTO) class for Interest.
 * This class is used to transfer interest data between different parts of the application.
 * It includes the name and type of the interest.
 * @see InterestEnum
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class InterestDto {
    @XmlElement
    private String name;  // Name of the interest

    @XmlElement
    private int type;  // Type of the interest

    /**
     * Default constructor for InterestDto.
     */
    public InterestDto() {
    }

    /**
     * Constructor for InterestDto with name and type parameters.
     *
     * @param name the name of the interest
     * @param type the type of the interest
     */
    public InterestDto(String name, int type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Getter for the name of the interest.
     *
     * @return the name of the interest
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the interest.
     *
     * @param name the new name of the interest
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the type of the interest.
     *
     * @return the type of the interest
     */
    public int getType() {
        return type;
    }

    /**
     * Setter for the type of the interest.
     *
     * @param type the new type of the interest
     */
    public void setType(int type) {
        this.type = type;
    }
}