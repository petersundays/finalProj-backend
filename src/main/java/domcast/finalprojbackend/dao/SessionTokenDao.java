package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.SessionTokenEntity;
import domcast.finalprojbackend.entity.ValidationTokenEntity;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class SessionTokenDao extends ValidationTokenDao {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SessionTokenDao.class);

       public SessionTokenDao() {
            super();
        }

}

