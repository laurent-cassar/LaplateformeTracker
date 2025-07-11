# LaplateformeTracker

# 🎓 Student Management System

Ce projet est une application Java de gestion d'étudiants permettant de gérer efficacement une base de données PostgreSQL.  
Il a pour objectif de renforcer mes compétences en **programmation Java**, **SQL** et **gestion de bases de données**.

---

## 🚀 Fonctionnalités principales

- **Ajouter un étudiant**
- **Modifier un étudiant** (par ID)
- **Supprimer un étudiant** (par ID)
- **Afficher tous les étudiants**
- **Rechercher un étudiant** (par ID)

---

## ⚙️ Fonctionnalités avancées

- **Tri des étudiants** (par nom, prénom, âge, ou moyenne des notes)
- **Recherche avancée** (par âge, moyenne des notes, etc.)
- **Statistiques** (moyenne générale, nombre d’étudiants par tranche d’âge, etc.)
- **Import/Export des données** (format CSV, XML ou JSON)
- **Pagination** pour une meilleure lisibilité
- **Gestion des erreurs améliorée** avec des messages clairs et des options de correction
- **Système d'authentification** (nom d'utilisateur + mot de passe)
- **Export des résultats** (CSV, PDF ou HTML)
- **Sauvegarde automatique** des données à intervalles réguliers

---

## 🏗️ Technologies utilisées

- **Java** (avec JDBC pour la connexion à la base de données)
- **PostgreSQL** (Base de données relationnelle)
- **SQL** (avec requêtes paramétrées pour éviter les injections SQL)
- (Optionnel) Librairies supplémentaires pour l'export PDF/CSV/JSON

---

## 🗄️ Structure de la base de données

Table principale : `student`

| Colonne     | Type       | Description                   |
|-------------|------------|-------------------------------|
| id          | SERIAL     | Identifiant unique (clé primaire) |
| first_name  | VARCHAR    | Prénom de l'étudiant          |
| last_name   | VARCHAR    | Nom de famille de l'étudiant  |
| age         | INTEGER    | Âge de l'étudiant             |
| grade       | FLOAT      | Note ou moyenne de l'étudiant |

---

## 🔑 Sécurité

- Requêtes SQL paramétrées (prévention contre l’injection SQL)
- Système d’authentification simple (login/mot de passe)

---
