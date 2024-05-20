package domcast.finalprojbackend.entity;

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
 */

@Entity
@Table(name = "component_resource")

public class ComponentResourceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the component resource
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Type of the object (component or resource)
    @Column(name = "type", nullable = false)
    private String type;

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

    // Quantity of the component or resource
    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Supplier of the component or resource
    @Column(name = "supplier", nullable = false)
    private String supplier;

    // Supplier contact of the component or resource
    @Column(name = "supplier_contact", nullable = false)
    private long supplierContact;

    // Observations of the component or resource
    @Column(name = "observations", nullable = false)
    private String observations;

    // Projects that use the component resource
    @ManyToMany(mappedBy = "componentResources")
    private Set<ProjectEntity> projects = new HashSet<>();


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public long getSupplierContact() {
        return supplierContact;
    }

    public void setSupplierContact(long supplierContact) {
        this.supplierContact = supplierContact;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Set<ProjectEntity> getProjects() {
        return projects;
    }

    public void setProjects(Set<ProjectEntity> projects) {
        this.projects = projects;
    }
}
