package com.example.ProductReviewsWebApp.controllers.webControllers;

import com.example.ProductReviewsWebApp.models.*;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainWebController {

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute FakeLoginRequest fakeLoginRequest, Model model) {
        model.addAttribute("loginRequest", new FakeLoginRequest(""));
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute FakeLoginRequest fakeLoginRequest, Model model, HttpServletResponse response) {
        model.addAttribute("loginRequest", fakeLoginRequest);

        if (clientRepository.existsByUsername(fakeLoginRequest.username())) {
            Client activeClient = clientRepository.findByUsername(fakeLoginRequest.username());
            Cookie activeClientID = new Cookie(SystemConstants.ACTIVE_CLIENT_ID_COOKIE, activeClient.getId().toString());

            response.addCookie(activeClientID);
            return "home";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logoutAction(HttpServletResponse response) {
        Cookie deleteActiveClientId = new Cookie(SystemConstants.ACTIVE_CLIENT_ID_COOKIE, null);
        response.addCookie(deleteActiveClientId);
        return "index";
    }
}
