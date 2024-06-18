package domcast.finalprojbackend.dto.taskDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class EditTask extends NewTask<Integer> implements Serializable {

    @XmlElement
    private int State;

    /**
     * Default constructor
     */
    public EditTask() {
    }

    /**
     * Constructor with all the attributes
     * @param title the title of the task
     * @param description the description of the task
     * @param projectedStartDate the projected start date of the task
     * @param deadline the deadline of the task
     * @param responsibleId the responsible for the task
     * @param projectId the project of the task
     * @param otherExecutors the other users that are executors of the task
     * @param dependencies the id's of other tasks that this task depends on
     * @param dependentTasks the id's of other tasks that depend on this task
     * @param state the state of the task
     */
    public EditTask(String title, String description, java.time.LocalDateTime projectedStartDate, java.time.LocalDateTime deadline, int responsibleId, int projectId, java.util.Set<String> otherExecutors, java.util.Set<Integer> dependencies, java.util.Set<Integer> dependentTasks, int state) {
        super(title, description, projectedStartDate, deadline, responsibleId, projectId, otherExecutors, dependencies, dependentTasks);
        State = state;
    }

    /**
     * Getter for the state of the task
     * @return the state of the task
     */

    public int getState() {
        return State;
    }

    /**
     * Setter for the state of the task
     * @param state the state of the task
     */
    public void setState(int state) {
        State = state;
    }
}
