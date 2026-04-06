# MathDoku (CalcuDoku) - Projet GL2

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)]()

## Présentation du Projet
Projet réalisé dans le cadre du module de **Génie Logiciel 2** (3ème année de licence Informatique à **Le Mans Université**).
Il s'agit de la conception et du développement d'une application de jeu **CalcuDoku** complète en **Java**.

L'objectif principal du projet se focalise sur **l’accompagnement du joueur**, avec la mise en place d’un système d’aide contextuelle intelligent conçu pour assister l’utilisateur sans résoudre le jeu à sa place.

---

## Qu'est-ce que le CalcuDoku ?
Le CalcuDoku est un casse-tête logique où le joueur doit remplir une grille N x N en respectant deux règles fondamentales :
- **L’unicité :** Pour remporter la partie, chaque chiffre de `1` à `N` ne doit apparaître qu'une seule fois par ligne et par colonne (le jeu vous laisse libre d'en placer plusieurs).
- **L’arithmétique :** La grille est divisée en zones délimitées. Chaque zone affiche un résultat cible et un opérateur (`+`, `-`, `x`, `÷`). Les chiffres placés dans la zone doivent produire le résultat cible en utilisant l'opérateur donné.

---

## Fonctionnalités Principales

### Système de Jeu Avancé
- **Détection des erreurs :** Retour visuel (activable ou désactivable dans les paramètres) signalant les conflits d'unicité lorsqu'un chiffre est présent en double sur une ligne ou une colonne.
- **Mode Candidat :** Possibilité d'annoter des hypothèses (brouillon) dans chaque case pour préparer ses prochains coups sans valider le choix.
- **Historique complet :** Registre séquentiel autorisant l'annulation (Undo / `Ctrl+Z`) et le rétablissement (Redo) de n'importe quelle action (y compris en mode candidat).

### Système d'Aide Progressif
Au lieu de simplement donner la solution au joueur bloqué, le jeu l'accompagne avec un système d'indices à paliers (impactant le score) :
1. **Description technique :** Explication théorique de la règle ou déduction à appliquer.
2. **Indice textuel ciblé :** Indication sur la zone spécifique de la grille (lignes, colonnes) à observer.
3. **Aide visuelle :** Mise en surbrillance d'une cellule, d'un secteur ou des candidats.

### Profils & Sauvegarde
- **Profils Joueurs :** Suivi des scores, des temps, des aides utilisées et sauvegarde du meilleur score de votre pseudonyme (visible par les autres).
- **Sauvegarde d'état complète :** Possibilité de quitter et de reprendre une partie exactement à l'endroit laissé (chronomètre, brouillons et historique conservés).
- **Personnalisation :** Thème sombre/clair, et contrôle des volumes (bruitages & musique).

### Interface & Modes
- **Multilingue :** Chargement dynamique et changement à la volée de la langue de l'interface.
- **Mode Campagne :** Suite de grilles prédéfinies à la difficulté croissante et intégrant un tutoriel de départ.
- **Mode Aléatoire :** Sélection d'une grille parmi une liste selon la difficulté choisie.

---

## Technique
- **Langage privilégié :** Java
- **Interface Graphique :** Swing
- **Multiplateforme :** Jouable sur Windows, macOS et Linux.

---

## Exécution du jeu
Une fois le projet compilé, utilisez l'archive exécutable générée pour lancer l'application :

```bash
java -jar groupe6.jar
```

---

## Équipe de Développement (Groupe 6)
*Licence Informatique 3ème année (2025-2026) - Le Mans Université*
- **Alban Borde**
- **Lucien Defosse**
- **Lucas Dupont**
- **Maël Gaumont**
- **Maxime Jouatel**
- **Lucas Reverbel–Longhi**
- **Louis Subtil**
