package Groupe6.ui;

public class BoutonChangeurEtat extends Bouton {
    
    public BoutonChangeurEtat(int x, int y, int largeur, int hauteur, int index){
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }

    @Override
    public void appliquerAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
