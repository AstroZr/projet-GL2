package Groupe6.etats;

import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.util.Random;

import Groupe6.utilz.HelpMethods;
import static Groupe6.utilz.Constants.animationBG;

/**
 * Fond d'écran en dégradé vertical (type aurore : ciel sombre en haut, clair en bas).
 * Dimensions initialisées au premier {@link #draw(Graphics)} via le clip pour ne pas dépendre
 * du GamePanel à la construction (évite NPE dans Start).
 */
public class FondDegrade {
    
    private static volatile FondDegrade instance = null;

    private int width;
    private int height;

    private Color cielNuit;
    private Color aurore;
    private Color warmOverlayColor;
    private float warmOverlayAlpha;

    private GradientPaint cachedTopPaint;
    private GradientPaint cachedBottomPaint;
    private int cachedGradientWidth = -1;
    private int cachedGradientHeight = -1;

    private BufferedImage bigNuages;
    private BufferedImage[] smallNuages;

    // Position des nuages
    private float[]bigNuagesXPos;
    private int[]bigNuagesYPos;
    private float[]smallNuagesXPos;
    private int[]smallNuagesYPos;
    
    // Index nuage plus à droite (empèche la recherche linéaire)
    private int indexSmallNuagePlusADroite;
    private int indexBigNuagePlusADroite;

    private static final float scrollSpeed = 0.75f; 
    private static final float BigSpeed = scrollSpeed * 0.5f;
    private Random rdn;
    
    // Constantes pour les nuages
    private final int BIG_NUAGE_WIDTH_DEFAULT = 448;
    private final int BIG_NUAGE_HEIGHT_DEFAULT = 101;
    private final int SMALL_NUAGE_1_WIDTH_DEFAULT = 74;
    private final int SMALL_NUAGE_1_HEIGHT_DEFAULT = 24;
    private final float scaling = 1.5f;

    private final int BIG_NUAGE_Y = (int)(1920 / 4);
    private final int BIG_NUAGE_WIDTH = (int)(BIG_NUAGE_WIDTH_DEFAULT * scaling);
    private final int BIG_NUAGE_HEIGHT = (int)(BIG_NUAGE_HEIGHT_DEFAULT * scaling);

    private final int SMALL_NUAGE_BASE_Y = 120;
    private final int SMALL_NUAGE_1_WIDTH = (int)(SMALL_NUAGE_1_WIDTH_DEFAULT* scaling * 1.5f);
    private final int SMALL_NUAGE_1_HEIGHT = (int)(SMALL_NUAGE_1_HEIGHT_DEFAULT * scaling * 1.5f);
    private final int SMALL_NUAGE_VARIANCE = 280;

    private FondDegrade() {
        this.width = 0;
        this.height = 0;
        this.rdn = new Random();

        chargerCouleurs();
        chargerNuages();
        initNuagePos();
    }

    public static FondDegrade getInstance() {
      if ( instance != null) return instance; 
      
      synchronized ( FondDegrade.class) {
        if ( instance == null) {
          instance = new FondDegrade();
        }
      }
      return instance;
    }

    private void chargerCouleurs(){
        //this.cielNuit = Color.decode("#D88373");
        //this.aurore = Color.decode("#F5E2C8");
        // this.cielNuit = new Color(0x1a,0x1a, 0x4a ); 
        // this.aurore = new Color(0xff, 0x8c, 0x64);
        this.cielNuit         = new Color(0x1a, 0x1a, 0x4a);
        this.aurore           = new Color(0xff, 0x8c, 0x64);
        this.warmOverlayColor = new Color(0xff, 0xe0, 0xc0); // orange doux
        this.warmOverlayAlpha = 0.25f;
    }

    /** Charge les sprites nuages (1 grand + 3 petits, répétés pour un tableau de 5). */
    private void chargerNuages() {
        this.bigNuages = HelpMethods.getSpriteAtlas(HelpMethods.NUAGES + "Big Clouds.png");
        this.smallNuages = new BufferedImage[3];
        
        for (int i = 0; i < 3; i++) {
          smallNuages[i] = HelpMethods.getSpriteAtlas(HelpMethods.NUAGES + "Small Cloud 1.png");
        }

        if (bigNuages == null) {
            System.err.println("Warning: Failed to load big clouds for FondDegrade");
        }
        for (int i = 0; i < smallNuages.length; i++) {
            if (smallNuages[i] == null) {
                System.err.println("Warning: Failed to load small cloud " + (i + 1) + " for FondDegrade");
            }
        }
    }
    /**
     * Dessine le dégradé (et initialise width/height au premier appel si besoin).
     */
    public void draw(Graphics g) {
        if (width <= 0 || height <= 0) {
            Rectangle clip = g.getClipBounds();
            width = (clip != null && clip.width > 0) ? clip.width : 1920;
            height = (clip != null && clip.height > 0) ? clip.height : 1080;
            initNuagePos();
        }
        
        Graphics2D g2d = (Graphics2D) g;
        drawFondGradient(g2d);
        drawNuages(g2d);
        drawOverlay(g2d);
    }

    /** Mise à jour logique du fond (nuages, etc.) ; à appeler chaque frame. */
    public void update() {
      if (!animationBG) {
        return;
      }
      updateBigNuages();
      updateSmallNuages();
    }

    public void reset() {
      initNuagePos();
    }

