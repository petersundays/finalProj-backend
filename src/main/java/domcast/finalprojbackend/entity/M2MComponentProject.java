package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

/**
 * Entity class for the project_component_resources table in the database.
 * Creates a many-to-many relationship between the project and the components and resources.
 * Contains all the attributes of the project_component_resources table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the project-component relationship.
 * - project: the project that uses the component or resource.
 * - componentResource: the component or resource used by the project.
 * - quantity: the quantity of the component or resource used by the project.
 * - active: the status of the project-component relationship.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "project_component_resources")

@NamedQuery(name = "M2MComponentProject.findM2MComponentProjectByProjectIdAndComponentId",
        query = "SELECT m FROM M2MComponentProject m WHERE m.project.id = :projectId AND m.componentResource.id = :componentId")
@NamedQuery(name = "M2MComponentProject.findComponentResourceIdsByProjectId",
        query = "SELECT m.componentResource.id FROM M2MComponentProject m WHERE m.project.id = :projectId")

public class M2MComponentProject implements Serializable {

    private static final long serialVersionUID = 1L;

    // Unique identifier for the project-component relationship
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Project that uses the component or resource
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private ProjectEntity project;

    // Component or resource used by the project
    @ManyToOne
    @JoinColumn(name = "component_resource_id", referencedColumnName = "id")
    private ComponentResourceEntity componentResource;

    // Quantity of the component or resource used by the project
    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Status of the project-component relationship
    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Empty constructor
    public M2MComponentProject() {
    }

    // Constructor with parameters
    public M2MComponentProject(ProjectEntity project, ComponentResourceEntity componentResource, int quantity) {
        this.project = project;
        this.componentResource = componentResource;
        this.quantity = quantity;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public ComponentResourceEntity getComponentResource() {
        return componentResource;
    }

    public void setComponentResource(ComponentResourceEntity componentResource) {
        this.componentResource = componentResource;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
