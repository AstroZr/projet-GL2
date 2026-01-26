package Groupe6.game;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

import Groupe6.inputs.KeyboardInputs;
import Groupe6.inputs.MouseInputs;

/**
 * Panneau de jeu - Composant graphique principal responsable du rendu et des entrées.
 * 
 * FONCTIONNALITÉS :
 * - Gère le rendu graphique du jeu via paintComponent
 * - Configure et intègre les gestionnaires d'entrées (souris et clavier)
 * - Définit les dimensions de la fenêtre de jeu
 * 
 * ARCHITECTURE :
 * - Étend JPanel pour l'intégration Swing
 * - Agit comme pont entre le système d'entrées et la logique du jeu
 * - Délègue le rendu à l'instance de Game
 */
public class GamePanel extends JPanel {
    private final int GAME_WIDTH = 1920;
    private final int GAME_HEIGHT = 1080;
    
    // === GESTION DES ENTRÉES ===
    private MouseInputs mouseInputs;  // Gestionnaire des entrées souris
    
    // === RÉFÉRENCE AU JEU ===
    private Game game;                // Instance principale du jeu

    /**
     * Constructeur du panneau de jeu.
     * Initialise les gestionnaires d'entrées et configure la taille du panneau.
     * 
     * @param game Instance du jeu à associer au panneau
     */
    public GamePanel(Game game) {
        this.mouseInputs = new MouseInputs(this);
        this.game = game;

        setPanelSize();
        
        // Configuration des listeners d'entrées
        addKeyListener(new KeyboardInputs(this));
        addMouseListener(mouseInputs);
        addMouseMotionListener(mouseInputs);
    }

    /**
     * Définit les dimensions du panneau de jeu.
     * Configure la taille préférée du panneau selon les constantes GAME_WIDTH et GAME_HEIGHT.
     */
    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setPreferredSize(size);
        System.out.println("Dimensions : " + GAME_WIDTH + "x" + GAME_HEIGHT);
    }

    /**
     * Méthode de rendu du panneau.
     * Appelée automatiquement par Swing pour dessiner le contenu du panneau.
     * 
     * @param g Contexte graphique utilisé pour le dessin
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        game.render(g);
    }

    /**
     * Retourne l'instance du jeu associée à ce panneau.
     * 
     * @return L'instance de Game
     */
    public Game getGame() {
        return this.game;
    }
}
