package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dto.componentResourceDto.CRPreview;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/component-resource")
public class ComponentResourceService {

    private static final Logger logger = LogManager.getLogger(ComponentResourceService.class);

    @Inject
    private ComponentResourceBean componentResourceBean;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;


    /**
     * Creates a new component resource based on the detailed component resource passed as parameter, independently of the project.
     * The method validates the data, registers the data in the database and creates a many-to-many relation between
     * the component resource and the project.
     * The method also converts the detailed component resource to a preview component resource.
     * @param detailedCR the detailed component resource to be created.
     * @return the preview component resource if created successfully, null otherwise.
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComponentResource(@HeaderParam("token") String token, @HeaderParam("id") int userId, DetailedCR detailedCR, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is trying to create a component-resource from IP address {}", token, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId)) {
            logger.info("User with session token {} tried to create a component-resource unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to create the component-resource for this project
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId)) {
            logger.info("User with session token {} tried tochange the state of a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        CRPreview crPreview;

        try {
            crPreview = componentResourceBean.createComponentResource(detailedCR);
            if (crPreview == null) {
                logger.error("Error creating component resource");
                return Response.status(500).entity("Error creating component resource").build();
            }
            response = Response.status(201).entity(crPreview).build();
        } catch (PersistenceException e) {
            logger.error("Error creating component resource due to persistence issue", e);
            response = Response.status(500).entity("Error creating component resource. Please try again later").build();
        } catch (Exception e) {
            logger.error("Error creating component resource", e);
            response = Response.status(500).entity("Error creating component resource. Please try again later").build();
        }

        return response;
    }

    /**
     * Creates a new component resource based on the detailed component resource passed as parameter.
     * The method validates the data, registers the data in the database and creates a many-to-many relation between
     * the component resource and the project.
     * The method also converts the detailed component resource to a preview component resource.
     * @param detailedCR the detailed component resource to be created.
     * @param projectId the id of the project where the component resource will be created.
     * @return the preview component resource if created successfully, null otherwise.
     */
    @POST
    @Path("/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createComponentResourceInProject(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("projectId") int projectId, DetailedCR detailedCR, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is trying to create a component-resource, for project with id , from IP address {}", token, /*projectId,*/ ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) && !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to create a component-resource, for project with id {}, unsuccessfully", token, projectId);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to create the component-resource for this project
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProject(userId, projectId)) {
            logger.info("User with session token {} tried to change the state of a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        CRPreview crPreview;

        try {
            crPreview = componentResourceBean.createComponentResource(detailedCR, projectId, detailedCR.getQuantity());
            if (crPreview == null) {
                logger.error("Error creating component resource for project with id {}", projectId);
                return Response.status(500).entity("Error creating component resource").build();
            }
            response = Response.status(201).entity(crPreview).build();
        } catch (PersistenceException e) {
            logger.error("Error creating component resource due to persistence issue", e);
            response = Response.status(500).entity("Error creating component resource. Please try again later").build();
        } catch (Exception e) {
            logger.error("Error creating component resource", e);
            response = Response.status(500).entity("Error creating component resource. Please try again later").build();
        }

        return response;
    }
}
