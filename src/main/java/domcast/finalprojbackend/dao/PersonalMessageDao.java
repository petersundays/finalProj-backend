package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.PersonalMessageEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for personal messages
 */
@Stateless
public class PersonalMessageDao extends AbstractDao<PersonalMessageEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SessionTokenDao.class);

    /**
     * Default constructor
     */
    public PersonalMessageDao() {
        super(PersonalMessageEntity.class);
    }

    /**
     * Persists a personal message entity
     * @param entity the entity to be persisted
     * @return the persisted entity
     * @throws PersistenceException if an error occurs during the persist operation
     */
    public PersonalMessageEntity persistPersonalMessage(PersonalMessageEntity entity) throws PersistenceException {
        try {
            em.persist(entity);
            em.flush(); // Ensure the entity is persisted immediately
            return entity; // Return the persisted entity
        } catch (PersistenceException e) {
            logger.error("Error while persisting entity: {}", e.getMessage());
            throw e; // Rethrow the exception to be handled by the caller
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            throw new PersistenceException("Unexpected error during persist operation", e);
        }
    }

    /**
     * Counts the number of unread personal messages for a user
     * @param userId the id of the user
     * @return the number of unread personal messages
     */
    public int countUnreadPersonalMessagesForUser(int userId) {
        logger.info("Counting unread personal messages for user with id {}", userId);

        try {
            return em.createNamedQuery("Message.countUnreadPersonalMessagesForUser", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult()
                    .intValue();
        } catch (Exception e) {
            logger.error("Error while counting unread personal messages: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Gets all personal messages where the receiver is the user with the given id
     * @param userId the id of the user
     * @return a list of personal messages
     */
    public List<PersonalMessageEntity> getAllPersonalMessagesWhereReceiverIs(int userId) {
        logger.info("Getting all personal messages where the receiver is the user with id {}", userId);

        try {
            return em.createNamedQuery("Message.getAllPersonalMessagesWhereReceiverIs", PersonalMessageEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error while getting personal messages: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gets all personal messages sent by the user with the given id
     * @param userId the id of the user
     * @return a list of personal messages
     */
    public List<PersonalMessageEntity> getAllPersonalMessagesSentByUser(int userId) {
        logger.info("Getting all personal messages sent by the user with id {}", userId);

        try {
            return em.createNamedQuery("Message.getAllPersonalMessagesSentByUser", PersonalMessageEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error while getting personal sent messages: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Marks a personal message as read
     * @param messageId the id of the message
     */
    public void markPersonalMessageAsRead(int messageId) {
        logger.info("Marking personal message with id {} as read", messageId);

        try {
            em.createNamedQuery("Message.markPersonalMessageAsRead")
                    .setParameter("messageId", messageId)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("Error while marking personal message as read: {}", e.getMessage());
        }
    }

    /**
     * Checks if a user is the receiver of a personal message
     * @param messageId the ID of the message
     * @param userId the id of the user
     * @return true if the user is the receiver of the message, false otherwise
     */
    public boolean isUserReceiverOfPersonalMessage(int messageId, int userId) {
        logger.info("Checking if user with id {} is the receiver of the personal message with id {}", userId, messageId);

        try {
            return em.createNamedQuery("Message.isUserReceiverOfPersonalMessage", Long.class)
                    .setParameter("messageId", messageId)
                    .setParameter("userId", userId)
                    .getSingleResult()
                    .intValue() > 0;
        } catch (Exception e) {
            logger.error("Error while checking if user is the receiver of the personal message: {}", e.getMessage());
            return false;
        }
    }
}