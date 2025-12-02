
# Architecture technique de la plateforme

La figure ci-dessous décrit l’architecture technique retenue pour la plateforme d’expérimentation. Elle repose sur des microservices conteneurisés, interconnectés via :

- Une **API Gateway**
- Un **bus d’événements Kafka**
- Un **service de découverte Eureka**
- Un **config server centralisé**
- Le tout orchestré avec **Docker Compose**

![Architecture](images/architecture_chapitre_4.png)

## Synthèse des choix technologiques

| Composant                   | Rôle                                           | Technologie              | Justification                                                                 |
|----------------------------|------------------------------------------------|--------------------------|-------------------------------------------------------------------------------|
| Service de découverte      | Localisation dynamique                         | **Eureka Server**        | Intégration native Spring Cloud                                               |
| Communication événementielle | Asynchrone                                  | **Apache Kafka**         | Performance, découplage, scalabilité                                          |
| Passerelle API             | Sécurité, routage                              | **Spring Cloud Gateway** | Facilité de config, compatibilité Spring Boot                                 |
| Config centralisée         | Fichiers dynamiques                            | **Spring Config Server** | Centralisation, Git, rechargement automatique                                 |
| Backend                    | Services métier                                | **Spring Boot**          | Développement rapide, microservices ready                                     |
| Conteneurisation           | Isolation et portabilité                       | **Docker**               | Standardisation du déploiement                                                |
| Orchestration              | Lancement coordonné                            | **Docker Compose**       | Simple à utiliser localement                                                  |
| Base relationnelle         | Stockage utilisateurs/interactions             | **MySQL**                | Robustesse et compatibilité                                                   |
| Base NoSQL                 | Résultats expérimentaux                        | **MongoDB**              | Schéma flexible                                                               |
| Time-series DB             | Événements temporels (optionnel)               | **InfluxDB**             | Spécialisée pour séries chronologiques                                        |
| Interface utilisateur      | Visualisation                                  | **ReactJS + Chart.js**   | Moderne, interactive                                                           |

---

## Services métier

### 1. Manager-Service

Gère les campagnes d’expérimentation :

- 📄 Classe principale : `ExperimentController`
- 📌 Fonctions :
  - `POST /experiments`
  - `GET /experiments/{id}`
  - `POST /experiments/launch`

![ExperimentController](images/Creer-Experimentation.png)

---

### 2. Evaluation-Service

Calcule les métriques à partir des résultats :

- 📄 Classe principale : `EvaluationEngine`
- 📌 Fonctions :
  - `POST /evaluate`
  - `GET /results`

![EvaluationEngine](images/EvaluationController.png)

---

### 3. Replay-Service

Simule les flux utilisateurs via Kafka :

- 📄 Classe principale : `ReplaySimulator`
- 📌 Fonction :
  - `POST /replay/send-Notification`

![ReplaySimulator](images/reply.png)

---

### 4. Extraits de code

- **Kafka Producer** :  
![Producer](images/KafkaItemProducer.png)

- **Kafka Consumer** :  
![Consumer](images/KafkaItemConsumer.png)

---

# Expérimentations

## Jeux de données utilisés

- **Plista** : données chronologiques issues de [ksta.de](http://www.ksta.de)
- +2 millions d’événements
- 1088 articles
- ~858k utilisateurs (dont 30% anonymes)

---

## Algorithmes évalués

- `Random` : recommandations aléatoires
- `Recently Popular` : items populaires récemment
- `Most Popular` : items les plus populaires globalement
- `Recently Clicked` : items consultés récemment
- `CoOccurrence` : items fréquemment co-consommés

---

## Protocole d’évaluation

Étapes principales :

1. **Préparation** : transformation des données en flux temporel
2. **Partitionnement** : découpage en fenêtres temporelles (`windowSize`)
3. **Distribution** : envoi des événements via Kafka
4. **Évaluation continue** : calcul de métriques

### Paramètres d’expérimentation

- Algorithme (e.g. `Random`)
- Dataset (`plista418_1M`)
- Stratégie temporelle (`windowSize`)
- `Top-K` (nombre de recommandations)
- Métriques (`Precision`, `Recall`, `NDCG`, `MAP`, `F1`, etc.)

Cette approche permet de simuler un environnement évolutif, réaliste et reproductible.
