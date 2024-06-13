package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.ComponentResourceEntity;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
