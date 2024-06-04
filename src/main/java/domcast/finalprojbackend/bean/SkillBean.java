package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.SkillDto;
import domcast.finalprojbackend.entity.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

@Stateless
public class SkillBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SkillBean.class);

    @EJB
    private UserDao userDao;
    @EJB
    private SkillDao skillDao;


    /**
     * Creates new skills in the database based on a list of SkillDTOs passed as parameter
     * @param skillsList List of SkillDto with the skills to be created.
     * Return List of String with the names of the skills created.
     */
    public ArrayList<String> createSkills(ArrayList<SkillDto> skillsList) {
        logger.info("Entering createSkills");

        if (skillsList == null) {
            logger.error("Skill's list must not be null");
            throw new IllegalArgumentException("Skill's list must not be null");
        }

        if (skillsList.isEmpty()) {
            logger.error("Skill's list must not be empty");
            throw new IllegalArgumentException("Skill's list must not be empty");
        }

        // Check if there are any null or empty skills and create a list with the names of the skills
        ArrayList<String> skillsNames = new ArrayList<>();
        for (SkillDto skill : skillsList) {
            if (skill == null || skill.getName() == null || skill.getName().isEmpty()) {
                logger.error("Skill must not be null");
                throw new IllegalArgumentException("Skill must not be null");
            }
            skillsNames.add(skill.getName());
        }

        logger.info("Creating skills");

        try {
            Set<SkillEntity> skills = skillDao.findSkillsByListOfNames(skillsNames);

            if (skills.size() == skillsList.size()) {
                logger.info("All skills already exist in database");
                return new ArrayList<>();
            }

            ArrayList<String> createdSkills = new ArrayList<>();

            for (String skill : skillsNames) {
                if (skills.stream().noneMatch(i -> i.getName().equals(skill))) {
                    SkillEntity newSkill = new SkillEntity();
                    newSkill.setName(skill);
                    skillDao.persist(newSkill);
                    createdSkills.add(skill);
                }
            }

            logger.info("Skills created");

            return createdSkills;

        } catch (Exception e) {
            logger.error("Error while creating skills: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Adds skills to a user
     * @param userId User ID to add skills to
     * @param skillsList List of skills to add
     */
    public void addSkillToUser(int userId, ArrayList<String> skillsList) {

        UserEntity user = null;
        try {
            user = userDao.findUserById(userId);
            logger.info("User found: {}", user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (skillsList == null) {
            logger.error("Skill's list must not be null");
            throw new IllegalArgumentException("Skill's list must not be null");
        }

        if (skillsList.isEmpty()) {
            logger.info("No skills to add to user");
            return;
        }

        logger.info("Adding skills to user");

        try {
            Set<SkillEntity> skills = skillDao.findSkillsByListOfNames(skillsList);

            if (skills.isEmpty()) {
                logger.info("No matching skills found in database");
                return;
            }

            for (SkillEntity skill : skills) {
                M2MUserSkill userSkill = new M2MUserSkill();
                userSkill.setUser(user);
                userSkill.setSkill(skill);
                user.addUserSkill(userSkill);
            }

            userDao.merge(user);

            logger.info("Skills added to user");

        } catch (NoResultException e) {
            logger.error("Error while finding skills: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error while adding skills to user: {}", e.getMessage());
            throw e;
        }
    }
}