package main;

import game.Game;

/**
 * Classe principale du jeu - Point d'entrée de l'application.
 * 
 * FONCTIONNALITÉS :
 * - Lance l'application et initialise le jeu
 * - Gère l'instance principale du jeu
 * 
 * ARCHITECTURE :
 * - Point d'entrée unique de l'application
 * - Crée et maintient l'instance de Game
 */
public class Main {
    
    // === INSTANCE DU JEU ===
    private static Game game;

    /**
     * Point d'entrée de l'application.
     * 
     * @param args Arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        Init();
    }

    /**
     * Initialise le jeu en créant une nouvelle instance de Game.
     * Cette méthode configure tous les composants nécessaires au démarrage du jeu.
     */
    private static void Init() {
        game = new Game();
    }
}