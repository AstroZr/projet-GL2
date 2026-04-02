package Groupe6.etats;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Groupe6.audio.SoundManager;
import Groupe6.fond.Fond;
import Groupe6.fond.FondCatppuccin;
import Groupe6.fond.FondClair;
import Groupe6.fond.FondDegrade;
import Groupe6.fond.FondFonce;
import Groupe6.game.Game;
import Groupe6.models.Grille;
import Groupe6.save.ParametresJoueur;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.FontCache;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * État « PARAMÈTRES » : écran de configuration du jeu (volume, langue, thème).
 * 
 * Éléments:
 * - Panneau avec champs pour:
 *   • Volume effets (slider 0-100)
 *   • Volume musique (slider 0-100)
 *   • Langue (dropdown: français, anglais, etc.)
 *   • Thème (boutons: Clair, Foncé, Catppuccin)
 * - Bouton "Appliquer" pour sauvegarder
 * - Bouton "Retour" pour revenir à l'état source
 * 
 * Source:
 * - Peut être appelé depuis Menu ou depuis Jeu (pendant la partie)
 * - etatSource mémorise l'endroit d'où on vient
 * - Retour revient à cet état
 * 
 * Persistance:
 * - Clic "Appliquer" → SaveManager.sauvegarderParametres()
 * - Stockage: saveGame/[joueurID]/settings.json
 * 
 * Héritage: Etats
 */
public class Parametres extends Etats {

  private static final int LARGEUR_BOUTON = 200;   // Boutons Appliquer/Retour
  private static final int HAUTEUR_BOUTON = 44;
  private static final BasicStroke STROKE_UI = new BasicStroke(1.5f);
  private static final BasicStroke STROKE_CHEVRON = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
  private static final Color COLOR_ACCENT = new Color(70, 130, 180);
  private static final Color COLOR_ACCENT_SOFT = new Color(70, 130, 180, 50);
  private static final Color COLOR_SECTION_LINE = new Color(128, 128, 128, 70);
  
  // ====== GESTION DU RETOUR À L'ÉTAT SOURCE ======
  /** État source : MENU ou GRILLE (depend d'où on appelle Parametres) */
  private static EtatJeu etatSource = EtatJeu.MENU;
  /** Track last used value to detect changes */
  private EtatJeu etatSourcePrecedent = null;
  
  /**
   * Définit l'état depuis lequel on a appelé Parametres.
   * @param etat EtatJeu.MENU ou EtatJeu.GRILLE
   */
  public static void setEtatSource(EtatJeu etat) {
    etatSource = etat;
  }

  // ====== SCALING & LAYOUT ======
  private LayoutScale layoutScale;  // Responsable du redimensionnement

  // ====== PANNEAU PARAMÈTRES ======
  private int panelX, panelY, panelWidth, panelHeight;  // Dimensions du panneau
  private int sliderEffetsX, sliderEffetsY, sliderWidth;
  private int sliderMusiqueX, sliderMusiqueY;
  private int dropdownX, dropdownY, dropdownWidth;
  private int themeDropdownX, themeDropdownY, themeDropdownWidth;

  // Valeurs des paramètres
  private float volumeEffets = 0.0f;
  private float volumeMusique = 0.0f;
  private final List<String> codesLangues = Arrays.asList("fr", "en");
  private List<String> languesAffichees = Arrays.asList("Français", "English");
  private int langueSelectionnee = 0;
  private boolean dropdownLangueOuvert = false;
  private final List<String> themes = Arrays.asList("Dégradé", "Mode Sombre", "Mode Clair", "Catppuccin");
  private List<String> themesAffiches = new ArrayList<>(themes);
  private int themeSelectionne = 0;
  private boolean dropdownThemeOuvert = false;
  private int indexSurvolTheme = -1;

  // ====== PARAMÈTRES DE JEU (TOGGLES) ======
  private boolean afficherPossibilites = true;      // Afficher/masquer les possibilités d'équations
  private boolean afficherErreurDouble = false;     // Afficher/masquer les erreurs de doublon
  private int togglePossibilitesX, togglePossibilitesY;
  private int toggleErreurDoubleX, toggleErreurDoubleY;
  private String labelPossibilites;
  private String labelErreurDouble;

  // État de drag des sliders
  private boolean draggingEffets = false;
  private boolean draggingMusique = false;

  // Hover états
  private boolean hoverDropdown = false;
  private boolean hoverThemeDropdown = false;
  private boolean hoverTogglePossibilites = false;
  private boolean hoverToggleErreurDouble = false;

  private String titreParametres;
  private String labelEffetsSonores;
  private String labelMusique;
  private String labelLangue;
  private String labelTheme;
  private String suffixeMute;
  private String boutonRetourLabel;
  private String boutonDefautLabel;
  private String boutonAppliquerLabel;

  private int langueAppliquee = 0;
  private int themeApplique = 0;
  private float volumeEffetsApplique = 0.0f;
  private float volumeMusiqueApplique = 0.0f;
  private boolean afficherPossibilitesApplique = true;
  private boolean afficherErreurDoubleApplique = false;
  private boolean synchroniseDepuisEntree = false;
  private Font fontTitre;
  private Font fontLabel;
  private Font fontEmoji;
  private int scaledX40;
  private int scaledX20;
  private int scaledX10;
  private int scaledY18;
  private int scaledY20;
  private int scaledY22;
  private int scaledY24;
  private int scaledY28;
  private int scaledY30;
  private int scaledY35;
  private int scaledY42;
  private int scaledY2;
  private int toggleControlX;
  private int sectionInterfaceY;
  private int sectionGameplayY;

