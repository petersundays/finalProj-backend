
import domcast.finalprojbackend.bean.user.EmailBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.bean.user.ValidatorAndHasher;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.dto.UserDto.FullRegistration;
import domcast.finalprojbackend.dto.UserDto.LoggedUser;
import domcast.finalprojbackend.dto.UserDto.Login;
import domcast.finalprojbackend.entity.LabEntity;
import domcast.finalprojbackend.entity.SessionTokenEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.entity.ValidationTokenEntity;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for UserBean
 * @see UserBean
 */
public class UserBeanTest {

    private static final Logger logger = LogManager.getLogger(UserBeanTest.class);

    @InjectMocks
    private UserBean userBean; // Inject the UserBean

    @Mock
    private UserDao userDao; // Mock the UserDao

    @Mock
    private ValidatorAndHasher validatorAndHasher; // Mock the ValidatorAndHasher

    @Mock
    private TokenBean tokenBean; // Mock the TokenBean

    @Mock
    private EmailBean emailBean; // Mock the EmailBean

    @Mock
    private LabDao labDao; // Mock the LabDao

    /**
     * Setup method to initialize mocks
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test method for registerEmail
     * This test checks the successful case where the email is registered correctly.
     */
    /*@Test
    public void testRegisterEmail_Success() {
        // Arrange
        FirstRegistration firstRegistration = new FirstRegistration();
        firstRegistration.setEmail("test@test.com");
        firstRegistration.setPassword("password");
        String ipAddress = "192.168.0.1"; // Add the IP address

        // Mock the behavior of the dependencies
        when(validatorAndHasher.isInputValid(firstRegistration)).thenReturn(true);
        when(validatorAndHasher.hashPassword(firstRegistration.getPassword())).thenReturn("hashedPassword");
        when(tokenBean.generateValidationToken(any(UserEntity.class), anyInt(), eq(ipAddress))).thenReturn(new ValidationTokenEntity());
        when(userDao.persist(any(UserEntity.class))).thenReturn(true);
        when(emailBean.sendConfirmationEmail(anyString(), anyString())).thenReturn(true);

        // Act
        boolean result = userBean.registerEmail(firstRegistration, ipAddress);

        // Assert
        assertTrue(result);

        // Verify that the sendConfirmationEmail method was called
        verify(emailBean, times(1)).sendConfirmationEmail(anyString(), anyString());
    }
*/
    /**
     * Test method for registerEmail when input is invalid
     * This test checks the failure case where the input validation fails.
     */
    @Test
    public void testRegisterEmail_Failure() {
        // Arrange
        FirstRegistration firstRegistration = new FirstRegistration();
        firstRegistration.setEmail("invalid@test.com");
        firstRegistration.setPassword("password");
        String ipAddress = "192.168.0.1"; // Add the IP address

        // Mock the behavior of the validatorAndHasher to return false for isInputValid
        when(validatorAndHasher.isInputValid(firstRegistration)).thenReturn(false);

        // Act
        // Call the method under test
        boolean result = userBean.registerEmail(firstRegistration, ipAddress);

        // Assert
        // Check that the method returned false and that the userDao.persist method was not called
        assertFalse(result);
        verify(userDao, times(0)).persist(any(UserEntity.class));
    }


    /**
     * Test method for fullRegistration
     * This test checks the successful case where the full registration is successful.
     */

