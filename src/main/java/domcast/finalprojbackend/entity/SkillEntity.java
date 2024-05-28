package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.SkillTypeEnum;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity class for the skill table in the database.
 * Contains all the attributes of the skill table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the skill.
 * - name: the name of the skill.
 * - type: the type of the skill.
 * - users: the users that have the skill.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "skill")

@NamedQuery(name = "Skill.findSkillByName", query = "SELECT s FROM SkillEntity s WHERE s.name = :name")
@NamedQuery(name = "Skill.findSkillsByListOfNames", query = "SELECT s FROM SkillEntity s WHERE s.name IN :names")

public class SkillEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the skill
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Name of the skill
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Type of the skill
    @Column(name = "type", nullable = false)
    private SkillTypeEnum type;

    // Users that have the skill
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MUserSkill> userSkills = new HashSet<>();

    // Projects that require the skill
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MProjectSkill> projectSkills = new HashSet<>();

    // Default constructor
    public SkillEntity() {
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SkillTypeEnum getType() {
        return type;
    }

    public void setType(SkillTypeEnum type) {
        this.type = type;
    }

    public Set<M2MUserSkill> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(Set<M2MUserSkill> userSkills) {
        this.userSkills = userSkills;
    }

    public void addUserSkill(M2MUserSkill userSkill) {
        this.userSkills.add(userSkill);
    }

    public Set<M2MProjectSkill> getProjectSkills() {
        return projectSkills;
    }

    public void setProjectSkills(Set<M2MProjectSkill> projectSkills) {
        this.projectSkills = projectSkills;
    }
}

