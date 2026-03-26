package Groupe6.aide;

import Groupe6.models.Grille;

public interface Aide {
    int getId();
    int getNbUtilisation();
    void setNbUtilisation(int nbUtilisation);
    AideTextuel getAideTextuel();
    AideVisuel getAideVisuel();
    boolean check(Grille grille);
    String getTitre();
    String getDescription();

    // Must be called after check awnser true
    int load(Grille grille, int nbAides);
}