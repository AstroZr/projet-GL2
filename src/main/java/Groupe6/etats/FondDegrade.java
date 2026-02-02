package Groupe6.etats;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import Groupe6.utilz.HelpMethods;
import static Groupe6.utilz.Constants.animationBG;
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
    private BufferedImage[] bigNuages;
    private BufferedImage[] smallNuages;

    // Position des nuages
    private BufferedImage[]bigNuagesXPos;
    private BufferedImage[]bigNuagesYPos;
    private BufferedImage[]smallNuagesXPos;
    private BufferedImage[]smallNuagesYPos;
    
    // Index nuage plus à droite (empèche la recherche linéaire)
    private int indexSmallNuagePlusADroite;
    private int indexBigNuagePlusADroite;

    private Random rdn;

    private GradientPaint cachedTopPaint;
    private GradientPaint cachedBottomPaint;
    private int cachedGradientWidth = -1;
    private int cachedGradientHeight = -1;


    private final int BIG_CLOUD_Y_DEFAULT = 0;
    private final int BIG_CLOUD_X_DEFAULT = 0;
    private final int BIG_CLOUD_Y = 0;
    private final int BIG_CLOUD_X = 0;
    private final int BIG_CLOUD_WIDTH_DEFAULT = 448;
    private final int BIG_CLOUD_HEIGHT_DEFAULT = 101;
    private final int BIG_CLOUD_WIDTH = (int)();
    private final int BIG_CLOUUD_HEIGHT = (int)();

    public FondDegrade() {
        this.width = 0;
        this.height = 0; 
        chargerCouleurs();
        chargerNuages();
    }

    private void chargerCouleurs(){
        //this.cielNuit = Color.decode("#D88373");
        //this.aurore = Color.decode("#F5E2C8");
        this.cielNuit = new Color(0x1a,0x1a, 0x4a ); 
        this.aurore = new Color(0xff, 0x8c, 0x64);
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


     /** Initialise des positions random pour les nuages */
    private void initializeCloudPositions() {
        
        int bigCloudsNeeded = Math.max(3, (int) Math.ceil((width * 2.0f) / BIG_CLOUDS_WIDTH) + 2);
        bigCloudsXPos = new float[bigCloudsNeeded];
        bigCloudsYPos = new int[bigCloudsNeeded];
        for (int i = 0; i < bigCloudsXPos.length; i++) {
            bigCloudsXPos[i] = i * BIG_CLOUDS_WIDTH;
            bigCloudsYPos[i] = BIG_CLOUD_Y;
        }
        
        // Calculer le nombre de petits nuages nécessaires pour couvrir GAME_WIDTH + marge généreuse
        int spacing = SMALL_CLOUD_1_WIDTH * 4;
        int smallCloudsNeeded = Math.max(8, (int) Math.ceil((GAME_WIDTH * 2.0f) / spacing) + 2);
        smallCloudsXPos = new float[smallCloudsNeeded];
        smallCloudsYPos = new int[smallCloudsNeeded];
        
        for (int i = 0; i < smallCloudsYPos.length; i++) {
            smallCloudsXPos[i] = i * spacing;
            // Range: 120px to 360px (higher in sky for sunset aesthetic)
            smallCloudsYPos[i] = SMALL_CLOUD_BASE_Y + random.nextInt(SMALL_CLOUD_VARIANCE);
        }
        
        // Initialiser les index du nuage le plus à droite (le dernier dans le tableau initial)
        rightmostBigCloudIndex = bigCloudsXPos.length > 0 ? bigCloudsXPos.length - 1 : 0;
        rightmostSmallCloudIndex = smallCloudsXPos.length > 0 ? smallCloudsXPos.length - 1 : 0;
    }

    

    /** Réservé à l’animation ou au déplacement des nuages. */
    private void updateNuages() {
      
    }

    private void drawNuages(Graphics g) {
      for(BufferedImage img : nuages) {
        g.drawImage(img, x, y, largeur, hauteur, null);
      }
    }
}
