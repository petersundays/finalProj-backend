package domcast.finalprojbackend.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "user")


public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "workplace", nullable = false)
    private String workplace;

    @Column(name = "photo")
    private String photo;

    @Column(name = "biography")
    private String biography;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "session_token_id", referencedColumnName = "id")
    private SessionToken sessionToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "validation_token_id", referencedColumnName = "id")
    private ValidationToken validationToken;

    public UserEntity() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public SessionToken getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(SessionToken sessionToken) {
        this.sessionToken = sessionToken;
    }

    public ValidationToken getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(ValidationToken validationToken) {
        this.validationToken = validationToken;
    }
}

