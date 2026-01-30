package Groupe6.ui;

public class BoutonAide extends Bouton {
    
    public BoutonAide(int x, int y, int largeur, int hauteur, int index) {
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }

    @Override
    public void appliquerAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
