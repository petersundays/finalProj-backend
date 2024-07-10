package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dto.RecordDto;
import domcast.finalprojbackend.dto.userDto.ProjectUser;
import domcast.finalprojbackend.entity.M2MProjectUser;
import domcast.finalprojbackend.enums.RecordTopicEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;

@Stateless
public class RecordBean implements Serializable {

    @EJB
    private UserBean userBean;

    private static final Logger logger = LogManager.getLogger(RecordBean.class);

    private static final long serialVersionUID = 1L;

    public RecordBean() {
    }

    public RecordDto newRecord(M2MProjectUser author, LocalDateTime timestamp, String content, int type) {

        if (author == null) {
            logger.error("Author is null");
            return null;
        }

        if (timestamp == null) {
            logger.error("Timestamp is null");
            return null;
        }

        if (content == null || content.isEmpty()) {
            logger.error("Content is null or empty");
            return null;
        }

        if (!RecordTopicEnum.isValidEnum(type)) {
            logger.error("Type is not a valid enum");
            return null;
        }

        logger.info("Creating new record");

        ProjectUser projectUser = userBean.projectUserToProjectUserDto(author);

        logger.info("Record created successfully");

        return new RecordDto(projectUser, timestamp, content, type);
    }
}
