package com.example.productreviewsapp.controllers.webControllers;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.SystemConstants;
import com.example.productreviewsapp.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Client controller class.
 */
@Controller
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Get client.
     *
     * @param id Long
     * @return Client
     */
    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    /**
     * Find client by id.
     *
     * @param id Long
     * @return Client
     */
    private Client findClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    /**
     * Get clients mapping.
     *
     * @param activeClientId String
     * @param model          Model
     * @return String
     */
    @GetMapping(value = "/client")
    public String getClients(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, Model model) {
        List<Client> clientList = clientRepository.findAll();
        Client client = getClient(Long.parseLong(activeClientId));
        model.addAttribute("activeClient", client);
        model.addAttribute("ClientList", clientList);
        return "client";
    }

    /**
     * Get my account mapping.
     *
     * @param activeClientId String
     * @param model          Model
     * @return String
     */
    @GetMapping(value = "/myaccount", produces = "application/json")
    public String getMyAccount(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, Model model) {
        Client client = getClient(Long.parseLong(activeClientId));
        model.addAttribute("activeClient", client);
        return "myAccount";
    }

    /**
     * Get client mapping.
     *
     * @param id             Long
     * @param activeClientId String
     * @param model          Model
     * @return String
     */
    @GetMapping(value = "/client/{id}", produces = "application/json")
    public String getClientById(@PathVariable("id") Long id, @CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, Model model) {
        if (id == Long.parseLong(activeClientId)) {
            model.addAttribute("activeClient", getClient(Long.parseLong(activeClientId)));
            return "myAccount";
        }
        Client client = this.findClientById(id);
        Client activeClient = this.findClientById(Long.parseLong(activeClientId));
        model.addAttribute("client", client);
        model.addAttribute("activeClient", activeClient);
        return "client-page";
    }

    /**
     * Get client by name mapping.
     *
     * @param name           String
     * @param activeClientId String
     * @param model          Model
     * @return String
     */
    @GetMapping(value = "/client/username/{name}", produces = "application/json")
    public String getClientByName(@PathVariable("name") String name, @CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId, Model model) {
        Client client = clientRepository.findByUsername(name);
        Client activeClient = this.findClientById(Long.parseLong(activeClientId));
        model.addAttribute("client", client);
        model.addAttribute("activeClient", activeClient);
        return "client-page";
    }

}
