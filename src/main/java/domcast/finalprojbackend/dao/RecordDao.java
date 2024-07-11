package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.RecordEntity;
import domcast.finalprojbackend.enums.MessageAndLogEnum;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RecordDao is a Data Access Object (DAO) class for RecordEntity.
 * It provides methods to interact with the database and perform operations on RecordEntity.
 * @see RecordEntity
 * @see AbstractDao
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class RecordDao extends AbstractDao<RecordEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(RecordEntity.class);

    /**
     * Default constructor for RecordDao.
     */

    public RecordDao() {
        super(RecordEntity.class);
    }

    /**
     * Finds all records where project id is projectId, author id is authorId and type is type.
     *
     * @param projectId the project id
     * @param authorId the author id
     * @param type the type of the record
     * @return the RecordEntity object if found, null otherwise
     */
    public boolean checkIfRecordExists(int projectId, int authorId, MessageAndLogEnum type) {
        logger.info("Checking if record exists with project id {}, author id {}, and type {}", projectId, authorId, type);
        try {
            RecordEntity record = (RecordEntity) em.createNamedQuery("Record.DoesRecordExist")
                    .setParameter("projectId", projectId)
                    .setParameter("authorId", authorId)
                    .setParameter("type", type)
                    .setParameter("startTimestamp", LocalDateTime.now().minusSeconds(5))
                    .setParameter("endTimestamp", LocalDateTime.now())
                    .getSingleResult();
            return true; // Record found
        } catch (NoResultException e) {
            logger.info("No record found with project id {}, author id {}, and type {}", projectId, authorId, type);
            return false;
        } catch (NonUniqueResultException e) {
            logger.error("Multiple records found with project id {}, author id {}, and type {}", projectId, authorId, type);
            return true;
        }
    }

    /**
     * Finds all records where project id is projectId.
     *
     * @param projectId the project id
     * @return the RecordEntity object if found, null otherwise
     */
    public List<RecordEntity> getRecordsByProject(int projectId) {
        logger.info("Getting records by project id {}", projectId);
        try {
            return em.createNamedQuery("Record.getRecordsByProject", RecordEntity.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
        } catch (NoResultException e) {
            logger.error("No records found with project id {}", projectId);
            return null;
        }
    }

}
