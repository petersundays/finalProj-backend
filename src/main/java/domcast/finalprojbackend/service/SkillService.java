package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dto.skillDto.SkillToList;
import domcast.finalprojbackend.dto.userDto.EnumDTO;
import domcast.finalprojbackend.enums.SkillTypeEnum;
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

@Path("/skill")
public class SkillService {

    private static final Logger logger = LogManager.getLogger(SkillService.class);

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private SkillBean skillBean;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills(@HeaderParam("token") String sessionToken,
                                 @HeaderParam("id") int id,
                                 @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is getting skills from IP address {}", sessionToken, id, ipAddress);

        Response response;

        if (!dataValidator.isIdValid(id)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with token {} and id {} tried to get skills with invalid id", sessionToken, id);
            return response;
        }

        // Check if the user is authorized to get the public profile
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get interests but is not authorized", sessionToken);
            return response;
        }

        try {
            List<SkillToList> skills = skillBean.getAllSkills();

            if (skills == null || skills.isEmpty()) {
                logger.info("No skills found");
                return Response.status(204).build();
            }

            logger.info("User with session token {} and id {} got skills", sessionToken, id);
            response = Response.status(200).entity(skills).build();
        } catch (Exception e) {
            logger.error("Error getting interests", e);
            response = Response.status(500).entity("Error getting interests").build();
        }

        return response;
    }

    @GET
    @Path("/enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkillEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the skill enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the skill enum but has an invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the skill enum but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the skill enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(SkillTypeEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the skill enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting skill enum", e);
            response = Response.status(500).entity("Error getting skill enum").build();
        }

        return response;
    }

    @GET
    @Path("/enum-unconfirmed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkillEnumUnconfirmed(@HeaderParam("token") String validationToken, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with validationToken {} and is trying to get the skill enum from IP address {}", validationToken, ipAddress);

        Response response;
        
        // Check if the user is authorized to get the skills while in registration
        if (!authenticationAndAuthorization.isMemberNotConfirmedAndValTokenActive(validationToken)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with validation validationToken {} tried to get skills but is not authorized", validationToken);
            return response;
        }

        

        try {
            logger.info("User with validationToken {} is getting the skill enum", validationToken);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(SkillTypeEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with validationToken {} successfully got the skill enum", validationToken);
        } catch (Exception e) {
            logger.error("Error getting skill enum for user with validationToken {}", validationToken, e);
            response = Response.status(500).entity("Error getting skill enum").build();
        }

        return response;
    }

    @GET
    @Path("unconfirmed-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkillsUnconfirmed(@HeaderParam("token") String validationToken,
                                         @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with validation token {} is getting skills from IP address {}", validationToken, ipAddress);

        Response response;

        // Check if the user is authorized to get the skills while in registration
        if (!authenticationAndAuthorization.isMemberNotConfirmedAndValTokenActive(validationToken)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with validation token {} tried to get skills but is not authorized", validationToken);
            return response;
        }

        try {
            List<SkillToList> skills = skillBean.getAllSkills();

            if (skills == null || skills.isEmpty()) {
                logger.info("No skills found for user with validation token {}", validationToken);
                return Response.status(204).build();
            }

            logger.info("User with validation token {} got skills", validationToken);
            response = Response.status(200).entity(skills).build();
        } catch (Exception e) {
            logger.error("Error getting interests for user with validation token {}", validationToken, e);
            response = Response.status(500).entity("Error getting interests for user with validation token").build();
        }

        return response;
    }
}
