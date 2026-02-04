package Groupe6.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import Groupe6.etats.EtatJeu;
import Groupe6.etats.Menu;
import Groupe6.etats.Start;


/**
 * Coeur du jeu : boucle update/render découplée (UPS fixe, FPS limité), délégation aux états (Start, Menu, etc.).
 */
public class Game implements Runnable {

    private final GamePanel gamePanel;
    private final GameWindow gameWindow;
    private Thread gameLoopThread;

    private static final int TARGET_UPS = 200;
    private static final int TARGET_FPS = 120;

    private int currentFPS = 0;
    private int currentUPS = 0;
    private boolean debug = true;
    private boolean repeindreFlag = false;

    private Start start;
    private Menu menu;

    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel, this);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        startGameLoop();
    }

    private void initClasses() {
        start = new Start(this);
        menu = new Menu(this);
    }

    private void startGameLoop() {
        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }

    private void update() {
        switch (EtatJeu.getEtatActuel()) {
            case START:
                start.update();
                break;
            case MENU:
                menu.update();
                break;
            default:
                break;
        }
        
    }

    public void render(Graphics g) {
        switch (EtatJeu.getEtatActuel()) {
            case START:
                start.draw(g);
                break;
            case MENU:
                menu.draw(g);
                break;
            default:
                break;
        }
        if (debug) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("FPS: " + currentFPS + " | UPS: " + currentUPS, 10, 20);
        }
        
    }

    /** Boucle principale : UPS fixe (accumulateur), rendu découplé, limitation FPS. */
    @Override
    public void run() {
        final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_UPS;
        int frames = 0;
        int updates = 0;
        long lastFpsCheck = System.currentTimeMillis();
        double accumulator = 0.0;
        long previousTime = System.nanoTime();

        while (true) {
            long currentTime = System.nanoTime();
            accumulator += currentTime - previousTime;
            previousTime = currentTime;

            while (accumulator >= NANOS_PER_UPDATE) {
                update();
                updates++;
                accumulator -= NANOS_PER_UPDATE;
                repeindreFlag = true;
            }

            if (repeindreFlag) {
                gamePanel.repaint();
                frames++;
                repeindreFlag = false;
            }

            long nanosPerFrame = 1_000_000_000L / TARGET_FPS;
            long frameTime = System.nanoTime() - currentTime;
            long sleepTime = (nanosPerFrame - frameTime) / 1_000_000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            long currentMillis = System.currentTimeMillis();
            if (currentMillis - lastFpsCheck >= 1000) {
                currentFPS = frames;
                currentUPS = updates;
                frames = 0;
                updates = 0;
                lastFpsCheck = currentMillis;
            }
        }
    }
    
    /** Sauvegarde l’état du jeu ; appelée avant fermeture (ex. dialogue fenêtre). */
    public void saveGame() {
        try {
            System.out.println("Sauvegarde du jeu en cours...");
            
            // Simuler une sauvegarde
            Thread.sleep(500);
            System.out.println("Jeu sauvegardé avec succès !");
        } catch (InterruptedException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    /** Arrête le thread de jeu et libère les ressources. */
    public void cleanup() {
        if (gameLoopThread != null && gameLoopThread.isAlive()) {
            gameLoopThread.interrupt();
            try {
                gameLoopThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Nettoyage des ressources terminé.");
    }
    
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public Start getStart() {
        return start;
    }

    public Menu getMenu() {
        return menu;
    }
}
