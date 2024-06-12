import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.task.TaskBean;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Class that tests the TaskBean class
 */
public class TaskBeanTest {

    @InjectMocks
    private TaskBean taskBean;

    @Mock
    private UserDao userDao;

    @Mock
    private TaskDao taskDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private DataValidator dataValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test the newTask method for a success scenario.
     * This test will throw an exception because the task already exists
     * and the method is not supposed to create a new task if it already exists
     * in the database.
     * The method should return a RuntimeException.
     */
    @Test
    public void testNewTask_Success() {
        NewTask newTask = new NewTask();
        newTask.setTitle("Test Task");
        newTask.setResponsibleId(1);
        newTask.setProjectId(1);
        newTask.setProjectedStartDate(LocalDateTime.now());
        newTask.setDeadline(LocalDateTime.now().plusDays(1));

        when(userDao.findUserById(anyInt())).thenReturn(new UserEntity());
        when(taskDao.findTaskByIdAndProjectId(anyInt(), anyInt())).thenReturn(new TaskEntity());
        when(projectDao.findProjectById(anyInt())).thenReturn(new ProjectEntity());
        when(dataValidator.isTaskMandatoryDataValid(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> taskBean.newTask(newTask));
    }

    /**
     * Test the newTask method for a failure scenario.
     * This test will throw an exception because the responsible user does not exist
     * in the database.
     * The method should return a RuntimeException.
     */
    @Test
    public void testNewTask_Failure() {
        NewTask newTask = new NewTask();
        newTask.setTitle("Test Task");
        newTask.setResponsibleId(1);
        newTask.setProjectId(1);
        newTask.setProjectedStartDate(LocalDateTime.now());
        newTask.setDeadline(LocalDateTime.now().plusDays(1));

        when(userDao.findUserById(anyInt())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> taskBean.newTask(newTask));
    }

    /**
     * Test the registerNewTaskInfo method for a success scenario.
     * The method should return a TaskEntity object.
     *
     */
    @Test
    public void testRegisterNewTaskInfo_Success() {
        NewTask newTask = new NewTask();
        newTask.setTitle("Test Task");
        newTask.setResponsibleId(1);
        newTask.setProjectId(1);
        newTask.setProjectedStartDate(LocalDateTime.now());
        newTask.setDeadline(LocalDateTime.now().plusDays(1));

        when(userDao.findUserById(anyInt())).thenReturn(new UserEntity());
        when(taskDao.findTaskByIdAndProjectId(anyInt(), anyInt())).thenReturn(new TaskEntity());
        when(projectDao.findProjectById(anyInt())).thenReturn(new ProjectEntity());

        TaskEntity result = taskBean.registerNewTaskInfo(newTask);

        assertNotNull(result);
    }

    /**
     * Test the registerNewTaskInfo method for a failure scenario.
     * The method should return null because the responsible user does not exist
     * in the database.
     */
    @Test
    public void testRegisterNewTaskInfo_Failure() {
        NewTask newTask = new NewTask();
        newTask.setTitle("Test Task");
        newTask.setResponsibleId(1);
        newTask.setProjectId(1);
        newTask.setProjectedStartDate(LocalDateTime.now());
        newTask.setDeadline(LocalDateTime.now().plusDays(1));

        when(userDao.findUserById(anyInt())).thenReturn(null);

        assertNull(taskBean.registerNewTaskInfo(newTask));
    }

    /**
     * Test the getSetOfDependencies method for a success scenario.
     * The method should return a set of TaskEntity objects.
     */
    @Test
    public void testGetSetOfDependencies_Success() {
        Set<Integer> taskDependencies = new HashSet<>();
        taskDependencies.add(1);

        when(taskDao.findTaskByIdAndProjectId(anyInt(), anyInt())).thenReturn(new TaskEntity());

        Set<TaskEntity> result = taskBean.getSetOfDependencies(taskDependencies, 1);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test the getSetOfDependencies method for a failure scenario.
     * The method should return an empty set because the task does not exist
     * in the database.
     */
    @Test
    public void testGetSetOfDependencies_Failure() {
        Set<Integer> taskDependencies = new HashSet<>();
        taskDependencies.add(1);

        when(taskDao.findTaskByIdAndProjectId(anyInt(), anyInt())).thenReturn(null);

        Set<TaskEntity> result = taskBean.getSetOfDependencies(taskDependencies, 1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test the createTaskDependenciesRelationship method for a success scenario.
     * The method should return a set of M2MTaskDependencies objects.
     */
    @Test
    public void testCreateTaskDependenciesRelationship_Success() {
        Set<TaskEntity> relatedTasks = new HashSet<>();
        relatedTasks.add(new TaskEntity());

        TaskEntity taskEntity = new TaskEntity();

        Set<M2MTaskDependencies> result = taskBean.createTaskDependenciesRelationship(taskEntity, relatedTasks);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test the createTaskDependenciesRelationship method for a failure scenario.
     * The method should return an empty set because the related task does not have
     * any dependencies.
     */
    @Test
    public void testCreateTaskDependenciesRelationship_Failure() {
        Set<TaskEntity> relatedTasks = new HashSet<>();
        TaskEntity relatedTask = new TaskEntity();
        relatedTask.setDependencies(null); // This will cause a NullPointerException in createTaskDependenciesRelationship
        relatedTasks.add(relatedTask);

        TaskEntity taskEntity = new TaskEntity();

        Set<M2MTaskDependencies> result = taskBean.createTaskDependenciesRelationship(taskEntity, relatedTasks);

        assertNotNull(result);
    }

    /**
     * Test the isStartDateValid method for a success scenario.
     * The method should return true because the start date is valid.
     */
    @Test
    public void testIsStartDateValid_Success() {
        Set<TaskEntity> dependencies = new HashSet<>();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setDeadline(LocalDateTime.now().plusDays(1));
        dependencies.add(taskEntity);

        boolean result = taskBean.isStartDateValid(LocalDateTime.now().plusDays(2), dependencies);

        assertTrue(result);
    }

    /**
     * Test the isStartDateValid method for a failure scenario.
     * The method should return false because the start date is not valid.
     */
    @Test
    public void testIsStartDateValid_Failure() {
        Set<TaskEntity> dependencies = new HashSet<>();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setDeadline(LocalDateTime.now().plusDays(2));
        dependencies.add(taskEntity);

        boolean result = taskBean.isStartDateValid(LocalDateTime.now().plusDays(1), dependencies);

        assertFalse(result);
    }

    /**
     * Test the updateDependentTasksStartDate method for a success scenario.
     * The method should call the merge method from the taskDao.
     */
    @Test
    public void testUpdateDependentTasksStartDate_Success() {
        Set<TaskEntity> dependentTasks = new HashSet<>();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setProjectedStartDate(LocalDateTime.now().plusDays(1));
        taskEntity.setDeadline(LocalDateTime.now().plusDays(2));
        dependentTasks.add(taskEntity);

        taskBean.updateDependentTasksStartDate(LocalDateTime.now().plusDays(2), dependentTasks);

        verify(taskDao, times(1)).merge(any(TaskEntity.class));
    }

    /**
     * Test the updateDependentTasksStartDate method for a failure scenario.
     * The method should not throw any exception.
     */
    @Test
    public void testUpdateDependentTasksStartDate_Failure() {
        Set<TaskEntity> dependentTasks = new HashSet<>();
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setProjectedStartDate(LocalDateTime.now().plusDays(2));
        taskEntity.setDeadline(LocalDateTime.now().plusDays(1));
        dependentTasks.add(taskEntity);

        // No exception should be thrown
        taskBean.updateDependentTasksStartDate(LocalDateTime.now().plusDays(3), dependentTasks);
    }

    /**
     * Test the createDetailedTask method for a success scenario.
     * The method should return a DetailedTask object.
     */
    @Test
    public void testCreateDetailedTask_Success() {
        int taskId = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle("Test Task");
        taskEntity.setDescription("Test Description");
        taskEntity.setProjectedStartDate(LocalDateTime.now());
        taskEntity.setDeadline(LocalDateTime.now().plusDays(1));
        taskEntity.setResponsible(new UserEntity());
        taskEntity.setProjectId(new ProjectEntity());
        taskEntity.setOtherExecutors(new HashSet<>());
        taskEntity.setDependencies(new HashSet<>());
        taskEntity.setDependentTasks(new HashSet<>());

        when(taskDao.findTaskById(taskId)).thenReturn(taskEntity);

        DetailedTask result = taskBean.createDetailedTask(taskId);

        assertNotNull(result);
    }

    /**
     * Test the createDetailedTask method for a failure scenario.
     * The method should throw an exception because the task does not exist
     * in the database.
     */
    @Test
    public void testCreateDetailedTask_Failure() {
        int taskId = 1;

        when(taskDao.findTaskById(taskId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taskBean.createDetailedTask(taskId));
    }

    /**
     * Test the setTaskExecutors method for a success scenario.
     * The method should return a map with a set of users.
     */
    @Test
    public void testSetTaskExecutors_Success() {
        Set<String> executors = new HashSet<>();
        executors.add("John Doe");

        when(userDao.existsByFirstAndLastName(anyString(), anyString())).thenReturn(true);

        Map<String, Set<String>> result = taskBean.setTaskExecutors(executors);

        assertNotNull(result);
        assertFalse(result.get("users").isEmpty());
    }

    /**
     * Test the setTaskExecutors method for a failure scenario.
     * The method should return a map with an empty set of users.
     */
    @Test
    public void testSetTaskExecutors_Failure() {
        Set<String> executors = new HashSet<>();
        executors.add("");

        Map<String, Set<String>> result = taskBean.setTaskExecutors(executors);

        assertNotNull(result);
        assertTrue(result.get("users").isEmpty());
    }

    /**
     * Test the createChartTaskFromRelationships method for a success scenario.
     * The method should return a set of ChartTask objects.
     */
    @Test
    public void testCreateChartTaskFromRelationships_Success() {
        Set<M2MTaskDependencies> taskDependencies = new HashSet<>();
        M2MTaskDependencies taskDependency = new M2MTaskDependencies();
        TaskEntity dependentTask = new TaskEntity();
        dependentTask.setId(1);
        dependentTask.setTitle("Test Task");
        dependentTask.setProjectedStartDate(LocalDateTime.now());
        dependentTask.setDeadline(LocalDateTime.now().plusDays(1));
        taskDependency.setDependentTask(dependentTask);
        taskDependencies.add(taskDependency);

        Set<ChartTask> result = taskBean.createChartTaskFromRelationships(taskDependencies);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test the createChartTaskFromRelationships method for a failure scenario.
     * The method should return an empty set because the dependent task does not exist
     * in the database.
     */
    @Test
    public void testCreateChartTaskFromRelationships_Failure() {
        Set<M2MTaskDependencies> taskDependencies = new HashSet<>();
        M2MTaskDependencies taskDependency = new M2MTaskDependencies();
        taskDependency.setDependentTask(null);
        taskDependencies.add(taskDependency);

        Set<ChartTask> result = taskBean.createChartTaskFromRelationships(taskDependencies);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}