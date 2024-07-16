package ru.inner.project.MyJAAS.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "initial_urls")
public class InitialUrl {
    @Id
    @Column(name = "user_ip")
    private String userIp;
    @Column(name = "url")
    private String url;
    @Column(name = "user_login")
    private String userLogin;
    @Column(name = "appeal_method")
    private String appealMethod;
}
