package domcast.finalprojbackend.dto.statistics;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class StatisticsPerLab implements Serializable {

    @XmlElement
    private int city;

    @XmlElement
    private long totalProjects;

    @XmlElement
    private double percentage;

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


    public StatisticsPerLab() {
        super();
    }

    public StatisticsPerLab(int city, long totalProjects, double percentage, long approvedProjects, double approvedPercentage, long finishedProjects, double finishedPercentage, long canceledProjects, double canceledPercentage) {
        this.city = city;
        this.totalProjects = totalProjects;
        this.percentage = percentage;
        this.approvedProjects = approvedProjects;
        this.approvedPercentage = approvedPercentage;
        this.finishedProjects = finishedProjects;
        this.finishedPercentage = finishedPercentage;
        this.canceledProjects = canceledProjects;
        this.canceledPercentage = canceledPercentage;
    }

    // Getters and Setters


    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
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
