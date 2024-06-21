package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.KeywordEntity;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
