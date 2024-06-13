package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.dao.ComponentResourceDao;
import domcast.finalprojbackend.dao.ProjectDao;
import domcast.finalprojbackend.dto.componentResourceDto.CRPreview;
import domcast.finalprojbackend.dto.componentResourceDto.DetailedCR;
import domcast.finalprojbackend.entity.ComponentResourceEntity;
import domcast.finalprojbackend.entity.M2MComponentProject;
import domcast.finalprojbackend.entity.ProjectEntity;
import domcast.finalprojbackend.enums.ComponentResourceEnum;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

/**
 * Bean class for the component resource.
 * Contains the methods to create a new component resource and convert a detailed component resource to a preview
 * component resource.
 * The class also contains the necessary annotations to work with the database.
 *
 * @author José Castro
 * @author Pedro Domingos
 */
@Stateless
public class ComponentResourceBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(ComponentResourceBean.class);

    @EJB
    private DataValidator dataValidator;

    @EJB
    private ComponentResourceDao componentResourceDao;

    @EJB
    private ProjectDao projectDao;

    // Default constructor
    public ComponentResourceBean() {
    }

    /**
     * Creates a new component resource based on the detailed component resource passed as parameter.
     * The method validates the data, registers the data in the database and creates a many-to-many relation between
     * the component resource and the project.
     * The method also converts the detailed component resource to a preview component resource.
     * @param detailedCR the detailed component resource to be created.
     * @return the preview component resource if created successfully, null otherwise.
     */
    public CRPreview createComponentResource(DetailedCR detailedCR) throws PersistenceException {
        logger.info("Creating component resource");

        if (detailedCR == null) {
            logger.error("Component resource is null");
            return null;
        }

        logger.info("Validating data");

        if (!dataValidator.isCRMandatoryDataValid(detailedCR)) {
            logger.error("Mandatory data is not valid");
            return null;
        }

        logger.info("Data is valid");

        ComponentResourceEntity componentResourceEntity = registerData(detailedCR);

        if (componentResourceEntity == null) {
            logger.error("Error registering component resource data");
            return null;
        }

        CRPreview crPreview;

        try {
            crPreview = detailedToPreviewCR(detailedCR);
        } catch (Exception e) {
            logger.error("Error converting detailed component resource to preview: {}", e.getMessage());
            return null;
        }

        if (crPreview == null) {
            logger.error("Error converting detailed component resource to preview");
            return null;
        }

        logger.info("Component resource created successfully");

        return crPreview;


    }

/**
     * Registers a new component resource in the database.
     *
     * @param detailedCR the detailed component resource to be registered
     * @return the ComponentResourceEntity object if registered, null otherwise
     */
    public ComponentResourceEntity registerData(DetailedCR detailedCR) throws PersistenceException {
        logger.info("Registering component resource data");

        if (detailedCR == null) {
            logger.error("Component resource is null while registering data");
            return null;
        }

        logger.info("Creating entity");

        ComponentResourceEntity componentResourceEntity = new ComponentResourceEntity();

        componentResourceEntity.setName(detailedCR.getName());
        componentResourceEntity.setType(ComponentResourceEnum.fromId(detailedCR.getType()));
        componentResourceEntity.setDescription(detailedCR.getDescription());
        componentResourceEntity.setBrand(detailedCR.getBrand());
        componentResourceEntity.setPartNumber(detailedCR.getPartNumber());
        componentResourceEntity.setSupplier(detailedCR.getSupplier());
        componentResourceEntity.setSupplierContact(detailedCR.getSupplierContact());

        boolean alreadyExists = componentResourceDao.doesCRExistByNameAndBrand(detailedCR.getName(), detailedCR.getBrand());

        if (alreadyExists) {
            logger.error("Component resource already exists");
            return null;
        }

        logger.info("Persisting entity");
        componentResourceDao.persist(componentResourceEntity);

        logger.info("Entity persisted");

        ComponentResourceEntity componentResourceFromDb = componentResourceDao.findCREntityByNameAndBrand(detailedCR.getName(), detailedCR.getBrand());

        logger.info("Component resource entity found");

        addCRToProject(detailedCR.getProjectId(), componentResourceFromDb, detailedCR.getQuantity());

        logger.info("Relation between component resource and project created");

        return componentResourceEntity;
    }

    /**
     * Creates a many-to-many relation between a component resource and a project.
     *
     * @param projectId the project id to create the relation with
     * @param componentResource the component resource to create the relation with
     * @param quantity the quantity of the component resource
     */
    public void addCRToProject(int projectId, ComponentResourceEntity componentResource, int quantity) {
        logger.info("Creating many-to-many relation between component resource and project");

        if (!dataValidator.isIdValid(projectId) || componentResource == null || quantity <= 0) {
            logger.error("Invalid ids or quantity");
            return;
        }

        logger.info("Creating relation");

        ProjectEntity projectEntity;
        try {
            projectEntity = projectDao.findProjectById(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding project: {}", e.getMessage());
            return;
        }

        try {
            M2MComponentProject m2MComponentProject = new M2MComponentProject();
            m2MComponentProject.setProject(projectEntity);
            m2MComponentProject.setComponentResource(componentResource);
            m2MComponentProject.setQuantity(quantity);
            componentResource.addProject(m2MComponentProject);
            projectEntity.addComponentResource(m2MComponentProject);
            projectDao.merge(projectEntity);
            componentResourceDao.merge(componentResource);
        } catch (PersistenceException e) {
            logger.error("Error creating relation: {}", e.getMessage());
        }

        logger.info("Relation component-resource with project created for project id: {} and component resource id: {}", projectId, componentResource);
    }

    /**
     * Converts a detailed component resource to a preview component resource.
     *
     * @param detailedCR the detailed component resource to convert
     * @return the preview component resource if converted, null otherwise
     */
    public CRPreview detailedToPreviewCR(DetailedCR detailedCR) {
        logger.info("Converting detailed component resource to preview");

        if (detailedCR == null) {
            logger.error("Detailed component resource is null");
            return null;
        }

        logger.info("Creating preview");

        CRPreview crPreview = new CRPreview();

        crPreview.setId(detailedCR.getId());
        crPreview.setName(detailedCR.getName());
        crPreview.setType(detailedCR.getType());
        crPreview.setBrand(detailedCR.getBrand());
        crPreview.setPartNumber(detailedCR.getPartNumber());
        crPreview.setSupplier(detailedCR.getSupplier());

        return crPreview;
    }
}
