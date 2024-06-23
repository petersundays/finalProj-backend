import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.KeywordBean;
import domcast.finalprojbackend.dao.KeywordDao;
import domcast.finalprojbackend.dto.KeywordDto;
import domcast.finalprojbackend.entity.KeywordEntity;
import domcast.finalprojbackend.entity.M2MKeyword;
import domcast.finalprojbackend.entity.ProjectEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

public class KeywordBeanTest {

    @InjectMocks
    KeywordBean keywordBean;

    @Mock
    KeywordDao keywordDao;

    @Mock
    private InterestBean interestBean;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test case for success scenario of createAndGetKeywords method.
     */
    @Test
    public void testCreateAndGetKeywords_Success() {
        Set<String> keywords = new HashSet<>();
        keywords.add("test");

        when(keywordDao.findKeywordByName(anyString())).thenReturn(null);

        Set<KeywordEntity> result = keywordBean.createAndGetKeywords(keywords);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test case for failure scenario of createAndGetKeywords method.
     */
    @Test
    public void testCreateAndGetKeywords_Failure() {
        Set<KeywordEntity> result = keywordBean.createAndGetKeywords(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test case for success scenario of createRelationship method.
     */
    @Test
    public void testCreateRelationship_Success() {
        ProjectEntity project = new ProjectEntity();
        Set<KeywordEntity> keywords = new HashSet<>();
        keywords.add(new KeywordEntity());

        Set<M2MKeyword> result = keywordBean.createRelationship(project, keywords);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    /**
     * Test case for failure scenario of createRelationship method.
     */
    @Test
    public void testCreateRelationship_Failure() {
        Set<M2MKeyword> result = keywordBean.createRelationship(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test case for success scenario of m2mToKeywordDto method.
     */
    @Test
    public void testM2mToKeywordDto_Success() {
        Set<M2MKeyword> m2MKeywords = new HashSet<>();
        M2MKeyword m2MKeyword = new M2MKeyword();
        KeywordEntity keywordEntity = new KeywordEntity();
        keywordEntity.setId(1);
        keywordEntity.setName("test");
        m2MKeyword.setKeyword(keywordEntity);
        m2MKeywords.add(m2MKeyword);

        Set<KeywordDto> result = keywordBean.m2mToKeywordDto(m2MKeywords);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(keywordEntity.getId(), result.iterator().next().getId());
        assertEquals(keywordEntity.getName(), result.iterator().next().getName());
    }

    /**
     * Test case for failure scenario of m2mToKeywordDto method.
     */
    @Test
    public void testM2mToKeywordDto_Failure() {
        Set<KeywordDto> result = keywordBean.m2mToKeywordDto(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Test method for getAllKeywordNames
     * This test checks the successful case where all keyword names are retrieved correctly.
     */
    @Test
    public void testGetAllKeywordNames_Success() {
        // Arrange
        List<String> keywordNames = Arrays.asList("Keyword1", "Keyword2");
        when(keywordDao.findAllKeywordsNames()).thenReturn(keywordNames);

        // Act
        List<String> result = keywordBean.getAllKeywordNames();

        // Assert
        assertEquals(keywordNames, result);
    }

    /**
     * Test method for getAllKeywordNames
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testGetAllKeywordNames_Failure() {
        // Arrange
        when(keywordDao.findAllKeywordsNames()).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            keywordBean.getAllKeywordNames();
        });
    }

    /**
     * Test method for keywordsAndInterestsNames
     * This test checks the successful case where all keyword and interest names are retrieved correctly.
     */
    @Test
    public void testKeywordsAndInterestsNames_Success() {
        // Arrange
        List<String> keywordNames = new ArrayList<>(Arrays.asList("Keyword1", "Keyword2"));
        List<String> interestNames = new ArrayList<>(Arrays.asList("Interest1", "Interest2"));
        when(keywordDao.findAllKeywordsNames()).thenReturn(keywordNames);
        when(interestBean.getAllInterestNames()).thenReturn(interestNames);

        // Act
        List<String> result = keywordBean.keywordsAndInterestsNames();

        // Assert
        assertTrue(result.containsAll(keywordNames));
        assertTrue(result.containsAll(interestNames));
    }

    /**
     * Test method for keywordsAndInterestsNames
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testKeywordsAndInterestsNames_Failure() {
        // Arrange
        when(keywordDao.findAllKeywordsNames()).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            keywordBean.keywordsAndInterestsNames();
        });
    }
}