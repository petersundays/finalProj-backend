package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.entity.InterestEntity;
import domcast.finalprojbackend.entity.SkillEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SkillDao is a Data Access Object (DAO) class for SkillEntity.
 * It provides methods to interact with the database and perform operations on SkillEntity.
 * @see SkillEntity
 * @see AbstractDao
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class SkillDao extends AbstractDao<SkillEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SkillDao.class);

    /**
     * Default constructor for SkillDao.
     */
    public SkillDao() {
        super(SkillEntity.class);
    }

    /**
     * Finds a skill by its name.
     *
     * @param name the name of the skill
     * @return the SkillEntity object if found, null otherwise
     */

    public SkillEntity findSkillByName(String name) {
        logger.info("Finding skill by name {}", name);
        try {
            return (SkillEntity) em.createNamedQuery("Skill.findSkillByName").setParameter("name", name)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("Skill with name {} not found", name);
            return null;
        }
    }

    /**
     * Finds a set of skills by their names.
     *
     * @param names the names of the skills
     * @return the set of SkillEntity objects if found, null otherwise
     */
    public Set<SkillEntity> findSkillsByListOfNames(List<String> names) {
        logger.info("Finding skills by names {}", names);

        try {
            List<SkillEntity> resultList = em.createNamedQuery("Skill.findSkillsByListOfNames", SkillEntity.class)
                    .setParameter("names", names)
                    .getResultList();
            return new HashSet<>(resultList);

        } catch (NoResultException e) {
            logger.error("Skills with names {} not found", names);
            return null;
        }
    }
}
