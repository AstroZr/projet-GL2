package Groupe6.fond;

import java.awt.Color;

/**
 * Fond d'ecran fonce : ciel nocturne profond, noir bleute en haut, violet sombre en bas.
 */
public class FondFonce extends AbstractFondDegrade {

    private static volatile FondFonce instance = null;

    private FondFonce() {
        super();
    }

    public static FondFonce getInstance() {
        if (instance != null) return instance;
        synchronized (FondFonce.class) {
            if (instance == null) {
                instance = new FondFonce();
            }
        }
        return instance;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut = new Color(0x0B, 0x0B, 0x2B);
        this.cielBas = new Color(0x2D, 0x1B, 0x4E);
        this.overlayColor = new Color(0x19, 0x19, 0x40);
        this.overlayAlpha = 0.15f;
    }

    @Override
    public Fond clair() {
        return FondClair.getInstance();
    }

    @Override
    public Fond fonce() {
        return this;
    }

    @Override
    public Fond catppuccin() {
        return FondCatppuccin.getInstance();
    }
}
