package domcast.finalprojbackend.dto.componentResourceDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CRPreview {

    @XmlElement
    private int id;

    @XmlElement
    private String name;

    @XmlElement
    private int type;

    @XmlElement
    private String brand;

    @XmlElement
    private Long partNumber;

    @XmlElement
    private String supplier;

    /**
     * Default constructor for CRPreview class
     */
    public CRPreview() {
    }

    // Getters and setters


    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public Long getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Long partNumber) {
        this.partNumber = partNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
