package Groupe6.fond;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import Groupe6.utilz.Constants;
import Groupe6.utilz.HelpMethods;

/**
 * Classe abstraite factorisant toute la logique commune des fonds d'ecran :
 * degradé bicolore, nuages animés et overlay translucide.
 *
 * Les sprites et les positions de nuages sont statiques (partagés)
 * pour eviter les doublons memoire et les sauts visuels au changement de theme.
 *
 * Les sous-classes ne definissent que leur palette via {@link #chargerCouleurs()}
 * et la navigation de theme via clair()/fonce()/catppuccin().
 */
public abstract class AbstractFondDegrade implements Fond {

    // --- Palette (definie par chaque sous-classe) ---
    protected Color cielHaut;
    protected Color cielBas;
    protected Color overlayColor;
    protected float overlayAlpha;

    // --- Cache du gradient (par instance car les couleurs diffèrent) ---
    private GradientPaint cachedTopPaint;
    private GradientPaint cachedBottomPaint;
    private int cachedGradientWidth = -1;
    private int cachedGradientHeight = -1;

    // --- Sprites partagés (chargés une seule fois pour toutes les instances) ---
    private static BufferedImage sharedBigNuage;
    private static BufferedImage[] sharedSmallNuages;
    private static boolean spritesCharges = false;

    // --- Positions de nuages partagées (évite les sauts au changement de thème) ---
    private static float[] bigNuagesXPos;
    private static int[] bigNuagesYPos;
    private static float[] smallNuagesXPos;
    private static int[] smallNuagesYPos;
    private static int indexBigNuagePlusADroite;
    private static int indexSmallNuagePlusADroite;
    private static int currentWidth;
    private static int currentHeight;
    private static boolean nuagesInitialises = false;

    private static final Random RDN = new Random();

    // --- Constantes nuages ---
    private static final float SCROLL_SPEED = 0.75f;
    private static final float BIG_SPEED = SCROLL_SPEED * 0.5f;

    private static final int BIG_NUAGE_WIDTH_DEFAULT = 448;
    private static final int BIG_NUAGE_HEIGHT_DEFAULT = 101;
    private static final int SMALL_NUAGE_1_WIDTH_DEFAULT = 74;
    private static final int SMALL_NUAGE_1_HEIGHT_DEFAULT = 24;
    private static final float SCALING = 1.5f;

    private static final int BIG_NUAGE_WIDTH = (int) (BIG_NUAGE_WIDTH_DEFAULT * SCALING);
    private static final int BIG_NUAGE_HEIGHT = (int) (BIG_NUAGE_HEIGHT_DEFAULT * SCALING);
    private static final int SMALL_NUAGE_1_WIDTH = (int) (SMALL_NUAGE_1_WIDTH_DEFAULT * SCALING * 1.5f);
    private static final int SMALL_NUAGE_1_HEIGHT = (int) (SMALL_NUAGE_1_HEIGHT_DEFAULT * SCALING * 1.5f);
    private static final int SMALL_NUAGE_BASE_Y = 120;
    private static final int SMALL_NUAGE_VARIANCE = 280;

    private static int getBigNuageY() {
        return Constants.game_width / 4;
    }

    // ======================= Construction =======================

    protected AbstractFondDegrade() {
        chargerSpritesPartages();
        chargerCouleurs();
        if (!nuagesInitialises) {
            initNuagePos();
        }
    }

    /** Chaque sous-classe definit sa palette ici. */
    protected abstract void chargerCouleurs();

    /**
     * Couleur utilisee pour le bas du degradé.
     * Par defaut retourne cielBas.brighter(), surcharge possible (ex: Catppuccin).
     */
    protected Color getCouleurBasGradient() {
        return cielBas.brighter();
    }

    // ======================= Fond interface =======================

    @Override
    public void draw(Graphics g) {
        if (currentWidth != Constants.game_width || currentHeight != Constants.game_height) {
            currentWidth = Constants.game_width;
            currentHeight = Constants.game_height;
            initNuagePos();
        }

        Graphics2D g2d = (Graphics2D) g;
        drawFondGradient(g2d);
        drawNuages(g2d);
        drawOverlay(g2d);
    }

    @Override
    public void update() {
        if (!Constants.animationBG) {
            return;
        }
        updateBigNuages();
        updateSmallNuages();
    }

    @Override
    public void reset() {
        initNuagePos();
    }

    // ======================= Sprites (chargement unique) =======================

    private static synchronized void chargerSpritesPartages() {
        if (spritesCharges) return;

        sharedBigNuage = HelpMethods.getSpriteAtlas(HelpMethods.NUAGES + "Big Clouds.png");
        sharedSmallNuages = new BufferedImage[3];
        for (int i = 0; i < 3; i++) {
            sharedSmallNuages[i] = HelpMethods.getSpriteAtlas(HelpMethods.NUAGES + "Small Cloud 1.png");
        }

        if (sharedBigNuage == null) {
            System.err.println("Warning: Failed to load big clouds");
        }
        for (int i = 0; i < sharedSmallNuages.length; i++) {
            if (sharedSmallNuages[i] == null) {
                System.err.println("Warning: Failed to load small cloud " + (i + 1));
            }
        }
        spritesCharges = true;
    }

    // ======================= Positions nuages (partagées) =======================

