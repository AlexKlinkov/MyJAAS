package ru.inner.project.MyJAAS.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_role")
@IdClass(UserRoleKey.class)
public class UserRole {
    @Id
    private Long user_id;
    @Id
    private String role;

    public UserRoleKey getId() {
        return new UserRoleKey(user_id, role);
    }
    public void setId(UserRoleKey id) {
        this.user_id = id.getUser_id();
        this.role = id.getRole();
    }
}
