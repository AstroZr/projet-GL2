package Groupe6.aide;

import Groupe6.models.Grille;

public interface Aide {
    int getId();
    int getNbUtilisation();
    AideTextuel getAideTextuel();
    AideVisuel getAideVisuel();
    boolean check(Grille grille);

    // Must be called after check awnser true
    boolean load(Grille grille);
}