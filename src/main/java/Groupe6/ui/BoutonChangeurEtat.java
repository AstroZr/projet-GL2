package Groupe6.ui;

/**
 * Bouton générique pour changer d’état (ex. Menu → Grille) ; l’état cible à définir.
 */
public class BoutonChangeurEtat extends Bouton {
    
    public BoutonChangeurEtat(int x, int y, int largeur, int hauteur, int index) {
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }

    @Override
    public void appliquerAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
