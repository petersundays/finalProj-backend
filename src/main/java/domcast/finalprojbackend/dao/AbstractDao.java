package domcast.finalprojbackend.dao;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract class that contains the basic CRUD operations for the database.
 * The class is abstract and is extended by the specific DAO classes.
 * The class contains the necessary annotations to work with the database.
 * The class is generic and the type of the class is the entity that the DAO is working with.
 * The class contains the following methods:
 * - find: finds an entity by its id.
 * - persist: persists an entity in the database.
 * - merge: merges an entity in the database.
 * - remove: removes an entity from the database.
 * - findAll: finds all the entities of the type.
 * - deleteAll: deletes all the entities of the type.
 * - flush: flushes the entity manager.
 */

@TransactionAttribute(TransactionAttributeType.REQUIRED)
/**
 * The class is annotated with the TransactionAttribute annotation to specify that the transaction is required.
 */
public abstract class AbstractDao<T extends Serializable> implements Serializable {
	// The serial version UID
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AbstractDao.class);


	// The class of the entity
	private final Class<T> clazz;

	// The entity manager
	@PersistenceContext(unitName = "PersistenceUnit")
	protected EntityManager em;

	/**
	 * Constructor with parameters.
	 * @param clazz the class of the entity.
	 */
	public AbstractDao(Class<T> clazz) 
	{
		this.clazz = clazz;
	}

	/**
	 * Method that finds an entity by its id.
	 * @param id the id of the entity.
	 * @return the entity with the id.
	 */
	// Find an entity by its id
	public T find(Object id) 
	{
		return em.find(clazz, id);
	}

	/**
	 * Method that persists an entity in the database.
	 * @param entity the entity to be persisted.
	 */
	// Persist an entity
	public boolean persist(final T entity) {
		try {
			em.persist(entity);
			return true;
		} catch (PersistenceException e) {
			logger.error("Error while persisting entity: {}", e.getMessage());
			return false;
		} catch (Exception e) {
			logger.error("Unexpected error: {}", e.getMessage());
			return false;
		}
	}


	/**
	 * Method that merges an entity in the database.
	 * @param entity the entity to be merged.
	 */
	// Merge an entity
	public boolean merge(final T entity) {
		try {
			em.merge(entity);
			return true;
		} catch (PersistenceException e) {
			logger.error("Error while merging entity: {}", e.getMessage());
			return false;
		} catch (Exception e) {
			logger.error("Unexpected error: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Method that removes an entity from the database.
	 * @param entity the entity to be removed.
	 */
	// Remove an entity
	public boolean remove(final T entity) {
		try {
			T mergedEntity = em.contains(entity) ? entity : em.merge(entity);
			em.remove(mergedEntity);
			em.flush(); // Ensure changes are applied immediately

			// Check if the entity still exists in the EntityManager
			if (em.contains(mergedEntity)) {
				logger.error("Failed to remove entity");
				return false;
			} else {
				return true;
			}
		} catch (PersistenceException e) {
			logger.error("Error while removing entity: {}", e.getMessage());
			return false;
		} catch (Exception e) {
			logger.error("Unexpected error: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Method that finds all the entities of the type.
	 * @return a list with all the entities of the type.
	 */
	// Find all entities
	public List<T> findAll() 
	{
		final CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clazz);
		criteriaQuery.select(criteriaQuery.from(clazz));
		return em.createQuery(criteriaQuery).getResultList();
	}

	/**
	 * Method that deletes all the entities of the type.
	 */
	// Delete all entities
	public void deleteAll() 
	{
		final CriteriaDelete<T> criteriaDelete = em.getCriteriaBuilder().createCriteriaDelete(clazz);
		criteriaDelete.from(clazz);
		em.createQuery(criteriaDelete).executeUpdate();
	}

	/**
	 * Method that flushes the entity manager.
	 */
	// Flush the entity manager
	// To flush is to write the changes to the database
	public void flush() {
		em.flush();
	}
}