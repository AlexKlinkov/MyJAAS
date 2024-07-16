package ru.inner.project.MyJAAS.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.inner.project.MyJAAS.entities.Account;
import ru.inner.project.MyJAAS.entities.UserRole;
import ru.inner.project.MyJAAS.repositories.UserRoleRepository;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Base class for spring security, which allow him to retrieve user's info for auth process by UserDetailsService
public class UserAccountDetailsImpl implements UserDetails {
    private String login;
    private String password;
    private List<GrantedAuthority> rolesAndAuthorities;
    private boolean isLockedAccount;

    public UserAccountDetailsImpl(Account userAccount, UserRoleRepository userRoleRepository) {
        this.login = userAccount.getLogin();
        this.password = userAccount.getPassword();
        this.rolesAndAuthorities = fillUpUserRoleAndAuthorities(
                userRoleRepository.getUserRolesByUserId(userAccount.getUserId()),
                userAccount.getSubscription()
        );
        this.isLockedAccount = userAccount.getIsLockedAccount();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { // Unique value in spring to identify users
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLockedAccount;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private List<GrantedAuthority> fillUpUserRoleAndAuthorities(List<UserRole> roles, String userSubscription) {
        if (!Objects.requireNonNull(roles).isEmpty()) { // fill up user role
            List<GrantedAuthority> userRoleAndAuthorities = new ArrayList<>();
            SimpleGrantedAuthority setOfUserRolesAndAuthority;
            for (UserRole role : roles) { // add user role here
                setOfUserRolesAndAuthority = new SimpleGrantedAuthority("ROLE_" + role.getId().getRole());
                userRoleAndAuthorities.add(setOfUserRolesAndAuthority);
            } // add user authorities (level of subscription)
            setOfUserRolesAndAuthority = new SimpleGrantedAuthority(userSubscription);
            userRoleAndAuthorities.add(setOfUserRolesAndAuthority);
            return userRoleAndAuthorities;
        }
        return null;
    }
}
