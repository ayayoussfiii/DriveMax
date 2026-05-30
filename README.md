# DriveMax

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Firebase-Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
  <img src="https://img.shields.io/badge/Room-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white"/>
  <img src="https://img.shields.io/badge/Min_SDK-26-brightgreen?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Version-1.0.0-blue?style=for-the-badge"/>
</p>

<p align="center">
  Application Android complète de gestion de location de véhicules.<br/>
  Trois rôles · Base locale Room · Sync Firestore · Contrats PDF double signature · Paiement simulé
</p>

---

## Ce que fait DriveMax

DriveMax couvre l'intégralité du cycle de vie d'une agence de location :

```
Client réserve → Employé confirme & signe → Client signe → PDF généré → Paiement → Accusé AR-DM-xxxx
```

| Rôle | Accès |
|---|---|
|  **Client** | Catalogue, réservation, signature contrat, paiement, réclamations |
|  **Employé** | Tâches du jour, confirmation réservations, signature contrat étape 1 |
|  **Admin** | KPI, statistiques, sécurité, santé flotte, gestion complète |

---

##  Fonctionnalités clés

- **Double signature numérique** — employé (étape 1) puis client (étape 2), PDF généré automatiquement
- **Mode hors ligne** — Room/SQLite local + sync automatique Firestore au retour de connexion
- **Notifications push** — FCM intégré, alertes de réservation en temps réel
- **OCR ML Kit** — scan CIN directement depuis la caméra
- **Anti brute-force** — blocage 2 min après 5 échecs de connexion
- **Centre de sécurité** — score /100 avec alertes et recommandations (Admin)
- **Maintenance prédictive** — alertes kilométrage et santé flotte (Admin)
- **Paiement simulé** — référence `AR-DM-xxxx`, accusé enregistré et synchronisé

---

## 🛠️ Stack technique

| Technologie | Usage |
|---|---|
| Java | Langage principal |
| Android SDK 26 → 34 | Plateforme mobile |
| Room 2.6 (SQLite) | Base de données locale |
| Firebase Firestore | Synchronisation cloud |
| Firebase Auth | Authentification |
| Firebase Messaging (FCM) | Notifications push |
| ML Kit (OCR) | Scan CIN |
| ViewBinding / Material 3 | Interface utilisateur |
| RecyclerView + DiffUtil | Listes performantes |
| ContratPdfHelper | Génération PDF contrats |

---

## 🏗️ Architecture

```
com.drivemax.app/
├── model/
│   ├── entities/     # Client, Voiture, Reservation, Paiement, Reclamation, Employe, Utilisateur
│   ├── dao/          # VoitureDao, ClientDao, ReservationDao, PaiementDao, ReclamationDao...
│   ├── database/     # AppDatabase (Room singleton)
│   └── sync/         # SyncManager (Firestore ↔ Room via flag synced)
├── view/
│   ├── auth/         # SplashActivity, LoginActivity, RegisterActivity, SessionManager
│   ├── admin/        # Dashboard, Stats, Clients, Voitures, Réservations, Contrats, Sécurité
│   ├── employe/      # Dashboard, Tâches, Réservations, Paiements
│   └── client/       # Dashboard, Catalogue, Réservations, Contrats, Paiements
└── utils/            # ContratPdfHelper, ContratSignatureUtil, Constants
```

---

## 🔄 Flux métier

### Réservation
```
EN_ATTENTE → CONFIRMEE → EN_COURS → TERMINEE
                                  ↘ ANNULEE
```

### Contrat (double signature)
```
Employé signe  →  ATTENTE_CLIENT
Client signe   →  COMPLET
PDF généré     →  partageable / imprimable
```

### Paiement
```
Client "Payer" → PAYE → AR-DM-xxxx → Room → Firestore → visible Admin + Employé
```

### Statut véhicule
```
DISPONIBLE ──(confirmation)──► LOUEE ──(retour)──► DISPONIBLE
```

---

## 💾 Stockage des données

```
┌─────────────────────────────────────────────────────────┐
│  Téléphone (local)                                      │
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
│  clients · voitures · reservations · paiements · ...   │
└─────────────────────────────────────────────────────────┘
```

> **Hors ligne** : l'app lit Room et fonctionne normalement.  
> **En ligne** : SyncManager synchronise automatiquement les entités avec `synced = false`.

---

## 🗄️ Base de données Room — Tables

<details>
<summary><strong>Table voitures</strong></summary>

