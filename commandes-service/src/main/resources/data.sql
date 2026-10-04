INSERT INTO commande (idempotency_key, client_id, date_commande, prix_total, statut)
VALUES ('commande-demo-1', 1, CURRENT_TIMESTAMP, 8297.48, 'CONFIRMEE');

INSERT INTO lignecommande (idcommande, produit_id, quantite, prix_unitaire, sous_total)
SELECT id, 1, 1, 1199.00, 1199.00 FROM commande WHERE idempotency_key = 'commande-demo-1';

INSERT INTO lignecommande (idcommande, produit_id, quantite, prix_unitaire, sous_total)
SELECT id, 2, 2, 1299.99, 2599.98 FROM commande WHERE idempotency_key = 'commande-demo-1';

INSERT INTO lignecommande (idcommande, produit_id, quantite, prix_unitaire, sous_total)
SELECT id, 3, 3, 1499.50, 4498.50 FROM commande WHERE idempotency_key = 'commande-demo-1';
