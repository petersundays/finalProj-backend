package domcast.finalprojbackend.dto.projectDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NewProjectDto {
    @XmlElement
    private String name;
}
