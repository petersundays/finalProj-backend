import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.interestDto.InterestDto;
import domcast.finalprojbackend.dto.interestDto.InterestToList;
import domcast.finalprojbackend.dto.userDto.UpdateUserDto;
import domcast.finalprojbackend.entity.InterestEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.InterestEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for InterestBean
 * @see InterestBean
 */
public class InterestBeanTest {

    @InjectMocks
    private InterestBean interestBean;

    @Mock
    private InterestEntity interestEntity;

    @Mock
    private UserDao userDao;

    @Mock
    private InterestDao interestDao;

    @Mock
    private DataValidator dataValidator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test method for createInterests
     * This test checks the successful case where the interests are created correctly.
     */
    @Test
    public void testCreateInterests_Success() {
        // Arrange
        ArrayList<InterestDto> interestsList = new ArrayList<>();
        InterestDto interestDto = new InterestDto();
        interestDto.setName("Interest1");
        interestDto.setType(1);
        interestsList.add(interestDto);

        when(dataValidator.validateAndExtractInterestNames(interestsList)).thenReturn(new ArrayList<>());
        when(interestDao.findInterestsByListOfNames(any(ArrayList.class))).thenReturn(new HashSet<>());

        // Act
        boolean result = interestBean.createInterests(interestsList);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for createInterests when an exception is thrown.
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testCreateInterests_Failure() {
        // Arrange
        ArrayList<InterestDto> interestsList = new ArrayList<>();
        InterestDto interestDto = new InterestDto();
        interestDto.setName("Interest1");
        interestDto.setType(1);
        interestsList.add(interestDto);

        when(dataValidator.validateAndExtractInterestNames(interestsList)).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            interestBean.createInterests(interestsList);
        });
    }

    /**
     * Test method for addInterestToUser
     * This test checks the successful case where the interests are added to the user correctly.
     */
    @Test
    public void testAddInterestToUser_Success() {
        // Arrange
        ArrayList<String> interestsList = new ArrayList<>();
        interestsList.add("Interest1");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);

        when(userDao.findUserById(userEntity.getId())).thenReturn(userEntity);
        Set<InterestEntity> interests = new HashSet<>();
        interests.add(new InterestEntity()); // Add an InterestEntity to the set
        when(interestDao.findInterestsByListOfNames(any(ArrayList.class))).thenReturn(interests);

        // Act
        interestBean.addInterestToUser(userEntity.getId(), interestsList);

        // Assert
        verify(userDao, times(1)).merge(any(UserEntity.class));
    }

    /**
     * Test method for addInterestToUser when an exception is thrown.
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testAddInterestToUser_Failure() {
        // Arrange
        ArrayList<String> interestsList = new ArrayList<>();
        interestsList.add("Interest1");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);

        when(userDao.findUserById(userEntity.getId())).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            interestBean.addInterestToUser(userEntity.getId(), interestsList);
        });
    }

    /**
     * Test method for convertTypeToEnum
     * This test checks the successful case where the type is converted correctly.
     */
    @Test
    public void testConvertTypeToEnum_Success() {
        // Arrange
        int type = 1; // Assuming 1 corresponds to a valid InterestEnum

        // Act
        InterestEnum result = interestBean.convertTypeToEnum(type);

        // Assert
        assertEquals(InterestEnum.fromId(type), result);
    }

    /**
     * Test method for convertTypeToEnum
     * This test checks the failure case where an invalid type is passed.
     */
    @Test
    public void testConvertTypeToEnum_Failure() {
        // Arrange
        int invalidType = -1; // Assuming -1 is an invalid InterestEnum

        // Act
        InterestEnum result = interestBean.convertTypeToEnum(invalidType);

        // Assert
        assertNull(result);
    }

    /**
     * Test method for updateUserInterestsIfChanged
     * This test checks the successful case where the user's interests are updated correctly.
     */
    @Test
    public void testUpdateUserInterestsIfChanged_Success() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setInterests(new HashSet<>());

        UpdateUserDto updateUserDto = new UpdateUserDto();
        ArrayList<String> interests = new ArrayList<>();
        interests.add("Interest1");
        updateUserDto.setInterests(interests);

        when(userDao.findUserById(userEntity.getId())).thenReturn(userEntity);
        when(interestDao.findInterestByName(any(String.class))).thenReturn(new InterestEntity());

        // Act
        UserEntity result = interestBean.updateUserInterestsIfChanged(userEntity, updateUserDto);

        // Assert
        assertNotNull(result);
        verify(userDao, times(1)).merge(any(UserEntity.class));
    }

    /**
     * Test method for updateUserInterestsIfChanged when an exception is thrown.
     * This test checks the failure case where an exception is thrown.
     */
    @Test
    public void testUpdateUserInterestsIfChanged_Failure() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setInterests(new HashSet<>());

        UpdateUserDto updateUserDto = new UpdateUserDto();
        ArrayList<String> interests = new ArrayList<>();
        interests.add("NonExistentInterest");
        updateUserDto.setInterests(interests);

        when(interestDao.findInterestByName(any(String.class))).thenThrow(new RuntimeException());

        // Act and Assert
        assertThrows(RuntimeException.class, () -> {
            interestBean.updateUserInterestsIfChanged(userEntity, updateUserDto);
        });
    }

    /**
     * Test for convertInterestEntityToInterestToList method when the conversion is successful.
     * The test is expected to pass.
     */
    @Test
    void convertInterestEntityToInterestToList_Success() {
        // Arrange
        when(interestEntity.getId()).thenReturn(1);
        when(interestEntity.getName()).thenReturn("Test Interest");
        when(interestEntity.getType()).thenReturn(InterestEnum.CAUSE);

        // Act
        InterestToList result = interestBean.convertInterestEntityToInterestToList(interestEntity);

        // Assert
        assertEquals(interestEntity.getId(), result.getId());
        assertEquals(interestEntity.getName(), result.getName());
        assertEquals(interestEntity.getType().getId(), result.getType());
    }

    /**
     * Test for convertInterestEntityToInterestToList method when the conversion fails due to a null InterestEntity.
     * The test is expected to throw an IllegalArgumentException.
     */
    @Test
    void convertInterestEntityToInterestToList_Failure() {
        // Arrange
        InterestEntity interestEntity = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> interestBean.convertInterestEntityToInterestToList(interestEntity));
    }

    /**
     * Test for getAllInterests method when the retrieval is successful.
     * The test is expected to pass.
     */
    @Test
    void getAllInterests_Success() {
        // Arrange
        List<InterestEntity> interestEntities = new ArrayList<>();
        InterestEntity interestEntity = new InterestEntity();
        interestEntity.setId(1);
        interestEntity.setName("Test Interest");
        interestEntity.setType(InterestEnum.THEME);
        interestEntities.add(interestEntity);
        when(interestDao.findAllInterests()).thenReturn(interestEntities);

        // Act
        List<InterestToList> result = interestBean.getAllInterests();

        // Assert
        assertEquals(1, result.size());
        InterestToList interestToList = result.iterator().next();
        assertEquals(interestEntity.getId(), interestToList.getId());
        assertEquals(interestEntity.getName(), interestToList.getName());
        assertEquals(interestEntity.getType().getId(), interestToList.getType());
    }

    /**
     * Test for getAllInterests method when the retrieval fails.
     * The test is expected to throw an Exception.
     */
    @Test
    void getAllInterests_Failure() {
        // Arrange
        when(interestDao.findAllInterests()).thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> interestBean.getAllInterests());
    }
}