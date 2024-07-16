package ru.inner.project.MyJAAS.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class UpdateSecurityContext {

    public void updateSecurityContext(UserDetails userDetails, HttpServletRequest request) {
        // create a security context
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetails(request)); // extend details for security context
        SecurityContextHolder.getContext().setAuthentication(authToken); // update security context
    }
}
