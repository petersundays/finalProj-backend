package domcast.finalprojbackend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "nickname", unique = true)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "workplace", referencedColumnName = "id")
    private LabEntity workplace;

    @Column(name = "photo")
    private String photo;

    @Column(name = "biography")
    private String biography;

    @Column(name = "visible", nullable = false)
    private boolean visible;

    @Column(name = "admin", nullable = false)
    private int type;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "session_token_id", referencedColumnName = "id")
    private SessionTokenEntity sessionToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "validation_token_id", referencedColumnName = "id")
    private ValidationTokenEntity validationToken;

    @OneToMany(mappedBy = "sender")
    private Set<MessageEntity> sentMessages = new HashSet<>();

    @OneToMany(mappedBy = "receiver")
    private Set<PersonalMessageEntity> receivedMessages = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_interest",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<InterestEntity> interests = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_skill",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<SkillEntity> skills = new HashSet<>();


    public UserEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public LabEntity getWorkplace() {
        return workplace;
    }

    public void setWorkplace(LabEntity workplace) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SessionTokenEntity getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(SessionTokenEntity sessionToken) {
        this.sessionToken = sessionToken;
    }

    public ValidationTokenEntity getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(ValidationTokenEntity validationToken) {
        this.validationToken = validationToken;
    }

    public Set<MessageEntity> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(Set<MessageEntity> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public Set<PersonalMessageEntity> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(Set<PersonalMessageEntity> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public Set<InterestEntity> getInterests() {
        return interests;
    }

    public void setInterests(Set<InterestEntity> interests) {
        this.interests = interests;
    }

    public Set<SkillEntity> getSkills() {
        return skills;
    }

    public void setSkills(Set<SkillEntity> skills) {
        this.skills = skills;
    }

}

