package domcast.finalprojbackend.bean;

import domcast.finalprojbackend.dao.ComponentResourceDao;
import domcast.finalprojbackend.dao.M2MComponentProjectDao;
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

    @EJB
    private M2MComponentProjectDao m2MComponentProjectDao;

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
    public CRPreview createComponentResource(DetailedCR detailedCR, Integer projectId, Integer quantity) throws PersistenceException {
        logger.info("Creating component resource");

        if (detailedCR == null) {
            logger.error("Component resource is null");
            return null;
        }

        logger.info("Validating data");

        if (!dataValidator.isCRMandatoryDataValid(detailedCR, projectId)) {
            logger.error("Mandatory data is not valid");
            return null;
        }

        logger.info("Data is valid");

        ComponentResourceEntity componentResourceEntity = registerData(detailedCR, projectId, quantity);

        if (componentResourceEntity == null) {
            logger.error("Error registering component resource data");
            return null;
        }

        CRPreview crPreview;

        try {
            crPreview = entityToPreviewCR(componentResourceEntity);
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
     * Overloaded method for creating a new component resource without associating it with a project.
     * @param detailedCR the detailed component resource to be created.
     * @return the preview component resource if created successfully, null otherwise.
     */
    public CRPreview createComponentResource(DetailedCR detailedCR) throws PersistenceException {
        return createComponentResource(detailedCR, null, null);
    }

/**
     * Registers a new component resource in the database.
     *
     * @param detailedCR the detailed component resource to be registered
     * @return the ComponentResourceEntity object if registered, null otherwise
     */
public ComponentResourceEntity registerData(DetailedCR detailedCR, Integer projectId, Integer quantity) throws PersistenceException {
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

    if (projectId != null && quantity != null) {
        addCRToProject(projectId, componentResourceFromDb, quantity);
        logger.info("Relation between component resource and project created");
    }

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

        if (projectEntity == null) {
            logger.error("Project not found");
            return;
        }

        try {
            M2MComponentProject m2MComponentProject = new M2MComponentProject();
            m2MComponentProject.setProject(projectEntity);
            m2MComponentProject.setComponentResource(componentResource);
            m2MComponentProject.setQuantity(quantity);
            projectEntity.addComponentResource(m2MComponentProject);
            componentResource.addProject(m2MComponentProject);
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
     * @param entityCR the detailed component resource to convert
     * @return the preview component resource if converted, null otherwise
     */
    public CRPreview entityToPreviewCR(ComponentResourceEntity entityCR) {
        logger.info("Converting detailed component resource to preview");

        if (entityCR == null) {
            logger.error("Detailed component resource is null");
            return null;
        }

        logger.info("Creating preview");

        CRPreview crPreview = new CRPreview();
        int type = ComponentResourceEnum.fromEnum(entityCR.getType());

        crPreview.setId(entityCR.getId());
        crPreview.setName(entityCR.getName());
        crPreview.setType(type);
        crPreview.setBrand(entityCR.getBrand());
        crPreview.setPartNumber(entityCR.getPartNumber());
        crPreview.setSupplier(entityCR.getSupplier());

        return crPreview;
    }

    /**
     * Edits a component resource based on the detailed component resource passed as parameter.
     *
     * @param detailedCR the detailed component resource to be edited.
     * @param quantity the quantity of the component resource in the project, if it is being edited in a project.
     * @param projectId the id of the project where the component resource will be edited.
     *                  It can be null if the component resource is not being edited in a project.
     * @return the detailed component resource if edited successfully, null otherwise.
     */
    public DetailedCR editComponentResource (DetailedCR detailedCR, Integer quantity, int projectId) {
        logger.info("Editing component resource");

        if (detailedCR == null) {
            logger.error("Component resource is null while editing");
            return null;
        }

        ComponentResourceEntity componentResourceEntity = componentResourceDao.findCREntityById(detailedCR.getId());

        if (componentResourceEntity == null) {
            logger.error("Component resource not found");
            return null;
        }

        try {
            componentResourceEntity = updateData(detailedCR, componentResourceEntity, projectId, quantity);
        } catch (Exception e) {
            logger.error("Error converting detailed component resource to preview: {}", e.getMessage());
            return null;
        }

        try {
            componentResourceDao.merge(componentResourceEntity);
        } catch (PersistenceException e) {
            logger.error("Error updating component resource data in editComponentResource: {}", e.getMessage());
            return null;
        }

        logger.info("Component resource edited successfully");

        DetailedCR updatedCr = entityToDetailedCr(componentResourceEntity);

        if (updatedCr == null) {
            logger.error("Error converting detailed component resource to a detailed component resource");
            return null;
        }

        return updatedCr;
    }

    /**
     * Overloaded method for editing a component resource without associating it with a project.
     *
     * @param detailedCR the detailed component resource to be edited.
     * @return the detailed component resource if edited successfully, null otherwise.
     */
    public DetailedCR editComponentResource (DetailedCR detailedCR) {
        return editComponentResource(detailedCR, null, 0);
    }


        /**
         * Updates the data of a component resource.
         * If the component resource is being updated in a project, the quantity of the component resource in the project is also updated.
         *
         * @param detailedCR the detailed component resource with the new data
         * @param componentResourceEntity the component resource entity to update
         * @param projectId the id of the project where the component resource is being updated
         * @param quantity the quantity of the component resource in the project
         * @return the updated component resource entity if updated, null otherwise
         */
    public ComponentResourceEntity updateData (DetailedCR detailedCR, ComponentResourceEntity componentResourceEntity, int projectId, int quantity) {
        logger.info("Updating data of component resource");

        if (detailedCR == null) {
            logger.error("Component resource is null while updating data");
            return null;
        }

        if (componentResourceEntity == null) {
            logger.error("Component resource entity is null while updating data");
            return null;
        }

        if (detailedCR.getName() != null && !detailedCR.getName().isBlank()) {
            componentResourceEntity.setName(detailedCR.getName());
        }

        if (detailedCR.getDescription() != null && !detailedCR.getDescription().isBlank()) {
            componentResourceEntity.setDescription(detailedCR.getDescription());
        }

        if (detailedCR.getBrand() != null && !detailedCR.getBrand().isBlank()) {
            componentResourceEntity.setBrand(detailedCR.getBrand());
        }

        if (detailedCR.getPartNumber() != null && detailedCR.getPartNumber() > 0) {
            componentResourceEntity.setPartNumber(detailedCR.getPartNumber());
        }

        if (detailedCR.getSupplier() != null && !detailedCR.getSupplier().isBlank()) {
            componentResourceEntity.setSupplier(detailedCR.getSupplier());
        }

        if (detailedCR.getSupplierContact() > 0) {
            componentResourceEntity.setSupplierContact(detailedCR.getSupplierContact());
        }

        if (detailedCR.getObservations() != null && !detailedCR.getObservations().isBlank()) {
            componentResourceEntity.setObservations(detailedCR.getObservations());
        }

        // If the update occurs in a project, update the quantity of the component resource in the project
        if (projectId > 0 && quantity > 0) {
            M2MComponentProject m2MComponentProject;
            try {
                m2MComponentProject = m2MComponentProjectDao.findM2MComponentProjectByComponentIdAndProjectId(projectId, componentResourceEntity.getId());
            } catch (PersistenceException e) {
                logger.error("Error finding relation between component resource and project: {}", e.getMessage());
                return null;
            }

            if (m2MComponentProject == null) {
                logger.error("Relation between component resource and project not found");
                return null;
            }

            m2MComponentProject.setQuantity(quantity);

            try {
                m2MComponentProjectDao.merge(m2MComponentProject);
            } catch (PersistenceException e) {
                logger.error("Error updating relation between component resource and project: {}", e.getMessage());
                return null;
            }
        }

        logger.info("Persisting entity with updated data");

        try {
            componentResourceDao.merge(componentResourceEntity);
        } catch (PersistenceException e) {
            logger.error("Error updating component resource data: {}", e.getMessage());
            return null;
        }

        return componentResourceEntity;
    }

    public DetailedCR entityToDetailedCr(ComponentResourceEntity entityCR) {
        logger.info("Converting detailed component resource to a detailed component resource");

        if (entityCR == null) {
            logger.error("Detailed component resource is null while converting");
            return null;
        }

        logger.info("Creating detailed component resource");

        DetailedCR detailedCR = new DetailedCR();
        int type = ComponentResourceEnum.fromEnum(entityCR.getType());

        detailedCR.setId(entityCR.getId());
        detailedCR.setName(entityCR.getName());
        detailedCR.setType(type);
        detailedCR.setBrand(entityCR.getBrand());
        detailedCR.setPartNumber(entityCR.getPartNumber());
        detailedCR.setSupplier(entityCR.getSupplier());
        detailedCR.setSupplierContact(entityCR.getSupplierContact());
        detailedCR.setObservations(entityCR.getObservations());

        return detailedCR;
    }
}