  public Parametres(Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    layoutScale = LayoutScale.getInstance();
    boutons = new ArrayList<>();
    updateTexts();
    String langCode = game != null ? game.getLangueCode() : "fr";
    langueAppliquee = codesLangues.indexOf(langCode);
    if (langueAppliquee < 0) langueAppliquee = 0;
    themeApplique = detecterThemeActuel();
    synchroniserEditionAvecValeursAppliquees();

    layoutScale.update(Constants.game_width, Constants.game_height);
    calculerPositions();
  }

  private void calculerPositions() {
    int cx = layoutScale.ratioX(0.5f);
    int cy = layoutScale.ratioY(0.5f);

    // Panel central — colonne unique, légèrement plus large et plus haute
    panelWidth = layoutScale.scaleX(820);
    panelHeight = layoutScale.scaleY(560);
    panelX = cx - panelWidth / 2;
    panelY = cy - panelHeight / 2 - layoutScale.scaleY(30);

    int leftMargin = layoutScale.scaleX(30);
    int fullRowW = panelWidth - 2 * leftMargin;

    // ── Section Audio ────────────────────────────────────────────
    sliderWidth = fullRowW;
    sliderEffetsX = panelX + leftMargin;
    sliderEffetsY = panelY + layoutScale.scaleY(60);
    sliderMusiqueX = sliderEffetsX;
    sliderMusiqueY = sliderEffetsY + layoutScale.scaleY(90);

    // ── Section Interface ─────────────────────────────────────────
    sectionInterfaceY = sliderMusiqueY + layoutScale.scaleY(80);
    dropdownX = panelX + leftMargin;
    dropdownY = sectionInterfaceY + layoutScale.scaleY(30);
    dropdownWidth = fullRowW;

    themeDropdownX = panelX + leftMargin;
    themeDropdownY = dropdownY + layoutScale.scaleY(90);
    themeDropdownWidth = fullRowW;

    // ── Section Gameplay ──────────────────────────────────────────
    sectionGameplayY = themeDropdownY + layoutScale.scaleY(80);
    togglePossibilitesX = panelX + leftMargin;
    togglePossibilitesY = sectionGameplayY + layoutScale.scaleY(30);
    toggleErreurDoubleX = panelX + leftMargin;
    toggleErreurDoubleY = togglePossibilitesY + layoutScale.scaleY(55);

    // Toggle positionné à droite du panneau
    int toggleW = layoutScale.scaleX(56);
    toggleControlX = panelX + panelWidth - leftMargin - toggleW;

    // Cache des dimensions et polices réutilisées dans draw
    fontTitre = FontCache.get("Berlin Sans FB Demi", Font.BOLD, layoutScale.scaleUniform(36));
    fontLabel = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14));
    fontEmoji = FontCache.get("Segoe UI Emoji", Font.PLAIN, layoutScale.scaleUniform(20));
    scaledX40 = layoutScale.scaleX(40);
    scaledX20 = layoutScale.scaleX(20);
    scaledX10 = layoutScale.scaleX(10);
    scaledY18 = layoutScale.scaleY(18);
    scaledY20 = layoutScale.scaleY(20);
    scaledY22 = layoutScale.scaleY(22);
    scaledY24 = layoutScale.scaleY(24);
    scaledY28 = layoutScale.scaleY(28);
    scaledY30 = layoutScale.scaleY(30);
    scaledY35 = layoutScale.scaleY(35);
    scaledY42 = layoutScale.scaleY(42);
    scaledY2 = layoutScale.scaleY(2);

    // Boutons de validation, de réinitialisation ou de retour
    boutons.clear();
    int by = panelY + panelHeight + layoutScale.scaleY(28);
    int bw = layoutScale.scaleX(LARGEUR_BOUTON);
    int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
    int gap = layoutScale.scaleX(20);

    int totalWidth = bw * 3 + gap * 2;
    int startX = cx - totalWidth / 2;

    boutons.add(new BoutonChangeurEtat(startX, by, bw, bh, etatSource, boutonRetourLabel));
    boutons.add(new BoutonChangeurEtat(startX + bw + gap, by, bw, bh, EtatJeu.PARAMETRES, boutonDefautLabel));
    boutons.add(new BoutonChangeurEtat(startX + 2 * (bw + gap), by, bw, bh, EtatJeu.PARAMETRES, boutonAppliquerLabel));
  }

  /** Met à jour le fond animé (nuages). */
  @Override
  public void update() {
    if (!synchroniseDepuisEntree) {
      chargerParametresJoueur(game != null ? game.getJoueurCourant() : null);
      synchroniserEditionAvecValeursAppliquees();
      synchroniseDepuisEntree = true;
    }
    
    // Si etatSource a changé depuis la dernière fois, recréer les boutons
    if (etatSourcePrecedent != etatSource) {
      calculerPositions();
      etatSourcePrecedent = etatSource;
    }
    
    getFond().update();
  }

  @Override
  protected void applyLayout(int w, int h) {
    calculerPositions();
  }

  /** Dessine le fond animé et tous les éléments des paramètres. */
  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();
    getFond().draw(g);

    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Titre — couleur adaptée au thème
    g.setColor(getFond().getCouleurTexte());
    g.setFont(fontTitre);
    String titre = titreParametres;
    int titreLargeur = g.getFontMetrics().stringWidth(titre);
    g.drawString(titre, layoutScale.centerX() - titreLargeur / 2, panelY - layoutScale.scaleY(18));

    // Panneau principal avec couleur thème
    Color panelBase = getFond().getCouleurFondBouton();
    g2d.setColor(new Color(panelBase.getRed(), panelBase.getGreen(), panelBase.getBlue(), 210));
    RoundRectangle2D panel = new RoundRectangle2D.Float(panelX, panelY, panelWidth, panelHeight, 20, 20);
    g2d.fill(panel);
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.setStroke(STROKE_UI);
    g2d.draw(panel);

    // Séparateurs de section
    int sepMargin = layoutScale.scaleX(30);
    int sepW = panelWidth - 2 * sepMargin;
    dessinerSectionLabel(g2d, panelX + sepMargin, sectionInterfaceY, sepW);
    dessinerSectionLabel(g2d, panelX + sepMargin, sectionGameplayY, sepW);

    // Contrôles
    dessinerSlider(g, sliderEffetsX, sliderEffetsY, sliderWidth, labelEffetsSonores, "🔊", "🔇", volumeEffets);
    dessinerSlider(g, sliderMusiqueX, sliderMusiqueY, sliderWidth, labelMusique, "🎵", "🔇", volumeMusique);
    dessinerTogglePossibilites(g);
    dessinerToggleErreurDouble(g);

    // Boutons du bas
    for (Bouton b : boutons) {
      b.draw(g, getFond());
    }

    // Dropdowns en dernier pour qu'ils s'affichent au-dessus de tout
    // Thème en premier, Langue en dernier pour que les options Langue s'affichent au-dessus du label Thème
    dessinerDropdownThemes(g);
    dessinerDropdown(g);
  }

  private void dessinerSectionLabel(Graphics2D g2d, int x, int y, int w) {
    g2d.setStroke(STROKE_UI);
    g2d.setColor(COLOR_SECTION_LINE);
    g2d.drawLine(x, y + layoutScale.scaleY(10), x + w, y + layoutScale.scaleY(10));
  }

  private void dessinerSlider(Graphics g, int x, int y, int largeur, String label, String icon, String muteIcon,
      float valeur) {
    Graphics2D g2d = (Graphics2D) g;
    boolean mute = valeur <= 0.0f;
    String iconAffiche = mute ? muteIcon : icon;
    String labelAffiche = mute ? label + suffixeMute : label;

    // Icône et label
    g.setColor(getFond().getCouleurTexte());
    g.setFont(fontEmoji);
    g.drawString(iconAffiche, x, y + scaledY20);

    g.setFont(fontLabel);
    g.drawString(labelAffiche, x + scaledX40, y + scaledY18);

    // Piste du slider
    int sliderY = y + scaledY30;
    int sliderX = x + scaledX40;
    int sliderH = layoutScale.scaleY(7);
    int trackWidth = largeur - scaledX40;

    // Fond de la piste (couleur thème)
    Color trackBg = getFond().getCouleurFondCellule();
    g2d.setColor(trackBg);
    RoundRectangle2D piste = new RoundRectangle2D.Float(sliderX, sliderY, trackWidth, sliderH, sliderH, sliderH);
    g2d.fill(piste);

    // Partie remplie
    int filledWidth = (int) (trackWidth * valeur);
    if (filledWidth > 0) {
      g2d.setColor(COLOR_ACCENT);
      RoundRectangle2D filled = new RoundRectangle2D.Float(sliderX, sliderY, filledWidth, sliderH, sliderH, sliderH);
      g2d.fill(filled);
    }

    // Curseur
    int thumbSize = layoutScale.scaleUniform(18);
    int thumbX = sliderX + (int) (trackWidth * valeur) - thumbSize / 2;
    int thumbY = sliderY + sliderH / 2 - thumbSize / 2;

    g2d.setColor(Color.WHITE);
    g2d.fillOval(thumbX, thumbY, thumbSize, thumbSize);
    g2d.setColor(COLOR_ACCENT);
    g2d.setStroke(STROKE_UI);
    g2d.drawOval(thumbX, thumbY, thumbSize, thumbSize);
  }

  private void dessinerDropdown(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    // Icône et label
    g.setColor(getFond().getCouleurTexte());
    g.setFont(fontEmoji);
    g.drawString("🌐", dropdownX, dropdownY + scaledY20);
    g.setFont(fontLabel);
    g.drawString(labelLangue, dropdownX + scaledX40, dropdownY + scaledY18);

    int dropY = dropdownY + scaledY28;
    int dropX = dropdownX + scaledX40;
    int dropW = dropdownWidth - scaledX40;
    int dropH = scaledY42;

    dessinerBoiteDropdown(g2d, dropX, dropY, dropW, dropH,
        languesAffichees.get(langueSelectionnee), hoverDropdown, dropdownLangueOuvert);

    if (dropdownLangueOuvert) {
      int optionStartY = dropY + dropH + scaledY2;
      int nbOptions = languesAffichees.size() - 1;
      int listH = nbOptions * (dropH + scaledY2) - scaledY2;

      // Ombre portée (multi-couche pour effet doux)
      g2d.setColor(new Color(0, 0, 0, 10));
      g2d.fillRoundRect(dropX + 6, optionStartY + 6, dropW, listH, 14, 14);
      g2d.setColor(new Color(0, 0, 0, 20));
      g2d.fillRoundRect(dropX + 4, optionStartY + 4, dropW, listH, 12, 12);
      g2d.setColor(new Color(0, 0, 0, 32));
      g2d.fillRoundRect(dropX + 2, optionStartY + 2, dropW, listH, 10, 10);

      // Fond solide de la liste (évite la transparence entre/sous les items)
      Color listBg = getFond().getCouleurFondCellule();
      g2d.setColor(new Color(listBg.getRed(), listBg.getGreen(), listBg.getBlue()));
      g2d.fillRoundRect(dropX, optionStartY, dropW, listH, 10, 10);

      // Items
      int optionY = optionStartY;
      for (int i = 0; i < languesAffichees.size(); i++) {
        if (i != langueSelectionnee) {
          dessinerOptionDropdown(g2d, dropX, optionY, dropW, dropH,
              languesAffichees.get(i), false, false);
          optionY += dropH + scaledY2;
        }
      }

      // Contour du conteneur (dessiné après les items pour superposition propre)
      g2d.setColor(getFond().getCouleurAccent());
      g2d.setStroke(new BasicStroke(1.5f));
      g2d.drawRoundRect(dropX, optionStartY, dropW, listH, 10, 10);
    }
  }

  private void dessinerDropdownThemes(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g.setColor(getFond().getCouleurTexte());
    g.setFont(fontEmoji);
    g.drawString("🎨", themeDropdownX, themeDropdownY + scaledY20);
    g.setFont(fontLabel);
    g.drawString(labelTheme, themeDropdownX + scaledX40, themeDropdownY + scaledY18);

    int dropY = themeDropdownY + scaledY28;
    int dropX = themeDropdownX + scaledX40;
    int dropW = themeDropdownWidth - scaledX40;
    int dropH = scaledY42;

    dessinerBoiteDropdown(g2d, dropX, dropY, dropW, dropH,
        themesAffiches.get(themeSelectionne), hoverThemeDropdown, dropdownThemeOuvert);

    if (dropdownThemeOuvert) {
      int optionStartY = dropY + dropH + scaledY2;
      int nbOptions = themes.size();
      int listH = nbOptions * (dropH + scaledY2) - scaledY2;

      // Ombre portée (multi-couche pour effet doux)
      g2d.setColor(new Color(0, 0, 0, 10));
      g2d.fillRoundRect(dropX + 6, optionStartY + 6, dropW, listH, 14, 14);
      g2d.setColor(new Color(0, 0, 0, 20));
      g2d.fillRoundRect(dropX + 4, optionStartY + 4, dropW, listH, 12, 12);
      g2d.setColor(new Color(0, 0, 0, 32));
      g2d.fillRoundRect(dropX + 2, optionStartY + 2, dropW, listH, 10, 10);

      // Fond solide de la liste (évite la transparence entre/sous les items)
      Color listBg = getFond().getCouleurFondCellule();
      g2d.setColor(new Color(listBg.getRed(), listBg.getGreen(), listBg.getBlue()));
      g2d.fillRoundRect(dropX, optionStartY, dropW, listH, 10, 10);

      // Items
      int optionY = optionStartY;
      for (int i = 0; i < themes.size(); i++) {
        boolean selected = (i == themeSelectionne);
        boolean hovered = (i == indexSurvolTheme);
        dessinerOptionDropdown(g2d, dropX, optionY, dropW, dropH,
            themesAffiches.get(i), hovered, selected);
        optionY += dropH + scaledY2;
      }

      // Pastilles de couleur pour les thèmes
      Color[] swatchCouleurs = {
          new Color(135, 190, 220),   // Dégradé - bleu ciel
          new Color(45, 45, 65),      // Mode Sombre - bleu nuit
          new Color(240, 240, 245),   // Mode Clair - blanc cassé
          new Color(203, 166, 247)    // Catppuccin - lavande
      };
      int swatchOptY = optionStartY;
      for (int i = 0; i < themes.size(); i++) {
        int swatchSize = layoutScale.scaleUniform(14);
        int swatchX = dropX + dropW - layoutScale.scaleX(50);
        int swatchY = swatchOptY + (dropH - swatchSize) / 2;
        g2d.setColor(swatchCouleurs[i]);
        g2d.fillOval(swatchX, swatchY, swatchSize, swatchSize);
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawOval(swatchX, swatchY, swatchSize, swatchSize);
        swatchOptY += dropH + scaledY2;
      }

      // Contour du conteneur (dessiné après les items pour superposition propre)
      g2d.setColor(getFond().getCouleurAccent());
      g2d.setStroke(new BasicStroke(1.5f));
      g2d.drawRoundRect(dropX, optionStartY, dropW, listH, 10, 10);
    }
  }

  /** Boîte principale du dropdown (valeur sélectionnée + chevron). */
  private void dessinerBoiteDropdown(Graphics2D g2d, int x, int y, int w, int h,
      String texte, boolean hover, boolean ouvert) {
    Color accent = getFond().getCouleurAccent();
    Color bgBase = hover ? getFond().getCouleurFondBoutonSurvol() : getFond().getCouleurFondCellule();

    // Fond avec gradient vertical subtil
    Color bgTop = bgBase;
    Color bgBot = new Color(
        Math.max(0, bgBase.getRed() - 14),
        Math.max(0, bgBase.getGreen() - 14),
        Math.max(0, bgBase.getBlue() - 14),
        bgBase.getAlpha()
    );
    RoundRectangle2D box = new RoundRectangle2D.Float(x, y, w, h, 10, 10);
    g2d.setPaint(new GradientPaint(x, y, bgTop, x, y + h, bgBot));
    g2d.fill(box);

    // Reflet verre (dégradé blanc→transparent sur la moitié haute)
    g2d.setPaint(new GradientPaint(x, y, new Color(255, 255, 255, hover ? 55 : 30),
        x, y + h / 2, new Color(255, 255, 255, 0)));
    g2d.fill(box);
    g2d.setPaint(null);

    // Lueur intérieure quand ouvert
    if (ouvert) {
      g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
      g2d.fill(box);
    }

    // Barre d'accent à gauche quand ouvert
    if (ouvert) {
      int barW = layoutScale.scaleX(3);
      g2d.setColor(accent);
      g2d.fillRoundRect(x + 1, y + layoutScale.scaleY(5), barW, h - layoutScale.scaleY(10), barW, barW);
    }

    // Bordure (accentuée quand ouvert, mi-teinte sur survol)
    Color borderColor = ouvert ? accent
        : hover ? new Color(
            (getFond().getCouleurBordreBouton().getRed() + accent.getRed()) / 2,
            (getFond().getCouleurBordreBouton().getGreen() + accent.getGreen()) / 2,
            (getFond().getCouleurBordreBouton().getBlue() + accent.getBlue()) / 2)
        : getFond().getCouleurBordreBouton();
    g2d.setColor(borderColor);
    g2d.setStroke(ouvert ? new BasicStroke(2f) : STROKE_UI);
    g2d.draw(box);

    // Texte sélectionné (couleur accent sur survol ou ouvert)
    g2d.setColor(hover || ouvert ? accent : getFond().getCouleurTexte());
    g2d.setFont(fontLabel);
    int ty = y + (h + g2d.getFontMetrics().getAscent()) / 2 - layoutScale.scaleY(2);
    int textX = ouvert ? x + scaledX10 + layoutScale.scaleX(6) : x + scaledX10;
    g2d.drawString(texte, textX, ty);

    // Chevron
    int chevX = x + w - scaledX20 - layoutScale.scaleX(4);
    int chevMidY = y + h / 2;
    dessinerChevron(g2d, chevX, chevMidY, ouvert);
  }

  /** Option dans la liste déroulée. */
  private void dessinerOptionDropdown(Graphics2D g2d, int x, int y, int w, int h,
      String texte, boolean surligne, boolean estSelectionne) {
    Color accent = getFond().getCouleurAccent();

    // Fond : hover > sélectionné légèrement teinté > neutre
    Color bg;
    if (surligne) {
      bg = getFond().getCouleurFondBoutonSurvol();
    } else if (estSelectionne) {
      Color base = getFond().getCouleurFondCellule();
      bg = new Color(
          (int) (base.getRed() * 0.88 + accent.getRed() * 0.12),
          (int) (base.getGreen() * 0.88 + accent.getGreen() * 0.12),
          (int) (base.getBlue() * 0.88 + accent.getBlue() * 0.12)
      );
    } else {
      bg = getFond().getCouleurFondCellule();
    }
    g2d.setColor(bg);
    RoundRectangle2D opt = new RoundRectangle2D.Float(x, y, w, h, 8, 8);
    g2d.fill(opt);

    // Barre d'accent à gauche pour hover ou sélectionné
    if (surligne || estSelectionne) {
      int barW = layoutScale.scaleX(3);
      g2d.setColor(accent);
      g2d.fillRoundRect(x + 1, y + layoutScale.scaleY(5), barW, h - layoutScale.scaleY(10), barW, barW);
    }

    // Bordure
    Color borderOpt = estSelectionne ? accent
        : surligne ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 130)
        : getFond().getCouleurBordreBouton();
    g2d.setColor(borderOpt);
    g2d.setStroke(estSelectionne ? new BasicStroke(1.5f) : STROKE_UI);
    g2d.draw(opt);

    // Texte (légèrement indenté quand actif, couleur accent sur survol/sélection)
    g2d.setColor(surligne || estSelectionne ? accent : getFond().getCouleurTexte());
    g2d.setFont(fontLabel);
    int ty = y + (h + g2d.getFontMetrics().getAscent()) / 2 - layoutScale.scaleY(2);
    int textX = (surligne || estSelectionne) ? x + scaledX10 + layoutScale.scaleX(5) : x + scaledX10;
    g2d.drawString(texte, textX, ty);

    // Coche pour l'élément sélectionné
    if (estSelectionne) {
      g2d.setColor(accent);
      g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
      int s = layoutScale.scaleUniform(5);
      int checkX = x + w - layoutScale.scaleX(24);
      int checkY = y + h / 2;
      g2d.drawLine(checkX - s, checkY, checkX - s / 3, checkY + s * 2 / 3);
      g2d.drawLine(checkX - s / 3, checkY + s * 2 / 3, checkX + s, checkY - s * 2 / 3);
    }
  }

  /** Chevron ˅ / ˄ dessiné avec deux segments (moderne, non rempli). */
  private void dessinerChevron(Graphics2D g2d, int cx, int cy, boolean up) {
    int s = layoutScale.scaleUniform(6);
    int circleR = layoutScale.scaleUniform(11);
    Color accentColor = getFond().getCouleurAccent();

    // Fond circulaire subtil derrière le chevron
    Color pillBg = up
        ? new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40)
        : new Color(128, 128, 128, 22);
    g2d.setColor(pillBg);
    g2d.fillOval(cx - circleR, cy - circleR, circleR * 2, circleR * 2);

    g2d.setColor(up ? accentColor : getFond().getCouleurTexte());
    g2d.setStroke(STROKE_CHEVRON);
    if (up) {
      g2d.drawLine(cx - s, cy + s / 2, cx, cy - s / 2);
      g2d.drawLine(cx, cy - s / 2, cx + s, cy + s / 2);
    } else {
      g2d.drawLine(cx - s, cy - s / 2, cx, cy + s / 2);
      g2d.drawLine(cx, cy + s / 2, cx + s, cy - s / 2);
    }
  }

  private void dessinerTogglePossibilites(Graphics g) {
    dessinerToggle(g, togglePossibilitesX, togglePossibilitesY,
        labelPossibilites != null ? labelPossibilites : "Possibilités",
        afficherPossibilites);
  }

  private void dessinerToggleErreurDouble(Graphics g) {
    dessinerToggle(g, toggleErreurDoubleX, toggleErreurDoubleY,
        labelErreurDouble != null ? labelErreurDouble : "Erreur doublon",
        afficherErreurDouble);
  }

  private void dessinerToggle(Graphics g, int rowX, int rowY, String label, boolean actif) {
    Graphics2D g2d = (Graphics2D) g;

    // Label aligné à gauche de la ligne
    g.setColor(getFond().getCouleurTexte());
    g.setFont(fontLabel);
    int labelY = rowY + (layoutScale.scaleY(30) + g.getFontMetrics().getAscent()) / 2 - layoutScale.scaleY(2);
    g.drawString(label, rowX, labelY);

    // Toggle aligné à droite
    int toggleW = layoutScale.scaleX(56);
    int toggleH = layoutScale.scaleY(30);
    int toggleX = toggleControlX;
    int toggleY = rowY;

    Color toggleBg = actif ? COLOR_ACCENT : getFond().getCouleurFondCellule();
    g2d.setColor(toggleBg);
    RoundRectangle2D toggle = new RoundRectangle2D.Float(toggleX, toggleY, toggleW, toggleH, toggleH, toggleH);
    g2d.fill(toggle);
    g2d.setColor(actif ? COLOR_ACCENT.darker() : getFond().getCouleurBordreBouton());
    g2d.setStroke(STROKE_UI);
    g2d.draw(toggle);

    // Curseur
    int circleDiam = layoutScale.scaleUniform(22);
    int circleX = actif ? toggleX + toggleW - circleDiam - 4 : toggleX + 4;
    int circleY = toggleY + (toggleH - circleDiam) / 2;
    g2d.setColor(Color.WHITE);
    g2d.fillOval(circleX, circleY, circleDiam, circleDiam);
    g2d.setColor(new Color(0, 0, 0, 40));
    g2d.setStroke(STROKE_UI);
    g2d.drawOval(circleX, circleY, circleDiam, circleDiam);
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // Non implémenté
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Non implémenté
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // Non implémenté
  }

  /** Met à jour l'état de survol selon la position de la souris. */
  @Override
  public void mouseMoved(MouseEvent e) {
    for (Bouton b : boutons) {
      b.setSourisSurvol(isIn(e, b));
    }

    int mx = e.getX();
    int my = e.getY();

    // Hover dropdown langue
    int dropY = dropdownY + layoutScale.scaleY(28);
    int dropX = dropdownX + layoutScale.scaleX(40);
    int dropW = dropdownWidth - layoutScale.scaleX(40);
    int dropH = layoutScale.scaleY(42);
    hoverDropdown = mx >= dropX && mx <= dropX + dropW && my >= dropY && my <= dropY + dropH;

    // Hover dropdown thème
    int themeDropY = themeDropdownY + layoutScale.scaleY(28);
    int themeDropX = themeDropdownX + layoutScale.scaleX(40);
    int themeDropW = themeDropdownWidth - layoutScale.scaleX(40);
    int themeDropH = layoutScale.scaleY(42);
    hoverThemeDropdown = mx >= themeDropX && mx <= themeDropX + themeDropW && my >= themeDropY
        && my <= themeDropY + themeDropH;

    indexSurvolTheme = -1;
    if (dropdownThemeOuvert) {
      int optionY = themeDropY + themeDropH + layoutScale.scaleY(2);
      for (int i = 0; i < themes.size(); i++) {
        if (mx >= themeDropX && mx <= themeDropX + themeDropW && my >= optionY && my <= optionY + themeDropH) {
          indexSurvolTheme = i;
          break;
        }
        optionY += themeDropH + layoutScale.scaleY(2);
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    int mx = e.getX();

    // Drag slider effets
    if (draggingEffets) {
      int sliderX = sliderEffetsX + layoutScale.scaleX(40);
      int sliderW = sliderWidth - layoutScale.scaleX(40);
      float newVal = (float) (mx - sliderX) / sliderW;
      volumeEffets = Math.max(0, Math.min(1, newVal));
    }

    // Drag slider musique
    if (draggingMusique) {
      int sliderX = sliderMusiqueX + layoutScale.scaleX(40);
      int sliderW = sliderWidth - layoutScale.scaleX(40);
      float newVal = (float) (mx - sliderX) / sliderW;
      volumeMusique = Math.max(0, Math.min(1, newVal));
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // Non implémenté
  }

  @Override
  public void mousePressed(MouseEvent e) {
    int mx = e.getX();
    int my = e.getY();
    boolean dropdownActif = dropdownLangueOuvert || dropdownThemeOuvert;

    if (!dropdownActif) {
      for (Bouton b : boutons) {
        if (isIn(e, b)) {
          b.setSourisEnfonce(true);
        }
      }
    }

    if (!dropdownActif) {
      int sliderY = sliderEffetsY + layoutScale.scaleY(20);
      int sliderX = sliderEffetsX + layoutScale.scaleX(40);
      int sliderW = sliderWidth - layoutScale.scaleX(40);
      if (mx >= sliderX && mx <= sliderX + sliderW && my >= sliderY && my <= sliderY + layoutScale.scaleY(30)) {
        draggingEffets = true;
        float newVal = (float) (mx - sliderX) / sliderW;
        volumeEffets = Math.max(0, Math.min(1, newVal));
      }

      sliderY = sliderMusiqueY + layoutScale.scaleY(20);
      sliderX = sliderMusiqueX + layoutScale.scaleX(40);
      if (mx >= sliderX && mx <= sliderX + sliderW && my >= sliderY && my <= sliderY + layoutScale.scaleY(30)) {
        draggingMusique = true;
        float newVal = (float) (mx - sliderX) / sliderW;
        volumeMusique = Math.max(0, Math.min(1, newVal));
      }
    }

    // ── Dropdown langue ──────────────────────────────────────────
    int dropY = dropdownY + layoutScale.scaleY(28);
    int dropX = dropdownX + layoutScale.scaleX(40);
    int dropW = dropdownWidth - layoutScale.scaleX(40);
    int dropH = layoutScale.scaleY(42);
    if (mx >= dropX && mx <= dropX + dropW && my >= dropY && my <= dropY + dropH) {
      dropdownLangueOuvert = !dropdownLangueOuvert;
      if (dropdownLangueOuvert) dropdownThemeOuvert = false;
    } else if (dropdownLangueOuvert) {
      int optionY = dropY + dropH + layoutScale.scaleY(2);
      boolean langueChoisie = false;
      for (int i = 0; i < languesAffichees.size(); i++) {
        if (i != langueSelectionnee) {
          if (mx >= dropX && mx <= dropX + dropW && my >= optionY && my <= optionY + dropH) {
            langueSelectionnee = i;
            dropdownLangueOuvert = false;
            langueChoisie = true;
            break;
          }
          optionY += dropH + layoutScale.scaleY(2);
        }
      }
      if (!langueChoisie) dropdownLangueOuvert = false;
    }

    // ── Dropdown thème ───────────────────────────────────────────
    int themeDropY = themeDropdownY + layoutScale.scaleY(28);
    int themeDropX = themeDropdownX + layoutScale.scaleX(40);
    int themeDropW = themeDropdownWidth - layoutScale.scaleX(40);
    int themeDropH = layoutScale.scaleY(42);

    if (mx >= themeDropX && mx <= themeDropX + themeDropW && my >= themeDropY && my <= themeDropY + themeDropH) {
      dropdownThemeOuvert = !dropdownThemeOuvert;
      if (dropdownThemeOuvert) dropdownLangueOuvert = false;
    } else if (dropdownThemeOuvert) {
      int optionY = themeDropY + themeDropH + layoutScale.scaleY(2);
      boolean themeChoisi = false;
      for (int i = 0; i < themes.size(); i++) {
        if (mx >= themeDropX && mx <= themeDropX + themeDropW && my >= optionY && my <= optionY + themeDropH) {
          themeSelectionne = i;
          appliquerThemeSelectionne();
          dropdownThemeOuvert = false;
          themeChoisi = true;
          break;
        }
        optionY += themeDropH + layoutScale.scaleY(2);
      }
      if (!themeChoisi) dropdownThemeOuvert = false;
    }

    // ── Toggles (positionnés à droite) ───────────────────────────
    if (!dropdownActif) {
      int toggleW = layoutScale.scaleX(56);
      int toggleH = layoutScale.scaleY(30);
      if (mx >= toggleControlX && mx <= toggleControlX + toggleW
          && my >= togglePossibilitesY && my <= togglePossibilitesY + toggleH) {
        afficherPossibilites = !afficherPossibilites;
      }
      if (mx >= toggleControlX && mx <= toggleControlX + toggleW
          && my >= toggleErreurDoubleY && my <= toggleErreurDoubleY + toggleH) {
        afficherErreurDouble = !afficherErreurDouble;
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    for (Bouton b : boutons) {
      if (b.isSourisEnfonce() && isIn(e, b)) {
        if (boutons.indexOf(b) == 1) {
          volumeEffets = 0.0f;
          volumeMusique = 0.0f;
          langueSelectionnee = 0;
          themeSelectionne = 0;
          afficherPossibilites = true;
          afficherErreurDouble = false;
          appliquerThemeSelectionne();
          dropdownLangueOuvert = false;
          dropdownThemeOuvert = false;
        } else if (boutons.indexOf(b) == 2) {
          appliquerThemeSelectionne();
          appliquerLangueSelectionnee();
          memoriserValeursAppliquees();
          SoundManager.getInstance().setVolumeEffets(volumeEffets);
          SoundManager.getInstance().setVolumeMusique(volumeMusique);
          sauvegarderParametresJoueur(game != null ? game.getJoueurCourant() : null);
          
          // Mettre à jour la grille du jeu si elle existe
          if (game != null) {
            Jeu jeuState = game.getJeu();
            if (jeuState != null) {
              Grille grille = jeuState.getGrille();
              if (grille != null) {
                grille.setAfficherPossibilites(afficherPossibilites);
                grille.setAfficherErreurDouble(afficherErreurDouble);
              }
            }
          }
        } else {
          synchroniseDepuisEntree = false;
          synchroniserEditionAvecValeursAppliquees();
          b.appliquerAction();
        }
      }
      b.setSourisEnfonce(false);
    }

    draggingEffets = false;
    draggingMusique = false;
  }

  @Override
  public void updateTexts() {
    languesAffichees = Arrays.asList(
        LangManager.get("parametres.langue.fr"),
        LangManager.get("parametres.langue.en"));
    themesAffiches = Arrays.asList(
        LangManager.get("parametres.theme.degrade"),
        LangManager.get("parametres.theme.sombre"),
        LangManager.get("parametres.theme.clair"),
        LangManager.get("parametres.theme.catppuccin"));

    titreParametres = LangManager.get("parametres.titre");
    labelEffetsSonores = LangManager.get("parametres.effets");
    labelMusique = LangManager.get("parametres.musique");
    labelLangue = LangManager.get("parametres.langue");
    labelTheme = LangManager.get("parametres.theme");
    labelPossibilites = LangManager.get("parametres.possibilites");
    labelErreurDouble = LangManager.get("parametres.erreur.double");
    suffixeMute = LangManager.get("parametres.mute");
    boutonRetourLabel = LangManager.get("common.retour");
    boutonDefautLabel = LangManager.get("parametres.defaut");
    boutonAppliquerLabel = LangManager.get("parametres.appliquer");

    if (boutons == null || boutons.size() < 3) {
      return;
    }

    ((BoutonChangeurEtat) boutons.get(0)).setLabel(boutonRetourLabel);
    ((BoutonChangeurEtat) boutons.get(1)).setLabel(boutonDefautLabel);
    ((BoutonChangeurEtat) boutons.get(2)).setLabel(boutonAppliquerLabel);
  }

  private void appliquerLangueSelectionnee() {
    if (game != null) {
      String code = codesLangues.get(Math.max(0, Math.min(codesLangues.size() - 1, langueSelectionnee)));
      game.setLangueSelectionnee(code);
    }
  }

  private int detecterThemeActuel() {
    if (getFond() == FondFonce.getInstance()) {
      return 1;
    }
    if (getFond() == FondClair.getInstance()) {
      return 2;
    }
    if (getFond() == FondCatppuccin.getInstance()) {
      return 3;
    }
    return 0;
  }

  private void appliquerThemeSelectionne() {
    Etats.setFondActuel(fondDepuisThemeSelectionne(themeSelectionne));
  }

  private Fond fondDepuisThemeSelectionne(int indexTheme) {
    switch (indexTheme) {
      case 1:
        return FondFonce.getInstance();
      case 2:
        return FondClair.getInstance();
      case 3:
        return FondCatppuccin.getInstance();
      default:
        return FondDegrade.getInstance();
    }
  }

  private void memoriserValeursAppliquees() {
    langueAppliquee = langueSelectionnee;
    themeApplique = themeSelectionne;
    volumeEffetsApplique = volumeEffets;
    volumeMusiqueApplique = volumeMusique;
    afficherPossibilitesApplique = afficherPossibilites;
    afficherErreurDoubleApplique = afficherErreurDouble;
  }

  private void synchroniserEditionAvecValeursAppliquees() {
    langueSelectionnee = langueAppliquee;
    themeSelectionne = themeApplique;
    volumeEffets = volumeEffetsApplique;
    volumeMusique = volumeMusiqueApplique;
    afficherPossibilites = afficherPossibilitesApplique;
    afficherErreurDouble = afficherErreurDoubleApplique;
    appliquerThemeSelectionne();
    dropdownLangueOuvert = false;
    dropdownThemeOuvert = false;
    indexSurvolTheme = -1;
  }

  public void chargerParametresJoueur(String pseudo) {
    if (pseudo == null || pseudo.trim().isEmpty()) {
      return;
    }

    // D'abord, charger depuis la grille du jeu si elle existe (paramètres en temps réel)
    if (game != null && etatSource == EtatJeu.GRILLE) {
      Jeu jeuState = game.getJeu();
      if (jeuState != null) {
        Grille grille = jeuState.getGrille();
        if (grille != null) {
          afficherPossibilitesApplique = grille.isAfficherPossibilites();
          afficherErreurDoubleApplique = grille.isAfficherErreurDouble();
        }
      }
    }

    ParametresJoueur params = SaveManager.chargerParametres(pseudo);
    if (params == null) {
      return;
    }

    String langue = params.getLanguage();
    langueAppliquee = codesLangues.indexOf(langue != null ? langue.toLowerCase() : "fr");
    if (langueAppliquee < 0) langueAppliquee = 0;
    themeApplique = Math.max(0, Math.min(themes.size() - 1, params.getModeSombre()));
    volumeEffetsApplique = Math.max(0f, Math.min(1f, params.getVolumeEffet() / 100f));
    volumeMusiqueApplique = Math.max(0f, Math.min(1f, params.getVolumeMusique() / 100f));
    
    // Ne pas overrider les paramètres s'ils viennent de la grille
    if (!(game != null && etatSource == EtatJeu.GRILLE)) {
      afficherPossibilitesApplique = params.isAfficherPossibilites();
      afficherErreurDoubleApplique = params.isAfficherErreurDouble();
    }

    SoundManager.getInstance().setVolumeEffets(volumeEffetsApplique);
    SoundManager.getInstance().setVolumeMusique(volumeMusiqueApplique);

    if (game != null) {
      String code = codesLangues.get(langueAppliquee);
      game.setLangueSelectionnee(code);
    }
    synchroniserEditionAvecValeursAppliquees();
  }

  private void sauvegarderParametresJoueur(String pseudo) {
    if (pseudo == null || pseudo.trim().isEmpty()) {
      return;
    }

    String codeLangue = codesLangues.get(Math.max(0, Math.min(codesLangues.size() - 1, langueSelectionnee)));
    ParametresJoueur params = new ParametresJoueur(
        pseudo,
        codeLangue,
        Math.round(volumeEffets * 100f),
        Math.round(volumeMusique * 100f),
        themeSelectionne);
    params.setAfficherPossibilites(afficherPossibilites);
    params.setAfficherErreurDouble(afficherErreurDouble);
    SaveManager.sauvegarderParametres(params);
  }

  public void sauvegarderProfilActuel() {
    String pseudo = game != null ? game.getJoueurCourant() : null;
    if (pseudo == null || pseudo.trim().isEmpty() || pseudo == "Default") {
      return;
    }

    String codeLangue = codesLangues.get(Math.max(0, Math.min(codesLangues.size() - 1, langueAppliquee)));
    ParametresJoueur params = new ParametresJoueur(
        pseudo,
        codeLangue,
        Math.round(volumeEffetsApplique * 100f),
        Math.round(volumeMusiqueApplique * 100f),
        themeApplique);
    params.setAfficherPossibilites(afficherPossibilitesApplique);
    params.setAfficherErreurDouble(afficherErreurDoubleApplique);
    SaveManager.sauvegarderParametres(params);
  }
}
