package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MComponentProject;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Data access object for the M2MComponentProject entity.
 * Contains all the necessary methods to interact with the database.
 * The class extends the AbstractDao class and has the
 * M2MComponentProject entity as a type parameter.
 *
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class M2MComponentProjectDao extends AbstractDao<M2MComponentProject> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(M2MComponentProject.class);

    /**
     * Default constructor for M2MComponentProjectDao.
     */
    public M2MComponentProjectDao() {
        super(M2MComponentProject.class);
    }


    public M2MComponentProject findM2MComponentProjectByComponentIdAndProjectId(int componentId, int projectId) {
        logger.info("Finding M2MComponentProject by component id and project id");
        try {
            return em.createNamedQuery("M2MComponentProject.findM2MComponentProjectByProjectIdAndComponentId", M2MComponentProject.class)
                    .setParameter("componentId", componentId)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("M2MComponentProject with component id {} and project id {} not found, when trying to return entity", componentId, projectId);
            return null;
        }
    }
}

