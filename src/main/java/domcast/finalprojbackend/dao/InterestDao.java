package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.InterestEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * InterestDao is a Data Access Object (DAO) class for InterestEntity.
 * It provides methods to interact with the database and perform operations on InterestEntity.
 * @see InterestEntity
 * @see AbstractDao
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class InterestDao extends AbstractDao<InterestEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(InterestDao.class);

    /**
     * Default constructor for InterestDao.
     */
    public InterestDao() {
        super(InterestEntity.class);
    }

    /**
     * Finds an interest by its name.
     *
     * @param name the name of the interest
     * @return the InterestEntity object if found, null otherwise
     */
    public InterestEntity findInterestByName(String name) {
        logger.info("Finding interest by name {}", name);
        try {
            return (InterestEntity) em.createNamedQuery("Interest.findInterestByName").setParameter("name", name)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("Interest with name {} not found", name);
            return null;
        }
    }

    /**
     * Finds a set of interests by their names.
     *
     * @param names the names of the interests
     * @return the set of InterestEntity objects if found, null otherwise
     */
    public Set<InterestEntity> findInterestsByListOfNames(ArrayList<String> names) {
        logger.info("Finding interests by names {}", names);
        try {
            List<InterestEntity> resultList = em.createNamedQuery("Interest.findInterestsByListOfNames", InterestEntity.class)
                    .setParameter("names", names)
                    .getResultList();
            return new HashSet<>(resultList);

        } catch (NoResultException e) {
            logger.error("Interests with names {} not found", names);
            return null;
        }
    }
}
