package ru.inner.project.MyJAAS.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.inner.project.MyJAAS.entities.UserRole;
import ru.inner.project.MyJAAS.entities.UserRoleKey;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey> {
    @Query(value = "SELECT * FROM user_role WHERE user_role.user_id = :userId", nativeQuery = true)
    List<UserRole> getUserRolesByUserId(Long userId);
}
