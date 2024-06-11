package domcast.finalprojbackend.bean.project;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.ProjectDao;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

@Stateless
public class ProjectBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @EJB
    private ProjectDao projectDao;


    /**
     * Default constructor for ProjectBean.
     */
    public ProjectBean() {
    }

    /**
     * Checks if a user is active in a project.
     *
     * @param userId    the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is active in the project
     */
    public boolean isUserActiveInProject(int userId, int projectId) {
        logger.info("Checking if user with ID {} is active in project with ID {}", userId, projectId);

        boolean isActive;
        try {
            isActive = projectDao.isUserActiveInProject(userId, projectId);
        } catch (PersistenceException e) {
            logger.error("Error checking if user with ID {} is active in project with ID {}", userId, projectId, e);
            throw new RuntimeException(e);
        }

        logger.info("User with ID {} is active in project with ID {}: {}", userId, projectId, isActive);
        return isActive;
    }
}
