package domcast.finalprojbackend.service;

import domcast.finalprojbackend.bean.user.UserBean;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

@Path("/user")
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Inject
    private UserBean userBean;

}
