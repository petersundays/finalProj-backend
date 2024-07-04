package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.PersonalMessageDao;
import domcast.finalprojbackend.dao.ProjectMessageDao;
import domcast.finalprojbackend.dao.SessionTokenDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.messageDto.PersonalMessage;
import domcast.finalprojbackend.dto.messageDto.ProjectMessage;
import domcast.finalprojbackend.dto.userDto.MessageUser;
import domcast.finalprojbackend.entity.PersonalMessageEntity;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.ProjectMessageEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.websocket.PersonalMessageWS;
import domcast.finalprojbackend.websocket.ProjectMessageWS;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.websocket.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Stateless
public class MessageBean implements Serializable {

    @EJB
    private UserDao userDao;

    @EJB
    private UserBean userBean;

    @EJB
    private ProjectMessageDao projectMessageDao;

    @EJB
    private PersonalMessageDao personalMessageDao;

    @EJB
    private SessionTokenDao tokenDao;

    @EJB
    private PersonalMessageWS personalMessageWS;

    @EJB
    private ProjectMessageWS projectMessageWS;

    @EJB
    private DataValidator dataValidator;


    private static final Logger logger = LogManager.getLogger(TaskBean.class);

    private static final long serialVersionUID = 1L;

    public MessageBean() {
    }

    /**
     * Persists a personal message
     * @param content the content of the message
     * @param sender the sender of the message
     * @param receiver the receiver of the message
     * @return the persisted message
     * @throws PersistenceException if an error occurs during the persist operation
     */
    public PersonalMessage persistPersonalMessage(String content, UserEntity sender, UserEntity receiver) {

        if (sender == null || receiver == null) {
            logger.error("Message not sent");
            throw new IllegalArgumentException("Sender or receiver is null");
        }

        if (content == null || content.isEmpty()) {
            logger.error("Message not sent");
            throw new IllegalArgumentException("Content is null or empty");
        }

        logger.info("Sending message from: {} to: {}", sender.getFirstName() + sender.getLastName(), receiver.getFirstName() + receiver.getLastName());

        PersonalMessageEntity messageEntity = new PersonalMessageEntity();
        messageEntity.setContent(content);
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);

        PersonalMessageEntity persistedMessage;

        try {
            persistedMessage = personalMessageDao.persistPersonalMessage(messageEntity);
            logger.info("Message persisted");
        } catch (PersistenceException e) {
            logger.error("Message not persisted");
            throw new PersistenceException("Message not persisted");
        }

        if (persistedMessage == null) {
            logger.error("Message not persisted");
            throw new PersistenceException("Message not persisted");
        }

        return personalMessageEntityToDto(persistedMessage);

    }

    public ProjectMessage persistGroupMessage(String content, UserEntity sender, ProjectEntity receiver) {

        if (sender == null) {
            logger.error("Message not sent");
            throw new IllegalArgumentException("Sender is null");
        }

        if (receiver == null) {
            logger.error("Message not sent to project.");
            throw new IllegalArgumentException("Receiver is null");
        }

        if (content == null || content.isEmpty()) {
            logger.error("Message not sent for project: {}", receiver.getName());
            throw new IllegalArgumentException("Content is null or empty");
        }

        logger.info("Sending message from: {} to project: {}", sender.getFirstName() + sender.getLastName(), receiver.getName());

        ProjectMessageEntity messageEntity = new ProjectMessageEntity();

        messageEntity.setContent(content);
        messageEntity.setSender(sender);
        messageEntity.setProject(receiver);
        messageEntity.setTimestamp(LocalDateTime.now());

        ProjectMessageEntity persistedMessage;

        try {
            persistedMessage = projectMessageDao.persistProjectMessage(messageEntity);
            logger.info("Message persisted for project: {}", receiver.getName());
        } catch (PersistenceException e) {
            logger.error("Message not persisted for project: {}", receiver.getName());
            throw new PersistenceException("Message not persisted");
        }

        if (persistedMessage == null) {
            logger.error("Message not persisted for project: {}", receiver.getName());
            throw new PersistenceException("Message not persisted");
        }

        return projectMessageEntityToDto(persistedMessage);
    }

    public PersonalMessage personalMessageEntityToDto (PersonalMessageEntity messageEntity) {

        logger.info("Converting personal message entity to DTO");

        if (messageEntity == null) {
            logger.error("Message entity is null");
            throw new IllegalArgumentException("Message entity is null");
        }

        MessageUser sender = userBean.entityToMessageUser(messageEntity.getSender());
        MessageUser receiver = userBean.entityToMessageUser(messageEntity.getReceiver());

        return new PersonalMessage(
                messageEntity.getId(),
                messageEntity.getContent(),
                sender,
                receiver,
                messageEntity.getTimestamp()
        );
    }

    public ProjectMessage projectMessageEntityToDto (ProjectMessageEntity messageEntity) {

        logger.info("Converting project message entity to DTO");

        if (messageEntity == null) {
            logger.error("Project message entity is null");
            throw new IllegalArgumentException("Project message entity is null");
        }

        MessageUser sender = userBean.entityToMessageUser(messageEntity.getSender());

        return new ProjectMessage(
                messageEntity.getId(),
                messageEntity.getContent(),
                sender,
                messageEntity.getProject().getId(),
                messageEntity.getTimestamp());
    }


    public void sendToUser(int userId, String jsonMessage, HashMap<String, Session> sessions) {

        if (!dataValidator.isIdValid(userId)) {
            logger.error("Invalid user id");
            throw new IllegalArgumentException("Invalid user id");
        }

        if (jsonMessage == null || jsonMessage.isEmpty()) {
            logger.error("Invalid message");
            throw new IllegalArgumentException("Invalid message");
        }

        if (sessions == null || sessions.isEmpty()) {
            logger.error("No active sessions");
            throw new IllegalArgumentException("No active sessions");
        }

        List<String> activeSessionTokens;
        try {
            activeSessionTokens = tokenDao.findActiveSessionTokensByUserId(userId);
            for (String token : activeSessionTokens) {
                Session session = sessions.get(token);
                if (session != null) {
                    personalMessageWS.send(token, jsonMessage);
                }
            }
            if (activeSessionTokens.isEmpty()) {
                logger.info("Message receiver is not online");
            }
        } catch (NoResultException e) {
            logger.info("No session tokens found for user with id: {}", userId);
        } catch (Exception e) {
            logger.error("Error finding session tokens by user with id: {}", userId, e);
        }
    }

    public void sendToProject(int projectId, String jsonMessage, HashMap<String, Session> sessions) {

        if (!dataValidator.isIdValid(projectId)) {
            logger.error("Invalid project id");
            throw new IllegalArgumentException("Invalid project id");
        }

        if (jsonMessage == null || jsonMessage.isEmpty()) {
            logger.error("Invalid message while sending message to project");
            throw new IllegalArgumentException("Invalid message");
        }

        if (sessions == null || sessions.isEmpty()) {
            logger.error("No active project chat sessions");
            throw new IllegalArgumentException("No active sessions");
        }

        sessions.forEach((token, session) -> {
            String sessionProjectId = session.getPathParameters().get("projectId");
            if (sessionProjectId != null && Integer.parseInt(sessionProjectId) == projectId) {
                projectMessageWS.send(token, jsonMessage);
            }
        });
    }
}
