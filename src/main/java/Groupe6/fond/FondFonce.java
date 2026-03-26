package Groupe6.fond;

import java.awt.Color;

/**
 * Fond « Nuit » : espace profond presque noir, accents cyan électrique.
 * Contraste maximal, lisibilité parfaite sur fond sombre.
 */
public class FondFonce extends AbstractFondDegrade {

    // Cellule & texte
    private static final Color C_FOND_CELLULE        = new Color(0x0A, 0x0A, 0x18);
    private static final Color C_TEXTE               = new Color(0xE8, 0xEA, 0xF4);
    // Grille — ardoise bleue + accent cyan
    private static final Color C_BORDURE_ZONE        = new Color(0x3A, 0x4A, 0x7A);
    private static final Color C_ETIQUETTE_ZONE      = new Color(0x5C, 0xCF, 0xE8);
    // Boutons — quasi noirs, bordure cyan subtile
    private static final Color C_FOND_BOUTON         = new Color(0x0E, 0x0E, 0x20, 210);
    private static final Color C_FOND_BOUTON_SURVOL  = new Color(0x18, 0x22, 0x38, 210);
    private static final Color C_FOND_BOUTON_CLIC    = new Color(0x22, 0x30, 0x4E, 210);
    private static final Color C_BORDURE_BOUTON      = new Color(0x3A, 0x5A, 0x8A);
    // Inputs — noirs purs avec accent cyan au focus
    private static final Color C_FOND_INPUT          = new Color(0x08, 0x08, 0x14);
    private static final Color C_FOND_INPUT_FOCUS    = new Color(0x10, 0x18, 0x28);
    private static final Color C_ACCENT              = new Color(0x5C, 0xCF, 0xE8);
    private static final Color C_PLACEHOLDER         = new Color(0x48, 0x50, 0x68);

    private static final FondFonce INSTANCE = new FondFonce();

    private FondFonce() { super(); }

    public static FondFonce getInstance() {
        return INSTANCE;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut    = new Color(0x05, 0x05, 0x10); // noir espace
        this.cielBas     = new Color(0x10, 0x18, 0x32); // ardoise profonde
        this.overlayColor = new Color(0x08, 0x10, 0x28);
        this.overlayAlpha = 0.20f;
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
    @Override public Fond fonce()      { return this; }
    @Override public Fond catppuccin() { return FondCatppuccin.getInstance(); }
}
