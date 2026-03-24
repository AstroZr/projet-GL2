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

import Groupe6.fond.Fond;
import Groupe6.fond.FondCatppuccin;
import Groupe6.fond.FondClair;
import Groupe6.fond.FondDegrade;
import Groupe6.fond.FondFonce;
import Groupe6.game.Game;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.LayoutScale;

/**
 * État « paramètres » : écran de configuration du jeu.
 */
public class Parametres extends Etats {

  private static final int LARGEUR_BOUTON = 200;
  private static final int HAUTEUR_BOUTON = 44;

  private LayoutScale layoutScale;

  // Paramètres de position
  private int panelX, panelY, panelWidth, panelHeight;
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

  public Parametres(Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    layoutScale = LayoutScale.getInstance();
    boutons = new ArrayList<>();
    updateTexts();
    langueAppliquee = game != null ? game.getLangueSelectionnee() : 0; // utiliser constante pour reucpere la langue
                                                                       // pour pas a regarder toute les fenetre
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

    // Boutons de validation, de réinitialisation ou de retour
    boutons.clear();
    int by = panelY + panelHeight + layoutScale.scaleY(30);
    int bw = layoutScale.scaleX(LARGEUR_BOUTON);
    int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
    int gap = layoutScale.scaleX(20);

    int totalWidth = bw * 3 + gap * 2;
    int startX = cx - totalWidth / 2;

    boutons.add(new BoutonChangeurEtat(startX, by, bw, bh, EtatJeu.MENU, boutonRetourLabel));
    boutons.add(new BoutonChangeurEtat(startX + bw + gap, by, bw, bh, EtatJeu.PARAMETRES, boutonDefautLabel));
    boutons.add(new BoutonChangeurEtat(startX + 2 * (bw + gap), by, bw, bh, EtatJeu.PARAMETRES, boutonAppliquerLabel));
  }

  /** Met à jour le fond animé (nuages). */
  @Override
  public void update() {
    if (!synchroniseDepuisEntree) {
      synchroniserEditionAvecValeursAppliquees();
      synchroniseDepuisEntree = true;
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
    g.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, layoutScale.scaleUniform(36)));
    String titre = titreParametres;
    int titreLargeur = g.getFontMetrics().stringWidth(titre);
    g.drawString(titre, layoutScale.centerX() - titreLargeur / 2, panelY - layoutScale.scaleY(30));

