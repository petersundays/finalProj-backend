package domcast.finalprojbackend.bean.task;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.TaskDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.taskDto.DetailedTask;
import domcast.finalprojbackend.dto.taskDto.EditTask;
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
import java.util.*;

/**
 * Bean class for the task entity.
 *
 * @author José Castro
 * @author Pedro Domingos
 */
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

    /**
     * Creates a new task
     * @param newTask the new task
     * @return the chart task
     */
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

    /**
     * Registers the new task info
     * @param newTask the new task
     * @return the task entity
     */
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

        if (!projectBean.isUserActiveAndApprovedInProject(newTask.getResponsibleId(), newTask.getProjectId())) {
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

        boolean isStartDateValid = dataValidator.isStartDateValid(newTask.getProjectedStartDate(), dependencies);

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

        TaskEntity presentationTask;
        M2MTaskDependencies presentationTaskRelationship;

        try {
            presentationTask = taskDao.findPresentationTaskInProject(newTask.getProjectId());
        } catch (Exception e) {
            logger.error("Error finding presentation task in project", e);
            return null;
        }

        try {
            presentationTaskRelationship = relatePresentationTask(presentationTask, taskEntity);
        } catch (Exception e) {
            logger.error("Error relating presentation task", e);
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
        taskEntity.addDependentTask(presentationTaskRelationship);
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

    /**
     * Updates the task's state
     * @param taskId the id of the task
     * @param stateId the id of the state
     * @return the detailed task
     */
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

    /**
     * Finds tasks by project id
     * @param projectId the id of the project
     * @return the list of chart tasks
     */
    public List<ChartTask> findTaskByProjectId (int projectId) {
        logger.info("Finding tasks by project id");

        // Check if the project id is valid
        if (!dataValidator.isIdValid(projectId)) {
            logger.error("The project id is invalid");
            throw new IllegalArgumentException("Invalid project id: " + projectId);
        }

        // Find the tasks by the project id
        List<TaskEntity> taskEntities;
        try {
            taskEntities = taskDao.findTaskByProjectId(projectId);
        } catch (Exception e) {
            logger.error("Error finding tasks by project id", e);
            throw new RuntimeException("Error finding tasks by project id: " + e.getMessage(), e);
        }

        // Check if the task entities are null or empty
        if (taskEntities == null || taskEntities.isEmpty()) {
            logger.info("No tasks found for project id: {}", projectId);
            return new ArrayList<>();
        }

        List<ChartTask> chartTasks = new ArrayList<>();

        logger.info("Task entities found: {}", taskEntities.size());

        // Convert the task entities to chart tasks
        try {
            for (TaskEntity taskEntity : taskEntities) {
                chartTasks.add(entityToChartTask(taskEntity));
            }
        } catch (Exception e) {
            logger.error("Error converting task entity to chart task", e);
            throw new RuntimeException("Error converting task entity to chart task: " + e.getMessage(), e);
        }

        return chartTasks;
    }

    /**
     * Converts a task entity to a chart task
     * @param taskEntity the task entity
     * @return the chart task
     */
    public ChartTask entityToChartTask (TaskEntity taskEntity) {

        logger.info("Converting task entity to chart task");

        if (taskEntity == null) {
            logger.error("The task entity is null");
            return null;
        }

        if (!dataValidator.isChartTaskInfoValid(taskEntity)) {
            logger.error("The task entity does not have all the necessary information");
            return null;
        }

        logger.info("Task entity with id {} is valid", taskEntity.getId());

        if (!dataValidator.isIdValid(taskEntity.getId())) {
            logger.error("The task entity id is invalid");
            return null;
        }
        return new ChartTask(taskEntity.getId(),
                taskEntity.getTitle(),
                taskEntity.getState().getId(),
                taskEntity.getProjectedStartDate(),
                taskEntity.getDeadline());
    }

    /**
     * Updates the task's information
     * @param editedTask the task with the information to be updated
     * @param taskId the id of the task to be updated
     * @return the detailed task with the updated information
     */
    public DetailedTask editTask (EditTask editedTask, int taskId) {
        logger.info("Editing task");

        if (!dataValidator.isIdValid(taskId)) {
            throw new IllegalArgumentException("Invalid task id: " + taskId);
        }

        if (editedTask == null) {
            throw new IllegalArgumentException("The input for the new task is null");
        }

        TaskEntity taskEntity;

        try {
            taskEntity = findTaskById(taskId);
            if (taskEntity == null) {
                throw new IllegalArgumentException("Task with id " + taskId + " not found");
            }
        } catch (Exception e) {
            logger.error("Error editing task", e);
            throw new RuntimeException("Error editing task: " + e.getMessage(), e);
        }

        try {
            taskEntity = updateTaskInfo(editedTask, taskEntity);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating task info", e);
            throw new IllegalArgumentException("Error updating task info: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            logger.error("Error updating task info", e);
            throw new RuntimeException("Error updating task info: " + e.getMessage(), e);
        }

        Map<String, Set<String>> executors = setTaskExecutors(taskEntity.getOtherExecutors());

        Set<String> users = executors.get("users");
        Set<String> externalExecutors = executors.get("externalExecutors");

        try {
            taskDao.merge(taskEntity);
        } catch (Exception e) {
            logger.error("Error merging task", e);
            throw new RuntimeException("Error merging task: " + e.getMessage(), e);
        }

        return new DetailedTask(taskEntity.getTitle(),
                taskEntity.getDescription(),
                taskEntity.getProjectedStartDate(),
                taskEntity.getDeadline(),
                taskEntity.getResponsible().getId(),
                taskEntity.getProjectId().getId(),
                users,
                createChartTaskFromRelationships(taskEntity.getDependencies()),
                createChartTaskFromRelationships(taskEntity.getDependentTasks()),
                taskEntity.getId(),
                taskEntity.getState().getId(),
                externalExecutors);
    }

    /**
     * Checks the information to be updated and updates the task entity
     * @param editedTask the task with the information to be updated
     * @param taskEntity the task entity to be updated
     * @return the updated task entity
     */
    public TaskEntity updateTaskInfo (EditTask editedTask, TaskEntity taskEntity) {
        logger.info("Updating task info");

        if (editedTask == null) {
            logger.error("The new task is null when updating task info");
            throw new IllegalArgumentException("The new task is null when updating task info");
        }

        if (taskEntity == null) {
            logger.error("The task entity is null when updating task info");
            throw new IllegalArgumentException("The task entity is null when updating task info");
        }

        try {
            if (editedTask.getTitle() != null && !editedTask.getTitle().isBlank()) {
                taskEntity.setTitle(editedTask.getTitle());
            }

            if (editedTask.getDescription() != null && !editedTask.getDescription().isBlank()) {
                taskEntity.setDescription(editedTask.getDescription());
            }

            if (editedTask.getProjectedStartDate() != null) {
                taskEntity.setProjectedStartDate(editedTask.getProjectedStartDate());
            }

            if (editedTask.getDeadline() != null) {
                TaskEntity presentationTask = taskDao.findPresentationTaskInProject(taskEntity.getProjectId().getId());
                if (presentationTask == null) {
                    throw new IllegalArgumentException("Presentation task of the project with id " + taskEntity.getProjectId().getId() + " not found");
                }
                if (dataValidator.isDeadlineValid(editedTask.getDeadline(), editedTask.getProjectedStartDate(), presentationTask.getProjectedStartDate())) {
                    throw new IllegalArgumentException("The deadline is not valid");
                }
                taskEntity.setDeadline(editedTask.getDeadline());
            }

            if (editedTask.getResponsibleId() > 0) {
                UserEntity responsible = userDao.findUserById(editedTask.getResponsibleId());
                if (responsible != null && projectBean.isUserActiveAndApprovedInProject(responsible.getId(), editedTask.getProjectId())) {
                    taskEntity.setResponsible(responsible);
                }
            }

            if (editedTask.getOtherExecutors() != null && !editedTask.getOtherExecutors().isEmpty()) {
                taskEntity.setOtherExecutors(editedTask.getOtherExecutors());
            }

            if (editedTask.getDependencies() != null && !editedTask.getDependencies().isEmpty()) {
                Set<TaskEntity> dependencies = getSetOfDependencies(editedTask.getDependencies(), taskEntity.getProjectId().getId());
                if (dependencies != null) {
                    Set<M2MTaskDependencies> dependenciesRelationship = createTaskDependenciesRelationship(taskEntity, dependencies);
                    taskEntity.setDependencies(dependenciesRelationship);
                }
            }

            if (editedTask.getDependentTasks() != null && !editedTask.getDependentTasks().isEmpty()) {
                Set<TaskEntity> dependentTasks = getSetOfDependencies(editedTask.getDependentTasks(), taskEntity.getProjectId().getId());
                if (dependentTasks != null) {
                    Set<M2MTaskDependencies> dependentRelationship = createTaskDependenciesRelationship(taskEntity, dependentTasks);
                    taskEntity.setDependentTasks(dependentRelationship);
                }
            }

            if (editedTask.getState() > 0) {
                taskEntity.setState(TaskStateEnum.fromId(editedTask.getState()));
            }

            return taskEntity;
        } catch (Exception e) {
            logger.error("Error updating task info", e);
            throw new RuntimeException("Error updating task info: " + e.getMessage(), e);
        }
    }

    public boolean presentationTask (int responsibleId, ProjectEntity project) {
        logger.info("Creating presentation task");

        if (!dataValidator.isIdValid(responsibleId)) {
            throw new IllegalArgumentException("Invalid responsible id");
        }

        if (project == null) {
            throw new IllegalArgumentException("The project is null");
        }

        TaskEntity taskEntity = new TaskEntity();

        UserEntity responsible;

        try {
            responsible = userDao.findUserById(responsibleId);
        } catch (Exception e) {
            logger.error("Error finding user by id", e);
            throw new RuntimeException("Error finding user by id: " + e.getMessage(), e);
        }

        if (responsible == null) {
            throw new IllegalArgumentException("User with id " + responsibleId + " not found");
        }

        taskEntity.setTitle("Presentation");
        taskEntity.setDescription("Presentation of the project");
        taskEntity.setProjectedStartDate(project.getDeadline().minusDays(1L));
        taskEntity.setDeadline(project.getDeadline());
        taskEntity.setState(TaskStateEnum.PLANNED);
        taskEntity.setResponsible(responsible);
        taskEntity.setProjectId(project);

        try {
            taskDao.persist(taskEntity);
        } catch (Exception e) {
            logger.error("Error persisting presentation task", e);
            throw new RuntimeException("Error persisting presentation task: " + e.getMessage(), e);
        }

        project.addTask(taskEntity);

        return true;
    }
    
    public M2MTaskDependencies relatePresentationTask (TaskEntity presentationTask, TaskEntity task) {
        logger.info("Relating presentation task");

        if (presentationTask == null || task == null) {
            throw new IllegalArgumentException("The presentation task or the task is null");
        }

        M2MTaskDependencies taskDependency = new M2MTaskDependencies();
        taskDependency.setTask(task);
        taskDependency.setDependentTask(presentationTask);

        return taskDependency;
    }
}
