package ru.inner.project.MyJAAS;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.inner.project.MyJAAS.utils.CustomInitialization;


@SpringBootApplication
public class MyJAAS {

    @Value("${spring.variable_admins_for_add_in_BD.username.admin_1}")
    @Getter
    private String adminName;

    @Value("${spring.variable_admins_for_add_in_BD.email.admin_1}")
    @Getter
    private String adminEmail;

    @Value("${spring.variable_admins_for_add_in_BD.age.admin_1}")
    @Getter
    private Integer adminAge;

    @Value("${spring.variable_admins_for_add_in_BD.login.admin_1}")
    @Getter
    private String adminLogin;

    @Value("${spring.variable_admins_for_add_in_BD.role.admin_1}")
    @Getter
    private String adminRole;

    @Value("${spring.variable_admins_for_add_in_BD.password.admin_1}")
    @Getter
    private String adminPassword;

    @Value("${spring.variable_admins_for_add_in_BD.subscription.admin_1}")
    @Getter
    private String adminSubscription;

    @Value("${spring.variable_admins_for_add_in_BD.is_locked_account.admin_1}")
    @Getter
    private boolean isLockedAccount;

    private CustomInitialization customInitialization;
    @Autowired
    public void setCustomInitialization(CustomInitialization customInitialization) {
        this.customInitialization = customInitialization;
    }

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MyJAAS.class, args);
        // Create 1st user, who will be an administrator (data about this user is saved in application.yml file)
        MyJAAS myJaas = context.getBean(MyJAAS.class);
        PasswordEncoder passwordEncoder = context.getBean(PasswordEncoder.class);
        EntityManager entityManager = context.getBean(EntityManager.class); // Retrieve EntityManager bean
        // Put information about first admin in BD
        myJaas.customInitialization.initFirstAdminInBD(
                entityManager, myJaas.getAdminName(), myJaas.getAdminEmail(), myJaas.getAdminAge(), myJaas.getAdminLogin(),
                passwordEncoder.encode(myJaas.getAdminPassword()), myJaas.getAdminSubscription(), myJaas.isLockedAccount(),
                myJaas.getAdminRole()
        );
    }
}
