package domcast.finalprojbackend.bean.startup;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;

/**
 * Bean that creates the default labs and the default user
 */
@Singleton
@Startup
public class StartupBean implements Serializable {

    @Inject
    StartupCreator startupCreator;

    @PersistenceContext
    EntityManager em;

    /**
     * Creates the default labs, the default user and sets the default system variables
     */
    @PostConstruct
    public void init() {
        // Check if the database is empty
        try {
            em.createQuery("SELECT 1 FROM LabEntity ").setMaxResults(1).getSingleResult();
            // If the query does not throw an exception, then the database is not empty
            return;
        } catch (NoResultException e) {
            // If the query throws a NoResultException, then the database is empty
        }

        startupCreator.createDefaultLabs();
        startupCreator.createDefaultSkills();
        startupCreator.createDefaultInterests();
        startupCreator.createDefaultUsers();
        startupCreator.setDefaultSystemVariables();
        startupCreator.createDefaultProjects();
    }
}