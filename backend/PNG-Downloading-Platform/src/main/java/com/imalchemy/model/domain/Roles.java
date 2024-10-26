package com.imalchemy.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Roles extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    private String roleName;

    // -------------------- Relationships --------------------
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    // -------------------- Methods --------------------

    /**
     * Add a user to the role. This method will add the user to the role's users
     * and also add the role to the user's roles.
     *
     * @param user the user to add
     */
    public void addUser(User user) {
        users.add(user);
        user.getRoles().add(this);
    }

    /**
     * Remove a user from the role. This method will remove the user from the
     * role's users and also remove the role from the user's roles.
     *
     * @param user the user to remove
     */
    public void removeUser(User user) {
        users.remove(user);
        user.getRoles().remove(this);
    }

}
