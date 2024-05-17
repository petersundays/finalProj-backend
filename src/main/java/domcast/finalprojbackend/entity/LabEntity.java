package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "lab")

public class LabEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "city", nullable = false, unique = true)
    private String city;


}
