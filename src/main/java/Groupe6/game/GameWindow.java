package Groupe6.game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Fenêtre principale du jeu - Conteneur JFrame pour l'affichage du jeu.
 * 
 * FONCTIONNALITÉS :
 * - Crée et configure la fenêtre principale du jeu
 * - Gère les événements de focus de la fenêtre (pause automatique, etc.)
 * - Configure les paramètres d'affichage (taille, titre, position)
 * 
 * ARCHITECTURE :
 * - Étend JFrame pour créer une fenêtre d'application
 * - Contient le GamePanel qui gère le rendu
 * - Implémente WindowFocusListener pour gérer la perte/gain de focus
 */
public class GameWindow extends JFrame {
    
    // Identifiant de sérialisation pour la compatibilité JFrame
    private static final long serialVersionUID = 1L;
    
    // Référence au jeu pour la sauvegarde
    private Game game;
    
    /**
     * Constructeur de la fenêtre de jeu.
     * Configure tous les paramètres de la fenêtre et ajoute le panneau de jeu.
     * 
     * @param gamePanel Le panneau de jeu à afficher dans la fenêtre
     */
    public GameWindow(GamePanel gamePanel, Game game) {
        // === CONFIGURATION DE LA FENÊTRE ===
        this.game = game;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Gère la fermeture manuellement
        add(gamePanel);                                   // Ajout du panneau de jeu
        setTitle("GL2 Projet");                          // Titre de la fenêtre
        setLocationRelativeTo(null);                      // Centre la fenêtre à l'écran
        setResizable(false);                              // Empêche le redimensionnement
        pack();                                           // Ajuste la taille au contenu
        setVisible(true);                                 // Affiche la fenêtre
        
        // === GESTION DE LA FERMETURE ===
        // Intercepte la fermeture de la fenêtre pour sauvegarder
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        
        // === GESTION DU FOCUS ===
        // Listener pour gérer les événements de focus (pause/reprise du jeu)
        addWindowFocusListener(new WindowFocusListener() {

            /**
             * Appelé quand la fenêtre gagne le focus.
             * Utile pour reprendre le jeu après une pause.
             * 
             * @param e Événement de focus
             */
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // TODO: Implémenter la reprise du jeu
                throw new UnsupportedOperationException("Unimplemented method 'windowGainedFocus'");
            }

            /**
             * Appelé quand la fenêtre perd le focus.
             * Permet de mettre automatiquement le jeu en pause.
             * 
             * @param e Événement de perte de focus
             */
            @Override
            public void windowLostFocus(WindowEvent e) {
                // TODO: Implémenter la mise en pause automatique
                throw new UnsupportedOperationException("Unimplemented method 'windowLostFocus'");
            }
        });
    }

    
    /**
     * Gère la fermeture de la fenêtre avec sauvegarde.
     * Affiche un dialogue de confirmation et sauvegarde avant de fermer.
     */
    private void handleWindowClosing() {
        // Afficher un dialogue de confirmation
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous sauvegarder avant de quitter ?",
            "Confirmation de fermeture",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        switch (choice) {
            case JOptionPane.YES_OPTION:
                // Sauvegarder et quitter
                if (game != null) {
                    game.saveGame();
                }
                game.cleanup();
                System.exit(0);
                break;
                
            case JOptionPane.NO_OPTION:
                // Quitter sans sauvegarder
                if (game != null) {
                    game.cleanup();
                }
                System.exit(0);
                break;
                
            case JOptionPane.CANCEL_OPTION:
            default:
                // Ne rien faire, la fenêtre reste ouverte
                break;
        }
    }
}
