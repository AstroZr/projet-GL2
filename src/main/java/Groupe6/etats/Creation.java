package Groupe6.etats;

import Groupe6.game.Game;
import Groupe6.save.SaveManager;
import Groupe6.ui.BoutonCreation;
import Groupe6.ui.TextInput;
import Groupe6.utilz.Constants;
import Groupe6.utilz.FontCache;
import Groupe6.utilz.HelpMethods;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * État « CRÉATION DE PROFIL » : formulaire pour créer un nouveau joueur.
 * 
 * Affichage:
 * - Logo CalcuDoku (haut)
 * - Titre "MathDoku" / "CalcuDoku" (image)
 * - Sous-titre "Création de compte"
 * - Champ texte pour entrée pseudo (max 20 car)
 * - Bouton "Créer profil"
 * - Bouton "Retour"
 * 
 * Interaction:
 * - Clavier: taper pseudo (TextInput récupère les caractères)
 * - Clic bouton: créer joueur → SaveManager.créerJoueur() → Menu
 * 
 * Contraintes:
 * - Pseudo: max 20 caractères
 * - Vérification doublon via SaveManager
 * 
 * Héritage: Etats
 */
public class Creation extends Etats {

    // ====== DIMENSIONS DE RÉFÉRENCE ======
    private static final int LOGO_DEFAULT_SIZE = 320;   // Logo CalcuDoku
    private static final int LARGEUR_BOUTON = 400;      // Boutons créer/retour
    private static final int HAUTEUR_BOUTON = 55;
    private static final int LARGEUR_CHAMP = 400;       // Champ texte pseudo
    private static final int HAUTEUR_CHAMP = 36;
    private static final int MAX_PSEUDO = 20;           // Longueur max pseudo

    // ====== IMAGES ======
    private BufferedImage logo;         // Logo CalcuDoku
    private BufferedImage nameAppImage; // Image du titre "MathDoku"
    private int logoX, logoY, logoSize;
    private int nameAppX, nameAppY, nameAppWidth, nameAppHeight;
    private int titleY;                 // Position Y du titre "Création"
    private int titleFontSize;          // Taille police titre
    private Font titleFont;

    // ====== LAYOUT ======
    /** Centre horizontal calculé dans updateLayout, cohérent avec logo/nom. */
    private int centerX;                // Centre horizontal écran

    // ====== SCALING & LAYOUT ======
    private LayoutScale layoutScale;    // Responsable du redimensionnement
    
    // ====== COMPOSANTS UI ======
    private TextInput textInput;        // Champ saisie pseudo
    private BoutonCreation bouton;      // Bouton "Créer profil"
    
    // ====== TEXTES LOCALISÉS ======
    private String titreCreation;                // "Création de compte"
    private String placeholderIdentifiant;       // Placeholder champ texte
    private String labelBoutonCreationProfil;    // Label bouton "Créer"
    private String messageErreurDoublon;         // Message erreur pseudo existant

    // ====== ÉTAT ======
    private String messageErreur;               // Message d'erreur affiché, null si aucun

  public Creation(Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    layoutScale = LayoutScale.getInstance();
    logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
    nameAppImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "NameApp.png");
    updateTexts();

