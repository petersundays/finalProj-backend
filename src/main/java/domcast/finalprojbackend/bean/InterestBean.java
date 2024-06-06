package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.InterestDto;
import domcast.finalprojbackend.dto.UserDto.UpdateUserDto;
import domcast.finalprojbackend.entity.InterestEntity;
import domcast.finalprojbackend.entity.M2MUserInterest;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.InterestEnum;
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
public class InterestBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(InterestBean.class);

    @EJB
    private UserDao userDao;
    @EJB
    private InterestDao interestDao;
    @EJB
    private ValidatorAndHasher validatorAndHasher;


    /**
     * Creates new interests in the database based on a list of InterestDTOs passed as parameter
     * @param interestsList List of InterestDto with the interests to be created.
     * Return boolean true if all interests were created successfully, false otherwise.
     */
    public boolean createInterests(ArrayList<InterestDto> interestsList) {
        logger.info("Entering createInterests");

        if (interestsList == null) {
            logger.info("Interest's list is null, user did not update interests");
            return true;
        }

        if (interestsList.isEmpty()) {
            logger.info("Interest's list is empty, user did not update interests");
            return true;
        }

        // Check if there are any null or empty interests and create a list with the names of the interests
        ArrayList<String> interestsNames = validatorAndHasher.validateAndExtractInterestNames(interestsList);

        logger.info("Creating interests");

        try {
            Set<InterestEntity> interests = interestDao.findInterestsByListOfNames(interestsNames);

            if (interests.size() == interestsList.size()) {
                logger.info("All interests already exist in database");
                return true;
            }

            for (String interest : interestsNames) {
                if (interests.stream().noneMatch(i -> i.getName().equals(interest))) {
                    InterestEntity newInterest = new InterestEntity();
                    newInterest.setName(interest);

                    // Get the corresponding InterestDto and set the type
                    InterestDto interestDto = interestsList.get(interestsNames.indexOf(interest));
                    newInterest.setType(convertTypeToEnum(interestDto.getType()));
                    interestDao.persist(newInterest);
                }
            }

            logger.info("Interests created");

            return true;

        } catch (Exception e) {
            logger.error("Error while creating interests: {}", e.getMessage());
            return false;
        }
    }


    /**
     * Adds interests to a user
     * @param userId User ID to add interests to
     * @param interestsList List of interests to add
     */
    public void addInterestToUser(int userId, ArrayList<String> interestsList) {

        logger.info("Entering addInterestToUser");

        UserEntity user = null;
        try {
            user = userDao.findUserById(userId);
            logger.info("User found: {}", user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (interestsList == null) {
            logger.error("Interest's list must not be null");
            throw new IllegalArgumentException("Interest's list must not be null");
        }

        if (interestsList.isEmpty()) {
            logger.info("No interests to add to user");
            return;
        }

        logger.info("Adding interests to user");

        try {
            Set<InterestEntity> interests = interestDao.findInterestsByListOfNames(interestsList);

            if (interests.isEmpty()) {
                logger.info("No matching interests found in database");
                return;
            }

            for (InterestEntity interest : interests) {
                // Check if the user already has this interest
                if (user.getInterests().stream().noneMatch(ui -> ui.getInterest().equals(interest))) {
                    M2MUserInterest userInterest = new M2MUserInterest();
                    userInterest.setUser(user);
                    userInterest.setInterest(interest);
                    user.addInterest(userInterest);
                }
            }

            userDao.merge(user);

            logger.info("Interests added to user");

        } catch (NoResultException e) {
            logger.error("Error while finding interests: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error while adding interests to user: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Converts an interest type to an enum.
     * @param type The interest type to convert.
     * @return The corresponding InterestEnum.
     */
    public InterestEnum convertTypeToEnum(int type) {
        return InterestEnum.fromId(type);
    }

    /**
     * Updates the user's interests if they have changed.
     * This method iterates over the user's existing interests and updates them based on the new list of interests.
     * If an interest is in the new list, it is set to active. If an interest is not in the new list, it is set to inactive.
     * If an interest is in the new list but not in the existing list, it is added to the user's interests.
     * @param userEntity The user entity to update.
     * @param user The user DTO containing the new information.
     * @return The updated user entity.
     */
    public UserEntity updateUserInterestsIfChanged(UserEntity userEntity, UpdateUserDto user) {
        if (user.getInterests() != null && !user.getInterests().isEmpty()) {
            // Convert the list of new interests to a set for efficient lookups
            Set<String> newInterests = new HashSet<>(user.getInterests());

            // Iterate over the existing relationships
            for (M2MUserInterest userInterest : userEntity.getInterests()) {
                String interestName = userInterest.getInterest().getName();

                if (newInterests.contains(interestName)) {
                    // The interest is in the new list, so ensure it's active
                    userInterest.setActive(true);
                    newInterests.remove(interestName);
                } else {
                    // The interest is not in the new list, so set it to inactive
                    userInterest.setActive(false);
                }
            }

            // Any remaining interests in the new list are new interests for the user
            for (String interestName : newInterests) {
                InterestEntity interestEntity = interestDao.findInterestByName(interestName);
                if (interestEntity != null) {
                    M2MUserInterest userInterest = new M2MUserInterest();
                    userInterest.setUser(userEntity);
                    userInterest.setInterest(interestEntity);
                    userInterest.setActive(true);
                    userEntity.addInterest(userInterest);
                }
            }

            userDao.merge(userEntity);
        }
        return userEntity;
    }
}
