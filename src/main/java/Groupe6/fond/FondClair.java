package Groupe6.fond;

import java.awt.Color;

/**
 * Fond « Jour » : ciel bleu pâle → sable chaud, palette claire complète.
 * Texte ardoise foncé, boutons blancs cassés, accent bleu vif.
 */
public class FondClair extends AbstractFondDegrade {

    // Cellule & texte
    private static final Color C_FOND_CELLULE        = new Color(0xF4, 0xED, 0xE0); // ivoire chaud
    private static final Color C_TEXTE               = new Color(0x1A, 0x23, 0x30); // marine profond
    // Grille
    private static final Color C_BORDURE_ZONE        = new Color(0x1F, 0x5C, 0xB8); // bleu vif
    private static final Color C_ETIQUETTE_ZONE      = new Color(0x18, 0x4E, 0xA6); // bleu intense
    // Boutons — crème chaud bien visible, bordure ardoise, états distincts
    private static final Color C_FOND_BOUTON         = new Color(0xEC, 0xE5, 0xD8, 245); // crème chaud
    private static final Color C_FOND_BOUTON_SURVOL  = new Color(0xCE, 0xE1, 0xF5, 250); // bleu doux
    private static final Color C_FOND_BOUTON_CLIC    = new Color(0xB3, 0xCE, 0xEC, 255); // bleu moyen
    private static final Color C_BORDURE_BOUTON      = new Color(0x52, 0x7E, 0xA8); // ardoise bleu
    // Inputs
    private static final Color C_FOND_INPUT          = new Color(0xE8, 0xE1, 0xD4); // crème foncé
    private static final Color C_FOND_INPUT_FOCUS    = new Color(0xF4, 0xED, 0xE0); // ivoire chaud
    private static final Color C_ACCENT              = new Color(0x1F, 0x5C, 0xB8); // bleu vif
    private static final Color C_PLACEHOLDER         = new Color(0x72, 0x84, 0x95); // ardoise moyen

    private static final FondClair INSTANCE = new FondClair();

    private FondClair() { super(); }

    public static FondClair getInstance() {
        return INSTANCE;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut    = new Color(0x2E, 0x72, 0xB0); // bleu azur profond
        this.cielBas     = new Color(0xE8, 0xC5, 0x6A); // ambre doré
        this.overlayColor = new Color(0xFF, 0xF8, 0xEE);
        this.overlayAlpha = 0.06f; // overlay minimal pour ne pas laver les couleurs
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

    @Override public Fond clair()      { return this; }
    @Override public Fond fonce()      { return FondFonce.getInstance(); }
    @Override public Fond catppuccin() { return FondCatppuccin.getInstance(); }
}
