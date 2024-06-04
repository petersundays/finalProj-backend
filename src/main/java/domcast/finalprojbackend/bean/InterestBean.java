package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.InterestDto;
import domcast.finalprojbackend.entity.InterestEntity;
import domcast.finalprojbackend.entity.M2MUserInterest;
import domcast.finalprojbackend.entity.UserEntity;
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


    /**
     * Creates new interests in the database based on a list of InterestDTOs passed as parameter
     * @param interestsList List of InterestDto with the interests to be created.
     * Return List of String with the names of the interests created.
     */
    public ArrayList<String> createInterests(ArrayList<InterestDto> interestsList) {
        logger.info("Entering createInterests");

        if (interestsList == null) {
            logger.error("Interest's list must not be null");
            throw new IllegalArgumentException("Interest's list must not be null");
        }

        if (interestsList.isEmpty()) {
            logger.error("Interest's list must not be empty");
            throw new IllegalArgumentException("Interest's list must not be empty");
        }

        // Check if there are any null or empty interests and create a list with the names of the interests
        ArrayList<String> interestsNames = new ArrayList<>();
        for (InterestDto interest : interestsList) {
            if (interest == null || interest.getName() == null || interest.getName().isEmpty() || interest.getType() == null) {
                logger.error("Interest must not be null");
                throw new IllegalArgumentException("Interest must not be null");
            }
            interestsNames.add(interest.getName());
        }

        logger.info("Creating interests");

        try {
            Set<InterestEntity> interests = interestDao.findInterestsByListOfNames(interestsNames);

            if (interests.size() == interestsList.size()) {
                logger.info("All interests already exist in database");
                return new ArrayList<>();
            }

            ArrayList<String> createdInterests = new ArrayList<>();

            for (String interest : interestsNames) {
                if (interests.stream().noneMatch(i -> i.getName().equals(interest))) {
                    InterestEntity newInterest = new InterestEntity();
                    newInterest.setName(interest);
                    newInterest.setType(interestsList.get(interestsNames.indexOf(interest)).getType());
                    interestDao.persist(newInterest);
                    createdInterests.add(interest);
                }
            }

            logger.info("Interests created");

            return createdInterests;

        } catch (Exception e) {
            logger.error("Error while creating interests: {}", e.getMessage());
            throw e;
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
}