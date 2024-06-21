/*
package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.project.ProjectBean;
import domcast.finalprojbackend.dto.projectDto.NewProjectDto;
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

@Path("/project")
public class ProjectService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private ProjectBean projectBean;

    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response createProject (@HeaderParam("token") String token, @HeaderParam("id") int userId, MultipartFormDataInput input, @Context HttpServletRequest request) throws IOException {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is creating a new project from IP address {}", token, userId, ipAddress);


    }

}
*/
