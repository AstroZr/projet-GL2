package Groupe6.utilz;

/**
 * Gestionnaire du scaling responsive pour l'UI (Singleton mutable + Facade).
 * 
 * Responsabilités:
 * - Centraliser tous les facteurs de scale (scaleX, scaleY, scale uniforme)
 * - Fournir des helpers pour positionner et redimensionner les éléments
 * - Support des ratios (pourcentage) de layout
 * 
 * Workflow:
 * 1. GamePanel notifie Constants.game_width/height au resize
 * 2. States appelle layoutScale.update(width, height) dans updateLayout
 * 3. States utilisent scalex(), scaleY(), scaleUniform() pour calc positions
 * 
 * Formules:
 * - scaleX = game_width / REF_WIDTH (ex: 1920/1920 = 1.0)
 * - scaleY = game_height / REF_HEIGHT (ex: 1080/1080 = 1.0)
 * - scale = min(scaleX, scaleY) pour aspect ratio preservé
 * - position = baseValue * scale
 * 
 * Pattern: Singleton thread-safe
 */
public class LayoutScale {

    // ====== SINGLETON ======
    private volatile static LayoutScale INSTANCE = null;

    // ====== DIMENSIONS ACTUELLES ======
    private int width;    // game_width courant
    private int height;   // game_height courant
    
    // ====== FACTEURS DE SCALE ======
    private float scaleX; // Ratio horizontal (1.0 = référence)
    private float scaleY; // Ratio vertical
    private float scale;  // Scale uniforme = min(scaleX, scaleY)

    private LayoutScale() {
    }

    public static LayoutScale getInstance() {
      if (INSTANCE != null) return INSTANCE;

      synchronized ( LayoutScale.class) {
        if ( INSTANCE == null) {
          INSTANCE = new LayoutScale();
        }
      }
      return INSTANCE;
    }

    /**
     * Met à jour les dimensions et recalcule les facteurs de scale.
     * À appeler avant utilisation (ex. dans updateLayout).
     */
    public void update(int width, int height) {
        this.width = width;
        this.height = height;
        this.scaleX = (float) width / Constants.REF_WIDTH;
        this.scaleY = (float) height / Constants.REF_HEIGHT;
        this.scale = Math.min(scaleX, scaleY);
    }

    /** Valeur en pixels selon le scale uniforme (logo, gaps). */
    public int scaleUniform(int refValue) {
        return (int) (refValue * scale);
    }

    /** Valeur en pixels selon scaleX (largeurs). */
    public int scaleX(int refValue) {
        return (int) (refValue * scaleX);
    }

    /** Valeur en pixels selon scaleY (hauteurs). */
    public int scaleY(int refValue) {
        return (int) (refValue * scaleY);
    }

    /** Position X centrée (ratio 0.5 par défaut). */
    public int centerX() {
        return (int) (width * Constants.Ratios.RATIO_CENTER_X);
    }

    /** Position X selon un ratio horizontal (0f à 1f). */
    public int ratioX(float ratio) {
        return (int) (width * ratio);
    }

    /** Position Y selon un ratio vertical (0f à 1f). */
    public int ratioY(float ratio) {
        return (int) (height * ratio);
    }
}
