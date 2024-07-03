package domcast.finalprojbackend.websocket;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.MessageBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.SessionTokenDao;
import domcast.finalprojbackend.dao.UserDao;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
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
    private UserDao userDao;

    @EJB
    private DataValidator dataValidator;

    @EJB
    private SessionTokenDao tokenDao;


    private static final Logger logger = LogManager.getLogger(ProjectMessageWS.class);

    private final HashMap<String, Session> sessions = new HashMap<String, Session>();

    /**
     * Sends a message to a project chat
     * @param id the id of the project to send the message to
     * @param msg the message to be sent
     */
    public void send(int id, String msg){
        Session session = sessions.get(id);

        logger.info("Sending message to project: {}", id);

        if (session != null){
            try {
                session.getBasicRemote().sendText(msg);
                logger.info("Message sent to project: {}", id);
            } catch (IOException e) {
                logger.error("Something went wrong!");
            }
        }
    }
    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("projectId") String token) {

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

    /*@OnMessage
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

        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(msg, JsonObject.class);
            content = jsonObject.get("content").getAsString();
        } catch (JsonSyntaxException e) {
            logger.error("Error parsing message content");
            return;
        }

        String receiverToken = "";

        try {
            receiverToken = tokenDao.findSessionTokenByUserId(receiverId);
        } catch (NoResultException e) {
            logger.error("No session token found for user id {}", receiverId, e);
        } catch (NonUniqueResultException e) {
            logger.error("Multiple session tokens found for user id {}", receiverId, e);
        } catch (Exception e) {
            logger.error("Error finding token by user id", e);
        }

        boolean receiverOnline = false;

        if (receiverToken != null && !receiverToken.isEmpty()) {

            for (String key : sessions.keySet()) {
                if (key.equals(receiverToken)) {
                    Session receiverSession = sessions.get(key);
                    int receiverUsername = Integer.parseInt(receiverSession.getPathParameters().get("receiver"));
                    if (receiverUsername == senderId) {
                        receiverOnline = true;
                    }
                }
            }
        }

        PersonalMessage message;

        try {
            message = messageBean.persistPersonalMessage(content, userSender, userReceiver);
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

        if (receiverOnline) {
            send(receiverToken, new Gson().toJson(message));
        }

        for (String key : sessions.keySet()) {
            if (key.equals(token)) {
                send(token, jsonMessage);
            }
        }
    }*/
}
