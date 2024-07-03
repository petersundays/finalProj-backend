package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.PersonalMessageEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

}