| Colonne | Type | Description |
|---|---|---|
| id | TEXT PK | UUID unique |
| marque | TEXT | Marque du véhicule |
| modele | TEXT | Modèle |
| annee | INTEGER | Année de fabrication |
| immatriculation | TEXT | Plaque |
| couleur | TEXT | Couleur |
| prix_journalier | REAL | Prix/jour (MAD) |
| statut | TEXT | DISPONIBLE / LOUEE / MAINTENANCE |
| kilometrage | INTEGER | Kilométrage actuel |
| carburant | TEXT | ESSENCE / DIESEL / ELECTRIQUE / HYBRIDE |
| transmission | TEXT | MANUELLE / AUTOMATIQUE |
| nombre_places | INTEGER | Nombre de places |
| image_url | TEXT | URL photo |
| latitude / longitude | REAL | Position GPS |
| description | TEXT | Description libre |
| synced | INTEGER | Flag sync Firestore |

</details>

<details>
<summary><strong>Table clients</strong></summary>

| Colonne | Type | Description |
|---|---|---|
| id | TEXT PK | UUID unique |
| nom / prenom | TEXT | Identité |
| email | TEXT | Email |
| telephone | TEXT | Téléphone |
| cin | TEXT | Carte d'identité nationale |
| permis_numero | TEXT | Numéro de permis |
| adresse | TEXT | Adresse postale |
| date_naissance | TEXT | Date de naissance |
| fcm_token | TEXT | Token Firebase Messaging |
| date_creation | INTEGER | Timestamp création |
| synced | INTEGER | Flag sync Firestore |

</details>

<details>
<summary><strong>Table reservations</strong></summary>

| Colonne | Type | Description |
|---|---|---|
| id | TEXT PK | UUID unique |
| client_id | TEXT | → clients.id |
| voiture_id | TEXT | → voitures.id |
| date_debut / date_fin | INTEGER | Timestamps location |
| prix_total | REAL | Montant total (MAD) |
| statut | TEXT | EN_ATTENTE / CONFIRMEE / EN_COURS / TERMINEE / ANNULEE |
| lieu_prise_en_charge | TEXT | Lieu de prise en charge |
| lieu_retour | TEXT | Lieu de retour |
| contrat_pdf_url | TEXT | Chemin local PDF |
| contrat_etape_sign | TEXT | ATTENTE_CLIENT / COMPLET |
| sig_employe_b64 | TEXT | Signature employé (Base64) |
| sig_client_b64 | TEXT | Signature client (Base64) |
| alerte_envoyee | INTEGER | Notification envoyée |
| mode_paiement | TEXT | Mode de paiement |
| synced | INTEGER | Flag sync Firestore |

</details>

<details>
<summary><strong>Tables paiements · reclamations · employes · utilisateurs</strong></summary>

**paiements** — `id, reservation_id, client_id, montant, statut, methode, reference (AR-DM-xxxx), notes, date_paiement, synced`

**reclamations** — `id, client_id, reservation_id, sujet, description, statut (EN_ATTENTE/EN_COURS/TRAITE/FERME), date_creation, date_traitement, synced`

**employes** — `id, nom, prenom, email, telephone, poste, date_embauche, actif, synced`

**utilisateurs** — `id (UID Firebase), email, nom, role (admin/employe/client), fcm_token, date_creation, synced`

</details>

---

## ☁️ Firebase Firestore — Collections

| Collection | Description |
|---|---|
| `utilisateurs` | Profils et rôles |
| `clients` | Données clients |
| `voitures` | Catalogue véhicules |
| `reservations` | Toutes les réservations |
| `paiements` | Historique paiements |
| `reclamations` | Réclamations clients |
| `employes` | Données employés |

---

## 🚀 Installation

### Prérequis
- Android Studio Hedgehog ou supérieur
- JDK 17
- Compte Firebase (projet configuré)

### Étapes

```bash
# 1. Cloner le projet
git clone https://github.com/votre-repo/drivemax.git
cd drivemax
```

```
# 2. Ouvrir dans Android Studio
File → Open → sélectionner le dossier DriveMax

# 3. Ajouter google-services.json
Placer le fichier dans : app/google-services.json

# 4. Builder et lancer
Build → Make Project
Run → Run 'app'
```

### Règles Firestore (développement)

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

## 🔒 Sécurité

| Mesure | Détail |
|---|---|
| Anti brute-force | Blocage 2 min après 5 échecs de connexion |
| Rôles stricts | Chaque écran vérifie le rôle via `SessionManager` |
| Données locales | `/data/data/com.drivemax.app/databases/drivemax_db` (accès privé) |
| Sync sécurisée | Flag `synced` sur chaque entité |
| Firebase Auth | Authentification obligatoire pour tout accès cloud |
| Centre de sécurité | Score /100 avec alertes et recommandations (Admin) |

---

## 📋 Informations projet

| Champ | Valeur |
|---|---|
| Version | 1.0.0 |
| Package | `com.drivemax.app` |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 34 (Android 14) |
| Base de données | `drivemax_db` (Room / SQLite) |

---

<p align="center">Made with ❤️ · DriveMax v1.0.0</p>
