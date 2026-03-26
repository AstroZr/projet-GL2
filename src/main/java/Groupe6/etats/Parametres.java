package Groupe6.etats;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
  private static final BasicStroke STROKE_UI = new BasicStroke(2f);
  private static final Color COLOR_PANEL_BG = new Color(200, 200, 200, 120);
  private static final Color COLOR_TRACK_BG = new Color(200, 200, 200);
  private static final Color COLOR_ACCENT = new Color(70, 130, 180);
  private static final Color COLOR_DROPDOWN_BG = new Color(200, 200, 200, 180);
  private static final Color COLOR_DROPDOWN_BG_HOVER = new Color(220, 220, 220, 180);
  private static final Color COLOR_OPTION_BG = new Color(240, 240, 240, 200);
  private static final Color COLOR_THEME_SELECTED = new Color(120, 170, 230, 210);
  private static final Color COLOR_THEME_HOVER = new Color(240, 240, 240, 210);
  private static final Color COLOR_THEME_NORMAL = new Color(225, 225, 225, 200);
  
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

  // État de drag des sliders
  private boolean draggingEffets = false;
  private boolean draggingMusique = false;

  // Hover états
  private boolean hoverDropdown = false;
  private boolean hoverThemeDropdown = false;

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
  private int scaledY2;

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

    // Panel central
    panelWidth = layoutScale.scaleX(800);
    panelHeight = layoutScale.scaleY(450);
    panelX = cx - panelWidth / 2;
    panelY = cy - panelHeight / 2 - layoutScale.scaleY(50);

    // Sliders
    sliderWidth = layoutScale.scaleX(350);
    sliderEffetsX = panelX + layoutScale.scaleX(100);
    sliderEffetsY = panelY + layoutScale.scaleY(100);
    sliderMusiqueX = sliderEffetsX;
    sliderMusiqueY = sliderEffetsY + layoutScale.scaleY(70);

    // Dropdown langue
    dropdownX = sliderEffetsX;
    dropdownY = sliderMusiqueY + layoutScale.scaleY(70);
    dropdownWidth = layoutScale.scaleX(350);

    // Dropdown thème
    themeDropdownX = panelX + panelWidth / 2 + layoutScale.scaleX(50);
    themeDropdownY = panelY + layoutScale.scaleY(170);
    themeDropdownWidth = layoutScale.scaleX(260);

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
    scaledY2 = layoutScale.scaleY(2);

    // Boutons de validation, de réinitialisation ou de retour
    boutons.clear();
    int by = panelY + panelHeight + layoutScale.scaleY(30);
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

    // Dessiner le titre "Paramètres"
    g.setColor(Color.BLACK);
    g.setFont(fontTitre);
    String titre = titreParametres;
    int titreLargeur = g.getFontMetrics().stringWidth(titre);
    g.drawString(titre, layoutScale.centerX() - titreLargeur / 2, panelY - layoutScale.scaleY(30));

    // Dessiner le panel principal
    g2d.setColor(COLOR_PANEL_BG);
    RoundRectangle2D panel = new RoundRectangle2D.Float(panelX, panelY, panelWidth, panelHeight, 20, 20);
    g2d.fill(panel);
    g2d.setColor(Color.GRAY);
    g2d.setStroke(STROKE_UI);
    g2d.draw(panel);

    // Dessiner les sliders et contrôles
    dessinerSlider(g, sliderEffetsX, sliderEffetsY, sliderWidth, labelEffetsSonores, "🔊", "🔇", volumeEffets);
    dessinerSlider(g, sliderMusiqueX, sliderMusiqueY, sliderWidth, labelMusique, "🎵", "🔇", volumeMusique);
    dessinerDropdown(g);
    dessinerDropdownThemes(g);

    // Dessiner les boutons du bas
    for (Bouton b : boutons) {
      b.draw(g, getFond());
    }
  }

  private void dessinerSlider(Graphics g, int x, int y, int largeur, String label, String icon, String muteIcon,
      float valeur) {
    Graphics2D g2d = (Graphics2D) g;
    boolean mute = valeur <= 0.0f;
    String iconAffiche = mute ? muteIcon : icon;
    String labelAffiche = mute ? label + suffixeMute : label;

    // Label et icône
    g.setColor(Color.BLACK);
    g.setFont(fontEmoji);
    g.drawString(iconAffiche, x, y + scaledY20);

    g.setFont(fontLabel);
    g.drawString(labelAffiche, x + scaledX40, y + scaledY18);

    // Piste du slider
    int sliderY = y + scaledY30;
    int sliderX = x + scaledX40;
    int sliderH = layoutScale.scaleY(8);
    int trackWidth = largeur - scaledX40;

    // Fond de la piste
    g2d.setColor(COLOR_TRACK_BG);
    RoundRectangle2D piste = new RoundRectangle2D.Float(sliderX, sliderY, trackWidth, sliderH,
        sliderH, sliderH);
    g2d.fill(piste);

    // Partie remplie
    int filledWidth = (int) (trackWidth * valeur);
    if (filledWidth > 0) {
      g2d.setColor(COLOR_ACCENT);
      RoundRectangle2D filled = new RoundRectangle2D.Float(sliderX, sliderY, filledWidth, sliderH, sliderH, sliderH);
      g2d.fill(filled);
    }

    // Curseur
    int thumbSize = layoutScale.scaleUniform(20);
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

    // Label et icône
    g.setColor(Color.BLACK);
    g.setFont(fontEmoji);
    g.drawString("🌐", dropdownX, dropdownY + scaledY20);

    g.setFont(fontLabel);
    g.drawString(labelLangue, dropdownX + scaledX40, dropdownY + scaledY18);

    // Boîte du dropdown
    int dropY = dropdownY + scaledY28;
    int dropX = dropdownX + scaledX40;
    int dropW = dropdownWidth - scaledX40;
    int dropH = scaledY35;

    g2d.setColor(hoverDropdown ? COLOR_DROPDOWN_BG_HOVER : COLOR_DROPDOWN_BG);
    RoundRectangle2D box = new RoundRectangle2D.Float(dropX, dropY, dropW, dropH, 10, 10);
    g2d.fill(box);
    g2d.setColor(Color.GRAY);
    g2d.setStroke(STROKE_UI);
    g2d.draw(box);

    // Texte sélectionné
    g.setColor(Color.BLACK);
    g.setFont(fontLabel);
    g.drawString(languesAffichees.get(langueSelectionnee), dropX + scaledX10,
      dropY + scaledY22);

    // Flèche
    int arrowX = dropX + dropW - scaledX20;
    int arrowY = dropY + dropH / 2;
    dessinerFleche(g2d, arrowX, arrowY, dropdownLangueOuvert);

    // Options si ouvert
    if (dropdownLangueOuvert) {
      int optionY = dropY + dropH;
      for (int i = 0; i < languesAffichees.size(); i++) {
        if (i != langueSelectionnee) {
          g2d.setColor(COLOR_OPTION_BG);
          RoundRectangle2D optionBox = new RoundRectangle2D.Float(dropX, optionY + scaledY2, dropW, dropH,
              10, 10);
          g2d.fill(optionBox);
          g2d.setColor(Color.GRAY);
          g2d.draw(optionBox);

          g.setColor(Color.BLACK);
          g.drawString(languesAffichees.get(i), dropX + scaledX10, optionY + scaledY24);
          optionY += dropH + scaledY2;
        }
      }
    }
  }

  private void dessinerFleche(Graphics2D g2d, int x, int y, boolean up) {
    int size = layoutScale.scaleUniform(5);
    int[] xPoints = { x - size, x + size, x };
    int[] yPoints;
    if (up) {
      yPoints = new int[] { y + size / 2, y + size / 2, y - size / 2 };
    } else {
      yPoints = new int[] { y - size / 2, y - size / 2, y + size / 2 };
    }
    g2d.setColor(Color.BLACK);
    g2d.fillPolygon(xPoints, yPoints, 3);
  }

  private void dessinerDropdownThemes(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    g.setColor(Color.BLACK);
    g.setFont(fontEmoji);
    g.drawString("🎨", themeDropdownX, themeDropdownY + scaledY20);

    g.setFont(fontLabel);
    g.drawString(labelTheme, themeDropdownX + scaledX40, themeDropdownY + scaledY18);

    int dropY = themeDropdownY + scaledY28;
    int dropX = themeDropdownX + scaledX40;
    int dropW = themeDropdownWidth - scaledX40;
    int dropH = scaledY35;

    g2d.setColor(hoverThemeDropdown ? COLOR_DROPDOWN_BG_HOVER : COLOR_DROPDOWN_BG);
    RoundRectangle2D box = new RoundRectangle2D.Float(dropX, dropY, dropW, dropH, 10, 10);
    g2d.fill(box);
    g2d.setColor(Color.GRAY);
    g2d.setStroke(STROKE_UI);
    g2d.draw(box);

    g.setColor(Color.BLACK);
    g.setFont(fontLabel);
    g.drawString(themesAffiches.get(themeSelectionne), dropX + scaledX10, dropY + scaledY22);

    int arrowX = dropX + dropW - scaledX20;
    int arrowY = dropY + dropH / 2;
    dessinerFleche(g2d, arrowX, arrowY, dropdownThemeOuvert);

    if (dropdownThemeOuvert) {
      int optionY = dropY + dropH;
      for (int i = 0; i < themes.size(); i++) {
        Color fond;
        if (i == themeSelectionne) {
          fond = COLOR_THEME_SELECTED;
        } else if (i == indexSurvolTheme) {
          fond = COLOR_THEME_HOVER;
        } else {
          fond = COLOR_THEME_NORMAL;
        }

        RoundRectangle2D optionBox = new RoundRectangle2D.Float(dropX, optionY + scaledY2, dropW,
            dropH, 10, 10);
        g2d.setColor(fond);
        g2d.fill(optionBox);
        g2d.setColor(Color.GRAY);
        g2d.draw(optionBox);

        g.setColor(Color.BLACK);
        g.drawString(themesAffiches.get(i), dropX + scaledX10, optionY + scaledY24);
        optionY += dropH + scaledY2;
      }
    }
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

    // Vérifier hover dropdown
    int dropY = dropdownY + layoutScale.scaleY(28);
    int dropX = dropdownX + layoutScale.scaleX(40);
    int dropW = dropdownWidth - layoutScale.scaleX(40);
    int dropH = layoutScale.scaleY(35);
    hoverDropdown = mx >= dropX && mx <= dropX + dropW && my >= dropY && my <= dropY + dropH;

    // Vérifier hover dropdown thème
    int themeDropY = themeDropdownY + layoutScale.scaleY(28);
    int themeDropX = themeDropdownX + layoutScale.scaleX(40);
    int themeDropW = themeDropdownWidth - layoutScale.scaleX(40);
    int themeDropH = layoutScale.scaleY(35);
    hoverThemeDropdown = mx >= themeDropX && mx <= themeDropX + themeDropW && my >= themeDropY
        && my <= themeDropY + themeDropH;

    indexSurvolTheme = -1;
    if (dropdownThemeOuvert) {
      int optionY = themeDropY + themeDropH;
      for (int i = 0; i < themes.size(); i++) {
        int optY = optionY + layoutScale.scaleY(2);
        if (mx >= themeDropX && mx <= themeDropX + themeDropW && my >= optY && my <= optY + themeDropH) {
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
    for (Bouton b : boutons) {
      if (isIn(e, b)) {
        b.setSourisEnfonce(true);
      }
    }

    int mx = e.getX();
    int my = e.getY();

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

    int dropY = dropdownY + layoutScale.scaleY(28);
    int dropX = dropdownX + layoutScale.scaleX(40);
    int dropW = dropdownWidth - layoutScale.scaleX(40);
    int dropH = layoutScale.scaleY(35);
    if (mx >= dropX && mx <= dropX + dropW && my >= dropY && my <= dropY + dropH) {
      dropdownLangueOuvert = !dropdownLangueOuvert;
      if (dropdownLangueOuvert) {
        dropdownThemeOuvert = false;
      }
    } else if (dropdownLangueOuvert) {
      int optionY = dropY + dropH;
      boolean langueChoisie = false;
      for (int i = 0; i < languesAffichees.size(); i++) {
        if (i != langueSelectionnee) {
          int optY = optionY + layoutScale.scaleY(2);
          if (mx >= dropX && mx <= dropX + dropW && my >= optY && my <= optY + dropH) {
            langueSelectionnee = i;
            dropdownLangueOuvert = false;
            langueChoisie = true;
            break;
          }
          optionY += dropH + layoutScale.scaleY(2);
        }
      }
      if (!langueChoisie) {
        dropdownLangueOuvert = false;
      }
    }

    int themeDropY = themeDropdownY + layoutScale.scaleY(28);
    int themeDropX = themeDropdownX + layoutScale.scaleX(40);
    int themeDropW = themeDropdownWidth - layoutScale.scaleX(40);
    int themeDropH = layoutScale.scaleY(35);

    if (mx >= themeDropX && mx <= themeDropX + themeDropW && my >= themeDropY && my <= themeDropY + themeDropH) {
      dropdownThemeOuvert = !dropdownThemeOuvert;
      if (dropdownThemeOuvert) {
        dropdownLangueOuvert = false;
      }
    } else if (dropdownThemeOuvert) {
      int optionY = themeDropY + themeDropH;
      boolean themeChoisi = false;
      for (int i = 0; i < themes.size(); i++) {
        int optY = optionY + layoutScale.scaleY(2);
        if (mx >= themeDropX && mx <= themeDropX + themeDropW && my >= optY && my <= optY + themeDropH) {
          themeSelectionne = i;
          appliquerThemeSelectionne();
          dropdownThemeOuvert = false;
          themeChoisi = true;
          break;
        }
        optionY += themeDropH + layoutScale.scaleY(2);
      }
      if (!themeChoisi) {
        dropdownThemeOuvert = false;
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
  }

  private void synchroniserEditionAvecValeursAppliquees() {
    langueSelectionnee = langueAppliquee;
    themeSelectionne = themeApplique;
    volumeEffets = volumeEffetsApplique;
    volumeMusique = volumeMusiqueApplique;
    appliquerThemeSelectionne();
    dropdownLangueOuvert = false;
    dropdownThemeOuvert = false;
    indexSurvolTheme = -1;
  }

  public void chargerParametresJoueur(String pseudo) {
    if (pseudo == null || pseudo.trim().isEmpty()) {
      return;
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
    SaveManager.sauvegarderParametres(params);
  }

  public void sauvegarderProfilActuel() {
    String pseudo = game != null ? game.getJoueurCourant() : null;
    if (pseudo == null || pseudo.trim().isEmpty()) {
      return;
    }

    String codeLangue = codesLangues.get(Math.max(0, Math.min(codesLangues.size() - 1, langueAppliquee)));
    ParametresJoueur params = new ParametresJoueur(
        pseudo,
        codeLangue,
        Math.round(volumeEffetsApplique * 100f),
        Math.round(volumeMusiqueApplique * 100f),
        themeApplique);
    SaveManager.sauvegarderParametres(params);
  }
}
