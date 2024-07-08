package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.ComponentResourceBean;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.project.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dto.componentResourceDto.CRPreview;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.userDto.EnumDTO;
import domcast.finalprojbackend.enums.ComponentResourceEnum;
import domcast.finalprojbackend.enums.util.EnumUtil;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

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
     *
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
     *
     * @param detailedCR the detailed component resource to be created.
     * @param projectId  the id of the project where the component resource will be created.
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
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
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

    /**
     * Edits a component resource based on the detailed component resource passed as parameter.
     * The method validates the data, registers the data in the database and returns the edited component resource.
     *
     * @param detailedCR the detailed component resource to be edited.
     * @param cRId       component resource's id to be edited.
     * @return the detailed component resource if edited successfully, null otherwise.
     */
    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editComponentResource(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("id") int cRId, DetailedCR detailedCR, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is trying to edit a component-resource with id {} from IP address {}", token, cRId, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) && !dataValidator.isIdValid(cRId)) {
            logger.info("User with session token {} tried to edit a component-resource with id {} unsuccessfully", token, cRId);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to edit the component-resource
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId)) {
            logger.info("User with session token {} tried to edit a component-resource with id {} unsuccessfully", token, cRId);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        DetailedCR editedCR;

        try {
            editedCR = componentResourceBean.editComponentResource(detailedCR, cRId);
            if (editedCR == null) {
                logger.error("Error editing component resource with id {}", cRId);
                return Response.status(500).entity("Error editing component resource").build();
            }
            response = Response.status(200).entity(editedCR).build();
        } catch (PersistenceException e) {
            logger.error("Error editing component resource with id {} due to persistence issue", cRId, e);
            response = Response.status(500).entity("Error editing component resource. Please try again later").build();
        } catch (Exception e) {
            logger.error("Error editing component resource with id {}", cRId, e);
            response = Response.status(500).entity("Error editing component resource. Please try again later").build();
        }

        return response;
    }

    @PUT
    @Path("/project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editComponentResourceInProject(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("id") int cRId, @QueryParam("projectId") int projectId, DetailedCR detailedCR, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is trying to edit a component-resource with id {} for project with id {} from IP address {}", token, cRId, projectId, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) && !dataValidator.isIdValid(cRId) && !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to edit a component-resource with id {} for project with id {} unsuccessfully", token, cRId, projectId);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to edit the component-resource
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
            logger.info("User with session token {} tried to edit a component-resource with id {} for project with id {} unsuccessfully", token, cRId, projectId);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        DetailedCR editedCR;

        try {
            editedCR = componentResourceBean.editComponentResource(detailedCR, cRId, projectId);
            if (editedCR == null) {
                logger.error("Error editing component resource with id {} for project with id {}", cRId, projectId);
                return Response.status(500).entity("Error editing component resource").build();
            }
            response = Response.status(200).entity(editedCR).build();
        } catch (PersistenceException e) {
            logger.error("Error editing component resource with id {} for project with id {} due to persistence issue", cRId, projectId, e);
            response = Response.status(500).entity("Error editing component resource. Please try again later").build();
        } catch (Exception e) {
            logger.error("Error editing component resource with id {} for project with id {}", cRId, projectId, e);
            response = Response.status(500).entity("Error editing component resource. Please try again later").build();
        }

        return response;
    }

    @GET
    @Path("project")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComponentResourcesByProjectId(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("projectId") int projectId, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is trying to get component-resources for project with id {} from IP address {}", token, projectId, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId) && !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to get component-resources for project with id {} unsuccessfully", token, projectId);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component-resources for this project
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
            logger.info("User with session token {} tried to get component-resources for project with id {} unsuccessfully", token, projectId);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        Set<CRPreview> crPreviews;

        try {
            crPreviews = componentResourceBean.getComponentResourcesByProjectId(projectId);
            if (crPreviews == null) {
                logger.error("Error getting component resources for project with id {}", projectId);
                return Response.status(500).entity("Error getting component resources").build();
            }
            response = Response.status(200).entity(crPreviews).build();
        } catch (PersistenceException e) {
            logger.error("Error getting component resources for project with id {} due to persistence issue", projectId, e);
            response = Response.status(500).entity("Error getting component resources. Please try again later").build();
        } catch (Exception e) {
            logger.error("Error getting component resources for project with id {}", projectId, e);
            response = Response.status(500).entity("Error getting component resources. Please try again later").build();
        }

        return response;
    }

    @GET
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComponentResourcesByCriteria(@HeaderParam("token") String token,
                                                    @HeaderParam("id") int userId,
                                                    @QueryParam("name") String name,
                                                    @QueryParam("brand") String brand,
                                                    @QueryParam("partNumber") long partNumber,
                                                    @QueryParam("supplier") String supplier,
                                                    @QueryParam("orderBy") String orderBy,
                                                    @QueryParam("orderAsc") boolean orderAsc,
                                                    @QueryParam("pageNumber") int pageNumber,
                                                    @QueryParam("pageSize") int pageSize,
                                                    @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is trying to get component-resources from IP address {}", token, ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(userId)) {
            logger.info("User with session token {} tried to get component-resources unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component-resources by criteria
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId)) {
            logger.info("User with session token {} tried to get component-resources unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        List<CRPreview> crPreviews;

        try {
            crPreviews = componentResourceBean.getComponentResourcesByCriteria(name, brand, partNumber, supplier, orderBy, orderAsc, pageNumber, pageSize);
            if (crPreviews == null) {
                logger.error("Error getting component resources with name: {}, brand: {}, partNumber: {}, supplier: {}, orderBy: {}, orderAsc: {}, pageNumber: {}, pageSize: {}", name, brand, partNumber, supplier, orderBy, orderAsc, pageNumber, pageSize);
                return Response.status(500).entity("Error getting component resources").build();
            }
            response = Response.status(200).entity(crPreviews).build();
        } catch (PersistenceException e) {
            logger.error("Error getting component resources with name: {}, brand: {}, partNumber: {}, supplier: {}, orderBy: {}, orderAsc: {}, pageNumber: {}, pageSize: {}", name, brand, partNumber, supplier, orderBy, orderAsc, pageNumber, pageSize, e);
            response = Response.status(500).entity("Error getting component resources. Please try again later").build();
        } catch (Exception e) {
            logger.error("Error getting component resources with name: {}, brand: {}, partNumber: {}, supplier: {}, orderBy: {}, orderAsc: {}, pageNumber: {}, pageSize: {}", name, brand, partNumber, supplier, orderBy, orderAsc, pageNumber, pageSize, e);
            response = Response.status(500).entity("Error getting component resources. Please try again later").build();
        }

        return response;
    }

    @GET
    @Path("/enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComponentResourceEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the component resource enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the component resource enum unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the component resource enum unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the component resource enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(ComponentResourceEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the component resource enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting component resource enum", e);
            response = Response.status(500).entity("Error getting component resource enum").build();
        }

        return response;
    }
}
