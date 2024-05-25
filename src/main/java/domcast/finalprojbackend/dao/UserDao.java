package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    public UserDao() {
        super(UserEntity.class);
    }

    public UserEntity findUserByEmail(String email) {
        logger.info("Finding user by email {}", email);
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("User with email {} not found", email);
            return null;
        }
    }

}
