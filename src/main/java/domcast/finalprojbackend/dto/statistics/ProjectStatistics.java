package domcast.finalprojbackend.dto.statistics;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents the statistics of a project.
 * It contains the average number of members and the average execution time of a project.
 * @author José Castro
 * @author Pedro Domingos
 */
@XmlRootElement
public class ProjectStatistics implements Serializable {

    @XmlElement
    private List<StatisticsPerLab> labStatistics;

    @XmlElement
    private double averageMembers;

    @XmlElement
    private double averageExecutionTime;

    @XmlElement
    private int totalProjects;


    public ProjectStatistics() {
        super();
    }

    public ProjectStatistics(List<StatisticsPerLab> labStatistics, double averageMembers, double averageExecutionTime, int totalProjects) {
        this.labStatistics = labStatistics;
        this.averageMembers = averageMembers;
        this.averageExecutionTime = averageExecutionTime;
        this.totalProjects = totalProjects;
    }

    // Getters and Setters


    public List<StatisticsPerLab> getLabStatistics() {
        return labStatistics;
    }

    public void setLabStatistics(List<StatisticsPerLab> labStatistics) {
        this.labStatistics = labStatistics;
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

    public int getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(int totalProjects) {
        this.totalProjects = totalProjects;
    }
}
