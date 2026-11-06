package com.ecom.commandesservice.feignClient;

import com.ecom.commandesservice.exception.ServiceExterneIndisponibleException;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResilientRequests {

    private final ClientsRequest clientsRequest;
    private final ProduitRequest produitRequest;
    private final CacheManager cacheManager;

    public ResilientRequests(ClientsRequest clientsRequest,
                             ProduitRequest produitRequest,
                             CacheManager cacheManager) {
        this.clientsRequest = clientsRequest;
        this.produitRequest = produitRequest;
        this.cacheManager = cacheManager;
    }

    // ========== Lecture : repli tolérant (cache ou objet vide) ==========

    @Retry(name = "clients-service", fallbackMethod = "clientDeRepli")
    @CircuitBreaker(name = "clients-service")
    @Bulkhead(name = "clients-service")
    public ClientDto getClientByIdFb(Long id) {
        ClientDto client = clientsRequest.getClientById(id);
        mettreEnCache("clients", id, client);
        log.info("Client {} put in cache", id);

        return client;
    }

    @Retry(name = "catalogue-service", fallbackMethod = "produitDeRepli")
    @CircuitBreaker(name = "catalogue-service")
    @Bulkhead(name = "catalogue-service")
    public ProduitDto getProduitByIdFb(Long id) {
        ProduitDto produit = produitRequest.getProduitById(id);
        mettreEnCache("produits", id, produit);
        log.info("Produit {} put in cache", id);
        return produit;
    }

    // ========== Création : repli strict (exception 503) ==========

    @Retry(name = "clients-service", fallbackMethod = "clientIndisponible")
    @CircuitBreaker(name = "clients-service")
    @Bulkhead(name = "clients-service")
    public ClientDto getClientByIdStrict(Long id) {
        ClientDto client = clientsRequest.getClientById(id);
        mettreEnCache("clients", id, client);
        log.info("Client {} put in cache", id);
        return client;
    }

    @Retry(name = "catalogue-service", fallbackMethod = "produitIndisponible")
    @CircuitBreaker(name = "catalogue-service")
    @Bulkhead(name = "catalogue-service")
    public ProduitDto getProduitByIdStrict(Long id) {
        ProduitDto produit = produitRequest.getProduitById(id);
        mettreEnCache("produits", id, produit);
        log.info("Produit {} put in cache", id);
        return produit;
    }

    // ========== Fallbacks ==========

    private ClientDto clientDeRepli(Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        ClientDto enCache = lireCache("clients", id, ClientDto.class);
        log.info("Client {} loaded from cache Or null", id);
        return enCache != null ? enCache : new ClientDto(id, null, null, null);
    }

    private ProduitDto produitDeRepli(Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        ProduitDto enCache = lireCache("produits", id, ProduitDto.class);
        log.info("Produit {} loaded from cache OR null", id);
        return enCache != null ? enCache : new ProduitDto(id, null, null, null, null, null);
    }

    private ClientDto clientIndisponible(Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        throw new ServiceExterneIndisponibleException(
                "Clients-Service est momentanément indisponible. Réessayez dans quelques instants.");
    }

    private ProduitDto produitIndisponible(Long id, RuntimeException ex) {
        if (!estIndisponible(ex)) {
            throw ex;
        }
        throw new ServiceExterneIndisponibleException(
                "Le Catalogue-Service est momentanément indisponible. Réessayez dans quelques instants.");
    }

    private boolean estIndisponible(RuntimeException cause) {
        return cause instanceof CallNotPermittedException
                || (cause instanceof FeignException feign
                && (feign.status() == -1 || feign.status() == 503));
    }
    // ========== Cache ==========

    private void mettreEnCache(String nomCache, Object cle, Object valeur) {
        Cache cache = cacheManager.getCache(nomCache);
        if (cache != null) {
            cache.put(cle, valeur);
        }
    }

    private <T> T lireCache(String nomCache, Object cle, Class<T> type) {
        Cache cache = cacheManager.getCache(nomCache);
        return cache != null ? cache.get(cle, type) : null;
    }
}
