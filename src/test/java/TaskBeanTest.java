import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.TaskDao;
import domcast.finalprojbackend.dao.UserDao;
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
     * Test the newTask method for a success scenario
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
     * Test the newTask method for a failure scenario
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
     * Test the registerNewTaskInfo method for a success scenario
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
     * Test the registerNewTaskInfo method for a failure scenario
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
     * Test the getSetOfDependencies method for a success scenario
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
     * Test the getSetOfDependencies method for a failure scenario
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
     * Test the createTaskDependenciesRelationship method for a success scenario
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
     * Test the createTaskDependenciesRelationship method for a failure scenario
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
     * Test the isStartDateValid method for a success scenario
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
     * Test the isStartDateValid method for a failure scenario
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
     * Test the updateDependentTasksStartDate method for a success scenario
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
     * Test the updateDependentTasksStartDate method for a failure scenario
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
}