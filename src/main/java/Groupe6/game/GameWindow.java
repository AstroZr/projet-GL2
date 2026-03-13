package Groupe6.game;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Fenêtre principale (JFrame) : contient le GamePanel, gère fermeture (sauvegarde) et focus.
 */
public class GameWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Game game;

    public GameWindow(GamePanel gamePanel, Game game) {
        this.game = game;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        add(gamePanel);
        setTitle("GL2 Projet");
        setLocationRelativeTo(null);
        setResizable(true);
        pack();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                System.out.println("Window gained focus");
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                System.out.println("Window lost focus");
            }
        });
    }

    public void handleWindowClosing() {
        boolean en = game != null && game.isEnglish();
        String message = en ? "Do you want to save before quitting?" : "Voulez-vous sauvegarder avant de quitter ?";
        String title = en ? "Exit confirmation" : "Confirmation de fermeture";

        int choice = JOptionPane.showConfirmDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        switch (choice) {
            case JOptionPane.YES_OPTION:
                if (game != null) {
                    game.saveGame();
                }
                game.cleanup();
                System.exit(0);
                break;
            case JOptionPane.NO_OPTION:
                if (game != null) {
                    game.cleanup();
                }
                System.exit(0);
                break;
            case JOptionPane.CANCEL_OPTION:
            default:
                break;
        }
    }
}
