package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MComponentProject;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * Method that finds a M2MComponentProject by its component id and project id.
     *
     * @param componentId the id of the component.
     * @param projectId   the id of the project.
     * @return the M2MComponentProject entity.
     */
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

    /**
     * Method that finds a list of component resource ids by project id.
     *
     * @param projectId the id of the project.
     * @return the list of component resource ids.
     */
    public Set<Integer> findComponentResourceIdsByProjectId(int projectId) {
        logger.info("Finding ComponentResource ids by project id");
        try {
            List<Integer> resultList = em.createNamedQuery("M2MComponentProject.findComponentResourceIdsByProjectId", Integer.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
            return new HashSet<>(resultList);
        } catch (Exception e) {
            logger.error("ComponentResource ids with project id {} not found, when trying to return set", projectId);
            return null;
        }
    }

    /**
     * Method that finds all the M2MComponentProject entities by project id.
     *
     * @param projectId the id of the project.
     * @return the set of M2MComponentProject entities.
     */
    public Set<M2MComponentProject> findAllByProjectId(int projectId) {
        logger.info("Finding all M2MComponentProject by project id");
        try {
            return new HashSet<>(em.createNamedQuery("M2MComponentProject.findAllByProjectId", M2MComponentProject.class)
                    .setParameter("projectId", projectId)
                    .getResultList());
        } catch (Exception e) {
            logger.error("M2MComponentProject with project id {} not found, when trying to return set", projectId);
            return null;
        }
    }

    /**
     * Method that sets a M2MComponentProject as active by its component id and project id.
     *
     * @param componentId the id of the component.
     * @param projectId   the id of the project.
     */
    public void setActiveByComponentIdAndProjectId(int componentId, int projectId) {
        logger.info("Setting M2MComponentProject as active by component id and project id");
        try {
            em.createNamedQuery("M2MComponentProject.setActiveByProjectIdAndComponentId")
                    .setParameter("componentId", componentId)
                    .setParameter("projectId", projectId)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("Error while setting M2MComponentProject as active by component id {} and project id {}: {}", componentId, projectId, e.getMessage());
        }
    }

    /**
     * Method that sets a M2MComponentProject as inactive by its component id and project id.
     *
     * @param componentId the id of the component.
     * @param projectId   the id of the project.
     */
    public void setInactiveByComponentIdAndProjectId(int componentId, int projectId) {
        logger.info("Setting M2MComponentProject as inactive by component id and project id");
        try {
            em.createNamedQuery("M2MComponentProject.setInactiveByProjectIdAndComponentId")
                    .setParameter("componentId", componentId)
                    .setParameter("projectId", projectId)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("Error while setting M2MComponentProject as inactive by component id {} and project id {}: {}", componentId, projectId, e.getMessage());
        }
    }
}

