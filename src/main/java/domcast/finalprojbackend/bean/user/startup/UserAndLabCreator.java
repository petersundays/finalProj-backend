package domcast.finalprojbackend.bean.user.startup;


import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.entity.LabEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Stateless EJB Bean responsible for creating default labs and a default user.
 * This is typically used at application startup to ensure necessary data is present in the database.
 */
@Stateless
public class UserAndLabCreator {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserDao userDao;

    @Inject
    private LabDao labDao;

    @Inject
    private ValidatorAndHasher validatorAndHasher;

    /**
     * Creates default labs in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultLabs() {
        for (LabEnum city : LabEnum.values()) {
            LabEntity lab = em.find(LabEntity.class, city.getId());
            if (lab == null) {
                lab = new LabEntity();
                lab.setCity(city);
                em.persist(lab);
            }
        }
    }

    /**
     * Creates a default user in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultUser() {
        UserEntity user = userDao.findUserByEmail("defaultUserEmail");
        String password = validatorAndHasher.hashPassword("admin");
        if (user == null) {
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

            userDao.persist(user);
        }
    }
}