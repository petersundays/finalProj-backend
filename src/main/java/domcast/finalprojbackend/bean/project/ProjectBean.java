package domcast.finalprojbackend.bean.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.KeywordBean;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.*;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.projectDto.DetailedProject;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.userDto.ProjectTeam;
import domcast.finalprojbackend.dto.userDto.ProjectUser;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.ProjectStateEnum;
import domcast.finalprojbackend.enums.ProjectUserEnum;
import domcast.finalprojbackend.service.ObjectMapperContextResolver;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

@Stateless
public class ProjectBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @EJB
    private ProjectDao projectDao;

    @EJB
    private UserDao userDao;

    @EJB
    private DataValidator dataValidator;

    @EJB
    private LabDao labDao;

    @EJB
    private KeywordBean keywordBean;

    @EJB
    private SkillBean skillBean;

    @EJB
    private ComponentResourceBean componentResourceBean;

    @EJB
    private TaskBean taskBean;

    @EJB
    private M2MProjectUserDao m2MProjectUserDao;

    @EJB
    private UserBean userBean;

    @EJB
    private M2MComponentProjectDao m2MComponentProjectDao;

    @Inject
    private ObjectMapperContextResolver objectMapperContextResolver;


    /**
     * Default constructor for ProjectBean.
     */
    public ProjectBean() {
    }

    /**
     * Checks if a user is active in a project.
     *
     * @param userId    the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is active in the project
     */
    public boolean isUserActiveAndApprovedInProject(int userId, int projectId) {
        logger.info("Checking if user with ID {} is active in project with ID {}", userId, projectId);

        boolean isActive;
        try {
            isActive = projectDao.isUserActiveAndApprovedInProject(userId, projectId);
        } catch (PersistenceException e) {
            logger.error("Error checking if user with ID {} is active in project with ID {}", userId, projectId, e);
            throw new RuntimeException(e);
        }

        logger.info("User with ID {} is active in project with ID {}: {}", userId, projectId, isActive);
        return isActive;
    }

    public DetailedProject newProject(NewProjectDto newProjectDto, ProjectTeam projectTeam, int responsibleUserId, Map<DetailedCR, Integer> cRDtos, ArrayList<SkillDto> newSkills) {

        if (newProjectDto == null) {
            logger.error("New project DTO is null");
            throw new IllegalArgumentException("New project DTO is null");
        }

        if (!dataValidator.isIdValid(responsibleUserId)) {
            logger.error("Responsible user ID is invalid");
            throw new IllegalArgumentException("Responsible user ID is invalid");
        }


        logger.info("Creating new project with name {}", newProjectDto.getName());

        if (!dataValidator.isProjectMandatoryDataValid(newProjectDto)) {
            logger.error("Mandatory data for new project is invalid");
            throw new IllegalArgumentException("Mandatory data for new project is invalid");
        }

        ProjectEntity projectEntity = new ProjectEntity();
        Map<Integer, Integer> componentResources = new HashMap<>();

        if (cRDtos != null && !cRDtos.isEmpty()) {
            try {
                for (Map.Entry<DetailedCR, Integer> entry : cRDtos.entrySet()) {
                    componentResourceBean.createComponentResource(entry.getKey());
                }
            } catch (RuntimeException e) {
                logger.error("Error creating component resources: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }

            try {
                componentResources = componentResourceBean.findEntityAndSetQuantity(cRDtos);
            } catch (RuntimeException e) {
                logger.error("Error finding component resources: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        boolean newSkillsCreated;
        Set<String> newSkillsNames = new HashSet<>();
        Set<Integer> newSkillsIds = new HashSet<>();

        if (newSkills != null && !newSkills.isEmpty()) {
            try {
                newSkillsCreated = skillBean.createSkills(newSkills);
            } catch (RuntimeException e) {
                logger.error("Error creating new skills: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }

            if (!newSkillsCreated) {
                logger.error("Error creating new skills");
                throw new RuntimeException("Error creating new skills");
            }


            try {
                for (SkillDto skill : newSkills) {
                    newSkillsNames.add(skill.getName());
                }

                newSkillsIds = skillBean.findSkillsIdsByListOfNames(newSkillsNames);
            } catch (RuntimeException e) {
                logger.error("Error finding new skills ids: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        try {
            projectEntity = registerProjectInfo(newProjectDto, projectEntity, projectTeam, responsibleUserId, newSkillsIds, componentResources);
        } catch (RuntimeException e) {
            logger.error("Error registering project info: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }


        try {
            projectDao.persist(projectEntity);
            projectDao.flush();
        } catch (PersistenceException e) {
            logger.error("Error persisting project: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        boolean presentationTask;
        try {
            presentationTask = taskBean.presentationTask(responsibleUserId, projectEntity);
        } catch (RuntimeException e) {
            logger.error("Error creating presentation task: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        if (!presentationTask) {
            logger.error("Error creating presentation task");
            throw new RuntimeException("Error creating presentation task");
        }

        for (M2MComponentProject m2MComponentProject : projectEntity.getComponentResources()) {
            m2MComponentProjectDao.persist(m2MComponentProject);
        }

        DetailedProject detailedProject = entityToDetailedProject(projectEntity);

        if (detailedProject == null) {
            logger.error("Error converting project entity to detailed project");
            throw new RuntimeException("Error converting project entity to detailed project");
        }

        logger.info("Successfully created new project with name {}", newProjectDto.getName());
        return detailedProject;
    }

    public ProjectEntity registerProjectInfo (NewProjectDto newProjectDto, ProjectEntity projectEntity, ProjectTeam projectTeam, int responsibleUserId, Set<Integer> newSkills, Map<Integer, Integer> newCRs) {

        if (newProjectDto == null || projectEntity == null) {
            logger.error("New project DTO or project entity is null");
            throw new IllegalArgumentException("New project DTO or project entity is null");
        }

        if (newProjectDto.getKeywords().isEmpty()) {
            logger.error("A Project must have at least one keyword");
            throw new IllegalArgumentException("A Project must have at least one keyword");
        }

        logger.info("Registering project info for project with name {}", newProjectDto.getName());


        LabEnum labEnum = LabEnum.fromId(newProjectDto.getLabId());
        String labCity = labEnum.getValue();

        LabEntity labEntity;

        try {
            labEntity = labDao.findLabByCity(labCity);
        } catch (PersistenceException e) {
            logger.error("Error finding lab by city: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        if (labEntity == null) {
            logger.error("Lab not found with city: {}", labCity);
            throw new IllegalArgumentException("Lab not found with city: " + labCity);
        }

        Set<KeywordEntity> keywordEntities;
        try {
            keywordEntities = keywordBean.createAndGetKeywords(newProjectDto.getKeywords());
        } catch (RuntimeException e) {
            logger.error("Error creating or getting keywords: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        Set<M2MKeyword> projectKeywords;
        try {
            projectKeywords = keywordBean.createRelationship(projectEntity, keywordEntities);
        } catch (RuntimeException e) {
            logger.error("Error creating relationship between project and keywords: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        // Set project users
        Set<M2MProjectUser> projectUsers = new HashSet<>();


        try {
            if (projectTeam == null) {
                projectTeam = new ProjectTeam();
            }
            projectUsers = createProjectTeam(projectTeam.getProjectUsers(), projectEntity, responsibleUserId);
        } catch (RuntimeException e) {
            logger.error("Error creating project team: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        Set<Integer> skillsIds = new HashSet<>();
        Set<M2MProjectSkill> projectSkills = new HashSet<>();

        if (newSkills != null && !newSkills.isEmpty()) {
            skillsIds.addAll(newSkills);
        }

        if (newProjectDto.getExistentSkills() != null && !newProjectDto.getExistentSkills().isEmpty()) {
            skillsIds.addAll(newProjectDto.getExistentSkills());
        }

        if (!skillsIds.isEmpty()) {
            try {
                projectSkills = skillBean.createRelationshipToProject(skillsIds, projectEntity);
            } catch (RuntimeException e) {
                logger.error("Error creating relationship between project and skills: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        Set<M2MComponentProject> m2MComponentProject = new HashSet<>();
        Map<Integer, Integer> componentResources = new HashMap<>();

        if (newCRs != null && !newCRs.isEmpty()) {
            componentResources.putAll(newCRs);
        }

        if (newProjectDto.getExistentResources() != null && !newProjectDto.getExistentResources().isEmpty()) {
            componentResources.putAll(newProjectDto.getExistentResources());
        }

        if (!componentResources.isEmpty()) {
            try {
                m2MComponentProject = componentResourceBean.relationInProjectCreation(componentResources, projectEntity);
            } catch (RuntimeException e) {
                logger.error("Error creating relationship between project and component resources: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        projectEntity.setName(newProjectDto.getName());
        projectEntity.setDescription(newProjectDto.getDescription());
        projectEntity.setLab(labEntity);
        projectEntity.setKeywords(projectKeywords);
        projectEntity.setProjectedStartDate(newProjectDto.getProjectedStartDate());
        projectEntity.setDeadline(newProjectDto.getDeadline());
        projectEntity.setProjectUsers(projectUsers);
        projectEntity.setSkills(projectSkills);
        projectEntity.setComponentResources(m2MComponentProject);

        return projectEntity;
    }

    /**
     * Creates a project team with the given team members and project.
     * @param teamMembers The team members to add to the project team.
     * @param project The project to add the team members to.
     * @return A set of M2MProjectUser objects that represent the project team.
     */
    public Set<M2MProjectUser> createProjectTeam (Map<Integer,Integer> teamMembers, ProjectEntity project, int responsibleId) {
        logger.info("Creating project team");

        Set<M2MProjectUser> projectTeam = new HashSet<>();

        if (project == null) {
            logger.error("Project must not be null");
            throw new IllegalArgumentException("Project must not be null");
        }

        if (!dataValidator.isIdValid(responsibleId)) {
            logger.error("Responsible ID is invalid while creating project team");
            throw new IllegalArgumentException("Responsible ID is invalid while creating project team");
        }

        M2MProjectUser projectUser= new M2MProjectUser();

        try {
            UserEntity responsible = userDao.findUserById(responsibleId);
            if (responsible == null) {
                logger.error("Responsible not found with id while creating project team: {}", responsibleId);
                throw new IllegalArgumentException("Responsible not found with id: " + responsibleId);
            }

            projectUser.setUser(responsible);
            projectUser.setProject(project);
            projectUser.setRole(ProjectUserEnum.MAIN_MANAGER);
            projectUser.setApproved(true);

            projectTeam.add(projectUser);

        if (teamMembers == null || teamMembers.isEmpty()) {
            logger.info("No team members to add to project team");
            return projectTeam;
        }

        ProjectUserEnum role;

            for (Map.Entry<Integer, Integer> entry : teamMembers.entrySet()) {
                UserEntity user = userDao.findUserById(entry.getKey());

                if (user == null) {
                    logger.error("User not found with id while creating project team: {}", entry.getKey());
                    continue;
                }

                role = ProjectUserEnum.fromId(entry.getValue());

                projectUser.setUser(user);
                projectUser.setProject(project);
                projectUser.setRole(role);
                projectUser.setApproved(true);

                projectTeam.add(projectUser);
            }
        } catch (Exception e) {
            logger.error("Error while creating project team: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return projectTeam;
    }

    /**
     * Converts a ProjectEntity object to a DetailedProject object.
     *
     * @param projectEntity the ProjectEntity object
     * @return the DetailedProject object
     */
    public DetailedProject entityToDetailedProject (ProjectEntity projectEntity) {
        logger.info("Converting ProjectEntity to DetailedProject");

        if (projectEntity == null) {
            logger.error("Project entity is null");
            throw new IllegalArgumentException("Project entity is null");
        }

        DetailedProject detailedProject = new DetailedProject();
        M2MProjectUser userInProject = new M2MProjectUser();
        Set<M2MProjectUser> projectTeam = new HashSet<>();
        Set<ChartTask> tasks = new HashSet<>();

        try {
            userInProject = m2MProjectUserDao.findMainManagerInProject(projectEntity.getId());
        } catch (PersistenceException e) {
            logger.error("Error finding main manager in project with id: {}", projectEntity.getId(), e);
        }

        ProjectUser mainManager = userBean.projectUserToProjectUserDto(userInProject);

        try {
            projectTeam = m2MProjectUserDao.findProjectTeam(projectEntity.getId());
        } catch (PersistenceException e) {
            logger.error("Error finding project team for project with id: {}", projectEntity.getId(), e);
        }

        if (projectTeam == null) {
            logger.warn("No team found for project with id: {}", projectEntity.getId());
        }
        Set<ProjectUser> collaboratorsDto = userBean.projectUsersToListOfProjectUser(projectTeam);

        try {
            List<ChartTask> taskList = taskBean.findTaskByProjectId(projectEntity.getId());
            tasks = new HashSet<>(taskList);
        } catch (PersistenceException e) {
            logger.error("Error finding tasks for project with id: {}", projectEntity.getId(), e);
        }

        System.out.println("DATAS : " + projectEntity.getProjectedStartDate());
        detailedProject.setId(projectEntity.getId());
        detailedProject.setName(projectEntity.getName());
        detailedProject.setDescription(projectEntity.getDescription());
        detailedProject.setLabId(projectEntity.getLab().getCity().getId());
        detailedProject.setState(ProjectStateEnum.getProjectStateValue(projectEntity.getState()));
        detailedProject.setProjectedStartDate(projectEntity.getProjectedStartDate());
        detailedProject.setDeadline(projectEntity.getDeadline());
        detailedProject.setKeywords(keywordBean.m2mToKeywordDto(projectEntity.getKeywords()));
        detailedProject.setSkills(skillBean.projectSkillToDto(projectEntity.getSkills()));
        detailedProject.setResources(componentResourceBean.componentProjectToCRPreview(projectEntity.getComponentResources()));
        detailedProject.setMainManager(mainManager);
        detailedProject.setCollaborators(collaboratorsDto);
        detailedProject.setTasks(tasks);

        logger.info("Successfully converted ProjectEntity to DetailedProject");

        return detailedProject;
    }

    /**
     * Extracts the new oroject DTO from the input.
     * @param input The multipart form data input containing the new project DTO.
     * @return The new project DTO extracted from the input.
     * @throws IOException If an error occurs while extracting the new project DTO.
     */
    public NewProjectDto extractNewProjectDto(MultipartFormDataInput input) throws IOException {
        InputPart part = input.getFormDataMap().get("project").get(0);
        String userString = part.getBodyAsString();
        ObjectMapper mapper = objectMapperContextResolver.getContext(null);
        return mapper.readValue(userString, NewProjectDto.class);
    }

    /**
     * Converts a DetailedProject object to a JSON string.
     * @param detailedProject The DetailedProject object to convert.
     * @return The JSON string representation of the DetailedProject object.
     * @throws JsonProcessingException If an error occurs while converting the DetailedProject object to a JSON string.
     */
    public String convertProjectToJson(DetailedProject detailedProject) throws JsonProcessingException {
        ObjectMapper mapper = objectMapperContextResolver.getContext(null);
        return mapper.writeValueAsString(detailedProject);
    }
}
