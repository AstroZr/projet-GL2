package Groupe6.fond;

import java.awt.Color;

/**
 * Fond d'ecran en degrade vertical type aurore : ciel sombre en haut, orange chaud en bas.
 * Theme par defaut de l'application.
 */
public class FondDegrade extends AbstractFondDegrade {

    private static final Color C_FOND_CELLULE       = new Color(0x25, 0x25, 0x55);
    private static final Color C_TEXTE              = new Color(0xFF, 0xE0, 0xC0);
    private static final Color C_BORDURE_ZONE       = new Color(0xFF, 0x8C, 0x64);
    private static final Color C_ETIQUETTE_ZONE     = new Color(0xFF, 0xB0, 0x80);
    private static final Color C_FOND_BOUTON        = new Color(0x25, 0x25, 0x55, 180);
    private static final Color C_FOND_BOUTON_SURVOL = new Color(0x35, 0x35, 0x70, 180);
    private static final Color C_FOND_BOUTON_CLIC   = new Color(0x45, 0x45, 0x88, 180);
    private static final Color C_BORDRE_BOUTON      = new Color(0xFF, 0x8C, 0x64);
    private static final Color C_FOND_INPUT         = new Color(0x1A, 0x1A, 0x45);
    private static final Color C_FOND_INPUT_FOCUS   = new Color(0x2A, 0x2A, 0x60);
    private static final Color C_ACCENT             = new Color(0xFF, 0x8C, 0x64);
    private static final Color C_PLACEHOLDER        = new Color(0x99, 0x88, 0x77);

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
    public Color getCouleurFondCellule() { return C_FOND_CELLULE; }

    @Override
    public Color getCouleurTexte() { return C_TEXTE; }

    @Override
    public Color getCouleurBordureZone() { return C_BORDURE_ZONE; }

    @Override
    public Color getCouleurEtiquetteZone() { return C_ETIQUETTE_ZONE; }

    @Override
    public Color getCouleurFondBouton() { return C_FOND_BOUTON; }

    @Override
    public Color getCouleurFondBoutonSurvol() { return C_FOND_BOUTON_SURVOL; }

    @Override
    public Color getCouleurFondBoutonClic() { return C_FOND_BOUTON_CLIC; }

    @Override
    public Color getCouleurBordreBouton() { return C_BORDRE_BOUTON; }

    @Override
    public Color getCouleurFondInput() { return C_FOND_INPUT; }

    @Override
    public Color getCouleurFondInputFocus() { return C_FOND_INPUT_FOCUS; }

    @Override
    public Color getCouleurAccent() { return C_ACCENT; }

    @Override
    public Color getCouleurPlaceholder() { return C_PLACEHOLDER; }

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
