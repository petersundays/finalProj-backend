package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.TaskEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * TaskDao is a Data Access Object (DAO) class for TaskEntity.
 * It provides methods to interact with the database and perform operations on TaskEntity.
 * @see TaskEntity
 * @see AbstractDao
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class TaskDao extends AbstractDao<TaskEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserDao.class);

    /**
     * Default constructor for TaskDao.
     */
    public TaskDao() {
        super(TaskEntity.class);
    }

    /**
     * Finds a task by its id.
     *
     * @param id the id of the task
     * @return the TaskEntity object if found, null otherwise
     */
    public TaskEntity findTaskByIdAndProjectId(int id, int projectId) {
        logger.info("Finding task by id {} and project id {}", id, projectId);
        try {
            return em.createNamedQuery("Task.findTaskByIdAndProjectId", TaskEntity.class)
                    .setParameter("id", id)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error("Task with id {} and project id {} not found", id, projectId);
            return null;
        }
    }

    /**
     * Finds a task by its title, responsible id and project id.
     * This method is used to find a task by its title, responsible id and project id.
     *
     * @param title the title of the task
     * @param responsibleId the id of the responsible user
     * @param projectId the id of the project
     * @return the TaskEntity object if found, null otherwise
     */
    public TaskEntity findTaskByTitleResponsibleProject(String title, int responsibleId, int projectId) {
        try {
            return em.createNamedQuery("Task.findTaskByTitleResponsibleProject", TaskEntity.class)
                    .setParameter("title", title)
                    .setParameter("responsibleId", responsibleId)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Finds a task by its id.
     *
     * @param id the id of the task
     * @return the TaskEntity object if found, null otherwise
     */
    public TaskEntity findTaskById(int id) {
        logger.info("Finding task by id {}", id);
        try {
            return em.createNamedQuery("Task.findTaskById", TaskEntity.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error("Task with id {} not found", id);
            return null;
        }
    }

    /**
     * Finds a task by its project id.
     *
     * @param projectId the id of the project
     * @return the TaskEntity object if found, null otherwise
     */
    public List<TaskEntity> findTaskByProjectId(int projectId) {
        logger.info("Finding task by project id {}", projectId);
        try {
            return em.createNamedQuery("Task.findTaskByProjectId", TaskEntity.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
        } catch (NoResultException e) {
            logger.error("Task with project id {} not found", projectId);
            return null;
        }
    }

    public TaskEntity findPresentationTaskInProject(int projectId) {
        logger.info("Finding presentation task in project with id {}", projectId);
        try {
            return em.createNamedQuery("Task.findPresentationTaskInProject", TaskEntity.class)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error("Presentation task in project with id {} not found", projectId);
            return null;
        }
    }
}
