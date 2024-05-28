package domcast.finalprojbackend.bean.user.startup;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.*;
import jakarta.inject.Inject;

@Singleton
@Startup
public class StartupBean {

    @Inject
    UserAndLabCreator userAndLabCreator;

    @PostConstruct
    public void init() {
        userAndLabCreator.createDefaultLabs();
        userAndLabCreator.createDefaultUser();
    }
}
