package domcast.finalprojbackend.bean.startup;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.*;
import jakarta.inject.Inject;

import java.io.Serializable;

/**
 * Bean that creates the default labs and the default user
 */
@Singleton
@Startup
public class StartupBean implements Serializable {

    @Inject
    StartupCreator startupCreator;

    /**
     * Creates the default labs, the default user and sets the default system variables
     */
    @PostConstruct
    public void init() {
        startupCreator.createDefaultLabs();
        startupCreator.createDefaultSkills();
        startupCreator.createDefaultInterests();
        startupCreator.createDefaultUsers();
        startupCreator.setDefaultSystemVariables();
        startupCreator.createDefaultProjects();
    }
}
