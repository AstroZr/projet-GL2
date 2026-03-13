package Groupe6.fond;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Contrat pour tous les fonds d'écran du jeu.
 * Chaque implémentation est un singleton offrant un thème visuel distinct
 * et permettant la navigation vers les autres thèmes via clair(), fonce(), catppuccin().
 */
public interface Fond {

    /** Dessine le fond à l'écran. */
    void draw(Graphics g);

    /** Met à jour la logique du fond (animations, nuages, etc.). */
    void update();

    /** Réinitialise les positions des éléments animés. */
    void reset();

    /** Retourne le singleton du thème clair. */
    Fond clair();

    /** Retourne le singleton du thème foncé. */
    Fond fonce();

    /** Retourne le singleton du thème Catppuccin. */
    Fond catppuccin();

    /** Couleur de fond des cellules de la grille. */
    Color getCouleurFondCellule();

    /** Couleur du texte (valeurs et étiquettes de zone). */
    Color getCouleurTexte();

    /** Couleur des bordures épaisses délimitant les zones. */
    Color getCouleurBordureZone();

    /** Couleur des étiquettes de zone (valeur cible + opérateur). */
    Color getCouleurEtiquetteZone();

    // --- Boutons ---

    /** Couleur de fond du bouton à l'état normal. */
    Color getCouleurFondBouton();

    /** Couleur de fond du bouton au survol. */
    Color getCouleurFondBoutonSurvol();

    /** Couleur de fond du bouton au clic. */
    Color getCouleurFondBoutonClic();

    /** Couleur de la bordure du bouton. */
    Color getCouleurBordreBouton();

    // --- TextInput ---

    /** Couleur de fond du champ texte non focalisé. */
    Color getCouleurFondInput();

    /** Couleur de fond du champ texte focalisé. */
    Color getCouleurFondInputFocus();

    /** Couleur d'accent (bordure focus, éléments d'accentuation). */
    Color getCouleurAccent();

    /** Couleur du texte placeholder. */
    Color getCouleurPlaceholder();
}
