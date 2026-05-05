# DriveMax 

Application Android de gestion de location de véhicules avec trois rôles (Admin, Employé, Client), base locale Room, synchronisation Firebase Firestore, contrats PDF avec double signature et paiement simulé.

---

## Table des matières

1. [Présentation](#présentation)
2. [Technologies utilisées](#technologies-utilisées)
3. [Architecture du projet](#architecture-du-projet)
4. [Rôles et fonctionnalités](#rôles-et-fonctionnalités)
5. [Flux métier](#flux-métier)
6. [Installation](#installation)
7. [Configuration Firebase](#configuration-firebase)
8. [Structure des données](#structure-des-données)
9. [Sécurité](#sécurité)


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
| Firebase Messaging | Notifications push |
| ViewBinding / Material | Interface utilisateur |
| RecyclerView + DiffUtil | Listes performantes |
| ML Kit (OCR) | Scan CIN sur contrat |

---

## Architecture du projet

```
com.drivemax.app/
├── model/
│   ├── entities/        # Client, Voiture, Reservation, Paiement, Reclamation...
│   ├── dao/             # VoitureDao, ClientDao, ReservationDao, PaiementDao...
│   ├── database/        # AppDatabase (Room)
│   └── sync/            # SyncManager (Firestore ↔ Room)
├── view/
│   ├── auth/            # SplashActivity, LoginActivity, RegisterActivity, SessionManager
│   ├── admin/           # Dashboard, Stats, Clients, Voitures, Reservations, Contrats...
│   ├── employe/         # Dashboard Employé, Tâches, Réservations, Paiements
│   └── client/          # Dashboard Client, Catalogue, Réservations, Contrats, Paiements
└── utils/               # ContratPdfHelper, ContratSignatureUtil, Constants...
```

---

## Rôles et fonctionnalités

### 👤 Client
- Inscription / Connexion Firebase Auth
- Catalogue véhicules disponibles
- Nouvelle réservation avec sélection de dates
- Timeline visuelle de progression de réservation
- Signature numérique du contrat
- Paiement simulé avec accusé de réception (référence AR-DM-…)
- Mes paiements, mes réclamations, mon profil

### 👷 Employé
- Dashboard avec statistiques en temps réel
- Tâches du jour (priorité haute / moyenne / basse)
- Gestion réservations : confirmation, statut
- Génération et validation des contrats
- Signature employé (étape 1 du contrat)
- Gestion voitures et clients
- Accès liste des paiements et accusés

### 🔑 Admin
- Dashboard complet avec KPI et insights business automatiques
- Statistiques avancées avec filtres période (Aujourd'hui / 7j / 30j)
  - Revenu mensuel, panier moyen, taux de conversion, taux d'annulation
  - Top véhicule le plus réservé et le plus rentable
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
Client clique "Payer" → Paiement créé (PAYE)
→ Référence AR-DM-xxxx générée
→ Accusé enregistré en Room
→ Upload Firestore
→ Visible Admin + Employé
```

### Statut Voiture
```
DISPONIBLE → LOUEE  (à la confirmation de réservation)
LOUEE → DISPONIBLE  (après retour du véhicule)
```

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

### Firestore — Collections utilisées
| Collection | Description |
|---|---|
| `utilisateurs` | Profils clients / employés / admin |
| `reservations` | Toutes les réservations |
| `voitures` | Catalogue véhicules |
| `clients` | Données clients |
| `paiements` | Historique paiements |
| `reclamations` | Réclamations clients |

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

## Structure des données

### Entité Voiture
| Champ | Type | Description |
|---|---|---|
| id | String (UUID) | Identifiant unique |
| marque | String | Marque du véhicule |
| modele | String | Modèle |
| annee | int | Année de fabrication |
| immatriculation | String | Plaque d'immatriculation |
| prixJournalier | double | Prix par jour (MAD) |
| statut | String | DISPONIBLE / LOUEE / MAINTENANCE |
| kilometrage | int | Kilométrage actuel |

### Entité Reservation
| Champ | Type | Description |
|---|---|---|
| id | String (UUID) | Identifiant unique |
| clientId | String | Référence client |
| voitureId | String | Référence voiture |
| dateDebut | long | Timestamp début |
| dateFin | long | Timestamp fin |
| prixTotal | double | Montant total (MAD) |
| statut | String | EN_ATTENTE / CONFIRMEE / EN_COURS / TERMINEE / ANNULEE |

---

## Sécurité

- **Anti brute-force** : blocage temporaire 2 minutes après 5 échecs de connexion admin
- **Rôles stricts** : chaque écran vérifie le rôle via SessionManager
- **Données locales** : stockées dans `/data/data/com.drivemax.app/databases/drivemax_db` (accès privé)
- **Sync sécurisée** : flag `synced` sur chaque entité pour éviter les doublons
- **Firebase Auth** : authentification obligatoire pour tout accès cloud

---



**Version** : 1.0.0  
**Package** : `com.drivemax.app`  
**Min SDK** : 26 (Android 8.0)  
**Target SDK** : 34 (Android 14)
