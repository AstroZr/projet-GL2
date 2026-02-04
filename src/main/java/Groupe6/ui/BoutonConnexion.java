package Groupe6.ui;

import java.awt.image.BufferedImage;
import Groupe6.etats.EtatJeu;
import Groupe6.utilz.HelpMethods;

/**
 * Bouton permettant la connexion au jeu.
 * 
 * FONCTIONNALITÉS :
 * - Gère la connexion de l'utilisateur
 * - Navigation automatique vers le MENU après connexion réussie
 * 
 * UTILISATION :
 * Bouton utilisé dans l'écran START pour se connecter
 */
public class BoutonConnexion extends Bouton {
    
    public BoutonConnexion(int x, int y, int largeur, int hauteur, int index) {
        super(x, y, largeur, hauteur, index);
        chargerImages();
    }
    
    @Override
    protected void chargerImages() {
        BufferedImage atlas = HelpMethods.GetSpriteAtlas(HelpMethods.BOUTONS + "connexion.png");
        if (atlas != null) {
            img = new BufferedImage[3];
            img[0] = atlas.getSubimage(0, 0, atlas.getWidth(), atlas.getHeight() / 3);
            img[1] = atlas.getSubimage(0, atlas.getHeight() / 3, atlas.getWidth(), atlas.getHeight() / 3);
            img[2] = atlas.getSubimage(0, 2 * atlas.getHeight() / 3, atlas.getWidth(), atlas.getHeight() / 3);
        }
    }
    
    @Override
    public void appliquerAction() {
    }
}
