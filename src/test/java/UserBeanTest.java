import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.user.*;
import domcast.finalprojbackend.dao.LabDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.userDto.*;
import domcast.finalprojbackend.entity.LabEntity;
import domcast.finalprojbackend.entity.SessionTokenEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.entity.ValidationTokenEntity;
import domcast.finalprojbackend.enums.LabEnum;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

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
    private DataValidator dataValidator; // Mock the ValidatorAndHasher

    @Mock
    private TokenBean tokenBean; // Mock the TokenBean

    @Mock
    private EmailBean emailBean; // Mock the EmailBean

    @Mock
    private LabDao labDao; // Mock the LabDao

    @Mock
    private InputPart inputPart; // Mock the InputPart

    @Mock
    private MultipartFormDataInput input; // Mock the MultipartFormDataInput

    @Mock
    private AuthenticationAndAuthorization authenticationAndAuthorization; // Mock the AuthenticationBean

    @Mock
    private PasswordBean passwordBean; // Mock the PasswordBean
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
        when(dataValidator.isInputValid(firstRegistration)).thenReturn(false);

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
        fullRegistration.setInterests(new ArrayList<>());
        fullRegistration.setSkills(new ArrayList<>());

        UserEntity userEntity = new UserEntity();
        LabEntity labEntity = new LabEntity();
        labEntity.setCity(LabEnum.LISBOA);
        SessionTokenEntity sessionTokenEntity = new SessionTokenEntity(); // Create a SessionTokenEntity

        when(dataValidator.isUserMandatoryDataValid(fullRegistration)).thenReturn(true);
        when(userDao.findUserByValidationToken(fullRegistration.getValidationToken())).thenReturn(userEntity);
        when(labDao.findLabByCity(fullRegistration.getWorkplace())).thenReturn(labEntity);
        when(tokenBean.setTokenInactive(fullRegistration.getValidationToken())).thenReturn(true); // Mock the inactivation of the token
        when(tokenBean.generateSessionToken(userEntity, "ipAddress")).thenReturn(sessionTokenEntity); // Mock the generation of the session token
        when(userDao.merge(any(UserEntity.class))).thenReturn(true); // Simulate success

        // Act
        LoggedUser result = userBean.fullRegistration(fullRegistration, "photoPath", "ipAddress");

        // Assert
        assertNotNull(result);
    }


    /**
     * Test method for fullRegistration
     * This test checks the failure case where the full registration is unsuccessful.
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

        when(dataValidator.isUserMandatoryDataValid(fullRegistration)).thenReturn(true);
        when(userDao.findUserByValidationToken(fullRegistration.getValidationToken())).thenReturn(userEntity);
        when(labDao.findLabByCity(fullRegistration.getWorkplace())).thenReturn(labEntity);
        when(userDao.merge(any(UserEntity.class))).thenReturn(false); // Simulate failure

        // Act
        LoggedUser result = userBean.fullRegistration(fullRegistration, "photoPath", "ipAddress");

        // Assert
        assertNull(result);
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

        when(dataValidator.isLoginValid(login)).thenReturn(true);
        when(userDao.findUserByEmail(login.getEmail())).thenReturn(userEntity);
        when(authenticationAndAuthorization.checkPassword(login.getPassword(), userEntity.getPassword())).thenReturn(true);
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

        when(dataValidator.isLoginValid(login)).thenReturn(false);

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

    /**
     * Test method for recoverPassword
     * This test checks the successful case where the password recovery is successful.
     */
    @Test
    public void testRecoverPassword_Success() {
        // Arrange
        String email = "test@test.com";
        String ipAddress = "192.168.0.1";
        UserEntity userEntity = new UserEntity();
        userEntity.setType(TypeOfUserEnum.STANDARD);
        ValidationTokenEntity validationTokenEntity = new ValidationTokenEntity();

        // Mock the behavior of the dependencies
        when(userDao.findUserByEmail(email)).thenReturn(userEntity);
        when(tokenBean.generateValidationToken(userEntity, 5, ipAddress)).thenReturn(validationTokenEntity);
        when(userDao.merge(userEntity)).thenReturn(true);
        when(emailBean.sendPasswordResetEmail(email, userEntity.getFirstName(), validationTokenEntity.getToken())).thenReturn(true);

        // Act
        boolean result = userBean.recoverPassword(email, ipAddress);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for recoverPassword
     * This test checks the failure case where the password recovery is unsuccessful.
     */
    @Test
    public void testRecoverPassword_Failure() {
        // Arrange
        String email = "invalid@test.com";
        String ipAddress = "192.168.0.1";

        // Mock the behavior of the userDao to return null for findUserByEmail
        when(userDao.findUserByEmail(email)).thenReturn(null);

        // Act
        boolean result = userBean.recoverPassword(email, ipAddress);

        // Assert
        assertFalse(result);
    }

    /**
     * Test method for resetPassword
     * This test checks the successful case where the password reset is successful.
     */
    @Test
    public void testResetPassword_Success() {
        // Arrange
        String validationToken = "validToken";
        String password = "password";
        UserEntity userEntity = new UserEntity();

        // Mock the behavior of the dependencies
        when(tokenBean.isValidationTokenExpired(validationToken)).thenReturn(false);
        when(userDao.findUserByValidationToken(validationToken)).thenReturn(userEntity);
        when(passwordBean.isPasswordValid(password)).thenReturn(true);
        when(passwordBean.hashPassword(password)).thenReturn("hashedPassword");
        when(tokenBean.setTokenInactive(validationToken)).thenReturn(true);
        when(userDao.merge(userEntity)).thenReturn(true);

        // Act
        boolean result = userBean.resetPassword(validationToken, password);

        // Assert
        assertTrue(result);
    }

    /**
     * Test method for resetPassword
     * This test checks the failure case where the password reset is unsuccessful.
     */
    @Test
    public void testResetPassword_Failure() {
        // Arrange
        String validationToken = "invalidToken";
        String password = "password";

        // Mock the behavior of the tokenBean to return true for isValidationTokenExpired
        when(tokenBean.isValidationTokenExpired(validationToken)).thenReturn(true);

        // Act
        boolean result = userBean.resetPassword(validationToken, password);

        // Assert
        assertFalse(result);
    }

    /**
     * Test method for uploadPhoto
     * This test checks the successful case where the photo upload is successful.
     */
    @Test
    public void testUploadPhoto_Success() throws Exception {
        // Arrange
        String token = "validToken";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);

        // Create a real InputStream with some dummy data
        byte[] bytes = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(bytes);

        Map<String, List<InputPart>> uploadForm = new HashMap<>();
        uploadForm.put("photo", Arrays.asList(inputPart));

        // Mock the behavior of the dependencies
        when(userDao.findUserByActiveValidationOrSessionToken(token)).thenReturn(userEntity);
        when(input.getFormDataMap()).thenReturn(uploadForm);
        when(inputPart.getBody(InputStream.class, null)).thenReturn(inputStream);
        when(dataValidator.isValidImage(bytes)).thenReturn(true);

        // Act
        userBean.uploadPhoto(token, input);

        // Verify that the userDao.merge method was called
        verify(userDao, times(1)).merge(any(UserEntity.class));
    }

    /**
     * Test method for uploadPhoto
     * This test checks the failure case where the photo upload fails.
     */
    @Test
    public void testUploadPhoto_Failure() throws Exception {
        // Arrange
        String token = "invalidToken";

        // Mock the behavior of the userDao to return null for findUserByActiveValidationOrSessionToken
        when(userDao.findUserByActiveValidationOrSessionToken(token)).thenReturn(null);

        // Act and Assert
        assertThrows(Exception.class, () -> userBean.uploadPhoto(token, input));
    }

    /**
     * Test method for updateUserProfile
     * This test checks the successful case where the user profile is updated correctly.
     */
    @Test
    public void testUpdateUserProfile_Success() {
        // Arrange
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("John");
        updateUserDto.setLastName("Doe");
        updateUserDto.setWorkplace("Lisboa");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setInterests(new HashSet<>()); // Ensure interests is not null

        // ... rest of your code
    }

    /**
     * Test method for updateUserProfile when user is not found.
     * This test checks the failure case where the user is not found.
     */
    @Test
    public void testUpdateUserProfile_Failure() {
        // Arrange
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("John");
        updateUserDto.setLastName("Doe");
        updateUserDto.setWorkplace("Lisboa");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);

        when(userDao.findUserById(userEntity.getId())).thenReturn(null);

        // Act and Assert
        assertThrows(NoSuchElementException.class, () -> {
            userBean.updateUserProfile(updateUserDto, userEntity.getId(), null, "token");
        });
    }
    /**
     * Test method for updateUserInterests
     * This test checks the successful case where the user interests are updated correctly.
     */
    @Test
    public void testUpdateUserPhoto_Success() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        LabEntity labEntity = new LabEntity(); // Create a LabEntity
        labEntity.setCity(LabEnum.LISBOA); // Set a city for the LabEntity
        userEntity.setWorkplace(labEntity); // Set the LabEntity as the workplace of the UserEntity
        String photoPath = "path/to/photo";
        String token = "token";

        when(userDao.merge(any(UserEntity.class))).thenReturn(true);

        // Act
        LoggedUser result = userBean.updateUserPhoto(userEntity, photoPath, token);

        // Assert
        assertNotNull(result);
        verify(userDao, times(1)).merge(any(UserEntity.class));
    }

    /**
     * Test method for updateUserInterests
     * This test checks the failure case where the user interests are not updated.
     */
    @Test
    public void testUpdateUserPhoto_Failure() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        String photoPath = "path/to/photo";
        String token = "token";

        when(userDao.merge(any(UserEntity.class))).thenReturn(false);

        // Act and Assert
        assertThrows(RuntimeException.class, () -> userBean.updateUserPhoto(userEntity, photoPath, token));
    }

    /**
     * Test method for updateUserInterests
     * This test checks the successful case where the user interests are updated correctly.
     */
    @Test
    public void testUpdateBasicInfoIfChanged_Success() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("John");
        updateUserDto.setLastName("Doe");
        updateUserDto.setWorkplace("Lisboa");
        String photoPath = "path/to/photo";

        LabEntity labEntity = new LabEntity(); // Create a LabEntity
        labEntity.setCity(LabEnum.LISBOA); // Set a city for the LabEntity

        when(labDao.findLabByCity(updateUserDto.getWorkplace())).thenReturn(labEntity);

        // Act
        UserEntity result = userBean.updateBasicInfoIfChanged(userEntity, updateUserDto, photoPath);

        // Assert
        assertNotNull(result);
        assertEquals(updateUserDto.getFirstName(), result.getFirstName());
        assertEquals(updateUserDto.getLastName(), result.getLastName());
        assertEquals(updateUserDto.getWorkplace(), result.getWorkplace().getCity().getValue());
    }

    /**
     * Test method for updateUserInterests
     * This test checks the failure case where the user interests are not updated.
     */
    @Test
    public void testUpdateBasicInfoIfChanged_Failure() {
        // Arrange
        UserEntity userEntity = null;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        String photoPath = "path/to/photo";

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.updateBasicInfoIfChanged(userEntity, updateUserDto, photoPath));
    }

    @Test
    public void testUpdateUserType_Success() {
        // Arrange
        int loggedId = 1;
        int id = 2;
        int type = TypeOfUserEnum.STANDARD.getId();

        when(userDao.getUserType(id)).thenReturn(TypeOfUserEnum.ADMIN.getId());
        when(userDao.setUserType(id, type)).thenReturn(true);

        // Act
        String result = userBean.updateUserType(loggedId, id, type);

        // Assert
        assertEquals("User type updated for user with id: " + id, result);
    }

    @Test
    public void testUpdateUserType_Failure_SameId() {
        // Arrange
        int loggedId = 1;
        int id = 1;
        int type = TypeOfUserEnum.STANDARD.getId();

        // Act
        String result = userBean.updateUserType(loggedId, id, type);

        // Assert
        assertEquals("User cannot change its own type", result);
    }

    @Test
    public void testUpdateUserType_Failure_NotConfirmed() {
        // Arrange
        int loggedId = 1;
        int id = 2;
        int type = TypeOfUserEnum.NOT_CONFIRMED.getId();

        when(userDao.getUserType(id)).thenReturn(TypeOfUserEnum.NOT_CONFIRMED.getId());

        // Act
        String result = userBean.updateUserType(loggedId, id, type);

        // Assert
        assertEquals("The user is not confirmed, please confirm the user first", result);
    }

    @Test
    public void testUpdateUserType_Failure_AlreadySet() {
        // Arrange
        int loggedId = 1;
        int id = 2;
        int type = TypeOfUserEnum.STANDARD.getId();

        when(userDao.getUserType(id)).thenReturn(type);

        // Act
        String result = userBean.updateUserType(loggedId, id, type);

        // Assert
        assertEquals("User type is already set to: " + type, result);
    }

    @Test
    public void testUpdateUserType_Failure_InvalidType() {
        // Arrange
        int loggedId = 1;
        int id = 2;
        int type = -1; // Invalid type

        when(userDao.getUserType(id)).thenReturn(TypeOfUserEnum.STANDARD.getId());

        // Act
        String result = userBean.updateUserType(loggedId, id, type);

        // Assert
        assertEquals("Invalid user type: " + type, result);
    }

    @Test
    public void testUpdateUserType_Failure_SetToNotConfirmed() {
        // Arrange
        int loggedId = 1;
        int id = 2;
        int type = TypeOfUserEnum.NOT_CONFIRMED.getId();

        when(userDao.getUserType(id)).thenReturn(TypeOfUserEnum.STANDARD.getId());

        // Act
        String result = userBean.updateUserType(loggedId, id, type);

        // Assert
        assertEquals("User type cannot be set to NOT_CONFIRMED", result);
    }

    @Test
    public void testUpdateUserType_Failure_UpdateFailed() {
        // Arrange
        int loggedId = 1;
        int id = 2;
        int type = TypeOfUserEnum.ADMIN.getId();

        when(userDao.getUserType(id)).thenReturn(TypeOfUserEnum.STANDARD.getId());
        when(userDao.setUserType(id, type)).thenReturn(false);

        // Act
        String result = userBean.updateUserType(loggedId, id, type);

        // Assert
        assertEquals("Error while updating user type for user with id: " + id, result);
    }

}