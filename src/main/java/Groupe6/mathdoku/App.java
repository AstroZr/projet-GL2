package Groupe6.mathdoku;

import Groupe6.game.Game;

/**
 * Point d’entrée de l’application : crée et lance l’instance de Game.
 */
public class App {

    private static Game game;

    public static void main(String[] args) {
        init();
    }

    private static void init() {
        game = new Game();
    }
}

