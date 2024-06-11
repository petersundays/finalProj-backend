package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MProjectUser;
import domcast.finalprojbackend.entity.ProjectEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Stateless
public class ProjectDao extends AbstractDao<ProjectEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserDao.class);

    /**
     * Default constructor for ProjectDao.
     */
    public ProjectDao() {
        super(ProjectEntity.class);
    }

    /**
     * Finds a project by its id.
     *
     * @param id the id of the project
     * @return the ProjectEntity object if found, null otherwise
     */
    public ProjectEntity findProjectById(int id) {
        logger.info("Finding project by id {}", id);
        try {
            return (ProjectEntity) em.createNamedQuery("Project.findProjectById").setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error("Project with id {} not found", id);
            return null;
        }
    }

    /**
     * Checks if a user is active in a project.
     *
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is active in the project
     */
    public boolean isUserActiveInProject(int userId, int projectId) {
        try {
            M2MProjectUser result = em.createNamedQuery("M2MProjectUser.isUserActiveInProject", M2MProjectUser.class)
                    .setParameter("userId", userId)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            return result != null;
        } catch (NoResultException e) {
            return false;
        }
    }
}
