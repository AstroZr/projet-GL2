package Groupe6.ui;

<<<<<<< HEAD
/**
 * Bouton « Paramètres » : ouvre l’écran des options (implémentation à venir).
 */
public class BoutonParametre extends Bouton {

=======
import java.awt.image.BufferedImage;

import Groupe6.etats.EtatJeu;
import Groupe6.utilz.HelpMethods;

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
     * @param index Index de l'image du bouton
     */
>>>>>>> c834f44 (Ajout des classes BoutonAide, BoutonChangeurEtat, BoutonConnexion et BoutonParametre avec gestion des images et actions appropriées pour chaque bouton.)
    public BoutonParametre(int x, int y, int largeur, int hauteur, int index) {
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }

    @Override
    public void appliquerAction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Charge les images du bouton depuis les ressources.
     */
    @Override
    protected void chargerImages() {
        BufferedImage atlas = HelpMethods.GetSpriteAtlas(HelpMethods.BOUTONS + "parametres.png");
        if (atlas != null) {
            img = new BufferedImage[3];
            // Image normale (index 0)
            img[0] = atlas.getSubimage(0, 0, atlas.getWidth(), atlas.getHeight() / 3);
            // Image survol (index 1)
            img[1] = atlas.getSubimage(0, atlas.getHeight() / 3, atlas.getWidth(), atlas.getHeight() / 3);
            // Image cliquée (index 2)
            img[2] = atlas.getSubimage(0, 2 * atlas.getHeight() / 3, atlas.getWidth(), atlas.getHeight() / 3);
        }
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
