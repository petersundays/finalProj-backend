package domcast.finalprojbackend.dto.interestDto;

import domcast.finalprojbackend.enums.InterestEnum;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class for Interest.
 * This class is used to transfer interest data between different parts of the application,
 * specifically to list interests in frontend.
 * It includes the name and type of the interest.
 * @see InterestEnum
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class InterestToList extends InterestDto implements Serializable {

    @XmlElement
    private int id;  // Id of the interest

    /**
     * Default constructor for InterestDto.
     */
    public InterestToList() {
    }

    /**
     * Constructor for InterestDto with name and type parameters.
     *
     * @param name the name of the interest
     * @param type the type of the interest
     */
    public InterestToList(String name, int type, int id) {
        super(name, type);
        this.id = id;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
