package Groupe6.ui;

import java.awt.image.BufferedImage;
import Groupe6.etats.EtatJeu;
import Groupe6.utilz.HelpMethods;

/**
 * Bouton permettant d'accéder à l'aide et aux astuces du jeu.
 * 
 * FONCTIONNALITÉS :
 * - Navigation vers l'écran ASTUCES
 * - Affiche l'aide et les conseils pour jouer
 * 
 * UTILISATION :
 * Bouton accessible depuis n'importe quel écran pour obtenir de l'aide
 */
public class BoutonAide extends Bouton {
    
    public BoutonAide(int x, int y, int largeur, int hauteur, int index) {
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }
    
    @Override
    protected void chargerImages() {
        BufferedImage atlas = HelpMethods.GetSpriteAtlas(HelpMethods.BOUTONS + "aide.png");
        if (atlas != null) {
            img = new BufferedImage[3];
            img[0] = atlas.getSubimage(0, 0, atlas.getWidth(), atlas.getHeight() / 3);
            img[1] = atlas.getSubimage(0, atlas.getHeight() / 3, atlas.getWidth(), atlas.getHeight() / 3);
            img[2] = atlas.getSubimage(0, 2 * atlas.getHeight() / 3, atlas.getWidth(), atlas.getHeight() / 3);
        }
    }
    
    @Override
    public void appliquerAction() {
        // Navigation vers l'écran d'aide et astuces
        EtatJeu.setEtatActuel(EtatJeu.ASTUCES);
    }
}
