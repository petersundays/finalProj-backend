import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.SkillDto;
import domcast.finalprojbackend.entity.SkillEntity;
import domcast.finalprojbackend.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Test class for SkillBean
 * @see SkillBean
 */
public class SkillBeanTest {

    @InjectMocks
    private SkillBean skillBean;

    @Mock
    private UserDao userDao;

    @Mock
    private SkillDao skillDao;

    @Mock
    private ValidatorAndHasher validatorAndHasher;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test method for createSkills
     * This test checks the successful case where the skills are created correctly.
     */
    @Test
    public void testCreateSkills_Success() {
        // Arrange
        ArrayList<SkillDto> skillsList = new ArrayList<>();
        SkillDto skillDto = new SkillDto();
        skillDto.setName("Skill1");
        skillDto.setType(1);
        skillsList.add(skillDto);

        when(validatorAndHasher.validateAndExtractSkillNames(skillsList)).thenReturn(new ArrayList<>());
        when(skillDao.findSkillsByListOfNames(anyList())).thenReturn(new HashSet<>());

        // Act
        boolean result = skillBean.createSkills(skillsList);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for createSkills when an exception is thrown.
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testCreateSkills_Failure() {
        // Arrange
        ArrayList<SkillDto> skillsList = new ArrayList<>();
        SkillDto skillDto = new SkillDto();
        skillDto.setName("Skill1");
        skillDto.setType(1);
        skillsList.add(skillDto);

        when(validatorAndHasher.validateAndExtractSkillNames(skillsList)).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            skillBean.createSkills(skillsList);
        });
    }

    /**
     * Test method for addSkillToUser
     * This test checks the successful case where the skills are added to the user correctly.
     */
    @Test
    public void testAddSkillToUser_Success() {
        // Arrange
        ArrayList<String> skillsList = new ArrayList<>();
        skillsList.add("Skill1");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);

        when(userDao.findUserById(userEntity.getId())).thenReturn(userEntity);
        Set<SkillEntity> skills = new HashSet<>();
        skills.add(new SkillEntity()); // Add a SkillEntity to the set
        when(skillDao.findSkillsByListOfNames(any(ArrayList.class))).thenReturn(skills);

        // Act
        skillBean.addSkillToUser(userEntity.getId(), skillsList);

        // Assert
        verify(userDao, times(1)).merge(any(UserEntity.class));
    }

    /**
     * Test method for addSkillToUser when an exception is thrown.
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testAddSkillToUser_Failure() {
        // Arrange
        ArrayList<String> skillsList = new ArrayList<>();
        skillsList.add("Skill1");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);

        when(userDao.findUserById(userEntity.getId())).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            skillBean.addSkillToUser(userEntity.getId(), skillsList);
        });
    }
}