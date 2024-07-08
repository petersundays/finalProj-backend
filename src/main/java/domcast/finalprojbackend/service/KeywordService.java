package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.KeywordBean;
import domcast.finalprojbackend.bean.project.AuthenticationAndAuthorization;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
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
    public Response getKeywords(@Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with ip address {} got keywords", ipAddress);

        Response response;

        try {
            List<String> keywords = keywordBean.getAllKeywordNames();

            if (keywords == null || keywords.isEmpty()) {
                logger.info("No keywords found");
                return Response.status(204).build();
            }

            logger.info("User with ip address {} got keywords", ipAddress);
            response = Response.status(200).entity(keywords).build();
        } catch (Exception e) {
            logger.error("Error getting keywords", e);
            response = Response.status(500).entity("Error getting keywords").build();
        }

        return response;
    }
}
