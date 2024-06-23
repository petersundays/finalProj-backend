import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.dao.SkillDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.skillDto.SkillDto;
import domcast.finalprojbackend.dto.skillDto.SkillToProject;
import domcast.finalprojbackend.dto.userDto.UpdateUserDto;
import domcast.finalprojbackend.entity.M2MProjectSkill;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.SkillEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.SkillTypeEnum;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;

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
    private DataValidator dataValidator;

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
        skillDto.setName("Test Skill");
        skillDto.setType(1);
        skillsList.add(skillDto);

        ArrayList<String> skillsNames = new ArrayList<>();
        skillsNames.add("Test Skill");

        Set<SkillEntity> skills = new HashSet<>();
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setName("Test Skill");
        skills.add(skillEntity);

        when(dataValidator.validateAndExtractSkillNames(skillsList)).thenReturn(skillsNames);
        when(skillDao.findSkillsByListOfNames(skillsNames)).thenReturn(skills);

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

        when(dataValidator.validateAndExtractSkillNames(skillsList)).thenThrow(new RuntimeException());

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

    @Test
    public void testGetSkillsIds_Success() {
        // Arrange
        Set<SkillDto> skills = new HashSet<>();
        SkillDto skillDto = new SkillDto();
        skillDto.setName("Test Skill");
        skills.add(skillDto);

        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setId(1);
        when(skillDao.findSkillByName(skillDto.getName())).thenReturn(skillEntity);

        // Act
        Set<Integer> result = skillBean.getSkillsIds(skills);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains(skillEntity.getId()));
    }

    @Test
    public void testGetSkillsIds_Failure() {
        // Arrange
        Set<SkillDto> skills = null;

        // Act
        Set<Integer> result = skillBean.getSkillsIds(skills);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCreateRelationshipToProject_Success() {
        // Arrange
        Set<Integer> skillsIds = new HashSet<>();
        skillsIds.add(1);

        ProjectEntity project = new ProjectEntity();
        project.setId(1);

        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setId(1);
        when(skillDao.findSkillById(1)).thenReturn(skillEntity);

        // Act
        Set<M2MProjectSkill> result = skillBean.createRelationshipToProject(skillsIds, project);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testCreateRelationshipToProject_Failure() {
        // Arrange
        Set<Integer> skillsIds = null;
        ProjectEntity project = null;

        // Act
        Set<M2MProjectSkill> result = skillBean.createRelationshipToProject(skillsIds, project);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testFindSkillsIdsByListOfNames_Success() {
        // Arrange
        Set<String> names = new HashSet<>();
        names.add("Test Skill");

        List<Integer> expectedIdsList = new ArrayList<>();
        expectedIdsList.add(1);
        Set<Integer> expectedIdsSet = new HashSet<>(expectedIdsList);
        when(skillDao.findSkillsIdsByListOfNames(anyList())).thenReturn(expectedIdsSet);

        // Act
        Set<Integer> result = skillBean.findSkillsIdsByListOfNames(names);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testFindSkillsIdsByListOfNames_Failure() {
        // Arrange
        Set<String> names = null;

        // Act
        Set<Integer> result = skillBean.findSkillsIdsByListOfNames(names);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testProjectSkillToDto_Success() {
        // Arrange
        Set<M2MProjectSkill> m2MProjectSkills = new HashSet<>();
        M2MProjectSkill m2MProjectSkill = new M2MProjectSkill();
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setId(1);
        skillEntity.setName("Test Skill");
        skillEntity.setType(SkillTypeEnum.HARDWARE);
        m2MProjectSkill.setSkill(skillEntity);
        m2MProjectSkills.add(m2MProjectSkill);

        // Act
        Set<SkillToProject> result = skillBean.projectSkillToDto(m2MProjectSkills);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testProjectSkillToDto_Failure() {
        // Arrange
        Set<M2MProjectSkill> m2MProjectSkills = null;

        // Act
        Set<SkillToProject> result = skillBean.projectSkillToDto(m2MProjectSkills);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    public void testExtractNewSkills_Success() throws IOException {
        // Arrange
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        List<InputPart> inputParts = new ArrayList<>();
        InputPart inputPart = mock(InputPart.class);
        inputParts.add(inputPart);
        formDataMap.put("skills", inputParts);
        when(input.getFormDataMap()).thenReturn(formDataMap);
        when(inputPart.getBodyAsString()).thenReturn("[{\"name\":\"Test Skill\",\"type\":1}]");

        // Act
        ArrayList<SkillDto> result = skillBean.extractNewSkills(input);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    public void testExtractNewSkills_Failure() throws IOException {
        // Arrange
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        Map<String, List<InputPart>> formDataMap = new HashMap<>();
        when(input.getFormDataMap()).thenReturn(formDataMap);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            skillBean.extractNewSkills(input);
        });
    }
}