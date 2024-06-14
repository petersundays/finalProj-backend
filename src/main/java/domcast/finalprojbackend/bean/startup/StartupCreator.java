package domcast.finalprojbackend.bean.startup;


import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.SystemBean;
import domcast.finalprojbackend.bean.user.PasswordBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.entity.*;
import domcast.finalprojbackend.enums.*;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Stateless EJB Bean responsible for creating default labs and a default user.
 * This is typically used at application startup to ensure necessary data is present in the database.
 */
@Stateless
public class StartupCreator implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(SystemBean.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private UserDao userDao;

    @Inject
    private LabDao labDao;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private SystemBean systemBean;

    @Inject
    private TokenBean tokenBean;

    @Inject
    private PasswordBean passwordBean;

    @Inject
    ProjectDao projectDao;

    /**
     * Creates default labs in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultLabs() {
        logger.info("Creating default labs");
        for (LabEnum city : LabEnum.values()) {
            LabEntity lab = em.find(LabEntity.class, city.getId());
            if (lab == null) {
                logger.info("Creating lab for city {}", city);
                lab = new LabEntity();
                lab.setCity(city);
                em.persist(lab);
            }
        }
        logger.info("Default labs created");
    }

    /**
     * Creates default skills in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultSkills() {
        logger.info("Creating default skills");

        String[] skillNames = {"Java", "Python", "JavaScript", "C++", "HTML", "CSS", "SQL", "Ruby", "PHP", "Swift"};
        SkillTypeEnum[] skillTypes = {SkillTypeEnum.HARDWARE, SkillTypeEnum.KNOWLEDGE, SkillTypeEnum.TOOLS, SkillTypeEnum.SOFTWARE,SkillTypeEnum.HARDWARE, SkillTypeEnum.KNOWLEDGE, SkillTypeEnum.TOOLS, SkillTypeEnum.SOFTWARE,SkillTypeEnum.HARDWARE, SkillTypeEnum.KNOWLEDGE};

        for (int i = 0; i < 10; i++) {
            SkillEntity skill = new SkillEntity();
            skill.setName(skillNames[i]);
            skill.setType(skillTypes[i]);
            em.persist(skill);
        }

        logger.info("Default skills created");
    }

    /**
     * Creates default interests in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultInterests() {
        logger.info("Creating default interests");

        String[] interestNames = {"Reading", "Traveling", "Cooking", "Hiking", "Photography", "Music", "Art", "Gaming", "Coding", "Sports"};
        InterestEnum[] interestTypes = {InterestEnum.KNOWLEDGE_AREA, InterestEnum.CAUSE, InterestEnum.THEME, InterestEnum.KNOWLEDGE_AREA, InterestEnum.CAUSE, InterestEnum.THEME, InterestEnum.KNOWLEDGE_AREA, InterestEnum.CAUSE, InterestEnum.THEME, InterestEnum.KNOWLEDGE_AREA};

        for (int i = 0; i < 10; i++) {
            InterestEntity interest = new InterestEntity();
            interest.setName(interestNames[i]);
            interest.setType(interestTypes[i]);
            em.persist(interest);
        }

        logger.info("Default interests created");
    }

    /**
     * Creates a default user in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultUsers() {
        logger.info("Creating default users");

        String[] firstNames = {"John", "Admin", "Bob", "Alice", "Charlie", "Eve", "John", "Trent", "Oscar", "Peggy"};
        String[] lastNames = {"Doe", "Admin", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez"};
        String[] nicknames = {"johnny", "admin", "bobby", "alice", "charlie", "eve", "mallory", "trent", "oscar", "peggy"};
        LabEnum[] labs = {LabEnum.LISBOA, LabEnum.COIMBRA, LabEnum.VISEU, LabEnum.PORTO, LabEnum.VILA_REAL, LabEnum.LISBOA, LabEnum.COIMBRA, LabEnum.VISEU, LabEnum.PORTO, LabEnum.VILA_REAL};

        for (int i = 0; i < 10; i++) {
            UserEntity user = userDao.findUserByEmail("defaultUserEmail" + (i+1));
            String password = passwordBean.hashPassword("password" + (i+1));
            if (user == null) {
                logger.info("Creating default user " + (i+1));
                user = new UserEntity();
                user.setEmail("user" + (i+1) + "@mail.com");
                user.setPassword(password);
                user.setFirstName(firstNames[i]);
                user.setLastName(lastNames[i]);
                user.setNickname(nicknames[i]);
                user.setBiography("biography" + (i+1));
                user.setType(TypeOfUserEnum.STANDARD);

                LabEntity lab = labDao.findLabByCity(labs[i].getValue());
                if (lab != null) {
                    user.setWorkplace(lab);
                }

                if (i == 0) {
                    user.setType(TypeOfUserEnum.NOT_CONFIRMED);
                    ValidationTokenEntity validationToken = tokenBean.generateValidationToken(user, 48*60, "127.0.0.1");
                    user.addValidationToken(validationToken);
                } else if (i == 1) {
                    user.setType(TypeOfUserEnum.ADMIN);
                    user.setEmail("admin@mail.com");
                    user.setPassword(passwordBean.hashPassword("admin"));
                } else if (i == 3 || i == 7 || i == 8) {
                    user.setVisible(true);
                }

                logger.info("Persisting default user " + (i+1));
                userDao.persist(user);
                logger.info("Default user " + (i+1) + " created");
            }
        }
    }

    /**
     * Creates default projects in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultProjects() {
        logger.info("Creating default projects");

        // Assuming SkillEntity, InterestEntity, LabEntity, and UserEntity instances are already created
        List<SkillEntity> skills = em.createQuery("SELECT s FROM SkillEntity s", SkillEntity.class).getResultList();
        List<InterestEntity> interests = em.createQuery("SELECT i FROM InterestEntity i", InterestEntity.class).getResultList();
        List<LabEntity> labs = em.createQuery("SELECT l FROM LabEntity l", LabEntity.class).getResultList();
        List<UserEntity> users = em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();

        // Get the maximum number of members allowed in a project
        SystemEntity systemEntity = em.find(SystemEntity.class, 1);
        int maxMembers = systemEntity.getMaxMembers();

        // Create a Random instance
        Random random = new Random();

        String[] projectNames = {"Climate Change Research", "AI Development", "Quantum Computing Study", "Cancer Treatment Research", "Space Exploration", "Renewable Energy Development", "Autonomous Vehicles", "Blockchain Technology", "Cybersecurity Enhancement", "Genetic Engineering"};
        String[] projectDescriptions = {"Researching the effects of climate change", "Developing artificial intelligence algorithms", "Studying the principles of quantum computing", "Researching new cancer treatments", "Exploring outer space", "Developing renewable energy sources", "Creating autonomous vehicles", "Implementing blockchain technology", "Enhancing cybersecurity measures", "Engineering genetic modifications"};
        ProjectStateEnum[] projectStates = {ProjectStateEnum.PLANNING, ProjectStateEnum.READY, ProjectStateEnum.APPROVED, ProjectStateEnum.IN_PROGRESS, ProjectStateEnum.CANCELED, ProjectStateEnum.FINISHED, ProjectStateEnum.PLANNING, ProjectStateEnum.READY, ProjectStateEnum.APPROVED, ProjectStateEnum.IN_PROGRESS};
        String[] componentResourceNames = { "Climate Data Analyzer", "AI Training Module", "Quantum Computer", "Cancer Cell Detector","Spacecraft", "Solar Panel", "Self-driving Car", "Blockchain Node", "Firewall", "DNA Sequencer"};
        String[] componentResourceBrands = { "BrandA", "BrandB", "BrandC", "BrandD", "BrandE", "BrandF", "BrandG", "BrandH", "BrandI", "BrandJ" };
        String[] componentResourceSuppliers = { "SupplierA", "SupplierB", "SupplierC", "SupplierD", "SupplierE", "SupplierF", "SupplierG", "SupplierH", "SupplierI", "SupplierJ" };

        // Shuffle the skills, interests, labs, and users list
        Collections.shuffle(skills);
        Collections.shuffle(interests);
        Collections.shuffle(labs);
        Collections.shuffle(users);

        for (int i = 0; i < 10; i++) {
            ProjectEntity project = new ProjectEntity();
            project.setName(projectNames[i]);
            project.setDescription(projectDescriptions[i]);
            project.setLab(labs.get(i % labs.size())); // Select a lab from the shuffled list
            project.setState(projectStates[i]);
            project.setCreationDate(LocalDateTime.now());
            project.setProjectedStartDate(LocalDateTime.now().plusDays(1));
            project.setDeadline(LocalDateTime.now().plusDays(30));

            // Set realStartDate and realEndDate based on the project's state
            if (projectStates[i] == ProjectStateEnum.PLANNING) {
                project.setRealStartDate(LocalDateTime.now());
            } else if (projectStates[i] == ProjectStateEnum.FINISHED) {
                project.setRealEndDate(LocalDateTime.now());
            }

            // Create and set project skills
            Set<M2MProjectSkill> projectSkills = new HashSet<>();
            for (int j = 0; j < 3; j++) { // Only select the first 3 skills from the shuffled list
                M2MProjectSkill projectSkill = new M2MProjectSkill();
                projectSkill.setProject(project);
                projectSkill.setSkill(skills.get(j));
                projectSkills.add(projectSkill);
            }
            project.setSkills(projectSkills);

            // Create and set project keywords
            Set<M2MKeyword> projectKeywords = new HashSet<>();
            for (int j = 0; j < 3; j++) { // Only select the first 3 interests from the shuffled list
                M2MKeyword projectKeyword = new M2MKeyword();
                projectKeyword.setProject(project);
                projectKeyword.setInterest(interests.get(j));
                projectKeywords.add(projectKeyword);
            }
            project.setKeywords(projectKeywords);
            
            // Create and set project component resources
            Set<M2MComponentProject> projectComponentResources = new HashSet<>();
            for (int j = 0; j < 5; j++) { // Create 5 componentResources for each project
                ComponentResourceEntity componentResource = new ComponentResourceEntity();
                componentResource.setName(componentResourceNames[j]);
                componentResource.setDescription("Description for " + componentResourceNames[j]);
                componentResource.setBrand(componentResourceBrands[j]);
                componentResource.setPartNumber((long) j);
                componentResource.setSupplier(componentResourceSuppliers[j]);
                componentResource.setSupplierContact(1234567890L); // Set a dummy supplier contact
                componentResource.setType(ComponentResourceEnum.COMPONENT); // Set type as COMPONENT
                em.persist(componentResource); // Persist the ComponentResourceEntity before using it

                M2MComponentProject projectComponentResource = new M2MComponentProject();
                projectComponentResource.setProject(project);
                projectComponentResource.setComponentResource(componentResource);
                projectComponentResource.setQuantity(1); // Set quantity as 1
                projectComponentResources.add(projectComponentResource);
            }
            project.setComponentResources(projectComponentResources);

            // Create and set project users
            Set<M2MProjectUser> projectUsers = new HashSet<>();
            int numUsers = 1 + random.nextInt(maxMembers); // Generate a random number between 1 and maxMembers
            for (int j = 0; j < numUsers; j++) {
                M2MProjectUser projectUser = new M2MProjectUser();
                projectUser.setProject(project);
                projectUser.setUser(users.get(j % users.size())); // Use modulo to avoid IndexOutOfBoundsException
                // Set the role as MAIN_MANAGER for the first user, and randomly assign PARTICIPANT or MANAGER for the rest
                if (j == 0) {
                    projectUser.setRole(ProjectUserEnum.MAIN_MANAGER);
                } else {
                    projectUser.setRole(random.nextBoolean() ? ProjectUserEnum.PARTICIPANT : ProjectUserEnum.MANAGER);
                }
                projectUser.setApproved(1); // Set approved as 1
                projectUser.setActive(true); // Set active as true
                projectUsers.add(projectUser);
            }
            project.setProjectUsers(projectUsers);

            em.persist(project);
        }

        logger.info("Default projects created");
    }

    /**
     * Sets the default system variables in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDefaultSystemVariables() {
        logger.info("Setting default system variables");
        try {
            int systemEntities = systemBean.numberOfSystemVariables();
            if (systemEntities == 0) {
                logger.info("Creating system entity");
                SystemEntity systemEntity = new SystemEntity();
                try {
                    systemEntity.setSessionTimeout(TypeOfUserEnum.ADMIN, 5);
                    systemEntity.setMaxUsers(TypeOfUserEnum.ADMIN, 5);
                } catch (IllegalArgumentException e) {
                    logger.error("User does not have admin privileges", e);
                    throw new RuntimeException("User does not have admin privileges", e);
                }
                em.persist(systemEntity);
                logger.info("System entity created");
            } else {
                logger.info("System entity already exists, no changes made");
            }
        } catch (PersistenceException e) {
            logger.error("Error persisting SystemEntity", e);
            throw new RuntimeException("Error persisting SystemEntity", e);
        } catch (Exception e) {
            logger.error("Error setting default system variables", e);
            throw new RuntimeException("Error setting default system variables", e);
        }
    }

}