package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MComponentProject;
import domcast.finalprojbackend.entity.M2MProjectUser;
import domcast.finalprojbackend.entity.ProjectEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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

    /**
     * Method to find a project user.
     *
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return the project user
     */
    public M2MProjectUser findProjectUser(int userId, int projectId) {
        try {
            logger.info("Attempting to find project user with user id: {} and project id: {}", userId, projectId);
            M2MProjectUser user = (M2MProjectUser) em.createNamedQuery("M2MProjectUser.findProjectUser")
                    .setParameter("userId", userId)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            logger.info("Found project user with user id: {} and project id: {}. Role: {}", userId, projectId, user.getRole());
            return user;
        } catch (NoResultException e) {
            logger.error("No project user found with user id: {} and project id: {}", userId, projectId, e);
            return null;
        } catch (NonUniqueResultException e) {
            logger.error("More than one project user found with user id: {} and project id: {}", userId, projectId, e);
            return null;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while finding project user with user id: {} and project id: {}", userId, projectId, e);
            return null;
        }
    }

    /**
     * Method to find the main manager user id in a project.
     *
     * @param projectId the id of the project
     * @return the main manager user id
     */
    public int findMainManagerUserIdInProject(int projectId) {
        try {
            logger.info("Attempting to find main manager user id for project with id: {}", projectId);
            int userId = (int) em.createNamedQuery("M2MProjectUser.findMainManagerUserIdInProject")
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            logger.info("Found main manager user id for project with id: {}", projectId);
            return userId;
        } catch (NoResultException e) {
            logger.error("No main manager user id found for project with id: {}", projectId, e);
            return -1;
        } catch (NonUniqueResultException e) {
            logger.error("More than one main manager user id found for project with id: {}", projectId, e);
            return -1;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while finding main manager user id for project with id: {}", projectId, e);
            return -1;
        }
    }

    /**
     * Method to remove a project user.
     *
     * @param userId the id of the user
     * @param projectId the id of the project
     */
    public void removeProjectUser(int userId, int projectId) {
        try {
            logger.info("Attempting to remove project user with user id: {} and project id: {}", userId, projectId);
            em.createNamedQuery("M2MProjectUser.removeProjectUser")
                    .setParameter("userId", userId)
                    .setParameter("projectId", projectId)
                    .executeUpdate();
            logger.info("Removed project user with user id: {} and project id: {}", userId, projectId);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while removing project user with user id: {} and project id: {}", userId, projectId, e);
        }
    }

    /**
     * Method to get the number of active users in a project.
     *
     * @param projectId the id of the project
     * @return the number of active users in the project
     */
    public int getNumberOfActiveUsersInProject(int projectId) {
        try {
            logger.info("Attempting to get number of active users in project with id: {}", projectId);
            int count = em.createNamedQuery("M2MProjectUser.getNumberOfActiveUsersInProject", Long.class)
                    .setParameter("projectId", projectId)
                    .getSingleResult()
                    .intValue();
            logger.info("Found {} active users in project with id: {}", count, projectId);
            return count;
        } catch (NoResultException e) {
            logger.error("No active users found in project with id: {}", projectId, e);
            return 0;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while getting number of active users in project with id: {}", projectId, e);
            return 0;
        }
    }

    /**
     * Method to get the projects that exceed the maximum number of users.
     *
     * @param number the maximum number of users
     * @return the projects that exceed the maximum number of users
     */
    public Set<ProjectEntity> getProjectsExceedingMaxUsers(long number) {
        try {
            List<ProjectEntity> resultList = em.createNamedQuery("M2MProjectUser.getProjectsExceedingMaxUsers", ProjectEntity.class)
                    .setParameter("number", number)
                    .getResultList();
            if (resultList == null) {
                logger.error("Query result is null for projects exceeding max users");
                return new HashSet<>();
            }
            return new HashSet<>(resultList);
        } catch (NoResultException e) {
            logger.error("No projects exceeding max users found", e);
            return new HashSet<>();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while getting projects exceeding max users", e);
            return new HashSet<>();
        }
    }

    /**
     * Method to get the users in a project.
     *
     * @param projectId the id of the project
     * @return the users in the project
     */
    public List<Integer> getUsersInProject(int projectId) {
        try {
            List<Integer> resultList = em.createNamedQuery("M2MProjectUser.getUsersInProject", Integer.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
            if (resultList == null) {
                logger.error("Query result is null for users in project with id: {}", projectId);
                return new ArrayList<>();
            }
            return resultList;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while getting users in project with id: {}", projectId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Method to find the project managers in a project.
     *
     * @param projectId the id of the project
     * @return the project managers in the project
     */
    public List<M2MProjectUser> findProjectManagers(int projectId) {
        try {
            List<M2MProjectUser> resultList = em.createNamedQuery("M2MProjectUser.findProjectManagers", M2MProjectUser.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
            if (resultList == null) {
                logger.error("Query result is null for project managers in project with id: {}", projectId);
                return new ArrayList<>();
            }
            return resultList;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while finding project managers in project with id: {}", projectId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Method to find the invited users in a project.
     *
     * @param projectId the id of the project
     * @return the invited users in the project
     */
    public List<M2MProjectUser> findInvitedUsers(int projectId) {
        try {
            List<M2MProjectUser> resultList = em.createNamedQuery("M2MProjectUser.findInvitedUsers", M2MProjectUser.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
            if (resultList == null) {
                logger.error("Query result is null for invited users in project with id: {}", projectId);
                return new ArrayList<>();
            }
            return resultList;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while finding invited users in project with id: {}", projectId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Method to find the candidates in a project.
     *
     * @param projectId the id of the project
     * @return the candidates in the project
     */
    public List<M2MProjectUser> findCandidates(int projectId) {
        try {
            List<M2MProjectUser> resultList = em.createNamedQuery("M2MProjectUser.findCandidates", M2MProjectUser.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
            if (resultList == null) {
                logger.error("Query result is null for candidates in project with id: {}", projectId);
                return new ArrayList<>();
            }
            return resultList;
        } catch (Exception e) {
            logger.error("An unexpected error occurred while finding candidates in project with id: {}", projectId, e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets the average number of users in projects.
     *
     * @return the average number of users in projects
     */
    public double averageUsersInProjects() {
        logger.info("Getting average number of users in projects");
        try {
            return (double) em.createNamedQuery("M2MProjectUser.averageActiveUsersAcrossAllProjects").getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }
}
