package Groupe6.etats;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Fond en dégradé type soleil levant.
 * Les dimensions sont récupérées au premier draw (via clip) pour éviter de dépendre du GamePanel à la construction.
 */
public class FondDegrade {

    private int width;
    private int height;
    private final Color cielNuit;
    private final Color aurore;
    private GradientPaint gradient;

    /** Construit le fond sans dimensions 
     *  elles seront prises au premier draw. 
     */
    public FondDegrade() {
        this.width = 0;
        this.height = 0;
        this.cielNuit = Color.decode("#C08497");
        this.aurore = Color.decode("#F7E3AF");
        //this.cielNuit = new Color(0x1a, 0x1a, 0x4a);
        //this.aurore = new Color(0xff, 0x8c, 0x64);
    }

    /**
     * Met à jour le gradient.
     */
    private void mettreAJourGradient() {
        this.gradient = new GradientPaint(0, 0, cielNuit, 0, height, aurore);
    }
    /**
     * Dessine le fond.
     * @param g Contexte graphique
     */
    public void draw(Graphics g) {
        // Dimensions prises au premier draw (évite NPE : GamePanel n'existe pas encore dans Start.initClasses)
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
    /**
     * Met à jour le fond.
     */
    public void update() {
        // TODO: Implémenter la mise à jour des images du background
    }
}