    @Test
    public void testFullRegistration_Success() {
        // Arrange
        FullRegistration fullRegistration = new FullRegistration();
        fullRegistration.setValidationToken("validToken");
        fullRegistration.setFirstName("John");
        fullRegistration.setLastName("Doe");
        fullRegistration.setWorkplace("Lisboa");

        UserEntity userEntity = new UserEntity();
        LabEntity labEntity = new LabEntity();
        labEntity.setCity(LabEnum.LISBOA);

        when(validatorAndHasher.isMandatoryDataValid(fullRegistration)).thenReturn(true);
        when(userDao.findUserByValidationToken(fullRegistration.getValidationToken())).thenReturn(userEntity);
        when(labDao.findLabByCity(fullRegistration.getWorkplace())).thenReturn(labEntity);
        when(tokenBean.setTokenInactive(fullRegistration.getValidationToken())).thenReturn(true); // Mock the inactivation of the token
        when(userDao.merge(any(UserEntity.class))).thenReturn(true); // Simulate success

        // Act
        boolean result = userBean.fullRegistration(fullRegistration);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for fullRegistration when mandatory data is invalid.
     * This test checks the failure case where the mandatory data is invalid.
     */
    @Test
    public void testFullRegistration_Failure() {
        // Arrange
        FullRegistration fullRegistration = new FullRegistration();
        fullRegistration.setValidationToken("validToken");
        fullRegistration.setFirstName("John");
        fullRegistration.setLastName("Doe");
        fullRegistration.setWorkplace("CityLab");

        UserEntity userEntity = new UserEntity();
        LabEntity labEntity = new LabEntity();

        when(validatorAndHasher.isMandatoryDataValid(fullRegistration)).thenReturn(true);
        when(userDao.findUserByValidationToken(fullRegistration.getValidationToken())).thenReturn(userEntity);
        when(labDao.findLabByCity(fullRegistration.getWorkplace())).thenReturn(labEntity);
        when(userDao.merge(any(UserEntity.class))).thenReturn(false); // Simulate failure

        // Act
        boolean result = userBean.fullRegistration(fullRegistration);

        // Assert
        assertFalse(result);
    }


    /**
     * Test method for login
     * This test checks the successful case where the login is successful.
     */
    @Test
    public void testLogin_Success() {
        // Arrange
        Login login = new Login();
        login.setEmail("test@test.com");
        login.setPassword("password");
        String ipAddress = "192.168.0.1";

        UserEntity userEntity = new UserEntity();
        userEntity.setPassword("hashedPassword");
        userEntity.setType(TypeOfUserEnum.STANDARD);

        // Create a LabEntity and set it as the workplace of the UserEntity
        LabEntity labEntity = new LabEntity();
        labEntity.setCity(LabEnum.LISBOA); // Or any other value from the LabEnum
        userEntity.setWorkplace(labEntity);

        SessionTokenEntity sessionTokenEntity = new SessionTokenEntity();

        when(validatorAndHasher.isLoginValid(login)).thenReturn(true);
        when(userDao.findUserByEmail(login.getEmail())).thenReturn(userEntity);
        when(validatorAndHasher.checkPassword(login.getPassword(), userEntity.getPassword())).thenReturn(true);
        when(tokenBean.generateSessionToken(userEntity, ipAddress)).thenReturn(sessionTokenEntity);
        when(userDao.merge(userEntity)).thenReturn(true);

        // Act
        LoggedUser result = userBean.login(login, ipAddress);

        // Assert
        assertNotNull(result);
    }

    /**
     * Test method for login
     * This test checks the failure case where the login is unsuccessful.
     */
    @Test
    public void testLogin_Failure() {
        // Arrange
        Login login = new Login();
        login.setEmail("test@test.com");
        login.setPassword("password");
        String ipAddress = "192.168.0.1";

        when(validatorAndHasher.isLoginValid(login)).thenReturn(false);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.login(login, ipAddress));
    }

    /**
     * Test method for logout
     * This test checks the successful case where the logout is successful.
     */
    @Test
    public void testLogout_Success() {
        // Arrange
        String sessionToken = "validToken";

        when(tokenBean.setTokenInactive(sessionToken)).thenReturn(true);
        when(tokenBean.setSessionTokenLogoutToNow(sessionToken)).thenReturn(true);

        // Act
        boolean result = userBean.logout(sessionToken);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for logout
     * This test checks the failure case where the logout is unsuccessful.
     */
    @Test
    public void testLogout_Failure() {
        // Arrange
        String sessionToken = "invalidToken";

        when(tokenBean.setTokenInactive(sessionToken)).thenReturn(false);
        when(tokenBean.setSessionTokenLogoutToNow(sessionToken)).thenReturn(false);

        // Act
        boolean result = userBean.logout(sessionToken);

        // Assert
        assertFalse(result);
    }
}