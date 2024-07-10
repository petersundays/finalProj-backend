package domcast.finalprojbackend.dto.userDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * EnumDTO is a data transfer object that represents an enum.
 */
@XmlRootElement
public class EnumDTO implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private String name;

    @XmlElement
    private int intValue;

    @XmlElement
    private String stringValue;

    /**
     * Default constructor for EnumDTO class
     */
    public EnumDTO() {
    }

    /**
     * Constructor for EnumDTO class
     * @param id the id of the enum
     * @param name the name of the enum
     * @param intValue the integer value of the enum
     * @param stringValue the string value of the enum
     */
    public EnumDTO(int id, String name, int intValue, String stringValue) {
        this.id = id;
        this.name = name;
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    // Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
