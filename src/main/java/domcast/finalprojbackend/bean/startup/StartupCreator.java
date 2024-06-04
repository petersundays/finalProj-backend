package domcast.finalprojbackend.bean.startup;


import domcast.finalprojbackend.bean.SystemBean;
import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.entity.LabEntity;
import domcast.finalprojbackend.entity.SystemEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.List;


/**
 * Stateless EJB Bean responsible for creating default labs and a default user.
 * This is typically used at application startup to ensure necessary data is present in the database.
 */
@Stateless
public class StartupCreator implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SystemBean.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserDao userDao;

    @Inject
    private LabDao labDao;

    @Inject
    private ValidatorAndHasher validatorAndHasher;

    @Inject
    private SystemBean systemBean;

    /**
     * Creates default labs in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultLabs() {
        logger.info("Creating default labs");
        for (LabEnum city : LabEnum.values()) {
            LabEntity lab = em.find(LabEntity.class, city.getId());
            if (lab == null) {
                logger.info("Creating lab for city {}", city);
                lab = new LabEntity();
                lab.setCity(city);
                em.persist(lab);
            }
        }
        logger.info("Default labs created");
    }

    /**
     * Creates a default user in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultUser() {
        logger.info("Creating default user");
        UserEntity user = userDao.findUserByEmail("defaultUserEmail");
        String password = validatorAndHasher.hashPassword("admin");
        if (user == null) {
            logger.info("Creating default user");
            user = new UserEntity();
            user.setEmail("admin@mail.com");
            user.setPassword(password);
            user.setFirstName("admin");
            user.setLastName("admin");
            user.setType(TypeOfUserEnum.ADMIN);
            user.setVisible(true);

            LabEntity lab = labDao.findLabByCity("Lisboa");
            if (lab != null) {
                user.setWorkplace(lab);
            }

            logger.info("Persisting default user");
            userDao.persist(user);
            logger.info("Default user created");
        }
    }

    /**
     * Sets the default system variables in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDefaultSystemVariables() {
        logger.info("Setting default system variables");
        try {
            int systemEntities = systemBean.numberOfSystemVariables();
            if (systemEntities == 0) {
                logger.info("Creating system entity");
                SystemEntity systemEntity = new SystemEntity();
                try {
                    systemEntity.setSessionTimeout(TypeOfUserEnum.ADMIN, 5);
                    systemEntity.setMaxUsers(TypeOfUserEnum.ADMIN, 5);
                } catch (IllegalArgumentException e) {
                    logger.error("User does not have admin privileges", e);
                    throw new RuntimeException("User does not have admin privileges", e);
                }
                em.persist(systemEntity);
                logger.info("System entity created");
            } else {
                logger.info("System entity already exists, no changes made");
            }
        } catch (PersistenceException e) {
            logger.error("Error persisting SystemEntity", e);
            throw new RuntimeException("Error persisting SystemEntity", e);
        } catch (Exception e) {
            logger.error("Error setting default system variables", e);
            throw new RuntimeException("Error setting default system variables", e);
        }
    }

}