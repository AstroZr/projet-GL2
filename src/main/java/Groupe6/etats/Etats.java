package Groupe6.etats;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import Groupe6.game.Game;
import Groupe6.ui.Bouton;



/**
 * Classe abstraite représentant un état du jeu.
 * 
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public abstract class Etats {
    /** Instance du jeu */
    protected  Game game;
    /** Liste des boutons de l'état */
    protected ArrayList<Bouton> boutons;

    /**
     * Constructeur de l'état.
     * 
     * @param game Instance du jeu
     */
    public Etats(Game game) {
        this.game = game;
    }

    /**
     * Constructeur de l'état.
     */
    public Etats() {
    }

    /**
     * Vérifie si le clic de la souris est dans la zone de délimitation du bouton.
     * 
     * @param e L'événement de la souris
     * @param b Le bouton à vérifier
     * @return true si le clic est dans la zone de délimitation du bouton, false sinon
     */
    public boolean isIn(MouseEvent e, Bouton b) {
        return b.getDelimitation().contains(e.getX(), e.getY());
    }

    /**
     * Vérifie si le clic de la souris est dans la zone de délimitation du rectangle.
     * 
     * @param e L'événement de la souris
     * @param r Le rectangle à vérifier
     * @return true si le clic est dans la zone de délimitation du rectangle, false sinon
     */
    public boolean isIn(MouseEvent e, Rectangle r) {
        return r.contains(e.getX(), e.getY());
    }

    /**
     * Récupère l'instance du jeu.
     * 
     * @return L'instance du jeu
     */
    public Game getGame() {
        return game;
    }

}
