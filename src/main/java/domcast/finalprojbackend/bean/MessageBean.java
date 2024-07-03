/*
package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.PersonalMessageDao;
import domcast.finalprojbackend.dao.ProjectMessageDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.messageDto.PersonalMessage;
import domcast.finalprojbackend.entity.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

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


    private static final Logger logger = LogManager.getLogger(TaskBean.class);

    private static final long serialVersionUID = 1L;

    public MessageBean() {
    }

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

        PersonalMessageEntity messageEntity = new PersonalMessageEntity;
        messageEntity.setContent(content);
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setTimestamp(LocalDateTime.now());

        try {
            personalMessageDao.persist(messageEntity);
            logger.info("Message persisted");
        } catch (PersistenceException e) {
            logger.error("Message not persisted");
            throw new PersistenceException("Message not persisted");
        }

    }


    public void markMessageAsRead(String username) {
        logger.info("Marking messages as read for: {}", username);
        ArrayList<MessageEntity> messages = projectMessageDao.findMessagesUnreadForUser(username);

        for (MessageEntity message : messages) {
            message.setRead(true);
            projectMessageDao.merge(message);

            logger.info("Message {} marked as read for: {}", message.getId(), username);
        }

    }

    public MessageEntity convertMessageDtoToEntity(Message message) {
        logger.info("Converting message DTO to entity");
        UserEntity sender = userDao.findUserByUsername(message.getSender());
        UserEntity receiver = userDao.findUserByUsername(message.getReceiver());
        return new MessageEntity(message.getContent(), sender, receiver, message.getTimestamp());
    }

    public Message convertMessageEntityToDto(MessageEntity messageEntity) {
        logger.info("Converting message entity to DTO");
        return new Message(messageEntity.getContent(), messageEntity.getSender().getUsername(), messageEntity.getReceiver().getUsername(), messageEntity.getTimestamp(), messageEntity.isRead());
    }

    public ArrayList<Message> getMessages(String token, String receiver) {
        logger.info("Getting messages for: {}", receiver);
        ArrayList<Message> messages = new ArrayList<>();
        User user = userBean.findUserByToken(token);

        if (user != null) {
            logger.info("User found: {}", user.getUsername());
            ArrayList<MessageEntity> messageEntities = projectMessageDao.findMessagesBetweenUsers(user.getUsername(), receiver);
            if(messageEntities!=null) {
                logger.info("Messages found for: {}", receiver);
                for (MessageEntity messageEntity : messageEntities) {
                    messages.add(convertMessageEntityToDto(messageEntity));
                    logger.info("Message {} added to list", messageEntity.getId());
                }
            }
        }

        logger.info("Returning messages for: {}", receiver);
        return messages;
    }
}
*/
