package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.user.AuthenticationAndAuthorization;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.taskDto.NewTask;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/task")
public class TaskService  {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private TaskBean taskBean;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTask(@HeaderParam("token") String token, @HeaderParam("id") int userId, NewTask newTask, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is creating a new task from IP address {}", token, ipAddress);

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId)) {
            logger.info("User with session token {} tried to create a task unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to create the task
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProject(userId, newTask.getProjectId())) {
            logger.info("User with session token {} tried to create a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        Response response;
        ChartTask chartTask;

        // Create the task
        try {
            chartTask = taskBean.newTask(newTask);
            logger.info("User with session token {} created a new task from IP address {}", token, ipAddress);
            response = Response.status(201).entity(chartTask).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error creating task", e);
            response = Response.status(400).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Error creating task", e);
            response = Response.status(500).entity(e.getMessage()).build();
        }

        return response;
    }


}
