package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.PasswordBean;
import domcast.finalprojbackend.dto.InterestDto;
import domcast.finalprojbackend.dto.SkillDto;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.dto.taskDto.NewTask;
import domcast.finalprojbackend.dto.userDto.FirstRegistration;
import domcast.finalprojbackend.dto.userDto.FullRegistration;
import domcast.finalprojbackend.dto.userDto.Login;
import domcast.finalprojbackend.enums.ComponentResourceEnum;
import domcast.finalprojbackend.enums.InterestEnum;
import domcast.finalprojbackend.enums.SkillTypeEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Stateless
public class DataValidator {

    private static final Logger logger = LogManager.getLogger(DataValidator.class);
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    @EJB
    private PasswordBean passwordBean;

    /**
     * Checks if the email is valid
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
     * @param login the login to be checked
     * @return boolean value indicating if the login is valid
     */
    public boolean isLoginValid (Login login) {
        logger.info("Checking if login is valid");

        return login != null && login.getEmail() != null && !login.getEmail().isBlank() &&
                login.getPassword() != null && !login.getPassword().isBlank();
    }

    /**
     * Checks if the image is valid
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

        return true;
    }

    /**
     * Checks if the mandatory data is valid for a new task
     * @param newTask the new task to be checked
     * @return boolean value indicating if the mandatory data is valid
     */
    public boolean isTaskMandatoryDataValid(NewTask<Integer> newTask) {
        logger.info("Checking if mandatory data is valid for new task");

        return newTask.getTitle() != null && !newTask.getTitle().isBlank() &&
                newTask.getDescription() != null && !newTask.getDescription().isBlank() &&
                newTask.getProjectedStartDate() != null && newTask.getDeadline() != null &&
                newTask.getDeadline().isAfter(newTask.getProjectedStartDate()) &&
                newTask.getResponsibleId() > 0 && newTask.getProjectId() > 0;
    }

    /**
     * Checks if the mandatory data is valid for detailed CR
     * @param detailedCR the detailed CR to be checked
     * @param projectId the id of the project where the CR will be created. It can be null if the CR is not being created in a project.
     * @return boolean value indicating if the mandatory data is valid
     */
    public boolean isCRMandatoryDataValid(DetailedCR detailedCR, Integer projectId) {
        logger.info("Checking if mandatory data is valid for detailed CR");

        boolean isValid = detailedCR.getName() != null && !detailedCR.getName().isBlank() &&
                detailedCR.getDescription() != null && !detailedCR.getDescription().isBlank() &&
                detailedCR.getBrand() != null && !detailedCR.getBrand().isBlank() &&
                detailedCR.getPartNumber() != null && detailedCR.getPartNumber() > 0 &&
                ComponentResourceEnum.isValidId(detailedCR.getType()) &&
                detailedCR.getQuantity() > 0 &&
                detailedCR.getSupplier() != null && !detailedCR.getSupplier().isBlank() &&
                detailedCR.getSupplierContact() > 0;

        if (projectId != null) {
            isValid = isValid && projectId > 0;
        }

        return isValid;
    }

    public boolean isPageNumberValid(int pageNumber) {
        logger.info("Checking if page number is valid");

        if (pageNumber <= 0) {
            logger.error("Page number must be greater than 0");
            throw new IllegalArgumentException("Page number must be greater than 0");
        }

        return true;
    }

    public boolean isPageSizeValid(int pageSize) {
        logger.info("Checking if page size is valid");

        if (pageSize <= 0) {
            logger.error("Page size must be greater than 0");
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        return true;
    }
}