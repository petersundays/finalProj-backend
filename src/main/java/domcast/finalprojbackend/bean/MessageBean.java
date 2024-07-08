package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.*;
import domcast.finalprojbackend.dto.messageDto.PersonalMessage;
import domcast.finalprojbackend.dto.messageDto.ProjectMessage;
import domcast.finalprojbackend.dto.userDto.MessageUser;
import domcast.finalprojbackend.entity.PersonalMessageEntity;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.ProjectMessageEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.websocket.NotificationWS;
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
import java.util.*;

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

    @EJB
    private NotificationWS notificationWS;

    @EJB
    private M2MProjectUserDao m2MProjectUserDao;


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
    public PersonalMessage persistPersonalMessage(String subject, String content, UserEntity sender, UserEntity receiver) {

        if (sender == null || receiver == null) {
            logger.error("Personal message not sent");
            throw new IllegalArgumentException("Sender or receiver is null");
        }

        if (subject == null) {
            logger.info("Subject is null, setting to empty string");
            subject = "";
        }

        if (content == null || content.isEmpty()) {
            logger.error("Personal message not sent");
            throw new IllegalArgumentException("Content is null or empty");
        }

        logger.info("Sending personal message from: {} to: {}", sender.getFirstName() + sender.getLastName(), receiver.getFirstName() + receiver.getLastName());

        PersonalMessageEntity messageEntity = new PersonalMessageEntity();
        messageEntity.setSubject(subject);
        messageEntity.setContent(content);
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);

        PersonalMessageEntity persistedMessage;

        try {
            persistedMessage = personalMessageDao.persistPersonalMessage(messageEntity);
            logger.info("Personal message persisted");
        } catch (PersistenceException e) {
            logger.error("Personal message not persisted");
            throw new PersistenceException("Personal message not persisted");
        }

        if (persistedMessage == null) {
            logger.error("Personal message not persisted");
            throw new PersistenceException("Personal message not persisted");
        }

        return personalMessageEntityToDto(persistedMessage);

    }

    /**
     * Persists a group message
     * @param content the content of the message
     * @param sender the sender of the message
     * @param receiver the receiver of the message
     * @return the persisted message
     * @throws PersistenceException if an error occurs during the persist operation
     */
    public ProjectMessage persistGroupMessage(String content, UserEntity sender, ProjectEntity receiver) {

        if (sender == null) {
            logger.error("Group message not sent");
            throw new IllegalArgumentException("Sender is null");
        }

        if (receiver == null) {
            logger.error("Group message not sent to project.");
            throw new IllegalArgumentException("Receiver is null");
        }

        if (content == null || content.isEmpty()) {
            logger.error("Group message not sent for project: {}", receiver.getName());
            throw new IllegalArgumentException("Content is null or empty");
        }

        logger.info("Sending group message from: {} to project: {}", sender.getFirstName() + sender.getLastName(), receiver.getName());

        ProjectMessageEntity messageEntity = new ProjectMessageEntity();

        messageEntity.setContent(content);
        messageEntity.setSender(sender);
        messageEntity.setProject(receiver);
        messageEntity.setTimestamp(LocalDateTime.now());

        ProjectMessageEntity persistedMessage;

        try {
            persistedMessage = projectMessageDao.persistProjectMessage(messageEntity);
            logger.info("Group message persisted for project: {}", receiver.getName());
        } catch (PersistenceException e) {
            logger.error("Group message not persisted for project: {}", receiver.getName());
            throw new PersistenceException("Group message not persisted");
        }

        if (persistedMessage == null) {
            logger.error("Group message not persisted for project: {}", receiver.getName());
            throw new PersistenceException("Group message not persisted");
        }

        return projectMessageEntityToDto(persistedMessage);
    }

    /**
     * Converts a personal message entity to a DTO
     * @param messageEntity the entity to be converted
     * @return the DTO
     */
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
                messageEntity.getSubject(),
                messageEntity.getContent(),
                sender,
                receiver,
                messageEntity.getTimestamp()
        );
    }

    /**
     * Converts a project message entity to a DTO
     * @param messageEntity the entity to be converted
     * @return the DTO
     */
    public ProjectMessage projectMessageEntityToDto (ProjectMessageEntity messageEntity) {

        logger.info("Converting project message entity to DTO");

        if (messageEntity == null) {
            logger.error("Project message entity is null");
            throw new IllegalArgumentException("Project message entity is null");
        }

        MessageUser sender = userBean.entityToMessageUser(messageEntity.getSender());

        ProjectMessage projectMessage = new ProjectMessage();
        projectMessage.setId(messageEntity.getId());
        projectMessage.setContent(messageEntity.getContent());
        projectMessage.setSender(sender);
        projectMessage.setProjectId(messageEntity.getProject().getId());
        projectMessage.setTimestamp(messageEntity.getTimestamp());

        return projectMessage;
    }

    /**
     * Sends a message to a user
     * @param userId the id of the user to send the message to
     * @param jsonMessage the message to be sent
     * @param sessions the active sessions
     */
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

    /**
     * Sends a message to a project
     * @param projectId the id of the project to send the message to
     * @param jsonMessage the message to be sent
     * @param sessions the active sessions
     */
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

    /**
     * Sends a notification to the user
     * @param userId the id of the user to send the notification to
     * @param sessions the active sessions
     */
    public void sendNotification(int userId, HashMap<String, Session> sessions) {

        logger.info("Entering sendNotification method");

        if (!dataValidator.isIdValid(userId)) {
            logger.error("Invalid user id while sending notification");
            throw new IllegalArgumentException("Invalid user id");
        }

        if (sessions == null || sessions.isEmpty()) {
            logger.error("No active sessions while sending notification");
            throw new IllegalArgumentException("No active sessions");
        }

        List<String> activeSessionTokens;
        try {
            activeSessionTokens = tokenDao.findActiveSessionTokensByUserId(userId);
            for (String token : activeSessionTokens) {
                Session session = sessions.get(token);
                if (session != null) {
                    notificationWS.send(token, NotificationWS.NOTIFICATION);
                }
            }
            if (activeSessionTokens.isEmpty()) {
                logger.info("Notification receiver is not online");
            }
        } catch (NoResultException e) {
            logger.info("No session tokens found for user with id: {} while sending notification.", userId);
        } catch (Exception e) {
            logger.error("Error finding session tokens by user with id: {} while sending notification.", userId, e);
        }
    }

    /**
     * Counts the number of unread personal messages for a user
     * @param userId the id of the user
     * @param token the token of the user
     * throws IllegalArgumentException if the user id or token is invalid
     *              or if an error occurs during the count operation
     */
    public boolean countUnreadPersonalMessagesForUser(int userId, String token) {

        if (!dataValidator.isIdValid(userId)) {
            logger.error("Invalid user id while counting unread personal messages");
            throw new IllegalArgumentException("Invalid user id");
        }

        if (token == null || token.isEmpty()) {
            logger.error("Invalid token while counting unread personal messages");
            throw new IllegalArgumentException("Invalid token");
        }

        logger.info("Counting unread personal messages for user with id: {}", userId);

        boolean hasUnreadMessages = false;
        int unreadMessages = 0;

        try {
            unreadMessages = personalMessageDao.countUnreadPersonalMessagesForUser(userId);
        } catch (Exception e) {
            logger.error("Error counting unread personal messages for user with id: {}", userId, e);
        }

        if (unreadMessages < 0) {
            logger.error("Error counting unread personal messages for user with id: {}", userId);
            throw new IllegalArgumentException("Error counting unread personal messages");
        }

        if (unreadMessages > 0) {
            notificationWS.send(token, NotificationWS.NOTIFICATION);
            hasUnreadMessages = true;
        }

        logger.info("User with id: {} has {} unread personal messages", userId, unreadMessages);
        return hasUnreadMessages;
    }

    /**
     * Counts the number of unread project messages for a user, for a specific project
     * @param userId the id of the user
     * @param token the token of the user
     * throws IllegalArgumentException if the user id or token is invalid
     *              or if an error occurs during the count operation
     */
    public boolean countUnreadProjectMessagesForUser(int userId, int projectId, String token) {

        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId)) {
            logger.error("Invalid user id or project id while counting unread project messages");
            throw new IllegalArgumentException("Invalid id");
        }

        if (token == null || token.isEmpty()) {
            logger.error("Invalid token while counting unread messages");
            throw new IllegalArgumentException("Invalid token");
        }

        logger.info("Counting unread project messages for user with id {} for project with id {}", userId, projectId);

        boolean hasUnreadMessages = false;
        int unreadMessages = 0;

        try {
            unreadMessages = projectMessageDao.countUnreadProjectMessagesForUser(projectId);
        } catch (Exception e) {
            logger.error("Error counting unread project messages for user with id: {} for project with id: {}", userId, projectId, e);
        }

        if (unreadMessages < 0) {
            logger.error("Error counting unread project messages for user with id: {} for project with id: {}", userId, projectId);
            throw new IllegalArgumentException("Error counting project unread messages");
        }

        if (unreadMessages > 0) {
            notificationWS.send(token, NotificationWS.NOTIFICATION);
            hasUnreadMessages = true;
        }

        logger.info("User with id: {} has {} unread messages for project with id: {}", userId, unreadMessages, projectId);
        return hasUnreadMessages;

    }

    /**
     * Gets all personal messages where the receiver is the user with the given id
     * @param userId the id of the user
     * @return a list of personal messages
     */
    public List<PersonalMessage> getAllPersonalMessagesWhereReceiverIs(int userId) {

        logger.info("Entering getAllPersonalMessagesWhereReceiverIs method");

        if (!dataValidator.isIdValid(userId)) {
            logger.error("Invalid user id while getting personal messages");
            throw new IllegalArgumentException("Invalid user id");
        }

        logger.info("Getting all personal messages where the receiver is the user with id: {}", userId);

        List<PersonalMessageEntity> personalMessages = new ArrayList<>();

        try {
            personalMessages = personalMessageDao.getAllPersonalMessagesWhereReceiverIs(userId);
        } catch (Exception e) {
            logger.error("Error getting personal messages for user with id: {}", userId, e);
        }

        if (personalMessages == null) {
            logger.info("Error getting personal messages for user with id: {}, returning empty list", userId);
            personalMessages = new ArrayList<>();
        }

        return personalMessages.stream()
                .map(this::personalMessageEntityToDto)
                .toList();
    }

    /**
     * Gets all project messages where the project is the one with the given id
     * @param projectId the id of the project
     * @return a list with all project messages where the project is the one with the given id
     */
    public List<ProjectMessage> getAllProjectMessagesWhereProjectIs(int projectId) {

        logger.info("Entering getAllProjectMessagesWhereProjectIs method");

        if (!dataValidator.isIdValid(projectId)) {
            logger.error("Invalid project id while getting project messages");
            throw new IllegalArgumentException("Invalid project id");
        }

        logger.info("Getting all project messages where the project is the one with id: {}", projectId);

        List<ProjectMessageEntity> projectMessages = new ArrayList<>();

        try {
            projectMessages = projectMessageDao.getAllProjectMessagesWhereProjectIs(projectId);
        } catch (Exception e) {
            logger.error("Error getting project messages for project with id: {}", projectId, e);
        }

        if (projectMessages == null) {
            logger.info("Error getting project messages for project with id: {}, returning empty list", projectId);
            projectMessages = new ArrayList<>();
        }

        return projectMessages.stream()
                .map(this::projectMessageEntityToDto)
                .toList();
    }

    /**
     * Gets all personal messages sent by the user with the given id
     * @param userId the id of the user
     * @return a list of personal messages
     */
    public List<PersonalMessage> getAllPersonalMessagesSentByUser(int userId) {

        logger.info("Entering getAllPersonalMessagesSentByUser method");

        if (!dataValidator.isIdValid(userId)) {
            logger.error("Invalid user id while getting personal sent messages");
            throw new IllegalArgumentException("Invalid user id");
        }

        logger.info("Getting all personal messages sent by the user with id: {}", userId);

        List<PersonalMessageEntity> personalMessages = new ArrayList<>();

        try {
            personalMessages = personalMessageDao.getAllPersonalMessagesSentByUser(userId);
        } catch (Exception e) {
            logger.error("Error getting personal sent messages for user with id: {}", userId, e);
        }

        if (personalMessages == null) {
            logger.info("Error getting personal sent messages for user with id: {}, returning empty list", userId);
            personalMessages = new ArrayList<>();
        }

        return personalMessages.stream()
                .map(this::personalMessageEntityToDto)
                .toList();
    }

    /**
     * Marks a personal message as read
     * @param messageId the ID of the message
     * @return true if the message was marked as read, false otherwise
     */
    public boolean markPersonalMessageAsRead(int messageId) {

        logger.info("Entering markPersonalMessageAsRead method");

        if (!dataValidator.isIdValid(messageId)) {
            logger.error("Invalid message id while marking personal message as read");
            throw new IllegalArgumentException("Invalid message id");
        }

        logger.info("Marking personal message with id: {} as read", messageId);

        try {
            personalMessageDao.markPersonalMessageAsRead(messageId);
            logger.info("Personal message with id: {} marked as read", messageId);
            return true;
        } catch (Exception e) {
            logger.error("Error marking personal message with id: {} as read", messageId, e);
            throw new IllegalArgumentException("Error marking personal message as read");
        }
    }
}
