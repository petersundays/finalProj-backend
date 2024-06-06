package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.LabEnum;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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


    public List<UserEntity> getUsersByCriteria(String firstName, String lastName, String nickname, String workplace) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);

        Root<UserEntity> user = cq.from(UserEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (firstName != null) {
            predicates.add(cb.equal(user.get("firstName"), firstName));
        }
        if (lastName != null) {
            predicates.add(cb.equal(user.get("lastName"), lastName));
        }
        if (nickname != null) {
            predicates.add(cb.equal(user.get("nickname"), nickname));
        }
        if (workplace != null) {
            LabEnum workplaceEnum = LabEnum.valueOf(workplace.toUpperCase());
            predicates.add(cb.equal(user.get("workplace").get("city"), workplaceEnum));
        }

        cq.select(user).where(cb.and(predicates.toArray(new Predicate[0])));

        TypedQuery<UserEntity> query = em.createQuery(cq);
        return query.getResultList();
    }
}