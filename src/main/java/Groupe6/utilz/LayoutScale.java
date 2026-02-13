package Groupe6.utilz;

/**
 * Facteurs de scaling pour le layout UI (Singleton mutable + Facade).
 * Centralise scaleX, scaleY, scale uniforme et helpers de position.
 * Mise à jour via update(w, h) avant utilisation.
 */
public class LayoutScale {

    private volatile static LayoutScale INSTANCE = null;

    private int width;
    private int height;
    private float scaleX;
    private float scaleY;
    private float scale;

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
