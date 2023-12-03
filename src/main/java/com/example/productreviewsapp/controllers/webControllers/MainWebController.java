package com.example.productreviewsapp.controllers.webControllers;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.FakeLoginRequest;
import com.example.productreviewsapp.models.SystemConstants;
import com.example.productreviewsapp.repositories.ClientRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Main controller class.
 */
@Controller
public class MainWebController {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Get index mapping.
     *
     * @return String
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Get home mapping.
     *
     * @return String
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    /**
     * Get login mapping.
     *
     * @return String
     */
    @GetMapping("/login")
    public String loginForm(@ModelAttribute FakeLoginRequest fakeLoginRequest, Model model) {
        model.addAttribute("loginRequest", new FakeLoginRequest(""));
        return "login";
    }

    /**
     * Post login mapping.
     *
     * @return String
     */
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

    /**
     * Get logout mapping.
     *
     * @return String
     */
    @GetMapping("/logout")
    public String logoutAction(HttpServletResponse response) {
        Cookie deleteActiveClientId = new Cookie(SystemConstants.ACTIVE_CLIENT_ID_COOKIE, null);
        response.addCookie(deleteActiveClientId);
        return "index";
    }

}
