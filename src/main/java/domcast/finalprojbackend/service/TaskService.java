package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.bean.task.TaskBean;
import domcast.finalprojbackend.bean.AuthenticationAndAuthorization;
import domcast.finalprojbackend.bean.user.TokenBean;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.taskDto.DetailedTask;
import domcast.finalprojbackend.dto.taskDto.EditTask;
import domcast.finalprojbackend.dto.taskDto.NewTask;
import domcast.finalprojbackend.dto.EnumDTO;
import domcast.finalprojbackend.enums.TaskStateEnum;
import domcast.finalprojbackend.enums.util.EnumUtil;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Path("/task")
public class TaskService  {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private TaskBean taskBean;

    @Inject
    private DataValidator dataValidator;

    @Inject
    private AuthenticationAndAuthorization authenticationAndAuthorization;

    @Inject
    private TokenBean tokenBean;

    /**
     * Creates a new task based on the new task passed as parameter.
     * The method validates the data, registers the data in the database and returns the created task.
     * @param token the token of the user creating the task.
     * @param userId the id of the user creating the task.
     * @param newTask the new task to be created.
     * @return the created task if created successfully, null otherwise.
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTask(@HeaderParam("token") String token, @HeaderParam("id") int userId, NewTask<Integer> newTask, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is creating a new task from IP address {}", token, ipAddress);

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId) && !dataValidator.isIdValid(newTask.getProjectId())) {
            logger.info("User with session token {} tried to create a task unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!authenticationAndAuthorization.ableToEditProject(newTask.getProjectId())) {
            logger.info("User with session token {} tried to create a task unsuccessfully", token);
            return Response.status(401).entity("Project is not able to be edited").build();
        }

        // Check if the user is authorized to create the task
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, newTask.getProjectId())) {
            logger.info("User with session token {} tried to create a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);

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

    /**
     * Gets the detailed information of a task based on the task id passed as parameter.
     * The method validates the data, gets the task from the database and returns the detailed task.
     * @param token the token of the user getting the task.
     * @param userId the id of the user getting the task.
     * @param taskId the id of the task to get the detailed information.
     * @param projectId the id of the project the task belongs to.
     * @return the detailed task if found, null otherwise.
     */
    @GET
    @Path("/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetailedTask(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("id") int taskId, @QueryParam("projectId") int projectId, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is getting the detailed information of the task with id {} from IP address {}", token, taskId, ipAddress);

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(taskId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to get the detailed information of a task unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the task
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
            logger.info("User with session token {} tried to get the detailed information of a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);

        Response response;
        DetailedTask detailedTask;

        // Get the task
        try {
            detailedTask = taskBean.createDetailedTask(taskId);
            logger.info("User with session token {} got the detailed information of the task with id {} from IP address {}", token, taskId, ipAddress);
            response = Response.status(200).entity(detailedTask).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error getting task", e);
            response = Response.status(400).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Error getting task", e);
            response = Response.status(500).entity(e.getMessage()).build();
        }

        return response;
    }

    /**
     * Changes the state of a task based on the task id and the new state passed as parameter.
     * The method validates the data, changes the state of the task in the database and returns the detailed task.
     * @param token the token of the user changing the state of the task.
     * @param userId the id of the user changing the state of the task.
     * @param taskId the id of the task to change the state.
     * @param projectId the id of the project the task belongs to.
     * @param state the new state of the task.
     * @return the detailed task if the state was changed successfully, null otherwise.
     */
    @PUT
    @Path("/state")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeTaskState(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("id") int taskId, @QueryParam("projectId") int projectId, int state, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is changing the state of the task with id {} to {} from IP address {}", token, taskId, state, ipAddress);

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(taskId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to change the state of a task unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!authenticationAndAuthorization.ableToEditProject(projectId)) {
            logger.info("User with session token {} tried to change the state of a task unsuccessfully", token);
            return Response.status(401).entity("Project is not able to be edited").build();
        }

        // Check if the user is authorized to change the state of the task
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
            logger.info("User with session token {} tried to change the state of a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);

        Response response;
        DetailedTask detailedTask;

        // Change the state of the task
        try {
            detailedTask = taskBean.updateTaskState(taskId, state, userId);
            logger.info("User with session token {} changed the state of the task with id {} to {} from IP address {}", token, taskId, state, ipAddress);
            response = Response.status(200).entity(detailedTask).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error changing task state", e);
            response = Response.status(400).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Error changing task state", e);
            response = Response.status(500).entity(e.getMessage()).build();
        }

        return response;
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTasks(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("projectId") int projectId, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is getting the tasks of the project with id {} from IP address {}", token, projectId, ipAddress);

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to get the tasks unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the tasks
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
            logger.info("User with session token {} tried to get the tasks unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);

        Response response;
        List<ChartTask> chartTasks;

        // Get the tasks
        try {
            chartTasks = taskBean.findTaskByProjectId(projectId);
            logger.info("User with session token {} got the tasks of the project with id {} from IP address {}", token, projectId, ipAddress);
            response = Response.status(200).entity(chartTasks).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error getting tasks", e);
            response = Response.status(400).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Error getting tasks", e);
            response = Response.status(500).entity(e.getMessage()).build();
        }

        return response;
    }

    @PUT
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTask(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("id") int taskId, EditTask editTask, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is updating the task with id {} from IP address {}", token, taskId, ipAddress);

        // Check if the id is valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(taskId) || !dataValidator.isIdValid(editTask.getProjectId())) {
            logger.info("User with session token {} tried to update a task unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!authenticationAndAuthorization.ableToEditProject(editTask.getProjectId())) {
            logger.info("User with session token {} tried to apply to the project but the project is not in a state that can be edited", token);
            return Response.status(401).entity("Unauthorized: Project is not in a state that can be edited").build();
        }

        // Check if the user is authorized to update the task
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, editTask.getProjectId())) {
            logger.info("User with session token {} tried to update a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);

        Response response;
        DetailedTask detailedTask;

        // Update the task
        try {
            detailedTask = taskBean.editTask(editTask, taskId, userId);
            logger.info("User with session token {} updated the task with id {} from IP address {}", token, taskId, ipAddress);
            response = Response.status(200).entity(detailedTask).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error updating task", e);
            response = Response.status(400).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Error updating task", e);
            response = Response.status(500).entity(e.getMessage()).build();
        }

        return response;
    }

    @GET
    @Path("/enum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaskStateEnum(@HeaderParam("token") String token, @HeaderParam("id") int id, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} and id {} is trying to get the task state enum from IP address {}", token, id, ipAddress);

        // Check if the user's id is valid
        if (!dataValidator.isIdValid(id)) {
            logger.info("User with session token {} tried to get the task state enum but has an invalid id", token);
            return Response.status(400).entity("Invalid id").build();
        }

        // Check if the user is authorized to get the component resource enum
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, id)) {
            logger.info("User with session token {} tried to get the task state enum but is not authorized", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);

        Response response;

        try {
            logger.info("User with session token {} and id {} is getting the task state enum", token, id);
            List<EnumDTO> enumDTOs = EnumUtil.getAllEnumDTOs(TaskStateEnum.class);
            response = Response.status(200).entity(enumDTOs).build();
            logger.info("User with session token {} and id {} successfully got the task state enum", token, id);
        } catch (Exception e) {
            logger.error("Error getting task state enum", e);
            response = Response.status(500).entity("Error getting task state enum").build();
        }

        return response;
    }

    @PUT
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteTask(@HeaderParam("token") String token, @HeaderParam("id") int userId, @QueryParam("projectId") int projectId, @QueryParam("id") int taskId, @Context HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("User with token {} is deleting the task with id {}, from project with id {} from IP address {}", token, taskId, projectId, ipAddress);

        // Check if user and task's id are valid
        if (!dataValidator.isIdValid(userId) || !dataValidator.isIdValid(taskId) || !dataValidator.isIdValid(projectId)) {
            logger.info("User with session token {} tried to delete a task unsuccessfully", token);
            return Response.status(400).entity("Invalid id").build();
        }

        if (!authenticationAndAuthorization.ableToEditProject(projectId)) {
            logger.info("User with session token {} tried to delete a task unsuccessfully", token);
            return Response.status(401).entity("Project is not able to be edited").build();
        }

        // Check if the user is authorized to delete the task
        if (!authenticationAndAuthorization.isTokenActiveAndFromUserId(token, userId) &&
                !authenticationAndAuthorization.isUserMemberOfTheProjectAndActive(userId, projectId)) {
            logger.info("User with session token {} tried to delete a task unsuccessfully", token);
            return Response.status(401).entity("Unauthorized").build();
        }

        tokenBean.setLastAccessToNow(token);

        Response response;
        boolean deleted;

        // Delete the task
        try {
            deleted = taskBean.deleteTask(taskId, userId);
            logger.info("User with session token {} deleted the task with id {} from IP address {}", token, taskId, ipAddress);
            response = Response.status(200).entity(deleted).build();
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting task", e);
            response = Response.status(400).entity(e.getMessage()).build();
        } catch (RuntimeException e) {
            logger.error("Error deleting task", e);
            response = Response.status(500).entity(e.getMessage()).build();
        }

        return response;
    }
}