    // Dessiner le panel principal
    g2d.setColor(new Color(200, 200, 200, 120));
    RoundRectangle2D panel = new RoundRectangle2D.Float(panelX, panelY, panelWidth, panelHeight, 20, 20);
    g2d.fill(panel);
    g2d.setColor(Color.GRAY);
    g2d.setStroke(new BasicStroke(2));
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
    g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, layoutScale.scaleUniform(20)));
    g.drawString(iconAffiche, x, y + layoutScale.scaleY(20));

    g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14)));
    g.drawString(labelAffiche, x + layoutScale.scaleX(40), y + layoutScale.scaleY(18));

    // Piste du slider
    int sliderY = y + layoutScale.scaleY(30);
    int sliderX = x + layoutScale.scaleX(40);
    int sliderH = layoutScale.scaleY(8);

    // Fond de la piste
    g2d.setColor(new Color(200, 200, 200));
    RoundRectangle2D piste = new RoundRectangle2D.Float(sliderX, sliderY, largeur - layoutScale.scaleX(40), sliderH,
        sliderH, sliderH);
    g2d.fill(piste);

    // Partie remplie
    int filledWidth = (int) ((largeur - layoutScale.scaleX(40)) * valeur);
    if (filledWidth > 0) {
      g2d.setColor(new Color(70, 130, 180));
      RoundRectangle2D filled = new RoundRectangle2D.Float(sliderX, sliderY, filledWidth, sliderH, sliderH, sliderH);
      g2d.fill(filled);
    }

    // Curseur
    int thumbSize = layoutScale.scaleUniform(20);
    int thumbX = sliderX + (int) ((largeur - layoutScale.scaleX(40)) * valeur) - thumbSize / 2;
    int thumbY = sliderY + sliderH / 2 - thumbSize / 2;

    g2d.setColor(Color.WHITE);
    g2d.fillOval(thumbX, thumbY, thumbSize, thumbSize);
    g2d.setColor(new Color(70, 130, 180));
    g2d.setStroke(new BasicStroke(2));
    g2d.drawOval(thumbX, thumbY, thumbSize, thumbSize);
  }

  private void dessinerDropdown(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;

    // Label et icône
    g.setColor(Color.BLACK);
    g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, layoutScale.scaleUniform(20)));
    g.drawString("🌐", dropdownX, dropdownY + layoutScale.scaleY(20));

    g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14)));
    g.drawString(labelLangue, dropdownX + layoutScale.scaleX(40), dropdownY + layoutScale.scaleY(18));

    // Boîte du dropdown
    int dropY = dropdownY + layoutScale.scaleY(28);
    int dropX = dropdownX + layoutScale.scaleX(40);
    int dropW = dropdownWidth - layoutScale.scaleX(40);
    int dropH = layoutScale.scaleY(35);

    g2d.setColor(hoverDropdown ? new Color(220, 220, 220, 180) : new Color(200, 200, 200, 180));
    RoundRectangle2D box = new RoundRectangle2D.Float(dropX, dropY, dropW, dropH, 10, 10);
    g2d.fill(box);
    g2d.setColor(Color.GRAY);
    g2d.setStroke(new BasicStroke(2));
    g2d.draw(box);

    // Texte sélectionné
    g.setColor(Color.BLACK);
    g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14)));
    g.drawString(languesAffichees.get(langueSelectionnee), dropX + layoutScale.scaleX(10),
        dropY + layoutScale.scaleY(22));

    // Flèche
    int arrowX = dropX + dropW - layoutScale.scaleX(20);
    int arrowY = dropY + dropH / 2;
    dessinerFleche(g2d, arrowX, arrowY, dropdownLangueOuvert);

    // Options si ouvert
    if (dropdownLangueOuvert) {
      int optionY = dropY + dropH;
      for (int i = 0; i < languesAffichees.size(); i++) {
        if (i != langueSelectionnee) {
          g2d.setColor(new Color(240, 240, 240, 200));
          RoundRectangle2D optionBox = new RoundRectangle2D.Float(dropX, optionY + layoutScale.scaleY(2), dropW, dropH,
              10, 10);
          g2d.fill(optionBox);
          g2d.setColor(Color.GRAY);
          g2d.draw(optionBox);

          g.setColor(Color.BLACK);
          g.drawString(languesAffichees.get(i), dropX + layoutScale.scaleX(10), optionY + layoutScale.scaleY(24));
          optionY += dropH + layoutScale.scaleY(2);
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
    g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, layoutScale.scaleUniform(20)));
    g.drawString("🎨", themeDropdownX, themeDropdownY + layoutScale.scaleY(20));

    g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14)));
    g.drawString(labelTheme, themeDropdownX + layoutScale.scaleX(40), themeDropdownY + layoutScale.scaleY(18));

    int dropY = themeDropdownY + layoutScale.scaleY(28);
    int dropX = themeDropdownX + layoutScale.scaleX(40);
    int dropW = themeDropdownWidth - layoutScale.scaleX(40);
    int dropH = layoutScale.scaleY(35);

    g2d.setColor(hoverThemeDropdown ? new Color(220, 220, 220, 180) : new Color(200, 200, 200, 180));
    RoundRectangle2D box = new RoundRectangle2D.Float(dropX, dropY, dropW, dropH, 10, 10);
    g2d.fill(box);
    g2d.setColor(Color.GRAY);
    g2d.setStroke(new BasicStroke(2));
    g2d.draw(box);

    g.setColor(Color.BLACK);
    g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14)));
    g.drawString(themesAffiches.get(themeSelectionne), dropX + layoutScale.scaleX(10), dropY + layoutScale.scaleY(22));

    int arrowX = dropX + dropW - layoutScale.scaleX(20);
    int arrowY = dropY + dropH / 2;
    dessinerFleche(g2d, arrowX, arrowY, dropdownThemeOuvert);

    if (dropdownThemeOuvert) {
      int optionY = dropY + dropH;
      for (int i = 0; i < themes.size(); i++) {
        Color fond;
        if (i == themeSelectionne) {
          fond = new Color(120, 170, 230, 210);
        } else if (i == indexSurvolTheme) {
          fond = new Color(240, 240, 240, 210);
        } else {
          fond = new Color(225, 225, 225, 200);
        }

        RoundRectangle2D optionBox = new RoundRectangle2D.Float(dropX, optionY + layoutScale.scaleY(2), dropW,
            dropH, 10, 10);
        g2d.setColor(fond);
        g2d.fill(optionBox);
        g2d.setColor(Color.GRAY);
        g2d.draw(optionBox);

        g.setColor(Color.BLACK);
        g.drawString(themesAffiches.get(i), dropX + layoutScale.scaleX(10), optionY + layoutScale.scaleY(24));
        optionY += dropH + layoutScale.scaleY(2);
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

          // Sauvegarde des réglages sons et thème du joueur
          String pseudo = game.getJoueurCourant();
          if (pseudo != null && !pseudo.equals("Invité")) {
            Groupe6.save.ParametresJoueur pj = new Groupe6.save.ParametresJoueur(pseudo, langueSelectionnee == 0 ? "Fr" : "En", (int)(volumeEffets * 100), (int)(volumeMusique * 100), themeSelectionne);
            Groupe6.save.SaveManager.sauvegarderParametres(pj);
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
    boolean en = game != null && game.isEnglish();

    languesAffichees = en ? Arrays.asList("French", "English") : Arrays.asList("Français", "English");
    themesAffiches = en ? Arrays.asList("Gradient", "Dark mode", "Light mode", "Catppuccin")
        : new ArrayList<>(themes);

    titreParametres = en ? "Settings" : "Paramètres";
    labelEffetsSonores = en ? "Sound effects" : "Effets sonores";
    labelMusique = en ? "Music" : "Musique";
    labelLangue = en ? "Language" : "Langue";
    labelTheme = en ? "Theme" : "Thème";
    suffixeMute = en ? " (Muted)" : " (Muet)";
    boutonRetourLabel = en ? "Back" : "Retour";
    boutonDefautLabel = en ? "Default" : "Défaut";
    boutonAppliquerLabel = en ? "Apply" : "Appliquer";

    if (boutons == null || boutons.size() < 3) {
      return;
    }

    ((BoutonChangeurEtat) boutons.get(0)).setLabel(boutonRetourLabel);
    ((BoutonChangeurEtat) boutons.get(1)).setLabel(boutonDefautLabel);
    ((BoutonChangeurEtat) boutons.get(2)).setLabel(boutonAppliquerLabel);
  }

  private void appliquerLangueSelectionnee() {
    if (game != null) {
      game.setLangueSelectionnee(langueSelectionnee);
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
}
