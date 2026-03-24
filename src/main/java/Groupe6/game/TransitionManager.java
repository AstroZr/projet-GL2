package Groupe6.game;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

/**
 * Transitions entre états : vague sinusoïdale lumineuse balayant de gauche à droite.
 * L'ancien état reste visible à droite de la vague jusqu'à sortie de l'écran.
 * Bord de vague rendu avec glow multicouche. Aucun écran noir.
 */
public class TransitionManager {

    private static final long DUREE_NS = 500_000_000L; // 500 ms

    // Paramètres visuels de la vague
    private static final float AMPLITUDE = 60f;   // hauteur de la sinusoïde en px
    private static final float FREQUENCE  = 2.8f;  // nombre de périodes sur la hauteur

    // Résolution de la vague : 120 points suffisent pour un rendu fluide
    private static final int WAVE_STEPS = 120;

    // BasicStrokes pré-allouées (immuables, réutilisées chaque frame)
    private static final BasicStroke STROKE_HALO   = new BasicStroke(44f,  BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    private static final BasicStroke STROKE_MEDIUM = new BasicStroke(18f,  BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    private static final BasicStroke STROKE_FIN    = new BasicStroke(4f,   BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
    private static final BasicStroke STROKE_LIGNE  = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

    // Tableaux/chemins pré-alloués, réinitialisés à chaque frame
    private final float[] wx = new float[WAVE_STEPS + 1];
    private final GeneralPath curtain  = new GeneralPath();
    private final GeneralPath wavePath = new GeneralPath();

    private boolean enTransition = false;
    private long debutNs = 0;
    private BufferedImage ancienFrame = null;

    /** Enregistre le dernier frame de l'état sortant et démarre la transition. */
    public void notifierChangementEtat(BufferedImage frameAConserver) {
        if (frameAConserver != null) {
            ancienFrame = new BufferedImage(
                frameAConserver.getWidth(), frameAConserver.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = ancienFrame.createGraphics();
            g.drawImage(frameAConserver, 0, 0, null);
            g.dispose();
        }
        enTransition = true;
        debutNs = System.nanoTime();
    }

    public void update() {
        if (enTransition && System.nanoTime() - debutNs >= DUREE_NS) {
            enTransition = false;
            ancienFrame = null;
        }
    }

    public boolean isEnTransition() { return enTransition; }

    /**
     * Dessine la transition.
     * - Zone droite de la vague : ancien état (rideau qui se referme vers la droite)
     * - Bord de vague : glow blanc multicouche (large → étroit → ligne fine)
     */
    public void draw(Graphics g, int width, int height) {
        if (!enTransition || ancienFrame == null) return;

        float raw = Math.min(1f, (float)(System.nanoTime() - debutNs) / DUREE_NS);

        // Easing cubique ease-in-out pour le mouvement spatial
        float t = raw < 0.5f
            ? 4f * raw * raw * raw
            : 1f - (float) Math.pow(-2f * raw + 2f, 3) / 2f;

        // Phase animée pour que la vague "coule" visuellement
        float phase = raw * (float)(Math.PI * 4);

        // Position du centre de la vague : de -AMPLITUDE à width + AMPLITUDE
        float sweepX = t * (width + AMPLITUDE * 2f) - AMPLITUDE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Précalcul des X de la vague (résolution réduite à WAVE_STEPS) ---
        for (int i = 0; i <= WAVE_STEPS; i++) {
            float fy = (float) i / WAVE_STEPS;
            wx[i] = sweepX + AMPLITUDE * (float) Math.sin(fy * FREQUENCE * 2f * Math.PI + phase);
        }

        // --- 1. Dessiner l'ancien état clippé à la zone droite de la vague ---
        curtain.reset();
        curtain.moveTo(wx[0], 0);
        curtain.lineTo(width, 0);
        curtain.lineTo(width, height);
        curtain.lineTo(wx[WAVE_STEPS], height);
        for (int i = WAVE_STEPS - 1; i >= 0; i--) {
            curtain.lineTo(wx[i], (float) i / WAVE_STEPS * height);
        }
        curtain.closePath();

        Shape savedClip = g2d.getClip();
        g2d.clip(curtain);
        g2d.drawImage(ancienFrame, 0, 0, width, height, null);
        g2d.setClip(savedClip);

        // --- 2. Chemin de la vague (pour le glow) ---
        wavePath.reset();
        wavePath.moveTo(wx[0], 0);
        for (int i = 1; i <= WAVE_STEPS; i++) {
            wavePath.lineTo(wx[i], (float) i / WAVE_STEPS * height);
        }

        // Glow qui s'estompe à mesure que la transition avance
        float glowFade = Math.max(0f, 1f - raw * 1.4f);

        Composite prevComp = g2d.getComposite();
        g2d.setColor(Color.WHITE);

        // Couche 1 — halo large (44px), très transparent
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f * glowFade));
        g2d.setStroke(STROKE_HALO);
        g2d.draw(wavePath);

        // Couche 2 — halo moyen (18px)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.32f * glowFade));
        g2d.setStroke(STROKE_MEDIUM);
        g2d.draw(wavePath);

        // Couche 3 — trait lumineux fin (4px)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.70f * glowFade));
        g2d.setStroke(STROKE_FIN);
        g2d.draw(wavePath);

        // Couche 4 — ligne centrale blanche pure (1.5px)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.90f * glowFade));
        g2d.setStroke(STROKE_LIGNE);
        g2d.draw(wavePath);

        g2d.setComposite(prevComp);
    }
}
