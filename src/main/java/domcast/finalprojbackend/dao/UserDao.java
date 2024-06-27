package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.entity.LabEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * UserDao is a Data Access Object (DAO) class for UserEntity.
 * It provides methods to interact with the database and perform operations on UserEntity.
 * @see UserEntity
 * @see UserBean
 * @see AbstractDao
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserDao.class);

    /**
     * Default constructor for UserDao.
     */
    public UserDao() {
        super(UserEntity.class);
    }

    /**
     * Finds a user by their id.
     *
     * @param id the id of the user
     * @return the UserEntity object if found, null otherwise
     */
    public  UserEntity findUserById(int id) {
        logger.info("Finding user by id {}", id);
        try {
            return (UserEntity) em.createNamedQuery("User.findUserById").setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("User with id {} not found", id);
            return null;
        }
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return the UserEntity object if found, null otherwise
     */
    public UserEntity findUserByEmail(String email) {
        logger.info("Finding user by email {}", email);
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("User with email {} not found", email);
            return null;
        }
    }

    /**
     * Finds a user by their validation token.
     *
     * @param token the validation token of the user
     * @return the UserEntity object if found, null otherwise
     */
    public UserEntity findUserByValidationToken(String token) {
        logger.info("Finding user by validation token {}", token);
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByValidationToken").setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("User with validation token {} not found", token);
            return null;
        }
    }

    /**
     * Finds a user by their active validation token or session token.
     *
     * @param token the validation token or session token of the user
     * @return the UserEntity object if found, null otherwise
     */
    public UserEntity findUserByActiveValidationOrSessionToken(String token) {
        logger.info("Finding user by active validation or session token {}", token);
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByActiveValidationOrSessionToken").setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("User with active validation or session token {} not found", token);
            return null;
        }
    }


    /**
     * Gets a list of users by criteria with pagination.
     *
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param nickname the nickname of the user
     * @param workplace the workplace of the user
     * @param orderBy the attribute by which to order the results
     * @param orderAsc whether to order the results in ascending order
     * @param pageNumber the page number
     * @param pageSize the number of records per page
     * @return a list of UserEntity objects
     */
    public List<UserEntity> getUsersByCriteria(String firstName, String lastName, String nickname, String workplace, String orderBy, boolean orderAsc, int pageNumber, int pageSize) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);

        Root<UserEntity> user = cq.from(UserEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (firstName != null && !firstName.isEmpty()) {
            predicates.add(cb.like(user.get("firstName"), "%" + firstName + "%"));
        }
        if (lastName != null && !lastName.isEmpty()) {
            predicates.add(cb.like(user.get("lastName"), "%" + lastName + "%"));
        }
        if (nickname != null && !nickname.isEmpty()) {
            predicates.add(cb.like(user.get("nickname"), "%" + nickname + "%"));
        }
        if (workplace != null && !workplace.isEmpty()) {
            LabEnum workplaceEnum = LabEnum.valueOf(workplace.toUpperCase());
            predicates.add(cb.equal(user.get("workplace").get("city"), workplaceEnum));
        }

        cq.select(user).where(cb.and(predicates.toArray(new Predicate[0])));

        if (orderBy != null && !orderBy.isEmpty()) {
            if (orderBy.equals("workplace")) {
                Join<UserEntity, LabEntity> join = user.join("workplace");
                if (orderAsc) {
                    cq.orderBy(cb.asc(join.get("city")));
                } else {
                    cq.orderBy(cb.desc(join.get("city")));
                }
            } else {
                if (orderAsc) {
                    cq.orderBy(cb.asc(user.get(orderBy)));
                } else {
                    cq.orderBy(cb.desc(user.get(orderBy)));
                }
            }
        }

        TypedQuery<UserEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize); // Adjust pageNumber to be 0-indexed
        query.setMaxResults(pageSize);

        try {
            return query.getResultList();
        } catch (Exception e) {
            logger.error("Error while getting users by criteria", e);
            return new ArrayList<>();
        }
    }

    /**
     * Gets the user password by id.
     *
     * @param id the id of the user
     * @return the password of the user
     */
    public String getUserPassword(int id) {
        logger.info("Getting user password");
        try {
            return (String) em.createNamedQuery("User.getUserPassword").setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            logger.error("User password not found");
            return null;
        }
    }

    /**
     * Sets the user password by id.
     *
     * @param id the id of the user
     * @param password the password to be set
     */
    public void setUserPassword(int id, String password) {
        logger.info("Setting user password");
        em.createNamedQuery("User.setUserPassword").setParameter("id", id).setParameter("password", password)
                .executeUpdate();
    }

    /**
     * Sets the user type by id.
     *
     * @param id the id of the user
     * @param type the type to be set
     * @return true if the user type was set, false otherwise
     */
    public boolean setUserType(int id, int type) {
        logger.info("Setting user type");
        try {
            TypeOfUserEnum userType = TypeOfUserEnum.fromId(type);
            em.createNamedQuery("User.setUserType").setParameter("id", id).setParameter("type", userType)
                    .executeUpdate();
            return true;
        } catch (Exception e) {
            logger.error("User type not set");
            return false;
        }
    }

    /**
     * Gets the user type by id.
     *
     * @param id the id of the user
     * @return the id of the user type
     */
    public int getUserType(int id) {
        logger.info("Getting user type");
        try {
            TypeOfUserEnum userType = (TypeOfUserEnum) em.createNamedQuery("User.getUserType").setParameter("id", id)
                    .getSingleResult();
            return userType.getId();
        } catch (NoResultException e) {
            logger.error("User type not found");
            return -1;
        }
    }

    /**
     * Checks if the user is an admin, given their session token.
     * @param token the session token of the user
     * @return true if the user is an admin, false otherwise
     */
    public boolean isUserAdmin(String token) {
        logger.info("Checking if user is admin");
        try {
            UserEntity user = (UserEntity) em.createNamedQuery("User.isUserAdminByToken")
                    .setParameter("token", token)
                    .getSingleResult();
            return user != null;
        } catch (NoResultException e) {
            logger.error("User with token {} is not an admin", token);
            return false;
        } catch (Exception e) {
            logger.error("Error while checking if user is admin: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the user exists by first and last name.
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @return true if the user exists, false otherwise
     */
    public boolean existsByFirstAndLastName(String firstName, String lastName) {
        Long count = (Long) em.createNamedQuery("User.existsByFirstAndLastName")
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .getSingleResult();
        return count > 0;
    }

    public boolean isUserAdminById(int id) {
        logger.info("Checking if user is admin by id");
        try {
            Long count = (Long) em.createNamedQuery("User.isUserAdminById")
                    .setParameter("id", id)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            logger.error("User with id {} is not an admin", id);
            return false;
        } catch (Exception e) {
            logger.error("Error while checking if user is admin by id: {}", e.getMessage());
            return false;
        }
    }
}