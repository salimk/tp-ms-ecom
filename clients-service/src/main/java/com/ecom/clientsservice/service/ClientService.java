package com.ecom.clientsservice.service;

import com.ecom.clientsservice.entite.Client;
import com.ecom.clientsservice.repository.ClientRepository;
import com.ecom.clientsservice.exception.RessourceIntrouvableException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClient() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Identifiant introuvable"));
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Client modifierClient(Long id, Client nouveauClient) {
        Client client = getClientById(id);
        client.setName(nouveauClient.getName());
        client.setEmail(nouveauClient.getEmail());
        client.setEtat(nouveauClient.getEtat());
        return clientRepository.save(client);
    }

    public void supprimerClient(Long id) {
        clientRepository.delete(getClientById(id));
    }
}
