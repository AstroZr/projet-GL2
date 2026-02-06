package Groupe6.ui;

<<<<<<< HEAD
<<<<<<< HEAD
/**
 * Bouton générique pour changer d’état (ex. Menu → Grille) ; l’état cible à définir.
 */
public class BoutonChangeurEtat extends Bouton {
    
    public BoutonChangeurEtat(int x, int y, int largeur, int hauteur, int index) {
=======
import java.awt.image.BufferedImage;
import Groupe6.etats.EtatJeu;
import Groupe6.utilz.HelpMethods;

/**
 * Bouton permettant de changer l'état du jeu.
 * 
 * FONCTIONNALITÉS :
 * - Change l'état du jeu de manière intelligente selon l'état actuel
 * - Navigation automatique : START -> MENU, MENU -> SELECTION, etc.
 * - Charge les images du bouton depuis les ressources
 * 
 * UTILISATION :
 * Créer simplement le bouton, il détermine automatiquement vers quel état naviguer
 */
public class BoutonChangeurEtat extends Bouton {
=======
import java.awt.image.BufferedImage;
import Groupe6.etats.EtatJeu;
import Groupe6.utilz.HelpMethods;

/**
 * Bouton permettant de changer l'état du jeu.
 * 
 * FONCTIONNALITÉS :
 * - Change l'état du jeu de manière intelligente selon l'état actuel
 * - Navigation automatique : START -> MENU, MENU -> SELECTION, etc.
 * - Charge les images du bouton depuis les ressources
 * 
 * UTILISATION :
 * Créer simplement le bouton, il détermine automatiquement vers quel état naviguer
 */
public class BoutonChangeurEtat extends Bouton {
>>>>>>> c834f44ae80db84f62b3b4f1b720d0bf51bec63f
    private EtatJeu action;
    /**
     * Constructeur du bouton changeur d'état.
     * 
     * @param x Position X du bouton
     * @param y Position Y du bouton
     * @param largeur Largeur du bouton
     * @param hauteur Hauteur du bouton
     * @param index Index de l'image du bouton
     */
    public BoutonChangeurEtat(int x, int y, int largeur, int hauteur, int index, EtatJeu action) {
<<<<<<< HEAD
>>>>>>> c834f44 (Ajout des classes BoutonAide, BoutonChangeurEtat, BoutonConnexion et BoutonParametre avec gestion des images et actions appropriées pour chaque bouton.)
=======
>>>>>>> c834f44ae80db84f62b3b4f1b720d0bf51bec63f
        super(x, y, largeur, hauteur, index);
        this.action = action;
        chargerImages();
    }
<<<<<<< HEAD
<<<<<<< HEAD

    @Override
    public void appliquerAction() {
        throw new UnsupportedOperationException("Not supported yet.");
=======
=======
>>>>>>> c834f44ae80db84f62b3b4f1b720d0bf51bec63f
    
    /**
     * Charge les images du bouton depuis les ressources.
     */
    @Override
    protected void chargerImages() {
        BufferedImage atlas = HelpMethods.GetSpriteAtlas(HelpMethods.BOUTONS + "changeur.png");
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

    public void appliquerAction(){
        EtatJeu.setEtatActuel(action);
<<<<<<< HEAD
>>>>>>> c834f44 (Ajout des classes BoutonAide, BoutonChangeurEtat, BoutonConnexion et BoutonParametre avec gestion des images et actions appropriées pour chaque bouton.)
=======
>>>>>>> c834f44ae80db84f62b3b4f1b720d0bf51bec63f
    }
}