    textInput =
        new TextInput(0, 0, LARGEUR_CHAMP, HAUTEUR_CHAMP, placeholderIdentifiant, MAX_PSEUDO);
    bouton = new BoutonCreation(0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON);
    bouton.setGame(game);  // Passer la Game instance au bouton
    bouton.setLabel(labelBoutonCreationProfil);
  }

  @Override
  public void update() {
    getFond().update();
    textInput.update(getFond());
  }

  /**
   * Bloc logo → nom app → titre « Création » → champ → bouton, centré, scaling depuis Constants.
   */
  @Override
  protected void applyLayout(int w, int h) {
    layoutScale.update(w, h);
    centerX = layoutScale.centerX();
    int cx = centerX;

    logoSize = layoutScale.scaleUniform(LOGO_DEFAULT_SIZE);
    logoX = cx - logoSize / 2;
    logoY = layoutScale.ratioY(Constants.Ratios.Creation.RATIO_LOGO_Y);
    int gapLogoName = layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_LOGO_NAME_REF);
    nameAppHeight = layoutScale.scaleUniform(Constants.Ratios.Creation.NAME_APP_REF_HEIGHT);
    nameAppHeight = Math.max(1, nameAppHeight);
    nameAppWidth =
        (nameAppImage != null && nameAppImage.getHeight() > 0)
            ? nameAppHeight * nameAppImage.getWidth() / nameAppImage.getHeight()
            : nameAppHeight;
    nameAppWidth = Math.max(1, nameAppWidth);
    nameAppX = cx - nameAppWidth / 2;
    nameAppY = logoY + logoSize + gapLogoName;

    int gapNameTitle =
        layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_NAME_TITLE_REF);
    titleFontSize = layoutScale.scaleUniform(Constants.Ratios.Creation.TITLE_FONT_SIZE_REF);
    titleFont = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, Math.max(12, titleFontSize));
    titleY = nameAppY + nameAppHeight + gapNameTitle;

    int gapTitleChamp =
        layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_TITLE_CHAMP_REF);
    int gapChampBouton =
        layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_CHAMP_BOUTON_REF);
    int cw = layoutScale.scaleX(LARGEUR_CHAMP);
    int ch = layoutScale.scaleY(HAUTEUR_CHAMP);
    int bw = layoutScale.scaleX(LARGEUR_BOUTON);
    int bh = layoutScale.scaleY(HAUTEUR_BOUTON);

    int champY = titleY + titleFontSize + gapTitleChamp;
    int buttonY = champY + ch + gapChampBouton;

    textInput.setBounds(cx - cw / 2, champY, cw, ch);
    bouton.setX(cx - bw / 2);
    bouton.setY(buttonY);
    bouton.setLargeur(bw);
    bouton.setHauteur(bh);
  }

  private void ensureImagesLoaded() {
    boolean needLayout = false;
    if (logo == null) {
      logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
      if (logo != null) needLayout = true;
    }
    if (nameAppImage == null) {
      nameAppImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "NameApp.png");
      if (nameAppImage != null) needLayout = true;
    }
    if (needLayout) {
      lastLayoutWidth = -1;
    }
  }

  @Override
  public void draw(Graphics g) {
    ensureImagesLoaded();
    ensureLayoutUpToDate();
    getFond().draw(g);

    if (logo != null) {
      g.drawImage(logo, logoX, logoY, logoSize, logoSize, null);
    }
    if (nameAppImage != null) {
      g.drawImage(nameAppImage, nameAppX, nameAppY, nameAppWidth, nameAppHeight, null);
    }
    g.setFont(titleFont);
    g.setColor(java.awt.Color.BLACK);
    int tw = g.getFontMetrics().stringWidth(titreCreation);
    g.drawString(titreCreation, centerX - tw / 2, titleY + g.getFontMetrics().getAscent());

    textInput.draw(g, getFond());
    bouton.draw(g, getFond());

    if (messageErreur != null) {
      g.setFont(titleFont);
      g.setColor(java.awt.Color.RED);
      int ew = g.getFontMetrics().stringWidth(messageErreur);
      int errorY = bouton.getY() + bouton.getHauteur() + g.getFontMetrics().getAscent() + 8;
      g.drawString(messageErreur, centerX - ew / 2, errorY);
    }
  }
    String joueurActuel = game.getJoueurCourant();

  @Override
  public void keyTyped(KeyEvent e) {
    textInput.handleKeyTyped(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {}

  @Override
  public void mouseMoved(MouseEvent e) {
    bouton.setSourisSurvol(isIn(e, bouton));
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {
    if (isIn(e, bouton)) {
      bouton.setSourisEnfonce(true);
    }
    if (textInput.contains(e.getX(), e.getY())) {
      textInput.setFocused(true);
      return;
    }
    textInput.setFocused(false);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (bouton.isSourisEnfonce() && isIn(e, bouton)) {
      String pseudo = getPseudoSaisi();
      if (pseudo != null && !pseudo.trim().isEmpty()) {
        if (SaveManager.listerJoueurs().contains(pseudo.trim())) {
          messageErreur = messageErreurDoublon;
        } else {
          messageErreur = null;
          bouton.setPseudo(pseudo);
          bouton.appliquerAction();
          game.setJoueurCourant(pseudo);
        }
      }
    }
    bouton.setSourisEnfonce(false);
  }

  @Override
  public void updateTexts() {
    titreCreation = LangManager.get("creation.titre");
    placeholderIdentifiant = LangManager.get("creation.identifiant");
    labelBoutonCreationProfil = LangManager.get("creation.bouton");
    messageErreurDoublon = LangManager.get("creation.erreur.doublon");

    if (textInput != null) {
      textInput.setPlaceholder(placeholderIdentifiant);
    }
    if (bouton != null) {
      bouton.setLabel(labelBoutonCreationProfil);
    }
  }

  public String getPseudoSaisi() {
    return textInput.getTextTrimmed();
  }
}
