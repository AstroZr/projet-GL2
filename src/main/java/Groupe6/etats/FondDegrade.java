package Groupe6.etats;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import Groupe6.utilz.HelpMethods;

/**
 * Fond d'écran en dégradé vertical (type aurore : ciel sombre en haut, clair en bas).
 * Dimensions initialisées au premier {@link #draw(Graphics)} via le clip pour ne pas dépendre
 * du GamePanel à la construction (évite NPE dans Start).
 */
public class FondDegrade {

    private int width;
    private int height;
    private Color cielNuit;
    private Color aurore;
    private GradientPaint gradient;
    private BufferedImage[] nuages;

    public FondDegrade() {
        this.width = 0;
        this.height = 0;
        chargerCouleurs();
        chargerNuages();
    }

    private void chargerCouleurs(){
        this.cielNuit = Color.decode("#D88373");
        this.aurore = Color.decode("#F5E2C8");
        // this.cielNuit = new Color(0x1a,0x1a, 0x4a ); 
        // this.aurore = new Color(0xff, 0x8c, 0x64)
    }

    private void mettreAJourGradient() {
        this.gradient = new GradientPaint(0, 0, cielNuit, 0, height, aurore);
    }

    /**
     * Dessine le dégradé (et initialise width/height au premier appel si besoin).
     */
    public void draw(Graphics g) {
        if (width <= 0 || height <= 0) {
            Rectangle clip = g.getClipBounds();
            width = (clip != null && clip.width > 0) ? clip.width : 1920;
            height = (clip != null && clip.height > 0) ? clip.height : 1080;
            mettreAJourGradient();
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

    }

    /** Mise à jour logique du fond (nuages, etc.) ; à appeler chaque frame. */
    public void update() {
        updateNuages();
    }

    /** Charge les sprites nuages (1 grand + 3 petits, répétés pour un tableau de 5). */
    private void chargerNuages() {
        BufferedImage bigCloud = HelpMethods.getSpriteAtlas(HelpMethods.NUAGES + "Big Clouds.png");
        BufferedImage[] smallClouds = new BufferedImage[3];
        for (int i = 0; i < 3; i++) {
            smallClouds[i] = HelpMethods.getSpriteAtlas(HelpMethods.NUAGES + "Small Cloud " + (i + 1) + ".png");
        }
        nuages = new BufferedImage[5];
        nuages[0] = bigCloud;
        System.arraycopy(smallClouds, 0, nuages, 1, 3);
        System.arraycopy(smallClouds, 0, nuages, 4, 1);
    }

    /** Réservé à l’animation ou au déplacement des nuages. */
    private void updateNuages() {
      
    }

    private void drawNuages(Graphics g) {
      for(BufferedImage img : nuages) {
          
      }
    }
}
