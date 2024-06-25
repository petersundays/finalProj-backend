package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MProjectSkill;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Access Object (DAO) class for the project_skill table in the database.
 * Contains all the necessary methods to make CRUD operations with the project_skill table.
 * The class extends the AbstractDao class and has the M2MProjectSkill entity class as a parameter.
 * It also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class M2MProjectSkillDao extends AbstractDao<M2MProjectSkill> {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(M2MProjectSkillDao.class);

    /**
     * Default constructor for M2MProjectSkillDao.
     */
    public M2MProjectSkillDao() {
            super(M2MProjectSkill.class);
    }

    /**
     * Method to find all the active project-skill relationships for a project.
     * @param projectId the id of the project
     * @return a set with all the active project-skill relationships for the project
     */
    public Set<M2MProjectSkill> findAllActiveForProject(int projectId) {
        try {
            return new HashSet<>(em.createNamedQuery("M2MProjectSkill.findAllActiveForProject", M2MProjectSkill.class)
                    .setParameter("projectId", projectId)
                    .getResultList());
        } catch (Exception e) {
            logger.error("Error while finding all active project-skill relationships for project: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Method to find all the inactive project-skill relationships for a project.
     * @param projectId the id of the project
     * @return a set with all the inactive project-skill relationships for the project
     */
    public Set<M2MProjectSkill> findAllInactiveForProject(int projectId) {
        try {
            return new HashSet<>(em.createNamedQuery("M2MProjectSkill.findAllInactiveForProject", M2MProjectSkill.class)
                    .setParameter("projectId", projectId)
                    .getResultList());
        } catch (Exception e) {
            logger.error("Error while finding all inactive project-skill relationships for project: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Method to find all the project-skill relationships for a project.
     * @param projectId the id of the project
     * @return a set with all the project-skill relationships for the project
     */
    public Set<M2MProjectSkill> findAllforProject(int projectId) {
        try {
            return new HashSet<>(em.createNamedQuery("M2MProjectSkill.findAllforProject", M2MProjectSkill.class)
                    .setParameter("projectId", projectId)
                    .getResultList());
        } catch (Exception e) {
            logger.error("Error while finding all project-skill relationships for project: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Method to set a project-skill relationship as inactive.
     * @param projectId the id of the project
     * @param skillId the id of the skill
     */
    public void setSkillInactiveForProject(int projectId, int skillId) {
        try {
            em.createNamedQuery("M2MProjectSkill.setSkillInactiveForProject")
                    .setParameter("projectId", projectId)
                    .setParameter("skillId", skillId)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("Error while setting skill inactive for project: {}", e.getMessage());
        }
    }

    /**
     * Method to set a project-skill relationship as active.
     * @param projectId the id of the project
     * @param skillId the id of the skill
     */
    public void setSkillActiveForProject(int projectId, int skillId) {
        try {
            em.createNamedQuery("M2MProjectSkill.setSkillActiveForProject")
                    .setParameter("projectId", projectId)
                    .setParameter("skillId", skillId)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("Error while setting skill active for project: {}", e.getMessage());
        }
    }
}
