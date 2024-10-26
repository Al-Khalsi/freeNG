package com.imalchemy.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity<UUID> {

    @Id
    private UUID id;

    /**
     * Overrides the default method to provide a clearer name.
     *
     * @return the UUID of the user
     */
    @Override
    @JsonProperty("id")
    public UUID getId() {
        return id;
    }

    /**
     * Method to handle operations before persisting a new User entity.
     */
    @PrePersist
    public void onCreate() {
        // Generate a new UUID if the id is not already set
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    private String username;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @CreatedDate
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    private int loginAttempts;


    // -------------------- Relationships --------------------
    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles = new HashSet<>();


    // -------------------- Methods --------------------
    public void addRole(Roles role) {
        // Add the role to the user's roles
        roles.add(role);
        // Add the user to the role's users
        role.getUsers().add(this);
    }

    /**
     * Remove a role from the user.
     * <p>
     * This method will remove the role from the user's roles and also remove
     * the user from the role's users.
     *
     * @param role the role to remove
     */
    public void removeRole(Roles role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }

}
