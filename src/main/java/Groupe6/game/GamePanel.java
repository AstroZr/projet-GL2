package Groupe6.game;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;

import Groupe6.inputs.KeyboardInputs;
import Groupe6.inputs.MouseInputs;
import Groupe6.utilz.Constants;

/**
 * Panneau Swing où le jeu est rendu ; délègue à Game.render et enregistre clavier/souris.
 * Initialise Constants.game_width/height et les met à jour à chaque resize.
 */
public class GamePanel extends JPanel {
    private final MouseInputs mouseInputs;
    private final Game game;

    public GamePanel(Game game) {
        this.mouseInputs = new MouseInputs(this);
        this.game = game;
        // Initialiser Constants avec la taille par défaut avant setPanelSize
        Constants.game_width = 1920;
        Constants.game_height = 1080;
        setPanelSize();
        addResizeListener();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
        addMouseWheelListener(mouseInputs);
    }

    /** Taille préférée basée sur Constants (cohérent au premier affichage). */
    private void setPanelSize() {
        setPreferredSize(new Dimension(Constants.game_width, Constants.game_height));
        System.out.println("Dimensions : " + Constants.game_width + "x" + Constants.game_height);
    }

    /** Met à jour Constants.game_width/height à chaque resize du panel. */
    private void addResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                if (w > 0 && h > 0) {
                    Constants.game_width = w;
                    Constants.game_height = h;
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.render(g);
    }

    public Game getGame() {
        return this.game;
    }
}
