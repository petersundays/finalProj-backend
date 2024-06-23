package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.KeywordDao;
import domcast.finalprojbackend.dto.KeywordDto;
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
import java.util.List;
import java.util.Set;

@Stateless
public class KeywordBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(UserBean.class);

    @EJB
    private KeywordDao keywordDao;

    @EJB
    private InterestBean interestBean;

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
    public Set<KeywordEntity> createAndGetKeywords(Set<String> keywords) {

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

    /**
     * Creates a relationship between a project and a set of keywords.
     *
     * @param project  the project
     * @param keywords the set of keywords
     * @return the set of M2MKeyword objects
     */
    public Set<M2MKeyword> createRelationship(ProjectEntity project, Set<KeywordEntity> keywords) {

        logger.info("Entering createRelationship for new project and keywords");

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

    /**
     * Converts a set of M2MKeyword objects to a set of KeywordDto objects.
     *
     * @param m2MKeywords the set of M2MKeyword objects
     * @return the set of KeywordDto objects
     */
    public Set<KeywordDto> m2mToKeywordDto(Set<M2MKeyword> m2MKeywords) {

        logger.info("Entering m2mToKeywordDto");

        Set<KeywordDto> keywordDtos = new HashSet<>();

        if (m2MKeywords == null || m2MKeywords.isEmpty()) {
            logger.error("M2MKeywords list is null or empty");
            return keywordDtos;
        }

        for (M2MKeyword m2MKeyword : m2MKeywords) {
            if (m2MKeyword == null) {
                logger.error("Null M2MKeyword object found");
                continue;
            }
            try {
                KeywordDto keywordDto = new KeywordDto();
                keywordDto.setId(m2MKeyword.getKeyword().getId());
                keywordDto.setName(m2MKeyword.getKeyword().getName());
                keywordDtos.add(keywordDto);
            } catch (Exception e) {
                logger.error("Error creating KeywordDto: {}", e.getMessage());
            }
        }

        logger.info("Exiting m2mToKeywordDto");

        return keywordDtos;
    }

    /**
     * Gets all keyword names.
     *
     * @return the list of keyword names
     */
    public List<String> getAllKeywordNames() {
        logger.info("Entering getAllKeywordNames");

        List<String> keywordNames;

        try {
            keywordNames = keywordDao.findAllKeywordsNames();
        } catch (Exception e) {
            logger.error("Error getting all keyword names: {}", e.getMessage());
            throw new RuntimeException("Error getting all keyword names", e);
        }

        logger.info("Exiting getAllKeywordNames");

        return keywordNames;
    }

    /**
     * Gets all keyword and interest names.
     *
     * @return the list of keyword and interest names
     */
    public List<String> keywordsAndInterestsNames() {
        logger.info("Entering keywordsAndInterestsNames");

        List<String> keywordNames;

        try {
            keywordNames = keywordDao.findAllKeywordsNames();
            keywordNames.addAll(interestBean.getAllInterestNames());
        } catch (Exception e) {
            logger.error("Error getting all keyword and interest names: {}", e.getMessage());
            throw new RuntimeException("Error getting all keyword and interest names", e);
        }

        // Order the list of names, ascending
        keywordNames.sort(String::compareToIgnoreCase);

        logger.info("Exiting keywordsAndInterestsNames");

        return keywordNames;
    }
}
