package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.KeywordEntity;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Stateless
public class KeywordDao extends AbstractDao<KeywordEntity> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(KeywordDao.class);

    /**
     * Default constructor for KeywordDao.
     */
    public KeywordDao() {
        super(KeywordEntity.class);
    }

    /**
     * Finds a keyword by its name.
     *
     * @param name the name of the keyword
     * @return the KeywordEntity object if found, null otherwise
     */
    public KeywordEntity findKeywordByName(String name) {
        logger.info("Entering findKeywordByName");

        try {
            return (KeywordEntity) em.createNamedQuery("Keyword.findKeywordByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Error finding keyword by name: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Finds all keyword's names.
     *
     * @return the list of keyword names
     */
    public List<String> findAllKeywordsNames() {
        logger.info("Entering findAllKeywordsNames");

        try {
            return em.createNamedQuery("Keyword.findAllKeywordsNames", String.class)
                    .getResultList();
        } catch (Exception e) {
            logger.error("Error finding all keywords names: {}", e.getMessage());
            return null;
        }
    }
}
