# TP 2 — Infrastructure Spring Cloud

## Jalon V1 du projet microservices

**Formation :** J2EE avancé et Spring Boot — 5e année ingénierie, EMSI

**Enseignant :** M. KHALIL Salim

L'application e-commerce fournie comporte trois services métier autonomes. Dans ce TP, vous allez leur ajouter un registre de services et une passerelle HTTP. Vous mettrez en place le routage statique, puis le routage dynamique, avant d'observer le fonctionnement de l'ensemble avec Actuator.

À la fin du jalon V1, les services devront s'enregistrer auprès d'Eureka et être accessibles par la Gateway. Vous devrez également montrer qu'un service qui rejoint le registre devient accessible sans ajouter de route ni redémarrer la Gateway.

La centralisation de la configuration avec Config Server sera abordée dans un jalon ultérieur.

## 1. Récupérer et examiner le projet

Dépôt de départ : [tp-ms-ecom](https://github.com/salimk/tp-ms-ecom.git).

Utilisez Java 17 et les versions retenues dans ce projet : Spring Boot 3.5.6 et Spring Cloud 2025.0.0. Les exemples Gateway de ce TP utilisent le starter WebFlux et le préfixe de configuration `spring.cloud.gateway.server.webflux` introduits avec Spring Cloud 2025.0. Voir les [notes de version Spring Cloud](https://github.com/spring-cloud/spring-cloud-release/wiki/Spring-Cloud-2025.0-Release-Notes).

### 1.1. Récupération

Si vous souhaitez sauvegarder votre travail sur GitHub, créez un fork sur votre compte, puis clonez ce fork. Sinon, clonez le dépôt de départ et travaillez localement. Le clonage du dépôt de l'enseignant ne vous donne pas de droit de push.

Dans IntelliJ IDEA : **File → New → Project from Version Control**, saisissez l'URL choisie et cliquez sur **Clone**. Créez ensuite une branche `tp2-nom-prenom`.

Sans Git, utilisez **Code → Download ZIP** sur GitHub, extrayez l'archive et ouvrez le dossier dans IntelliJ avec **File → Open**.

### 1.2. Import Maven

Ouvrez le dossier contenant le `pom.xml` racine. Acceptez **Load Maven Project** ; si nécessaire, utilisez **Add as Maven Project** sur ce fichier. Attendez la résolution des dépendances et vérifiez la présence des trois modules dans la fenêtre Maven.

Le POM racine est un agrégateur : il énumère les modules. Chaque service possède actuellement son propre parent Spring Boot ; ajouter une propriété au POM racine ne suffit donc pas à la transmettre aux services.

Les trois services métier sont fournis avec Java 17 et le BOM Spring Cloud 2025.0.0 déjà configurés dans leurs POM.

| Module | Port initial | Chemin du contrôleur |
| --- | --- | --- |
| clients-service | 8081 | `/clients` |
| catalogue-service | 8082 | `/produits` |
| commandes-service | 8083 | `/commandes` |

Repérez dans chaque module la classe principale, les contrôleurs, la couche service, les repositories et le fichier `application.properties`.

### 1.3. Vérification de départ

Depuis la racine, compilez le projet :

```powershell
.\mvnw.cmd clean verify
```

Démarrez les services dans trois terminaux distincts :

```powershell
.\mvnw.cmd -pl clients-service spring-boot:run
.\mvnw.cmd -pl catalogue-service spring-boot:run
.\mvnw.cmd -pl commandes-service spring-boot:run
```

Testez les requêtes GET suivantes avec un navigateur ou Postman :

```text
http://localhost:8081/clients
http://localhost:8082/produits
http://localhost:8083/commandes
```

Les bases H2 sont alimentées par `data.sql` et recréées au démarrage. Utilisez les opérations de consultation pour ce TP. La création d'une commande via `POST /commandes/new` reste à réaliser dans le futur TP sur les communications interservices.

## 2. Créer le registre Eureka

### 2.1. Ajouter le module eureka-server

Créez une application Spring Boot Maven nommée `eureka-server`, dans un dossier du même nom à la racine. Utilisez Java 17, Spring Boot 3.5.6 et le package `com.ecom.eurekaserver`. Ajoutez le module à la liste `<modules>` du POM racine.

Pour les nouveaux modules d'infrastructure, utilisez un POM basé sur celui d'un service fourni : conservez le parent Spring Boot, les propriétés Java/Spring Cloud, le bloc `dependencyManagement` et le plugin Spring Boot. Adaptez le nom, l'artifactId et les dépendances au module créé.

Ajoutez cette dépendance à `eureka-server` :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

Vérifiez aussi la présence du plugin `spring-boot-maven-plugin` dans les nouveaux modules, comme dans les services fournis.

La classe principale devient :

```java
package com.ecom.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

Configurez `eureka-server/src/main/resources/application.properties` :

```properties
server.port=8761
spring.application.name=eureka-server
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

Démarrez le module, puis ouvrez [le tableau de bord Eureka](http://localhost:8761) :

```powershell
.\mvnw.cmd -pl eureka-server spring-boot:run
```

Expliquez pourquoi ce serveur ne s'enregistre pas auprès de lui-même.

### 2.2. Enregistrer les services métier

Dans les POM des trois services métier, ajoutez la dépendance Eureka Client :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

Dans leurs fichiers `application.properties`, conservez les noms, les ports et les paramètres H2/JPA existants. Ajoutez :

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

La dépendance active le client Eureka ; aucune annotation de découverte n'est nécessaire sur les classes principales.

Redémarrez les services. Attendez leur enregistrement et relevez dans Eureka le nom, le statut et le port de chaque instance. Les noms sont généralement affichés en majuscules : `CLIENTS-SERVICE`, `CATALOGUE-SERVICE` et `COMMANDES-SERVICE`.

## 3. Mettre en place le routage statique

### 3.1. Créer le module gateway-service

Créez une application Maven Spring Boot `gateway-service` sur les mêmes versions que les autres modules et ajoutez-la au POM racine. Sa classe principale est une classe Spring Boot standard avec `@SpringBootApplication` et une méthode `main`.

Ajoutez ces dépendances au module Gateway :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Cette Gateway utilise WebFlux et Netty. Ne sélectionnez pas Spring Web et n'ajoutez pas `spring-boot-starter-web` à ce module. Voir le [starter Gateway WebFlux](https://docs.spring.io/spring-cloud-gateway/reference/4.3/spring-cloud-gateway-server-webflux/starter.html).

Dans `gateway-service/src/main/resources/application.properties`, placez la configuration commune :

```properties
server.port=8080
spring.application.name=gateway-service
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

### 3.2. Déclarer les routes

Créez `application-statique.properties` dans le même dossier :

```properties
spring.cloud.gateway.server.webflux.discovery.locator.enabled=false

spring.cloud.gateway.server.webflux.routes[0].id=clients-route
spring.cloud.gateway.server.webflux.routes[0].uri=lb://CLIENTS-SERVICE
spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/clients/**

spring.cloud.gateway.server.webflux.routes[1].id=catalogue-route
spring.cloud.gateway.server.webflux.routes[1].uri=lb://CATALOGUE-SERVICE
spring.cloud.gateway.server.webflux.routes[1].predicates[0]=Path=/produits/**

spring.cloud.gateway.server.webflux.routes[2].id=commandes-route
spring.cloud.gateway.server.webflux.routes[2].uri=lb://COMMANDES-SERVICE
spring.cloud.gateway.server.webflux.routes[2].predicates[0]=Path=/commandes/**
```

Le prédicat `Path` sélectionne les requêtes qui correspondent au chemin déclaré. Le préfixe `lb://` demande à Spring Cloud LoadBalancer de choisir une instance du service découvert via Eureka. Les routes sont déclarées manuellement, mais les adresses et les ports des instances restent résolus par la découverte de services.

Démarrez la Gateway avec le profil `statique` ; dans IntelliJ, vous pouvez renseigner ce profil dans la configuration de lancement. Avec Maven :

```powershell
.\mvnw.cmd -pl gateway-service spring-boot:run "-Dspring-boot.run.profiles=statique"
```

Testez :

```text
http://localhost:8080/clients
http://localhost:8080/produits
http://localhost:8080/commandes
```

Comparez les réponses avec celles obtenues directement sur les ports métier. Dessinez le trajet d'une requête vers `/produits` et distinguez la consultation du registre du transfert de la requête HTTP.

## 4. Passer au routage dynamique

Créez `application-dynamique.properties` dans les ressources de la Gateway :

```properties
spring.cloud.gateway.server.webflux.discovery.locator.enabled=true
spring.cloud.gateway.server.webflux.discovery.locator.lower-case-service-id=true
```

Arrêtez la Gateway et relancez-la avec le seul profil `dynamique` :

```powershell
.\mvnw.cmd -pl gateway-service spring-boot:run "-Dspring-boot.run.profiles=dynamique"
```

Les routes statiques restent dans leur profil ; elles ne doivent pas être recopiées dans le fichier commun. Vous utilisez un seul module Gateway sur le port 8080, avec un mode de routage à la fois.

Testez les nouvelles URL :

```text
http://localhost:8080/clients-service/clients
http://localhost:8080/catalogue-service/produits
http://localhost:8080/commandes-service/commandes
```

Le locator construit les routes à partir des services découverts. Le filtre généré retire le préfixe du service : `/catalogue-service/produits` est transmis au catalogue sous la forme `/produits`. Les prédicats et filtres générés peuvent être personnalisés. Voir le [fonctionnement du DiscoveryClient Route Definition Locator](https://docs.spring.io/spring-cloud-gateway/reference/4.3/spring-cloud-gateway-server-webflux/the-discoveryclient-route-definition-locator.html).

Complétez ce tableau à partir de vos essais :

| Point à comparer | Routage statique | Routage dynamique |
| --- | --- | --- |
| Origine de la définition des routes | | |
| URL utilisée pour consulter les produits | | |
| Modification nécessaire pour exposer un nouveau service | | |
| Résolution de l'adresse et du port de l'instance | | |

## 5. Observer les services et les routes avec Actuator

### 5.1. État de santé et métriques

Vérifiez que chaque service métier possède la dépendance `spring-boot-starter-actuator` ; ajoutez-la si elle manque. Dans leurs fichiers `application.properties`, définissez :

```properties
management.endpoints.web.exposure.include=health,info,metrics
```

Après redémarrage, consultez `/actuator/health` sur chacun des ports métier. Consultez également `/actuator/metrics` pour connaître les métriques disponibles, puis une métrique de votre choix, par exemple `/actuator/metrics/jvm.memory.used`.

### 5.2. Inspection des routes Gateway

Dans le fichier commun `application.properties` de la Gateway, ajoutez :

```properties
management.endpoints.web.exposure.include=health,info,metrics,gateway
management.endpoint.gateway.access=read-only
```

Ces paramètres exposent les points d'observation nécessaires au TP local. L'endpoint Gateway doit être autorisé et exposé pour être accessible. Voir l'[API Actuator Gateway](https://docs.spring.io/spring-cloud-gateway/reference/4.3/spring-cloud-gateway-server-webflux/actuator-api.html).

Redémarrez la Gateway. Consultez :

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/gateway/routes
http://localhost:8080/actuator/metrics
```

Pour chaque mode de routage, relevez les identifiants de routes, les URI `lb://`, les prédicats et les filtres. En mode dynamique, repérez le filtre qui retire le nom du service du chemin. Une route vers la Gateway elle-même peut aussi apparaître puisqu'elle est enregistrée dans Eureka.

Effectuez plusieurs appels métier via la Gateway, puis cherchez la métrique `spring.cloud.gateway.requests` dans `/actuator/metrics`. Si elle est présente, consultez `/actuator/metrics/spring.cloud.gateway.requests` et examinez ses tags.

Actuator permet ici d'observer l'état local et le routage. Un état `UP` de la Gateway ne prouve pas à lui seul que tous les services métier sont joignables : vérifiez aussi les requêtes de bout en bout.

### 5.3. Vérifier la détection automatique d'un service

Réalisez cette expérience avec le profil `dynamique` :

1. Arrêtez proprement `commandes-service`, puis attendez que son inscription disparaisse d'Eureka et que la liste des routes soit actualisée.
2. Consultez `/actuator/gateway/routes` et conservez la réponse obtenue sans ce service.
3. Redémarrez `commandes-service` sans modifier la configuration de la Gateway et sans la redémarrer.
4. Observez son retour dans Eureka, puis l'apparition de sa route dans `/actuator/gateway/routes`.
5. Testez `http://localhost:8080/commandes-service/commandes` et conservez la réponse.

L'enregistrement, la récupération du registre et l'actualisation des routes prennent du temps. Répétez les consultations jusqu'à observer le changement et relevez le délai constaté. En cas d'arrêt brutal, Eureka peut conserver une inscription plus longtemps ; ne concluez pas à une disparition immédiate.

Cette expérience montre l'ajout d'une route pour un nom de service qui rejoint le registre. Démarrer une deuxième instance sous le même nom ajoute une instance disponible pour la répartition des requêtes, pas une nouvelle route par instance.

Reprenez ensuite le profil `statique` et comparez : les définitions de routes restent déclarées dans la configuration, même lorsqu'aucune instance du service cible n'est disponible.

## 6. Livrer le jalon V1

Votre projet doit contenir les trois services métier, `eureka-server` et `gateway-service`. Les cinq modules doivent être reconnus par Maven. Conservez les configurations des deux profils Gateway pour permettre leur démonstration.

Exécutez `clean verify` depuis la racine. Pour la démonstration, démarrez Eureka, les services métier, puis la Gateway avec le profil choisi.

Remettez le projet et un compte rendu comprenant :

- un schéma de l'architecture avec les noms et ports des cinq applications ;
- la liste des dépendances ajoutées et leur rôle ;
- une capture du registre Eureka avec les services enregistrés ;
- les requêtes et réponses de consultation via les deux modes Gateway ;
- les relevés Actuator des routes statiques et dynamiques ;
- les observations avant et après le retour de `commandes-service`, avec le délai constaté ;
- les réponses aux questions ci-dessous.

Si vous utilisez Git, enregistrez vos modifications et créez un tag `v1` sur le commit correspondant au jalon validé.

### Questions de synthèse

1. Quel rôle joue Eureka ? Quelles informations une instance lui communique-t-elle ?
2. Pourquoi une route `lb://CATALOGUE-SERVICE` continue-t-elle à fonctionner si le port du catalogue change après son réenregistrement ?
3. Une route statique avec `lb://` utilise-t-elle la découverte de services ? Justifiez.
4. Comment la Gateway transforme-t-elle `/catalogue-service/produits` avant de transmettre la requête ?
5. Quelles preuves montrent que la route de commandes a été détectée automatiquement ?
6. Quelle différence faites-vous entre un service enregistré, une route présente et une requête métier réussie ?
7. Dans quel cas choisiriez-vous des routes déclarées explicitement plutôt que des routes générées par la découverte ?
8. Quels endpoints Actuator avez-vous utilisés pour observer l'état des applications, leurs routes et leurs métriques ?
