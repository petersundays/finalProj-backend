package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "component_resource")

public class ComponentResourceEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "part_number", nullable = false)
    private Long partNumber;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "supplier", nullable = false)
    private String supplier;

    @Column(name = "supplier_contact", nullable = false)
    private long supplierContact;

    @Column(name = "observations", nullable = false)
    private String observations;

    @ManyToMany(mappedBy = "componentResources")
    private Set<ProjectEntity> projects = new HashSet<>();


    public ComponentResourceEntity() {
    }

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
