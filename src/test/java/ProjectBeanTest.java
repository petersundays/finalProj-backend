import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domcast.finalprojbackend.bean.*;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.*;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.projectDto.DetailedProject;
import domcast.finalprojbackend.dto.projectDto.EditProject;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
import domcast.finalprojbackend.dto.projectDto.ProjectPreview;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.userDto.ProjectTeam;
import domcast.finalprojbackend.dto.userDto.ProjectUser;
import domcast.finalprojbackend.entity.LabEntity;
import domcast.finalprojbackend.entity.M2MProjectUser;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.ProjectStateEnum;
import domcast.finalprojbackend.enums.ProjectUserEnum;
import domcast.finalprojbackend.service.ObjectMapperContextResolver;
import jakarta.persistence.PersistenceException;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProjectBeanTest {

    @InjectMocks
    private ProjectBean projectBean;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private ProjectEntity projectEntity;

    @Mock
    private ObjectMapperContextResolver objectMapperContextResolver;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MultipartFormDataInput input;

    @Mock
    private M2MComponentProjectDao m2MComponentProjectDao;

    @Mock
    private TaskBean taskBean;

    @Mock
    private SkillBean skillBean;

    @Mock
    private ComponentResourceBean componentResourceBean;

    @Mock
    private DataValidator dataValidator;

    @Mock
    private LabDao labDao;

    @Mock
    private KeywordBean keywordBean;

    @Mock
    private UserDao userDao;

    @Mock
    private M2MProjectUserDao m2MProjectUserDao;

    @Mock
    private UserBean userBean;

    @Mock
    private SystemBean systemBean;

    @Mock
    private InputPart part;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        LabEntity labEntity = new LabEntity();
        labEntity.setCity(LabEnum.COIMBRA);
        when(labDao.findLabByCity(anyString())).thenReturn(labEntity);
        when(keywordBean.createAndGetKeywords(anySet())).thenReturn(new HashSet<>());
        when(userDao.findUserById(anyInt())).thenReturn(new UserEntity());
        when(m2MProjectUserDao.findMainManagerInProject(anyInt())).thenReturn(new M2MProjectUser());
        when(userBean.projectUserToProjectUserDto(any(M2MProjectUser.class))).thenReturn(new ProjectUser());
    }

    /**
     * Test for isUserActiveAndApprovedInProject method when the user is active and approved in the project.
     * The test is expected to pass.
     */
    @Test
    public void testIsUserActiveAndApprovedInProject_Success() {
        int userId = 1;
        int projectId = 1;

        when(projectDao.isUserActiveAndApprovedInProject(userId, projectId)).thenReturn(true);

        boolean result = projectBean.isUserActiveAndApprovedInProject(userId, projectId);

        assertTrue(result);
        verify(projectDao, times(1)).isUserActiveAndApprovedInProject(userId, projectId);
    }

    /**
     * Test for isUserActiveAndApprovedInProject method when the user is not active and approved in the project.
     * The test is expected to throw a RuntimeException.
     */
    @Test
    public void testIsUserActiveAndApprovedInProject_Failure() {
        int userId = 1;
        int projectId = 1;

        when(projectDao.isUserActiveAndApprovedInProject(userId, projectId)).thenThrow(PersistenceException.class);

        assertThrows(RuntimeException.class, () -> projectBean.isUserActiveAndApprovedInProject(userId, projectId));
        verify(projectDao, times(1)).isUserActiveAndApprovedInProject(userId, projectId);
    }

    /**
     * Test for newProject method when the project is successfully created.
     * The test is expected to pass.
     */
    @Test
    public void testNewProject_Success() {
        NewProjectDto newProjectDto = new NewProjectDto();
        Set<String> keywords = new HashSet<>();
        keywords.add("keyword1");
        newProjectDto.setKeywords(keywords);
        newProjectDto.setLabId(1);
        ProjectTeam projectTeam = new ProjectTeam();
        int responsibleUserId = 1;
        Set<DetailedCR> cRDtos = new HashSet<>();
        ArrayList<SkillDto> newSkills = new ArrayList<>();

        when(dataValidator.isIdValid(responsibleUserId)).thenReturn(true);
        when(dataValidator.isProjectMandatoryDataValid(newProjectDto)).thenReturn(true);
        when(projectDao.persist(any(ProjectEntity.class))).thenReturn(true);
        when(taskBean.presentationTask(anyInt(), any(ProjectEntity.class))).thenReturn(true);

        DetailedProject result = projectBean.newProject(newProjectDto, projectTeam, responsibleUserId, cRDtos, newSkills);

        assertNotNull(result);
        verify(projectDao, times(1)).persist(any(ProjectEntity.class));
    }

    /**
     * Test for newProject method when the project creation fails.
     * The test is expected to throw a RuntimeException.
     */
    @Test
    public void testNewProject_Failure() {
        NewProjectDto newProjectDto = mock(NewProjectDto.class);
        Set<String> keywords = new HashSet<>();
        keywords.add("keyword1");
        when(newProjectDto.getLabId()).thenReturn(1);
        when(newProjectDto.getKeywords()).thenReturn(keywords);
        ProjectTeam projectTeam = new ProjectTeam();
        int responsibleUserId = 1;
        Set<DetailedCR> cRDtos = new HashSet<>();
        ArrayList<SkillDto> newSkills = new ArrayList<>();

        when(dataValidator.isIdValid(responsibleUserId)).thenReturn(true);
        when(dataValidator.isProjectMandatoryDataValid(newProjectDto)).thenReturn(true);
        when(projectDao.persist(any(ProjectEntity.class))).thenThrow(PersistenceException.class);

        assertThrows(RuntimeException.class, () -> projectBean.newProject(newProjectDto, projectTeam, responsibleUserId, cRDtos, newSkills));
        verify(projectDao, times(1)).persist(any(ProjectEntity.class));
    }

    /**
     * Test for updateProject method when the project is successfully updated.
     * The test is expected to pass.
     */
    @Test
    public void testRegisterProjectInfo_Success() {
        // Arrange
        NewProjectDto newProjectDto = mock(NewProjectDto.class);
        ProjectEntity projectEntity = mock(ProjectEntity.class);
        ProjectTeam projectTeam = mock(ProjectTeam.class);
        Set<Integer> newSkills = new HashSet<>();
        Map<Integer, Integer> newCRs = new HashMap<>();

        when(newProjectDto.getLabId()).thenReturn(1);
        when(newProjectDto.getKeywords()).thenReturn(new HashSet<>(Arrays.asList("keyword1")));
        when(newProjectDto.getName()).thenReturn("Test Project");
        when(newProjectDto.getDescription()).thenReturn("Test Description");
        when(newProjectDto.getProjectedStartDate()).thenReturn(LocalDateTime.now());
        when(newProjectDto.getDeadline()).thenReturn(LocalDateTime.now());
        when(newProjectDto.getExistentSkills()).thenReturn(new HashSet<>());
        when(newProjectDto.getExistentResources()).thenReturn(new HashMap<>());
        when(userDao.findUserById(1)).thenReturn(new UserEntity());
        when(dataValidator.isIdValid(1)).thenReturn(true);

        // Act
        ProjectEntity result = projectBean.registerProjectInfo(newProjectDto, projectEntity, projectTeam, 1, newSkills, newCRs);

        // Assert
        assertNotNull(result);
        verify(projectEntity, times(1)).setName(anyString());
        verify(projectEntity, times(1)).setDescription(anyString());
        verify(projectEntity, times(1)).setLab(any(LabEntity.class));
        verify(projectEntity, times(1)).setKeywords(anySet());
        verify(projectEntity, times(1)).setProjectedStartDate(any(java.time.LocalDateTime.class));
        verify(projectEntity, times(1)).setDeadline(any(java.time.LocalDateTime.class));
        verify(projectEntity, times(1)).setProjectUsers(anySet());
        verify(projectEntity, times(1)).setSkills(anySet());
        verify(projectEntity, times(1)).setComponentResources(anySet());
    }

    /**
     * Test for updateProject method when the project update fails.
     * The test is expected to throw a RuntimeException.
     */
    @Test
    public void testRegisterProjectInfo_Failure() {
        // Arrange
        NewProjectDto newProjectDto = mock(NewProjectDto.class);
        ProjectEntity projectEntity = mock(ProjectEntity.class);
        ProjectTeam projectTeam = mock(ProjectTeam.class);
        Set<Integer> newSkills = new HashSet<>();
        Map<Integer, Integer> newCRs = new HashMap<>();

        when(newProjectDto.getLabId()).thenReturn(1);
        when(newProjectDto.getKeywords()).thenReturn(new HashSet<>()); // No keywords should cause failure

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> projectBean.registerProjectInfo(newProjectDto, projectEntity, projectTeam, 1, newSkills, newCRs));
    }

    /**
     * Test for updateProject method when the project is successfully updated.
     * The test is expected to pass.
     */
    @Test
    void createProjectTeam_Success() {
        // Arrange
        Map<Integer, Integer> teamMembers = new HashMap<>();
        teamMembers.put(1, 1);
        ProjectEntity projectEntity = new ProjectEntity();

        when(dataValidator.isIdValid(anyInt())).thenReturn(true);
        when(userDao.findUserById(anyInt())).thenReturn(new UserEntity());
        when(systemBean.getProjectMaxUsers()).thenReturn(10);

        // Act
        Set<M2MProjectUser> result = projectBean.createProjectTeam(teamMembers, projectEntity, 1);

        // Assert
        assertEquals(1, result.size());
    }

    /**
     * Test for updateProject method when the project update fails.
     * The test is expected to throw a RuntimeException.
     */
    @Test
    void createProjectTeam_Failure() {
        // Arrange
        Map<Integer, Integer> teamMembers = new HashMap<>();
        teamMembers.put(1, 1);
        ProjectEntity projectEntity = new ProjectEntity();

        when(dataValidator.isIdValid(anyInt())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> projectBean.createProjectTeam(teamMembers, projectEntity, 1));
    }

    /**
     * Test for entityToDetailedProject method when the conversion is successful.
     * The test is expected to pass.
     */
    @Test
    void entityToDetailedProject_Success() {
        // Arrange
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(1);
        projectEntity.setName("Test Project");
        projectEntity.setDescription("Test Description");
        projectEntity.setProjectedStartDate(LocalDateTime.now());
        projectEntity.setDeadline(LocalDateTime.now());
        LabEntity labEntity = new LabEntity();
        labEntity.setCity(LabEnum.COIMBRA);
        projectEntity.setLab(labEntity);

        M2MProjectUser mainManager = new M2MProjectUser();
        mainManager.setUser(new UserEntity());
        mainManager.setProject(projectEntity);
        mainManager.setRole(ProjectUserEnum.MAIN_MANAGER);
        mainManager.setApproved(true);

        Set<M2MProjectUser> projectTeam = new HashSet<>();
        projectTeam.add(mainManager);

        List<ChartTask> taskList = new ArrayList<>();
        ChartTask task = new ChartTask();
        task.setId(1);
        task.setTitle("Test Task");
        taskList.add(task);

        when(m2MProjectUserDao.findMainManagerInProject(anyInt())).thenReturn(mainManager);
        when(m2MProjectUserDao.findProjectTeam(anyInt())).thenReturn(projectTeam);
        when(taskBean.findTaskByProjectId(anyInt())).thenReturn(taskList);

        // Act
        DetailedProject result = projectBean.entityToDetailedProject(projectEntity);

        // Assert
        assertEquals(projectEntity.getId(), result.getId());
        assertEquals(projectEntity.getName(), result.getName());
        assertEquals(projectEntity.getDescription(), result.getDescription());
        assertEquals(projectEntity.getProjectedStartDate(), result.getProjectedStartDate());
        assertEquals(projectEntity.getDeadline(), result.getDeadline());
        assertEquals(mainManager.getUser().getId(), result.getMainManager().getId());
        assertEquals(taskList.size(), result.getTasks().size());
    }

    /**
     * Test for entityToDetailedProject method when the conversion fails due to a null ProjectEntity.
     * The test is expected to throw an IllegalArgumentException.
     */
    @Test
    void entityToDetailedProject_Failure() {
        // Arrange
        ProjectEntity projectEntity = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> projectBean.entityToDetailedProject(projectEntity));
    }

    /**
     * Test for extractNewProjectDto method when the extraction is successful.
     * The test is expected to pass.
     */
    @Test
    void extractNewProjectDto_Success() throws IOException {
        // Arrange
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        formDataMap.put("project", Collections.singletonList(part));
        when(input.getFormDataMap()).thenReturn(formDataMap);
        when(part.getBodyAsString()).thenReturn("{\"name\":\"Test Project\"}");
        when(objectMapperContextResolver.getContext(null)).thenReturn(objectMapper);
        when(objectMapper.readValue("{\"name\":\"Test Project\"}", NewProjectDto.class)).thenReturn(new NewProjectDto());

        // Act
        NewProjectDto result = projectBean.extractNewProjectDto(input);

        // Assert
        assertNotNull(result);
    }

    /**
     * Test for extractNewProjectDto method when the extraction fails.
     * The test is expected to throw an IOException.
     */
    @Test
    void extractNewProjectDto_Failure() throws IOException {
        // Arrange
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        formDataMap.put("project", Collections.singletonList(part));
        when(input.getFormDataMap()).thenReturn(formDataMap);
        when(part.getBodyAsString()).thenReturn("{\"name\":\"Test Project\"}");
        when(objectMapperContextResolver.getContext(null)).thenReturn(objectMapper);
        when(objectMapper.readValue("{\"name\":\"Test Project\"}", NewProjectDto.class)).thenThrow(new RuntimeException(new IOException()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> projectBean.extractNewProjectDto(input));
    }

    /**
     * Test for convertProjectToJson method when the conversion is successful.
     * The test is expected to pass.
     */
    @Test
    void convertProjectToJson_Success() throws JsonProcessingException {
        // Arrange
        DetailedProject detailedProject = new DetailedProject();
        detailedProject.setName("Test Project");
        when(objectMapperContextResolver.getContext(null)).thenReturn(objectMapper);
        when(objectMapper.writeValueAsString(detailedProject)).thenReturn("{\"name\":\"Test Project\"}");

        // Act
        String result = projectBean.convertProjectToJson(detailedProject);

        // Assert
        assertEquals("{\"name\":\"Test Project\"}", result);
    }

    /**
     * Test for convertProjectToJson method when the conversion fails.
     * The test is expected to throw a JsonProcessingException.
     */
    @Test
    void convertProjectToJson_Failure() throws JsonProcessingException {
        // Arrange
        DetailedProject detailedProject = new DetailedProject();
        detailedProject.setName("Test Project");
        when(objectMapperContextResolver.getContext(null)).thenReturn(objectMapper);
        when(objectMapper.writeValueAsString(detailedProject)).thenThrow(new JsonProcessingException("Error") {});

        // Act & Assert
        assertThrows(JsonProcessingException.class, () -> projectBean.convertProjectToJson(detailedProject));
    }

    /**
     * Test for isUserManagerInProject method when the user is a manager in the project.
     * The test is expected to pass.
     */
    @Test
    public void testIsUserPartOfProjectAndActive_Success() {
        int userId = 1;
        int projectId = 1;

        when(projectDao.isUserPartOfProjectAndActive(userId, projectId)).thenReturn(true);

        boolean result = projectBean.isUserPartOfProjectAndActive(userId, projectId);

        assertTrue(result);
        verify(projectDao, times(1)).isUserPartOfProjectAndActive(userId, projectId);
    }

    /**
     * Test for isUserPartOfProjectAndActive method when the user is not part of the project.
     * The test is expected to throw a RuntimeException.
     */
    @Test
    public void testIsUserPartOfProjectAndActive_Failure() {
        int userId = 1;
        int projectId = 1;

        when(projectDao.isUserPartOfProjectAndActive(userId, projectId)).thenThrow(PersistenceException.class);

        assertThrows(RuntimeException.class, () -> projectBean.isUserPartOfProjectAndActive(userId, projectId));
        verify(projectDao, times(1)).isUserPartOfProjectAndActive(userId, projectId);
    }

    /**
     * Test for extractEditProjectDto method when the extraction is successful.
     * The test is expected to pass.
     */
    @Test
    public void testExtractEditProjectDto_Success() throws IOException {
        // Arrange
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        formDataMap.put("project", Collections.singletonList(part));
        when(input.getFormDataMap()).thenReturn(formDataMap);
        when(part.getBodyAsString()).thenReturn("{\"name\":\"Test Project\"}");
        when(objectMapperContextResolver.getContext(null)).thenReturn(objectMapper);
        when(objectMapper.readValue("{\"name\":\"Test Project\"}", EditProject.class)).thenReturn(new EditProject());

        // Act
        EditProject result = projectBean.extractEditProjectDto(input);

        // Assert
        assertNotNull(result);
    }

    /**
     * Test for extractEditProjectDto method when the extraction fails.
     * The test is expected to throw an IOException.
     */
    @Test
    public void testExtractEditProjectDto_Failure() throws IOException {
        // Arrange
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        formDataMap.put("project", Collections.singletonList(part));
        when(input.getFormDataMap()).thenReturn(formDataMap);
        when(part.getBodyAsString()).thenReturn("{\"name\":\"Test Project\"}");
        when(objectMapperContextResolver.getContext(null)).thenReturn(objectMapper);
        when(objectMapper.readValue("{\"name\":\"Test Project\"}", EditProject.class)).thenThrow(new RuntimeException(new IOException()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> projectBean.extractEditProjectDto(input));
    }

    /**
     * Test for editStateByManager method when the project id is invalid.
     * The test is expected to throw an IllegalArgumentException.
     */
    @Test
    public void testEditStateByManager_InvalidProjectId() {
        int projectId = 1;
        int newState = 2;

        when(dataValidator.isIdValid(projectId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> projectBean.editStateByManager(projectId, newState));
    }

    /**
     * Test for editStateByManager method when the state is invalid.
     * The test is expected to throw an IllegalArgumentException.
     */
    @Test
    public void testEditStateByManager_InvalidState() {
        int projectId = 1;
        int newState = -1;

        when(dataValidator.isIdValid(projectId)).thenReturn(true);

        boolean isValid = ProjectStateEnum.isValidId(newState);
        assertFalse(isValid, "State should be invalid");

        // If the state is invalid, the method should throw an IllegalArgumentException
        if (!isValid) {
            assertThrows(IllegalArgumentException.class, () -> projectBean.editStateByManager(projectId, newState));
        }
    }

    @Test
    public void testEditStateByManager_PersistenceException() {
        int projectId = 1;
        int newState = 2;
        assertTrue(ProjectStateEnum.isValidId(newState));

        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setState(ProjectStateEnum.fromId(1));
        when(projectDao.findProjectById(projectId)).thenReturn(projectEntity);

        // Mock the merge method
        when(projectDao.merge(any(ProjectEntity.class))).thenThrow(PersistenceException.class);

        assertThrows(RuntimeException.class, () -> projectBean.editStateByManager(projectId, newState));
    }

    /**
     * Test for editStateByManager method when the state is successfully updated.
     * The test is expected to pass.
     */
    @Test
    public void testIsUserManagerInProject_Success() {
        // Arrange
        int userId = 1;
        int projectId = 1;

        when(dataValidator.isIdValid(userId)).thenReturn(true);
        when(dataValidator.isIdValid(projectId)).thenReturn(true);
        when(projectDao.isUserManagerInProject(userId, projectId)).thenReturn(true);

        // Act
        boolean result = projectBean.isUserManagerInProject(userId, projectId);

        // Assert
        assertTrue(result);
        verify(dataValidator, times(2)).isIdValid(userId); // Expect two invocations
    }

    /**
     * Test for isUserManagerInProject method when the userId is invalid.
     * The test is expected to throw an IllegalArgumentException.
     */
    @Test
    public void testIsUserManagerInProject_Failure() {
        // Arrange
        int userId = -1; // Invalid userId
        int projectId = 1;

        when(dataValidator.isIdValid(userId)).thenReturn(false); // Simulate invalid userId

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> projectBean.isUserManagerInProject(userId, projectId));
        verify(dataValidator, times(1)).isIdValid(userId);
        verify(dataValidator, never()).isIdValid(projectId); // Should not be called due to early exit
        verify(projectDao, never()).isUserManagerInProject(userId, projectId); // Should not be called due to early exit
    }

    /**
     * Test for isUserManagerInProject method when the projectId is invalid.
     * The test is expected to throw an IllegalArgumentException.
     */
    @Test
    public void testGetProjectsByCriteria_Failure() {
        // Arrange
        int userId = 1;
        String name = "Test Project";
        int lab = 1;
        int state = 1;
        String keyword = "Test";
        int skill = 1;
        String orderBy = "name";
        boolean orderAsc = true;
        int pageNumber = 1;
        int pageSize = 10;

        when(dataValidator.isIdValid(userId)).thenReturn(true);
        when(dataValidator.validateSearchCriteria(lab, orderBy, pageNumber, pageSize)).thenReturn(true);
        when(dataValidator.isOrderByValidForProject(orderBy)).thenReturn(true);
        when(dataValidator.isValidName(name)).thenReturn(true);
        when(dataValidator.getFirstWord(keyword)).thenReturn(keyword);
        when(dataValidator.isValidName(keyword)).thenReturn(true);
        when(dataValidator.isIdValid(skill)).thenReturn(true);
        when(systemBean.getProjectMaxUsers()).thenReturn(10);

        when(projectDao.getProjectsByCriteria(userId, name, lab, state, keyword, skill, 10, orderBy, orderAsc, pageNumber, pageSize)).thenThrow(PersistenceException.class);

        // Act & Assert

        assertThrows(RuntimeException.class, () -> projectBean.getProjectsByCriteria(userId, name, lab, state, keyword, skill, orderBy, orderAsc, pageNumber, pageSize));
    }

    /**
     * Test for getProjectsByCriteria method when the criteria is successfully retrieved.
     * The test is expected to pass.
     */
    @Test
    public void testProjectEntityToProjectPreview_Success() {
        // Arrange
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(1);
        projectEntity.setName("Test Project");
        projectEntity.setDescription("Test Description");
        LabEntity labEntity = new LabEntity();
        labEntity.setCity(LabEnum.fromId(1));
        projectEntity.setLab(labEntity);
        projectEntity.setState(ProjectStateEnum.fromId(1));
        M2MProjectUser m2MProjectUser = new M2MProjectUser();
        m2MProjectUser.setId(1);
        Set<M2MProjectUser> projectUsers = new HashSet<>();
        projectUsers.add(m2MProjectUser);
        projectEntity.setProjectUsers(projectUsers);
        ProjectUser projectUser = new ProjectUser();
        projectUser.setId(1);
        when(userBean.projectUserToProjectUserDto(m2MProjectUser)).thenReturn(projectUser);

        // Act
        ProjectPreview result = projectBean.projectEntityToProjectPreview(projectEntity);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Project", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(1, result.getLabId());
        assertEquals(ProjectStateEnum.fromId(1).getValue(), result.getState());
        assertEquals(1, result.getProjectUsers().size());
    }

    /**
     * Test for projectEntityToProjectPreview method when the ProjectEntity is null.
     * The test is expected to throw an IllegalArgumentException.
     */
    @Test
    public void testProjectEntityToProjectPreview_Failure() {
        // Arrange
        ProjectEntity projectEntity = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> projectBean.projectEntityToProjectPreview(projectEntity));
    }

   /* @Test
    public void testRemoveUserFromProject_Success() {
        // Arrange
        int projectId = 1;
        int userId = 2;
        when(dataValidator.isIdValid(projectId)).thenReturn(true);
        when(dataValidator.isIdValid(userId)).thenReturn(true);
        ProjectEntity projectEntity = new ProjectEntity();
        LabEntity labEntity = new LabEntity();
        labEntity.setCity(LabEnum.fromId(1)); // Ensure the city is set
        projectEntity.setLab(labEntity);
        when(projectDao.findProjectById(projectId)).thenReturn(projectEntity);
        M2MProjectUser m2MProjectUser = new M2MProjectUser();
        m2MProjectUser.setRole(ProjectUserEnum.MANAGER);
        when(m2MProjectUserDao.findProjectUser(userId, projectId)).thenReturn(m2MProjectUser);
        int mainManagerId = 3;
        when(m2MProjectUserDao.findMainManagerUserIdInProject(projectId)).thenReturn(mainManagerId);
        UserEntity mainManager = new UserEntity();
        when(userDao.findUserById(mainManagerId)).thenReturn(mainManager);
        when(m2MProjectUserDao.merge(m2MProjectUser)).thenReturn(true);
        DetailedProject detailedProject = new DetailedProject();
        when(projectBean.entityToDetailedProject(projectEntity)).thenReturn(detailedProject);

        // Act
        DetailedProject result = projectBean.removeUserFromProject(projectId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(detailedProject, result);
    }*/

    @Test
    public void testRemoveUserFromProject_Failure() {
        // Arrange
        int projectId = -1; // Invalid projectId
        int userId = 2;
        boolean removed = false;
        when(dataValidator.isIdValid(projectId)).thenReturn(false); // Simulate invalid projectId

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> projectBean.removeUserFromProject(projectId, userId, removed));
    }

}