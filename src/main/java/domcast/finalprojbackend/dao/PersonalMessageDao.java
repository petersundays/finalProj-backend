package domcast.finalprojbackend.dao;

import domcast.finalprojbackend.entity.PersonalMessageEntity;
import jakarta.ejb.Stateless;

@Stateless
public class PersonalMessageDao extends AbstractDao<PersonalMessageEntity> {

    private static final long serialVersionUID = 1L;

    public PersonalMessageDao() {
        super(PersonalMessageEntity.class);
    }


}