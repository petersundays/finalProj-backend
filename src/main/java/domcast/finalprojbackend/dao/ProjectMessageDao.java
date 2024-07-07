package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.ProjectMessageEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for project messages
 */
@Stateless
public class ProjectMessageDao extends AbstractDao<ProjectMessageEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SessionTokenDao.class);

    /**
     * Default constructor
     */
    public ProjectMessageDao() {
        super(ProjectMessageEntity.class);
    }

    /**
     * Persists a project message entity
     * @param entity the entity to be persisted
     * @return the persisted entity
     * @throws PersistenceException if an error occurs during the persist operation
     */
    public ProjectMessageEntity persistProjectMessage(ProjectMessageEntity entity) throws PersistenceException {
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
     * Counts the number of unread project messages for a user
     * @param projectId the id of the project
     * @return the number of unread project messages
     */
    public int countUnreadProjectMessagesForUser(int projectId) {
        logger.info("Counting unread project messages for project with id {}", projectId);

        try {
            return em.createNamedQuery("Message.countUnreadProjectMessagesForUser", Long.class)
                    .setParameter("projectId", projectId)
                    .getSingleResult()
                    .intValue();
        } catch (Exception e) {
            logger.error("Error while counting unread project messages: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Gets all project messages where the project is the one with the given id
     * @param projectId the id of the project
     * @return a list with all project messages where the project is the one with the given id
     */
    public List<ProjectMessageEntity> getAllProjectMessagesWhereProjectIs(int projectId) {
        logger.info("Getting all project messages where the project is the one with id {}", projectId);

        try {
            return em.createNamedQuery("Message.getAllProjectMessagesWhereProjectIs", ProjectMessageEntity.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error while getting all project messages where the project is the one with id {}: {}", projectId, e.getMessage());
            return new ArrayList<>();
        }
    }
}