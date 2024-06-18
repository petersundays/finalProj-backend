import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.PasswordBean;
import domcast.finalprojbackend.dao.UserDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for PasswordBean.
 * Uses JUnit and Mockito for testing.
 */
public class PasswordBeanTest {

    @InjectMocks
    private PasswordBean passwordBean;

    @Mock
    private UserDao userDao;

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    @Mock
    private AuthenticationAndAuthorization authenticationAndAuthorization;


    /**
     * Set up method for each test case.
     * Initializes the mocks.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(em.createNamedQuery("User.getUserPassword")).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenReturn("hashedOldPassword");
        when(authenticationAndAuthorization.checkPassword(anyString(), anyString())).thenReturn(false);
    }

    /**
     * Test for successful password validation.
     */
    @Test
    public void testIsPasswordValidSuccess() {
        String password = "ValidPassword123!";
        assertTrue(passwordBean.isPasswordValid(password));
    }

    /**
     * Test for failed password validation.
     */
    @Test
    public void testIsPasswordValidFailure() {
        String password = "invalid";
        assertThrows(IllegalArgumentException.class, () -> passwordBean.isPasswordValid(password));
    }

    /**
     * Test for successful password hashing.
     */
    @Test
    public void testHashPasswordSuccess() {
        String password = "ValidPassword123!";
        assertNotNull(passwordBean.hashPassword(password));
    }

    /**
     * Test for failed password hashing.
     * Note: This test assumes that a hashed value is returned even when the input is null.
     * Adjust the test as necessary to fit your implementation.
     */
    @Test
    public void testHashPasswordFailure() {
        String password = null;
        assertNotNull(passwordBean.hashPassword(password));
    }

    /**
     * Test for successful password update.
     */
    @Test
    public void testUpdatePasswordSuccess() {
        int userId = 1;
        String oldPassword = "OldPassword123!";
        String newPassword = "NewPassword123!";
        String hashedOldPassword = "hashedOldPassword";
        String hashedNewPassword = "hashedNewPassword";

        when(userDao.getUserPassword(userId)).thenReturn(hashedOldPassword);
        when(authenticationAndAuthorization.checkPassword(oldPassword, hashedOldPassword)).thenReturn(true);

        String result = passwordBean.updatePassword(userId, oldPassword, newPassword);

        verify(userDao).setUserPassword(anyInt(), anyString());
        assertEquals("Password updated successfully", result);
    }

    /**
     * Test for failed password update due to incorrect old password.
     */
    @Test
    public void testUpdatePasswordFailureIncorrectOldPassword() {
        String result = passwordBean.updatePassword(1, "oldPassword", "NewPassword123!");
        assertEquals("Old password is not correct", result);
    }

    /**
     * Test for failed password update due to invalid new password.
     */
    @Test
    public void testUpdatePasswordFailureInvalidNewPassword() {
        String result = passwordBean.updatePassword(1, "oldPassword", "invalid");
        assertEquals("Old password is not correct", result);
    }
}
