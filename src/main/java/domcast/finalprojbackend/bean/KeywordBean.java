package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.KeywordDao;
import domcast.finalprojbackend.entity.KeywordEntity;
import domcast.finalprojbackend.entity.M2MKeyword;
import domcast.finalprojbackend.entity.ProjectEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Stateless
public class KeywordBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @EJB
    private KeywordDao keywordDao;

    /**
     * Default constructor for KeywordBean.
     */
    public KeywordBean() {
    }

    /**
     * Creates and gets keywords from a list of strings.
     *
     * @param keywords the list of keywords
     * @return the set of KeywordEntity objects
     */
    public Set<KeywordEntity> createAndGetKeywords (Set<String> keywords) {

        logger.info("Entering createAndGetKeywords");

        Set<KeywordEntity> keywordEntities = new HashSet<>();

        if (keywords == null || keywords.isEmpty()) {
            logger.error("Keywords list is null or empty");
            return keywordEntities;
        }

        try {
            for (String keyword : keywords) {

                try {
                    KeywordEntity keywordEntity = keywordDao.findKeywordByName(keyword);
                    if (keywordEntity == null) {
                        keywordEntity = new KeywordEntity();
                        keywordEntity.setName(keyword);
                        keywordDao.persist(keywordEntity);
                    }
                    keywordEntities.add(keywordEntity);
                } catch (PersistenceException e) {
                    logger.error("Error creating or getting keyword: {}", e.getMessage());
                    throw new RuntimeException("Error creating or getting keyword", e);
                }
            }
        } catch (Exception e) {
            logger.error("Error creating or getting keywords: {}", e.getMessage());
            throw new RuntimeException("Error creating or getting keywords", e);
        }

        logger.info("Exiting createAndGetKeywords");

        return keywordEntities;
    }

    public Set<M2MKeyword> createRelationship (ProjectEntity project, Set<KeywordEntity> keywords) {

        logger.info("Entering createRelationship for project with ID {}", project.getId());

        Set<M2MKeyword> m2MKeywords = new HashSet<>();

        if (keywords == null || keywords.isEmpty()) {
            logger.error("Keywords list is null or empty when creating relationship");
            return m2MKeywords;
        }

        if (project == null) {
            logger.error("Project is null");
            throw new IllegalArgumentException("Project is null");
        }

        for (KeywordEntity keyword : keywords) {
            M2MKeyword projectKeyword = new M2MKeyword();
            projectKeyword.setProject(project);
            projectKeyword.setKeyword(keyword);
            m2MKeywords.add(projectKeyword);
        }

        logger.info("Exiting createRelationship");

        return m2MKeywords;
    }
}
