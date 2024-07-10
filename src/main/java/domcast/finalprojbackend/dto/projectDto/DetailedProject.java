package domcast.finalprojbackend.dto.projectDto;

import domcast.finalprojbackend.dto.RecordDto;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.userDto.InvitedOrCandidate;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Data transfer object for the project table in the database.
 * Contains all the attributes of the project table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the project.
 * - name: the name of the project.
 * - description: the description of the project.
 * - state: the state of the project.
 * - projectedStartDate: the projected start date of the project.
 * - deadline: the deadline of the project.
 * - keywords: the keywords of the project.
 * - skills: the skills of the project.
 * - resources: the resources of the project.
 * - responsible: the project's responsible.
 * - collaborators: the collaborators of the project.
 * - tasks: the tasks of the project.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class DetailedProject extends PublicProject implements Serializable {

    @XmlElement
    private Set<ChartTask> tasks;

    @XmlElement
    private int maxMembers;

    @XmlElement
    private List<InvitedOrCandidate> invited;

    @XmlElement
    private List<InvitedOrCandidate> candidates;

    @XmlElement
    private List<RecordDto> records;

    /**
     * Empty constructor
     */
    public DetailedProject() {
    }

    // Getters and setters

    public Set<ChartTask> getTasks() {
        return tasks;
    }

    public void setTasks(Set<ChartTask> tasks) {
        this.tasks = tasks;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public List<InvitedOrCandidate> getInvited() {
        return invited;
    }

    public void setInvited(List<InvitedOrCandidate> invited) {
        this.invited = invited;
    }

    public List<InvitedOrCandidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<InvitedOrCandidate> candidates) {
        this.candidates = candidates;
    }

    public List<RecordDto> getRecords() {
        return records;
    }

    public void setRecords(List<RecordDto> records) {
        this.records = records;
    }
}
