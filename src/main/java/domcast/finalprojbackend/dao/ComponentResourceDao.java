package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.ComponentResourceEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ComponentResourceDao extends AbstractDao<ComponentResourceEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(InterestDao.class);


    /**
     * Default constructor for ComponentResourceDao.
     */
    public ComponentResourceDao() {
        super(ComponentResourceEntity.class);
    }

    /**
     * Checks if a component resource exists by name and brand.
     * A component resource cannot have the same name and brand.
     * @param name the name of the component resource.
     * @param brand the brand of the component resource.
     * @return boolean value indicating if the component resource exists.
     */
    public boolean doesCRExistByNameAndBrand(String name, String brand) {
        logger.info("Checking if component resource exists by name and brand");
        try {
            return em.createNamedQuery("ComponentResource.doesCRExistByNameAndBrand", Boolean.class)
                    .setParameter("name", name)
                    .setParameter("brand", brand)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("An error occurred when checking if a component resource with name {} and brand {} exists", name, brand, e);
            return false;
        }
    }

    /**
     * Finds a component resource entity by name and brand.
     * @param name the name of the component resource.
     * @param brand the brand of the component resource.
     * @return the component resource entity.
     */
    public ComponentResourceEntity findCREntityByNameAndBrand(String name, String brand) {
        logger.info("Finding component resource entity by name and brand");
        try {
            return em.createNamedQuery("ComponentResource.findCREntityByNameAndBrand", ComponentResourceEntity.class)
                    .setParameter("name", name)
                    .setParameter("brand", brand)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Component resource with name {} and brand {} not found, when trying to return entity", name, brand);
            return null;
        }
    }

    /**
     * Finds a component resource entity by id.
     * @param id the id of the component resource.
     * @return the component resource entity.
     */
    public ComponentResourceEntity findCREntityById(int id) {
        logger.info("Finding component resource entity by id");
        try {
            return em.createNamedQuery("ComponentResource.findCREntityById", ComponentResourceEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Component resource with id {} not found, when trying to return entity", id);
            return null;
        }
    }

    /**
     * Gets a list of ComponentResourceEntity by criteria.
     *
     * @param name the name of the component resource
     * @param brand the brand of the component resource
     * @param partNumber the part number of the component resource
     * @param supplier the supplier of the component resource
     * @param orderBy the attribute by which to order the results
     * @param orderAsc whether to order the results in ascending order
     * @return a list of ComponentResourceEntity objects
     */
    public List<ComponentResourceEntity> getComponentResourcesByCriteria(String name, String brand, String partNumber, String supplier, String orderBy, boolean orderAsc) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ComponentResourceEntity> cq = cb.createQuery(ComponentResourceEntity.class);

        Root<ComponentResourceEntity> componentResource = cq.from(ComponentResourceEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (name != null) {
            predicates.add(cb.equal(componentResource.get("name"), name));
        }
        if (brand != null) {
            predicates.add(cb.equal(componentResource.get("brand"), brand));
        }
        if (partNumber != null) {
            predicates.add(cb.equal(componentResource.get("partNumber"), partNumber));
        }
        if (supplier != null) {
            predicates.add(cb.equal(componentResource.get("supplier"), supplier));
        }

        cq.select(componentResource).where(cb.and(predicates.toArray(new Predicate[0])));

        if (orderBy != null) {
            if (orderAsc) {
                cq.orderBy(cb.asc(componentResource.get(orderBy)));
            } else {
                cq.orderBy(cb.desc(componentResource.get(orderBy)));
            }
        }

        TypedQuery<ComponentResourceEntity> query = em.createQuery(cq);
        return query.getResultList();
    }
}
