package com.example.productreviewsapp.controllers.restControllers;

import com.example.productreviewsapp.models.Client;
import com.example.productreviewsapp.models.SystemConstants;
import com.example.productreviewsapp.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * RestController for API calls for the Client class.
 */
@RestController
@RequestMapping(value = "/api")
public class ClientRESTController {

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
     * @return Iterable<Client>
     */
    @GetMapping("/client")
    public Iterable<Client> getClients() {
        return clientRepository.findAll();
    }

    /**
     * Get client by id mapping.
     *
     * @param id Long
     * @return Client
     */
    @GetMapping(value = "/client/{id}", produces = "application/json")
    public Client getClientById(@PathVariable("id") Long id) {
        return findClientById(id);
    }

    /**
     * Get active client id mapping.
     *
     * @param activeClientId String
     * @return Long
     */
    @GetMapping("/activeClientId")
    public Long getActiveClientId(@CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId) {
        if (!activeClientId.isEmpty())
            return Long.parseLong(activeClientId);
        return 0L;
    }

    /**
     * Get the Jaccard distance between two clients.
     *
     * @param id             Long
     * @param activeClientId String
     * @return double
     */
    @GetMapping(value = "/client/{id}/jaccardDistance", produces = "application/json")
    public double getJaccardDistanceFromUserToActiveUserById(@PathVariable("id") Long id, @CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId) {
        long activeID;

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

    /**
     * Post mapping follow.
     *
     * @param id             Long
     * @param activeClientId String
     */
    @PostMapping(value = "/client/{id}/follow")
    public void follow(@PathVariable Long id, @CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId) {
        Client activeClient = getClient(Long.parseLong(activeClientId));
        Client client = getClient(id);

        if (!activeClient.isFollowing(client)) {
            activeClient.followUser(client);
            clientRepository.save(activeClient);
        }
    }

    /**
     * Post mapping unfollow.
     *
     * @param id             Long
     * @param activeClientId String
     */
    @PostMapping(value = "/client/{id}/unfollow")
    public void unFollow(@PathVariable Long id, @CookieValue(value = SystemConstants.ACTIVE_CLIENT_ID_COOKIE) String activeClientId) {
        Client activeClient = getClient(Long.parseLong(activeClientId));
        Client client = getClient(id);

        if (activeClient.isFollowing(client)) {
            activeClient.unfollowUser(client);
            clientRepository.save(activeClient);
            clientRepository.save(client);
        }
    }

}
