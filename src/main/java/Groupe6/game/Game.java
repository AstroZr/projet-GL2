package Groupe6.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Map;

import Groupe6.audio.SoundManager;
import Groupe6.etats.MethodesEtats;
import Groupe6.etats.EtatJeu;
import Groupe6.etats.Jeu;
import Groupe6.etats.Menu;
import Groupe6.etats.Parametres;
import Groupe6.etats.Records;
import Groupe6.etats.Start;
import Groupe6.etats.Connexion;
import Groupe6.etats.Creation;
import Groupe6.etats.Selection;
import Groupe6.utilz.Constants;
import Groupe6.utilz.FontCache;
import Groupe6.utilz.LangManager;

/**
 * Coeur du jeu : boucle update/render découplée (UPS fixe, FPS limité), délégation aux états (Start, Menu, etc.).
 * Utilise un pattern State Machine pour gérer les différents écrans (Menu, Jeu, Paramètres, etc.).
 * 
 * Architecture:
 * - Une EnumMap mappe EtatJeu -> MethodesEtats pour accès O(1) aux états
 * - Boucle asynchrone avec accumulation de temps pour UPS stable
 * - Rendu découplé (FPS limité) indépendant des updates
 * - Gestion des transitions fluides entre états avec fade-out/fade-in
 */
public class Game implements Runnable {
    private static final Font DEBUG_FONT = FontCache.get("Arial", Font.PLAIN, 14);

    // ====== COMPOSANTS GRAPHIQUES ======
    private final GamePanel gamePanel;      // Panneau Swing contenant le rendu
    private final GameWindow gameWindow;    // Fenêtre principale

    // ====== BOUCLE DE JEU ======
    private Thread gameLoopThread;
    private static final int TARGET_UPS = 60;  // Updates Par Seconde (logique fixe)
    private static final int TARGET_FPS = 60;  // Frames Par Seconde (rendu limité)
    
    // ====== COMPTEURS & ÉTAT ======
    private int currentFPS = 0;             // FPS actuel (mis à jour chaque seconde)
    private int currentUPS = 0;             // UPS actuel (mis à jour chaque seconde)
    private boolean debug = true;           // Afficher les stats FPS/UPS en haut-gauche
    private boolean repeindreFlag = false;  // Flag pour demander un repaint du panel
    
    // ====== CONFIGURATION GLOBALE ======
    private String langueCode = "fr";       // Code de la langue ("fr" ou "en")
    
    // ====== GESTION DES TRANSITIONS ======
    private final TransitionManager transitionManager = new TransitionManager();
    private EtatJeu dernierEtat = null;     // État précédent (pour détecter changement)
    private java.awt.image.BufferedImage frameBuffer = null;  // Double-buffer hors-écran
    
    // ====== JOUEUR COURANT ======
    private String joueurCourant = "Invité";  // Nom/pseudo du joueur actif

    private Start start;
    private Menu menu;
    private Parametres parametres;
    private Connexion connexion;
    private Creation creation;
    private Jeu jeu;
    private Selection selection;
    private Records records;

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
        jeu = new Jeu(this);
        stateByEnum.put(EtatJeu.GRILLE, jeu);
        selection = new Selection(this);
        stateByEnum.put(EtatJeu.SELECTION, selection);
        records = new Records(this);
        stateByEnum.put(EtatJeu.RECORDS, records);

        notifierChangementLangue();
    }

    public boolean isEnglish() {
        return "en".equals(langueCode);
    }

    public String getLangueCode() {
        return langueCode;
    }

    public void setLangueSelectionnee(String code) {
        if (code == null || code.isBlank()) return;
        if (langueCode.equals(code)) return;
        langueCode = code;
        LangManager.setLangue(code);
        notifierChangementLangue();
    }

    public String getJoueurCourant() {
        return joueurCourant;
    }

    public void setJoueurCourant(String pseudo) {
        this.joueurCourant = pseudo;
        if (parametres != null) {
            parametres.chargerParametresJoueur(pseudo);
        }
        if (records != null) {
            records.invaliderDonnees();
        }
    }

    private void notifierChangementLangue() {
        LangManager.setLangue(langueCode);
        if (start != null) start.updateTexts();
        if (menu != null) menu.updateTexts();
        if (parametres != null) parametres.updateTexts();
        if (connexion != null) connexion.updateTexts();
        if (creation != null) creation.updateTexts();
        if (jeu != null) jeu.updateTexts();
        if (selection != null) selection.updateTexts();
        if (records != null) records.updateTexts();
    }

    private void startGameLoop() {
        gameLoopThread = new Thread(this);
        gameLoopThread.start();
    }

    /** Retourne l'état courant (pour update, render et délégation clavier/souris). */
    public MethodesEtats getCurrentState() {
        EtatJeu e = EtatJeu.getEtatActuel();
        if (e == EtatJeu.QUITTER) {
            EtatJeu.setEtatActuel(EtatJeu.MENU); // reset avant le dialog pour éviter les appels multiples
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
        EtatJeu etatCourant = EtatJeu.getEtatActuel();
        if (dernierEtat != null && etatCourant != dernierEtat) {
            transitionManager.notifierChangementEtat(frameBuffer);
            SoundManager.getInstance().playTransition();
            getCurrentState().onEnter();
        }
        dernierEtat = etatCourant;
        transitionManager.update();
        getCurrentState().update();
    }

    public void render(Graphics g) {
        int w = Constants.game_width;
        int h = Constants.game_height;

        // Maintenir le frame buffer à la bonne taille
        if (frameBuffer == null || frameBuffer.getWidth() != w || frameBuffer.getHeight() != h) {
            frameBuffer = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        }

        // Rendre l'état courant dans le buffer hors-écran
        Graphics2D offG = frameBuffer.createGraphics();
        getCurrentState().draw(offG);
        offG.dispose();

        // Blit vers l'écran
        g.drawImage(frameBuffer, 0, 0, null);

        // Superposer l'ancien frame avec opacité décroissante (crossfade)
        transitionManager.draw(g, w, h);

        if (debug) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            g2d.setFont(DEBUG_FONT);
            g2d.drawString("FPS: " + currentFPS + " | UPS: " + currentUPS, 10, 20);
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
        try {
            System.out.println("Sauvegarde du jeu en cours...");
            
            // sauvegarde la grille si une partie est en cours
            if(jeu != null && jeu.getGrille() != null){
                jeu.getGrille().saveGrille();
            }

            // (les paramètres sont déjà sauvegardés via le bouton appliquer de Parametres.java)
            if (parametres != null) {
                parametres.sauvegarderProfilActuel();
            }
            if (jeu != null) {
                jeu.sauvegarderEtatNiveauCourant();
            }

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

    public Parametres getParametres() {
        return parametres;
    }

    public Jeu getJeu() {
        return jeu;
    }
}
