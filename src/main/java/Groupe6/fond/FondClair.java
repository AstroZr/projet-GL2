package Groupe6.fond;

import java.awt.Color;

/**
 * Fond « Jour » : ciel bleu pâle → sable chaud, palette claire complète.
 * Texte ardoise foncé, boutons blancs cassés, accent bleu vif.
 */
public class FondClair extends AbstractFondDegrade {

    // Cellule & texte
    private static final Color C_FOND_CELLULE        = new Color(0xFF, 0xFF, 0xFD);
    private static final Color C_TEXTE               = new Color(0x1F, 0x29, 0x37); // ardoise foncé
    // Grille
    private static final Color C_BORDURE_ZONE        = new Color(0x2A, 0x6E, 0xD6);
    private static final Color C_ETIQUETTE_ZONE      = new Color(0x1D, 0x5E, 0xC4);
    // Boutons — blanc cassé, bordure gris clair, hover subtil
    private static final Color C_FOND_BOUTON         = new Color(0xF8, 0xF9, 0xFA, 210);
    private static final Color C_FOND_BOUTON_SURVOL  = new Color(0xEB, 0xF3, 0xFF, 220);
    private static final Color C_FOND_BOUTON_CLIC    = new Color(0xD6, 0xE8, 0xFF, 230);
    private static final Color C_BORDURE_BOUTON      = new Color(0xB0, 0xC4, 0xDE);
    // Inputs — fond très clair, focus avec bord bleu
    private static final Color C_FOND_INPUT          = new Color(0xF0, 0xF4, 0xF8);
    private static final Color C_FOND_INPUT_FOCUS    = new Color(0xFF, 0xFF, 0xFF);
    private static final Color C_ACCENT              = new Color(0x2A, 0x6E, 0xD6);
    private static final Color C_PLACEHOLDER         = new Color(0x9C, 0xA3, 0xAF);

    private static volatile FondClair instance = null;

    private FondClair() { super(); }

    public static FondClair getInstance() {
        if (instance != null) return instance;
        synchronized (FondClair.class) {
            if (instance == null) instance = new FondClair();
        }
        return instance;
    }

    @Override
    protected void chargerCouleurs() {
        this.cielHaut    = new Color(0x72, 0xB7, 0xE0); // bleu ciel moyen
        this.cielBas     = new Color(0xFD, 0xF0, 0xE0); // sable chaud
        this.overlayColor = new Color(0xFF, 0xFF, 0xF8);
        this.overlayAlpha = 0.12f;
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
