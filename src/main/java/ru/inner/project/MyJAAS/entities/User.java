package ru.inner.project.MyJAAS.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "username")
    private String username;
    @Column(name = "age")
    private Integer age;
    @Column(name = "email")
    private String email;
}
