package domcast.finalprojbackend.bean.user.startup;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.*;
import jakarta.inject.Inject;

/**
 * Bean that creates the default labs and the default user
 */
@Singleton
@Startup
public class StartupBean {

    @Inject
    UserAndLabCreator userAndLabCreator;

    /**
     * Method that creates the default labs and the default user
     */
    @PostConstruct
    public void init() {
        userAndLabCreator.createDefaultLabs();
        userAndLabCreator.createDefaultUser();
    }
}
