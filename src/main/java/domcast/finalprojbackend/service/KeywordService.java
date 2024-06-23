package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.KeywordBean;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
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

@Path("/keyword")
public class KeywordService {

    private static final Logger logger = LogManager.getLogger(KeywordService.class);

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private KeywordBean keywordBean;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKeywords(@HeaderParam("token") String sessionToken,
                                 @HeaderParam("id") int id,
                                 @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is getting keywords from IP address {}", sessionToken, id, ipAddress);

        Response response;

        if (!dataValidator.isIdValid(id)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with token {} and id {} tried to get keywords with invalid id", sessionToken, id);
            return response;
        }

        // Check if the user is authorized to get the public profile
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(sessionToken, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to get keywords but is not authorized", sessionToken);
            return response;
        }

        try {
            List<String> keywords = keywordBean.getAllKeywordNames();

            if (keywords == null || keywords.isEmpty()) {
                logger.info("No keywords found");
                return Response.status(204).build();
            }

            logger.info("User with session token {} and id {} got keywords", sessionToken, id);
            response = Response.status(200).entity(keywords).build();
        } catch (Exception e) {
            logger.error("Error getting keywords", e);
            response = Response.status(500).entity("Error getting keywords").build();
        }

        return response;
    }
}
