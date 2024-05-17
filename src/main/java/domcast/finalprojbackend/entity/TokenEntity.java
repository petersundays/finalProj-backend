package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@MappedSuperclass
public abstract class TokenEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    protected int id;

    @Column(name = "token")
    protected String token;

    @Column(name = "email")
    protected String email;

    public TokenEntity() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}