package domcast.finalprojbackend.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.MessageBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.SessionTokenDao;
import domcast.finalprojbackend.dto.messageDto.ProjectMessage;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.service.ObjectMapperContextResolver;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

@Singleton
@ServerEndpoint("/websocket/project-chat/{token}/{projectId}")
public class ProjectMessageWS {
    @EJB
    private MessageBean messageBean;

    @EJB
    private UserBean userBean;

    @EJB
    private TokenBean tokenBean;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private DataValidator dataValidator;

    @EJB
    private SessionTokenDao tokenDao;


    private static final Logger logger = LogManager.getLogger(ProjectMessageWS.class);

    private final HashMap<String, Session> sessions = new HashMap<String, Session>();

    /**
     * Sends a message to a project chat
     * @param token the token of the user to send the message to
     * @param msg the message to be sent
     */
    public void send(String token, String msg){
        Session session = sessions.get(token);

        logger.info("Sending message to user with token in project chat for project: {}", token);

        if (session != null){
            try {
                session.getBasicRemote().sendText(msg);
                logger.info("Message sent to user with token in project chat for project: {}", token);
            } catch (IOException e) {
                logger.error("Something went wrong sending message in project chat.");
            }
        }
    }
    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token, @PathParam("projectId") int projectId) {

        logger.info("Opening websocket session with token {} for project {}", token, projectId);

        boolean authenticated;

        try {
            authenticated = dataValidator.isTokenValidForWebSocket(token, sessions);
        } catch (Exception e) {
            logger.error("Error validating token");

            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Invalid token"));
            } catch (IOException ioException) {
                logger.error("Error closing session due to invalid token", ioException);
            }
            return;
        }

        if (!authenticated) {
            logger.error("User not authenticated");
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Not authenticated"));
            } catch (IOException e) {
                logger.error("Error closing session due to authentication failure", e);
            }
            return;
        }

        sessions.put(token, session);

        logger.info("Session added, there are {} sessions open for project {}", sessions.size(), projectId);
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        logger.info("Websocket session is closed for project with id {} with CloseCode: {}: {}", session.getPathParameters().get("projectId"), reason.getCloseCode(), reason.getReasonPhrase());

        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));

        logger.info("Session removed, there still are {} sessions open for project {}", sessions.size(), session.getPathParameters().get("projectId"));
    }

    @OnMessage
    public void toDoOnMessage(Session session, String msg){

        String token = session.getPathParameters().get("token");

        UserEntity userSender;

        try {
            userSender = tokenBean.findUserByToken(token);
        } catch (Exception e) {
            logger.error("Error finding user by token");
            return;
        }

        if (userSender == null) {
            logger.error("User not found by token");
            return;
        }

        int projectId = Integer.parseInt(session.getPathParameters().get("projectId"));
        ProjectEntity projectReceiver;

        try {
            projectReceiver = projectDao.findProjectById(projectId);
        } catch (Exception e) {
            logger.error("Error finding project by id");
            return;
        }

        if (projectReceiver == null) {
            logger.error("Project not found by id");
            return;
        }

        int senderId = userSender.getId();
        String content;

        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(msg, JsonObject.class);
            content = jsonObject.get("content").getAsString();
        } catch (JsonSyntaxException e) {
            logger.error("Error parsing message content");
            return;
        }

        ProjectMessage message;

        try {
            message = messageBean.persistGroupMessage(content, userSender, projectReceiver);
        } catch (Exception e) {
            logger.error("Error persisting message in project chat", e);
            return;
        }

        if (message == null) {
            logger.error("Message not persisted in project chat");
            return;
        }

        ObjectMapperContextResolver contextResolver = new ObjectMapperContextResolver();
        ObjectMapper objectMapper = contextResolver.getContext(null);

        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing message", e);
            return;
        }

        // Send the message to all active sessions of the receiver
        messageBean.sendToProject(projectId, jsonMessage, sessions);
    }
}
