package Groupe6.fond;

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
}
