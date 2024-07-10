package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.bean.user.UserBean;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dao.UserDao;
import domcast.finalprojbackend.dto.RecordDto;
import domcast.finalprojbackend.dto.userDto.RecordAuthor;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.entity.RecordEntity;
import domcast.finalprojbackend.entity.TaskEntity;
import domcast.finalprojbackend.entity.UserEntity;
import domcast.finalprojbackend.enums.MessageAndLogEnum;
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

    @EJB
    private ProjectDao projectDao;

    @EJB
    private UserDao userDao;

    private static final Logger logger = LogManager.getLogger(RecordBean.class);

    private static final long serialVersionUID = 1L;

    public RecordBean() {
    }

    /**
     * Creates a new record and adds it to the project.
     *
     * @param author    Author of the record.
     * @param project   Project to which the record will be added.
     * @param timestamp Timestamp of the record.
     * @param content   Content of the record.
     * @param type      Type of the record.
     * @param task      Task of the record.
     */
    public void newRecord(UserEntity author, ProjectEntity project, LocalDateTime timestamp, String content, MessageAndLogEnum type, TaskEntity task) {

        if (author == null) {
            logger.error("Author is null");
            return;
        }

        if (project == null) {
            logger.error("Project is null");
            return;
        }

        if (timestamp == null) {
            logger.error("Timestamp is null");
            return;
        }

        if (content == null || content.isEmpty()) {
            logger.error("Content is null or empty");
            return;
        }

        if (type == null) {
            logger.error("Type is not a valid enum");
            return;
        }

        if (task == null) {
            logger.error("Task is null");
        }

        logger.info("Creating new record");

        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setProject(project);
        recordEntity.setAuthor(author);
        recordEntity.setTimestamp(timestamp);
        recordEntity.setContent(content);
        recordEntity.setType(type);

        if (task != null) {
            recordEntity.setTask(task);
        }

        project.getRecords().add(recordEntity);

        try {
            projectDao.persist(project);
            projectDao.flush();
            logger.info("Project persisted with new record");
        } catch (Exception e) {
            logger.error("Error persisting project with new record");
        }

        logger.info("Record created successfully");
    }

    /**
     * Converts a RecordEntity to a RecordDto.
     * @param recordEntity RecordEntity to be converted.
     * @return RecordDto with the same data as the RecordEntity.
     */
    public RecordDto entityToRecordDto(RecordEntity recordEntity) {
        if (recordEntity == null) {
            logger.error("RecordEntity is null");
            return null;
        }

        RecordAuthor author = userBean.entityToRecordAuthor(recordEntity.getAuthor());

        RecordDto recordDto = new RecordDto();
        recordDto.setAuthor(author);
        recordDto.setTimestamp(recordEntity.getTimestamp());
        recordDto.setContent(recordEntity.getContent());
        recordDto.setType(recordEntity.getType().getId());

        return recordDto;
    }
}
