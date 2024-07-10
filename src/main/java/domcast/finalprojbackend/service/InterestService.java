package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.InterestBean;
import domcast.finalprojbackend.bean.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dto.interestDto.InterestToList;
import domcast.finalprojbackend.dto.userDto.EnumDTO;
import domcast.finalprojbackend.enums.InterestEnum;
import domcast.finalprojbackend.enums.util.EnumUtil;
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

    private static final Logger logger = LogManager.getLogger(InterestService.class);

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

    @GET
    @Path("/enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterestEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the interest enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the interest enum with invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the interest enum but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the interest enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(InterestEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the interest enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting interest enum", e);
            response = Response.status(500).entity("Error getting interest enum").build();
        }

        return response;
    }

    @GET
    @Path("/enum-unconfirmed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterestEnumUnconfirmed(@HeaderParam("token") String validationToken,@Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with validationToken {} is trying to get the interest enum from IP address {}", validationToken, ipAddress);

        Response response;

        // Check if the user is authorized to get the interests while in registration
        if (!authenticationAndAuthorization.isMemberNotConfirmedAndValTokenActive(validationToken)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with validation validationToken {} tried to get interests but is not authorized", validationToken);
            return response;
        }

        try {
            logger.info("User with  validationToken {} is getting the interest enum", validationToken);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(InterestEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with validationToken {} successfully got the interest enum", validationToken);
        } catch (Exception e) {
            logger.error("Error getting interest enum while in registration", e);
            response = Response.status(500).entity("Error getting interest enum while in registration").build();
        }

        return response;
    }

    @GET
    @Path("unconfirmed-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInterestsUnconfirmed(@HeaderParam("token") String validationToken,
                                            @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with validation token {} is getting interests from IP address {}", validationToken, ipAddress);

        Response response;

        // Check if the user is authorized to get the public profile
        if (!authenticationAndAuthorization.isMemberNotConfirmedAndValTokenActive(validationToken)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with validation token {} tried to get interests but is not authorized", validationToken);
            return response;
        }

        try {
            List<InterestToList> interests = interestBean.getAllInterests();

            if (interests == null || interests.isEmpty()) {
                logger.info("No interests found for user with validation token {}", validationToken);
                return Response.status(204).build();
            }

            logger.info("User with validation token {} got interests", validationToken);
            response = Response.status(200).entity(interests).build();
        } catch (Exception e) {
            logger.error("Error getting interests", e);
            response = Response.status(500).entity("Error getting interests").build();
        }

        return response;
    }
}
