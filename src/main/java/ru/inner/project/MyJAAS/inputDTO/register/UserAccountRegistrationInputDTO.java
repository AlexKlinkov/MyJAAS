package ru.inner.project.MyJAAS.inputDTO.register;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountRegistrationInputDTO {

    @Size(min = 2, max = 100)
    private String username;
    @Positive
    private Integer age;
    @Email(message = "Max length of email is 50 letters")
    private String email;
    @Size(min = 2, max = 30, message = "Login should be unique")
    private String login;
    @Size(min = 6, max = 16, message = "Password should be between 6 till 16")
    private String password;
    @Size(min = 6, max = 16, message = "Second password should be the same like first one")
    private String repeatPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
