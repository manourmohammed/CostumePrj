# 🎭 Costume Boutique - Fashion Atelier

Bienvenue dans le projet **Costume Boutique**, une application mobile moderne et élégante conçue pour la gestion et la location de costumes de haute couture. Cette application offre une expérience utilisateur premium avec un design raffiné ("Fashion Atelier") et une gestion complète côté administrateur.

---

## 📸 Aperçu de l'Application

| Flux Principal | Détails du Costume | Gestion Administrateur |
|:---:|:---:|:---:|
| <img src="https://generated-image-url-placeholder/app_feed_mockup.png" width="200" alt="Flux Principal"/> | <img src="https://generated-image-url-placeholder/costume_detail_mockup.png" width="200" alt="Détails"/> | <img src="https://generated-image-url-placeholder/admin_panel_mockup.png" width="200" alt="Admin"/> |

---

## 🚀 Fonctionnalités Clés

### 👤 Pour les Utilisateurs
- **Authentification Sécurisée** : Connexion et inscription avec gestion de session.
- **Galerie de Costumes** : Parcourez une large sélection de costumes avec des images haute résolution.
- **Détails Complets** : Consultez les descriptions, les prix et les disponibilités.
- **Favoris** : Enregistrez vos costumes préférés pour un accès rapide.
- **Profil Utilisateur** : Gérez vos informations personnelles.

### 🛠 Pour les Administrateurs
- **Tableau de Bord de Gestion** : Vue d'ensemble de tout le catalogue.
- **Gestion CRUD** : Ajouter, modifier ou supprimer des costumes via l'application.
- **Téléchargement d'Images** : Intégration directe pour l'ajout d'images de nouveaux costumes.

---

## 🛠 Technologies Utilisées

### 📱 Application Mobile (Android)
- **Langage** : Java
- **UI Framework** : Material Design Components pour une esthétique premium.
- **Réseau** : [Retrofit](https://square.github.io/retrofit/) pour les appels API REST.
- **Chargement d'Images** : [Picasso](https://square.github.io/picasso/) pour une gestion fluide des images et du cache.
- **Analyse JSON** : [Gson](https://github.com/google/gson).
- **Architecture** : Basée sur des activités organisées par fonctionnalités (Auth, Main, Admin).

### 🖥 Backend (API)
- **Framework** : [Laravel](https://laravel.com/) (PHP)
- **Base de Données** : MySQL
- **Authentification** : Gestion des jetons (Tokens) pour sécuriser les accès.
- **Stockage** : Gestion des liens symboliques pour le stockage des images costumes.

---

## ⚙️ Installation et Configuration

### Backend
1. Clonez le dépôt.
2. Configurez votre fichier `.env` (base de données, app_key).
3. Exécutez `php artisan migrate` pour créer les tables.
4. Lancez le serveur : `php artisan serve --host=0.0.0.0`.
5. Créez le lien de stockage : `php artisan storage:link`.

### Mobile
1. Ouvrez le dossier `android` dans Android Studio.
2. Mettez à jour l'adresse IP du serveur dans `Constants.java`.
3. Compilez et lancez sur un émulateur ou un appareil physique.

---

## 🎨 Design System
L'application utilise un thème **Fashion Atelier** caractérisé par :
- Un mode sombre (Dark Mode) élégant.
- Des accents dorés et crème pour un aspect luxueux.
- Une typographie moderne et épurée.

---

*Développé avec passion pour le monde de la mode.* 👗✨
