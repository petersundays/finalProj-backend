package domcast.finalprojbackend.bean.user.startup;


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

@Stateless
public class UserAndLabCreator {

    @PersistenceContext
    EntityManager em;

    @Inject
    UserDao userDao;

    @Inject
    LabDao labDao;

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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultUser() {
        UserEntity user = userDao.findUserByEmail("defaultUserEmail");
        if (user == null) {
            user = new UserEntity();
            user.setEmail("admin@mail.com");
            user.setPassword("admin");
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