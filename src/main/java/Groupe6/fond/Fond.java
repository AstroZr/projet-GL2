package Groupe6.fond;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Interface de contrat pour tous les fonds d'écran/thèmes du jeu.
 * 
 * Responsabilités:
 * - Fournir les couleurs pour chaque élément UI (grille, boutons, inputs, etc.)
 * - Dessiner l'arrière-plan (gradient, nuages, pattern, etc.)
 * - Gérer animations (ex: nuages qui se déplacent)
 * - Offrir navigation entre les 3 thèmes: Clair, Foncé, Catppuccin
 * 
 * Singleton Pattern: Chaque thème a une instance unique (getInstance)
 * 
 * Implémentations:
 * - FondClair: Thème clair (fond blanc/bleu ciel, texte foncé)
 * - FondFonce: Thème sombre (fond gris foncé, texte clair)
 * - FondCatppuccin: Palette Catppuccin (couleurs harmonieuses)
 * - AbstractFondDegrade: Base avec gradient horizontale
 */
public interface Fond {

    // ====== RENDU ======
    /** Dessine le fond (gradient, nuages, pattern, etc.) à l'écran. */
    void draw(Graphics g);

    /** Met à jour la logique du fond (anim nuages, particules, etc.). */
    void update();

    /** Réinitialise les positions des éléments animés (après changement état). */
    void reset();

    // ====== NAVIGATION THÈMES ======
    /** Retourne le singleton du thème Clair (FondClair). */
    Fond clair();

    /** Retourne le singleton du thème Foncé (FondFonce). */
    Fond fonce();

    /** Retourne le singleton du thème Catppuccin (FondCatppuccin). */
    Fond catppuccin();

    // ====== GRILLE & ZONES ======
    /** Couleur de fond des cellules de la grille (normal, non sélectionné). */
    Color getCouleurFondCellule();

    /** Couleur du texte (valeurs dans les cellules, étiquettes de zone). */
    Color getCouleurTexte();

    /** Couleur des bordures épaisses délimitant les zones (épaisseur 5px). */
    Color getCouleurBordureZone();

    /** Couleur des étiquettes de zone (valeur cible + opérateur, ex: "6+"). */
    Color getCouleurEtiquetteZone();

    // ====== BOUTONS ======
    /** Couleur de fond du bouton à l'état normal. */
    Color getCouleurFondBouton();

    /** Couleur de fond du bouton au survol souris. */
    Color getCouleurFondBoutonSurvol();

    /** Couleur de fond du bouton au clic (enfoncé). */
    Color getCouleurFondBoutonClic();

    /** Couleur de la bordure du bouton (contour 2px). */
    Color getCouleurBordreBouton();

    // ====== CHAMPS TEXTE (TextInput) ======
    /** Couleur de fond du champ texte non focalisé. */
    Color getCouleurFondInput();

    /** Couleur de fond du champ texte focalisé (clavier actif). */
    Color getCouleurFondInputFocus();

    /** Couleur d'accent (bordure focus, éléments de mise en évidence). */
    Color getCouleurAccent();

    /** Couleur du texte placeholder (indice de saisie). */
    Color getCouleurPlaceholder();
}
