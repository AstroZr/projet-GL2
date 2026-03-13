package Groupe6.fond;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import Groupe6.utilz.HelpMethods;

/**
 * Fond d'ecran aux couleurs Catppuccin Mocha :
 * base #1E1E2E (crust) en haut, lavender #B4BEFE en bas,
 * overlay mauve #CBA6F7 translucide, surface1 #45475A en bas du gradient.
 */
public class FondCatppuccin extends AbstractFondDegrade {

    private static final Color C_FOND_CELLULE       = new Color(0x31, 0x32, 0x44);
    private static final Color C_TEXTE              = new Color(0xCD, 0xD6, 0xF4);
    private static final Color C_BORDURE_ZONE       = new Color(0x6C, 0x70, 0x86);
    private static final Color C_ETIQUETTE_ZONE     = new Color(0xB4, 0xBE, 0xFE);
    private static final Color C_FOND_BOUTON        = new Color(0x31, 0x32, 0x44, 200);
    private static final Color C_FOND_BOUTON_SURVOL = new Color(0x45, 0x47, 0x5A, 200);
    private static final Color C_FOND_BOUTON_CLIC   = new Color(0x58, 0x5B, 0x70, 200);
    private static final Color C_BORDRE_BOUTON      = new Color(0x6C, 0x70, 0x86);
    private static final Color C_FOND_INPUT         = new Color(0x1E, 0x1E, 0x2E);
    private static final Color C_FOND_INPUT_FOCUS   = new Color(0x31, 0x32, 0x44);
    private static final Color C_ACCENT             = new Color(0x89, 0xB4, 0xFA);
    private static final Color C_PLACEHOLDER        = new Color(0xA6, 0xAD, 0xC8);
    private static final Color C_BAS_GRADIENT       = new Color(0x45, 0x47, 0x5A);

    private static volatile FondCatppuccin instance = null;
    private static BufferedImage wallpaper;

    private FondCatppuccin() {
        super();
        wallpaper = HelpMethods.getSpriteAtlas(HelpMethods.WALLPAPER + "Catppuccin.png");
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

    @Override
    public Color getCouleurFondCellule() { return C_FOND_CELLULE; } // surface0

    @Override
    public Color getCouleurTexte() { return C_TEXTE; } // text

    @Override
    public Color getCouleurBordureZone() { return C_BORDURE_ZONE; } // overlay0

    @Override
    public Color getCouleurEtiquetteZone() { return C_ETIQUETTE_ZONE; } // lavender

    @Override
    public Color getCouleurFondBouton() { return C_FOND_BOUTON; } // surface0

    @Override
    public Color getCouleurFondBoutonSurvol() { return C_FOND_BOUTON_SURVOL; } // surface1

    @Override
    public Color getCouleurFondBoutonClic() { return C_FOND_BOUTON_CLIC; } // surface2

    @Override
    public Color getCouleurBordreBouton() { return C_BORDRE_BOUTON; } // overlay0

    @Override
    public Color getCouleurFondInput() { return C_FOND_INPUT; } // base

    @Override
    public Color getCouleurFondInputFocus() { return C_FOND_INPUT_FOCUS; } // surface0

    @Override
    public Color getCouleurAccent() { return C_ACCENT; } // blue

    @Override
    public Color getCouleurPlaceholder() { return C_PLACEHOLDER; } // subtext0

    @Override
    protected void dessinerFondBase(Graphics2D g2d) {
        if (wallpaper != null) {
            g2d.drawImage(wallpaper, 0, 0, currentWidth, currentHeight, null);
        } else {
            super.dessinerFondBase(g2d);
        }
    }

    /** Surface1 Catppuccin au lieu de cielBas.brighter() */
    @Override
    protected Color getCouleurBasGradient() {
        return C_BAS_GRADIENT;
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
