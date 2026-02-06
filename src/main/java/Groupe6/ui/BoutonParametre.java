package Groupe6.ui;

/**
 * Bouton permettant d'ouvrir le menu des paramètres du jeu.
 * 
 * FONCTIONNALITÉS :
 * - Ouvre l'écran des paramètres
 * - Charge les images du bouton depuis les ressources
 * 
 * UTILISATION :
 * Bouton utilisé dans le menu principal pour accéder aux paramètres
 */
public class BoutonParametre extends Bouton {

    /**
     * Constructeur du bouton paramètre.
     * 
     * @param x Position X du bouton
     * @param y Position Y du bouton
     * @param largeur Largeur du bouton
     * @param hauteur Hauteur du bouton
     */
    public BoutonParametre(int x, int y, int largeur, int hauteur) {
        super(x, y, largeur, hauteur);
    }
    
    /**
     * Action à effectuer lors du clic sur le bouton.
     * Ouvre le menu des paramètres.
     */
    @Override
    public void appliquerAction() {
        //TODO
    }
}
