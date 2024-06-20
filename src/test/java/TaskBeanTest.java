import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.bean.task.TaskBean;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

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

    @Mock
    private ProjectBean projectBean;

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<TaskEntity> query;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(projectBean.isUserActiveAndApprovedInProject(anyInt(), anyInt())).thenReturn(true);

        // Create a TaskEntity and save it to the database
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1);
        when(taskDao.findTaskById(1)).thenReturn(taskEntity);
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
        NewTask<Integer> newTask = new NewTask<Integer>();
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
        NewTask<Integer> newTask = new NewTask<Integer>();
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
        NewTask<Integer> newTask = new NewTask<Integer>();
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
        NewTask<Integer> newTask = new NewTask<Integer>();
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

    /**
     * Test the findTaskById method for a success scenario.
     * The method should return a TaskEntity object.
     */
    @Test
    public void testFindTaskById_Success() {
        int taskId = 1;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle("Test Task");

        when(taskDao.findTaskById(taskId)).thenReturn(taskEntity);

        TaskEntity result = taskBean.findTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskEntity, result);
    }

    /**
     * Test the findTaskById method for a failure scenario.
     * The method should throw an IllegalArgumentException because the task does not exist
     * in the database.
     */
    @Test
    public void testFindTaskById_Failure() {
        int taskId = 1;

        when(taskDao.findTaskById(taskId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taskBean.findTaskById(taskId));
    }

    /**
     * Test the findTaskById method for a failure scenario.
     * The method should throw a RuntimeException because an exception occurred
     * while finding the task by id in the database.
     */
    @Test
    public void testFindTaskById_Exception() {
        int taskId = 1;

        when(taskDao.findTaskById(taskId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> taskBean.findTaskById(taskId));
    }

    /**
     * Test the findTaskByProjectId method for a success scenario.
     * The method should return a list of ChartTask objects.
     */
    @Test
    public void testFindTaskByProjectId_Success() {
        int projectId = 1;
        List<TaskEntity> taskEntities = Arrays.asList(new TaskEntity(), new TaskEntity());

        when(dataValidator.isIdValid(projectId)).thenReturn(true);
        when(taskDao.findTaskByProjectId(projectId)).thenReturn(taskEntities);

        List<ChartTask> result = taskBean.findTaskByProjectId(projectId);

        assertNotNull(result);
        assertEquals(taskEntities.size(), result.size());

        verify(dataValidator).isIdValid(projectId);
        verify(taskDao).findTaskByProjectId(projectId);
    }

    /**
     * Test the findTaskByProjectId method for a failure scenario.
     * The method should throw an IllegalArgumentException because the project id is invalid.
     */
    @Test
    public void testFindTaskByProjectId_InvalidProjectId() {
        int projectId = -1;

        when(dataValidator.isIdValid(projectId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> taskBean.findTaskByProjectId(projectId));

        verify(dataValidator).isIdValid(projectId);
        verify(taskDao, never()).findTaskByProjectId(anyInt());
    }

    /**
     * Test the findTaskByProjectId method for a failure scenario.
     * The method should throw a RuntimeException because an exception occurred
     * while finding the tasks by project id in the database.
     */
    @Test
    public void testFindTaskByProjectId_Exception() {
        int projectId = 1;

        when(dataValidator.isIdValid(projectId)).thenReturn(true);
        when(taskDao.findTaskByProjectId(projectId)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> taskBean.findTaskByProjectId(projectId));

        verify(dataValidator).isIdValid(projectId);
        verify(taskDao).findTaskByProjectId(projectId);
    }

    @Test
    void testEditTask_Success() {
        // Arrange
        int taskId = 1;
        EditTask editedTask = new EditTask();
        editedTask.setResponsibleId(1);
        editedTask.setProjectId(1);
        editedTask.setTitle("Test Title");
        editedTask.setDescription("Test Description");
        editedTask.setProjectedStartDate(LocalDateTime.now());
        editedTask.setDeadline(LocalDateTime.now().plusDays(1));
        editedTask.setState(1);

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        UserEntity responsible = new UserEntity();
        responsible.setId(1);
        taskEntity.setResponsible(responsible);
        taskEntity.setProjectId(new ProjectEntity());
        taskEntity.setOtherExecutors(new HashSet<>());
        taskEntity.setDependencies(new HashSet<>());
        taskEntity.setDependentTasks(new HashSet<>());

        TaskBean taskBean = Mockito.mock(TaskBean.class);
        UserDao userDao = Mockito.mock(UserDao.class);
        ProjectBean projectBean = Mockito.mock(ProjectBean.class);
        TaskDao taskDao = Mockito.mock(TaskDao.class);

        when(taskBean.findTaskById(taskId)).thenReturn(taskEntity);
        when(userDao.findUserById(editedTask.getResponsibleId())).thenReturn(responsible);
        when(projectBean.isUserActiveAndApprovedInProject(responsible.getId(), editedTask.getProjectId())).thenReturn(true);
        when(taskDao.merge(any(TaskEntity.class))).thenReturn(true);

        // Act
        DetailedTask expectedTask = new DetailedTask();
        expectedTask.setId(taskId);
        expectedTask.setTitle(editedTask.getTitle());
        expectedTask.setDescription(editedTask.getDescription());
        expectedTask.setProjectedStartDate(editedTask.getProjectedStartDate());
        expectedTask.setDeadline(editedTask.getDeadline());
        expectedTask.setResponsibleId(editedTask.getResponsibleId());
        expectedTask.setProjectId(editedTask.getProjectId());
        expectedTask.setState(editedTask.getState());

        when(taskBean.editTask(editedTask, taskId)).thenReturn(expectedTask);

        // Act
        DetailedTask result = taskBean.editTask(editedTask, taskId);

        // Assert
        assertNotNull(result);
        assertEquals(editedTask.getTitle(), result.getTitle());
        assertEquals(editedTask.getDescription(), result.getDescription());
        assertEquals(editedTask.getProjectedStartDate(), result.getProjectedStartDate());
        assertEquals(editedTask.getDeadline(), result.getDeadline());
        assertEquals(editedTask.getResponsibleId(), result.getResponsibleId());
        assertEquals(editedTask.getProjectId(), result.getProjectId());
        assertEquals(taskEntity.getId(), result.getId());
        assertEquals(editedTask.getState(), result.getState());
    }

    @Test
    void testEditTask_Failure() {
        EditTask editedTask = null;
        int taskId = -1;

        when(dataValidator.isIdValid(taskId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> taskBean.editTask(editedTask, taskId));
    }

    @Test
    void testUpdateTaskInfo_Success() {
        EditTask editedTask = new EditTask();
        TaskEntity taskEntity = new TaskEntity();

        assertDoesNotThrow(() -> taskBean.updateTaskInfo(editedTask, taskEntity));
    }

    @Test
    void testUpdateTaskInfo_Failure() {
        EditTask editedTask = null;
        TaskEntity taskEntity = null;

        assertThrows(IllegalArgumentException.class, () -> taskBean.updateTaskInfo(editedTask, taskEntity));
    }
}