package domcast.finalprojbackend.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * Data transfer object for the keyword table in the database.
 * Contains all the attributes of the keyword table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the keyword.
 * - name: the name of the keyword.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class KeywordDto implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private String name;

    /**
     * Empty constructor
     */
    public KeywordDto() {
    }

    /**
     * Constructor with all the attributes
     * @param id the id of the keyword
     * @param name the name of the keyword
     */
    public KeywordDto(int id, String name) {
        this.id = id;
        this.name = name;
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
}
