package Groupe6.mathdoku;

import Groupe6.game.Game;

/**
 * Point d'entrée (main) de l'application CalcuDoku/MathDoku.
 * 
 * Responsabilité unique:
 * - Créer et initialiser l'instance Game (qui elle-même crée la fenêtre et lance la boucle)
 * 
 * Thread: main thread (créé par JVM au démarrage)
 */
public class App {

    // ====== INSTANCE GLOBALE ======
    private static Game game;  // L'instance Game centrale (accès singleton)

    /**
     * Point d'entrée JVM : initialise et lance le jeu.
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        init();
    }

    /**
     * Initialise l'instance Game (crée fenêtre, GamePanel, états, et démarre boucle asynchrone).
     */
    private static void init() {
        game = new Game();  // Constructeur lance la boucle de jeu dans un thread
    }
}

