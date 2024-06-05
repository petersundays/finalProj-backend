package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.InterestDto;
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
                M2MUserInterest userInterest = new M2MUserInterest();
                userInterest.setUser(user);
                userInterest.setInterest(interest);
                user.addInterest(userInterest);
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

    public InterestEnum convertTypeToEnum(int type) {
        return InterestEnum.fromId(type);
    }
}
