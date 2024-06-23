package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MComponentProject;
import domcast.finalprojbackend.entity.M2MProjectUser;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class M2MProjectUserDao extends AbstractDao<M2MProjectUser> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(M2MComponentProject.class);

    /**
     * Default constructor for M2MProjectUserDao.
     */
    public M2MProjectUserDao() {
            super(M2MProjectUser.class);
    }

    /**
     * Method to find the main manager in a project.
     *
     * @param projectId the id of the project
     * @return the main manager in the project
     */
    public M2MProjectUser findMainManagerInProject(int projectId) {
        try {
            logger.info("Attempting to find main manager for project with id: {}", projectId);
            M2MProjectUser user = (M2MProjectUser) em.createNamedQuery("M2MProjectUser.findMainManagerInProject")
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            logger.info("Found main manager for project with id: {}. Main manager role: {}", projectId, user.getRole());
            return user;
        } catch (NoResultException e) {
            logger.error("No main manager found for project with id: {}", projectId, e);
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("More than one main manager found for project with id: {}", projectId, e);
            return null;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while finding main manager for project with id: {}", projectId, e);
            return null;
        }
    }
    
    /**
     * Method to find the project team, excluding the main manager.
     *
     * @param projectId the id of the project
     * @return the project team
     */
    public Set<M2MProjectUser> findProjectTeam(int projectId) {
        try {
            List<M2MProjectUser> resultList = em.createNamedQuery("M2MProjectUser.findProjectTeam", M2MProjectUser.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
            if (resultList == null) {
                logger.error("Query result is null for project with id: {}", projectId);
                return new HashSet<>();
            }
            return new HashSet<>(resultList);
        } catch (NoResultException e) {
            logger.error("No team found for project with id: {}", projectId, e);
            return new HashSet<>();
        }
    }
}
