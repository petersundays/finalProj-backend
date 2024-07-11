package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.PasswordBean;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.EnumDTO;
import domcast.finalprojbackend.dto.userDto.*;
import domcast.finalprojbackend.enums.TypeOfUserEnum;
import domcast.finalprojbackend.enums.util.EnumUtil;
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

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * UserService class that handles user related operations.
 */
@Path("/user")
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private UserBean userBean;

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private InterestBean interestBean;

    @Inject
    private SkillBean skillBean;

    @Inject
    private PasswordBean passwordBean;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private TokenBean tokenBean;

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
     * @param input   The data to confirm the registration.
     * @param request The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @POST
    @Path("/confirm")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response confirmUser(MultipartFormDataInput input, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to confirm registration", ipAddress);

        // Check if the input contains the necessary keys
        if (!input.getFormDataMap().containsKey("user")) {
            logger.error("No data provided when trying to confirm registration");
            return Response.status(400).entity("No data provided").build();
        }

        Response response = null;

        try {
            FullRegistration user = userBean.extractFullRegistrationDto(input);
            logger.info("Extracted FullRegistration object: {}", user);

            if ((user.getInterestDtos() != null && !user.getInterestDtos().isEmpty()) || (user.getSkillDtos() != null && !user.getSkillDtos().isEmpty())) {

                // Create interests and skills
                if (!userBean.createInterestsAndSkillsForRegistration(user)) {
                    logger.error("Error creating interests and skills for user: {}", user);
                    response = Response.status(400).entity("Error creating interests and skills").build();
                    return response;
                }
            }

            // Update photo
            String photoPath = null;
            if (input.getFormDataMap().containsKey("photo")) {
                try {
                    photoPath = userBean.uploadPhoto(user.getValidationToken(), input);
                    logger.info("Uploaded photo for user: {}", user);
                } catch (IOException e) {
                    logger.error("Error uploading photo for user: {}", user, e);
                    response = Response.status(400).entity("Error uploading photo").build();
                    return response;
                }
            }

            // Complete registration
            if (userBean.fullRegistration(user, photoPath, ipAddress)) {
                logger.info("Completed registration for user: {}", user);

                response = Response.status(200).entity("User registered successfully").build();
                logger.info("User with IP address {} confirmed registration successfully", ipAddress);
            }
        } catch (Exception e) {
            logger.error("Error confirming registration for user with IP address {}", ipAddress, e);
            response = Response.status(400).entity("Error confirming registration: " + e.getMessage()).build();
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
    public Response logout(@HeaderParam("token") String sessionToken, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to logout", ipAddress);

        Response response;

        // Check if the id is valid
        if (!dataValidator.isIdValid(id)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to logout unsuccessfully", sessionToken);
            return response;
        }

        // Check if the user is authorized to log out
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to logout without authorization", sessionToken);
            return response;
        }

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
    public Response resetPassword(@HeaderParam("token") String validationToken, String password, @Context HttpServletRequest request) {
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
     * This method is transactional and requires a new transaction,
     * which means that if an exception occurs, the transaction will be rolled back.
     *
     * @param sessionToken The session token of the user to update the profile.
     * @param request      The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @PUT
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response updateUser(@HeaderParam("token") String sessionToken, @HeaderParam("id") int userId, MultipartFormDataInput input, @Context HttpServletRequest request) throws IOException {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to update their profile", ipAddress);

        // Check if the input contains the necessary keys
        if (!input.getFormDataMap().containsKey("user") && !input.getFormDataMap().containsKey("photo")) {
            logger.error("No data provided when trying to update the profile");
            return Response.status(400).entity("No data provided").build();
        }

        Response response;
        LoggedUser updatedUser;

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to update the profile unsuccessfully", sessionToken);
            return response;
        }

        // Check if the user is authorized to update the profile
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, userId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to update the profile from user with id {} without authorization", sessionToken, userId);
            return response;
        }

        tokenBean.setLastAccessToNow(sessionToken);

        try {
            UpdateUserDto user = null;
            // Extract UpdateUserDto from MultipartFormDataInput and create interests and skills
            if (input.getFormDataMap().containsKey("user")) {
                user = userBean.extractUpdateUserDto(input);
                // Create interests and skills
                if (!userBean.createInterestsAndSkillsForUpdate(user)) {
                    logger.error("Error creating interests and skills");
                    response = Response.status(400).entity("Error creating interests and skills").build();
                    return response;
                }
            }

            // Update photo
            String photoPath = null;
            if (input.getFormDataMap().containsKey("photo")) {
                try {
                    photoPath = userBean.uploadPhoto(sessionToken, input);
                } catch (Exception e) {
                    logger.error("Error uploading photo: {}", e.getMessage());
                    throw e; // rethrow the exception to roll back the transaction
                }
            }

            // Update profile
            updatedUser = userBean.updateUserProfile(user, userId, photoPath, sessionToken);

            // Convert the updatedUser object to a JSON string
            String updatedUserJson = userBean.convertUserToJson(updatedUser);

            response = Response.status(200).entity(updatedUserJson).build();
            logger.info("User with IP address {} updated its profile successfully", ipAddress);
        } catch (Exception e) {
            response = Response.status(400).entity("Error updating profile").build();
            logger.info("User with IP address {} tried to update its profile unsuccessfully", ipAddress);
        }

        return response;
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsersByCriteria(@HeaderParam("token") String sessionToken,
                                       @HeaderParam("id") int userId,
                                       @QueryParam("firstName") String firstName,
                                       @QueryParam("lastName") String lastName,
                                       @QueryParam("nickname") String nickname,
                                       @QueryParam("workplace") int workplace,
                                       @QueryParam("orderBy") String orderBy,
                                       @QueryParam("orderAsc") boolean orderAsc,
                                       @QueryParam("pageNumber") int pageNumber,
                                       @QueryParam("pageSize") int pageSize,
                                       @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to get users by criteria", ipAddress);

        Response response;
        List<SearchedUser> users;

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to get users by criteria unsuccessfully", sessionToken);
            return response;
        }

        // Check if the user is authorized to get users by criteria
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, userId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get users by criteria without authorization", sessionToken);
            return response;
        }

        tokenBean.setLastAccessToNow(sessionToken);

        try {
            users = userBean.getUsersByCriteria(firstName, lastName, nickname, workplace, orderBy, orderAsc, pageNumber, pageSize);
            response = Response.status(200).entity(users).build();
            logger.info("User with IP address {} got users by criteria successfully", ipAddress);
        } catch (IllegalArgumentException e) {
            response = Response.status(400).entity(e.getMessage()).build();
            logger.info("User with IP address {} tried to get users by criteria unsuccessfully", ipAddress);
        } catch (NoSuchElementException e) {
            response = Response.status(404).entity(e.getMessage()).build();
            logger.info("User with IP address {} tried to get users but no users were found", ipAddress);
        }

        return response;
    }

    /**
     * Updates the password for a user.
     *
     * @param sessionToken The session token of the user to update the password.
     * @param userId       The id of the user to update the password.
     * @param oldPassword  The old password.
     * @param newPassword  The new password.
     * @param request      The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @PUT
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePassword(@HeaderParam("token") String sessionToken,
                                   @HeaderParam("id") int userId,
                                   @HeaderParam("oldPassword") String oldPassword,
                                   @HeaderParam("newPassword") String newPassword,
                                   @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to update password", ipAddress);

        Response response;

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to update the password unsuccessfully", sessionToken);
            return response;
        }

        // Check if the user is authorized to update the password
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, userId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to update the password from user with id {} without authorization", sessionToken, userId);
            return response;
        }

        tokenBean.setLastAccessToNow(sessionToken);

        String result = passwordBean.updatePassword(userId, oldPassword, newPassword);
        if ("Password updated successfully".equals(result)) {
            response = Response.status(200).entity(result).build();
            logger.info("User with IP address {} updated the password successfully", ipAddress);
        } else {
            response = Response.status(400).entity(result).build();
            logger.info("User with IP address {} tried to update the password unsuccessfully. Error message: {}", ipAddress, result);
        }

        return response;
    }

    /**
     * Updates the user type for a user.
     *
     * @param sessionToken The session token of the user to update the user type.
     * @param userId       The id of the user to update the user type.
     * @param type         The new type of the user.
     * @param request      The HTTP request.
     * @return A response indicating the result of the operation.
     */
    @PUT
    @Path("/type")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserType(@HeaderParam("token") String sessionToken,
                                   @HeaderParam("loggedId") int loggedId,
                                   @QueryParam("id") int userId,
                                   @HeaderParam("type") int type,
                                   @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with IP address {} is trying to update user type of user with id {}", ipAddress, userId);

        Response response;

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with IP address {} tried to update the user type unsuccessfully, for user with id {}", ipAddress, userId);
            return response;
        }

        // Check if the user is authorized to update the user type
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, loggedId) || !authenticationAndAuthorization.isUserAdmin(sessionToken)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with SessionToken {} tried to update the user type from user with id {} without authorization", sessionToken, userId);
            return response;
        }

        tokenBean.setLastAccessToNow(sessionToken);

        try {
            String result = userBean.updateUserType(loggedId, userId, type);
            response = Response.status(200).entity(result).build();
            logger.info("User with IP address {} updated the user type successfully for user with id {}", ipAddress, userId);
        } catch (Exception e) {
            response = Response.status(400).entity("Error updating user type").build();
            logger.info("User with IP address {} tried to update the user type unsuccessfully, for user with id {}", ipAddress, userId);
        }

        return response;
    }

    @GET
    @Path("/public-profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublicProfile(@HeaderParam("token") String sessionToken,
                                     @HeaderParam("loggedId") int userId,
                                     @QueryParam("id") int id,
                                     @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with session token {} is trying to get public profile of user with id {}", sessionToken, id);

        Response response;
        PublicProfileUser publicProfileUser;

        if (!dataValidator.isIdValid(id)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to get the public profile of user with id {} unsuccessfully", sessionToken, id);
            return response;
        }

        // Check if the user is authorized to get the public profile
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, userId)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get the public profile from user with id {} without authorization", sessionToken, id);
            return response;
        }

        tokenBean.setLastAccessToNow(sessionToken);

        try {
            publicProfileUser = userBean.returnPublicProfile(id);
            response = Response.status(200).entity(publicProfileUser).build();
            logger.info("User with session token {} got the public profile of user with id {} successfully", sessionToken, id);
        } catch (Exception e) {
            response = Response.status(400).entity(e.getMessage()).build();
            logger.info("User with session token {} tried to get the public profile of user with id {} unsuccessfully. Error: {}", sessionToken, id, e.getMessage());
        }

        return response;
    }

    @GET
    @Path("/enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTypeOfUserEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the type of user enum", token, id);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the type of user enum but is not authorized", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the type of user enum without authorization", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);
        System.out.println("User with session token {} and id {} is getting the type of user enum" + token + id);
        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the type of user enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(TypeOfUserEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the type of user enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting type of user enum", e);
            response = Response.status(500).entity("Error getting type of user enum").build();
        }

        return response;
    }
}