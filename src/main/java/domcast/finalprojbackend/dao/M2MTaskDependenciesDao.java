package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MTaskDependencies;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * M2MTaskDependenciesDao is a Data Access Object (DAO) class for TaskEntity.
 * It provides methods to interact with the database and perform operations on M2MTaskDependencies entities.
 * @see M2MTaskDependencies
 * @see AbstractDao
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class M2MTaskDependenciesDao extends AbstractDao<M2MTaskDependencies> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserDao.class);

    /**
     * Default constructor for M2MTaskDependenciesDao.
     */
    public M2MTaskDependenciesDao() {
        super(M2MTaskDependencies.class);
    }

}
