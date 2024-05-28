package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.UserDto.FirstRegistration;
import domcast.finalprojbackend.dto.UserDto.FullRegistration;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/user")
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private UserBean userBean;

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

}
