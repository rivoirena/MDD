# MDD - Back-end

API REST du projet **MDD (Monde de Dév)**, un réseau social destiné aux développeurs : abonnement à des thèmes, publication d'articles, commentaires.

Développée avec Spring Boot et MySQL.

## Prérequis

- [Java](https://adoptium.net/) **25**
- [MySQL](https://www.mysql.com/) **8.0** en cours d'exécution
- Maven : non requis, le wrapper `mvnw` est fourni dans le dépôt

```bash
java -version
mysql --version
```

## Installation

```bash
# 1. Cloner le dépôt
git clone https://github.com/rivoirena/MDD.git
cd MDD/back
```

### Base de données

1. Démarrer le serveur MySQL.
2. Créer la base `mdd` et ses tables avec le script d'initialisation :

```bash
mysql -u <utilisateur> -p < src/main/resources/initSQL.sql
```

> Le script `initSQL.sql` crée le schéma de la base de données.

## Configuration

La configuration se trouve dans `src/main/resources/application.properties` :

```properties
spring.application.name=MDD

spring.datasource.url=jdbc:mysql://localhost:3306/mdd
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.expiration-ms=86400000
```

Les informations sensibles ne sont **pas** écrites en dur : elles sont lues depuis des **variables d'environnement** à définir avant de lancer l'application.

| Variable      | Description                                   |
|---------------|-----------------------------------------------|
| `DB_USERNAME` | Utilisateur MySQL                             |
| `DB_PASSWORD` | Mot de passe MySQL                            |
| `JWT_SECRET`  | Clé secrète de signature des JWT              |

La durée de validité du token est définie par `jwt.expiration-ms` (86 400 000 ms, soit 24 h).

> ⚠️ Si `JWT_SECRET` fait moins de 32 caractères, l'application refuse de démarrer
> (la clé HMAC est trop courte).
### Définir les variables d'environnement

**Windows (PowerShell)**, pour la session courante :

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="mon_mot_de_passe"
$env:JWT_SECRET="une_cle_secrete_d_au_moins_32_caracteres"
```

Pour les rendre permanentes : *Paramètres système avancés > Variables d'environnement*, ou `setx DB_USERNAME "root"` (puis rouvrir le terminal).

**Linux / macOS :**

```bash
export DB_USERNAME=root
export DB_PASSWORD=mon_mot_de_passe
export JWT_SECRET=ma_cle_secrete
```

## Lancement

### Serveur de développement

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

L'API est accessible sur [http://localhost:8080/api](http://localhost:8080/api).

### Build

```bash
./mvnw clean package
java -jar target/mdd-0.0.1-SNAPSHOT.jar
```

## Authentification

L'authentification repose sur **Spring Security** et des **JWT** (bibliothèque `jjwt`).

1. L'utilisateur s'inscrit (`/api/auth/register`) ou se connecte (`/api/auth/login`) et reçoit un token.
2. Le token doit ensuite être envoyé dans le header `Authorization: Bearer <token>` pour accéder aux routes protégées.

Les routes `/api/auth/**` sont publiques ; toutes les autres renvoient `401` sans token valide.

### CORS

Seule l'origine `http://localhost:4200` (front Angular) est autorisée. Cette valeur est
codée en dur dans `WebSecurityConfig#corsConfigurationSource()` : elle est à modifier si le
front est servi depuis une autre adresse.
## Endpoints de l'API

Toutes les routes sont préfixées par `/api`. Sauf mention contraire, elles nécessitent d'être authentifié.

### Authentification

| Méthode | Route                | Description                           | Corps de la requête |
|---------|----------------------|---------------------------------------|---------------------|
| POST    | `/api/auth/register` | Créer un compte (retourne un JWT)     | `RegisterRequest`   |
| POST    | `/api/auth/login`    | Se connecter (retourne un JWT)        | `LoginRequest`      |

### Utilisateur

| Méthode | Route           | Description                                                  | Corps de la requête    |
|---------|-----------------|--------------------------------------------------------------|------------------------|
| GET     | `/api/users/me` | Récupérer le profil de l'utilisateur connecté                | -                      |
| PUT     | `/api/users/me` | Modifier le profil (retourne un nouveau JWT)                 | `UpdateProfileRequest` |

### Thèmes et abonnements

| Méthode | Route                                    | Description                                    |
|---------|------------------------------------------|------------------------------------------------|
| GET     | `/api/themes`                            | Lister tous les thèmes                         |
| GET     | `/api/users/me/subscriptions`            | Lister les thèmes auxquels je suis abonné      |
| POST    | `/api/users/me/subscriptions/{themeId}`  | S'abonner à un thème                           |
| DELETE  | `/api/users/me/subscriptions/{themeId}`  | Se désabonner d'un thème                       |

### Articles

| Méthode | Route                | Description                                                                 | Corps de la requête    |
|---------|----------------------|-----------------------------------------------------------------------------|------------------------|
| GET     | `/api/articles`      | Lister les articles des thèmes suivis. Paramètre `sort` : `asc` ou `desc` (défaut `desc`) | -   |
| GET     | `/api/articles/{id}` | Récupérer un article                                                        | -                      |
| POST    | `/api/articles`      | Créer un article                                                            | `CreateArticleRequest` |

### Commentaires

| Méthode | Route                                | Description                          | Corps de la requête    |
|---------|--------------------------------------|--------------------------------------|------------------------|
| GET     | `/api/articles/{articleId}/comments` | Lister les commentaires d'un article | -                      |
| POST    | `/api/articles/{articleId}/comments` | Ajouter un commentaire               | `CreateCommentRequest` |

## Stack technique

- [Spring Boot](https://spring.io/projects/spring-boot) 4.1.1
- Spring Data JPA (persistance)
- Spring Security (authentification et autorisation)
- Spring Validation (validation des données)
- Spring Web MVC (API REST)
- [MySQL](https://www.mysql.com/) 8.0 via `mysql-connector-j`
- [jjwt](https://github.com/jwtk/jjwt) 0.12.6 (JWT)
- [MapStruct](https://mapstruct.org/) 1.6.3 (mapping entités / DTO)
- [Lombok](https://projectlombok.org/) (réduction du code répétitif)
- Java 25

## Structure du projet

```
src/main/
├── java/com/mdd/
│   ├── controllers/       # Contrôleurs REST
│   ├── dto/               # Objets de transfert de données
│   ├── exception/         # Exceptions personnalisées et gestion des erreurs
│   ├── mapper/            # Mappers MapStruct (entité <-> DTO)
│   ├── models/            # Entités JPA
│   ├── payload/
│   │   ├── request/       # Corps des requêtes entrantes
│   │   └── response/      # Corps des réponses sortantes
│   ├── repository/        # Repositories Spring Data JPA
│   ├── security/          # Configuration Spring Security et JWT
│   └── services/          # Logique métier
└── resources/
    ├── application.properties
    └── initSQL.sql        # Script d'initialisation de la base
```

## Javadoc

Le code source (contrôleurs, services, repositories, mappers, entités, DTO, sécurité) est documenté en Javadoc.

### Générer la documentation

Depuis le dossier `back` :

```bash
# Windows
.\mvnw.cmd javadoc:javadoc

# Linux / macOS
./mvnw javadoc:javadoc
```

### Consulter la documentation

La documentation est générée dans `target/reports/apidocs/` (ou `target/site/apidocs/` selon la version du plugin Maven Javadoc). Ouvrir le fichier `index.html` dans un navigateur :

```bash
# Windows
start target/reports/apidocs/index.html

# macOS
open target/reports/apidocs/index.html

# Linux
xdg-open target/reports/apidocs/index.html
```

Le dossier `target/` est régénéré à chaque build : il n'est pas versionné et la documentation doit être générée localement.

## Tests

_Section à compléter._