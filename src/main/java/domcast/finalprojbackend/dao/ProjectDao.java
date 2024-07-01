package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MKeyword;
import domcast.finalprojbackend.entity.M2MProjectSkill;
import domcast.finalprojbackend.entity.M2MProjectUser;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.enums.ProjectStateEnum;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProjectDao extends AbstractDao<ProjectEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserDao.class);

    /**
     * Default constructor for ProjectDao.
     */
    public ProjectDao() {
        super(ProjectEntity.class);
    }

    /**
     * Finds a project by its id.
     *
     * @param id the id of the project
     * @return the ProjectEntity object if found, null otherwise
     */
    public ProjectEntity findProjectById(int id) {
        logger.info("Finding project by id {}", id);
        try {
            return (ProjectEntity) em.createNamedQuery("Project.findProjectById").setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.error("Project with id {} not found", id);
            return null;
        }
    }

    /**
     * Checks if a user is active and approved in a project.
     *
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is active and approved in the project
     */
    public boolean isUserActiveAndApprovedInProject(int userId, int projectId) {
        try {
            M2MProjectUser result = em.createNamedQuery("M2MProjectUser.isUserActiveAndApprovedInProject", M2MProjectUser.class)
                    .setParameter("userId", userId)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            return result != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Checks if a user is part of a project and active.
     *
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is part of the project and active
     */
    public boolean isUserPartOfProjectAndActive(int userId, int projectId) {
        try {
            Long count = em.createNamedQuery("M2MProjectUser.isUserPartOfProjectAndActive", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    /**
     * Checks if a user is a manager in a project.
     *
     * @param userId the id of the user
     * @param projectId the id of the project
     * @return boolean value indicating if the user is a manager in the project
     */
    public boolean isUserManagerInProject(int userId, int projectId) {
        try {
            Long count = em.createNamedQuery("M2MProjectUser.isUserManagerInProject", Long.class)
                    .setParameter("userId", userId)
                    .setParameter("projectId", projectId)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            return false;
        }
    }

    public List<ProjectEntity> getProjectsByCriteria(int userId, String name, int lab, int state, String keyword, int skill, int maxUsers, String orderBy, boolean orderAsc, int pageNumber, int pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);

        Root<ProjectEntity> project = cq.from(ProjectEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (userId != 0) {
            Join<ProjectEntity, M2MProjectUser> joinProjectUser = project.join("projectUsers");
            if (joinProjectUser != null) {
                predicates.add(cb.equal(joinProjectUser.get("user").get("id"), userId));
            }
        }

        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(project.get("name"), "%" + name + "%"));
        }

        if (lab != 0) {
            predicates.add(cb.equal(project.get("lab").get("id"), lab));
        }

        if (state != 0) {
            predicates.add(cb.equal(project.get("state"), ProjectStateEnum.fromId(state)));
        }

        if (keyword != null && !keyword.isEmpty()) {
            Join<ProjectEntity, M2MKeyword> joinKeyword = project.join("keywords");
            predicates.add(joinKeyword.get("keyword").get("name").in(keyword));
        }

        if (skill != 0) {
            Join<ProjectEntity, M2MProjectSkill> joinSkill = project.join("skills", JoinType.INNER);
            Predicate skillNamePredicate = cb.equal(joinSkill.get("skill").get("id"), skill);
            Predicate skillActivePredicate = cb.isTrue(joinSkill.get("active"));
            predicates.add(cb.and(skillNamePredicate, skillActivePredicate));
        }

        cq.select(project).where(cb.and(predicates.toArray(new Predicate[0])));

        if (orderBy != null && !orderBy.isEmpty()) {
            switch (orderBy) {
                case "state" -> {
                    if (orderAsc) {
                        cq.orderBy(cb.asc(project.get("state")));
                    } else {
                        cq.orderBy(cb.desc(project.get("state")));
                    }
                }
                case "readyDate" -> {
                    if (orderAsc) {
                        cq.orderBy(cb.asc(cb.coalesce(project.get("readyDate"), LocalDateTime.MAX)));
                        // coalesce is used
                        // to handle null values
                        // which will be the last in the order
                    } else {
                        cq.orderBy(cb.desc(cb.coalesce(project.get("readyDate"), LocalDateTime.MIN)));
                        // coalesce is used
                        // to handle null values
                        // which will be the first in the order
                    }
                }
                case "lab" -> {
                    if (orderAsc) {
                        cq.orderBy(cb.asc(project.get("lab").get("id")));
                    } else {
                        cq.orderBy(cb.desc(project.get("lab").get("id")));
                    }
                }
                case "name" -> {
                    if (orderAsc) {
                        cq.orderBy(cb.asc(project.get("name")));
                    } else {
                        cq.orderBy(cb.desc(project.get("name")));
                    }
                }
                case "availablePlaces" -> {
                    // Subquery to calculate the number of active users in each project
                    Subquery<Long> activeUsersSubquery = cq.subquery(Long.class);
                    Root<M2MProjectUser> projectUser = activeUsersSubquery.from(M2MProjectUser.class);
                    activeUsersSubquery.select(cb.count(projectUser));
                    activeUsersSubquery.where(cb.and(
                            cb.equal(projectUser.get("project"), project),
                            cb.isTrue(projectUser.get("active"))
                    ));

                    // Expression to calculate the number of places still available
                    Expression<Long> availablePlaces = cb.diff((long) maxUsers, activeUsersSubquery);

                    if (orderAsc) {
                        cq.orderBy(cb.asc(availablePlaces));
                    } else {
                        cq.orderBy(cb.desc(availablePlaces));
                    }
                }
            }
        }

        TypedQuery<ProjectEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        try {
            return query.getResultList();
        } catch (NoResultException e) {
            logger.error("No projects found with the given criteria", e);
            return new ArrayList<>();
        } catch (PersistenceException e) {
            logger.error("Database error while getting projects by criteria", e);
            return new ArrayList<>();
        }
    }
}
