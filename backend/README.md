# Experts Voyages API

Backend Spring Boot 3.5, Java 21, Maven et MySQL. La base s’appelle `expert_voyages`.

## Préparer la base

Dans MySQL, crée d’abord la base :

```sql
CREATE DATABASE expert_voyages CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

La configuration locale utilise `root` sans mot de passe, comme indiqué dans `backend/.env`. Depuis le dossier `backend`, démarre l’API :

```sh
mvn spring-boot:run
```

Au démarrage, Hibernate crée les tables à partir des entités dans `model`. Aucune ville, aucun bus, aucune caravane, aucun tarif et aucun siège n’est préinséré.

Si aucun compte ne porte l’adresse `admin@gmail.com`, `config/DatabaseSeeder.java` crée `Mouhamed Ngom` avec le mot de passe `admin`, enregistré avec BCrypt. Les données du compte sont représentées par `model/Admin.java`.

Le modèle de tarif stocke le lien Wave par destination. Le lien correspondant est renvoyé avec les destinations et la réponse de réservation.

## Authentification administrateur

- `POST /api/auth/admin/login` reçoit `{"email":"...","motDePasse":"..."}` et renvoie un access token JWT et un refresh token JWT.
- `POST /api/auth/refresh` reçoit `{"refreshToken":"..."}`. Le refresh token est à usage unique et est remplacé à chaque renouvellement.
- `POST /api/auth/logout` reçoit le refresh token et le révoque.
- Les routes sous `/api/admin/**` exigent `Authorization: Bearer <accessToken>`.

Les access tokens expirent après 15 minutes et les refresh tokens après 7 jours par défaut. Le fichier `.env` contient une clé `JWT_SECRET` aléatoire locale ; elle ne doit pas être publiée. L’origine CORS se configure avec `FRONTEND_ORIGIN`.

L’API écoute sur `http://localhost:8080`. Le frontend Vite peut l’appeler avec `VITE_API_URL=http://localhost:8080`.

Les clients n’ont pas besoin de compte. Wave, les tickets PDF/PNG et les routes protégées de l’administration restent à intégrer.
