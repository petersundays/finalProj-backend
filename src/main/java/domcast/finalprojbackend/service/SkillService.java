package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.SkillBean;
import domcast.finalprojbackend.bean.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.dto.skillDto.SkillToList;
import domcast.finalprojbackend.dto.EnumDTO;
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

    @Inject
    private TokenBean tokenBean;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSkills(@Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} got skills", ipAddress);

        Response response;

        try {
            List<SkillToList> skills = skillBean.getAllSkills();

            if (skills == null || skills.isEmpty()) {
                logger.info("No skills found");
                return Response.status(204).build();
            }

            logger.info("User with ip address {} got skills", ipAddress);
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

        tokenBean.setLastAccessToNow(token);

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
    public Response getSkillsUnconfirmed(@Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("An unlogged user with ip address {} is trying to get skills", ipAddress);

        Response response;


        try {
            List<SkillToList> skills = skillBean.getAllSkills();

            if (skills == null || skills.isEmpty()) {
                logger.info("No skills found for unlogged user with ip address {}", ipAddress);
                return Response.status(204).build();
            }

            logger.info("Unlogged user with ip address {} got skills", ipAddress);
            response = Response.status(200).entity(skills).build();
        } catch (Exception e) {
            logger.error("Error getting skills for unlogged user with ip address {}", ipAddress, e);
            response = Response.status(500).entity("Error getting skills").build();
        }

        return response;
    }
}
