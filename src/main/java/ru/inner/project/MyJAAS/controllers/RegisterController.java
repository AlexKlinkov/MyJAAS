package ru.inner.project.MyJAAS.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.inner.project.MyJAAS.entities.InitialUrl;
import ru.inner.project.MyJAAS.inputDTO.register.UserAccountRegistrationInputDTO;
import ru.inner.project.MyJAAS.outputDTO.register.UserRegistrationOutputDTO;
import ru.inner.project.MyJAAS.repositories.InitialUrlRepository;
import ru.inner.project.MyJAAS.services.RegisterServiceImpl;

import java.io.IOException;

@Controller
public class RegisterController {

    private final RegisterServiceImpl service;
    private final InitialUrlRepository initialUrlRepository;

    @Autowired
    public RegisterController(RegisterServiceImpl service, InitialUrlRepository initialUrlRepository) {
        this.service = service;
        this.initialUrlRepository = initialUrlRepository;
    }

    @GetMapping("/register")
    public String getRegisterForm(Model model) {
        model.addAttribute("register", new UserAccountRegistrationInputDTO());
        return "register"; // returns view which in charges of rendering html page 'register form'
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationOutputDTO> register(@ModelAttribute UserAccountRegistrationInputDTO inputDTO,
                                                              Model model, HttpServletRequest request,
                                                              HttpServletResponse response) throws ServletException, IOException {
        model.addAttribute("register", inputDTO);
        UserRegistrationOutputDTO registrationResult = service.register(inputDTO); // registration
        InitialUrl url;
        try {
            url = initialUrlRepository.findById(request.getRemoteAddr()).orElse(null);
        } catch (Exception e) {
            url = null;
        }
        if (url != null) {
            url.setUserLogin(inputDTO.getLogin()); // add userLogin
            initialUrlRepository.save(url); // update info
            request.getRequestDispatcher("/login").forward(request, response);
            return ResponseEntity.ok().build(); // Вернуть пустой ответ, так как перенаправление уже выполнено
        }
        return ResponseEntity.ok(registrationResult);
    }
}