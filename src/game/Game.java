package game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Classe centrale du jeu - Gère la boucle de jeu et coordonne les composants.
 * 
 * FONCTIONNALITÉS :
 * - Gère la boucle de jeu principale (update/render)
 * - Coordonne les différents composants du jeu
 * - Gère les états du jeu (menu, pause, gameplay, etc.)
 * 
 * ARCHITECTURE :
 * - Classe principale qui orchestre tous les systèmes du jeu
 * - Implémente le pattern Game Loop
 * - Interface avec GamePanel pour le rendu et les entrées
 */
public class Game implements Runnable {

    // === COMPOSANTS PRINCIPAUX ===
    private final GamePanel gamePanel;      // Panneau où le jeu est rendu
    private final GameWindow gameWindow;    // Fenêtre qui contient le panneau
    private Thread gameLoopThread;          // Thread dédié à la boucle de jeu

    // === PARAMÈTRES DE PERFORMANCE ===
    // Taux de mise à jour de la logique (updates par seconde)
    private static final int TARGET_UPS = 200;
    // Taux de rafraîchissement de l'affichage (frames par seconde)
    private static final int TARGET_FPS = 120;

    // === SUIVI DES PERFORMANCES ===
    private int currentFPS = 0;             // FPS actuels mesurés
    private int currentUPS = 0;             // UPS actuels mesurés
    private boolean debug = false;          // Active/désactive l'affichage debug

    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        startGameLoop();
    }

    /**
     * Initialise tous les états et classes du jeu.
     * L'ordre d'initialisation est important car certains états peuvent dépendre d'autres.
     * 
     * TODO: Implémenter l'initialisation des états (Menu, Pause, Settings, etc.)
     */
    private void initClasses() {
        // Initialiser tous les états du jeu
        throw new UnsupportedOperationException("Unimplemented method 'initClasses'");
    }

    /**
     * Démarre la boucle de jeu dans un thread séparé.
     */
    private void startGameLoop() {
        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }

    /**
     * Met à jour l'état du jeu en fonction du state actuel.
     * Délègue la mise à jour à l'état approprié.
     */
    private void update() {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    /**
     * Dessine l'état actuel du jeu selon le GameState.
     * Délègue le rendu à l'état approprié.
     * 
     * AFFICHAGE :
     * - Rendu de l'état de jeu actif
     * - Affichage optionnel des informations de debug (FPS/UPS)
     *
     * @param g Contexte graphique utilisé pour le dessin
     */
    public void render(Graphics g) {
        // Affichage de l'overlay FPS/UPS si le mode debug est activé
        if (debug) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("FPS: " + currentFPS + " | UPS: " + currentUPS, 10, 20);
        }
    }

    /**
     * Implémente la boucle principale du jeu avec découplage complet FPS/UPS.
     * 
     * ARCHITECTURE :
     * - UPS (Logique) : Fixed Time Step strict à 200 UPS avec accumulateur
     * - FPS (Rendu) : Limité mais découplé, configurable depuis les settings
     * - Utilise System.nanoTime() pour une précision maximale
     * - Thread.sleep() pour limiter le FPS et économiser les ressources CPU
     * 
     * FIXED TIME STEP :
     * L'UPS utilise un accumulateur qui garantit exactement 200 updates/seconde
     * même si le rendu est plus lent ou plus rapide. Cela assure une physique
     * stable et reproductible.
     */
    @Override
    public void run() {
        // === CONSTANTES DE TEMPS ===
        // Temps par update en nanosecondes (Fixed Time Step pour UPS)
        final double NANOS_PER_UPDATE = 1_000_000_000.0 / TARGET_UPS;
        
        // Variables pour le tracking FPS/UPS
        int frames = 0;              // Compteur de frames rendues
        int updates = 0;             // Compteur de mises à jour effectuées
        long lastFpsCheck = System.currentTimeMillis();
        
        // === FIXED TIME STEP - ACCUMULATEUR ===
        // L'accumulateur garantit que la logique tourne exactement à TARGET_UPS
        double accumulator = 0.0;     // Accumulateur pour les updates (Fixed Time Step)
        
        // === TIMING ===
        long previousTime = System.nanoTime(); // Temps précédent en nanosecondes
        
        // === BOUCLE PRINCIPALE ===
        while (true) {
            long currentTime = System.nanoTime();
            long elapsedNanos = currentTime - previousTime;
            previousTime = currentTime;
            
            // === PHASE 1: LOGIC (UPS) - FIXED TIME STEP ===
            // Ajouter le temps écoulé à l'accumulateur
            accumulator += elapsedNanos;
            
            // Exécuter les updates nécessaires pour maintenir TARGET_UPS
            // L'accumulateur garantit qu'on fait exactement le bon nombre d'updates
            while (accumulator >= NANOS_PER_UPDATE) {
                update();            // Mise à jour de la logique du jeu
                updates++;
                accumulator -= NANOS_PER_UPDATE; // Retirer un "tick" de l'accumulateur
            }
            
            // === PHASE 2: RENDERING (FPS) - DÉCOUPLÉ ===
            // Le rendu est complètement indépendant de la logique
            // On peut rendre à n'importe quelle fréquence (limitée par TARGET_FPS)
            gamePanel.repaint();
            frames++;
            
            // === PHASE 3: LIMITATION FPS ET ÉCONOMIE CPU ===
            // Calculer le temps disponible avant le prochain frame
            
            long nanosPerFrame = 1_000_000_000L / TARGET_FPS;
            long frameTime = System.nanoTime() - currentTime;
            long sleepTime = (nanosPerFrame - frameTime) / 1_000_000; // Convertir en millisecondes
            
            // Dormir seulement si on a le temps (évite les valeurs négatives)
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break; // Sortir de la boucle si interrompu
                }
            }
            // Si sleepTime <= 0, on est en retard et on continue sans dormir
            
            // === PHASE 4: TRACKING FPS/UPS (affichage) ===
            // Mettre à jour les compteurs chaque seconde
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
    
}
