package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.entity.ValidationTokenEntity;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class ValidationTokenDao extends AbstractDao<ValidationTokenEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(ValidationTokenDao.class);

    public ValidationTokenDao() {
        super(ValidationTokenEntity.class);
    }

    public boolean setTokenInactive(String token) {
        logger.info("Setting token {} as inactive", token);
        try {
            int updatedEntities = em.createNamedQuery("Token.setTokenInactive")
                    .setParameter("token", token)
                    .executeUpdate();
            return updatedEntities > 0;
        } catch (Exception e) {
            logger.error("Error setting token {} as inactive", token, e);
            return false;
        }
    }

}

