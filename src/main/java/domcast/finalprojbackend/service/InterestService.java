package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dto.interestDto.InterestToList;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Path("/interest")
public class InterestService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private InterestBean interestBean;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterests(@HeaderParam("token") String sessionToken,
                                 @HeaderParam("id") int id,
                                 @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is getting interests from IP address {}", sessionToken, id, ipAddress);

        Response response;

        if (!dataValidator.isIdValid(id)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with token {} and id {} tried to get interests with invalid id", sessionToken, id);
            return response;
        }

        // Check if the user is authorized to get the public profile
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get interests but is not authorized", sessionToken);
            return response;
        }

        try {
            List<InterestToList> interests = interestBean.getAllInterests();

            if (interests == null || interests.isEmpty()) {
                logger.info("No interests found");
                return Response.status(204).build();
            }

            logger.info("User with session token {} and id {} got interests", sessionToken, id);
            response = Response.status(200).entity(interests).build();
        } catch (Exception e) {
            logger.error("Error getting interests", e);
            response = Response.status(500).entity("Error getting interests").build();
        }

        return response;
    }
}
