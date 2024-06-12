package domcast.finalprojbackend.bean.task;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.TaskDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.taskDto.DetailedTask;
import domcast.finalprojbackend.dto.taskDto.NewTask;
import domcast.finalprojbackend.entity.M2MTaskDependencies;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.TaskEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.TaskStateEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    private ProjectBean projectBean;

    // Default constructor
    public TaskBean() {
    }

    public ChartTask newTask(NewTask<Integer> newTask) {
        logger.info("Creating new task");

        if (newTask == null) {
            throw new IllegalArgumentException("The input for the new task is null");
        }

        if (!dataValidator.isTaskMandatoryDataValid(newTask)) {
            throw new IllegalArgumentException("The new task does not have all the mandatory data");
        }

        // Create the new task and persist it in the database
        TaskEntity taskEntity;

        try {
            taskEntity = registerNewTaskInfo(newTask);
        } catch (IllegalArgumentException e) {
            logger.error("Error registering new task info", e);
            throw new RuntimeException("Error registering new task info: " + e.getMessage(), e);
        }

        if (taskEntity == null) {
            throw new RuntimeException("Error registering new task info");
        }

        try {
            taskDao.persist(taskEntity);
        } catch (Exception e) {
            logger.error("Error persisting new task", e);
            throw new RuntimeException("Error persisting new task: " + e.getMessage(), e);
        }

        TaskEntity taskEntityFromDB = taskDao.findTaskByTitleResponsibleProject(newTask.getTitle(), newTask.getResponsibleId(), newTask.getProjectId());

        if (taskEntityFromDB == null) {
            throw new RuntimeException("Error finding task by title, responsible id and project id");
        }

        return new ChartTask(taskEntityFromDB.getId(),
                taskEntityFromDB.getTitle(),
                taskEntityFromDB.getState().getId(),
                taskEntityFromDB.getProjectedStartDate(),
                taskEntityFromDB.getDeadline());
    }

    public TaskEntity registerNewTaskInfo (NewTask<Integer> newTask) {

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

        if (!projectBean.isUserActiveInProject(newTask.getResponsibleId(), newTask.getProjectId())) {
            logger.error("The responsible user is not a member of the project");
            return null;
        }

        Set<String> otherExecutors = null;

        if (newTask.getOtherExecutors() != null && !newTask.getOtherExecutors().isEmpty()) {
            otherExecutors = newTask.getOtherExecutors();
        }

        Set<TaskEntity> dependencies = getSetOfDependencies(newTask.getDependencies(), newTask.getProjectId());

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

        Set<TaskEntity> dependentTasks = getSetOfDependencies(newTask.getDependentTasks(), newTask.getProjectId());

        if (dependentTasks == null) {
            logger.error("The task's dependencies are invalid");
            return null;
        }

        try {
            updateDependentTasksStartDate(newTask.getDeadline(), dependentTasks);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating dependent tasks start date", e);
            throw e;
        }

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
    public Set<TaskEntity> getSetOfDependencies(Set<Integer> taskDependencies, int projectId) {
        Set<TaskEntity> dependencies = new HashSet<>();

        if (taskDependencies != null && !taskDependencies.isEmpty()) {
            for (Integer dependencyId : taskDependencies) {
                if (dependencyId <= 0) {
                    logger.warn("The task's dependency id is invalid: {}", dependencyId);
                    continue;
                }
                TaskEntity dependency = taskDao.findTaskByIdAndProjectId(dependencyId, projectId);
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
            logger.info("The task has no dependencies, so its start date is always valid");
            return true;
        }

        int countInvalid = 0;
        for (TaskEntity dependency : dependencies) {
            if (projectedStartDate.isBefore(dependency.getDeadline())) {
                countInvalid++;
            }
        }

        if (countInvalid > 0) {
            logger.error("The projected start date of the new task is earlier than the deadline of {} of its dependencies", countInvalid);
            return false;
        }

        return true;
    }

    /**
     * Updates the start date of the dependent tasks.
     * The start date of the dependent task is updated to the deadline of the new task
     * if it is earlier than the current start date.
     * @param newTaskDeadline the deadline of the new task
     * @param dependentTasks the dependent tasks of the new task.
     *                       Throws IllegalArgumentException if the projected start date of a dependent task is after its deadline
     */
    public void updateDependentTasksStartDate(LocalDateTime newTaskDeadline, Set<TaskEntity> dependentTasks) {
        logger.info("Updating the start date of the dependent tasks");

        if (newTaskDeadline == null) {
            logger.error("The deadline of the new task is null");
            return;
        }

        if (dependentTasks == null || dependentTasks.isEmpty()) {
            logger.info("The new task has no dependent tasks, so no start dates to update");
            return;
        }

        for (TaskEntity dependentTask : dependentTasks) {
            if (dependentTask.getProjectedStartDate().isBefore(newTaskDeadline)) {
                dependentTask.setProjectedStartDate(newTaskDeadline);
                taskDao.merge(dependentTask);
                logger.info("Updated the start date of dependent task {} to {}", dependentTask.getId(), newTaskDeadline);
            } else if (dependentTask.getProjectedStartDate().isAfter(dependentTask.getDeadline())) {
                throw new IllegalArgumentException("The projected start date of dependent task " + dependentTask.getTitle() + " is being set after its deadline");
            }
        }
    }

    /**
     * Creates a detailed task, from an id passed as a parameter, that belongs to a ChartTask.
     * This task is used to display the detailed information of a task.
     * @param id the id of the task
     * @return the detailed task
     */
    public DetailedTask createDetailedTask (int id) {
        logger.info("Creating detailed task");

        // Find the task by its id
        TaskEntity taskEntity = findTaskById(id);

        Map<String, Set<String>> executors = setTaskExecutors(taskEntity.getOtherExecutors());

        Set<String> users = executors.get("users");
        Set<String> externalExecutors = executors.get("externalExecutors");

// Now you can use users and externalExecutors to set your variables
        Set<ChartTask> dependencies = createChartTaskFromRelationships(taskEntity.getDependencies());
        Set<ChartTask> dependentTasks = createChartTaskFromRelationships(taskEntity.getDependentTasks());

        return new DetailedTask(taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getProjectedStartDate(),
                taskEntity.getDeadline(),
                taskEntity.getResponsible().getId(),
                taskEntity.getProjectId().getId(),
                users,
                dependencies,
                dependentTasks,
                taskEntity.getId(),
                taskEntity.getState().getId(),
                externalExecutors);

    }

    /**
     * Finds a task by its id
     * @param id the id of the task
     * @return the task entity
     */
    public TaskEntity findTaskById(int id) {
        TaskEntity taskEntity;
        try {
            taskEntity = taskDao.findTaskById(id);
        } catch (Exception e) {
            logger.error("Error finding task by id", e);
            throw new RuntimeException("Error finding task by id: " + e.getMessage(), e);
        }

        if (taskEntity == null) {
            throw new IllegalArgumentException("Task with id " + id + " not found");
        }
        return taskEntity;
    }

    /**
     * Sets the task executors
     * @param executors the set of executors
     * @return a map with the users and the external executors
     */
    public Map<String, Set<String>> setTaskExecutors(Set<String> executors) {
        logger.info("Setting task executors");

        Set<String> users = new HashSet<>();
        Set<String> externalExecutors = new HashSet<>();
        boolean userExists;
        String firstName;
        String lastName;

        // Check if the task has executors
        // If it does not have executors, return an empty map
        if (executors == null || executors.isEmpty()) {
            logger.info("The task has no executors");
            return new HashMap<>();
        }

        // Check if the executors are users or external collaborators
        for (String executor : executors) {
            if (executor == null || executor.isBlank()) {
                logger.warn("The executor is null or blank");
                continue;
            }

            String[] names = executor.split(" ");

            firstName = names[0];
            lastName = names[names.length - 1];

            try {
                userExists = userDao.existsByFirstAndLastName(firstName, lastName);
            } catch (Exception e) {
                logger.error("Error checking if user exists by first and last name", e);
                throw new RuntimeException("Error checking if user exists by first and last name: " + e.getMessage(), e);
            }

            if (userExists) {
                users.add(executor);
            } else {
                externalExecutors.add(executor);
            }
        }

        // Combine users and externalExecutors into a map
        Map<String, Set<String>> allExecutors = new HashMap<>();
        allExecutors.put("users", users);
        allExecutors.put("externalExecutors", externalExecutors);

        return allExecutors;
    }

    /**
     * Creates chart tasks from the relationships between tasks
     * @param taskDependencies the set of task dependencies
     * @return the set of chart tasks
     */
    public Set<ChartTask> createChartTaskFromRelationships(Set<M2MTaskDependencies> taskDependencies) {
        logger.info("Creating chart tasks from relationships");

        Set<ChartTask> chartTasks = new HashSet<>();

        // Check if the task dependencies are null or empty
        if (taskDependencies == null || taskDependencies.isEmpty()) {
            logger.info("The task dependencies are null or empty");
            return chartTasks;
        }

        // Create chart tasks from the task dependencies
        for (M2MTaskDependencies taskDependency : taskDependencies) {
            TaskEntity dependentTask = taskDependency.getDependentTask();
            if (dependentTask != null) {
                chartTasks.add(new ChartTask(dependentTask.getId(),
                        dependentTask.getTitle(),
                        dependentTask.getState().getId(),
                        dependentTask.getProjectedStartDate(),
                        dependentTask.getDeadline()));
            } else {
                logger.warn("Dependent task is null for taskDependency with id: {}", taskDependency.getId());
            }
        }

        return chartTasks;
    }

    public DetailedTask updateTaskState(Integer taskId, Integer stateId) {
        logger.info("Updating task state");

        // Check if the task id and the state id are valid
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Invalid task id: " + taskId);
        }

        logger.info("Task id: {}", taskId);

        // Check if the state id is valid
        if (stateId == null || !TaskStateEnum.isValidId(stateId)) {
            throw new IllegalArgumentException("Invalid state id: " + stateId);
        }

        logger.info("State id: {}", stateId);

        // Find the task by its id
        TaskEntity taskEntity = findTaskById(taskId);

        logger.info("Task entity: {}", taskEntity);

        // Check if the task entity is null
        if (taskEntity == null) {
            throw new IllegalArgumentException("Task with id " + taskId + " not found");
        }

        logger.info("Task entity state: {}", taskEntity.getState());

        // Update the task state
        taskEntity.setState(TaskStateEnum.fromId(stateId));

        // Merge the task entity
        taskDao.merge(taskEntity);

        return createDetailedTask(taskId);
    }
}
