package Groupe6.etats;

import Groupe6.game.Game;
import Groupe6.models.Grille;
import Groupe6.ui.Bouton;
import Groupe6.utilz.Constants;
import Groupe6.utilz.FontCache;
import Groupe6.utilz.LangManager;
import Groupe6.view.VueGrille;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/** État TUTORIEL : guide le joueur pas à pas sur une grille 3×3. */
public class Tutoriel extends Etats {

  private static final int LARGEUR_BOUTON = 160;
  private static final int HAUTEUR_BOUTON = 40;
  private static final int LARGEUR_BOUTON_NUM = 80;
  private static final int HAUTEUR_BOUTON_NUM = 64;
  private static final int ESPACEMENT_NUM = 12;

  private static final Font FONT_BOUTON = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 18);
  private static final Font FONT_NUM_BOUTON = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 26);
  private static final Font FONT_PANEL = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, 15);
  private static final Font FONT_PANEL_TITRE = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 16);
  private static final Font FONT_ETAPE_NUM = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 13);
  private static final Font FONT_CONTINUER = FontCache.get("Berlin Sans FB Demi", Font.ITALIC, 13);

  // ---- Types d'étapes ----

  private enum TypeEtape {
    INFO,
    FILL
  }

  private static class EtapeTutoriel {
    final TypeEtape type;
    final String cleMessage;
    final int ligne;
    final int colonne;
    final int valeurAttendue;

    /** Étape informationnelle : cliquer pour continuer. */
    EtapeTutoriel(String cleMessage) {
      this.type = TypeEtape.INFO;
      this.cleMessage = cleMessage;
      this.ligne = -1;
      this.colonne = -1;
      this.valeurAttendue = -1;
    }

    /** Étape de remplissage : attendre que la bonne valeur soit saisie dans la cellule cible. */
    EtapeTutoriel(String cleMessage, int ligne, int colonne, int valeurAttendue) {
      this.type = TypeEtape.FILL;
      this.cleMessage = cleMessage;
      this.ligne = ligne;
      this.colonne = colonne;
      this.valeurAttendue = valeurAttendue;
    }
  }

  private static final EtapeTutoriel[] ETAPES = {
    new EtapeTutoriel("tuto.etape0"),
    new EtapeTutoriel("tuto.etape1"),
    new EtapeTutoriel("tuto.etape2"),
    new EtapeTutoriel("tuto.etape3", 0, 0, 1),
    new EtapeTutoriel("tuto.etape4", 1, 0, 3),
    new EtapeTutoriel("tuto.etape5", 0, 1, 2),
    new EtapeTutoriel("tuto.etape6", 0, 2, 3),
    new EtapeTutoriel("tuto.etape7", 1, 1, 1),
    new EtapeTutoriel("tuto.etape8", 1, 2, 2),
    new EtapeTutoriel("tuto.etape9", 2, 0, 2),
    new EtapeTutoriel("tuto.etape10", 2, 1, 3),
    new EtapeTutoriel("tuto.etape11", 2, 2, 1),
    new EtapeTutoriel("tuto.etape12"),
  };

  // ---- Classe interne bouton ----

  private static class BoutonTutoAction extends Bouton {
    private String label;
    private final Runnable action;
    private final Font font;

    BoutonTutoAction(int x, int y, int largeur, int hauteur, String label, Runnable action) {
      this(x, y, largeur, hauteur, label, action, FONT_BOUTON);
    }

    BoutonTutoAction(int x, int y, int largeur, int hauteur, String label, Runnable action, Font font) {
      super(x, y, largeur, hauteur);
      this.label = label;
      this.action = action;
      this.font = font;
    }

    @Override
    public void appliquerAction() {
      if (action != null) action.run();
    }

    @Override
    public void draw(Graphics g, Groupe6.fond.Fond fond) {
      super.draw(g, fond);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setColor(fond.getCouleurTexte());
      g.setFont(font);
      int lw = g.getFontMetrics().stringWidth(label);
      int lx = x + (largeur - lw) / 2;
      int ly = y + (hauteur + g.getFontMetrics().getAscent()) / 2 - 2;
      g.drawString(label, lx, ly);
    }

    void setLabel(String label) {
      this.label = label;
    }
  }

  // ---- Champs d'instance ----

  private Grille grille;
  private VueGrille vueGrille;
  private int indexEtape = 0;

  private BoutonTutoAction boutonRetour;
  private final ArrayList<BoutonTutoAction> boutonsNumeriques = new ArrayList<>();

  private String labelRetour;

  // ---- Constructeur ----

  public Tutoriel(Game game) {
    super(game);
    boutons = new ArrayList<>();
    updateTexts();

    boutonRetour =
        new BoutonTutoAction(50, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON, labelRetour, this::retourMenu);
    boutons.add(boutonRetour);
  }

  // ---- Initialisation à l'entrée dans l'état ----

  @Override
  public void onEnter() {
    String joueur = game.getJoueurCourant();
    if (joueur == null) joueur = "Default";
    grille = new Grille(joueur, "Tutoriel", true);
    vueGrille = new VueGrille(grille);
    indexEtape = 0;
    initBoutonsNumeriques();
    appliquerEtapeCourante();
    lastLayoutWidth = -1;
    lastLayoutHeight = -1;
  }

  private void initBoutonsNumeriques() {
    for (BoutonTutoAction b : boutonsNumeriques) boutons.remove(b);
    boutonsNumeriques.clear();

    if (grille == null) return;
    int max = Math.min(9, Math.max(1, grille.getTaille()));
    for (int i = 1; i <= max; i++) {
      final int val = i;
      BoutonTutoAction btn =
          new BoutonTutoAction(
              0,
              0,
              LARGEUR_BOUTON_NUM,
              HAUTEUR_BOUTON_NUM,
              String.valueOf(i),
              () -> {
                if (vueGrille != null) vueGrille.saisirValeur(val);
              },
              FONT_NUM_BOUTON);
      boutonsNumeriques.add(btn);
      boutons.add(btn);
    }
  }

  // ---- Logique des étapes ----

  private void appliquerEtapeCourante() {
    if (indexEtape >= ETAPES.length || grille == null) return;
    EtapeTutoriel etape = ETAPES[indexEtape];
    if (etape.type == TypeEtape.FILL) {
      grille.selectionnerCellule(etape.ligne, etape.colonne);
    }
  }

  private void avancerEtape() {
    indexEtape++;
    if (indexEtape >= ETAPES.length) {
      retourMenu();
      return;
    }
    appliquerEtapeCourante();
  }

  private void verifierAvancementFill() {
    if (indexEtape >= ETAPES.length) return;
    EtapeTutoriel etape = ETAPES[indexEtape];
    if (etape.type != TypeEtape.FILL || grille == null) return;
    Groupe6.models.Cellule c = grille.getCellule(etape.ligne, etape.colonne);
    if (c != null && c.getValeur() == etape.valeurAttendue) {
      avancerEtape();
    }
  }

  private void retourMenu() {
    EtatJeu.setEtatActuel(EtatJeu.MENU);
  }

  // ---- Update & Draw ----

  @Override
  public void update() {
    getFond().update();
    if (indexEtape < ETAPES.length && ETAPES[indexEtape].type == TypeEtape.FILL) {
      verifierAvancementFill();
    }
  }

  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();
    getFond().draw(g);

    if (vueGrille != null) {
      vueGrille.draw(g, getFond());
    }

    dessinerPanneauInstruction(g);

    for (Bouton b : boutons) {
      b.draw(g, getFond());
    }
  }

  private void dessinerPanneauInstruction(Graphics g) {
    if (indexEtape >= ETAPES.length) return;
    int w = Constants.game_width;
    int h = Constants.game_height;

    String message = LangManager.get(ETAPES[indexEtape].cleMessage);

    int panelH = 130;
    int panelW = w - 80;
    int panelX = 40;
    int panelY = h - panelH - 20;

    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    Color bg = getFond().getCouleurFondBouton();
    g2d.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 210));
    g2d.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);

    Color accent = getFond().getCouleurAccent();
    g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
    g2d.setStroke(new java.awt.BasicStroke(2f));
    g2d.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);
    g2d.setStroke(new java.awt.BasicStroke(1f));

    // Numéro d'étape
    g2d.setFont(FONT_ETAPE_NUM);
    g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200));
    String numLabel = (indexEtape + 1) + " / " + ETAPES.length;
    FontMetrics fmNum = g2d.getFontMetrics();
    g2d.drawString(numLabel, panelX + panelW - fmNum.stringWidth(numLabel) - 12, panelY + fmNum.getAscent() + 8);

    // Texte principal
    g2d.setFont(FONT_PANEL);
    FontMetrics fmP = g2d.getFontMetrics();
    List<String> lignes = wrapText(message, fmP, panelW - 24);
    int textY = panelY + 20 + fmP.getAscent();
    g2d.setColor(getFond().getCouleurTexte());
    for (String ligne : lignes) {
      g2d.drawString(ligne, panelX + 12, textY);
      textY += fmP.getHeight() + 2;
    }

    // "Cliquez pour continuer" pour les étapes INFO
    if (ETAPES[indexEtape].type == TypeEtape.INFO) {
      String continuer = LangManager.get("tuto.continuer");
      g2d.setFont(FONT_CONTINUER);
      FontMetrics fmC = g2d.getFontMetrics();
      Color texte = getFond().getCouleurTexte();
      g2d.setColor(new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 150));
      g2d.drawString(
          continuer,
          panelX + panelW - fmC.stringWidth(continuer) - 12,
          panelY + panelH - 10);
    }
  }

  private List<String> wrapText(String text, FontMetrics fm, int maxWidth) {
    List<String> lines = new ArrayList<>();
    for (String paragraph : text.split("\n")) {
      String[] words = paragraph.split(" ");
      StringBuilder current = new StringBuilder();
      for (String word : words) {
        String candidate = current.length() == 0 ? word : current + " " + word;
        if (fm.stringWidth(candidate) > maxWidth && current.length() > 0) {
          lines.add(current.toString());
          current = new StringBuilder(word);
        } else {
          current = new StringBuilder(candidate);
        }
      }
      if (current.length() > 0) {
        lines.add(current.toString());
      }
    }
    return lines;
  }

  // ---- Layout ----

  @Override
  protected void applyLayout(int w, int h) {
    // Bouton Retour : coin haut-gauche, au-dessus de tout
    boutonRetour.setX(20);
    boutonRetour.setY(20);
    boutonRetour.setLargeur(LARGEUR_BOUTON);
    boutonRetour.setHauteur(HAUTEUR_BOUTON);

    // Boutons numériques : colonne droite, centrés verticalement
    // (zone disponible = hauteur écran moins la bande d'instruction en bas)
    int disponibleH = h - 150; // réserve 150px pour le panneau instruction
    int numBtnW = Math.max(LARGEUR_BOUTON_NUM, (int)(w * 0.055));
    int numBtnH = Math.max(HAUTEUR_BOUTON_NUM, (int)(h * 0.075));
    int numGap  = Math.max(ESPACEMENT_NUM, 14);
    int totalH  = boutonsNumeriques.size() * numBtnH
                  + (boutonsNumeriques.size() - 1) * numGap;
    int startY  = (disponibleH - totalH) / 2;
    int startX  = w - numBtnW - 30;

    for (int i = 0; i < boutonsNumeriques.size(); i++) {
      BoutonTutoAction b = boutonsNumeriques.get(i);
      b.setX(startX);
      b.setY(startY + i * (numBtnH + numGap));
      b.setLargeur(numBtnW);
      b.setHauteur(numBtnH);
    }

    if (vueGrille != null) vueGrille.applyLayout(w, h);
  }

  // ---- Textes ----

  @Override
  public void updateTexts() {
    labelRetour = LangManager.get("common.retour");
    if (boutonRetour != null) boutonRetour.setLabel(labelRetour);
  }

  // ---- Inputs ----

  @Override
  public void mouseClicked(MouseEvent e) {
    if (vueGrille != null) vueGrille.mouseClicked(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    for (Bouton b : boutons) {
      if (isIn(e, b)) b.setSourisEnfonce(true);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    boolean clicSurBouton = false;
    for (Bouton b : boutons) {
      if (b.isSourisEnfonce() && isIn(e, b)) {
        b.appliquerAction();
        clicSurBouton = true;
      }
      b.setSourisEnfonce(false);
    }

    if (!clicSurBouton
        && indexEtape < ETAPES.length
        && ETAPES[indexEtape].type == TypeEtape.INFO) {
      avancerEtape();
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    for (Bouton b : boutons) b.setSourisSurvol(isIn(e, b));
    if (vueGrille != null) vueGrille.mouseMoved(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyPressed(KeyEvent e) {
    if (indexEtape < ETAPES.length && ETAPES[indexEtape].type == TypeEtape.INFO) {
      int code = e.getKeyCode();
      if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
        avancerEtape();
        return;
      }
    }
    if (vueGrille != null) vueGrille.keyPressed(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {}
}
