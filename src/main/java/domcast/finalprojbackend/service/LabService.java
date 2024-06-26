package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dto.userDto.EnumDTO;
import domcast.finalprojbackend.enums.LabEnum;
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

@Path("/lab")
public class LabService {

    private static final Logger logger = LogManager.getLogger(LabService.class);

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private DataValidator dataValidator;
    
    @GET
    @Path("/enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLabEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the lab enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the lab enum with invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the lab enum but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the lab enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(LabEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the lab enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting lab enum", e);
            response = Response.status(500).entity("Error getting lab enum").build();
        }

        return response;
    }

    @GET
    @Path("/enum-unconfirmed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLabEnumUnconfirmed(@HeaderParam("token") String validationToken, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with validationToken {} is trying to get the lab enum from IP address {}", validationToken, ipAddress);

        Response response;

        // Check if the user is authorized to get the labs while in registration
        if (!authenticationAndAuthorization.isMemberNotConfirmedAndValTokenActive(validationToken)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with validation validationToken {} tried to get the labs but is not authorized", validationToken);
            return response;
        }

        try {
            logger.info("User with validationToken {} is getting the lab enum", validationToken);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(LabEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with validationToken {} successfully got the lab enum", validationToken);
        } catch (Exception e) {
            logger.error("Error getting lab enum while in registration", e);
            response = Response.status(500).entity("Error getting lab enum while in registration").build();
        }

        return response;
    }
}
