package Groupe6.fond;

import java.awt.Color;

/**
 * Fond d'ecran clair : ciel bleu pastel en haut, blanc creme en bas.
 */
public class FondClair extends AbstractFondDegrade {

    private static volatile FondClair instance = null;

    private FondClair() {
        super();
    }

    public static FondClair getInstance() {
        if (instance != null) return instance;
        synchronized (FondClair.class) {
            if (instance == null) {
                instance = new FondClair();
            }
        }
        return instance;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut = new Color(0x87, 0xCE, 0xEB);
        this.cielBas = new Color(0xFD, 0xF5, 0xE6);
        this.overlayColor = new Color(0xFF, 0xFF, 0xF0);
        this.overlayAlpha = 0.10f;
    }

    @Override
    public Fond clair() {
        return this;
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
