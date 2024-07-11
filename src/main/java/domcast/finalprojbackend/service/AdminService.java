package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.SystemBean;
import domcast.finalprojbackend.bean.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.TokenBean;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AdminService class that handles admin related operations.
 */
@Path("/admin")
public class AdminService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private TokenBean tokenBean;
    @Inject
    private SystemBean systemBean;

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    /**
     * Updates the session timeout.
     * @param token the session token of the admin user
     * @param timeout the new session timeout to set
     * @return a response indicating the result of the operation
     */
    @PUT
    @Path("/session-timeout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSessionTimeout(@HeaderParam("token") String token, @HeaderParam("timeout") int timeout, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to set session timeout", ipAddress);

        Response response;


        try {
            if (tokenBean.isSessionTokenFromAdminTypeUser(token)) {

                tokenBean.setLastAccessToNow(token);

                if (systemBean.setSessionTimeout(timeout)) {
                    response = Response.status(200).entity("Session timeout set successfully").build();
                    logger.info("Admin set session timeout successfully");
                } else {
                    response = Response.status(400).entity("Error setting session timeout").build();
                    logger.info("Admin tried to set session timeout unsuccessfully");
                }
            } else {
                response = Response.status(401).entity("Unauthorized").build();
                logger.info("Unauthorized user tried to set session timeout");
            }
        } catch (Exception e) {
            response = Response.status(500).entity("Internal server error").build();
            logger.error("Error setting session timeout", e);
        }

        return response;
    }

    @PUT
    @Path("/project-max-members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setProjectMaxUsers(@HeaderParam("token") String token, @HeaderParam("maxMembers") int maxMembers, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to set project max members", ipAddress);

        Response response;

        try {
            if (tokenBean.isSessionTokenFromAdminTypeUser(token)) {

                tokenBean.setLastAccessToNow(token);

                if (systemBean.setProjectMaxMembers(maxMembers)) {
                    response = Response.status(200).entity("Project max members set successfully").build();
                    logger.info("Admin set project max members successfully");
                } else {
                    response = Response.status(400).entity("Error setting project max members").build();
                    logger.info("Admin tried to set project max members unsuccessfully");
                }
            } else {
                response = Response.status(401).entity("Unauthorized").build();
                logger.info("Unauthorized admin tried to set project max members");
            }
        } catch (Exception e) {
            response = Response.status(500).entity("Internal server error").build();
            logger.error("Error setting project max members", e);
        }

        return response;
    }

    @GET
    @Path("/session-timeout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSessionTimeout(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to get session timeout", ipAddress);

        Response response;

        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("Unauthorized user tried to get session timeout");
            return response;
        }

        if (!authenticationAndAuthorization.isUserAdminById(id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User is not an admin and tried to get session timeout");
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        int timeout;

        try {
            timeout = systemBean.getSessionTimeout();
            response = Response.status(200).entity(timeout).build();
            logger.info("Session timeout retrieved successfully");
        } catch (Exception e) {
            response = Response.status(500).entity("Internal server error").build();
            logger.error("Error getting session timeout", e);
        }

        return response;
    }

    @GET
    @Path("/max-members")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectMaxMembers(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to get project max members", ipAddress);

        Response response;

        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("Unauthorized user tried to get project max members");
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        int maxMembers;

        try {
            maxMembers = systemBean.getProjectMaxUsers();
            response = Response.status(200).entity(maxMembers).build();
            logger.info("Project max members retrieved successfully");
        } catch (Exception e) {
            response = Response.status(500).entity("Internal server error").build();
            logger.error("Error getting project max members", e);
        }

        return response;
    }

    @GET
    @Path("/statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatistics(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to get statistics", ipAddress);

        Response response;

        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("Unauthorized user tried to get statistics");
            return response;
        }

        if (!authenticationAndAuthorization.isUserAdminById(id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User is not an admin and tried to get statistics");
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        try {
            response = Response.status(200).entity(systemBean.projectStatistics()).build();
            logger.info("Statistics retrieved successfully");
        } catch (Exception e) {
            response = Response.status(500).entity("Internal server error").build();
            logger.error("Error getting statistics", e);
        }

        return response;
    }

}
