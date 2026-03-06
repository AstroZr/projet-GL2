package Groupe6.fond;

import java.awt.Color;

/**
 * Fond d'ecran en degrade vertical type aurore : ciel sombre en haut, orange chaud en bas.
 * Theme par defaut de l'application.
 */
public class FondDegrade extends AbstractFondDegrade {

    private static volatile FondDegrade instance = null;

    private FondDegrade() {
        super();
    }

    public static FondDegrade getInstance() {
        if (instance != null) return instance;
        synchronized (FondDegrade.class) {
            if (instance == null) {
                instance = new FondDegrade();
            }
        }
        return instance;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut = new Color(0x1a, 0x1a, 0x4a);
        this.cielBas = new Color(0xff, 0x8c, 0x64);
        this.overlayColor = new Color(0xff, 0xe0, 0xc0);
        this.overlayAlpha = 0.25f;
    }

    @Override
    public Fond clair() {
        return FondClair.getInstance();
    }

    @Override
    public Fond fonce() {
        return FondFonce.getInstance();
    }

    @Override
    public Fond catppuccin() {
        return FondCatppuccin.getInstance();
    }
}
