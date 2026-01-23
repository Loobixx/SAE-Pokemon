# 🐾 Pokédex Tracker — Documentation du Projet

Le **Pokédex Tracker** est une application web permettant aux utilisateurs de gérer leur collection de Pokémon, incluant les formes **normales**, **chromatiques (Shiny)**, ainsi qu’une **liste de souhaits**.

---

## 💻 Front-End (Vue.js 3 / Vuetify)

L’interface utilisateur repose sur :

- **Vue.js 3** (Composition API)
- **Vuetify** pour les composants graphiques et la mise en page

---

## 🔍 Système de Recherche et Tri

### 🔎 Recherche Dynamique
- Barre de recherche située en haut à gauche
- Filtrage par **nom** ou **numéro**
- Mode **“commence par”** pour optimiser la pertinence

### ⚡ Réactivité
- La pagination se réinitialise automatiquement à **la page 1** dès qu’une recherche est effectuée

---

## 🛠️ Architecture du Composant `HomeView.vue`

### 📁 Gestion des Onglets
Trois vues sont contrôlées via `currentTab` :

- **Pokédex**
- **Attrapés**
- **Souhaités**

### 🧮 Propriétés Calculées (Computed)

#### `baseFilteredPokemons`
- Fusionne les données de l’API avec :
    - la recherche
    - les filtres actifs

#### `filteredPokemons`
- Découpe la liste pour l’affichage paginé
- **50 Pokémon par page**

### 🔐 Sécurité des Données
- Vérifications intégrées pour éviter les erreurs en cas de données incomplètes

---

## ⚙️ Back-End (API REST)

Le back-end gère :

- Les données utilisateurs
- L’état de capture des Pokémon
- Les listes de souhaits

### 📡 Endpoints Principaux

| Méthode | Endpoint              | Description |
|--------|------------------------|-------------|
| GET    | `/api/game/captured`  | Liste des Pokémon capturés |
| GET    | `/api/game/wished`    | Liste des Pokémon souhaités |

---

## 🔐 Sécurité et Authentification

- **JWT obligatoire** pour accéder aux données privées
- Le token doit être envoyé dans l’en-tête :
