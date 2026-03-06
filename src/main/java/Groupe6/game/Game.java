package Groupe6.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import Groupe6.etats.MethodesEtats;
import Groupe6.etats.EtatJeu;
import Groupe6.etats.Jeu;
import Groupe6.etats.Menu;
import Groupe6.etats.Parametres;
import Groupe6.etats.Start;
import Groupe6.etats.Connexion;
import Groupe6.etats.Creation;
import Groupe6.models.SaveData;
import Groupe6.utilz.SaveManager;

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
    private Parametres parametres;
    private Connexion connexion;
    private Creation creation;
    private Jeu jeu;
    
    // Le profil actuellement chargé !
    private SaveData currentSave;

    /** Association EtatJeu -> état concret ; évite les switch dans getCurrentState et dans les inputs. */
    private final Map<EtatJeu, MethodesEtats> stateByEnum = new EnumMap<>(EtatJeu.class);

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
        stateByEnum.put(EtatJeu.START, start);
        menu = new Menu(this);
        stateByEnum.put(EtatJeu.MENU, menu);
        parametres = new Parametres(this);
        stateByEnum.put(EtatJeu.PARAMETRES, parametres);
        connexion = new Connexion(this);
        stateByEnum.put(EtatJeu.CONNEXION, connexion);
        creation = new Creation(this);
        stateByEnum.put(EtatJeu.CREATION, creation);
        
        // La nouvelle grille ajoutée par tes camarades !
        jeu = new Jeu(this);
        stateByEnum.put(EtatJeu.GRILLE, jeu);
    }

    private void startGameLoop() {
        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }

    /** Retourne l'état courant (pour update, render et délégation clavier/souris). */
    public MethodesEtats getCurrentState() {
        EtatJeu e = EtatJeu.getEtatActuel();
        if (e == EtatJeu.QUITTER) {
            quitterJeu();
            return stateByEnum.get(EtatJeu.MENU);
        }
        MethodesEtats state = stateByEnum.get(e);
        if (state == null) {
            throw new IllegalStateException("État de jeu non géré: " + e);
        }
        return state;
    }

    private void update() {
        getCurrentState().update();
    }

    public void render(Graphics g) {
        getCurrentState().draw(g);

        if (debug) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("FPS: " + currentFPS + " | UPS: " + currentUPS, 10, 20);
            
            // Petit debug pour voir qui joue
            if (currentSave != null) {
                g2d.drawString("Joueur: " + currentSave.pseudonyme, 10, 40);
            }
        }
    }

    private void quitterJeu() {
        gameWindow.handleWindowClosing();
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
        if (currentSave != null) {
            System.out.println("Sauvegarde du jeu en cours pour " + currentSave.pseudonyme + "...");
            SaveManager.getInstance().sauvegarderJeu(currentSave);
        } else {
            System.out.println("Aucun joueur connecté, rien à sauvegarder.");
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
    
    // GETTERS & SETTERS
    public SaveData getCurrentSave() { return currentSave; }
    public void setCurrentSave(SaveData save) { this.currentSave = save; }
    
    public GamePanel getGamePanel() { return gamePanel; }
    public Start getStart() { return start; }
    public Menu getMenu() { return menu; }
    public Parametres getParametres() { return parametres; }
    public Jeu getJeu() { return jeu; }
}
