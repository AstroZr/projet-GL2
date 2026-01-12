package main;

import game.Game;

public class Main{
    
    private static Game game;

    public static void main(String[] args){
        Init();
    }

    private static void Init(){
        game = new Game();
    }
}