package domcast.finalprojbackend.bean.project;

import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.KeywordBean;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
import domcast.finalprojbackend.dto.userDto.ProjectTeam;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.ProjectUserEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public ProjectEntity newProject(NewProjectDto newProjectDto, ProjectTeam projectTeam, int responsibleUserId, Map<DetailedCR, Integer> cRDtos, Set<Integer> newSkills, Set<Integer> newCRs) {

        if (newProjectDto == null) {
            logger.error("New project DTO is null");
            throw new IllegalArgumentException("New project DTO is null");
        }

        if (dataValidator.isIdValid(responsibleUserId)) {
            logger.error("Responsible user ID is invalid");
            throw new IllegalArgumentException("Responsible user ID is invalid");
        }


        logger.info("Creating new project with name {}", newProjectDto.getName());

        if (!dataValidator.isProjectMandatoryDataValid(newProjectDto)) {
            logger.error("Mandatory data for new project is invalid");
            throw new IllegalArgumentException("Mandatory data for new project is invalid");
        }

        ProjectEntity projectEntity = new ProjectEntity();
        Map<ComponentResourceEntity, Integer> componentResources = new HashMap<>();

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

        try {
            projectEntity = registerProjectInfo(newProjectDto, projectEntity, projectTeam, responsibleUserId, newSkills, componentResources);
        } catch (RuntimeException e) {
            logger.error("Error registering project info: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        try {
            projectDao.persist(projectEntity);
        } catch (PersistenceException e) {
            logger.error("Error persisting project: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        return projectEntity;
    }

    public ProjectEntity registerProjectInfo (NewProjectDto newProjectDto, ProjectEntity projectEntity, ProjectTeam projectTeam, int responsibleUserId, Set<Integer> newSkills, Map<ComponentResourceEntity, Integer> newCRs) {

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
        Set<M2MProjectUser> projectUsers;

        try {
            projectUsers = createProjectTeam(projectTeam.getProjectUsers(), projectEntity, responsibleUserId);
        } catch (RuntimeException e) {
            logger.error("Error creating project team: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        Set<Integer> skillsIds = new HashSet<>();
        Set<M2MProjectSkill> projectSkills;

        if (!newProjectDto.getExistentSkills().isEmpty()) {
            skillsIds.addAll(newProjectDto.getExistentSkills());
        }

        if (!newSkills.isEmpty()) {
            skillsIds.addAll(newSkills);
        }

        try {
            projectSkills = skillBean.createRelationshipToProject(skillsIds, projectEntity);
        } catch (RuntimeException e) {
            logger.error("Error creating relationship between project and skills: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }

        Set<M2MComponentProject> m2MComponentProject;

        try {
            m2MComponentProject = componentResourceBean.relationInProjectCreation(newCRs, projectEntity);
        } catch (RuntimeException e) {
            logger.error("Error creating relationship between project and component resources: {}", e.getMessage());
            // Rethrow the exception to the caller method
            throw e;
        }


        projectEntity.setName(newProjectDto.getName());
        projectEntity.setDescription(newProjectDto.getDescription());
        projectEntity.setLab(labDao.findLabByCity(labCity));
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

        if (teamMembers == null || teamMembers.isEmpty()) {
            logger.info("No team members to add to project team");
            return projectTeam;
        }

        if (project == null) {
            logger.error("Project must not be null");
            throw new IllegalArgumentException("Project must not be null");
        }

        if (responsibleId <= 0) {
            logger.error("Responsible must not be null");
            throw new IllegalArgumentException("Responsible must not be null");
        }

        ProjectUserEnum role;
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

}
