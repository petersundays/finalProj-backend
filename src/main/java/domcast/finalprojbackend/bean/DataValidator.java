package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.PasswordBean;
import domcast.finalprojbackend.dao.M2MProjectUserDao;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.SessionTokenDao;
import domcast.finalprojbackend.dao.TaskDao;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.interestDto.InterestDto;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import domcast.finalprojbackend.dto.taskDto.NewTask;
import domcast.finalprojbackend.dto.userDto.FirstRegistration;
import domcast.finalprojbackend.dto.userDto.FullRegistration;
import domcast.finalprojbackend.dto.userDto.Login;
import domcast.finalprojbackend.entity.TaskEntity;
import domcast.finalprojbackend.enums.*;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.websocket.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Stateless
public class DataValidator {

    private static final Logger logger = LogManager.getLogger(DataValidator.class);
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final int MAX_STRING_LENGTH = 20;

    @EJB
    private PasswordBean passwordBean;

    @EJB
    private TaskDao taskDao;

    @EJB
    private SystemBean systemBean;

    @EJB
    private M2MProjectUserDao m2MProjectUserDao;

    @EJB
    private SessionTokenDao sessionTokenDao;

    @EJB
    private ProjectDao projectDao;


    /**
     * Checks if the email is valid
     *
     * @param email the email to be checked
     * @return boolean value indicating if the email is valid
     */
    public boolean isEmailValid(String email) {
        logger.info("Checking if email is valid");

        if (email == null) {
            logger.error("Email cannot be null");
            throw new IllegalArgumentException("Email cannot be null");
        }

        if (email.isBlank()) {
            logger.error("Email cannot be blank");
            throw new IllegalArgumentException("Email cannot be blank");
        }

        return pattern.matcher(email).matches();
    }

    /**
     * Checks if the input is valid
     *
     * @param firstRegistration the input to be checked
     * @return boolean value indicating if the input is valid
     */
    public boolean isInputValid(FirstRegistration firstRegistration) {
        logger.info("Checking if input is valid");

        if (firstRegistration == null) {
            logger.error("FirstRegistration object cannot be null");
            throw new IllegalArgumentException("FirstRegistration object cannot be null");
        }

        return isEmailValid(firstRegistration.getEmail()) && passwordBean.isPasswordValid(firstRegistration.getPassword());
    }

    public boolean isUserMandatoryDataValid(FullRegistration fullRegistration) {

        logger.info("Checking if mandatory data is valid for full registration");

        return fullRegistration.getFirstName() != null && !fullRegistration.getFirstName().isBlank() &&
                fullRegistration.getLastName() != null && !fullRegistration.getLastName().isBlank() &&
                fullRegistration.getWorkplace() != null && !fullRegistration.getWorkplace().isBlank();
    }

    /**
     * Checks if the login is valid
     *
     * @param login the login to be checked
     * @return boolean value indicating if the login is valid
     */
    public boolean isLoginValid(Login login) {
        logger.info("Checking if login is valid");

        return login != null && login.getEmail() != null && !login.getEmail().isBlank() &&
                login.getPassword() != null && !login.getPassword().isBlank();
    }

