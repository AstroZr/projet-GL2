package Groupe6.game;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import Groupe6.inputs.KeyboardInputs;
import Groupe6.inputs.MouseInputs;

/**
 * Panneau Swing où le jeu est rendu ; délègue à Game.render et enregistre clavier/souris.
 */
public class GamePanel extends JPanel {
    private static final int GAME_WIDTH = 1920;
    private static final int GAME_HEIGHT = 1080;

    private final MouseInputs mouseInputs;
    private final Game game;

    public GamePanel(Game game) {
        this.mouseInputs = new MouseInputs(this);
        this.game = game;
        setPanelSize();
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    private void setPanelSize() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        System.out.println("Dimensions : " + GAME_WIDTH + "x" + GAME_HEIGHT);
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
