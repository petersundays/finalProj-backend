package domcast.finalprojbackend.dto.taskDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDateTime;
import java.util.Set;

@XmlRootElement
public class DetailedTask extends NewTask<ChartTask> {
    @XmlElement
    private int id;

    @XmlElement
    private int state;

    @XmlElement
    private Set<String> externalExecutors;

    // Default constructor
    public DetailedTask() {
    }

    // Constructor with all parameters from parent and this class
    public DetailedTask(String title, String description, LocalDateTime projectedStartDate, LocalDateTime deadline, int responsibleId, int projectId, Set<String> otherExecutors, Set<ChartTask> dependencies, Set<ChartTask> dependentTasks, int id, int state, Set<String> externalExecutors) {
        super(title, description, projectedStartDate, deadline, responsibleId, projectId, otherExecutors, dependencies, dependentTasks);
        this.id = id;
        this.state = state;
        this.externalExecutors = externalExecutors;
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