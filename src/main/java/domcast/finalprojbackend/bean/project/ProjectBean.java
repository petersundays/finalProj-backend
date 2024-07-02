package domcast.finalprojbackend.bean.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domcast.finalprojbackend.bean.*;
import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.*;
import domcast.finalprojbackend.dto.componentResourceDto.CRQuantity;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.projectDto.DetailedProject;
import domcast.finalprojbackend.dto.projectDto.EditProject;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
import domcast.finalprojbackend.dto.projectDto.ProjectPreview;
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
import jakarta.persistence.NoResultException;
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

    @EJB
    private SystemBean systemBean;

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

    public DetailedProject newProject(NewProjectDto newProjectDto, ProjectTeam projectTeam, int responsibleUserId, Set<DetailedCR> cRDtos, ArrayList<SkillDto> newSkills) {

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
                for (DetailedCR detailedCR : cRDtos) {
                    componentResourceBean.createComponentResource(detailedCR);
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

        M2MProjectUser mainManager= new M2MProjectUser();

        try {
            UserEntity responsible = userDao.findUserById(responsibleId);
            if (responsible == null) {
                logger.error("Responsible not found with id while creating project team: {}", responsibleId);
                throw new IllegalArgumentException("Responsible not found with id: " + responsibleId);
            }

            mainManager.setUser(responsible);
            mainManager.setProject(project);
            mainManager.setRole(ProjectUserEnum.MAIN_MANAGER);
            mainManager.setApproved(true);

            projectTeam.add(mainManager);

            logger.info("Added responsible user with id {} to project team with role MAIN_MANAGER", responsibleId);

        if (teamMembers == null || teamMembers.isEmpty()) {
            logger.info("No team members to add to project team");
            return projectTeam;
        }

        int maxMembers;

        try {
            maxMembers = systemBean.getProjectMaxUsers();
        } catch (Exception e) {
            logger.error("Error getting project max members while creating project team: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // Check if the number of team members plus the responsible user exceeds the maximum number of members
            // allowed for a project.
            // If it does, log an error and return the project team with only the responsible user
        if (teamMembers.size() > maxMembers - 1) {
            logger.error("Number of team members plus responsible user exceeds the maximum number of members allowed for a project, which is {}", maxMembers);
            return projectTeam;
        }

        ProjectUserEnum role;

            for (Map.Entry<Integer, Integer> entry : teamMembers.entrySet()) {

                M2MProjectUser projectUser= new M2MProjectUser();
                UserEntity user = userDao.findUserById(entry.getKey());

                if (user == null) {
                    logger.error("User not found with id while creating project team: {}", entry.getKey());
                    continue;
                }

                // Check if a M2MProjectUser with the same user ID already exists in the projectTeam
                if (projectTeam.stream().anyMatch(existingUser -> existingUser.getUser().getId() == user.getId())) {
                    logger.error("User with id {} is already in the project team", user.getId());
                    continue;
                }

                role = ProjectUserEnum.fromId(entry.getValue());

                if (role == ProjectUserEnum.MAIN_MANAGER) {
                    logger.error("Main manager role is not allowed for team members. Setting role to MANAGER.");
                    role = ProjectUserEnum.MANAGER;
                }

                projectUser.setUser(user);
                projectUser.setProject(project);
                projectUser.setRole(role);
                projectUser.setApproved(true);

                projectTeam.add(projectUser);
                logger.info("Added user with id {} to project team with role {}", entry.getKey(), role);
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
     * Converts a DetailedProject object to a JSON string.
     * @param detailedProject The DetailedProject object to convert.
     * @return The JSON string representation of the DetailedProject object.
     * @throws JsonProcessingException If an error occurs while converting the DetailedProject object to a JSON string.
     */
    public String convertProjectToJson(DetailedProject detailedProject) throws JsonProcessingException {
        ObjectMapper mapper = objectMapperContextResolver.getContext(null);
        return mapper.writeValueAsString(detailedProject);
    }

    /**
     * Edits a project with the given information.
     *
     * @param editProject The information to edit the project with.
     * @param projectId The ID of the project to edit.
     * @param cRDtos The new component resources to create and add to the project.
     * @param newSkills The new skills to add to the project.
     * @return The edited project as a DetailedProject object.
     */
    public DetailedProject editProject (EditProject editProject, int projectId, Set<DetailedCR> cRDtos, ArrayList<SkillDto> newSkills) {

        if (!dataValidator.isIdValid(projectId)) {
            logger.error("Project ID is invalid");
            throw new IllegalArgumentException("Project ID is invalid");
        }

        logger.info("Editing project with ID {}", projectId);

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {}", projectId, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found with ID {}", projectId);
            throw new IllegalArgumentException("Project not found with ID " + projectId);
        }

        Map<Integer, Integer> componentResources = new HashMap<>();

        if (cRDtos != null && !cRDtos.isEmpty()) {
            try {
                int i = 0;
                for (DetailedCR detailedCR : cRDtos) {
                    componentResourceBean.createComponentResource(detailedCR);
                    i++;
                }
            } catch (RuntimeException e) {
                logger.error("Error creating component resources while editing project: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }

            try {
                componentResources = componentResourceBean.findEntityAndSetQuantity(cRDtos);
            } catch (RuntimeException e) {
                logger.error("Error finding component resources while editing project: {}", e.getMessage());
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
                logger.error("Error creating new skills while editing project: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }

            if (!newSkillsCreated) {
                logger.error("Error creating new skills while editing project");
                throw new RuntimeException("Error creating new skills");
            }

            try {
                for (SkillDto skill : newSkills) {
                    newSkillsNames.add(skill.getName());
                }

                newSkillsIds = skillBean.findSkillsIdsByListOfNames(newSkillsNames);
            } catch (RuntimeException e) {
                logger.error("Error finding new skills ids while editing project: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        Set<KeywordEntity> keywordEntities = new HashSet<>();

        if (editProject != null) {
            try {
                projectEntity = updateBasicInfo(editProject, projectEntity);
            } catch (RuntimeException e) {
                logger.error("Error updating basic info while editing project: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }

            if (editProject.getSkills() != null && !editProject.getSkills().isEmpty()) {
                newSkillsIds.addAll(editProject.getSkills());
            }


            if (editProject.getResources() != null && !editProject.getResources().isEmpty()) {
                for (CRQuantity crQuantity : editProject.getResources()) {
                    componentResources.put(crQuantity.getId(), crQuantity.getQuantity());
                }
            }

            if (editProject.getKeywords() != null && !editProject.getKeywords().isEmpty()) {
                keywordEntities = keywordBean.createAndGetKeywords(editProject.getKeywords());
            }
        }


        if (newSkillsIds != null && !newSkillsIds.isEmpty()) {
            try {
                Set<M2MProjectSkill> projectSkills = skillBean.updateRelationshipToProject(newSkillsIds, projectEntity);
                if (projectSkills != null && !projectSkills.isEmpty()) {
                    projectEntity.setSkills(projectSkills);
                }
            } catch (RuntimeException e) {
                logger.error("Error creating relationship between project and skills while editing project: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        if (componentResources != null && !componentResources.isEmpty()) {
            try {
                Set<M2MComponentProject> m2MComponentProject = componentResourceBean.updateRelationshipToProject(componentResources, projectEntity);
                if (m2MComponentProject != null && !m2MComponentProject.isEmpty()) {
                    projectEntity.setComponentResources(m2MComponentProject);
                }
            } catch (RuntimeException e) {
                logger.error("Error creating relationship between project and component resources while editing project: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        if (keywordEntities != null && !keywordEntities.isEmpty()) {
            try {
                Set<M2MKeyword> projectKeywords = keywordBean.updateRelationshipToProject(keywordEntities, projectEntity);
                if (projectKeywords != null && !projectKeywords.isEmpty()) {
                    projectEntity.setKeywords(projectKeywords);
                }
            } catch (RuntimeException e) {
                logger.error("Error creating relationship between project and keywords while editing project: {}", e.getMessage());
                // Rethrow the exception to the caller method
                throw e;
            }
        }

        try {
            projectDao.merge(projectEntity);
            projectDao.flush();
        } catch (PersistenceException e) {
            logger.error("Error merging project while editing project: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        DetailedProject detailedProject = entityToDetailedProject(projectEntity);

        if (detailedProject == null) {
            logger.error("Error converting project entity to detailed project while editing project");
            throw new RuntimeException("Error converting project entity to detailed project while editing project");
        }

        logger.info("Successfully edited project with ID {}", projectId);
        return detailedProject;

    }


    public ProjectEntity updateBasicInfo (EditProject editProject, ProjectEntity projectEntity) {

        if (projectEntity == null || editProject == null) {
            logger.error("Project entity or edit project is null while updating basic info");
            throw new IllegalArgumentException("Project entity or edit project is null");
        }

        logger.info("Updating basic info for project with ID {}", projectEntity.getId());

        if (editProject.getName() != null) {
            projectEntity.setName(editProject.getName());
        }

        if (editProject.getDescription() != null) {
            projectEntity.setDescription(editProject.getDescription());
        }

        if (editProject.getLabId() != 0) {
            try {
                LabEntity labEntity = labDao.findLabByCity(LabEnum.fromId(editProject.getLabId()).getValue());
                if (labEntity != null) {
                    projectEntity.setLab(labEntity);
                } else {
                    logger.error("Lab not found with ID: {}", editProject.getLabId());
                }
            } catch (RuntimeException e) {
                logger.error("Error finding lab by city while updating basic info: {}", e.getMessage());
            }
        }

        if (editProject.getProjectedStartDate() != null) {
            projectEntity.setProjectedStartDate(editProject.getProjectedStartDate());
        }

        if (editProject.getDeadline() != null) {
            projectEntity.setDeadline(editProject.getDeadline());
        }

        return projectEntity;

    }

    /**
     * Checks if a user is part of a project and active.
     *
     * @param userId    the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is part of the project and active
     */
    public boolean isUserPartOfProjectAndActive(int userId, int projectId) {
        try {
            return projectDao.isUserPartOfProjectAndActive(userId, projectId);
        } catch (PersistenceException e) {
            logger.error("Error checking if user with ID {} is part of project with ID {}: {}", userId, projectId, e.getMessage());
            throw new RuntimeException(e);
        }
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
     * Extracts the EditProject DTO from the input.
     * @param input The multipart form data input containing the EditProject DTO.
     * @return The EditProject DTO extracted from the input.
     * @throws IOException If an error occurs while extracting the EditProject DTO.
     */
    public EditProject extractEditProjectDto(MultipartFormDataInput input) throws IOException {
        InputPart part = input.getFormDataMap().get("project").get(0);
        String projectString = part.getBodyAsString();
        ObjectMapper mapper = objectMapperContextResolver.getContext(null);
        return mapper.readValue(projectString, EditProject.class);
    }

    /**
     * Method to approve or cancel a project.
     * @param projectId The ID of the project to approve or cancel.
     *                  If the project is approved, the state will be set to APPROVED.
     *                  If the project is canceled, the state will be set to CANCELED.
     *
     * @param newState The new state of the project.
     * @return The detailed project object of the project that was approved or canceled.
     */
    public DetailedProject editStateByManager(int projectId, int newState) {

        if (!dataValidator.isIdValid(projectId)) {
            logger.error("Project ID is invalid while editing project state");
            throw new IllegalArgumentException("Project ID is invalid while editing project state");
        }

        if (!ProjectStateEnum.isValidId(newState)) {
            logger.error("Invalid project state");
            throw new IllegalArgumentException("Invalid project state");
        }

        logger.info("Editing state of project with ID {}", projectId);

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {}", projectId, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found with ID {} while editing project state", projectId);
            throw new IllegalArgumentException("Project not found with ID " + projectId + " while editing project state");
        }

        ProjectStateEnum newStateEnum = ProjectStateEnum.fromId(newState);
        ProjectStateEnum currentStateEnum = projectEntity.getState();

        dataValidator.validateStateChange(currentStateEnum, newStateEnum);

        projectEntity.setState(newStateEnum);

        try {
            projectDao.merge(projectEntity);
        } catch (PersistenceException e) {
            logger.error("Error merging project while editing project state: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        DetailedProject detailedProject = entityToDetailedProject(projectEntity);

        if (detailedProject == null) {
            logger.error("Error converting project entity to detailed project while editing project state");
            throw new RuntimeException("Error converting project entity to detailed project while editing project state");
        }

        logger.info("Successfully edited state of project with ID {}", projectId);
        return detailedProject;
    }

    /**
     * Checks if a user is a manager in a project.
     *
     * @param userId    the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is a manager in the project
     */
    public boolean isUserManagerInProject(int userId, int projectId) {

        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId)) {
            logger.error("User ID or project ID is invalid while checking if user is a manager in project");
            throw new IllegalArgumentException("User ID or project ID is invalid while checking if user is a manager in project");
        }

        logger.info("Checking if user with ID {} is a manager in project with ID {}", userId, projectId);

        try {
            return projectDao.isUserManagerInProject(userId, projectId);
        } catch (PersistenceException e) {
            logger.error("Error checking if user with ID {} is a manager in project with ID {}: {}", userId, projectId, e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public boolean approveProject(int projectId, int newState) {

        if (!dataValidator.isIdValid(projectId)) {
            logger.error("Project ID is invalid while approving project");
            throw new IllegalArgumentException("Project ID is invalid while approving project");
        }

        if (!ProjectStateEnum.isValidId(newState)) {
            logger.error("Invalid project state while approving project");
            throw new IllegalArgumentException("Invalid project state");
        }

        logger.info("Approving project with ID {}", projectId);

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {}", projectId, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found with ID {} while approving project", projectId);
            throw new IllegalArgumentException("Project not found with ID " + projectId + " while approving project");
        }

        ProjectStateEnum currentStateEnum = projectEntity.getState();
        ProjectStateEnum newStateEnum = ProjectStateEnum.fromId(newState);

        dataValidator.validateProjectApproval(currentStateEnum, newStateEnum);

        projectEntity.setState(newStateEnum);

        try {
            projectDao.merge(projectEntity);
        } catch (PersistenceException e) {
            logger.error("Error merging project while approving project: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        logger.info("Successfully approved project with ID {}", projectId);
        return true;
    }

    public List<ProjectPreview> getProjectsByCriteria (Integer userId, String name, int lab, int state, String keyword, int skill, String orderBy, boolean orderAsc, int pageNumber, int pageSize) {

        if (userId != 0) {
            if (!dataValidator.isIdValid(userId)) {
                logger.error("Invalid user ID while getting projects by criteria");
                throw new IllegalArgumentException("Invalid user ID while getting projects by criteria");
            }
        }

        if (!dataValidator.validateSearchCriteria(lab,orderBy,pageNumber,pageSize)) {
            logger.error("Invalid input parameters while getting projects by criteria");
            throw new IllegalArgumentException("Invalid input parameters while getting projects by criteria");
        }

        if (!dataValidator.isOrderByValidForProject(orderBy)) {
            logger.error("Invalid order by parameter while getting projects by criteria: {}", orderBy);
            throw new IllegalArgumentException("Invalid order by parameter while getting projects by criteria");
        }

        if (name != null && !name.isEmpty()) {
            if (!dataValidator.isValidName(name)) {
                logger.error("Invalid project name while getting projects by criteria");
                throw new IllegalArgumentException("Invalid project name while getting projects by criteria");
            }
        }

        String keywordTrimmed = "";

        if (keyword != null && keyword.isEmpty()) {
            keywordTrimmed = dataValidator.getFirstWord(keyword);
            keywordTrimmed = dataValidator.isValidName(keywordTrimmed) ? keywordTrimmed : "";

            if (keywordTrimmed != null && !keywordTrimmed.isEmpty() && !keywordTrimmed.equals(keyword)) {
                logger.info("Keyword had more than one word, only the first word will be used: '{}'", keywordTrimmed);
            }
        }


        if (skill != 0) {
            if (!dataValidator.isIdValid(skill)) {
                logger.error("Invalid skill ID while getting projects by criteria");
                throw new IllegalArgumentException("Invalid skill ID while getting projects by criteria");
            }
        }

        int maxUsers;

        try {
            maxUsers = systemBean.getProjectMaxUsers();
        } catch (Exception e) {
            logger.error("Error getting project max users while getting projects by criteria: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        logger.info("Getting projects by criteria");

        List<ProjectEntity> projects;

        try {
            projects = projectDao.getProjectsByCriteria(userId, name, lab, state, keywordTrimmed, skill, maxUsers, orderBy, orderAsc, pageNumber, pageSize);
            if (projects == null || projects.isEmpty()) {
                logger.warn("No projects found by criteria");
                throw new IllegalArgumentException("No projects found by criteria");
            }
        } catch (PersistenceException e) {
            logger.error("Error getting projects by criteria: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        logger.info("Successfully got {} projects by criteria", projects.size());

        List<ProjectPreview> projectPreviews = new ArrayList<>();

        for (ProjectEntity project : projects) {
            try {
                ProjectPreview projectPreview = projectEntityToProjectPreview(project);
                projectPreviews.add(projectPreview);
            } catch (RuntimeException e) {
                logger.error("Error converting project entity to project preview while getting projects by criteria: {}", e.getMessage());
            }
        }

        logger.info("Successfully got projects by criteria");
        return projectPreviews;
    }

    public ProjectPreview projectEntityToProjectPreview(ProjectEntity projectEntity) {
        if (projectEntity == null) {
            logger.error("Project entity is null while converting to project preview");
            throw new IllegalArgumentException("Project entity is null while converting to project preview");
        }

        logger.info("Converting project entity to project preview");

        ProjectPreview projectPreview = new ProjectPreview();
        try {
            projectPreview.setId(projectEntity.getId());
            projectPreview.setName(projectEntity.getName());
            projectPreview.setDescription(projectEntity.getDescription());

            LabEntity lab = projectEntity.getLab();
            if (lab != null && lab.getCity() != null) {
                projectPreview.setLabId(LabEnum.fromValue(lab.getCity().getValue()).getId());
            }

            ProjectStateEnum state = projectEntity.getState();
            if (state != null) {
                projectPreview.setState(ProjectStateEnum.getProjectStateValue(state));
            }
        } catch (NullPointerException e) {
            logger.error("Null value encountered while converting project entity to project preview", e);
            throw new RuntimeException("Null value encountered while converting project entity to project preview", e);
        }

        Set<ProjectUser> projectUsers = new HashSet<>();

        for (M2MProjectUser m2MProjectUser : projectEntity.getProjectUsers()) {
            ProjectUser projectUser = userBean.projectUserToProjectUserDto(m2MProjectUser);

            if (projectUser == null) {
                logger.error("Error converting project user with ID {} to project user DTO while converting project entity {} to project preview", m2MProjectUser.getId(), projectEntity.getId());
            }
            projectUsers.add(projectUser);
        }

        if (!projectUsers.isEmpty()) {
            projectPreview.setProjectUsers(projectUsers);
            logger.info("Added {} project users to project preview", projectUsers.size());
        }

        logger.info("Successfully converted project entity to project preview");

        return projectPreview;
    }

    public DetailedProject removeUserFromProject (int projectId, int userId) {

        if (!dataValidator.isIdValid(projectId) || !dataValidator.isIdValid(userId)) {
            logger.error("Invalid project ID or user ID while removing user from project");
            throw new IllegalArgumentException("Invalid project ID or user ID while removing user from project");
        }

        logger.info("Removing user with ID {} from project with ID {}", userId, projectId);

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {} while removing user from project", projectId, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found with ID {} while removing user from project", projectId);
            throw new IllegalArgumentException("Project not found with ID " + projectId + " while removing user from project");
        }

        M2MProjectUser m2MProjectUser;

        try {
            m2MProjectUser = m2MProjectUserDao.findProjectUser(userId, projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project user with project ID {} and user ID {} while removing user from project", projectId, userId, e);
            throw new RuntimeException(e);
        }

        if (m2MProjectUser == null) {
            logger.error("User with ID {} is not part of project with ID {} while removing user from project", userId, projectId);
            throw new IllegalArgumentException("User with ID " + userId + " is not part of project with ID " + projectId + " while removing user from project");
        }

        logger.info("Found user with ID {} in project with ID {}", userId, projectId);

        if (m2MProjectUser.getRole() == ProjectUserEnum.MAIN_MANAGER) {
            logger.error("Main manager cannot be removed from project");
            throw new IllegalArgumentException("Main manager cannot be removed from project");
        }

        int mainManagerId;

        try {
            mainManagerId = m2MProjectUserDao.findMainManagerUserIdInProject(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding main manager ID for project with ID {} while removing user from project", projectId, e);
            throw new RuntimeException(e);
        }

        if (mainManagerId == 0) {
            logger.error("Main manager not found for project with ID {} while removing user from project", projectId);
            throw new IllegalArgumentException("Main manager not found for project with ID " + projectId + " while removing user from project");
        }

        logger.info("Found main manager with ID {} for project with ID {}", mainManagerId, projectId);

        UserEntity mainManager;

        try {
            mainManager = userDao.findUserById(mainManagerId);
        } catch (PersistenceException e) {
            logger.error("Error finding main manager with ID {} while removing user from project", mainManagerId, e);
            throw new RuntimeException(e);
        }

        if (mainManager == null) {
            logger.error("Main manager not found with ID {} while removing user from project", mainManagerId);
            throw new IllegalArgumentException("Main manager not found with ID " + mainManagerId + " while removing user from project");
        }

        for (TaskEntity task : projectEntity.getTasks()) {
            if (task.getResponsible().getId() == userId) {
                task.setResponsible(mainManager);
                logger.info("Changed responsible user for task with ID {} to project's main manager with ID {}", task.getId(), mainManagerId);
            }
        }

        m2MProjectUser.setActive(false);

        try {
            if (!m2MProjectUserDao.merge(m2MProjectUser)) {
                logger.error("Error removing user with ID {} from project with ID {}", userId, projectId);
                throw new RuntimeException("Error removing user from project");
            }
        } catch (PersistenceException e) {
            logger.error("Error removing user with ID {} from project with ID {}: {}", userId, projectId, e.getMessage());
            throw new RuntimeException(e);
        }
        
        DetailedProject detailedProject = entityToDetailedProject(projectEntity);

        if (detailedProject == null) {
            logger.error("Error converting project entity to detailed project while removing user from project");
            throw new RuntimeException("Error converting project entity to detailed project while removing user from project");
        }

        logger.info("Successfully removed user with ID {} from project with ID {}", userId, projectId);

        return detailedProject;
    }

    /**
     * Invites a user to a project.
     *
     * @param projectId the id of the project
     * @param userId    the id of the user
     * @param role      the role of the user in the project
     * @return boolean value indicating if the user was invited to the project
     */
    public boolean inviteToProject(int projectId, int userId, int role) {

        if (!dataValidator.isIdValid(projectId) || !dataValidator.isIdValid(userId)) {
            logger.error("Invalid project ID or user ID while inviting user to project");
            throw new IllegalArgumentException("Invalid project ID or user ID while inviting user to project");
        }

        if (!ProjectUserEnum.containsId(role)) {
            logger.error("Invalid role while inviting user to project");
            throw new IllegalArgumentException("Invalid role while inviting user to project");
        }

        logger.info("Inviting user with ID {} to project with ID {}", userId, projectId);

        boolean invited;

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {} while inviting user to project", projectId, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found with ID {} while inviting user to project", projectId);
            throw new IllegalArgumentException("Project not found with ID " + projectId + " while inviting user to project");
        }

        logger.info("Found project with ID {}", projectId);

        for (M2MProjectUser m2MProjectUser : projectEntity.getProjectUsers()) {
            System.out.println("User ID: " + m2MProjectUser.getUser().getId() + " Active: " + m2MProjectUser.isActive());
        }


        if (!dataValidator.availablePlacesInProject(projectId)) {
            logger.error("Project with ID {} is full while inviting user to project", projectId);
            throw new IllegalArgumentException("Project with ID " + projectId + " is full while inviting user to project");
        }

        UserEntity userEntity;

        try {
            userEntity = userDao.findUserById(userId);
        } catch (PersistenceException e) {
            logger.error("Error finding user with ID {} while inviting user to project", userId, e);
            throw new RuntimeException(e);
        }

        if (userEntity == null) {
            logger.error("User not found with ID {} while inviting user to project", userId);
            throw new IllegalArgumentException("User not found with ID " + userId + " while inviting user to project");
        }

        logger.info("Found user with ID {}", userId);

        M2MProjectUser m2MProjectUser;
        ProjectUserEnum projectUserEnum = ProjectUserEnum.fromId(role);

        try {
            m2MProjectUser = m2MProjectUserDao.findProjectUser(userId, projectId);
        } catch (NoResultException e) {
            m2MProjectUser = null;
        } catch (PersistenceException e) {
            logger.error("Error finding project user with user ID {} and project ID {} while inviting user to project", userId, projectId, e);
            throw new RuntimeException(e);
        }

        if (m2MProjectUser != null) {
            if (m2MProjectUser.isActive()) {
                logger.error("User with ID {} is already part of project with ID {} while inviting user to project", userId, projectId);
                throw new IllegalArgumentException("User with ID " + userId + " is already part of project with ID " + projectId + " while inviting user to project");
            } else {

                invited = true;
            }
        } else {
            m2MProjectUser = new M2MProjectUser();
            m2MProjectUser = userBean.createProjectUser(m2MProjectUser, userEntity, projectEntity, projectUserEnum);

            try {
                if (!m2MProjectUserDao.persist(m2MProjectUser)) {
                    logger.error("Error inviting new user with ID {} to project with ID {}", userId, projectId);
                    throw new RuntimeException("Error inviting user to project");
                }
            } catch (PersistenceException e) {
                logger.error("Error inviting new user with ID {} to project with ID {}: {}", userId, projectId, e.getMessage());
                throw new RuntimeException(e);
            }

            invited = true;
        }

        ////////////////// SEND MESSAGE TO USER //////////////////

        // **** if Candidate, send message to managers, else send message to user **** //

        ////////////////// Create log in project //////////////////
        logger.info("Successfully invited user with ID {} to project with ID {}", userId, projectId);

        return invited;
    }

    public boolean answerInvitationOrApplication(int projectId, int userId, boolean answer, boolean application) {

        String invitationType = application ? "application" : "invitation";

        logger.info("Answering invitation to project");

        if (!dataValidator.isIdValid(projectId) || !dataValidator.isIdValid(userId)) {
            logger.error("Invalid project ID or user ID while answering {} to project", invitationType);
            throw new IllegalArgumentException("Invalid project ID or user ID while answering invitation to project");
        }

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {} while answering {} to project", projectId, invitationType, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found with ID {} while answering {} to project", projectId, invitationType);
            throw new IllegalArgumentException("Project not found with ID " + projectId + " while answering " + invitationType + " to project");
        }

        M2MProjectUser m2MProjectUser;

        try {
            m2MProjectUser = m2MProjectUserDao.findProjectUser(userId, projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project user with user ID {} and project ID {} while answering {} to project", userId, projectId, invitationType, e);
            throw new RuntimeException(e);
        }

        if (m2MProjectUser == null) {
            logger.error("User with ID {} is not invited to project with ID {} while answering {} to project", userId, projectId, invitationType);
            throw new IllegalArgumentException("User with ID " + userId + " is not invited to project with ID " + projectId + " while answering " + invitationType + " to project");
        }

        if (m2MProjectUser.isActive()) {
            logger.error("User with ID {} is already part of project with ID {} while answering {} to project", userId, projectId, invitationType);
            throw new IllegalArgumentException("User with ID " + userId + " is already part of project with ID " + projectId + " while answering " + invitationType + " to project");
        }

        String message;

        if (answer) {

            if (!dataValidator.availablePlacesInProject(projectId)) {
                logger.error("Project with ID {} is full while answering {} to project", projectId, invitationType);
                throw new IllegalArgumentException("Project with ID " + projectId + " is full while answering " + invitationType + " to project");
            }

            m2MProjectUser.setActive(true);
            m2MProjectUser.setApproved(true);
            projectEntity.addProjectUser(m2MProjectUser);

            if (application) {
                m2MProjectUser.setRole(ProjectUserEnum.PARTICIPANT);
            }

            try {
                if (!projectDao.merge(projectEntity)) {
                    logger.error("Error merging project while answering {} to project with ID {}", invitationType, projectId);
                    throw new RuntimeException("Error merging project while answering " + invitationType + " to project");
                }
            } catch (PersistenceException e) {
                logger.error("Error persisting project while answering {} to project with ID {}: {}", invitationType, projectId, e.getMessage());
                throw new RuntimeException(e);
            }
            message = "User with ID " + userId + " joined project with ID " + projectId;
        } else {
            try {
                m2MProjectUserDao.removeProjectUser(m2MProjectUser.getUser().getId(), projectId);
            } catch (PersistenceException e) {
                logger.error("Error removing project user while answering {} to project with ID {}: {}", invitationType, projectId, e.getMessage());
                throw new RuntimeException(e);
            }

            try {
                projectEntity.removeProjectUser(m2MProjectUser);
            } catch (PersistenceException e) {
                logger.error("Error removing project user from project while answering {} to project with ID {}: {}", invitationType, projectId, e.getMessage());
                throw new RuntimeException(e);
            }

            message = "User with ID " + userId + " did not join project with ID " + projectId;
        }

        try {
            if (!m2MProjectUserDao.merge(m2MProjectUser)) {
                logger.error("Error answering {} to project with ID {}", invitationType, projectId);
                throw new RuntimeException("Error answering " + invitationType + " to project");
            }
        } catch (PersistenceException e) {
            logger.error("Error answering {} to project with ID {}: {}", invitationType, projectId, e.getMessage());
            throw new RuntimeException(e);
        }

        ////////////////// SEND A MESSAGE //////////////////

// ** if application, send message to user, else send message to managers ** //

        ////////////////// Create project logs //////////////////

        logger.info(message);

        return true;
    }

    /**
     * Method to apply to a project.
     * @param projectId The ID of the project to apply to.
     * @param userId The ID of the user applying to the project.
     * @return A boolean value indicating if the user applied to the project.
     */
    public boolean applyToProject(int projectId, int userId) {

        logger.info("Applying to project.");
        boolean applied;

        applied = inviteToProject(projectId, userId, ProjectUserEnum.CANDIDATE.getId());

        return applied;
    }

    /**
     * Method to approve an application to a project.
     * @param projectId The ID of the project to approve the application to.
     * @param userId The ID of the user whose application is being approved.
     * @param answer A boolean value indicating if the application is approved.
     * @return A boolean value indicating if the application was approved.
     */
    public boolean approveApplication(int projectId, int userId, boolean answer) {

        logger.info("Approving application to project.");

        boolean approved;

        approved = answerInvitationOrApplication(projectId, userId, answer, true);

        return approved;
    }

    public DetailedProject getProjectById(int projectId) {

        if (!dataValidator.isIdValid(projectId)) {
            logger.error("Invalid project ID while getting project by ID");
            throw new IllegalArgumentException("Invalid project ID while getting project by ID");
        }

        logger.info("Getting project with ID {}", projectId);

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {}", projectId, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found while getting project by ID: {}", projectId);
            throw new IllegalArgumentException("Project not found with ID " + projectId);
        }

        DetailedProject detailedProject = entityToDetailedProject(projectEntity);

        if (detailedProject == null) {
            logger.error("Error converting project entity to detailed project while getting project by ID");
            throw new RuntimeException("Error converting project entity to detailed project while getting project by ID");
        }

        logger.info("Successfully got project with ID {}", projectId);

        return detailedProject;
    }

    public DetailedProject changeRole(int projectId, int userId, int role) {

        if (!dataValidator.isIdValid(projectId) || !dataValidator.isIdValid(userId)) {
            logger.error("Invalid project ID or user ID while promoting user to manager");
            throw new IllegalArgumentException("Invalid project ID or user ID while promoting user to manager");
        }

        if (!ProjectUserEnum.containsId(role)) {
            logger.error("Invalid role while promoting user to manager");
            throw new IllegalArgumentException("Invalid role while promoting user to manager");
        }

        if (role == ProjectUserEnum.MAIN_MANAGER.getId() || role == ProjectUserEnum.CANDIDATE.getId()) {
            logger.error("Invalid role while promoting user to manager");
            throw new IllegalArgumentException("Invalid role while promoting user to manager");
        }

        logger.info("Promoting user with ID {} to manager in project with ID {}", userId, projectId);

        ProjectUserEnum projectUserEnum = ProjectUserEnum.fromId(role);

        ProjectEntity projectEntity;

        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project with ID {} while promoting user to manager", projectId, e);
            throw new RuntimeException(e);
        }

        if (projectEntity == null) {
            logger.error("Project not found with ID {} while promoting user to manager", projectId);
            throw new IllegalArgumentException("Project not found with ID " + projectId + " while promoting user to manager");
        }

        M2MProjectUser m2MProjectUser;

        try {
            m2MProjectUser = m2MProjectUserDao.findProjectUser(userId, projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project user with user ID {} and project ID {} while promoting user to manager", userId, projectId, e);
            throw new RuntimeException(e);
        }

        if (m2MProjectUser == null || !m2MProjectUser.isActive()) {
            logger.error("User with ID {} is not part of project with ID {} while promoting user to manager", userId, projectId);
            throw new IllegalArgumentException("User with ID " + userId + " is not part of project with ID " + projectId + " while promoting user to manager");
        }

        if (m2MProjectUser.getRole() == ProjectUserEnum.MAIN_MANAGER) {
            logger.error("User with ID {} is already main manager in project with ID {} while promoting user to manager", userId, projectId);
            throw new IllegalArgumentException("User with ID " + userId + " is already main manager in project with ID " + projectId + " while promoting user to manager");
        }

        if (m2MProjectUser.getRole() == ProjectUserEnum.CANDIDATE) {
            logger.error("User with ID {} is a candidate in project with ID {} while promoting user to manager", userId, projectId);
            throw new IllegalArgumentException("User with ID " + userId + " is a candidate in project with ID " + projectId + " and must be approved before being promoted to manager");
        }

        m2MProjectUser.setRole(projectUserEnum);

        try {
            if (!m2MProjectUserDao.merge(m2MProjectUser)) {
                logger.error("Error promoting user with ID {} to manager in project with ID {}", userId, projectId);
                throw new RuntimeException("Error promoting user to manager in project");
            }
        } catch (PersistenceException e) {
            logger.error("Error promoting user with ID {} to manager in project with ID {}: {}", userId, projectId, e.getMessage());
            throw new RuntimeException(e);
        }

        ////////////////// SEND MESSAGE TO USER //////////////////

        ////////////////// Create log in project //////////////////

        DetailedProject detailedProject = entityToDetailedProject(projectEntity);

        if (detailedProject == null) {
            logger.error("Error converting project entity to detailed project while promoting user to manager");
            throw new RuntimeException("Error converting project entity to detailed project while promoting user to manager");
        }

        logger.info("Successfully promoted user with ID {} to manager in project with ID {}", userId, projectId);

        return detailedProject;
    }
}
