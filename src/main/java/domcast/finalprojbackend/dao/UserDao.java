package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.UserEntity;
import jakarta.ejb.Stateless;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;

    public UserDao() {
        super(UserEntity.class);
    }

}
