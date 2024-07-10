package domcast.finalprojbackend.dto;

import domcast.finalprojbackend.dto.userDto.RecordAuthor;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data transfer object for the record table in the database.
 * Contains all the attributes of the record table and their getters and setters.
 * The attributes are the following:
 * author: the author of the record.
 * timestamp: the timestamp of the record.
 * content: the content of the record.
 * type: the type of the record.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class RecordDto implements Serializable {

    @XmlElement
    private RecordAuthor author;

    @XmlElement
    private LocalDateTime timestamp;

    @XmlElement
    private String content;

    @XmlElement
    private int type;


    public RecordDto() {
    }

    public RecordDto(RecordAuthor author, LocalDateTime timestamp, String content, int type) {
        this.author = author;
        this.timestamp = timestamp;
        this.content = content;
        this.type = type;
    }

    // Getters and Setters


    public RecordAuthor getAuthor() {
        return author;
    }

    public void setAuthor(RecordAuthor author) {
        this.author = author;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
