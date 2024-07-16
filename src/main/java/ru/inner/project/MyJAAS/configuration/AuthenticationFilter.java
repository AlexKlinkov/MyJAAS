package ru.inner.project.MyJAAS.configuration;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.inner.project.MyJAAS.entities.InitialUrl;
import ru.inner.project.MyJAAS.repositories.InitialUrlRepository;
import ru.inner.project.MyJAAS.services.user.UserAccountDetailsServiceImpl;
import ru.inner.project.MyJAAS.utils.GenerateNewAndCheckAlreadyExistToken;
import ru.inner.project.MyJAAS.utils.GetPetitionerInformation;
import ru.inner.project.MyJAAS.utils.UpdateSecurityContext;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final GenerateNewAndCheckAlreadyExistToken jwtService;
    private final UserAccountDetailsServiceImpl userAccountDetailsService;
    private final UpdateSecurityContext updateSecurityContext;
    private final InitialUrlRepository initialUrlRepository;
    private final GetPetitionerInformation getPetitionerInformation;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userLogin;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // taking user's token
            try {
                userLogin = jwtService.extractUserLogin(jwt); // user's login is unique in BD
                // the checking if user was authenticated before
                if (userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // take user from BD
                    UserDetails userDetails = this.userAccountDetailsService.loadUserByUsername(userLogin);
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        updateSecurityContext.updateSecurityContext(userDetails, request);
                        if (!userDetails.isAccountNonLocked()) { // account was locked by admin
                            request.getRequestDispatcher("/user/account/locked").forward(request, response);
                            return;
                        }
                        redirectToLoginPageIfUserLoggedOutOfSystem(request, response);
/*                        addCsrfTokenToResponse(request, response); // add CSRF token to header response*/
                        filterChain.doFilter(request, response);
                        return;
                    }
                }
            } catch (ExpiredJwtException e) {
                saveInitialUrl(request);
            }
        }
        saveInitialUrl(request);
        filterChain.doFilter(request, response); // to pass filter go ahead
    }

    // it's necessary for forward/redirect user on initial requested url (besides public, available urls for anyone)
    private void saveInitialUrl(HttpServletRequest request) {
        if (!isPublicEndpoint(request) && endpointsForSave(request)) {
            InitialUrl url = new InitialUrl();
            url.setUserIp(request.getRemoteAddr());
            url.setUrl(request.getRequestURI());
            url.setAppealMethod(request.getMethod());
            initialUrlRepository.save(url);
        }
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/login") || path.equals("/register")
                || path.equals("/favicon.ico") || path.equals("/perform-login");
    }

    // This method determines, which endpoints can be saved for forward/redirect queries
    private boolean endpointsForSave(HttpServletRequest request) {
        String path = request.getRequestURI();
        return
                path.equals("/api/v1/authorized/resources/free") ||
                        path.equals("/api/v1/authorized/resources/standard") ||
                        path.equals("/api/v1/authorized/resources/premium");
    }

    private void redirectToLoginPageIfUserLoggedOutOfSystem(HttpServletRequest request,
                                                            HttpServletResponse response) throws ServletException, IOException {
        // take a user account by user login as to check the user in system, or he is logged out
        Boolean userIsLoggedOut = userAccountDetailsService.getUserAccountByLogin(
                getPetitionerInformation.getPetitionerLoginFromContext()).getIsLoggedOut();
        if (Objects.nonNull(userIsLoggedOut) && userIsLoggedOut) {
            request.getRequestDispatcher("/login").forward(request, response); // forward query to login page
        }
    }

}
