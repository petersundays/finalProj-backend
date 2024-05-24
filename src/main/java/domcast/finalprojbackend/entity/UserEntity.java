package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


/**
 * Entity class for the user table in the database.
 * Contains all the attributes of the user table and their getters and setters.
 * The attributes are the following:
 * - id: the id of the user.
 * - email: the email of the user.
 * - password: the password of the user.
 * - firstName: the first name of the user.
 * - lastName: the last name of the user.
 * - nickname: the nickname of the user.
 * - workplace: the workplace of the user.
 * - photo: the photo of the user.
 * - biography: the biography of the user.
 * - visible: the visibility of the user.
 * - type: the type of the user.
 * - sessionToken: the session token of the user.
 * - validationToken: the validation token of the user.
 * - sentMessages: the messages sent by the user.
 * - receivedMessages: the messages received by the user.
 * - interests: the interests of the user.
 * - skills: the skills of the user.
 * The class also contains the necessary annotations to work with the database.
 * @author José Castro
 * @author Pedro Domingos
 */

@Entity
@Table(name = "user")

public class UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the user
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Email of the user
    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String email;

    // Password of the user
    @Column(name = "password", nullable = false)
    private String password;

    // First name of the user
    @Column(name = "first_name")
    private String firstName;

    // Last name of the user
    @Column(name = "last_name")
    private String lastName;

    // Nickname of the user
    @Column(name = "nickname", unique = true)
    private String nickname;

    // Reference to the location of the photo of the user
    @Column(name = "photo")
    private String photo;

    // Biography of the user
    @Column(name = "biography")
    private String biography;

    // Visibility of the user
    @Column(name = "visible", nullable = false)
    private boolean visible;

    // The type is an enum
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "type", nullable = false)
    private TypeOfUserEnum type = TypeOfUserEnum.NOT_CONFIRMED;

    // The workplace is a foreign key to the lab table
    @ManyToOne
    @JoinColumn(name = "workplace", referencedColumnName = "id")
    private LabEntity workplace;

    // The session token is a foreign key to the session_token table
    // It uses CascadeType.ALL to cascade all the operations as the session token is a child of TokenEntity
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SessionTokenEntity> sessionTokens = new HashSet<>();

    // Validation tokens associated with the user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ValidationTokenEntity> validationTokens = new HashSet<>();

    // The sent messages are a set of messages sent by the user
    @OneToMany(mappedBy = "sender")
    private Set<MessageEntity> sentMessages = new HashSet<>();

    // The received messages are a set of messages received by the user
    @OneToMany(mappedBy = "receiver")
    private Set<PersonalMessageEntity> receivedMessages = new HashSet<>();

    // The interests are a set of interests of the user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MUserInterest> interests = new HashSet<>();

    // The skills are a set of skills of the user
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MUserSkill> userSkills = new HashSet<>();

    // The projects in which the user is involved
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<M2MProjectUser> projectUsers = new HashSet<>();

    // Validation method to check if the user is confirmed
    // If the user is confirmed, the first name, last name, and workplace must not be null
    // If the user is not confirmed, the first name, last name, and workplace can be null
    // The method is called before the user is persisted or updated
    // This is needed so a user can be registered only with an email and password and then complete the profile later
    @PrePersist
    @PreUpdate
    public void validate() {
        if (type != TypeOfUserEnum.NOT_CONFIRMED) {
            if (firstName == null || lastName == null || workplace == null) {
                throw new IllegalArgumentException("First name, last name, and workplace must not be null for confirmed users");
            }
        }
    }

    // Default constructor
    public UserEntity() {
    }

    // Getters and setters

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

    public TypeOfUserEnum getType() {
        return type;
    }

    public void setType(TypeOfUserEnum type) {
        this.type = type;
    }

    public Set<SessionTokenEntity> getSessionTokens() {
        return sessionTokens;
    }

    public void setSessionTokens(Set<SessionTokenEntity> sessionTokens) {
        this.sessionTokens = sessionTokens;
    }

    public Set<ValidationTokenEntity> getValidationTokens() {
        return validationTokens;
    }

    public void setValidationTokens(Set<ValidationTokenEntity> validationTokens) {
        this.validationTokens = validationTokens;
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

    public Set<M2MUserInterest> getInterests() {
        return interests;
    }

    public void setInterests(Set<M2MUserInterest> interests) {
        this.interests = interests;
    }

    public Set<M2MUserSkill> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(Set<M2MUserSkill> userSkills) {
        this.userSkills = userSkills;
    }

    public Set<M2MProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Set<M2MProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }
}

