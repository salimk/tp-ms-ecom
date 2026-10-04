package com.ecom.clientsservice.controller;

import com.ecom.clientsservice.dto.ClientDto;
import com.ecom.clientsservice.entite.Client;
import com.ecom.clientsservice.mapper.ClientMapper;
import com.ecom.clientsservice.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;
    private final ClientMapper clientMapper;

    public ClientController(ClientService clientService, ClientMapper clientMapper) {
        this.clientService = clientService;
        this.clientMapper = clientMapper;
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClient().stream().map(clientMapper::toDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientMapper.toDto(clientService.getClientById(id)));
    }

    @PostMapping("/new")
    public ResponseEntity<ClientDto> ajouterClient(@Valid @RequestBody ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        client.setId(null);
        client = clientService.saveClient(client);
        return ResponseEntity.created(URI.create("/clients/" + client.getId()))
                .body(clientMapper.toDto(client));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> modifierClient(@PathVariable Long id,
                                                       @Valid @RequestBody ClientDto clientDto) {
        Client client = clientService.modifierClient(id, clientMapper.toEntity(clientDto));
        return ResponseEntity.ok(clientMapper.toDto(client));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerClient(@PathVariable Long id) {
        clientService.supprimerClient(id);
        return ResponseEntity.noContent().build();
    }
}
