package Groupe6.etats;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.utilz.Constants;



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

    /** Dernières dimensions pour lesquelles le layout a été appliqué ; -1 force un layout au premier draw. */
    protected int lastLayoutWidth = -1;
    protected int lastLayoutHeight = -1;

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

    /** Compare les dimensions locales à Constants ; si différentes, applique le layout et met à jour les locales. */
    protected final void ensureLayoutUpToDate() {
        if (lastLayoutWidth != Constants.game_width || lastLayoutHeight != Constants.game_height) {
            applyLayout(Constants.game_width, Constants.game_height);
            lastLayoutWidth = Constants.game_width;
            lastLayoutHeight = Constants.game_height;
        }
    }
    /** DEBUG: Affiche une grille de carrées de 20x20 pixels afin de rendre le design de l'UI plus simple*/
    protected final void drawGrid(Graphics g) {
        
      int w = Constants.game_width;
      int h = Constants.game_height;
      final int dw = w / 3;
      final int dh = h / 3;
      g.setColor(Color.RED);
      for (int x = 0; x <= w ; x+= dw)
        g.drawLine(x,0,x,h);
      for (int y = 0; y <= h; y+= dh)
        g.drawLine(0, y, w,y);

    }

    /** Applique le positionnement des éléments UI pour les dimensions données. */
    protected abstract void applyLayout(int w, int h);

}
