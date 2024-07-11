package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.RecordBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.dto.projectDto.DetailedProject;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/record")
public class RecordService {

    private static final Logger logger = LogManager.getLogger(RecordService.class);

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private RecordBean recordBean;

    @Inject
    private TokenBean tokenBean;
    
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRecord(@HeaderParam("token") String token,
                                 @HeaderParam("id") int userId,
                                 @QueryParam("projectId") int projectId,
                                 String content,
                                 @Context HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        logger.info("User with session token {} is creating a record from IP address {}", token, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to apply to the project, but the ids are invalid", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
            logger.info("User with session token {} tried to edit a project but is not a member of the project", token);
            return Response.status(401).entity("Unauthorized: User is not a member of the project").build();
        }

        if (!authenticationAndAuthorization.ableToEditProject(projectId)) {
            logger.info("User with session token {} tried to apply to the project but the project is not in a state that can be edited", token);
            return Response.status(401).entity("Unauthorized: Project is not in a state that can be edited").build();
        }

        tokenBean.setLastAccessToNow(token);

        Response response;

        DetailedProject project;

        try {
            logger.info("Adding annotation to project with id {}", projectId);
            project = recordBean.addAnnotation(projectId, userId, content);
            if (project == null) {
                logger.info("Annotation not created for project with id {}", projectId);
                response = Response.status(404).entity("Annotation not created").build();
            } else {
                logger.info("Annotation created for project with id {}", projectId);
                response = Response.status(200).entity(project).build();
            }
        } catch (Exception e) {
            logger.error("Error while adding annotation to project with id {}", projectId, e);
            response = Response.status(500).entity("Error while adding annotation").build();
        }

        return response;

    }

}
