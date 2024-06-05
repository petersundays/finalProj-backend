import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.InterestDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.InterestDto;
import domcast.finalprojbackend.entity.InterestEntity;
import domcast.finalprojbackend.entity.UserEntity;
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
 * Test class for InterestBean
 * @see InterestBean
 */
public class InterestBeanTest {

    @InjectMocks
    private InterestBean interestBean;

    @Mock
    private UserDao userDao;

    @Mock
    private InterestDao interestDao;

    @Mock
    private ValidatorAndHasher validatorAndHasher;

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

        when(validatorAndHasher.validateAndExtractInterestNames(interestsList)).thenReturn(new ArrayList<>());
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

        when(validatorAndHasher.validateAndExtractInterestNames(interestsList)).thenThrow(new RuntimeException());

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
}