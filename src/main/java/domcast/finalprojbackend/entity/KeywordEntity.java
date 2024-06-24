package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "keyword")

@NamedQuery(name = "Keyword.findKeywordByName", query = "SELECT k FROM KeywordEntity k WHERE k.name = :name")
@NamedQuery(name = "Keyword.findAllKeywordsNames", query = "SELECT k.name FROM KeywordEntity k ORDER BY k.name ASC")
@NamedQuery(name = "Keyword.findKewyordById", query = "SELECT k FROM KeywordEntity k WHERE k.id = :id")


public class KeywordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // Unique identifier for the keyword
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Name of the keyword
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "keyword",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MKeyword> projects = new HashSet<>();

    // Empty constructor
    public KeywordEntity() {
    }

    // Getters and setters


    public Set<M2MKeyword> getProjects() {
        return projects;
    }

    public void setProjects(Set<M2MKeyword> projects) {
        this.projects = projects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
