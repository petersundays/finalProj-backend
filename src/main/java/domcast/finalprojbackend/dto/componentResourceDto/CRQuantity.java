package domcast.finalprojbackend.dto.componentResourceDto;

import jakarta.xml.bind.annotation.XmlElement;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Data Transfer Object (DTO) class to send component resource information to and from the frontend.
 * It includes the id and the quantity of the component resource.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class CRQuantity implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private int quantity;

    /**
     * Empty constructor
     */
    public CRQuantity() {
    }

    /**
     * Constructor with all the attributes
     * @param id the id of the component resource
     * @param quantity the quantity of the component resource
     */
    public CRQuantity(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    //Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


}