    private static void initNuagePos() {
        int w = currentWidth;

        int bigNuagesNeeded = Math.max(3, (int) Math.ceil((w * 2.0f) / BIG_NUAGE_WIDTH) + 2);
        bigNuagesXPos = new float[bigNuagesNeeded];
        bigNuagesYPos = new int[bigNuagesNeeded];
        int bigNuageY = getBigNuageY();
        for (int i = 0; i < bigNuagesXPos.length; i++) {
            bigNuagesXPos[i] = i * BIG_NUAGE_WIDTH;
            bigNuagesYPos[i] = bigNuageY;
        }

        int spacing = SMALL_NUAGE_1_WIDTH * 4;
        int smallNuagesNeeded = Math.max(8, (int) Math.ceil((w * 2.0f) / spacing) + 2);
        smallNuagesXPos = new float[smallNuagesNeeded];
        smallNuagesYPos = new int[smallNuagesNeeded];

        for (int i = 0; i < smallNuagesYPos.length; i++) {
            smallNuagesXPos[i] = i * spacing;
            smallNuagesYPos[i] = SMALL_NUAGE_BASE_Y + RDN.nextInt(SMALL_NUAGE_VARIANCE);
        }

        indexBigNuagePlusADroite = bigNuagesXPos.length > 0 ? bigNuagesXPos.length - 1 : 0;
        indexSmallNuagePlusADroite = smallNuagesXPos.length > 0 ? smallNuagesXPos.length - 1 : 0;
        nuagesInitialises = true;
    }

    // ======================= Update nuages =======================

    private static void updateBigNuages() {
        for (int i = 0; i < bigNuagesXPos.length; i++) {
            bigNuagesXPos[i] -= BIG_SPEED;

            if (bigNuagesXPos[i] + BIG_NUAGE_WIDTH < -BIG_NUAGE_WIDTH) {
                float plusADroiteX = bigNuagesXPos[indexBigNuagePlusADroite];
                bigNuagesXPos[i] = plusADroiteX + BIG_NUAGE_WIDTH - 1;
                indexBigNuagePlusADroite = i;
            }

            if (bigNuagesXPos[i] > bigNuagesXPos[indexBigNuagePlusADroite]) {
                indexBigNuagePlusADroite = i;
            }
        }
    }

    private static void updateSmallNuages() {
        for (int i = 0; i < smallNuagesXPos.length; i++) {
            smallNuagesXPos[i] -= SCROLL_SPEED;

            int spacing = SMALL_NUAGE_1_WIDTH * 4;
            if (smallNuagesXPos[i] + SMALL_NUAGE_1_WIDTH < -spacing) {
                float rightmostX = smallNuagesXPos[indexSmallNuagePlusADroite];
                smallNuagesXPos[i] = rightmostX + spacing - 1;
                indexSmallNuagePlusADroite = i;
                smallNuagesYPos[i] = SMALL_NUAGE_BASE_Y + RDN.nextInt(SMALL_NUAGE_VARIANCE);
            }

            if (smallNuagesXPos[i] > smallNuagesXPos[indexSmallNuagePlusADroite]) {
                indexSmallNuagePlusADroite = i;
            }
        }
    }

    // ======================= Draw =======================

    private static void drawNuages(Graphics2D g2d) {
        if (sharedBigNuage != null) {
            for (int i = 0; i < bigNuagesXPos.length; i++) {
                int x = (int) bigNuagesXPos[i];
                if (x + BIG_NUAGE_WIDTH > 0 && x < currentWidth) {
                    g2d.drawImage(sharedBigNuage, x, bigNuagesYPos[i],
                                  BIG_NUAGE_WIDTH, BIG_NUAGE_HEIGHT, null);
                }
            }
        }

        if (sharedSmallNuages != null && sharedSmallNuages.length > 0) {
            for (int i = 0; i < smallNuagesXPos.length; i++) {
                int sx = (int) smallNuagesXPos[i];
                if (sx + SMALL_NUAGE_1_WIDTH > 0 && sx < currentWidth) {
                    BufferedImage img = sharedSmallNuages[i % sharedSmallNuages.length];
                    g2d.drawImage(img, sx, smallNuagesYPos[i],
                                  SMALL_NUAGE_1_WIDTH, SMALL_NUAGE_1_HEIGHT, null);
                }
            }
        }
    }

    private void drawOverlay(Graphics2D g2d) {
        Composite prev = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, overlayAlpha));
        g2d.setColor(overlayColor);
        g2d.fillRect(0, 0, currentWidth, currentHeight);
        g2d.setComposite(prev);
    }

    private void drawFondGradient(Graphics2D g2d) {
        if (cachedTopPaint == null || cachedBottomPaint == null
            || cachedGradientWidth != currentWidth || cachedGradientHeight != currentHeight) {

            int horizonY = currentHeight / 2;

            cachedTopPaint = new GradientPaint(
                0, 0, cielHaut,
                0, horizonY, cielBas
            );

            cachedBottomPaint = new GradientPaint(
                0, horizonY, cielBas,
                0, currentHeight, getCouleurBasGradient()
            );

            cachedGradientWidth = currentWidth;
            cachedGradientHeight = currentHeight;
        }

        int horizonY = currentHeight / 2;

        g2d.setPaint(cachedTopPaint);
        g2d.fillRect(0, 0, currentWidth, horizonY);

        g2d.setPaint(cachedBottomPaint);
        g2d.fillRect(0, horizonY, currentWidth, currentHeight - horizonY);
    }
}
