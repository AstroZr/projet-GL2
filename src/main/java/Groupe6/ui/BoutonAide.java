package Groupe6.ui;

/**
 * Bouton « Aide » : ouvre l’écran ou la fenêtre d’aide (implémentation à venir).
 */
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
