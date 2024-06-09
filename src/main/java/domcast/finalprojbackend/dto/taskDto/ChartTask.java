package domcast.finalprojbackend.dto.taskDto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * This class is used to represent a task in the chart.
 * It contains the following attributes:
 * - id: the id of the task.
 * - title: the title of the task.
 * - state: the state of the task.
 * - projectedStartDate: the projected start date of the task.
 * - deadline: the deadline of the task.
 * The class also contains the necessary annotations to work with the database.
 *
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class ChartTask implements Serializable {

    @XmlElement
    private int id;

    @XmlElement
    private String title;

    @XmlElement
    private int state;

    @XmlElement
    private LocalDateTime projectedStartDate;

    @XmlElement
    private LocalDateTime deadline;

    // Default constructor
    public ChartTask() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public LocalDateTime getProjectedStartDate() {
        return projectedStartDate;
    }

    public void setProjectedStartDate(LocalDateTime projectedStartDate) {
        this.projectedStartDate = projectedStartDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
