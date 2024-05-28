package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.entity.LabEntity;
import domcast.finalprojbackend.enums.LabEnum;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LabDao is a Data Access Object (DAO) class for LabEntity.
 * It provides methods to interact with the database and perform operations on LabEntity.
 * @see LabEntity
 * @see AbstractDao
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class LabDao extends AbstractDao<LabEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(LabDao.class);

    /**
     * Default constructor for LabDao.
     */
    public LabDao() {
        super(LabEntity.class);
    }

    /**
     * Finds a lab by its city name.
     *
     * @param city the name of the lab's city
     * @return the LabEntity object if found, null otherwise
     */

    public LabEntity findLabByCity(String city) {
        logger.info("Finding lab by city {}", city);
        try {
            return (LabEntity) em.createNamedQuery("Lab.findLabByCity")
                    .setParameter("city", LabEnum.fromValue(city))
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error("Lab with city {} not found", city);
            return null;
        }
    }

}
