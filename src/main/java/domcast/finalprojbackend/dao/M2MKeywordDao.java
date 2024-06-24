package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.M2MKeyword;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

@Stateless
public class M2MKeywordDao extends AbstractDao<M2MKeyword> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(M2MKeywordDao.class);

    /**
     * Default constructor for KeywordDao.
     */
    public M2MKeywordDao() {
        super(M2MKeyword.class);
    }

    /**
     * Finds all the keywords of a project.
     *
     * @param projectId the id of the project
     * @return the list of keywords
     */
    public Set<M2MKeyword> findAllKeywordsByProject(int projectId) {

        logger.info("Entering findAllKeywordsByProject");

        try {
            return new HashSet<>(em.createNamedQuery("M2MKeyword.findAllKeywordsByProject", M2MKeyword.class)
                    .setParameter("projectId", projectId)
                    .getResultList());
        } catch (PersistenceException e) {
            logger.error("Error finding all keywords by project: {}", e.getMessage());
            return new HashSet<>();
        }
    }

/**
     * Sets a keyword as active for a project.
     *
     * @param projectId the id of the project
     * @param keywordId the id of the keyword
     */
    public void setKeywordActiveForProject(int projectId, int keywordId) {

        logger.info("Entering setKeywordActiveForProject");

        try {
            em.createNamedQuery("M2MKeyword.setKeywordActiveForProject")
                    .setParameter("projectId", projectId)
                    .setParameter("keywordId", keywordId)
                    .executeUpdate();
        } catch (PersistenceException e) {
            logger.error("Error setting keyword active for project: {}", e.getMessage());
        }
    }

    /**
     * Sets a keyword as inactive for a project.
     *
     * @param projectId the id of the project
     * @param keywordId the id of the keyword
     */
    public void setKeywordInactiveForProject(int projectId, int keywordId) {

        logger.info("Entering setKeywordInactiveForProject");

        try {
            em.createNamedQuery("M2MKeyword.setKeywordInactiveForProject")
                    .setParameter("projectId", projectId)
                    .setParameter("keywordId", keywordId)
                    .executeUpdate();
        } catch (PersistenceException e) {
            logger.error("Error setting keyword inactive for project: {}", e.getMessage());
        }
    }

}
