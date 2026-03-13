package Groupe6.fond;

import java.awt.Color;
import Groupe6.fond.Fond;

/**
 * Fond d'ecran fonce : ciel nocturne profond, noir bleute en haut, violet sombre en bas.
 */
public class FondFonce extends AbstractFondDegrade {

    private static final Color C_FOND_CELLULE      = new Color(0x12, 0x12, 0x30);
    private static final Color C_TEXTE             = new Color(0xCC, 0xCC, 0xFF);
    private static final Color C_BORDURE_ZONE      = new Color(0x60, 0x60, 0xB0);
    private static final Color C_ETIQUETTE_ZONE    = new Color(0x90, 0xA0, 0xFF);
    private static final Color C_FOND_BOUTON       = new Color(0x1A, 0x1A, 0x3A, 200);
    private static final Color C_FOND_BOUTON_SURVOL = new Color(0x2A, 0x2A, 0x55, 200);
    private static final Color C_FOND_BOUTON_CLIC  = new Color(0x3A, 0x3A, 0x70, 200);
    private static final Color C_BORDRE_BOUTON     = new Color(0x60, 0x60, 0xB0);
    private static final Color C_FOND_INPUT        = new Color(0x15, 0x15, 0x35);
    private static final Color C_FOND_INPUT_FOCUS  = new Color(0x25, 0x25, 0x50);
    private static final Color C_ACCENT            = new Color(0x90, 0xA0, 0xFF);
    private static final Color C_PLACEHOLDER       = new Color(0x70, 0x70, 0x90);

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
        return this;
    }

    @Override
    public Fond catppuccin() {
        return FondCatppuccin.getInstance();
    }
}
