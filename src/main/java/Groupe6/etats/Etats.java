package Groupe6.etats;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;



/**
 * Base abstraite pour tous les écrans/états du jeu (menu, grille, paramètres, etc.).
 * Fournit l'accès au Game, la liste des boutons et des helpers de hit-test souris.
 *
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public abstract class Etats {
    protected Game game;
    protected ArrayList<Bouton> boutons;

    public Etats(Game game) {
        this.game = game;
    }

    /** Constructeur par défaut (sans référence au jeu). */
    public Etats() {
    }

    /** Indique si le clic souris tombe dans la zone du bouton. */
    public boolean isIn(MouseEvent e, Bouton b) {
        return b.getDelimitation().contains(e.getX(), e.getY());
    }

    /** Indique si le clic souris tombe dans le rectangle donné. */
    public boolean isIn(MouseEvent e, Rectangle r) {
        return r.contains(e.getX(), e.getY());
    }

    public Game getGame() {
        return game;
    }

}
