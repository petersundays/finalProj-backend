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
import domcast.finalprojbackend.dao.SessionTokenDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.messageDto.PersonalMessage;
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
@ServerEndpoint("/websocket/messages/{token}/{receiver}")
public class PersonalMessageWS {
    @EJB
    private MessageBean messageBean;

    @EJB
    private UserBean userBean;

    @EJB
    private TokenBean tokenBean;

    @EJB
    private UserDao userDao;

    @EJB
    private DataValidator dataValidator;

    @EJB
    private SessionTokenDao tokenDao;


    private static final Logger logger = LogManager.getLogger(PersonalMessageWS.class);

    private final HashMap<String, Session> sessions = new HashMap<String, Session>();

    /**
     * Sends a message to a user
     * @param token the token of the user to send the message to
     * @param msg the message to be sent
     */
    public void send(String token, String msg){
        Session session = sessions.get(token);

        logger.info("Sending message to: {}", token);

        if (session != null){
            try {
                session.getBasicRemote().sendText(msg);
                logger.info("Message sent to: {}", token);
            } catch (IOException e) {
                logger.error("Something went wrong!");
            }
        }
    }
    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token){

        logger.info("Opening websocket session with token: {}", token);

        boolean authenticated;

        try {
            authenticated = dataValidator.isTokenValidForWebSocket(token, sessions);
        } catch (Exception e) {
            logger.error("Error validating token");

            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid token"));
            } catch (IOException ioException) {
                logger.error("Error closing session due to invalid token", ioException);
            }
            return;
        }

        if (!authenticated) {
            logger.error("User not authenticated");
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not authenticated"));
            } catch (IOException e) {
                logger.error("Error closing session due to authentication failure", e);
            }
            return;
        }

        sessions.put(token, session);

        logger.info("Session added, there are {} sessions open", sessions.size());
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        logger.info("Websocket session is closed with CloseCode: {}: {}", reason.getCloseCode(), reason.getReasonPhrase());

        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));

        logger.info("Session removed, there still are {} sessions open", sessions.size());
    }

    @OnMessage
    public void toDoOnMessage(Session session, String msg) {

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

        int receiverId = Integer.parseInt(session.getPathParameters().get("receiver"));
        UserEntity userReceiver;

        try {
            userReceiver = userDao.findUserById(receiverId);
        } catch (Exception e) {
            logger.error("Error finding user by id");
            return;
        }

        if (userReceiver == null) {
            logger.error("User not found by id");
            return;
        }

        int senderId = userSender.getId();
        String content;
        String subject;

        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(msg, JsonObject.class);
            subject = jsonObject.get("subject").getAsString();
            content = jsonObject.get("content").getAsString();
        } catch (JsonSyntaxException e) {
            logger.error("Error parsing message content");
            return;
        }


        PersonalMessage message;

        try {
            message = messageBean.persistPersonalMessage(subject, content, userSender, userReceiver);
        } catch (Exception e) {
            logger.error("Error persisting message");
            return;
        }

        if (message == null) {
            logger.error("Message not persisted");
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

        // Send the message to all active sessions of the sender
        messageBean.sendToUser(senderId, jsonMessage, sessions);

        // Send the message to all active sessions of the receiver
        messageBean.sendToUser(receiverId, jsonMessage, sessions);
    }
}
