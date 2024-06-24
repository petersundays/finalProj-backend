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
}
