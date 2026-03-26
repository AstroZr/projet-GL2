package Groupe6.etats;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Color;
import Groupe6.audio.SoundManager;
import Groupe6.fond.Fond;
import Groupe6.fond.FondDegrade;
import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.utilz.Constants;
import Groupe6.utilz.LayoutScale;



/**
 * Base abstraite pour tous les écrans/états du jeu (menu, grille, paramètres, création, etc.).
 * 
 * Fournit:
 * - Accès à la référence Game (pour changements d'état, joueur actif, etc.)
 * - Gestion centralisée du fond/thème (partage entre tous les états)
 * - Liste de boutons et détection hit-test souris
 * - Gestion du layout (recalcul responsive lors resize fenêtre)
 * 
 * Tous les états concrets (Start, Menu, Jeu, etc.) héritent de cette classe.
 *
 * @author Lounol72
 * @version 1.0
 * @since 2026-01-28
 */
public abstract class Etats implements MethodesEtats {
    // ====== RÉFÉRENCES GLOBALES ======
    protected Game game;                   // Référence au contrôleur central (changements d'état, etc.)
    protected ArrayList<Bouton> boutons;   // Boutons de cet état (gérés par les sous-classes)

    // ====== THÈME GLOBAL (singleton par défaut) ======
    /** Fond/thème partagé par TOUS les états ; FondDegrade par défaut. */
    private static Fond fondActuel = FondDegrade.getInstance();

    /** Retourne le fond actif (thème couleurs + gradient). */
    protected static Fond getFond() {
        return fondActuel;
    }

    /** Change le fond globalement pour TOUS les états (ex: mode clair/foncé). */
    public static void setFondActuel(Fond nouveauFond) {
        fondActuel = nouveauFond;
    }

    // ====== GESTION RESPONSIVE ======
    /** Dernières dimensions pour lesquelles updateLayout a été appelé. -1 force un layout au premier draw. */
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
            updateLayout(Constants.game_width, Constants.game_height);
            lastLayoutWidth = Constants.game_width;
            lastLayoutHeight = Constants.game_height;
        }
    }

    @Override
    public void updateLayout(int gameWidth, int gameHeight) {
        LayoutScale.getInstance().update(gameWidth, gameHeight);
        applyLayout(gameWidth, gameHeight);
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

    // ── Navigation clavier ────────────────────────────────────────────────────

    /** Indice du bouton actuellement focalisé au clavier (-1 = aucun). */
    protected int indiceFocusClavierBouton = -1;

    /**
     * Déplace le focus clavier de {@code delta} positions dans la liste des boutons.
     * Joue le son de survol.
     */
    protected void deplacerFocusBouton(int delta) {
        if (boutons == null || boutons.isEmpty()) return;
        for (Bouton b : boutons) b.setFocusClavier(false);
        if (indiceFocusClavierBouton < 0) {
            indiceFocusClavierBouton = delta > 0 ? 0 : boutons.size() - 1;
        } else {
            indiceFocusClavierBouton = (indiceFocusClavierBouton + delta + boutons.size()) % boutons.size();
        }
        boutons.get(indiceFocusClavierBouton).setFocusClavier(true);
        SoundManager.getInstance().playClick();
    }

    /**
     * Gère les touches de navigation (↑↓←→, hjkl, Entrée/Espace).
     * @return {@code true} si la touche a été consommée.
     */
    protected boolean gererNavigationClavier(KeyEvent e) {
        int code = e.getKeyCode();
        char c = Character.toLowerCase(e.getKeyChar());
        if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_RIGHT || c == 'j' || c == 'l') {
            deplacerFocusBouton(1);
            return true;
        }
        if (code == KeyEvent.VK_UP || code == KeyEvent.VK_LEFT || c == 'k' || c == 'h') {
            deplacerFocusBouton(-1);
            return true;
        }
        if ((code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE)
                && indiceFocusClavierBouton >= 0
                && indiceFocusClavierBouton < boutons.size()) {
            boutons.get(indiceFocusClavierBouton).appliquerAction();
            return true;
        }
        return false;
    }

    /** Efface le focus clavier (appelé quand la souris reprend la main). */
    protected void clearFocusClavier() {
        indiceFocusClavierBouton = -1;
        if (boutons != null) {
            for (Bouton b : boutons) b.setFocusClavier(false);
        }
    }

}
