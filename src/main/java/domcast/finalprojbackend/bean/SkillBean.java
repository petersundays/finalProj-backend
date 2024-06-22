package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import domcast.finalprojbackend.dto.skillDto.SkillToProject;
import domcast.finalprojbackend.dto.userDto.UpdateUserDto;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.SkillTypeEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class SkillBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SkillBean.class);

    @EJB
    private UserDao userDao;
    @EJB
    private SkillDao skillDao;
    @EJB
    private DataValidator dataValidator;


    /**
     * Creates new skills in the database based on a list of SkillDTOs passed as parameter
     * @param skillsList List of SkillDto with the skills to be created.
        * Return boolean true if all skills were created successfully, false otherwise.
        */
    public boolean createSkills(ArrayList<SkillDto> skillsList) {
        logger.info("Entering createSkills");

        if (skillsList == null) {
            logger.error("Skill's list is null, user did not update interests");
            return true;
        }

        if (skillsList.isEmpty()) {
            logger.error("Skill's list is empty, user did not update interests");
            return true;
        }

        // Check if there are any null or empty skills and create a list with the names of the skills
        ArrayList<String> skillsNames = dataValidator.validateAndExtractSkillNames(skillsList);

        logger.info("Creating skills");

        try {
            Set<SkillEntity> skills = skillDao.findSkillsByListOfNames(skillsNames);

            if (skills.size() == skillsList.size()) {
                logger.info("All skills already exist in database");
                return true;
            }

            for (String skill : skillsNames) {
                if (skills.stream().noneMatch(i -> i.getName().equals(skill))) {
                    SkillEntity newSkill = new SkillEntity();
                    newSkill.setName(skill);
                    // Get the corresponding SkillDto and set the type
                    SkillDto skillDto = skillsList.get(skillsNames.indexOf(skill));
                    newSkill.setType(convertTypeToEnum(skillDto.getType()));
                    skillDao.persist(newSkill);
                }
            }

            logger.info("Skills created");

            return true;

        } catch (Exception e) {
            logger.error("Error while creating skills: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Adds skills to a user
     * @param userId User ID to add skills to
     * @param skillsList List of skills to add
     */
    public void addSkillToUser(int userId, ArrayList<String> skillsList) {

        logger.info("Entering addSkillToUser");

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
                // Check if the user already has this skill
                if (user.getUserSkills().stream().noneMatch(us -> us.getSkill().equals(skill))) {
                    M2MUserSkill userSkill = new M2MUserSkill();
                    userSkill.setUser(user);
                    userSkill.setSkill(skill);
                    user.addUserSkill(userSkill);
                }
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

    /**
     * Converts a skill type from an integer to a SkillTypeEnum
     * @param type The integer representing the skill type
     * @return The SkillTypeEnum corresponding to the integer
     */
    public SkillTypeEnum convertTypeToEnum(int type) {
        return SkillTypeEnum.fromId(type);
    }

    /**
     * Updates the user's skills if they have changed.
     * This method iterates over the user's existing skills and updates them based on the new list of skills.
     * If a skill is in the new list, it is set to active. If a skill is not in the new list, it is set to inactive.
     * If a skill is in the new list but not in the existing list, it is added to the user's skills.
     * @param userEntity The user entity to update.
     * @param user The user DTO containing the new information.
     * @return The updated user entity.
     */
    public UserEntity updateUserSkillsIfChanged(UserEntity userEntity, UpdateUserDto user) {
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            // Convert the list of new skills to a set for efficient lookups
            Set<String> newSkills = new HashSet<>(user.getSkills());

            // Iterate over the existing relationships
            for (M2MUserSkill userSkill : userEntity.getUserSkills()) {
                String skillName = userSkill.getSkill().getName();

                if (newSkills.contains(skillName)) {
                    // The skill is in the new list, so ensure it's active
                    userSkill.setActive(true);
                    newSkills.remove(skillName);
                } else {
                    // The skill is not in the new list, so set it to inactive
                    userSkill.setActive(false);
                }
            }

            // Any remaining skills in the new list are new skills for the user
            for (String skillName : newSkills) {
                SkillEntity skillEntity = skillDao.findSkillByName(skillName);
                if (skillEntity != null) {
                    M2MUserSkill userSkill = new M2MUserSkill();
                    userSkill.setUser(userEntity);
                    userSkill.setSkill(skillEntity);
                    userSkill.setActive(true);
                    userEntity.addUserSkill(userSkill);
                }
            }

            userDao.merge(userEntity);
        }
        return userEntity;
    }

    /**
     * Gets the ids of the skills based on their names
     * @param skills The names of the skills
     * @return The ids of the skills
     */
    public Set<Integer> getSkillsIds(Set<SkillDto> skills) {
        if (skills == null) {
            return new HashSet<>();
        }

        logger.info("Getting skills ids");

        try {
            Set<Integer> skillsIds = new HashSet<>();
            for (SkillDto skill : skills) {
                SkillEntity skillEntity = skillDao.findSkillByName(skill.getName());
                if (skillEntity != null) {
                    skillsIds.add(skillEntity.getId());
                }
            }
            return skillsIds;
        } catch (Exception e) {
            logger.error("Error while getting skills ids: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    public Set<M2MProjectSkill> createRelationshipToProject (Set<Integer> skillsIds, ProjectEntity project) {
        logger.info("Entering createRelationshipToProject for project");

        Set<M2MProjectSkill> m2MProjectSkills = new HashSet<>();

        if (skillsIds == null || skillsIds.isEmpty()) {
            logger.error("Skills list is null or empty when creating relationship");
            return m2MProjectSkills;
        }

        if (project == null) {
            logger.error("Project is null");
            throw new IllegalArgumentException("Project is null");
        }

        for (Integer skillId : skillsIds) {
            SkillEntity skill = skillDao.findSkillById(skillId);
            if (skill == null) {
                logger.error("Skill not found with id while creating relationship: {}", skillId);
                continue;
            }

            M2MProjectSkill projectSkill = new M2MProjectSkill();
            projectSkill.setProject(project);
            projectSkill.setSkill(skill);
            m2MProjectSkills.add(projectSkill);
        }

        return m2MProjectSkills;
    }

    public Set<Integer> findSkillsIdsByListOfNames(Set<String> names) {
        logger.info("Entering findSkillsIdsByListOfNames");

        if (names == null || names.isEmpty()) {
            logger.error("Names list is null or empty");
            return new HashSet<>();
        }

        try {
            return skillDao.findSkillsIdsByListOfNames(new ArrayList<>(names));
        } catch (Exception e) {
            logger.error("Error while finding skills ids by names: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    public Set<SkillToProject> projectSkillToDto(Set<M2MProjectSkill> m2MProjectSkills) {
        logger.info("Entering m2mProjectSkillToSkillToProject");

        Set<SkillToProject> skills = new HashSet<>();
        if (m2MProjectSkills == null || m2MProjectSkills.isEmpty()) {
            logger.error("M2MProjectSkills list is null or empty");
            return skills;
        }

        for (M2MProjectSkill m2MProjectSkill : m2MProjectSkills) {
            if (m2MProjectSkill == null) {
                logger.error("Null M2MProjectSkill object found");
                continue;
            }
            SkillToProject skill = new SkillToProject();
            skill.setId(m2MProjectSkill.getSkill().getId());
            skill.setName(m2MProjectSkill.getSkill().getName());
            skill.setType(m2MProjectSkill.getSkill().getType().getId());
            skills.add(skill);
        }

        return skills;
    }
}
