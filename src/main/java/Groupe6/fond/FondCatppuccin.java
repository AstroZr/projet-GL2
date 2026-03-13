package Groupe6.fond;

import java.awt.Color;

/**
 * Fond d'ecran aux couleurs Catppuccin Mocha :
 * base #1E1E2E (crust) en haut, lavender #B4BEFE en bas,
 * overlay mauve #CBA6F7 translucide, surface1 #45475A en bas du gradient.
 */
public class FondCatppuccin extends AbstractFondDegrade {

    private static volatile FondCatppuccin instance = null;

    private FondCatppuccin() {
        super();
    }

    public static FondCatppuccin getInstance() {
        if (instance != null) return instance;
        synchronized (FondCatppuccin.class) {
            if (instance == null) {
                instance = new FondCatppuccin();
            }
        }
        return instance;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut = new Color(0x1E, 0x1E, 0x2E);
        this.cielBas = new Color(0xB4, 0xBE, 0xFE);
        this.overlayColor = new Color(0xCB, 0xA6, 0xF7);
        this.overlayAlpha = 0.12f;
    }

    /** Surface1 Catppuccin au lieu de cielBas.brighter() */
    @Override
    protected Color getCouleurBasGradient() {
        return new Color(0x45, 0x47, 0x5A);
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
        return this;
    }
}
