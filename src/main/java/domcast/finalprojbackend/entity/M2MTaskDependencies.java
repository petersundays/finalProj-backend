package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the task_dependencies table in the database.
 * The task_dependencies table is a join table between the task table and itself.
 * Contains all the attributes of the task_dependencies table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the task relationship with other tasks.
 * - task: the task of the task relationship with other tasks.
 * - dependentTask: the dependent task of the task relationship with other tasks.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@Entity
@Table(name = "task_dependencies")

public class M2MTaskDependencies implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the task relationship with other tasks
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Task with which the task is associated
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    // Dependent task with which the task is associated
    @ManyToOne
    @JoinColumn(name = "dependent_task_id", nullable = false)
    private TaskEntity dependentTask;

    // Default constructor
    public M2MTaskDependencies() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    public TaskEntity getDependentTask() {
        return dependentTask;
    }

    public void setDependentTask(TaskEntity dependentTask) {
        this.dependentTask = dependentTask;
    }
}