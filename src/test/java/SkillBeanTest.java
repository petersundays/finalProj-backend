import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.SkillDto;
import domcast.finalprojbackend.dto.UserDto.UpdateUserDto;
import domcast.finalprojbackend.entity.SkillEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.SkillTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
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

    /**
     * Test method for convertTypeToEnum
     * This test checks the successful case where the type is converted correctly.
     */
    @Test
    public void testConvertTypeToEnum_Success() {
        // Arrange
        int type = 1; // Assuming 1 corresponds to a valid SkillTypeEnum

        // Act
        SkillTypeEnum result = skillBean.convertTypeToEnum(type);

        // Assert
        assertEquals(SkillTypeEnum.fromId(type), result);
    }

    /**
     * Test method for convertTypeToEnum
     * This test checks the failure case where an invalid type is passed.
     */
    @Test
    public void testConvertTypeToEnum_Failure() {
        // Arrange
        int invalidType = -1; // Assuming -1 is an invalid SkillTypeEnum

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> {
            skillBean.convertTypeToEnum(invalidType);
        });
    }

    /**
     * Test method for updateUserSkillsIfChanged
     * This test checks the successful case where the user's skills are updated correctly.
     */
    @Test
    public void testUpdateUserSkillsIfChanged_Success() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setUserSkills(new HashSet<>());

        UpdateUserDto updateUserDto = new UpdateUserDto();
        ArrayList<String> skills = new ArrayList<>();
        skills.add("Skill1");
        updateUserDto.setSkills(skills);

        when(userDao.findUserById(userEntity.getId())).thenReturn(userEntity);
        when(skillDao.findSkillByName(any(String.class))).thenReturn(new SkillEntity());

        // Act
        UserEntity result = skillBean.updateUserSkillsIfChanged(userEntity, updateUserDto);

        // Assert
        assertNotNull(result);
        verify(userDao, times(1)).merge(any(UserEntity.class));
    }

    /**
     * Test method for updateUserSkillsIfChanged when an exception is thrown.
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testUpdateUserSkillsIfChanged_Failure() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setUserSkills(new HashSet<>());

        UpdateUserDto updateUserDto = new UpdateUserDto();
        ArrayList<String> skills = new ArrayList<>();
        skills.add("NonExistentSkill");
        updateUserDto.setSkills(skills);

        when(skillDao.findSkillByName(any(String.class))).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            skillBean.updateUserSkillsIfChanged(userEntity, updateUserDto);
        });
    }
}