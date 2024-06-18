package domcast.finalprojbackend.entity;

import domcast.finalprojbackend.enums.TypeOfUserEnum;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * SystemEntity class represents a system in the application.
 * It contains information about the system such as timeout and maximum users that can be part of a project.
 */
@Entity
@Table(name = "system_variables")

@NamedQuery(name = "System.numberOfSystemVariables", query = "SELECT COUNT(s) FROM SystemEntity s")
@NamedQuery(name = "System.getSessionTimeout", query = "SELECT s.sessionTimeout FROM SystemEntity s")
@NamedQuery(name = "System.getProjectMaxMembers", query = "SELECT s.maxMembers FROM SystemEntity s")
@NamedQuery(name = "System.updateTimeout", query = "UPDATE SystemEntity s SET s.sessionTimeout = :timeout")
@NamedQuery(name = "System.updateMaxMembers", query = "UPDATE SystemEntity s SET s.maxMembers = :maxMembers")

public class SystemEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the system
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    // Timeout for a session
    @Column(name = "session_timeout", nullable = false, updatable = true)
    private int sessionTimeout;

    // Maximum number of users that can be part of a project
    @Column(name = "max_users", nullable = false, updatable = true)
    private int maxMembers;

    /**
     * Default constructor for SystemEntity.
     */
    public SystemEntity() {
    }

    /**
     * Gets the id of the system variable.
     * @return the id of the system variable.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the system variable.
     * @param id the id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the timeout for a session.
     * @return the timeout for a session.
     */
    public int getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * Sets the timeout for a session.
     * Only admins can change the timeout.
     * @param typeOfUser the type of the user trying to set the timeout.
     * @param timeout the timeout to set.
     * @throws IllegalArgumentException if the user is not an admin.
     */
    public void setSessionTimeout(TypeOfUserEnum typeOfUser, int timeout) {
        if (typeOfUser == TypeOfUserEnum.ADMIN) {
            this.sessionTimeout = timeout;
        } else {
            throw new IllegalArgumentException("Only admins can change the timeout");
        }
    }

    /**
     * Gets the maximum number of users that can be part of a project.
     * @return the maximum number of users that can be part of a project.
     */
    public int getMaxMembers() {
        return maxMembers;
    }

    /**
     * Sets the maximum number of users that can be part of a project.
     * Only admins can change the maximum number of users that can be part of a project.
     * @param typeOfUser the type of the user trying to set the maximum number of users.
     *                   Only admins can change the maximum number of users that can be part of a project.
     * @param maxUsers the maximum number of users to set.
     * @throws IllegalArgumentException if the user is not an admin.
     */
    public void setMaxUsers(TypeOfUserEnum typeOfUser, int maxUsers) {
        if (typeOfUser == TypeOfUserEnum.ADMIN) {
            this.maxMembers = maxUsers;
        } else {
            throw new IllegalArgumentException("Only admins can change the maximum number of users that can be part of a project");
        }
    }
}