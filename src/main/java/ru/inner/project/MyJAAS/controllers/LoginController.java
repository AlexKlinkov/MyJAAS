package ru.inner.project.MyJAAS.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.inner.project.MyJAAS.client.WebClientOkhttp;
import ru.inner.project.MyJAAS.entities.Account;
import ru.inner.project.MyJAAS.entities.InitialUrl;
import ru.inner.project.MyJAAS.inputDTO.authentication.AuthenticationInputDTO;
import ru.inner.project.MyJAAS.outputDTO.authentication.AuthenticationOutputDTO;
import ru.inner.project.MyJAAS.repositories.InitialUrlRepository;
import ru.inner.project.MyJAAS.services.AuthenticationServiceImpl;
import ru.inner.project.MyJAAS.services.user.UserServiceImpl;
import ru.inner.project.MyJAAS.utils.GetPetitionerInformation;
import ru.inner.project.MyJAAS.utils.ResourceListWithEndpointInfo;

import java.io.IOException;


@Controller
public class LoginController {
    private final AuthenticationServiceImpl service;
    private final InitialUrlRepository initialUrlRepository;
    private final UserServiceImpl userService;
    private final GetPetitionerInformation getPetitionerInformation;

    @Autowired
    public LoginController(AuthenticationServiceImpl service,
                           InitialUrlRepository initialUrlRepository,
                           UserServiceImpl userService, GetPetitionerInformation getPetitionerInformation) {
        this.service = service;
        this.initialUrlRepository = initialUrlRepository;
        this.userService = userService;
        this.getPetitionerInformation = getPetitionerInformation;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request,
                                HttpServletResponse response) throws ServletException, IOException {
        model.addAttribute("login", new AuthenticationInputDTO());
        // check if user doesn't appeal to '/login' endpoint directly and user hasn't registered in system yet
        if (getUserLoginFromInitialQueryByIp(request.getRemoteAddr()) == null && !userService.isUserInBd()) {
            request.getRequestDispatcher("/register").forward(request, response); // user is not registered yet
        }
        return "login"; // returns view which in charges of rendering html page 'login form'
    }

    @PostMapping("/perform-login")
    @Transactional
    public ResponseEntity<?> loginSubmit(@ModelAttribute AuthenticationInputDTO loginAttributes,
                                                               Model model, HttpServletRequest request) {
        model.addAttribute("login", loginAttributes);
        AuthenticationOutputDTO authResult = service.authenticate(loginAttributes); // authentication
        InitialUrl initialUrl = initialUrlRepository.findById(request.getRemoteAddr()).orElse(null);
        // check if user doesn't appeal to '/login' endpoint directly
        if (initialUrl != null) {
            try {
                // create a web client who tries to return a response depends on appeal method to a final endpoint
                if (initialUrl.getAppealMethod().equals("GET")) {
                    return getResponseAfterRedirectedInitialQuery(
                            new WebClientOkhttp(request.getScheme() + "://" + request.getServerName() + ":"
                                    + request.getServerPort() + initialUrl.getUrl(),
                                    authResult.getJwt_token()).get(),
                            (MediaType) ResourceListWithEndpointInfo.getResourceInfo(initialUrl.getUrl()).get(0));
                } // here can be added a handler of others endpoint methods
            } catch (Exception ex) {
                return new ResponseEntity<>("The redirect on initial url: " + initialUrl.getUrl()
                        + " wasn't accomplished successful", HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(authResult);
    }

    @PostMapping("/my/logout")
    public ResponseEntity<String> logout() {
        changeUserStatusAsToLogInSystem(true);
        return ResponseEntity.ok("Logout successful");
    }

    // Auxiliaries methods
    private String getUserLoginFromInitialQueryByIp(String ip) { // checking that initial query isn't '/login'
        return initialUrlRepository.findById(ip).isPresent() ?
                initialUrlRepository.findById(ip).get().getUserLogin() : null;
    }
    // formatting redirected response
    private ResponseEntity<?> getResponseAfterRedirectedInitialQuery(byte[] answer, MediaType mediaType) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(mediaType); // Create HttpHeaders with appropriate content type
        return new ResponseEntity<>(answer, responseHeaders, HttpStatus.OK);
    }

    private void changeUserStatusAsToLogInSystem(boolean isLoggedOut) {
        try {
            Account userAccount = service.getUserAccountRepository().findAccountByLogin(
                    getPetitionerInformation.getPetitionerLoginFromContext());
            userAccount.setIsLoggedOut(isLoggedOut);
            service.getUserAccountRepository().save(userAccount); // update info in BD
        } catch (RuntimeException ex) {
            // "User is absent in system BD"
        }
    }

}