package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.projectDto.DetailedProject;
import domcast.finalprojbackend.dto.projectDto.EditProject;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
import domcast.finalprojbackend.dto.projectDto.ProjectPreview;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import domcast.finalprojbackend.dto.userDto.EnumDTO;
import domcast.finalprojbackend.dto.userDto.ProjectTeam;
import domcast.finalprojbackend.enums.ProjectStateEnum;
import domcast.finalprojbackend.enums.ProjectUserEnum;
import domcast.finalprojbackend.enums.RecordTopicEnum;
import domcast.finalprojbackend.enums.util.EnumUtil;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Path("/project")
public class ProjectService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private ProjectBean projectBean;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private UserBean userBean;

    @Inject
    private ComponentResourceBean componentResourceBean;

    @Inject
    private SkillBean skillBean;

    /**
     * Method to create a new project.
     *
     * @param token the session token
     * @param id the id of the user
     * @param input the input data
     * @param request the HTTP request
     * @return the response
     * @throws IOException if an error occurs while reading the input data
     */
    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response createProject (@HeaderParam("token") String token, @HeaderParam("id") int id, MultipartFormDataInput input, @Context HttpServletRequest request) throws IOException {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is creating a new project from IP address {}", token, id, ipAddress);

        Response response;

        if (!dataValidator.isIdValid(id)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to create a new project with invalid id {}", token, id);
            return response;
        }

        // Check if the user is authorized to create a new project
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to create a new project but is not authorized", token);
            return response;
        }

        if (!input.getFormDataMap().containsKey("project")) {
            response = Response.status(400).entity("Missing project data").build();
            logger.info("User with session token {} tried to create a new project but the project data is missing", token);
            return response;
        }

        DetailedProject detailedProject;
        ProjectTeam projectTeam = null;
        Set<DetailedCR> cRDtos = null;
        ArrayList<SkillDto> newSkills = null;

        try {
            NewProjectDto newProjectDto = projectBean.extractNewProjectDto(input);

            if (input.getFormDataMap().containsKey("team")) {
                projectTeam = userBean.extractProjectTeam(input);
            }
            if (input.getFormDataMap().containsKey("components")) {
                cRDtos = componentResourceBean.extractCRDtos(input);
            }
            if (input.getFormDataMap().containsKey("skills")) {
                newSkills = skillBean.extractNewSkills(input);
            }

            logger.info("User with session token {} and id {} is creating a new project", token, id);

            detailedProject = projectBean.newProject(newProjectDto, projectTeam, id, cRDtos, newSkills);

            // Convert the registeredUser object to a JSON string
            String detailedProjectJson = projectBean.convertProjectToJson(detailedProject);

            response = Response.status(201).entity(detailedProjectJson).build();
            logger.info("User with session token {} and id {} created a new project", token, id);
        } catch (Exception e) {
            logger.error("Error creating new project: {}", e.getMessage());
            response = Response.status(500).entity("Error creating new project").build();
        }

        return response;

    }
    /**
     * Method to edit a project.
     *
     * @param token the session token
     * @param id the id of the user
     * @param projectId the id of the project
     * @param input the input data
     * @param request the HTTP request
     * @return the response
     * @throws IOException if an error occurs while reading the input data
     */
    @PUT
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response editProject (@HeaderParam("token") String token, @HeaderParam("id") int id, @QueryParam("id") int projectId, MultipartFormDataInput input, @Context HttpServletRequest request) throws IOException {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is editing a project from IP address {}", token, id, ipAddress);

        Response response;

        if (!dataValidator.isIdValid(id) || !dataValidator.isIdValid(projectId)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to edit a project with invalid id {}", token, id);
            return response;
        }

        // Check if the user is authorized to create a new project
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to edit a project but is not authorized", token);
            return response;
        }

        if (!authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(id, projectId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to edit a project but is not a member of the project", token);
            return response;
        }

        if (!input.getFormDataMap().containsKey("project") && !input.getFormDataMap().containsKey("components") && !input.getFormDataMap().containsKey("skills")) {
            response = Response.status(400).entity("Missing project data").build();
            logger.info("User with session token {} tried to edit a project but the project data is missing", token);
            return response;
        }

        DetailedProject detailedProject;
        Set<DetailedCR> cRDtos = null;
        ArrayList<SkillDto> newSkills = null;
        EditProject editProject = null;

        try {
            if (input.getFormDataMap().containsKey("project")) {
                editProject = projectBean.extractEditProjectDto(input);
            }

            if (input.getFormDataMap().containsKey("components")) {
                cRDtos = componentResourceBean.extractCRDtos(input);
            }
            if (input.getFormDataMap().containsKey("skills")) {
                newSkills = skillBean.extractNewSkills(input);
            }

            logger.info("User with session token {} and id {} is editing the project with id {}", token, id, projectId);

            detailedProject = projectBean.editProject(editProject, projectId, cRDtos, newSkills);

            // Convert the registeredUser object to a JSON string
            String detailedProjectJson = projectBean.convertProjectToJson(detailedProject);

            response = Response.status(201).entity(detailedProjectJson).build();
            logger.info("User with session token {} and id {} edited the project with id {}", token, id, projectId);
        } catch (Exception e) {
            logger.error("Error editing project with id {}: {}", projectId, e.getMessage());
            response = Response.status(500).entity("Error creating new project").build();
        }

        return response;

    }

    /**
     * Method to get the possible states of a project.
     * @param token the session token
     * @param id the id of the user
     * @param request the HTTP request
     * @return the response with the possible states of a project
     *
     */
    @GET
    @Path("/state-enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectStateEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the project state enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the project state enum but is not authorized", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the project state enum but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the project state enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(ProjectStateEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the project state enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting project state enum: {}", e.getMessage());
            response = Response.status(500).entity("Error getting project state enum").build();
        }

        return response;
    }

    /**
     * Method to get the possible roles of a user in a project.
     * @param token the session token
     * @param id the id of the user
     * @param request the HTTP request
     * @return the response with the possible roles of a user in a project
     *
     */
    @GET
    @Path("/user-enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectUserEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the project user enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the project user enum but is not authorized", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the project user enum but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the project user enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(ProjectUserEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the project user enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting project user enum", e);
            response = Response.status(500).entity("Error getting project user enum").build();
        }

        return response;
    }

    /**
     * Method to get the record topic enum.
     * @param token the session token
     * @param id the id of the user
     * @param request the HTTP request
     * @return the response with the record topic enum
     *
     */
    @GET
    @Path("/record-enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecordTopicEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the record topic enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the record topic enum but is not authorized", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the record topic enum but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the record topic enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(RecordTopicEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the record topic enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting record topic enum", e);
            response = Response.status(500).entity("Error getting record topic enum").build();
        }

        return response;
    }

    /**
     * Method to update the state of a project by a manager.
     * @param token the session token
     * @param userId the id of the user
     * @param projectId the id of the project
     * @param state the new state of the project
     * @param request the HTTP request
     * @return the response with the updated project
     */
    @PUT
    @Path("/state")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStateByManager(@HeaderParam("token") String token,
                                         @HeaderParam("id") int userId,
                                         @QueryParam("id") int projectId,
                                         @QueryParam("newState") int state,
                                         @Context HttpServletRequest request) {


        String ipAddress = request.getRemoteAddr();
        logger.info("User with session token {} and id {} is updating the state of the project with id {} from IP address {}", token, userId, projectId, ipAddress);

        // Check if the user's and project's id are valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to update the state of the project with invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!ProjectStateEnum.isValidId(state)) {
            logger.info("User with session token {} tried to update the state of the project with invalid state", token);
            return Response.status(400).entity("New state is invalid").build();
        }

        if (!authenticationAndAuthorization.isUserManagerInProject(userId, projectId)) {
            logger.info("User with session token {} tried to update the state of the project but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is updating the state of the project with id {}", token, userId, projectId);
            DetailedProject detailedProject = projectBean.editStateByManager(projectId, state);
            response = Response.status(200).entity(detailedProject).build();
            logger.info("User with session token {} and id {} successfully updated the state of the project with id {}", token, userId, projectId);
        } catch (Exception e) {
            logger.error("Error updating the state of the project with id {}: {}", projectId, e.getMessage());
            response = Response.status(500).entity("Error updating the state of the project").build();
        }

        return response;

    }

    /**
     * Method to approve a project.
     * Only an admin can approve a project.
     * @param token the session token
     * @param userId the id of the user
     * @param projectId the id of the project
     * @param state the new state of the project
     * @param request the HTTP request
     * @return the response
     */
    @PUT
    @Path("/approve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response approve(@HeaderParam("token") String token,
                                         @HeaderParam("id") int userId,
                                         @QueryParam("id") int projectId,
                                         @QueryParam("newState") int state,
                                         @Context HttpServletRequest request) {


        String ipAddress = request.getRemoteAddr();
        logger.info("User with session token {} and id {} is trying to approve the project with id {} from IP address {}", token, userId, projectId, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to approve the project with invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!ProjectStateEnum.isValidId(state)) {
            logger.info("User with session token {} tried to approve the project with invalid state", token);
            return Response.status(400).entity("New state is invalid").build();
        }

        if (!authenticationAndAuthorization.isUserAdminById(userId)) {
            logger.info("User with session token {} tried to approve the project but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is approving the project with id {}", token, userId, projectId);
            if (projectBean.approveProject(projectId, state)) {
                if (state == ProjectStateEnum.APPROVED.getId()) {
                    response = Response.status(200).entity("Project successfully approved").build();
                    logger.info("User with session token {} and id {} successfully approved the project with id {}", token, userId, projectId);
                } else {
                    response = Response.status(200).entity("Project cancellation successfully approved").build();
                    logger.info("User with session token {} and id {} successfully approved the cancellation of the project with id {}", token, userId, projectId);
                }
            } else {
                response = Response.status(400).entity("Project approval status could not be set").build();
                logger.info("User with session token {} and id {} could not set the approval status of the project with id {}", token, userId, projectId);
            }
        } catch (Exception e) {
            logger.error("Error while setting the approval status of the project with id {}: {}", projectId, e.getMessage());
            response = Response.status(500).entity("Error while setting the approval status of the project").build();
        }

        return response;

    }

    /**
     * Method to get the projects by criteria.
     * @param sessionToken the session token
     * @param loggedUserId the id of the user
     * @param userId the id of the user
     * @param name the name of the project
     * @param labId the id of the lab
     * @param stateId the id of the state
     * @param keyword the keyword
     * @param orderBy the order by
     * @param orderAsc the order asc
     * @param pageNumber the page number
     * @param pageSize the page size
     * @param request the HTTP request
     * @return the response with the projects
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectsByCriteria(@QueryParam("userId") int userId,
                                          @QueryParam("name") String name,
                                          @QueryParam("lab") int labId,
                                          @QueryParam("state") int stateId,
                                          @QueryParam("keyword") String keyword,
                                          @QueryParam("skill") int skill,
                                          @QueryParam("orderBy") String orderBy,
                                          @QueryParam("orderAsc") boolean orderAsc,
                                          @QueryParam("pageNumber") int pageNumber,
                                          @QueryParam("pageSize") int pageSize,
                                          @Context HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to get projects by criteria", ipAddress);

        Response response;
        List<ProjectPreview> projects;

        try {
            projects = projectBean.getProjectsByCriteria(userId, name, labId, stateId, keyword, skill, orderBy, orderAsc, pageNumber, pageSize);
            response = Response.status(200).entity(projects).build();
            logger.info("User with IP address {} got {} projects by criteria", ipAddress, projects.size());
        } catch (IllegalArgumentException e) {
            response = Response.status(400).entity(e.getMessage()).build();
            logger.info("User with IP address {} tried to get projects by criteria unsuccessfully", ipAddress);
        }

        return response;
    }

    /**
     * Method to remove a user from a project.
     * @param token the session token
     * @param userId the id of the user
     * @param projectId the id of the project
     * @param userToRemoveId the id of the user to remove
     * @param request the HTTP request
     * @return the response with the result
     */
    @PUT
    @Path("/remove-user")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUserFromProject(@HeaderParam("token") String token,
                                          @HeaderParam("id") int userId,
                                          @QueryParam("projectId") int projectId,
                                          @QueryParam("userId") int userToRemoveId,
                                          @Context HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        logger.info("User with session token {} and id {} is trying to remove the user with id {} from the project with id {} from IP address {}", token, userId, userToRemoveId, projectId, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId) || !dataValidator.isIdValid(userToRemoveId)) {
            logger.info("User with session token {} tried to remove a user from the project, but the ids are invalid", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!authenticationAndAuthorization.isUserManagerInProject(userId, projectId)) {
            logger.info("User with session token {} tried to remove the user with id {} from the project but is not authorized", token, userToRemoveId);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        DetailedProject detailedProject;

        try {
            logger.info("User with session token {} and id {} is removing the user with id {} from the project with id {}", token, userId, userToRemoveId, projectId);
            detailedProject = projectBean.removeUserFromProject(projectId, userToRemoveId);
            if (detailedProject != null) {
                response = Response.status(200).entity("User with id " + userToRemoveId + " successfully removed from the project with id " + projectId).build();
                logger.info("User with session token {} and id {} successfully removed the user with id {} from the project with id {}", token, userId, userToRemoveId, projectId);
            } else {
                response = Response.status(400).entity("User with id " + userToRemoveId + " could not be removed from the project with id " + projectId).build();
                logger.info("User with session token {} and id {} could not remove the user with id {} from the project with id {}", token, userId, userToRemoveId, projectId);
            }
        } catch (Exception e) {
            logger.error("Error while removing the user with id {} from the project with id {}: {}", userToRemoveId, projectId, e.getMessage());
            response = Response.status(500).entity("Error while removing the user from the project").build();
        }

        return response;

    }

    /**
     * Method to invite a user to a project.
     * This method is idempotent, meaning that if the user is already invited,
     * the method will return a success message.
     *
     * @param token the session token
     * @param userId the id of the user
     * @param projectId the id of the project
     * @param userToInviteId the id of the user to invite
     * @param role the role of the user in the project
     * @param request the HTTP request
     * @return the response
     */
    @PUT
    @Path("/invite")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response inviteToProject(@HeaderParam("token") String token,
                                    @HeaderParam("id") int userId,
                                    @QueryParam("projectId") int projectId,
                                    @QueryParam("userId") int userToInviteId,
                                    @QueryParam("role") int role,
                                    @Context HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        logger.info("User with session token {} and id {} is trying to invite the user with id {} to the project with id {} from IP address {}", token, userId, userToInviteId, projectId, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId) || !dataValidator.isIdValid(userToInviteId)) {
            logger.info("User with session token {} tried to invite a user to the project, but the ids are invalid", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!ProjectUserEnum.containsId(role)) {
            logger.info("User with session token {} tried to invite a user to the project with invalid role", token);
            return Response.status(400).entity("Invalid role").build();
        }

        if (!authenticationAndAuthorization.isUserManagerInProject(userId, projectId)) {
            logger.info("User with session token {} tried to invite the user with id {} to the project but is not authorized", token, userToInviteId);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        boolean invited;

        try {
            logger.info("User with session token {} and id {} is inviting the user with id {} to the project with id {}", token, userId, userToInviteId, projectId);
            invited = projectBean.inviteToProject(projectId, userToInviteId, role);
            if (invited) {
                response = Response.status(200).entity("User with id " + userToInviteId + " successfully invited to the project with id " + projectId).build();
                logger.info("User with session token {} and id {} successfully invited the user with id {} to the project with id {}", token, userId, userToInviteId, projectId);
            } else {
                response = Response.status(400).entity("User with id " + userToInviteId + " could not be invited to the project with id " + projectId).build();
                logger.info("User with session token {} and id {} could not invite the user with id {} to the project with id {}", token, userId, userToInviteId, projectId);
            }
        } catch (Exception e) {
            logger.error("Error while inviting the user with id {} to the project with id {}: {}", userToInviteId, projectId, e.getMessage());
            response = Response.status(500).entity("Error while inviting the user to the project").build();
        }

        return response;

    }

    /**
     * Method to answer an invitation to a project.
     * @param token the session token
     * @param userId the id of the user
     * @param projectId the id of the project
     * @param request the HTTP request
     * @return the response
     */
    @PUT
    @Path("/answer-invitation")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response answerInvitation(@HeaderParam("token") String token,
                                    @HeaderParam("id") int userId,
                                    @QueryParam("projectId") int projectId,
                                    @QueryParam("answer") boolean answer,
                                    @Context HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        logger.info("User with session token {} and id {} is trying to answer the invitation to the project with id {} from IP address {}", token, userId, projectId, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to answer the invitation to the project, but the ids are invalid", token);
            return Response.status(400).entity("Invalid id").build();
        }

        Response response;
        boolean success;

        try {
            logger.info("User with session token {} and id {} is answering the invitation to the project with id {}", token, userId, projectId);
            success = projectBean.answerInvitation(projectId, userId, answer);
            if (success) {
                response = Response.status(200).entity("User with id " + userId + " successfully answered the invitation to the project with id " + projectId).build();
                logger.info("User with session token {} and id {} successfully answered the invitation to the project with id {}", token, userId, projectId);
            } else {
                response = Response.status(400).entity("User with id " + userId + " could not answer the invitation to the project with id " + projectId).build();
                logger.info("User with session token {} and id {} could not answer the invitation to the project with id {}", token, userId, projectId);
            }
        } catch (Exception e) {
            logger.error("Error while answering the invitation to the project with id {}: {}", projectId, e.getMessage());
            response = Response.status(500).entity("Error while answering the invitation to the project").build();
        }

        return response;

    }
}
