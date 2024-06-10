package domcast.finalprojbackend.dto.taskDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Set;

@XmlRootElement
public class DetailedTask extends NewTask {
    @XmlElement
    private int id;

    @XmlElement
    private int state;

    @XmlElement
    private Set<String> externalExecutors;

    // Default constructor
    public DetailedTask() {
    }

    // Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Set<String> getExternalExecutors() {
        return externalExecutors;
    }

    public void setExternalExecutors(Set<String> externalExecutors) {
        this.externalExecutors = externalExecutors;
    }
}
