package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.projectDto.DetailedProject;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import domcast.finalprojbackend.dto.userDto.ProjectTeam;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
        Map<DetailedCR, Integer> cRDtos = null;
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

            response = Response.status(201).entity(detailedProject).build();
            logger.info("User with session token {} and id {} created a new project", token, id);
        } catch (Exception e) {
            logger.error("Error creating new project: {}", e.getMessage());
            response = Response.status(500).entity("Error creating new project").build();
        }

        return response;

    }

}
