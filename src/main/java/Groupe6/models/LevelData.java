package Groupe6.models;

public class LevelData {
    public int niveauId;
    public int tailleGrille;
    public int score;
    public int compteurErreur;
    public long tempsEcoule;
    public long meilleurTemp;

    public LevelData(int niveauId, int tailleGrille) {
        this.niveauId = niveauId;
        this.tailleGrille = tailleGrille;
    }
}
