package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.dto.UserDto.FullRegistration;
import domcast.finalprojbackend.dto.UserDto.LoggedUser;
import domcast.finalprojbackend.dto.UserDto.Login;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * UserService class that handles user related operations.
 */
@Path("/user")
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private UserBean userBean;

    /**
     * Registers a new user.
     *
     * @param user    The user to register.
     * @param request The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(FirstRegistration user, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to register", ipAddress);

        Response response;

        if (userBean.registerEmail(user)) {
            response = Response.status(201).entity("User registered successfully").build();
            logger.info("User with IP address {} registered the email {} successfully", ipAddress, user.getEmail());
        } else {
            response = Response.status(400).entity("Error registering user with email " + user.getEmail()).build();
            logger.info("User with IP address {} tried to register the email {} unsuccessfully", ipAddress, user.getEmail());
        }

        return response;
    }

    /**
     * Confirms a user's registration.
     *
     * @param user    The user to confirm.
     * @param request The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @POST
    @Path("/confirm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmUser(FullRegistration user, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to confirm registration", ipAddress);

        Response response;

        if (userBean.fullRegistration(user)) {
            response = Response.status(200).entity("User confirmed registration successfully").build();
            logger.info("User with IP address {} confirmed registration successfully with validation token {}", ipAddress, user.getValidationToken());
        } else {
            response = Response.status(400).entity("Error confirming registration").build();
            logger.info("User with IP address {} tried to confirm registration unsuccessfully with validation token {}", ipAddress, user.getValidationToken());
        }

        return response;
    }

    /**
     * Logs a user in.
     *
     * @param userToLogin The user to log in.
     * @param request     The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Login userToLogin, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to login", ipAddress);

        Response response;
        LoggedUser loggedUser;

        try {
            loggedUser = userBean.login(userToLogin, ipAddress);
            response = Response.status(200).entity(loggedUser).build();
            logger.info("User with IP address {} logged in successfully with email {}", ipAddress, userToLogin.getEmail());
        } catch (Exception e) {
            response = Response.status(400).entity("Error logging in").build();
            logger.info("User with IP address {} tried to login unsuccessfully with email {}", ipAddress, userToLogin.getEmail());
        }

        return response;
    }

    /**
     * Logs a user out.
     *
     * @param sessionToken The session token of the user to log out.
     * @param request      The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @POST
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("token") String sessionToken, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to logout", ipAddress);

        Response response;

        if (userBean.logout(sessionToken)) {
            response = Response.status(200).entity("User logged out successfully").build();
            logger.info("User with IP address {} logged out successfully", ipAddress);
        } else {
            response = Response.status(400).entity("Error logging out").build();
            logger.info("User with IP address {} tried to logout unsuccessfully", ipAddress);
        }

        return response;
    }
}