package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.M2MProjectUserDao;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.SystemDao;
import domcast.finalprojbackend.dto.EnumDTO;
import domcast.finalprojbackend.dto.statistics.ProjectStatistics;
import domcast.finalprojbackend.dto.statistics.StatisticsPerLab;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.SessionTokenEntity;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.util.EnumUtil;
import domcast.finalprojbackend.websocket.NotificationWS;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.websocket.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * SystemBean is a stateless EJB that provides an interface for interacting with system settings.
 * It uses the SystemDao to perform database operations.
 */
@Stateless
public class SystemBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SystemBean.class);

    // Inject the SystemDao EJB
    @EJB
    private SystemDao systemDao;

    @EJB
    private TokenBean tokenBean;

    @EJB
    private UserBean userBean;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private M2MProjectUserDao m2MProjectUserDao;

    @EJB
    private ProjectBean projectBean;

    @EJB
    private MessageBean messageBean;

    @EJB
    private NotificationWS notificationWS;

    /**
     * Default constructor for SystemBean.
     */
    public SystemBean() {
        super();
    }

    /**
     * Retrieves the session timeout from the database.
     *
     * @return the session timeout.
     */
    public int getSessionTimeout() {
        try {
            return systemDao.getSessionTimeout();
        } catch (Exception e) {
            logger.error("Error getting session timeout", e);
            throw new RuntimeException("Error getting session timeout", e);
        }
    }

    /**
     * Retrieves the maximum number of users that can be part of a project from the database.
     *
     * @return the maximum number of users.
     */
    public int getProjectMaxUsers() {
        try {
            return systemDao.getProjectMaxUsers();
        } catch (Exception e) {
            logger.error("Error getting project max users", e);
            throw new RuntimeException("Error getting project max users", e);
        }
    }

    /**
     * Updates the session timeout in the database.
     *
     * @param timeout the new session timeout to set.
     * @return boolean indicating if the operation was successful
     */
    public boolean setSessionTimeout(int timeout) {
        try {
            logger.info("Setting session timeout to {}", timeout);
            systemDao.setSessionTimeout(timeout);
            return true;
        } catch (Exception e) {
            logger.error("Error setting session timeout", e);
            return false;
        }
    }

    /**
     * Updates the maximum number of users that can be part of a project in the database.
     *
     * @param maxMembers the new maximum number of members to set.
     * @return boolean indicating if the operation was successful
     */
    public boolean setProjectMaxMembers(int maxMembers) {
        logger.info("Setting project max users to {}", maxMembers);

        if (maxMembers < 1) {
            logger.error("Error setting project max users: max members must be greater than 0");
            return false;
        }

        Set<ProjectEntity> projects = new HashSet<>();
        try {
            projects = m2MProjectUserDao.getProjectsExceedingMaxUsers((long) maxMembers);
        } catch (Exception e) {
            logger.error("Error getting projects exceeding max users", e);
            return false;
        }

        if (!projects.isEmpty()) {
            if (!projectBean.updateListOfProjectsMaxUsers(projects, maxMembers)) {
                return false;
            }
        }

        try {
            logger.info("Setting project max users to {}", maxMembers);
            systemDao.setProjectMaxMembers(maxMembers);
            return true;
        } catch (Exception e) {
            logger.error("Error setting project max users", e);
            return false;
        }
    }

    /**
     * Retrieves the number of system variables from the database.
     *
     * @return the number of system variables.
     */
    public int numberOfSystemVariables() {
        try {
            return systemDao.numberOfSystemVariables();
        } catch (Exception e) {
            logger.error("Error getting number of system variables", e);
            throw new RuntimeException("Error getting number of system variables", e);
        }
    }

    /**
     * Session timer that checks for active sessions that have exceeded the timeout every 30 seconds and logs them out.
     */
    @Schedule(second = "*/30", minute = "*", hour = "*") // this automatic timer is set to expire every 30 seconds
    public void sessionTimer() throws Exception {
        logger.info("Session timer started");

        HashMap<String, Session> allSessions = notificationWS.getSessions();

        try {
            // Find active sessions that have exceeded the timeout
            List<SessionTokenEntity> activeSessions = tokenBean.findActiveSessionsExceededTimeout(getSessionTimeout());

            // Log out the active sessions that have exceeded the timeout
            for (SessionTokenEntity session : activeSessions) {
                logger.info("Session token {} has exceeded the timeout", session.getToken());

                try {
                    userBean.logout(session.getToken());
                    messageBean.sendLogoutNotification(session.getToken(), allSessions);
                    logger.info("Session token {} has been logged out", session.getToken());
                } catch (Exception e) {
                    logger.error("Error setting session token {} logout time to now", session.getToken(), e);
                    throw e;
                }
            }
        } catch (Exception e) {
            logger.error("Error in session timer", e);
            throw e;
        } finally {
            logger.info("Session timer ended");
        }
    }

    public int totalProjectsPerLab(int labId) {
        logger.info("Getting the number of projects per lab");

        try {
            return projectDao.numberOfProjectsPerLab(labId);
        } catch (Exception e) {
            logger.error("Error getting the number of projects for lab with ID {}", labId, e);
            throw new RuntimeException("Error getting the number of projects for lab with ID " + labId, e);
        }
    }

    public int totalApprovedProjectsPerLab(int labId) {
        logger.info("Getting the number of approved projects per lab");

        try {
            return projectDao.numberOfApprovedProjectsByLab(labId);
        } catch (Exception e) {
            logger.error("Error getting the number of approved projects for lab with ID {}", labId, e);
            throw new RuntimeException("Error getting the number of approved projects for lab with ID " + labId, e);
        }
    }

    /**
     * Retrieves the number of projects per lab from the database.
     *
     * @param labId the lab ID to get the number of projects for.
     * @return the number of projects for the lab.
     */
    public int totalFinnishedProjectsPerLab(int labId) {
        logger.info("Getting the number of finished projects per lab");

        try {
            return projectDao.numberOfFinishedProjectsByLab(labId);
        } catch (Exception e) {
            logger.error("Error getting the number of finished projects for lab with ID {}", labId, e);
            throw new RuntimeException("Error getting the number of finished projects for lab with ID " + labId, e);
        }
    }

    /**
     * Retrieves the number of projects per lab from the database.
     *
     * @param labId the lab ID to get the number of projects for.
     * @return the number of projects for the lab.
     */
    public int totalCancelledProjectsPerLab(int labId) {
        logger.info("Getting the number of canceled projects per lab");

        try {
            return projectDao.numberOfCanceledProjectsByLab(labId);
        } catch (Exception e) {
            logger.error("Error getting the number of canceled projects for lab with ID {}", labId, e);
            throw new RuntimeException("Error getting the number of canceled projects for lab with ID " + labId, e);
        }
    }

    /**
     * Retrieves the average number of active users across all projects from the database.
     *
     * @return the average number of active users across all projects.
     */
    public double averageActiveUsersAcrossAllProjects() {
        logger.info("Getting the average number of active users across all projects");

        try {
            return m2MProjectUserDao.averageUsersInProjects();
        } catch (Exception e) {
            logger.error("Error getting the average number of active users across all projects", e);
            throw new RuntimeException("Error getting the average number of active users across all projects", e);
        }
    }

    /**
     * Retrieves the average number of members across all projects from the database.
     *
     * @return the average number of members across all projects.
     */
    public double averageProjectExecutionTime() {
        logger.info("Getting the average execution time of all projects");

        try {
            return projectDao.getAverageExecutionTime();
        } catch (Exception e) {
            logger.error("Error getting the average execution time of all projects", e);
            throw new RuntimeException("Error getting the average execution time of all projects", e);
        }
    }

    /**
     * Retrieves the statistics of projects per lab from the database.
     *
     * @param labId the lab ID to get the statistics of projects for.
     * @return the statistics of projects for the lab.
     */
    public StatisticsPerLab projectsStatsByLab (int labId) {
        logger.info("Getting the statistics of projects per lab");

        if (!LabEnum.isValidLabId(labId)) {
            logger.error("Error getting the statistics of projects for lab with ID {}: invalid lab ID", labId);
            throw new IllegalArgumentException("Invalid lab ID: " + labId);
        }

        long totalCompanyProjects;

        try {
            totalCompanyProjects = projectDao.getNumberOfProjects();
        } catch (Exception e) {
            logger.error("Error getting the statistics of projects for lab with ID {}", labId, e);
            throw new RuntimeException("Error getting the statistics of projects for lab with ID " + labId, e);
        }

        if (totalCompanyProjects == 0) {
            return new StatisticsPerLab(labId, 0, 0, 0, 0, 0, 0, 0, 0);
        }

        long totalProjects = totalProjectsPerLab(labId);

        double percentage = (double) totalProjects * 100 / totalCompanyProjects;

        int approvedProjects = totalApprovedProjectsPerLab(labId);

        double approvedPercentage = (double) approvedProjects * 100 / totalProjects;

        int finishedProjects = totalFinnishedProjectsPerLab(labId);

        double finishedPercentage = (double) finishedProjects * 100 / totalProjects;

        int canceledProjects = totalCancelledProjectsPerLab(labId);

        double canceledPercentage = (double) canceledProjects * 100 / totalProjects;

        return new StatisticsPerLab(labId, totalProjects, percentage, approvedProjects, approvedPercentage, finishedProjects, finishedPercentage, canceledProjects, canceledPercentage);

    }

    /**
     * Retrieves the statistics of projects from the database.
     *
     * @return the statistics of projects.
     */
    public ProjectStatistics projectStatistics () {
        logger.info("Getting the statistics of projects");

        ProjectStatistics projectStatistics = new ProjectStatistics();

        List<StatisticsPerLab> labStatistics = new ArrayList<>();

        double averageMembers = averageActiveUsersAcrossAllProjects();

        double averageExecutionTime = averageProjectExecutionTime();

        List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(LabEnum.class);

        int totalProjects = 0;

        try {
            totalProjects = projectDao.getNumberOfProjects();
        } catch (Exception e) {
            logger.error("Error getting the total number of projects", e);
            throw new RuntimeException("Error getting the total number of projects", e);
        }

        for (EnumDTO enumDto : enumDTOs) {
            try {
                StatisticsPerLab statisticsPerLab = projectsStatsByLab(enumDto.getId());
                labStatistics.add(statisticsPerLab);
            } catch (Exception e) {
                logger.error("Error getting the statistics of projects for lab with ID {}", enumDto.getId(), e);
            }
        }

        projectStatistics.setLabStatistics(labStatistics);
        projectStatistics.setAverageMembers(averageMembers);
        projectStatistics.setAverageExecutionTime(averageExecutionTime);
        projectStatistics.setTotalProjects(totalProjects);

        logger.info("Statistics of projects retrieved successfully");
        return projectStatistics;

    }
}