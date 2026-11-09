# MDD - Front-end

Application web du projet **MDD (Monde de Dév)**, un réseau social destiné aux développeurs : abonnement à des thèmes, lecture et publication d'articles, commentaires.

Développée avec Angular et Angular Material.

## Prérequis

- [Node.js](https://nodejs.org/) **22 ou supérieur** (minimum requis par Angular 22)
- [npm](https://www.npmjs.com/) 11.4.2 (version définie dans `packageManager`)
- Le **back-end MDD** démarré sur `http://localhost:8080` (voir le README du back)

```bash
node -v
npm -v
```

## Installation

```bash
# 1. Cloner le dépôt
git clone https://github.com/rivoirena/MDD.git
cd MDD/front

# 2. Installer les dépendances
npm install
```

Le Angular CLI est installé en dépendance de développement : pas besoin de l'installer globalement, les commandes `npm run ...` suffisent.

## Configuration

L'URL de l'API du back-end est définie dans `src/environnements/environment.ts` :

```ts
export const environment = {
  apiUrl: 'http://localhost:8080/api'
};
```

Modifier `apiUrl` si votre back-end tourne sur une autre adresse ou un autre port.

## Lancement

### Serveur de développement

```bash
npm start
```

L'application est accessible sur [http://localhost:4200](http://localhost:4200) et se recharge automatiquement à chaque modification.

### Build de production

```bash
npm run build
```

Les fichiers générés sont placés dans le dossier `dist/`.

### Build en mode watch

```bash
npm run watch
```

## Scripts disponibles

| Commande        | Description                                       |
| --------------- | ------------------------------------------------- |
| `npm start`     | Lance le serveur de développement (`ng serve`)    |
| `npm run build` | Compile l'application pour la production          |
| `npm run watch` | Compile en continu en configuration développement |
| `npm test`      | Lance les tests _(section à compléter)_           |

## Authentification

L'authentification repose sur un **JWT stocké dans le `localStorage`** du navigateur. Les routes protégées sont sécurisées par des guards, et un intercepteur HTTP ajoute le token dans le header `Authorization: Bearer <token>` des requêtes envoyées à l'API.

## Utilisation

L'application est **responsive** : elle s'adapte à l'ordinateur, à la tablette et au téléphone. Sur mobile, le menu de navigation est accessible via l'icône ☰ en haut à droite.

### Compte et connexion

- **Créer un compte** : sur la page d'accueil, cliquer sur « S'inscrire », renseigner nom d'utilisateur, e-mail et mot de passe, puis valider.
- **Se connecter** : cliquer sur « Se connecter », saisir son e-mail ou son nom d'utilisateur et son mot de passe.
- **Se déconnecter** : cliquer sur « Se déconnecter » en haut à droite (sur mobile, via le menu ☰).
- **Modifier ses informations** : cliquer sur l'icône de profil en haut à droite, modifier nom d'utilisateur, e-mail ou mot de passe, puis cliquer sur « Sauvegarder ».

### Thèmes et abonnements

- **S'abonner** : dans l'onglet « Thèmes », cliquer sur « S'abonner » sur le thème souhaité. Les thèmes déjà suivis affichent « Déjà abonné » (bouton grisé).
- **Se désabonner** : dans le profil, section « Abonnements », cliquer sur « Se désabonner » sous le thème concerné.
- **Liste de ses abonnements** : visible dans la section « Abonnements » de la page « Profil utilisateur ».

### Articles

- **Consulter la liste** : cliquer sur « Articles » dans le menu. Chaque article affiche son titre, sa date, son auteur et un extrait. Seuls les articles des thèmes auxquels l'utilisateur est abonné sont affichés.
- **Trier** : utiliser l'option « Trier par » en haut à droite de la liste.
- **Consulter un article** : cliquer sur l'article pour accéder à son contenu complet (date, auteur, thème).
- **Créer un article** : depuis la page « Articles », cliquer sur « Créer un article », choisir un thème, saisir le titre et le contenu, puis cliquer sur « Créer ».
- **Commenter** : dans l'article, saisir son message dans le champ « Écrivez ici votre commentaire » puis cliquer sur l'icône d'envoi.

### Navigation

- **Retour** : la flèche ← en haut à gauche permet de revenir à la page précédente.

## Stack technique

- [Angular](https://angular.dev/) 22
- [Angular Material](https://material.angular.dev/) / CDK
- [RxJS](https://rxjs.dev/) 7.8
- [TypeScript](https://www.typescriptlang.org/) 6
- [Prettier](https://prettier.io/) pour le formatage

## Structure du projet

```
src/
├── environments/        # Configuration (URL de l'API)
└── app/
    ├── components/        # Composants réutilisables
    │   ├── article-card/
    │   └── theme-card/
    ├── core/
    │   ├── models/        # Interfaces / modèles de données
    │   └── services/      # Services (appels API, état)
    ├── guards/            # Guards de routing (protection des routes)
    ├── interceptors/      # Intercepteurs HTTP
    ├── layout/            # Layouts de l'application
    │   ├── app-header/
    │   ├── app-layout/    # Layout des pages connectées
    │   ├── public-header/
    │   └── public-layout/ # Layout des pages publiques
    └── pages/             # Pages (une par route)
        ├── article-create/
        ├── article-detail/
        ├── article-list/
        ├── home/
        ├── login/
        ├── me/            # Profil utilisateur
        ├── register/
        └── themes/
```

## Tests

_Section à compléter (tests unitaires avec Jest)._