package domcast.finalprojbackend.dto.componentResourceDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class DetailedCR extends CRPreview implements Serializable {

    @XmlElement
    private int projectId;

    @XmlElement
    private String description;

    @XmlElement
    private int quantity;

    @XmlElement
    private long supplierContact;

    @XmlElement
    private String observations;

    /**
     * Default constructor for DetailedCR class
     */
    public DetailedCR() {
        super();
    }

    // Getters and setters

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getSupplierContact() {
        return supplierContact;
    }

    public void setSupplierContact(long supplierContact) {
        this.supplierContact = supplierContact;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
