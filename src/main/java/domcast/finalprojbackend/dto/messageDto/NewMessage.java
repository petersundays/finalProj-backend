package domcast.finalprojbackend.dto.messageDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class NewMessage implements Serializable {

    @XmlElement
    private String subject;

    @XmlElement
    private String content;

    /**
     * Default constructor for NewMessage.
     */
    public NewMessage() {
    }

    public NewMessage(String subject, String content) {
        this.subject = subject;
        this.content = content;

    }

    // Getters and setters


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