    /**
     * Checks if the image is valid
     *
     * @param bytes the image to be checked
     * @return boolean value indicating if the image is valid
     */
    public boolean isValidImage(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes)) != null;
        } catch (IOException e) {
            logger.error("Error while checking image: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates and extracts the interest names
     *
     * @param interestsList the list of interests to be validated
     * @return the list of interest names
     */
    public ArrayList<String> validateAndExtractInterestNames(ArrayList<InterestDto> interestsList) {
        logger.info("Validating and extracting interest names");

        ArrayList<String> interestsNames = new ArrayList<>();
        ArrayList<String> invalidInterests = new ArrayList<>();
        for (InterestDto interest : interestsList) {
            if (interest == null || interest.getName() == null || interest.getName().isEmpty() || !InterestEnum.containsId(interest.getType())) {
                logger.error("Invalid interest: {}", interest);
                assert interest != null;
                invalidInterests.add(interest.getName());
                continue;
            }
            interestsNames.add(interest.getName());
        }
        if (!invalidInterests.isEmpty()) {
            logger.error("Invalid interests: {}", invalidInterests);
            throw new IllegalArgumentException("Invalid interests: " + String.join(", ", invalidInterests));
        }
        logger.info("Interest names validated and extracted");
        return interestsNames;
    }

    /**
     * Validates and extracts the skill names
     *
     * @param skillsList the list of skills to be validated
     * @return the list of skill names
     */
    public ArrayList<String> validateAndExtractSkillNames(ArrayList<SkillDto> skillsList) {
        logger.info("Validating and extracting skill names");

        ArrayList<String> skillsNames = new ArrayList<>();
        ArrayList<String> invalidSkills = new ArrayList<>();
        for (SkillDto skill : skillsList) {
            if (skill == null || skill.getName() == null || skill.getName().isEmpty() || !SkillTypeEnum.containsId(skill.getType())) {
                logger.error("Invalid skill: {}", skill);
                assert skill != null;
                invalidSkills.add(skill.getName());
                continue;
            }
            skillsNames.add(skill.getName());
        }
        if (!invalidSkills.isEmpty()) {
            logger.error("Invalid skills: {}", invalidSkills);
            throw new IllegalArgumentException("Invalid skills: " + String.join(", ", invalidSkills));
        }
        logger.info("Skill names validated and extracted");
        return skillsNames;
    }

    /**
     * Checks if the id is valid
     *
     * @param id the id to be checked
     *           throws IllegalArgumentException if the id is not valid
     * @return boolean value indicating if the id is valid
     */
    public boolean isIdValid(int id) {
        logger.info("Checking if id is valid");

        if (id <= 0) {
            logger.error("Id must be greater than 0");
            throw new IllegalArgumentException("Id must be greater than 0");
        }

        logger.info("Id is valid");
        return true;
    }

    /**
     * Checks if the mandatory data is valid for a new task
     *
     * @param newTask the new task to be checked
     * @return boolean value indicating if the mandatory data is valid
     */
    public boolean isTaskMandatoryDataValid(NewTask<Integer> newTask) {
        logger.info("Checking if mandatory data is valid for new task");

        TaskEntity presentationTask;

        try {
            presentationTask = taskDao.findPresentationTaskInProject(newTask.getProjectId());
        } catch (Exception e) {
            logger.error("Error while finding presentation task in project: {}", e.getMessage());
            return false;
        }

        return newTask.getTitle() != null && !newTask.getTitle().isBlank() &&
                newTask.getDescription() != null && !newTask.getDescription().isBlank() &&
                newTask.getProjectedStartDate() != null &&
                !newTask.getProjectedStartDate().isBefore(LocalDateTime.now().toLocalDate().atStartOfDay()) &&
                newTask.getDeadline() != null &&
                newTask.getDeadline().isAfter(newTask.getProjectedStartDate()) &&
                newTask.getDeadline().isBefore(presentationTask.getProjectedStartDate()) &&
                newTask.getResponsibleId() > 0 && newTask.getProjectId() > 0;
    }

    /**
     * Checks if the start date of the new task is not before the deadline of its dependencies
     *
     * @param projectedStartDate the start date of the new task
     * @param dependencies       the dependencies of the new task
     * @return true if the start date of the new task is valid, false otherwise
     */
    public boolean isStartDateValid(LocalDateTime projectedStartDate, Set<TaskEntity> dependencies) {
        logger.info("Checking if the start date of the new task is not before the deadline of its dependencies");

        if (projectedStartDate == null) {
            logger.error("The projected start date cannot be null");
            throw new IllegalArgumentException("The projected start date cannot be null");
        }

        if (dependencies == null || dependencies.isEmpty()) {
            logger.info("The task has no dependencies, so its start date is always valid");
            return true;
        }

        int countInvalid = 0;
        for (TaskEntity dependency : dependencies) {
            if (dependency.getDeadline() == null) {
                logger.error("The deadline of a dependency cannot be null");
                throw new IllegalArgumentException("The deadline of a dependency cannot be null");
            }

            if (projectedStartDate.isBefore(dependency.getDeadline())) {
                countInvalid++;
            }
        }

        if (countInvalid > 0) {
            logger.error("The projected start date of the new task is earlier than the deadline of {} of its dependencies", countInvalid);
            return false;
        }

        return true;
    }

    /**
     * Checks if the deadline of the new task is valid
     * The deadline of the new task must be after the projected start date and before the deadline of the project
     *
     * @param taskDeadline              the deadline of the new task
     * @param taskProjectedStartDate    the projected start date of the new task
     * @param presentationTaskStartDate the projected start date the project's presentation task
     * @return boolean value indicating if the deadline of the new task is valid
     */
    public boolean isDeadlineValid(LocalDateTime taskDeadline, LocalDateTime taskProjectedStartDate, LocalDateTime presentationTaskStartDate) {
        logger.info("Checking if the deadline of the new task is valid");

        if (taskDeadline == null || taskProjectedStartDate == null || presentationTaskStartDate == null) {
            logger.error("The deadline of the new task is null");
            return false;
        }

        if (taskDeadline.isBefore(taskProjectedStartDate) || taskDeadline.isAfter(presentationTaskStartDate)) {
            logger.error("The deadline of the new task is not valid");
            return false;
        }

        return true;
    }

    /**
     * Checks if the mandatory data is valid for detailed CR
     *
     * @param detailedCR the detailed CR to be checked
     * @param projectId  the id of the project where the CR will be created. It can be null if the CR is not being created in a project.
     * @return boolean value indicating if the mandatory data is valid
     */
    public boolean isCRMandatoryDataValid(DetailedCR detailedCR, Integer projectId, Integer quantity) {
        if (detailedCR.getName() == null || detailedCR.getName().isBlank()) {
            throw new IllegalArgumentException("Name is null or blank");
        }

        if (detailedCR.getDescription() == null || detailedCR.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description is null or blank");
        }

        if (detailedCR.getBrand() == null || detailedCR.getBrand().isBlank()) {
            throw new IllegalArgumentException("Brand is null or blank");
        }

        if (detailedCR.getPartNumber() == null || detailedCR.getPartNumber() <= 0) {
            throw new IllegalArgumentException("PartNumber is null or not greater than 0");
        }

        if (!ComponentResourceEnum.isValidId(detailedCR.getType())) {
            throw new IllegalArgumentException("Type is not a valid ComponentResourceEnum id");
        }

        if (detailedCR.getSupplier() == null || detailedCR.getSupplier().isBlank()) {
            throw new IllegalArgumentException("Supplier is null or blank");
        }

        if (detailedCR.getSupplierContact() <= 0) {
            throw new IllegalArgumentException("SupplierContact is not greater than 0");
        }

        if (projectId != null && projectId <= 0) {
            throw new IllegalArgumentException("ProjectId is not greater than 0");
        }

        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("Quantity is not greater than 0");
        }

        return true;
    }

    /**
     * Checks if the page number is valid
     *
     * @param pageNumber the page number to be checked
     * @return boolean value indicating if the page number is valid
     */
    public boolean isPageNumberValid(int pageNumber) {
        logger.info("Checking if page number is valid");

        if (pageNumber <= 0) {
            logger.error("Page number must be greater than 0");
            throw new IllegalArgumentException("Page number must be greater than 0");
        }

        return true;
    }

    /**
     * Checks if the page size is valid
     *
     * @param pageSize the page size to be checked
     * @return boolean value indicating if the page size is valid
     */
    public boolean isPageSizeValid(int pageSize) {
        logger.info("Checking if page size is valid");

        if (pageSize <= 0) {
            logger.error("Page size must be greater than 0");
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        return true;
    }

    /**
     * Checks if the task entity is valid
     *
     * @param taskEntity the task entity to be checked
     * @return boolean value indicating if the task entity is valid
     */
    public boolean isChartTaskInfoValid(TaskEntity taskEntity) {
        logger.info("Checking if chart task info is valid");

        return taskEntity != null && taskEntity.getTitle() != null && !taskEntity.getTitle().isBlank() &&
                taskEntity.getState() != null && taskEntity.getProjectedStartDate() != null && taskEntity.getDeadline() != null;
    }

    public boolean isProjectMandatoryDataValid(NewProjectDto newProjectDto) {
        logger.info("Checking if mandatory data is valid for new project");

        return newProjectDto.getName() != null && !newProjectDto.getName().isBlank() &&
                newProjectDto.getDescription() != null && !newProjectDto.getDescription().isBlank() &&
                newProjectDto.getLabId() > 0;
    }

    /**
     * Validates the state change of a project
     *
     * @param currentStateEnum the current state of the project
     * @param newStateEnum     the new state of the project
     */
    public void validateStateChange(ProjectStateEnum currentStateEnum, ProjectStateEnum newStateEnum) {

        if (currentStateEnum == null || newStateEnum == null) {
            logger.error("Current state or new state is null");
            throw new IllegalArgumentException("Current state or new state is null");
        }

        logger.info("Validating state change from {} to {}", currentStateEnum, newStateEnum);

        if (currentStateEnum == newStateEnum) {
            logger.error("Project is already in state {}", newStateEnum);
            throw new IllegalArgumentException("Project is already in state " + newStateEnum);
        }

        if (currentStateEnum == ProjectStateEnum.CANCELED) {
            logger.error("Project is already canceled");
            throw new IllegalArgumentException("Project is already canceled");
        }

        if (currentStateEnum == ProjectStateEnum.FINISHED) {
            logger.error("Project is already finished");
            throw new IllegalArgumentException("Project is already finished");
        }

        if (currentStateEnum == ProjectStateEnum.PLANNING && (newStateEnum != ProjectStateEnum.READY && newStateEnum != ProjectStateEnum.CANCELED)) {
            logger.error("Project is in planning state and can only be set to ready or canceled");
            throw new IllegalArgumentException("Project is in planning state and can only be set to ready or canceled");
        }

        if (currentStateEnum == ProjectStateEnum.READY && newStateEnum != ProjectStateEnum.CANCELED) {
            logger.error("Project is in ready state and can only be set to canceled or approved by an admin");
            throw new IllegalArgumentException("Project is in ready state and can only be set to canceled");
        }

        if (currentStateEnum == ProjectStateEnum.APPROVED &&
                (newStateEnum != ProjectStateEnum.CANCELED && newStateEnum != ProjectStateEnum.IN_PROGRESS)) {
            logger.error("Project is in approved state and can only be set to canceled or in progress");
            throw new IllegalArgumentException("Project is in approved state and can only be set to canceled or in progress");
        }

        if (currentStateEnum == ProjectStateEnum.IN_PROGRESS &&
                (newStateEnum != ProjectStateEnum.CANCELED && newStateEnum != ProjectStateEnum.FINISHED)) {
            logger.error("Project is in in progress state and can only be set to canceled or finished");
            throw new IllegalArgumentException("Project is in in progress state and can only be set to canceled or finished");
        }

        logger.info("Successfully validated state change from {} to {}", currentStateEnum, newStateEnum);
    }

    /**
     * Validates the project approval
     *
     * @param currentStateEnum the current state of the project
     * @param newStateEnum     the new state of the project
     */
    public void validateProjectApproval(ProjectStateEnum currentStateEnum, ProjectStateEnum newStateEnum) {
        if (currentStateEnum == null || newStateEnum == null) {
            logger.error("Current state or new state is null while validating project approval");
            throw new IllegalArgumentException("Current state or new state is null while validating project approval");
        }

        logger.info("Validating project approval");

        if (currentStateEnum != ProjectStateEnum.READY) {
            logger.error("Project is not in ready state while validating project approval");
            throw new IllegalArgumentException("Project is not in ready state while validating project approval");
        }

        if (newStateEnum != ProjectStateEnum.APPROVED && newStateEnum != ProjectStateEnum.PLANNING) {
            logger.error("Invalid new state while validating project approval");
            throw new IllegalArgumentException("Invalid new state while validating project approval");
        }

        logger.info("Successfully validated project approval");
    }

    /**
     * Sanitizes a string by removing special characters
     *
     * @param str the string to be sanitized
     * @return the sanitized string
     */
    private String sanitizeString(String str) {
        return str.replaceAll("[^a-zA-Z0-9]", "");
    }

    public String getFirstWord(String str) {
        if (str == null) {
            return null;
        }

        // Remove all leading spaces
        str = str.replaceFirst("^\\s+", "");

        if (str.isEmpty() || str.length() > MAX_STRING_LENGTH) {
            return null;
        }

        // Split the string into words
        String[] words = str.split("\\s+");

        // If the string contains more than one word, consider only the first word
        if (words.length > 1) {
            str = words[0];
        }

        return str;
    }

    /**
     * Validates the project name
     *
     * @param name the name to be checked
     * @return boolean value indicating if the project name is valid
     */
    public boolean isValidName(String name) {
        String regex = "^[a-zA-Z0-9 _-]+$";
        return name != null && name.matches(regex);
    }

    /**
     * Validates the orderBy field for getting users by criteria
     *
     * @param orderBy the orderBy field to be checked
     * @return boolean value indicating if the orderBy field is valid
     */
    public boolean isOrderByValidForUser(String orderBy) {

        if (orderBy == null || orderBy.isBlank()) {
            logger.error("OrderBy field is null or blank while getting users by criteria");
            throw new IllegalArgumentException("OrderBy field is null or blank");
        }

        // Validate orderBy field
        List<String> allowedOrderByFields = Arrays.asList("firstName", "lastName", "nickname", "lab");
        if (!allowedOrderByFields.contains(orderBy)) {
            logger.error("Invalid orderBy field while getting users by criteria");
            throw new IllegalArgumentException("Invalid orderBy field");
        }

        logger.info("OrderBy field is valid for getting users by criteria");
        return true;
    }

    /**
     * Validates the orderBy field for getting projects by criteria
     *
     * @param orderBy the orderBy field to be checked
     * @return boolean value indicating if the orderBy field is valid
     */
    public boolean isOrderByValidForProject(String orderBy) {

        if (orderBy == null || orderBy.isBlank()) {
            logger.error("OrderBy field is null or blank while getting projects by criteria");
            throw new IllegalArgumentException("OrderBy field is null or blank");
        }

        // Validate orderBy field
        List<String> allowedOrderByFields = Arrays.asList("name", "lab", "readyDate", "state", "availablePlaces");
        if (!allowedOrderByFields.contains(orderBy)) {
            logger.error("Invalid orderBy field while getting projects by criteria");
            throw new IllegalArgumentException("Invalid orderBy field");
        }

        logger.info("OrderBy field is valid for getting projects by criteria");
        return true;
    }

    /**
     * Validates the user search criteria
     *
     * @param lab        the lab to be checked
     * @param orderBy    the order by to be checked
     * @param pageNumber the page number to be checked
     * @param pageSize   the page size to be checked
     * @return boolean value indicating if the user search criteria is valid
     */
    public boolean validateSearchCriteria(int lab, String orderBy, int pageNumber, int pageSize) {

        logger.info("Validating user search criteria");

        if (!isPageSizeValid(pageSize)) {
            logger.error("Invalid page size while getting users by criteria");
            throw new IllegalArgumentException("Invalid page size");
        }

        if (!isPageNumberValid(pageNumber)) {
            logger.error("Invalid page number while getting users by criteria");
            throw new IllegalArgumentException("Invalid page number");
        }

        if (lab != 0) {
            if (!LabEnum.isValidLabId(lab)) {
                logger.error("Invalid lab while getting users by criteria");
                throw new IllegalArgumentException("Invalid lab");
            }
        }

        logger.info("Search criteria is valid");
        return true;
    }


    /**
     * Checks if there are available places in the project
     *
     * @param projectId the id of the project to be checked
     * @return boolean value indicating if there are available places in the project
     */
    public boolean availablePlacesInProject(int projectId) {
        logger.info("Checking if there are available places in the project");

        if (!isIdValid(projectId)) {
            logger.error("Invalid project id while checking available places in the project");
            throw new IllegalArgumentException("Invalid project id");
        }

        int numberOfActiveUsers;
        int maxUsers;

        try {
            numberOfActiveUsers = m2MProjectUserDao.getNumberOfActiveUsersInProject(projectId);
        } catch (Exception e) {
            logger.error("Error while getting number of active users in project: {}", e.getMessage());
            return false;
        }

        try {
            maxUsers = systemBean.getProjectMaxUsers();
        } catch (Exception e) {
            logger.error("Error while getting project max users: {}", e.getMessage());
            return false;
        }

        if (numberOfActiveUsers >= maxUsers) {
            logger.error("There are no available places in the project");
            return false;
        }

        logger.info("There are available places in the project");
        return true;
    }

    /**
     * Checks if the token is valid for websocket
     *
     * @param token    the token to be checked
     * @param sessions the sessions to be checked
     * @return boolean value indicating if the token is valid for websocket
     */
    public boolean isTokenValidForWebSocket(String token, HashMap<String, Session> sessions) {
        logger.info("Checking if token is valid for websocket");

        if (token == null || token.isBlank()) {
            logger.error("Token is null or blank while checking if token is valid for websocket");
            return false;
        }

        if (sessions == null) {
            logger.error("Sessions is null while checking if token is valid for websocket");
            return false;
        }

        boolean isTokenActive;
        try {
            isTokenActive = sessionTokenDao.isTokenActive(token);
        } catch (Exception e) {
            logger.error("Error while checking if token is active: {}", e.getMessage());
            return false;
        }

        if (!isTokenActive) {
            logger.info("Token is not active for websocket");
            return false;
        }

        if (sessions.containsKey(token)) {
            logger.info("Token already exists in sessions");
            return false;
        }

        logger.info("Token is valid for websocket");
        return true;
    }

}