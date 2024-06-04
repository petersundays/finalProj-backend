package domcast.finalprojbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.user.AuthenticationBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.InterestDto;
import domcast.finalprojbackend.dto.SkillDto;
import domcast.finalprojbackend.dto.UserDto.*;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.util.ArrayList;
import java.util.List;

/**
 * UserService class that handles user related operations.
 */
@Path("/user")
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private UserBean userBean;

    @Inject
    private AuthenticationBean authenticationBean;

    @Inject
    private InterestBean interestBean;

    @Inject
    private SkillBean skillBean;

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

        if (userBean.registerEmail(user, ipAddress)) {
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
     * Uploads a photo for a user.
     *
     * @param token The session token of the user.
     * @param input The photo to upload.
     * @param request The HTTP request.
     * @return A response indicating the result of the operation, with the path of the uploaded photo.
     */
    @POST
    @Path("/photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPhoto(@HeaderParam("token") String token, MultipartFormDataInput input, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to upload a photo", ipAddress);

        Response response;

        try {
            String photoPath = userBean.uploadPhoto(token, input);
            response = Response.status(200).entity(photoPath).build();
            logger.info("User with IP address {} uploaded a photo successfully", ipAddress);
        } catch (Exception e) {
            response = Response.status(400).entity("Error uploading photo").build();
            logger.info("User with IP address {} tried to upload a photo unsuccessfully", ipAddress);
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

    /**
     * Recovers a user's password.
     *
     * @param email   The email of the user to recover the password.
     * @param request The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @POST
    @Path("/recover-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response recoverPassword(@HeaderParam("email") String email, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to recover password with email {}", ipAddress, email);

        Response response;

        if (userBean.recoverPassword(email, ipAddress)) {
            response = Response.status(200).entity("Password recovery email sent successfully").build();
            logger.info("An email was sent to {} with a password recovery link, to user with IP address {}", email, ipAddress);
        } else {
            response = Response.status(400).entity("Error sending password recovery email").build();
            logger.info("Failed to send an email to {} with a password recovery link, to user with IP address {}", email, ipAddress);
        }

        return response;
    }

    /**
     * Resets a user's password.
     *
     * @param validationToken The validation token of the user to reset the password.
     * @param password        The new password.
     * @param request         The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @PUT
    @Path("/reset-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@HeaderParam("token") String validationToken, @HeaderParam("password") String password, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to change password", ipAddress);

        Response response;

        if (userBean.resetPassword(validationToken, password)) {
            response = Response.status(200).entity("Password changed successfully").build();
            logger.info("User with IP address {} changed password successfully", ipAddress);
        } else {
            response = Response.status(400).entity("Error changing password").build();
            logger.info("User with IP address {} tried to change password unsuccessfully", ipAddress);
        }

        return response;
    }

    /**
     * Updates a user's profile.
     *
     * @param sessionToken The session token of the user to update the profile.
     * @param user         The updated user.
     * @param request      The HTTP request.
     * @return A response indicating the result of the operation.
     */
    /*@PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@HeaderParam("token") String sessionToken, UpdateUserDto user, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to update their profile", ipAddress);

        Response response;
        LoggedUser updatedUser;

        if (!authenticationBean.isTokenActiveAndFromUserId(sessionToken, user.getId())) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with IP address {} tried to update the profile from user with id {} without authorization", ipAddress, user.getId());
            return response;
        }

        try {
            updatedUser = userBean.updateUserProfile(user, sessionToken);

            // Convert the updatedUser object to a JSON string
            ObjectMapper mapper = new ObjectMapper();
            String updatedUserJson = mapper.writeValueAsString(updatedUser);

            response = Response.status(200).entity(updatedUserJson).build();
            logger.info("User with IP address {} updated its profile successfully", ipAddress);
        } catch (Exception e) {
            response = Response.status(400).entity("Error updating profile").build();
            logger.info("User with IP address {} tried to update its profile unsuccessfully", ipAddress);
        }

        return response;
    }*/

    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateUser(@HeaderParam("token") String sessionToken, ArrayList<InterestDto> interests, ArrayList<SkillDto> skills, UpdateUserDto user, @Context HttpServletRequest request, MultipartFormDataInput input) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to update their profile", ipAddress);

        Response response;
        LoggedUser updatedUser;

        if (!authenticationBean.isTokenActiveAndFromUserId(sessionToken, user.getId())) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with IP address {} tried to update the profile from user with id {} without authorization", ipAddress, user.getId());
            return response;
        }

        try {

            // Create interests
            interestBean.createInterests(interests);

            // Create skills
            skillBean.createSkills(skills);

            // Update photo
            String photoPath = userBean.uploadPhoto(sessionToken, input);

            // Update profile
            updatedUser = userBean.updateUserProfile(user, photoPath, sessionToken);

            // Convert the updatedUser object to a JSON string
            ObjectMapper mapper = new ObjectMapper();
            String updatedUserJson = mapper.writeValueAsString(updatedUser);

            response = Response.status(200).entity(updatedUserJson).build();
            logger.info("User with IP address {} updated its profile successfully", ipAddress);
        } catch (Exception e) {
            response = Response.status(400).entity("Error updating profile").build();
            logger.info("User with IP address {} tried to update its profile unsuccessfully", ipAddress);
        }

        return response;
    }


}