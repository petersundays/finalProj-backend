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

        String[] interestNames = {"Reading", "Traveling", "Cybersecurity", "Hiking", "Photography", "Music", "Art", "Gaming", "Coding", "Sports"};
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
     * Creates default keywords in the database.
     * This method is transactional and requires a new transaction.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createDefaultKeywords() {
        logger.info("Creating default keywords");

        String[] keywordNames = {"Climate", "AI", "Quantum", "Cancer", "Space", "Coding", "Autonomous", "Blockchain", "Cybersecurity", "Genetic", "Research", "Development", "Study", "Treatment", "Gaming", "Energy", "Vehicles", "Technology", "Enhancement", "Engineering"};

        for (String keywordName : keywordNames) {
            KeywordEntity keyword = new KeywordEntity();
            keyword.setName(keywordName);
            em.persist(keyword);
        }

        logger.info("Default keywords created");
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

                if (i == 9) {
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

        List<SkillEntity> skills = em.createQuery("SELECT s FROM SkillEntity s", SkillEntity.class).getResultList();
        List<InterestEntity> interests = em.createQuery("SELECT i FROM InterestEntity i", InterestEntity.class).getResultList();
        List<KeywordEntity> keywords = em.createQuery("SELECT k FROM KeywordEntity k", KeywordEntity.class).getResultList();
        List<LabEntity> labs = em.createQuery("SELECT l FROM LabEntity l", LabEntity.class).getResultList();
        List<UserEntity> users = em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();

        // Get the maximum number of members allowed in a project
        SystemEntity systemEntity = em.find(SystemEntity.class, 1);
        int maxMembers = systemEntity.getMaxMembers();

        UserEntity user2 = em.find(UserEntity.class, 2);
        if (user2 == null) {
            throw new RuntimeException("User with id 2 not found");
        }

        // Create a Random instance
        Random random = new Random();

        // Create a HashSet to store the already used componentResource names and brands
        Set<String> usedComponentResourceNamesAndBrands = new HashSet<>();

        String[] projectNames = {"Climate Change Research", "AI Development", "Quantum Computing Study", "Cancer Treatment Research", "Space Exploration", "Renewable Energy Development", "Autonomous Vehicles", "Blockchain Technology", "Cybersecurity Enhancement", "Genetic Engineering"};
        String[] projectDescriptions = {"Researching the effects of climate change", "Developing artificial intelligence algorithms", "Studying the principles of quantum computing", "Researching new cancer treatments", "Exploring outer space", "Developing renewable energy sources", "Creating autonomous vehicles", "Implementing blockchain technology", "Enhancing cybersecurity measures", "Engineering genetic modifications"};
        ProjectStateEnum[] projectStates = {ProjectStateEnum.PLANNING, ProjectStateEnum.READY, ProjectStateEnum.APPROVED, ProjectStateEnum.IN_PROGRESS, ProjectStateEnum.CANCELED, ProjectStateEnum.FINISHED, ProjectStateEnum.PLANNING, ProjectStateEnum.READY, ProjectStateEnum.APPROVED, ProjectStateEnum.IN_PROGRESS};
        String[] componentResourceNames = { "Climate Data Analyzer", "AI Training Module", "Quantum Computer", "Cancer Cell Detector","Spacecraft", "Solar Panel", "Self-driving Car", "Blockchain Node", "Firewall", "DNA Sequencer"};
        String[] componentResourceBrands = { "ThinkPad", "Surface", "MacBook", "Galaxy", "Xperia", "Pavilion", "Inspiron", "Predator", "Omen", "Alienware" };
        String[] componentResourceSuppliers = { "Intel", "AMD", "Nvidia", "Microsoft", "Apple", "Samsung", "Sony", "LG", "Canon", "Dell" };
        String[] taskNames = {"Design Phase", "Development Phase", "Testing Phase", "Deployment Phase", "Maintenance Phase"};
        String[] taskDescriptions = {
                "Design the architecture of the project",
                "Develop the core features of the project",
                "Test the project for any bugs or issues",
                "Deploy the project in the production environment",
                "Maintain the project after deployment"
        };

        // Create a HashSet to store the already used supplier contacts and part numbers
        Set<Long> usedSupplierContacts = new HashSet<>();
        Set<Long> usedPartNumbers = new HashSet<>();

        // Shuffle the skills, interests, labs, and users list
        Collections.shuffle(skills);
        Collections.shuffle(interests);
        Collections.shuffle(keywords);
        Collections.shuffle(labs);
        Collections.shuffle(users);

        boolean isUser2MainManager = false;

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
            for (int j = 0; j < 3; j++) { // Only select the first 3 keywords from the shuffled list
                M2MKeyword projectKeyword = new M2MKeyword();
                projectKeyword.setProject(project);
                projectKeyword.setKeyword(keywords.get(j));
                projectKeywords.add(projectKeyword);
            }
            project.setKeywords(projectKeywords);

            // Create and set project component resources
            Set<M2MComponentProject> projectComponentResources = new HashSet<>();
            for (int j = 0; j < 5; j++) { // Create 5 componentResources for each project
                ComponentResourceEntity componentResource = new ComponentResourceEntity();

                // Generate a unique name and brand
                String name, brand;
                do {
                    name = componentResourceNames[random.nextInt(componentResourceNames.length)];
                    brand = componentResourceBrands[random.nextInt(componentResourceBrands.length)];
                } while (usedComponentResourceNamesAndBrands.contains(name + brand));
                usedComponentResourceNamesAndBrands.add(name + brand);


                componentResource.setName(name);
                componentResource.setDescription("Description for " + name);
                componentResource.setBrand(brand);

                // Generate a unique part number
                long partNumber;
                do {
                    partNumber = 100000 + random.nextInt(900000); // Generate a random number between 100000 and 999999
                } while (usedPartNumbers.contains(partNumber));
                usedPartNumbers.add(partNumber);
                componentResource.setPartNumber(partNumber);

                componentResource.setSupplier(componentResourceSuppliers[j % componentResourceSuppliers.length]); // Use modulo to avoid IndexOutOfBoundsException

                // Generate a unique supplier contact
                long range = 9999999999L - 1000000000L + 1;
                long supplierContact;
                do {
                    supplierContact = 1000000000L + (long)(Math.random() * range);
                } while (usedSupplierContacts.contains(supplierContact));
                usedSupplierContacts.add(supplierContact);
                componentResource.setSupplierContact(supplierContact);

                // Randomly set the type as COMPONENT or RESOURCE
                componentResource.setType(random.nextBoolean() ? ComponentResourceEnum.COMPONENT : ComponentResourceEnum.RESOURCE);
                em.persist(componentResource); // Persist the ComponentResourceEntity before using it

                M2MComponentProject projectComponentResource = new M2MComponentProject();
                projectComponentResource.setProject(project);
                projectComponentResource.setComponentResource(componentResource);
                projectComponentResource.setQuantity(random.nextInt(10) + 1); // Generate a random number between 1 and 10
                projectComponentResources.add(projectComponentResource);
            }
            project.setComponentResources(projectComponentResources);


            // Create and set project users
            Set<M2MProjectUser> projectUsers = new HashSet<>();
            int numUsers = 1 + random.nextInt(maxMembers); // Generate a random number between 1 and maxMembers
            for (int j = 0; j < numUsers; j++) {
                UserEntity user = users.get(j % users.size()); // Use modulo to avoid IndexOutOfBoundsException

                // Check if the user is already part of the project
                boolean isUserAlreadyInProject = project.getProjectUsers().stream()
                        .anyMatch(pu -> pu.getUser().equals(user));

                // If the user is not already part of the project, add them
                if (!isUserAlreadyInProject) {
                    M2MProjectUser projectUser = new M2MProjectUser();
                    projectUser.setProject(project);
                    projectUser.setUser(user);
                    // Set the role as MAIN_MANAGER for the first user, and randomly assign PARTICIPANT or MANAGER for the rest
                    if (j == 0) {
                        projectUser.setRole(ProjectUserEnum.MAIN_MANAGER);
                    } else {
                        projectUser.setRole(random.nextBoolean() ? ProjectUserEnum.PARTICIPANT : ProjectUserEnum.MANAGER);
                    }
                    projectUser.setApproved(true); // Set approved as 1
                    projectUser.setActive(true); // Set active as true
                    projectUsers.add(projectUser);
                }
            }

// Add user with id 2 to the project
            boolean isUser2InProject = project.getProjectUsers().stream()
                    .anyMatch(pu -> pu.getUser().equals(user2));

            if (!isUser2InProject) {
                M2MProjectUser projectUser2 = new M2MProjectUser();
                projectUser2.setProject(project);
                projectUser2.setUser(user2);
                projectUser2.setRole(ProjectUserEnum.PARTICIPANT);
                projectUser2.setApproved(true);
                projectUser2.setActive(true);
                projectUsers.add(projectUser2);

                // If user 2 has not been assigned as a main manager yet, assign them as the main manager for the current project
                if (!isUser2MainManager) {
                    projectUser2.setRole(ProjectUserEnum.MAIN_MANAGER);
                    isUser2MainManager = true;
                }
            }

            project.setProjectUsers(projectUsers);

            em.persist(project);

            // Create a list to hold the tasks of the current project
            List<TaskEntity> projectTasks = new ArrayList<>();

            // Generate a random number of tasks between 2 and 6 for each project
            int numTasks = 2 + random.nextInt(5);
            for (int j = 0; j < numTasks; j++) {
                // Create a task
                TaskEntity task = new TaskEntity();
                // Set the task title and description based on the predefined task names and descriptions
                task.setTitle(taskNames[j % taskNames.length]);
                task.setDescription(taskDescriptions[j % taskDescriptions.length]);
                task.setProjectedStartDate(project.getDeadline().minusDays(numTasks - j));
                task.setDeadline(project.getDeadline().minusDays(numTasks - j - 1));
                task.setProjectId(project);

                // Set the responsible user only if the user is part of the project
                UserEntity responsibleUser = users.get(j % users.size()); // Set the first user as the responsible
                if (project.getProjectUsers().stream().anyMatch(pu -> pu.getUser().equals(responsibleUser))) {
                    task.setResponsible(responsibleUser);
                } else {
                    // If the responsible user is not part of the project, set another user that is part of the project as responsible
                    Optional<UserEntity> anotherUser = project.getProjectUsers().stream().map(M2MProjectUser::getUser).findFirst();
                    anotherUser.ifPresent(task::setResponsible);
                }


                // Set the state of the task based on the project's state
                if (projectStates[i] == ProjectStateEnum.FINISHED) {
                    task.setState(TaskStateEnum.FINISHED);
                } else if (projectStates[i] == ProjectStateEnum.IN_PROGRESS || projectStates[i] == ProjectStateEnum.CANCELED) {
                    // The task's state can be PLANNED, IN_PROGRESS, or FINISHED
                    int taskStateIndex = random.nextInt(3);
                    if (taskStateIndex == 0) {
                        task.setState(TaskStateEnum.PLANNED);
                    } else if (taskStateIndex == 1) {
                        task.setState(TaskStateEnum.IN_PROGRESS);
                    } else {
                        task.setState(TaskStateEnum.FINISHED);
                    }
                } else {
                    task.setState(TaskStateEnum.PLANNED);
                }

                // Set the task executors
                Set<String> executors = new HashSet<>();
                for (M2MProjectUser projectUser : project.getProjectUsers()) {
                    executors.add(projectUser.getUser().getFirstName() + " " + projectUser.getUser().getLastName());
                }
                task.setOtherExecutors(executors);

                // If it's the last task, it's the presentation of the project
                if (j == numTasks - 1) {
                    task.setTitle("Presentation");
                    task.setDescription("Presentation of the project " + projectNames[i]);
                    task.setRealStartDate(project.getDeadline());
                }

                em.persist(task);

                // Add the task to the project's tasks set
                project.getTasks().add(task);

                // Randomly decide if the current task depends on any of the previous tasks in the same project
                if (!projectTasks.isEmpty() && random.nextBoolean()) {
                    TaskEntity dependentTask = projectTasks.get(random.nextInt(projectTasks.size()));
                    M2MTaskDependencies taskDependency = new M2MTaskDependencies();
                    taskDependency.setTask(task);
                    taskDependency.setDependentTask(dependentTask);
                    task.getDependencies().add(taskDependency);
                    dependentTask.getDependentTasks().add(taskDependency);
                }

                // Add the task to the list of tasks of the current project
                projectTasks.add(task);
            }

            // Create the presentation task for the project only if it doesn't exist
            List<TaskEntity> existingPresentationTasks = em.createQuery("SELECT t FROM TaskEntity t WHERE t.projectId = :project AND t.title = 'Presentation'", TaskEntity.class)
                    .setParameter("project", project)
                    .getResultList();

            if (existingPresentationTasks.isEmpty()) {
                TaskEntity presentationTask = new TaskEntity();
                presentationTask.setTitle("Presentation");
                presentationTask.setDescription("Presentation of the project " + projectNames[i]);

                // Set the projectedStartDate and deadline of the presentation task to the deadline of the project
                presentationTask.setProjectedStartDate(project.getDeadline());
                presentationTask.setDeadline(project.getDeadline());

                presentationTask.setProjectId(project);
                presentationTask.setState(TaskStateEnum.PLANNED);

                // Set the responsible user only if the user is part of the project
                UserEntity responsibleUser = users.get(i % users.size()); // Set the first user as the responsible
                if (project.getProjectUsers().stream().anyMatch(pu -> pu.getUser().equals(responsibleUser))) {
                    presentationTask.setResponsible(responsibleUser);
                } else {
                    // If the responsible user is not part of the project, set another user that is part of the project as responsible
                    Optional<UserEntity> anotherUser = project.getProjectUsers().stream().map(M2MProjectUser::getUser).findFirst();
                    anotherUser.ifPresent(presentationTask::setResponsible);
                }

                // Persist the presentation task
                em.persist(presentationTask);

                // Add the presentation task to the project's tasks set
                project.getTasks().add(presentationTask);
            }

            em.merge(project);
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
                    systemEntity.setMaxUsers(TypeOfUserEnum.ADMIN, 4);
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