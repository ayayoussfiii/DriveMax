#  DriveMax — Application de Location de Voitures

Application Android de gestion de location de voitures, développée avec **Java**, **Firebase** et **Room Database**.

---

##  Fonctionnalités

###  Client
- Inscription / Connexion
- Catalogue de voitures disponibles
- Nouvelle réservation avec sélection de dates
- Suivi de ses réservations et paiements
- Gestion du profil

### Admin / Employé
- Dashboard de gestion
- Gestion des voitures (ajout, modification, suppression)
- Gestion des clients
- Suivi de toutes les réservations
- Génération de contrats PDF
- Carte GPS des véhicules
- Statistiques et historique des paiements

---

##  Architecture

Le projet suit le pattern **MVC (Model-View-Controller)** :

```
com.drivemax.app/
├── model/
│   ├── entities/        # Voiture, Client, Reservation, Paiement, Utilisateur
│   ├── dao/             # Interfaces Room DAO
│   ├── database/        # AppDatabase (Room)
│   └── sync/            # SyncManager, ConnectivityReceiver
├── view/
│   ├── admin/           # Activities côté admin
│   ├── client/          # Activities côté client
│   ├── auth/            # Login, Register, Splash, SessionManager
│   └── adapter/         # RecyclerView Adapters
├── controller/
│   └── AuthController   # Logique d'authentification
├── notification/        # Firebase Messaging
└── utils/               # PdfGenerator, DateUtils, Constants
```

---

##  Stack technique

| Technologie | Usage |
|---|---|
| Java | Langage principal |
| Firebase Auth | Authentification |
| Firebase Firestore | Base de données cloud |
| Room (SQLite) | Base de données locale |
| Firebase Cloud Messaging | Notifications push |
| ViewBinding | Liaison des vues |
| Google Maps SDK | Carte GPS des véhicules |

---

## Installation

1. Clone le repo :
```bash
git clone https://github.com/ayayoussfiii/DriveMax.git
```

2. Ouvre le projet dans **Android Studio**

3. Ajoute ton fichier `google-services.json` dans le dossier `app/` (Firebase)

4. Build & Run sur un émulateur ou appareil Android (API 24+)

---

##  Structure des rôles

| Rôle | Accès |
|---|---|
| `CLIENT` | Catalogue, réservations, profil, paiements |
| `EMPLOYE` | Dashboard admin complet |
| `ADMIN` | Dashboard admin complet |

---



**Aya YOUSSFI** — 
[GitHub](https://github.com/ayayoussfiii) · [Portfolio](http://aya-portfolio-theta.vercel.app) · [LinkedIn](https://www.linkedin.com/in/aya-youssfi-)
