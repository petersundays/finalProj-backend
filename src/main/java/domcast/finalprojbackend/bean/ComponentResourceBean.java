package domcast.finalprojbackend.bean;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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

        try {
            dataValidator.isCRMandatoryDataValid(detailedCR, projectId, quantity);
        } catch (IllegalArgumentException e) {
            logger.error("Data validation failed: ", e);
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
            logger.error("Error converting detailed component resource to preview while creating component resource");
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
            logger.error("Invalid ids or quantity while creating relation");
            return;
        }

        logger.info("Checking if relation already exists");
        
        M2MComponentProject previousRelation;
        try {
            previousRelation = m2MComponentProjectDao.findM2MComponentProjectByComponentIdAndProjectId(projectId, componentResource.getId());
        } catch (PersistenceException e) {
            logger.error("Error finding relation between component resource and project while creating relation: {}", e.getMessage());
            return;
        }

        // If the relation already exists, updates if the quantity is different,
        // activates if it is inactive or returns if it is active and the quantity is the same
        if (alreadyExists(quantity, previousRelation)) {
            return;
        }

        // If the relation does not exist, create it
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
     * Checks if the relation between a component resource and a project already exists.
     * If the relation already exists, updates the quantity if it is different,
     * activates it if it is inactive or returns if it is active and the quantity is the same.
     *
     * @param quantity the quantity of the component resource
     * @param previousRelation the previous relation between the component resource and the project
     * @return true if the relation already exists, false otherwise
     */
    public boolean alreadyExists(int quantity, M2MComponentProject previousRelation) {

        if (previousRelation != null && previousRelation.isActive() && previousRelation.getQuantity() == quantity) {
            logger.error("Relation already exists with the same quantity of this component resource");
            return true;

        } else if (previousRelation != null && previousRelation.getQuantity() != quantity) {
            logger.info("Updating quantity of relation");
            previousRelation.setQuantity(quantity);
            try {
                m2MComponentProjectDao.merge(previousRelation);
            } catch (PersistenceException e) {
                logger.error("Error updating quantity of relation: {}", e.getMessage());
            }
            return true;

        } else if (previousRelation != null && !previousRelation.isActive()) {
            logger.info("Activating relation");
            previousRelation.setActive(true);
            previousRelation.setQuantity(quantity);

            try {
                m2MComponentProjectDao.merge(previousRelation);
            } catch (PersistenceException e) {
                logger.error("Error activating relation: {}", e.getMessage());
            }
            return true;
        }
        return false;
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
        if (entityCR.getType() != null) {
            int type = ComponentResourceEnum.fromEnum(entityCR.getType());
            crPreview.setType(type);
        }
        crPreview.setId(entityCR.getId());
        crPreview.setName(entityCR.getName());
        crPreview.setBrand(entityCR.getBrand());
        crPreview.setPartNumber(entityCR.getPartNumber());
        crPreview.setSupplier(entityCR.getSupplier());

        return crPreview;
    }

    /**
     * Edits a component resource based on the detailed component resource passed as parameter.
     *
     * @param detailedCR the detailed component resource to be edited.
     * @param id component-resource's id to edit.
     * @param projectId the id of the project where the component resource will be edited.
     *                  It can be null if the component resource is not being edited in a project.
     * @return the detailed component resource if edited successfully, null otherwise.
     */
    public DetailedCR editComponentResource (DetailedCR detailedCR, int id, Integer projectId) {
        logger.info("Editing component resource");

        if (detailedCR == null) {
            logger.error("Component resource is null while editing");
            return null;
        }

        ComponentResourceEntity componentResourceEntity = componentResourceDao.findCREntityById(id);

        if (componentResourceEntity == null) {
            logger.error("Component resource not found");
            return null;
        }

        try {
            componentResourceEntity = updateData(detailedCR, componentResourceEntity, projectId);
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
     * @param id component-resource's id to edit.
     * @return the detailed component resource if edited successfully, null otherwise.
     */
    public DetailedCR editComponentResource (DetailedCR detailedCR, int id) {
        return editComponentResource(detailedCR, id, null);
    }


        /**
         * Updates the data of a component resource.
         * If the component resource is being updated in a project, the quantity of the component resource in the project is also updated.
         *
         * @param detailedCR the detailed component resource with the new data
         * @param componentResourceEntity the component resource entity to update
         * @param projectId the id of the project where the component resource is being updated
         * @return the updated component resource entity if updated, null otherwise
         */
    public ComponentResourceEntity updateData (DetailedCR detailedCR, ComponentResourceEntity componentResourceEntity, Integer projectId) {
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
        if (projectId != null && detailedCR.getQuantity() > 0) {
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

            m2MComponentProject.setQuantity(detailedCR.getQuantity());

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
        if (entityCR.getType() != null) {
            int type = ComponentResourceEnum.fromEnum(entityCR.getType());
            detailedCR.setType(type);
        }
        detailedCR.setId(entityCR.getId());
        detailedCR.setName(entityCR.getName());
        detailedCR.setBrand(entityCR.getBrand());
        detailedCR.setPartNumber(entityCR.getPartNumber());
        detailedCR.setSupplier(entityCR.getSupplier());
        detailedCR.setSupplierContact(entityCR.getSupplierContact());
        detailedCR.setObservations(entityCR.getObservations());

        return detailedCR;
    }

    /**
     * Inactivates a relation between a component resource and a project.
     * This is used to remove a component resource from a project's Bill of Materials.
     *
     * @param projectId the project id to inactivate the relation with
     * @param componentId the component resource id to inactivate the relation with
     * @return true if the relation was inactivated successfully, false otherwise
     */
    public boolean inactivateRelation(int projectId, int componentId) {
        logger.info("Inactivating relation between component resource and project");

        if (!dataValidator.isIdValid(projectId) || !dataValidator.isIdValid(componentId)) {
            logger.error("Invalid ids while inactivating relation");
            return false;
        }

        M2MComponentProject m2MComponentProject;
        try {
            m2MComponentProject = m2MComponentProjectDao.findM2MComponentProjectByComponentIdAndProjectId(projectId, componentId);
        } catch (PersistenceException e) {
            logger.error("Error finding relation between component resource and project: {}", e.getMessage());
            return false;
        }

        if (m2MComponentProject == null) {
            logger.error("Relation between component resource and project not found");
            return false;
        }

        m2MComponentProject.setActive(false);

        try {
            m2MComponentProjectDao.merge(m2MComponentProject);
        } catch (PersistenceException e) {
            logger.error("Error inactivating relation between component resource and project: {}", e.getMessage());
            return false;
        }

        logger.info("Relation inactivated successfully");

        return true;
    }

    /**
     * Gets the component resources by project id.
     * The method validates the data,
     * gets the component resources from the database and returns the preview component resources.
     *
     * @param projectId the id of the project to get the component resources from
     * @return the set of preview component resources if found, null otherwise
     */
    public Set<CRPreview> getComponentResourcesByProjectId(int projectId) {
        logger.info("Getting component resources by project id");

        if (!dataValidator.isIdValid(projectId)) {
            logger.error("Invalid id while getting component resources by project id");
            return null;
        }

        Set<Integer> componentResourceIds;
        try {
            componentResourceIds = m2MComponentProjectDao.findComponentResourceIdsByProjectId(projectId);
        } catch (PersistenceException e) {
            logger.error("Error finding component resource ids by project id: {}", e.getMessage());
            return null;
        }

        if (componentResourceIds == null) {
            logger.error("Component resource ids not found");
            return null;
        }

        Set<CRPreview> crPreviews = new HashSet<>();

        // For each component resource id, convert the detailed component resource to a preview component resource
        for (Integer componentResourceId : componentResourceIds) {
            ComponentResourceEntity componentResourceEntity;
            try {
                componentResourceEntity = componentResourceDao.findCREntityById(componentResourceId);
            } catch (PersistenceException e) {
                logger.error("Error finding component resource entity by id: {}", e.getMessage());
                return null;
            }

            if (componentResourceEntity == null) {
                logger.error("Component resource entity not found");
                return null;
            }

            CRPreview crPreview = entityToPreviewCR(componentResourceEntity);

            if (crPreview == null) {
                logger.error("Error converting detailed component resource to preview");
                return null;
            }

            crPreviews.add(crPreview);
        }

        return crPreviews;
    }

    /**
     * Gets the component resources by criteria.
     * The method validates the data,
     * gets the component resources from the database and returns the preview component resources.
     *
     * @param name the name of the component resource
     * @param brand the brand of the component resource
     * @param partNumber the part number of the component resource
     * @param supplier the supplier of the component resource
     * @param orderBy the field to order the component resources by
     * @param orderAsc the order of the component resources
     * @param pageNumber the page number to get the component resources from
     * @param pageSize the page size to get the component resources from
     * @return the list of preview component resources if found, empty list otherwise
     */
    public List<CRPreview> getComponentResourcesByCriteria(String name, String brand, long partNumber, String supplier, String orderBy, boolean orderAsc, int pageNumber, int pageSize) {
        logger.info("Getting component resources by criteria: name={}, brand={}, partNumber={}, supplier={}, orderBy={}, orderAsc={}, pageNumber={}, pageSize={}", name, brand, partNumber, supplier, orderBy, orderAsc, pageNumber, pageSize);

        // Validate orderBy field
        List<String> allowedOrderByFields = Arrays.asList("name", "brand", "partNumber", "supplier");
        if (orderBy != null && !orderBy.isEmpty() && !allowedOrderByFields.contains(orderBy)) {
            logger.error("Invalid orderBy field");
            return Collections.emptyList();
        }

        if (!dataValidator.isPageNumberValid(pageNumber)) {
            logger.error("Invalid page number: {}", pageNumber);
            return Collections.emptyList();
        }
        if (!dataValidator.isPageSizeValid(pageSize)) {
            logger.error("Invalid page size: {}", pageSize);
            return Collections.emptyList();
        }

        // Get the component resources from the database
        List<ComponentResourceEntity> componentResourceEntities;
        try {
            componentResourceEntities = componentResourceDao.getComponentResourcesByCriteria(name, brand, partNumber, supplier, orderBy, orderAsc, pageNumber, pageSize);
        } catch (PersistenceException e) {
            logger.error("Error getting component resources by criteria: {}", e.getMessage());
            return Collections.emptyList();
        }

        if (componentResourceEntities == null || componentResourceEntities.isEmpty()) {
            logger.info("No component resources found for the given criteria");
            return Collections.emptyList();
        }

        // Convert the detailed component resources to preview component resources
        List<CRPreview> crPreviews;
        try {
            crPreviews = componentResourceEntities.stream()
                    .map(this::entityToPreviewCR)
                    .toList();
        } catch (Exception e) {
            logger.error("Error converting detailed component resource list to preview list: {}", e.getMessage());
            return Collections.emptyList();
        }

        return crPreviews;
    }

    /**
     * Finds the component resource entity by name and brand.
     *
     * @param detailedCRs the detailed component resources to find the entity by name and brand
     * @return the set of component resource ids if found, null otherwise
     */
    public Set<Integer> findCREntityByNameAndBrand (Set<DetailedCR> detailedCRs) {
        logger.info("Finding component resource entity by name and brand");

        if (detailedCRs == null) {
            logger.error("Detailed component resources is null");
            return null;
        }

        Set<Integer> componentResourceIds = new HashSet<>();

        for (DetailedCR detailedCR : detailedCRs) {
            if (detailedCR == null) {
                logger.error("Detailed component resource is null while finding component resource entity by name and brand");
                return null;
            }

            ComponentResourceEntity componentResourceEntity;
            try {
                componentResourceEntity = componentResourceDao.findCREntityByNameAndBrand(detailedCR.getName(), detailedCR.getBrand());
            } catch (PersistenceException e) {
                logger.error("Error finding component resource entity by name and brand: {}", e.getMessage());
                return null;
            }

            if (componentResourceEntity == null) {
                logger.error("Component resource entity not found for name: {} and brand: {}", detailedCR.getName(), detailedCR.getBrand());
                return null;
            }

            componentResourceIds.add(componentResourceEntity.getId());
        }

        return componentResourceIds;
    }

    public Map<Integer, Integer> findEntityAndSetQuantity (Set<DetailedCR> cRDtos) {
        logger.info("Finding component resource entity by name and brand and setting quantity");

        if (cRDtos == null) {
            logger.error("Detailed component resources is null while finding component resource entity by name and brand");
            return null;
        }

        Map<Integer, Integer> componentResources = new HashMap<>();

        for (DetailedCR detailedCR : cRDtos) {

            if (detailedCR == null || detailedCR.getQuantity() <= 0) {
                logger.error("Detailed component resource or quantity is null or invalid while finding component resource entity by name and brand");
                return null;
            }

            ComponentResourceEntity componentResourceEntity;
            int quantity = detailedCR.getQuantity();

            try {
                componentResourceEntity = componentResourceDao.findCREntityByNameAndBrand(detailedCR.getName(), detailedCR.getBrand());
            } catch (PersistenceException e) {
                logger.error("Error finding component resource entity by name and brand and setting quantity: {}", e.getMessage());
                return null;
            }

            if (componentResourceEntity == null) {
                logger.error("Component resource entity not found for name: {} and brand {}", detailedCR.getName(), detailedCR.getBrand());
                return null;
            }

            componentResources.put(componentResourceEntity.getId(), quantity);
        }

        return componentResources;
    }

    /**
     * Creates many-to-many relations between component resources and project, while creating a project.
     *
     * @param componentResources the component resources to create the relations with
     * @param projectEntity the project entity to create the relations with
     * @return the set of many-to-many relations, which are the component resources in the project, or an empty set if there are no relations created
    */
    public Set<M2MComponentProject> relationInProjectCreation (Map<Integer, Integer> componentResources, ProjectEntity projectEntity) {
        logger.info("Creating many-to-many relations between component resources and project");

        Set<M2MComponentProject> m2MComponentProjects = new HashSet<>();

        if (componentResources == null || componentResources.isEmpty()) {
            logger.error("Component resources is null or empty while creating relations");
            return m2MComponentProjects;
        }

        if (projectEntity == null) {
            logger.error("Project entity is null while creating relations");
            throw new IllegalArgumentException("Project entity is null while creating relations");
        }

        for (Map.Entry<Integer, Integer> entry : componentResources.entrySet()) {
            ComponentResourceEntity componentResource;
            Integer quantity = entry.getValue();
            try {
                if (dataValidator.isIdValid(entry.getKey())) {
                    try {
                        componentResourceDao.clear();
                        componentResource = componentResourceDao.findCREntityById(entry.getKey());
                    } catch (PersistenceException e) {
                        logger.error("Error finding component resource entity by id while creating relation: {}", e.getMessage());
                        throw new RuntimeException("Error finding component resource entity by id", e);
                    }

                    if (componentResource == null || quantity == null || quantity <= 0) {
                        logger.error("Component resource or quantity is null or invalid while creating relation");
                        continue;
                    }

                    M2MComponentProject m2MComponentProject = new M2MComponentProject();
                    m2MComponentProject.setProject(projectEntity);
                    m2MComponentProject.setComponentResource(componentResource);
                    m2MComponentProject.setQuantity(quantity);
                    projectEntity.addComponentResource(m2MComponentProject);
                    componentResource.addProject(m2MComponentProject);
                    m2MComponentProjects.add(m2MComponentProject);
/*
                    // Persist the M2MComponentProject object in the database
                    m2MComponentProjectDao.persist(m2MComponentProject);*/
                }
            } catch (PersistenceException e) {
                logger.error("Error creating relation between component resource and project: {}", e.getMessage());
                return null;
            }
        }

        return m2MComponentProjects;
    }

    public Set<CRPreview> componentProjectToCRPreview(Set<M2MComponentProject> m2MComponentProjects) {
        logger.info("Converting many-to-many relations between component resources and project to preview component resources");

        if (m2MComponentProjects == null) {
            logger.error("Many-to-many relations between component resources and project is null while converting");
            return null;
        }

        Set<CRPreview> crPreviews = new HashSet<>();

        for (M2MComponentProject m2MComponentProject : m2MComponentProjects) {
            if (m2MComponentProject == null) {
                logger.error("Null M2MComponentProject object found");
                continue;
            }

            ComponentResourceEntity componentResourceEntity = m2MComponentProject.getComponentResource();

            if (componentResourceEntity == null) {
                logger.error("Component resource entity is null while converting");
                continue;
            }

            CRPreview crPreview = entityToPreviewCR(componentResourceEntity);

            if (crPreview == null) {
                logger.error("Error converting detailed component resource to preview while converting many-to-many relations");
                continue;
            }

            crPreviews.add(crPreview);
        }

        return crPreviews;
    }

    /**
     * Extracts the detailed component resources from the input.
     *
     * @param input the input to extract the detailed component resources from
     * @return the map of detailed component resources if extracted, null otherwise
     */
    public Set<DetailedCR> extractCRDtos(MultipartFormDataInput input) throws IOException {
        InputPart part = input.getFormDataMap().get("components").get(0);
        String cRDtosString = part.getBodyAsString();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(cRDtosString, new TypeReference<Set<DetailedCR>>() {});
    }

    public Set<M2MComponentProject> updateRelationshipToProject(Map<Integer, Integer> componentResources, ProjectEntity projectEntity) {
        logger.info("Updating many-to-many relations between component resources and project");

        Set<M2MComponentProject> newRelations = new HashSet<>();
        Set<M2MComponentProject> oldRelations;

        if (componentResources == null || componentResources.isEmpty()) {
            logger.error("Component resources is null or empty while updating relations");
            return newRelations;
        }

        if (projectEntity == null) {
            logger.error("Project entity is null while updating relations");
            throw new IllegalArgumentException("Project entity is null while updating relations");
        }

        try {
            oldRelations = m2MComponentProjectDao.findAllByProjectId(projectEntity.getId());
        } catch (Exception e) {
            logger.error("Error finding all many-to-many relations by project id while updating relation: {}", e.getMessage());
            throw new RuntimeException("Error finding all many-to-many relations by project id", e);
        }

        for (Map.Entry<Integer, Integer> entry : componentResources.entrySet()) {
            ComponentResourceEntity componentResource;
            Integer quantity = entry.getValue();

            try {
                if (dataValidator.isIdValid(entry.getKey())) {
                    componentResource = componentResourceDao.findCREntityById(entry.getKey());
                } else {
                    throw new RuntimeException("Invalid component resource id");
                }
            } catch (Exception e) {
                logger.error("Error finding component resource entity by id while updating relation: {}", e.getMessage());
                throw new RuntimeException("Error finding component resource entity by id", e);
            }

            if (componentResource == null || quantity == null || quantity <= 0) {
                logger.error("Component resource or quantity is null or invalid while updating relation");
                continue;
            }

            if (oldRelations != null && !oldRelations.isEmpty()) {
                M2MComponentProject existingRelation = oldRelations.stream()
                        .filter(i -> i.getComponentResource().equals(componentResource))
                        .findFirst()
                        .orElse(null);

                if (existingRelation != null) {
                    if (!existingRelation.isActive() || existingRelation.getQuantity() != (quantity)) {
                        try {
                            m2MComponentProjectDao.setActiveByComponentIdAndProjectId(componentResource.getId(), projectEntity.getId());
                            existingRelation.setQuantity(quantity);
                            newRelations.add(existingRelation);
                        } catch (Exception e) {
                            logger.error("Error while setting quantity for project: {}", e.getMessage());
                        }
                    }
                } else {
                    M2MComponentProject newRelation = new M2MComponentProject();
                    newRelation.setProject(projectEntity);
                    newRelation.setComponentResource(componentResource);
                    newRelation.setQuantity(quantity);

                    try {
                        m2MComponentProjectDao.persist(newRelation);
                    } catch (Exception e) {
                        logger.error("Error creating new relation between component resource {} and project {}: {}", componentResource.getId(), projectEntity.getId(), e.getMessage());
                        continue;
                    }

                    newRelations.add(newRelation);
                }
            }
        }

        if (oldRelations != null && !oldRelations.isEmpty()) {
            for (M2MComponentProject oldRelation : oldRelations) {
                if (componentResources.keySet().stream().noneMatch(i -> i.equals(oldRelation.getComponentResource().getId()))) {
                    try {
                        m2MComponentProjectDao.setInactiveByComponentIdAndProjectId(oldRelation.getComponentResource().getId(), projectEntity.getId());
                    } catch (Exception e) {
                        logger.error("Error while setting component resource inactive for project: {}", e.getMessage());
                    }
                }
            }
        }

        return newRelations;
    }
}
