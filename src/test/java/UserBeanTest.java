
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.bean.validationAndEncryption.EmailAndPassword;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for UserBean
 */
public class UserBeanTest {

    @InjectMocks
    private UserBean userBean;

    @Mock
    private UserDao userDao;

    @Mock
    private EmailAndPassword emailAndPassword;

    /**
     * Setup method to initialize mocks
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test method for registerEmail
     */
    @Test
    public void testRegisterEmail() {
        // Arrange
        FirstRegistration firstRegistration = new FirstRegistration();
        firstRegistration.setEmail("test@test.com");
        firstRegistration.setPassword("password");

        when(emailAndPassword.isInputValid(firstRegistration)).thenReturn(true);
        when(emailAndPassword.hashPassword(firstRegistration.getPassword())).thenReturn("hashedPassword");

        // Act
        boolean result = userBean.registerEmail(firstRegistration);

        // Assert
        assertTrue(result);
        verify(userDao, times(1)).persist(any(UserEntity.class));
    }
}