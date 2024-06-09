package domcast.finalprojbackend.bean.task;

import domcast.finalprojbackend.bean.DataValidator;
import domcast.finalprojbackend.dto.taskDto.ChartTask;
import domcast.finalprojbackend.dto.taskDto.NewTask;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

@Stateless
public class TaskBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(TaskBean.class);

    @EJB
    private DataValidator dataValidator;

    // Default constructor
    public TaskBean() {
    }

    public ChartTask newTask(NewTask newTask) {
        logger.info("Creating new task");

        if (newTask == null) {
            logger.error("The new task is null");
            return null;
        }

        if (!dataValidator.isTaskMandatoryDataValid(newTask)) {
            logger.error("The new task does not have all the mandatory data");
            return null;
        }

        // Create the new task an persist it in the database
    }
}
