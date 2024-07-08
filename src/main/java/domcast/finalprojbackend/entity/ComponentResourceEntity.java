package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.ComponentResourceEnum;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the component_resource table in the database.
 * Contains all the attributes of the component_resource table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the component resource.
 * - type: the type of the component resource.
 * - name: the name of the component resource.
 * - description: the description of the component resource.
 * - brand: the brand of the component resource.
 * - partNumber: the part number of the component resource.
 * - quantity: the quantity of the component resource.
 * - supplier: the supplier of the component resource.
 * - supplierContact: the supplier contact of the component resource.
 * - observations: the observations of the component resource.
 * - projects: the projects that use the component resource.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "component_resource")

@NamedQuery(name = "ComponentResource.doesCRExistByNameAndBrand",
        query = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM ComponentResourceEntity c WHERE c.name = :name AND c.brand = :brand")
@NamedQuery(name = "ComponentResource.findCREntityByNameAndBrand",
        query = "SELECT c FROM ComponentResourceEntity c WHERE c.name = :name AND c.brand = :brand")
@NamedQuery(name = "ComponentResource.findCREntityById",
        query = "SELECT c FROM ComponentResourceEntity c WHERE c.id = :id")
@NamedQuery(name = "ComponentResource.findM2MComponentProjectByProjectIdAndComponentId",
        query = "SELECT m FROM M2MComponentProject m WHERE m.project.id = :projectId AND m.componentResource.id = :componentId")

public class ComponentResourceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the component resource
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Type of the object (component or resource)
    @Column(name = "type", nullable = false)
    private ComponentResourceEnum type;

    // Name of the component or resource
    @Column(name = "name", nullable = false)
    private String name;

    // Description of the component or resource
    @Column(name = "description", nullable = false)
    private String description;

    // Brand of the component or resource
    @Column(name = "brand", nullable = false)
    private String brand;

    // Part number of the component or resource
    @Column(name = "part_number", nullable = false)
    private Long partNumber;

    // Supplier of the component or resource
    @Column(name = "supplier", nullable = false)
    private String supplier;

    // Supplier contact of the component or resource
    @Column(name = "supplier_contact", nullable = false)
    private String supplierContact;

    // Observations of the component or resource
    @Column(name = "observations")
    private String observations;

    // Projects that use the component or resource
    @OneToMany(mappedBy = "componentResource", fetch = FetchType.LAZY)
    private Set<M2MComponentProject> projects = new HashSet<>();

    // Default constructor
    public ComponentResourceEntity() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ComponentResourceEnum getType() {
        return type;
    }

    public void setType(ComponentResourceEnum type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Long partNumber) {
        this.partNumber = partNumber;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSupplierContact() {
        return supplierContact;
    }

    public void setSupplierContact(String supplierContact) {
        this.supplierContact = supplierContact;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Set<M2MComponentProject> getProjects() {
        return projects;
    }

    public void setProjects(Set<M2MComponentProject> projects) {
        this.projects = projects;
    }

    public void addProject(M2MComponentProject project) {
        this.projects.add(project);
    }
}
