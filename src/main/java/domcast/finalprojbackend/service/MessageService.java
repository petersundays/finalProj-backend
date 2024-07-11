package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.MessageBean;
import domcast.finalprojbackend.bean.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.dto.messageDto.PersonalMessage;
import domcast.finalprojbackend.dto.messageDto.ProjectMessage;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Path("/message")
public class MessageService {

    private static final Logger logger = LogManager.getLogger(MessageService.class);

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private MessageBean messageBean;

    @Inject
    private TokenBean tokenBean;

    @GET
    @Path("personal-received")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonalReceivedMessages(@HeaderParam("token") String token,
                                @HeaderParam("id") int id,
                                @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} is trying to get received messages", ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get received messages but has an invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        Response response;

        // Check if the user is authorized to get the unread messages
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get received messages but is unauthorized", token);
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        List<PersonalMessage> messages;

        try {
            messages = messageBean.getAllPersonalMessagesWhereReceiverIs(id);
            logger.info("User with ip address {} got his received messages", ipAddress);
            response = Response.status(200).entity(messages).build();
        } catch (Exception e) {
            logger.error("Error while getting personal received messages: {}", e.getMessage());
            response = Response.status(500).entity("Error while getting personal messages").build();
        }

        return response;
    }

    @GET
    @Path("personal-sent")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonalSentMessages(@HeaderParam("token") String token,
                                        @HeaderParam("id") int id,
                                        @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} is trying to get his sent messages", ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get his sent messages but has an invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        Response response;

        // Check if the user is authorized to get the unread messages
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get sent messages but is unauthorized", token);
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        List<PersonalMessage> messages;

        try {
            messages = messageBean.getAllPersonalMessagesSentByUser(id);
            logger.info("User with ip address {} got his sent messages", ipAddress);
            response = Response.status(200).entity(messages).build();
        } catch (Exception e) {
            logger.error("Error while getting personal sent messages: {}", e.getMessage());
            response = Response.status(500).entity("Error while getting personal sent messages").build();
        }

        return response;
    }

    @GET
    @Path("project")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectMessages(@HeaderParam("token") String token,
                                        @HeaderParam("id") int id,
                                        @QueryParam("project") int projectId,
                                        @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} is trying to get messages for a project", ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(id) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to get messages for project with id {} but has an invalid id", token, projectId);
            return Response.status(400).entity("Invalid id").build();
        }

        Response response;

        // Check if the user is authorized to get the unread messages
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get messages for project with id {} but is unauthorized", token, projectId);
            return response;
        }

        // Check if the user is a member of the project
        if (!authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(id, projectId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get the project's messages for project with id {} but is not a member of the project", projectId, token);
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        List<ProjectMessage> messages;

        try {
            messages = messageBean.getAllProjectMessagesWhereProjectIs(projectId);
            logger.info("User with ip address {} got the project's messages for project with id {}", ipAddress, projectId);
            response = Response.status(200).entity(messages).build();
        } catch (Exception e) {
            logger.error("Error while getting project messages for project with id {}: {}", projectId, e.getMessage());
            response = Response.status(500).entity("Error while getting project messages").build();
        }

        return response;
    }

    @GET
    @Path("/count-personal-unread")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countUnreadPersonalMessages(@HeaderParam("token") String token,
                                     @HeaderParam("id") int id,
                                     @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} is trying to get unread messages", ipAddress);

        // Check if the user's is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get unread messages but has an invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        Response response;

        // Check if the user is authorized to get the unread messages
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get unread messages but is unauthorized", token);
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        boolean hasUnreadMessages;

        try {
            hasUnreadMessages = messageBean.countUnreadPersonalMessagesForUser(id, token);
            logger.info("User with ip address {} got the count of unread messages", ipAddress);
            response = Response.status(200).entity(hasUnreadMessages).build();
        } catch (Exception e) {
            logger.error("Error while counting unread messages: {}", e.getMessage());
            response = Response.status(500).entity("Error while counting unread messages").build();
        }

        return response;
    }

    @GET
    @Path("/count-project-unread")
    @Produces(MediaType.APPLICATION_JSON)
    public Response countUnreadProjectMessages(@HeaderParam("token") String token,
                                      @HeaderParam("id") int id,
                                      @QueryParam("project") int projectId,
                                      @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} is trying to get unread messages for a project", ipAddress);

        // Check if the user's and project's ids are valid
        if (!dataValidator.isIdValid(id) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to get unread messages for project with id {} but has an invalid id", token, projectId);
            return Response.status(400).entity("Invalid id").build();
        }

        Response response;

        // Check if the user is authorized to get the unread messages
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get unread messages for project with id {} but is unauthorized", token, projectId);
            return response;
        }

        // Check if the user is a member of the project
        if (!authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(id, projectId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get the project but is not a member of the project", token);
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        boolean hasUnreadMessages;

        try {
            hasUnreadMessages = messageBean.countUnreadProjectMessagesForUser(id, projectId, token);
            logger.info("User with ip address {} got the count of unread messages for project with id {}", ipAddress, projectId);
            response = Response.status(200).entity(hasUnreadMessages).build();
        } catch (Exception e) {
            logger.error("Error while counting unread messages for project with id {}: {}", projectId, e.getMessage());
            response = Response.status(500).entity("Error while counting unread messages").build();
        }

        return response;
    }

    @PUT
    @Path("/mark-personal-read")
    @Produces(MediaType.APPLICATION_JSON)
    public Response markPersonalMessageAsRead(@HeaderParam("token") String token,
                                              @HeaderParam("id") int id,
                                              @QueryParam("message") int messageId,
                                              @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} is trying to mark a personal message as read", ipAddress);

        Response response;

        // Check if the user's and message's ids are valid
        if (!dataValidator.isIdValid(id) || !dataValidator.isIdValid(messageId)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to mark a personal message as read but has an invalid id", token);
            return response;
        }

        // Check if the user is correctly authenticated and authorized
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to mark a personal message as read but is unauthorized", token);
            return response;
        }

        if (!authenticationAndAuthorization.isUserReceiverOfPersonalMessage(id, messageId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to mark a personal message as read but is not the receiver of the message", token);
            return response;
        }

        tokenBean.setLastAccessToNow(token);

        boolean markedAsRead;

        try {
            markedAsRead = messageBean.markPersonalMessageAsRead(messageId);
            logger.info("User with ip address {} marked a personal message as read", ipAddress);
            response = Response.status(200).entity(markedAsRead).build();
        } catch (Exception e) {
            logger.error("Error while marking a personal message as read: {}", e.getMessage());
            response = Response.status(500).entity("Error while marking a personal message as read").build();
        }

        return response;
    }
}
