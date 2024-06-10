package domcast.finalprojbackend.bean.task;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.TaskDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.taskDto.NewTask;
import domcast.finalprojbackend.entity.M2MTaskDependencies;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.TaskEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.ProjectStateEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class TaskBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(TaskBean.class);

    @EJB
    private DataValidator dataValidator;

    @EJB
    private UserDao userDao;

    @EJB
    private TaskDao taskDao;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private ProjectStateEnum projectStateEnum;

    // Default constructor
    public TaskBean() {
    }

    public ChartTask newTask(NewTask newTask) {
        logger.info("Creating new task");

        if (newTask == null) {
            logger.error("The new task is null");
            return null;
        }

        if (!dataValidator.isTaskMandatoryDataValid(newTask)) {
            logger.error("The new task does not have all the mandatory data");
            return null;
        }

        // Create the new task and persist it in the database
        TaskEntity taskEntity = registerNewTaskInfo(newTask);

        if (taskEntity == null) {
            logger.error("Error registering new task info");
            return null;
        }

        try {
            taskDao.persist(taskEntity);
        } catch (Exception e) {
            logger.error("Error persisting new task", e);
            return null;
        }

        TaskEntity taskEntityFromDB = taskDao.findTaskByTitleResponsibleProject(newTask.getTitle(), newTask.getResponsibleId(), newTask.getProjectId());

        if (taskEntityFromDB == null) {
            logger.error("Error finding task by title, responsible id and project id");
            return null;
        }

        int state = projectStateEnum.getIdFromState(taskEntityFromDB.getState());

        return new ChartTask(taskEntityFromDB.getId(),
                taskEntityFromDB.getTitle(),
                state,
                taskEntityFromDB.getProjectedStartDate(),
                taskEntityFromDB.getDeadline());
    }

    public TaskEntity registerNewTaskInfo (NewTask newTask) {

        if (newTask == null) {
            logger.error("When registering new task info, the new task is null");
            return null;
        }

        TaskEntity taskEntity = new TaskEntity();

        UserEntity responsible = userDao.findUserById(newTask.getResponsibleId());

        if (responsible == null) {
            logger.error("The responsible user does not exist");
            return null;
        }

        Set<String> otherExecutors = null;

        if (newTask.getOtherExecutors() != null && !newTask.getOtherExecutors().isEmpty()) {
            otherExecutors = newTask.getOtherExecutors();
        }

        Set<TaskEntity> dependencies = getSetOfDependencies(newTask.getDependencies());

        if (dependencies == null) {
            logger.error("The task's dependencies are invalid");
            return null;
        }

        boolean isStartDateValid = isStartDateValid(newTask.getProjectedStartDate(), dependencies);

        if (!isStartDateValid) {
            logger.error("The start date of the new task is not valid");
            return null;
        }

        Set<M2MTaskDependencies> dependenciesRelationship = createTaskDependenciesRelationship(taskEntity, dependencies);

        Set<TaskEntity> dependentTasks = getSetOfDependencies(newTask.getDependentTasks());

        if (dependentTasks == null) {
            logger.error("The task's dependencies are invalid");
            return null;
        }

        updateDependentTasksStartDate(newTask.getDeadline(), dependentTasks);

        Set<M2MTaskDependencies> dependentRelationship = createTaskDependenciesRelationship(taskEntity, dependentTasks);


        ProjectEntity project;
        try {
            project = projectDao.findProjectById(newTask.getProjectId());
        } catch (Exception e) {
            logger.error("Error finding project by id", e);
            return null;
        }

        // Set the new task's data
        taskEntity.setTitle(newTask.getTitle());
        taskEntity.setDescription(newTask.getDescription());
        taskEntity.setProjectedStartDate(newTask.getProjectedStartDate());
        taskEntity.setDeadline(newTask.getDeadline());
        taskEntity.setResponsible(responsible);
        taskEntity.setOtherExecutors(otherExecutors);
        taskEntity.setDependencies(dependenciesRelationship);
        taskEntity.setDependentTasks(dependentRelationship);
        taskEntity.setProjectId(project);

        return taskEntity;
    }

    /**
     * Gets the set of task's dependencies
     * @param taskDependencies the set of task's dependencies
     * @return the set of task's dependencies
     */
    public Set<TaskEntity> getSetOfDependencies(Set<Integer> taskDependencies) {
        Set<TaskEntity> dependencies = new HashSet<>();

        if (taskDependencies != null && !taskDependencies.isEmpty()) {
            for (Integer dependencyId : taskDependencies) {
                if (dependencyId <= 0) {
                    logger.warn("The task's dependency id is invalid: {}", dependencyId);
                    continue;
                }
                TaskEntity dependency = taskDao.findTaskById(dependencyId);
                if (dependency == null) {
                    logger.warn("The task's dependency does not exist: {}", dependencyId);
                    continue;
                }
                dependencies.add(dependency);
            }
        }
        return dependencies;
    }

    /**
     * Creates the relationship between the new task and its dependencies or dependent tasks
     * @param taskEntity the new task
     * @param relatedTasks the dependencies or the dependent tasks of the new task
     * @return the set of task's dependencies
     */
    public Set<M2MTaskDependencies> createTaskDependenciesRelationship(TaskEntity taskEntity, Set<TaskEntity> relatedTasks) {
        logger.info("Creating the relationship between the new task and its dependencies");

        Set<M2MTaskDependencies> taskDependencies = new HashSet<>();
        try {
            for (TaskEntity relatedTask : relatedTasks) {
                M2MTaskDependencies taskDependency = new M2MTaskDependencies();
                taskDependency.setTask(taskEntity);
                taskDependency.setDependentTask(relatedTask);
                taskEntity.getDependencies().add(taskDependency);
                taskDependencies.add(taskDependency);
            }
        } catch (Exception e) {
            logger.error("Error creating task dependencies relationship", e);
            return null;
        }
        return taskDependencies;
    }

    /**
     * Checks if the start date of the new task is not before the deadline of its dependencies
     * @param projectedStartDate the start date of the new task
     * @param dependencies the dependencies of the new task
     * @return true if the start date of the new task is valid, false otherwise
     */
    public boolean isStartDateValid(LocalDateTime projectedStartDate, Set<TaskEntity> dependencies) {
        logger.info("Checking if the start date of the new task is not before the deadline of its dependencies");

        if (dependencies == null || dependencies.isEmpty()) {
            logger.error("The task has no dependencies, so its start date cannot be valid");
            return false;
        }

        int countInvalid = 0;
        for (TaskEntity dependency : dependencies) {
            if (projectedStartDate.isBefore(dependency.getDeadline())) {
                countInvalid++;
            }
        }

        if (countInvalid > 0) {
            logger.error("The projected start date of the new task is earlier than the deadline of " + countInvalid + " of its dependencies");
            return false;
        }

        return true;
    }

    /**
     * Updates the start date of the dependent tasks
     * @param newTaskDeadline the deadline of the new task
     * @param dependentTasks the dependent tasks of the new task
     */
    public void updateDependentTasksStartDate(LocalDateTime newTaskDeadline, Set<TaskEntity> dependentTasks) {
        logger.info("Updating the start date of the dependent tasks");

        if (newTaskDeadline == null) {
            logger.error("The deadline of the new task is null");
            return;
        }

        if (dependentTasks == null || dependentTasks.isEmpty()) {
            logger.info("The new task has no dependent tasks");
            return;
        }

        for (TaskEntity dependentTask : dependentTasks) {
            if (dependentTask.getProjectedStartDate().isBefore(newTaskDeadline)) {
                dependentTask.setProjectedStartDate(newTaskDeadline);
                taskDao.merge(dependentTask);
                logger.info("Updated the start date of dependent task {} to {}", dependentTask.getId(), newTaskDeadline);
            }
        }
    }
}
