package com.ecom.catalogueservice.repository;

import com.ecom.catalogueservice.entite.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produit p where p.id = :id")
    Optional<Produit> findProduitPourModification(@Param("id") Long id);
}
