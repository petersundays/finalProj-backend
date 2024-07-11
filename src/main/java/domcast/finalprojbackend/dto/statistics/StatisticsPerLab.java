package domcast.finalprojbackend.dto.statistics;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class ProjectStatisticsDTO implements Serializable {

    @XmlElement
    private int locationId;

    @XmlElement
    private long totalProjects;

    @XmlElement
    private double percentage;

    @XmlElement
    private double averageMembers;

    @XmlElement
    private double averageExecutionTime;

    @XmlElement
    private long approvedProjects;

    @XmlElement
    private double approvedPercentage;

    @XmlElement
    private long finishedProjects;

    @XmlElement
    private double finishedPercentage;

    @XmlElement
    private long canceledProjects;

    @XmlElement
    private double canceledPercentage;


    public ProjectStatisticsDTO() {
        super();
    }

    public ProjectStatisticsDTO(int locationId, long totalProjects, double percentage, double averageMembers, double averageExecutionTime, long approvedProjects, double approvedPercentage, long finishedProjects, double finishedPercentage, long canceledProjects, double canceledPercentage) {
        this.locationId = locationId;
        this.totalProjects = totalProjects;
        this.percentage = percentage;
        this.averageMembers = averageMembers;
        this.averageExecutionTime = averageExecutionTime;
        this.approvedProjects = approvedProjects;
        this.approvedPercentage = approvedPercentage;
        this.finishedProjects = finishedProjects;
        this.finishedPercentage = finishedPercentage;
        this.canceledProjects = canceledProjects;
        this.canceledPercentage = canceledPercentage;
    }

    // Getters and Setters


    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getAverageMembers() {
        return averageMembers;
    }

    public void setAverageMembers(double averageMembers) {
        this.averageMembers = averageMembers;
    }

    public double getAverageExecutionTime() {
        return averageExecutionTime;
    }

    public void setAverageExecutionTime(double averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
    }

    public long getApprovedProjects() {
        return approvedProjects;
    }

    public void setApprovedProjects(long approvedProjects) {
        this.approvedProjects = approvedProjects;
    }

    public double getApprovedPercentage() {
        return approvedPercentage;
    }

    public void setApprovedPercentage(double approvedPercentage) {
        this.approvedPercentage = approvedPercentage;
    }

    public long getFinishedProjects() {
        return finishedProjects;
    }

    public void setFinishedProjects(long finishedProjects) {
        this.finishedProjects = finishedProjects;
    }

    public double getFinishedPercentage() {
        return finishedPercentage;
    }

    public void setFinishedPercentage(double finishedPercentage) {
        this.finishedPercentage = finishedPercentage;
    }

    public long getCanceledProjects() {
        return canceledProjects;
    }

    public void setCanceledProjects(long canceledProjects) {
        this.canceledProjects = canceledProjects;
    }

    public double getCanceledPercentage() {
        return canceledPercentage;
    }

    public void setCanceledPercentage(double canceledPercentage) {
        this.canceledPercentage = canceledPercentage;
    }
}
