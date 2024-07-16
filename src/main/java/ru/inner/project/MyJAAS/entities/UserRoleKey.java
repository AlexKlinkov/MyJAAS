package ru.inner.project.MyJAAS.entities;

import jakarta.persistence.IdClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@IdClass(UserRoleKey.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleKey implements Serializable {

    private Long user_id;
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleKey that = (UserRoleKey) o;
        return Objects.equals(user_id, that.user_id) &&
                Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, role);
    }
}
