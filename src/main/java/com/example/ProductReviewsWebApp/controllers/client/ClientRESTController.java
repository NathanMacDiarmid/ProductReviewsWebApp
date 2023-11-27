package com.example.ProductReviewsWebApp.controllers.client;

import com.example.ProductReviewsWebApp.models.Client;
import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping(value="/api")
public class ClientRESTController {

    @Autowired
    private ClientRepository clientRepository;

    private Client getClient(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    private Client findClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
        }
        return client.get();
    }

    @GetMapping("/client")
    public Iterable<Client> getClients() { return clientRepository.findAll(); }

    @GetMapping(value="/client/{id}", produces = "application/json")
    public Client getClientById(@PathVariable("id") Long id) { return findClientById(id); }

    @GetMapping("/activeClientId")
    public Long getActiveClientId(@CookieValue(value = "activeClientID") String activeClientId) {
        if (!activeClientId.isEmpty())
            return Long.parseLong(activeClientId);

        return 0L;
    }

    @GetMapping(value = "/client/{id}/jaccardDistance", produces = "application/json")
    public double getJaccardDistanceFromUserToActiveUserById(@PathVariable("id") Long id, @CookieValue(value = "activeClientID") String activeClientId) {
        Long activeID;

        if (!activeClientId.isEmpty())
            activeID = Long.parseLong(activeClientId);
        else
            return 0;

        Optional<Client> activeClient = clientRepository.findById(activeID);
        Optional<Client> clientToCompare = clientRepository.findById(id);

        if (activeClient.isPresent() && clientToCompare.isPresent())
            return activeClient.get().getJaccardDistanceWithUser(clientToCompare.get());
        else
            return 0;
    }

    @PostMapping(value="/client/{id}/follow")
    public void follow(@PathVariable Long id, @CookieValue(value = "activeClientID") String activeClientId) {
        Client activeClient = getClient(Long.parseLong(activeClientId));
        Client client = getClient(id);

        if (!activeClient.isFollowing(client)) {
            activeClient.followUser(client);
            clientRepository.save(activeClient);
        }
    }

    @PostMapping(value="/client/{id}/unfollow")
    public void unFollow(@PathVariable Long id, @CookieValue(value = "activeClientID") String activeClientId) {
        Client activeClient = getClient(Long.parseLong(activeClientId));
        Client client = getClient(id);

        if (activeClient.isFollowing(client)) {
            activeClient.unfollowUser(client);
            clientRepository.save(activeClient);
            clientRepository.save(client);
        }
    }
}
