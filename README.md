# DriveMax 

Application Android de gestion de location de véhicules avec trois rôles (Admin, Employé, Client), base locale Room (SQLite), synchronisation Firebase Firestore, contrats PDF avec double signature et paiement simulé.

---

## Table des matières

1. [Présentation](#présentation)
2. [Technologies utilisées](#technologies-utilisées)
3. [Architecture du projet](#architecture-du-projet)
4. [Rôles et fonctionnalités](#rôles-et-fonctionnalités)
5. [Flux métier](#flux-métier)
6. [Stockage des données](#stockage-des-données)
7. [Base de données Room — Tables complètes](#base-de-données-room--tables-complètes)
8. [Firebase Firestore — Collections](#firebase-firestore--collections)
9. [SharedPreferences](#sharedpreferences)
10. [Installation](#installation)
11. [Configuration Firebase](#configuration-firebase)
12. [Sécurité](#sécurité)


---

## Présentation

DriveMax est une application Android complète pour agences de location de véhicules. Elle permet de gérer l'ensemble du cycle de vie d'une location : catalogue véhicules, réservations, contrats PDF avec signatures numériques, paiements simulés, réclamations et statistiques avancées.

---

## Technologies utilisées

| Technologie | Usage |
|---|---|
| Java | Langage principal |
| Android SDK 26 → 34 | Plateforme mobile |
| Room 2.6 (SQLite) | Base de données locale |
| Firebase Firestore | Synchronisation cloud |
| Firebase Auth | Authentification |
| Firebase Messaging (FCM) | Notifications push |
| ViewBinding / Material | Interface utilisateur |
| RecyclerView + DiffUtil | Listes performantes |
| ML Kit (OCR) | Scan CIN sur contrat |
| ContratPdfHelper | Génération PDF contrats |

---

## Architecture du projet

```
com.drivemax.app/
├── model/
│   ├── entities/        # Client, Voiture, Reservation, Paiement, Reclamation, Employe, Utilisateur
│   ├── dao/             # VoitureDao, ClientDao, ReservationDao, PaiementDao, ReclamationDao...
│   ├── database/        # AppDatabase (Room - singleton)
│   └── sync/            # SyncManager (Firestore ↔ Room via flag synced)
├── view/
│   ├── auth/            # SplashActivity, LoginActivity, RegisterActivity, SessionManager
│   ├── admin/           # Dashboard, Stats, Clients, Voitures, Reservations, Contrats, Securite...
│   ├── employe/         # Dashboard Employé, Tâches, Réservations, Paiements
│   └── client/          # Dashboard Client, Catalogue, Réservations, Contrats, Paiements
└── utils/               # ContratPdfHelper, ContratSignatureUtil, ContratSignatureSteps, Constants
```

---

## Rôles et fonctionnalités

### 👤 Client
- Inscription / Connexion Firebase Auth
- Catalogue véhicules disponibles
- Nouvelle réservation avec sélection de dates
- Timeline visuelle de progression de réservation
- Signature numérique du contrat (étape 2)
- Paiement simulé avec accusé de réception (référence AR-DM-…)
- Mes paiements, mes réclamations, mon profil

### 👷 Employé
- Dashboard avec statistiques en temps réel
- Tâches du jour avec priorité (haute / moyenne / basse)
- Gestion réservations : confirmation, changement de statut
- Génération et validation des contrats
- Signature employé (étape 1 du contrat)
- Gestion voitures et clients (ajout / modification / suppression)
- Accès liste des paiements et accusés

### 🔑 Admin
- Dashboard complet avec KPI et insights business automatiques
- Statistiques avancées avec filtres période (Aujourd'hui / 7j / 30j)
- Centre de sécurité (score /100, alertes, recommandations)
- Santé flotte (maintenance prédictive, alertes kilométrage)
- Intelligence business (insights automatiques depuis Room)
- Gestion complète : clients, voitures, réservations, contrats, paiements, employés
- Anti brute-force login (blocage 2 min après 5 échecs)

---

## Flux métier

### Réservation
```
EN_ATTENTE → CONFIRMEE → EN_COURS → TERMINEE / ANNULEE
```

### Contrat (double signature)
```
1. Employé signe   → statut : ATTENTE_CLIENT
2. Client signe    → statut : COMPLET
3. PDF généré      → partageable / imprimable
```

### Paiement
```
Client clique "Payer" → Paiement créé (statut : PAYE)
→ Référence AR-DM-xxxx générée
→ Accusé enregistré dans Room
→ Upload Firestore
→ Visible Admin + Employé
```

### Statut Voiture
```
DISPONIBLE → LOUEE      (confirmation réservation)
LOUEE      → DISPONIBLE (retour du véhicule)
```

---

## Stockage des données

Les données sont stockées à **trois niveaux** :

```
┌─────────────────────────────────────────────────────────┐
│  Téléphone (local)                                      │
│                                                         │
│  ┌─────────────────┐    ┌──────────────────────────┐   │
│  │  Room (SQLite)  │    │  SharedPreferences       │   │
│  │  drivemax_db    │    │  Session, thème, tâches  │   │
│  └────────┬────────┘    └──────────────────────────┘   │
│           │ synced = false                              │
└───────────┼─────────────────────────────────────────────┘
            │ SyncManager (upload / download)
            ▼
┌─────────────────────────────────────────────────────────┐
│  Firebase Firestore (cloud)                             │
│  Collections : clients, voitures, reservations...      │
└─────────────────────────────────────────────────────────┘
```

- **Hors ligne** : l'app lit Room et fonctionne normalement
- **En ligne** : SyncManager synchronise automatiquement vers Firestore
- **Chemin SQLite** : `/data/data/com.drivemax.app/databases/drivemax_db`

---

## Base de données Room — Tables complètes

### Table `voitures`

| Colonne | Type Room | Description |
|---|---|---|
| `id` | TEXT — PrimaryKey | UUID unique |
| `marque` | TEXT | Marque du véhicule |
| `modele` | TEXT | Modèle du véhicule |
| `annee` | INTEGER | Année de fabrication |
| `immatriculation` | TEXT | Plaque d'immatriculation |
| `couleur` | TEXT | Couleur |
| `prix_journalier` | REAL | Prix par jour (MAD) |
| `statut` | TEXT | `DISPONIBLE` / `LOUEE` / `MAINTENANCE` |
| `kilometrage` | INTEGER | Kilométrage actuel |
| `carburant` | TEXT | `ESSENCE` / `DIESEL` / `ELECTRIQUE` / `HYBRIDE` |
| `transmission` | TEXT | `MANUELLE` / `AUTOMATIQUE` |
| `nombre_places` | INTEGER | Nombre de places |
| `image_url` | TEXT | URL photo du véhicule |
| `latitude` | REAL | Position GPS latitude |
| `longitude` | REAL | Position GPS longitude |
| `description` | TEXT | Description libre |
| `synced` | INTEGER (bool) | Flag synchronisation Firestore |

---

### Table `clients`

| Colonne | Type Room | Description |
|---|---|---|
| `id` | TEXT — PrimaryKey | UUID unique |
| `nom` | TEXT | Nom de famille |
| `prenom` | TEXT | Prénom |
| `email` | TEXT | Adresse email |
| `telephone` | TEXT | Numéro de téléphone |
| `cin` | TEXT | Carte d'identité nationale |
| `permis_numero` | TEXT | Numéro de permis de conduire |
| `adresse` | TEXT | Adresse postale |
| `date_naissance` | TEXT | Date de naissance |
| `fcm_token` | TEXT | Token Firebase Messaging |
| `date_creation` | INTEGER | Timestamp création compte |
| `synced` | INTEGER (bool) | Flag synchronisation Firestore |

---

### Table `reservations`

| Colonne | Type Room | Description |
|---|---|---|
| `id` | TEXT — PrimaryKey | UUID unique |
| `client_id` | TEXT | Référence → `clients.id` |
| `voiture_id` | TEXT | Référence → `voitures.id` |
| `date_debut` | INTEGER | Timestamp début location |
| `date_fin` | INTEGER | Timestamp fin location |
| `prix_total` | REAL | Montant total (MAD) |
| `statut` | TEXT | `EN_ATTENTE` / `CONFIRMEE` / `EN_COURS` / `TERMINEE` / `ANNULEE` |
| `lieu_prise_en_charge` | TEXT | Lieu de prise en charge |
| `lieu_retour` | TEXT | Lieu de retour |
| `notes` | TEXT | Notes libres |
| `contrat_pdf_url` | TEXT | Chemin local du PDF généré |
| `contrat_etape_sign` | TEXT | `ATTENTE_CLIENT` / `COMPLET` |
| `sig_employe_b64` | TEXT | Signature employé (Base64 PNG) |
| `sig_client_b64` | TEXT | Signature client (Base64 PNG) |
| `date_creation` | INTEGER | Timestamp de création |
| `alerte_envoyee` | INTEGER (bool) | Notification push envoyée |
| `mode_paiement` | TEXT | Mode de paiement utilisé |
| `synced` | INTEGER (bool) | Flag synchronisation Firestore |

---

### Table `paiements`

| Colonne | Type Room | Description |
|---|---|---|
| `id` | TEXT — PrimaryKey | UUID unique |
| `reservation_id` | TEXT | Référence → `reservations.id` |
| `client_id` | TEXT | Référence → `clients.id` |
| `montant` | REAL | Montant payé (MAD) |
| `statut` | TEXT | `PAYE` / `EN_ATTENTE` / `REMBOURSE` |
| `methode` | TEXT | `Carte (simulation)` / `Especes` / `Virement` |
| `reference` | TEXT | Référence accusé (ex: `AR-DM-xxxx`) |
| `notes` | TEXT | Détails accusé électronique |
| `date_paiement` | INTEGER | Timestamp du paiement |
| `synced` | INTEGER (bool) | Flag synchronisation Firestore |

---

### Table `reclamations`

| Colonne | Type Room | Description |
|---|---|---|
| `id` | TEXT — PrimaryKey | UUID unique |
| `client_id` | TEXT | Référence → `clients.id` |
| `reservation_id` | TEXT | Référence → `reservations.id` |
| `sujet` | TEXT | Sujet de la réclamation |
| `description` | TEXT | Description détaillée |
| `statut` | TEXT | `EN_ATTENTE` / `EN_COURS` / `TRAITE` / `FERME` |
| `date_creation` | INTEGER | Timestamp de création |
| `date_traitement` | INTEGER | Timestamp de traitement |
| `synced` | INTEGER (bool) | Flag synchronisation Firestore |

---

### Table `employes`

| Colonne | Type Room | Description |
|---|---|---|
| `id` | TEXT — PrimaryKey | UUID unique |
| `nom` | TEXT | Nom de famille |
| `prenom` | TEXT | Prénom |
| `email` | TEXT | Adresse email |
| `telephone` | TEXT | Numéro de téléphone |
| `poste` | TEXT | Poste occupé |
| `date_embauche` | TEXT | Date d'embauche |
| `actif` | INTEGER (bool) | Compte actif ou non |
| `synced` | INTEGER (bool) | Flag synchronisation Firestore |

---

### Table `utilisateurs`

| Colonne | Type Room | Description |
|---|---|---|
| `id` | TEXT — PrimaryKey | UID Firebase Auth |
| `email` | TEXT | Adresse email |
| `nom` | TEXT | Nom complet affiché |
| `role` | TEXT | `admin` / `employe` / `client` |
| `fcm_token` | TEXT | Token notifications push |
| `date_creation` | INTEGER | Timestamp création compte |
| `synced` | INTEGER (bool) | Flag synchronisation Firestore |

---

## Firebase Firestore — Collections

| Collection | Correspond à | Description |
|---|---|---|
| `utilisateurs` | Table `utilisateurs` | Profils et rôles |
| `clients` | Table `clients` | Données clients |
| `voitures` | Table `voitures` | Catalogue véhicules |
| `reservations` | Table `reservations` | Toutes les réservations |
| `paiements` | Table `paiements` | Historique paiements |
| `reclamations` | Table `reclamations` | Réclamations clients |
| `employes` | Table `employes` | Données employés |

> **Mécanisme de sync** : chaque entité a un flag `synced`. Quand `synced = false`, le `SyncManager` upload vers Firestore puis passe `synced = true`.

---

## SharedPreferences

| Clé | Type | Description |
|---|---|---|
| `is_logged_in` | boolean | Session active |
| `user_id` | String | UID utilisateur connecté |
| `user_email` | String | Email utilisateur |
| `user_role` | String | `admin` / `employe` / `client` |
| `user_nom` | String | Nom affiché dans le dashboard |
| `theme_dark` | boolean | Mode sombre activé |
| `employe_tasks_{userId}` | String (JSON) | Tâches du jour employé |
| `login_fail_count` | int | Nombre d'échecs de connexion |
| `login_last_fail` | long | Timestamp dernier échec |
| `login_last_success` | long | Timestamp dernière connexion réussie |

---

## Installation

### Prérequis
- Android Studio Hedgehog ou supérieur
- JDK 17
- Compte Firebase (projet configuré)

### Étapes

**1. Cloner le projet**
```bash
git clone https://github.com/votre-repo/drivemax.git
cd drivemax
```

**2. Ouvrir dans Android Studio**
```
File → Open → sélectionner le dossier DriveMax
```

**3. Ajouter google-services.json**
```
Placer le fichier dans : app/google-services.json
```

**4. Builder et lancer**
```
Build → Make Project
Run → Run 'app'
```

---

## Configuration Firebase

### Règles Firestore recommandées (développement)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

---

## Sécurité

- **Anti brute-force** : blocage 2 min après 5 échecs de connexion admin
- **Rôles stricts** : chaque écran vérifie le rôle via `SessionManager`
- **Données locales** : `/data/data/com.drivemax.app/databases/drivemax_db` (accès privé)
- **Sync sécurisée** : flag `synced` sur chaque entité
- **Firebase Auth** : authentification obligatoire pour tout accès cloud
- **Centre de sécurité** : score /100 avec alertes et recommandations

---

**Version** : 1.0.0  
**Package** : `com.drivemax.app`  
**Min SDK** : 26 (Android 8.0)  
**Target SDK** : 34 (Android 14)  
**Base de données** : `drivemax_db` (Room / SQLite)
