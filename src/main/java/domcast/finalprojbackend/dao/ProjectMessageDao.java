package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.ProjectMessageEntity;
import jakarta.ejb.Stateless;

@Stateless
public class ProjectMessageDao extends AbstractDao<ProjectMessageEntity> {

    private static final long serialVersionUID = 1L;

    public ProjectMessageDao() {
        super(ProjectMessageEntity.class);
    }


}