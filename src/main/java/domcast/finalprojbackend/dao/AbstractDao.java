package domcast.finalprojbackend.dao;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;

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
	public void persist(final T entity) 
	{
		em.persist(entity);
	}

	/**
	 * Method that merges an entity in the database.
	 * @param entity the entity to be merged.
	 */
	// Merge an entity
	public void merge(final T entity) 
	{
		em.merge(entity);
	}

	/**
	 * Method that removes an entity from the database.
	 * @param entity the entity to be removed.
	 */
	// Remove an entity
	public void remove(final T entity) 
	{
		em.remove(em.contains(entity) ? entity : em.merge(entity));
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