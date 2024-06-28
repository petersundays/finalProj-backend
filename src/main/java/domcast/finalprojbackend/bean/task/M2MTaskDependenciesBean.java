package domcast.finalprojbackend.bean.task;

import domcast.finalprojbackend.dao.M2MTaskDependenciesDao;
import domcast.finalprojbackend.entity.M2MTaskDependencies;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Set;

/**
 * Bean class for the task_dependencies table in the database.
 *
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class M2MTaskDependenciesBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(M2MTaskDependenciesBean.class);

    @EJB
    private M2MTaskDependenciesDao m2MTaskDependenciesDao;

    /**
     * Default constructor for M2MTaskDependenciesBean.
     */
    public M2MTaskDependenciesBean() {
    }

    public boolean removeTaskRelationship(Set<M2MTaskDependencies> relationships) {

        if (relationships == null) {
            logger.error("Error deleting task relationship: relationships is null");
            throw new RuntimeException("Error deleting task relationship: relationships is null");
        }

        logger.info("Deleting task dependencies");

        for (M2MTaskDependencies relationship : relationships) {
            try {
                if (!m2MTaskDependenciesDao.remove(relationship)) {
                    logger.error("Error deleting task relationship");
                    throw new RuntimeException("Error deleting task relationship");
                }

                logger.info("Task relationship with id {} deleted", relationship.getId());
            } catch (Exception e) {
                logger.error("Error deleting task relationship with id {}: {}", relationship.getId(), e.getMessage());
                throw new RuntimeException("Error deleting task relationship.");
            }
        }
        return true;
    }
}
