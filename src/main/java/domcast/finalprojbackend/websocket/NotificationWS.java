package domcast.finalprojbackend.websocket;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.MessageBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.SessionTokenDao;
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
@ServerEndpoint("/websocket/notification/{token}")
public class NotificationWS {
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


    private static final Logger logger = LogManager.getLogger(NotificationWS.class);

    private final HashMap<String, Session> sessions = new HashMap<String, Session>();

    /**
     * Sends notifications to the user
     * @param token the token of the user to send the notification to
     * @param msg the notification to be sent
     */
    public void send(String token, String msg){
        Session session = sessions.get(token);

        logger.info("Sending notification to user with token: {}", token);

        if (session != null){
            try {
                session.getBasicRemote().sendText(msg);
                logger.info("Notification sent to user with token {}", token);
            } catch (IOException e) {
                logger.error("Something went wrong sending notification to user with token {}", token);
            }
        }
    }
    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token, @PathParam("projectId") int projectId) {

        logger.info("Opening websocket session for notifications with token {}: ", token);

        boolean authenticated;

        try {
            authenticated = dataValidator.isTokenValidForWebSocket(token, sessions);
        } catch (Exception e) {
            logger.error("Error validating token for notifications");

            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Invalid token"));
            } catch (IOException ioException) {
                logger.error("Error closing session due to invalid token", ioException);
            }
            return;
        }

        if (!authenticated) {
            logger.error("User not authenticated for notifications");
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Not authenticated"));
            } catch (IOException e) {
                logger.error("Error closing session due to authentication failure", e);
            }
            return;
        }

        sessions.put(token, session);

        logger.info("Session added, there are {} sessions open for notifications", sessions.size());
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason) {
        logger.info("Websocket session closed for notifications with CloseCode: {}: {}", reason.getCloseCode(), reason.getReasonPhrase());

        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));

        logger.info("Session removed, there still are {} sessions open for notifications", sessions.size());
    }

    @OnMessage
    public void toDoOnMessage(Session session, String msg){

        logger.info("A new message is received for notifications: {}", msg);

        String token = session.getPathParameters().get("token");

    }
}
