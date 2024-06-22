package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;

@Path("/project")
public class ProjectService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private ProjectBean projectBean;

    @Inject
    private DataValidator dataValidator;

    @EJB
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response createProject (@HeaderParam("token") String token, @HeaderParam("id") int id, MultipartFormDataInput input, @Context HttpServletRequest request) throws IOException {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is creating a new project from IP address {}", token, id, ipAddress);

        Response response;

        if (!dataValidator.isIdValid(id)) {
            response = Response.status(400).entity("Invalid id").build();
            logger.info("User with session token {} tried to create a new project with invalid id {}", token, id);
            return response;
        }

        // Check if the user is authorized to create a new project
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            response = Response.status(401).entity("Unauthorized").build();
            logger.info("User with session token {} tried to create a new project but is not authorized", token);
            return response;
        }

        if (!input.getFormDataMap().containsKey("project")) {
            response = Response.status(400).entity("Missing project data").build();
            logger.info("User with session token {} tried to create a new project but the project data is missing", token);
            return response;
        }



    }

}
