package Groupe6.fond;

import java.awt.Color;

/**
 * Fond « Aurore » : dégradé vertical nuit profonde → coucher de soleil corail.
 * Thème par défaut de l'application.
 */
public class FondDegrade extends AbstractFondDegrade {

    // Cellule & texte
    private static final Color C_FOND_CELLULE        = new Color(0x18, 0x18, 0x3E);
    private static final Color C_TEXTE               = new Color(0xFF, 0xE8, 0xD0);
    // Grille
    private static final Color C_BORDURE_ZONE        = new Color(0xF4, 0x84, 0x5F);
    private static final Color C_ETIQUETTE_ZONE      = new Color(0xFF, 0xAD, 0x8A);
    // Boutons — fond sombre teinté, bordure corail
    private static final Color C_FOND_BOUTON         = new Color(0x1A, 0x1A, 0x42, 200);
    private static final Color C_FOND_BOUTON_SURVOL  = new Color(0x2A, 0x2A, 0x58, 200);
    private static final Color C_FOND_BOUTON_CLIC    = new Color(0x3A, 0x3A, 0x70, 200);
    private static final Color C_BORDURE_BOUTON      = new Color(0xF4, 0x84, 0x5F);
    // Inputs
    private static final Color C_FOND_INPUT          = new Color(0x12, 0x12, 0x32);
    private static final Color C_FOND_INPUT_FOCUS    = new Color(0x22, 0x22, 0x50);
    private static final Color C_ACCENT              = new Color(0xF4, 0x84, 0x5F);
    private static final Color C_PLACEHOLDER         = new Color(0x88, 0x78, 0x68);

    private static volatile FondDegrade instance = null;

    private FondDegrade() { super(); }

    public static FondDegrade getInstance() {
        if (instance != null) return instance;
        synchronized (FondDegrade.class) {
            if (instance == null) instance = new FondDegrade();
        }
        return instance;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut    = new Color(0x0D, 0x0D, 0x26); // nuit profonde
        this.cielBas     = new Color(0xF0, 0x70, 0x45); // coucher de soleil corail
        this.overlayColor = new Color(0xFF, 0xCC, 0xA0);
        this.overlayAlpha = 0.18f;
    }

    @Override public Color getCouleurFondCellule()        { return C_FOND_CELLULE; }
    @Override public Color getCouleurTexte()              { return C_TEXTE; }
    @Override public Color getCouleurBordureZone()        { return C_BORDURE_ZONE; }
    @Override public Color getCouleurEtiquetteZone()      { return C_ETIQUETTE_ZONE; }
    @Override public Color getCouleurFondBouton()         { return C_FOND_BOUTON; }
    @Override public Color getCouleurFondBoutonSurvol()   { return C_FOND_BOUTON_SURVOL; }
    @Override public Color getCouleurFondBoutonClic()     { return C_FOND_BOUTON_CLIC; }
    @Override public Color getCouleurBordreBouton()       { return C_BORDURE_BOUTON; }
    @Override public Color getCouleurFondInput()          { return C_FOND_INPUT; }
    @Override public Color getCouleurFondInputFocus()     { return C_FOND_INPUT_FOCUS; }
    @Override public Color getCouleurAccent()             { return C_ACCENT; }
    @Override public Color getCouleurPlaceholder()        { return C_PLACEHOLDER; }

    @Override public Fond clair()      { return FondClair.getInstance(); }
    @Override public Fond fonce()      { return FondFonce.getInstance(); }
    @Override public Fond catppuccin() { return FondCatppuccin.getInstance(); }
}
