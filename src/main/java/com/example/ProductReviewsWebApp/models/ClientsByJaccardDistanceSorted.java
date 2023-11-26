package com.example.ProductReviewsWebApp.models;

import com.example.ProductReviewsWebApp.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ClientsByJaccardDistanceSorted {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private final Map<Long, Double> clientsByJaccardDistance = new HashMap<>();

    private final List<Client> jaccardDistanceSortedClients = new ArrayList<>();

    public ClientsByJaccardDistanceSorted(ClientRepository clientRepository, Long activeClientId) {
        Optional<Client> activeClient = clientRepository.findById(activeClientId);
        List<Client> allClients = clientRepository.findAll();
        List<Double> sortedJaccardDistances = new ArrayList<>();

        for (Client client : allClients) {
            if (activeClient.isPresent()) {
                double jaccardDistance = activeClient.get().getJaccardDistanceWithUser(client);
                clientsByJaccardDistance.put(client.getId(), jaccardDistance);
                sortedJaccardDistances.add(jaccardDistance);
            }
        }

        sortedJaccardDistances.sort(Comparator.reverseOrder());

        for (double jaccardDistanceToFind : sortedJaccardDistances) {

            Map<Long, Double> tempMap = new HashMap<>();

            for (Long id : clientsByJaccardDistance.keySet()) {
                tempMap.put(id, clientsByJaccardDistance.get(id));
            }

            if (tempMap.containsValue(jaccardDistanceToFind)) {

                for (Long id : clientsByJaccardDistance.keySet()) {

                    if (tempMap.get(id) == jaccardDistanceToFind) {
                        if (clientRepository.findById(id).isPresent()) {
                            Client clientToAdd = clientRepository.findById(id).get();

                            if (!jaccardDistanceSortedClients.contains(clientToAdd))
                                jaccardDistanceSortedClients.add(clientToAdd);
                        }
                    }

                    tempMap.remove(id);
                }
            }
        }
    }

    public List<Client> getClientsSortedByJaccardDistance() {
        return this.jaccardDistanceSortedClients;
    }

    public double getJaccardDistanceForId(Long id) {
        return clientsByJaccardDistance.get(id);
    }
}