    /** Initialise des positions random pour les nuages */
    private void initNuagePos() {
        
        int bigNuagesNeeded = Math.max(3, (int) Math.ceil((width * 2.0f) / BIG_NUAGE_WIDTH) + 2);
        bigNuagesXPos = new float[bigNuagesNeeded];
        bigNuagesYPos = new int[bigNuagesNeeded];
        for (int i = 0; i < bigNuagesXPos.length; i++) {
            bigNuagesXPos[i] = i * BIG_NUAGE_WIDTH;
            bigNuagesYPos[i] = BIG_NUAGE_Y;
        }
        
        // Calculer le nombre de petits nuages nécessaires pour couvrir GAME_WIDTH + marge généreuse
        int spacing = SMALL_NUAGE_1_WIDTH * 4;
        int smallNuagesNeeded = Math.max(8, (int) Math.ceil((width * 2.0f) / spacing) + 2);
        smallNuagesXPos = new float[smallNuagesNeeded];
        smallNuagesYPos = new int[smallNuagesNeeded];
        
        for (int i = 0; i < smallNuagesYPos.length; i++) {
            smallNuagesXPos[i] = i * spacing;
            // Range: 120px to 360px (higher in sky for sunset aesthetic)
            smallNuagesYPos[i] = SMALL_NUAGE_BASE_Y + rdn.nextInt(SMALL_NUAGE_VARIANCE);
        }
        
        // Initialiser les index du nuage le plus à droite (le dernier dans le tableau initial)
        indexBigNuagePlusADroite = bigNuagesXPos.length > 0 ? bigNuagesXPos.length - 1 : 0;
        indexSmallNuagePlusADroite = smallNuagesXPos.length > 0 ? smallNuagesXPos.length - 1 : 0;
    }

    

    /** animation / déplacement des nuages. */
    private void updateBigNuages() {


      for(int i = 0; i < bigNuagesXPos.length; i++) {
        bigNuagesXPos[i] -= BigSpeed;

        // Cas nuage trop à gauche
        if (bigNuagesXPos[i] + BIG_NUAGE_WIDTH < -BIG_NUAGE_WIDTH) {
          float plusADroiteX = bigNuagesXPos[indexBigNuagePlusADroite];
          bigNuagesXPos[i] = plusADroiteX + BIG_NUAGE_WIDTH -1;
          indexBigNuagePlusADroite = i;
        }

        if (bigNuagesXPos[i] > bigNuagesXPos[indexBigNuagePlusADroite]) {
          indexBigNuagePlusADroite = i;
        }
      }
    }

    private void updateSmallNuages() {
      for (int i = 0; i < smallNuagesXPos.length; i++) {
        smallNuagesXPos[i] -= scrollSpeed;

        int spacing = SMALL_NUAGE_1_WIDTH * 4;
        if (smallNuagesXPos[i] + SMALL_NUAGE_1_WIDTH < -spacing) {
            float rightmostX = smallNuagesXPos[indexSmallNuagePlusADroite];
            smallNuagesXPos[i] = rightmostX + spacing - 1;
            indexSmallNuagePlusADroite = i;

            // Variété de hauteur
            smallNuagesYPos[i] = SMALL_NUAGE_BASE_Y + rdn.nextInt(SMALL_NUAGE_VARIANCE);
            }

            if (smallNuagesXPos[i] > smallNuagesXPos[indexSmallNuagePlusADroite]) {
                indexSmallNuagePlusADroite = i;
            }
      }
    }

    private void drawNuages(Graphics2D g2d) {
        // Gros nuages (arrière-plan, plus lents)
        if (bigNuages != null) {
            for (int i = 0; i < bigNuagesXPos.length; i++) {
                int x = (int) bigNuagesXPos[i];
                if (x + BIG_NUAGE_WIDTH > 0 && x < width) {
                    g2d.drawImage(bigNuages, x, bigNuagesYPos[i],
                                  BIG_NUAGE_WIDTH, BIG_NUAGE_HEIGHT, null);
                }
            }
        }

        // Petits nuages (premier plan, plus rapides)
        if (smallNuages != null && smallNuages.length > 0) {
            for (int i = 0; i < smallNuagesXPos.length; i++) { 
                if (((int) smallNuagesXPos[i] + SMALL_NUAGE_1_WIDTH) > 0 && ((int) (smallNuagesXPos[i]) < width)) {
                    BufferedImage img = smallNuages[i % smallNuages.length];
                    g2d.drawImage(img,(int) (smallNuagesXPos[i]), smallNuagesYPos[i],
                                  SMALL_NUAGE_1_WIDTH, SMALL_NUAGE_1_HEIGHT, null);
                }
            }
        }
    }
    private void drawOverlay(Graphics2D g2d) {
        Composite prev = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, warmOverlayAlpha));
        g2d.setColor(warmOverlayColor);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(prev);
    }

    private void drawFondGradient(Graphics2D g2d) {

        if (cachedTopPaint == null || cachedBottomPaint == null ||
            cachedGradientWidth != width || cachedGradientHeight != height) {

            int horizonY = height  / 2;

            cachedTopPaint = new GradientPaint(
                0, 0, cielNuit,
                0, horizonY, aurore
            );

            cachedBottomPaint = new GradientPaint(
                0, horizonY, aurore,
                0, height, aurore.brighter()
            );

            cachedGradientWidth  = width;
            cachedGradientHeight = height;
        }

        int horizonY = height / 2;

        g2d.setPaint(cachedTopPaint);
        g2d.fillRect(0, 0, width, horizonY);

        g2d.setPaint(cachedBottomPaint);
        g2d.fillRect(0, horizonY, width, height - horizonY);
    }
}
