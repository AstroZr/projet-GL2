package Groupe6.etats;

import Groupe6.aide.AideManager;
import Groupe6.aide.AideTextuel;
import Groupe6.game.Game;
import Groupe6.models.Grille;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonAide;
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
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

/** État du jeu en cours : affiche et gère la grille Mathdoku. */
public class Jeu extends Etats {

  /*Variables d'instance*/
  private static final int LARGEUR_BOUTON = 200;
  private static final int HAUTEUR_BOUTON = 44;
  private static final int LARGEUR_BOUTON_UNDO = 140;
  private static final int ESPACEMENT_BOUTONS = 24;
  private static final int LARGEUR_BOUTON_NUM = 44;
  private static final int HAUTEUR_BOUTON_NUM = 32;
  private static final int ESPACEMENT_NUM = 8;
  private static final Font FONT_TIMER = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 22);
  private static final Font FONT_LABEL = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 16);
  private static final Font FONT_BOUTON = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 16);
  private static final Font FONT_OVERLAY_TITRE = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 20);
  private static final Font FONT_OVERLAY_TEXTE = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, 15);
  private static final Font FONT_MSG_TITRE = FontCache.get("Berlin Sans FB Demi", Font.BOLD, 14);
  private static final Font FONT_MSG_TEXTE = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, 13);

  private Grille grille;
  private VueGrille vueGrille;
  private BoutonJeuAction boutonRetour;
  private BoutonJeuAction boutonUndo;
  private BoutonJeuAction boutonRedo;
  private BoutonJeuAction boutonModeCandidat;
  private BoutonJeuAction boutonParametres;
  private BoutonAide boutonAide;
  private BoutonJeuAction boutonAbandon;
  private final ArrayList<BoutonJeuAction> boutonsNumeriques = new ArrayList<>();
  private static final int TAILLE_GRILLE = 4; // Grille 4x4 par défaut
  private String labelRetour;
  private String labelAide;
  private String labelUndo;
  private String labelRedo;
  private String labelCandidatOn;
  private String labelCandidatOff;
  private String labelParametres;
  private String labelTimer;
  private long startTimerMillis;
  private long baseElapsedMillis;
  private boolean timerPaused = false;
  private long pausedElapsedMillis = 0;
  private boolean overlayAideVisible = false;
  private String overlayAideTitre = "";
  private String overlayAideTexte = "";
  private final List<AideTextuel> messagesAide = new ArrayList<>();
  private int messagesScrollOffset = 0;
  private boolean victoireAnnoncee = false;
  private String labelVictoireTitre;
  private String labelVictoireTexte;
  private long lastTimerSecond = -1;
  private String cachedTimerText = "00:00:00";
  private List<String> overlayAideLinesCache = new ArrayList<>();
  private String overlayAideTexteCacheKey = "";
  private int overlayAideMaxWidthCache = -1;
  private static final String[] TWO_DIGITS = {
    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09"
  };
  // Pour bouton abandon
  private boolean overlayAbandonVisible = false;
  private String labelAbandon;
  private String labelRecommencer;
  // Rectangles des 3 boutons du popup du bouton abandon
  private java.awt.Rectangle rectBtnSolution;
  private java.awt.Rectangle rectBtnRecommencer;
  private java.awt.Rectangle rectBtnAnnuler;

  private static class BoutonJeuAction extends Bouton {
    private String label;
    private final Runnable action;

    BoutonJeuAction(int x, int y, int largeur, int hauteur, String label, Runnable action) {
      super(x, y, largeur, hauteur);
      this.label = label;
      this.action = action;
    }

    @Override
    public void appliquerAction() {
      if (action != null) {
        action.run();
      }
    }

    @Override
    public void draw(Graphics g, Groupe6.fond.Fond fond) {
      super.draw(g, fond);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setColor(fond.getCouleurTexte());
      g.setFont(FONT_BOUTON);
      int lw = g.getFontMetrics().stringWidth(label);
      int lx = x + (largeur - lw) / 2;
      int ly = y + (hauteur + g.getFontMetrics().getAscent()) / 2 - 2;
      g.drawString(label, lx, ly);
    }

    void setLabel(String label) {
      this.label = label;
    }
  }

  public Jeu(Game game) {
    super(game);
    initClasses();
  }

  private void initClasses() {
    boutons = new ArrayList<>();
    updateTexts();

    AideManager aideManager = AideManager.getInstance();

    String joueurActuel = game.getJoueurCourant();
    if (joueurActuel == null) joueurActuel = "testUser";
    grille = new Grille(joueurActuel, "test");
    victoireAnnoncee = false;

    // Créer la vue
    vueGrille = new VueGrille(grille);
    baseElapsedMillis = grille.getTempsEcoule();
    startTimerMillis = System.currentTimeMillis();

    // Bouton retour au menu - position initiale
    int cx = 50;
    int cy = 950;
    boutonRetour =
        new BoutonJeuAction(
            cx, cy, LARGEUR_BOUTON, HAUTEUR_BOUTON, labelRetour, this::quitterNiveauVersMenu);
    boutons.add(boutonRetour);

    // Bouton aide
    int aideX = cx + LARGEUR_BOUTON + ESPACEMENT_BOUTONS;
    boutonAide = new BoutonAide(aideX, cy, LARGEUR_BOUTON, HAUTEUR_BOUTON, labelAide, this);
    boutons.add(boutonAide);

    // Bouton undo
    int undoX = aideX + LARGEUR_BOUTON + ESPACEMENT_BOUTONS;
    boutonUndo =
        new BoutonJeuAction(
            undoX, cy, LARGEUR_BOUTON_UNDO, HAUTEUR_BOUTON, labelUndo, this::undoAction);
    boutons.add(boutonUndo);

    // Bouton redo
    int redoX = undoX + LARGEUR_BOUTON_UNDO + ESPACEMENT_BOUTONS;
    boutonRedo =
        new BoutonJeuAction(
            redoX, cy, LARGEUR_BOUTON_UNDO, HAUTEUR_BOUTON, labelRedo, this::redoAction);
    boutons.add(boutonRedo);

    // Bouton mode candidat
    int candidatX = redoX + LARGEUR_BOUTON_UNDO + ESPACEMENT_BOUTONS;
    boutonModeCandidat =
        new BoutonJeuAction(
            candidatX,
            cy,
            LARGEUR_BOUTON,
            HAUTEUR_BOUTON,
            "",
            () -> {
              if (vueGrille != null) {
                vueGrille.toggleModeCandidat();
                updateLabelModeCandidat();
              }
            });
    boutons.add(boutonModeCandidat);

    int paramX = candidatX + LARGEUR_BOUTON + ESPACEMENT_BOUTONS;
    boutonParametres =
        new BoutonJeuAction(
            paramX,
            cy,
            LARGEUR_BOUTON,
            HAUTEUR_BOUTON,
            labelParametres,
            this::ouvrirParametresDepuisJeu);
    boutons.add(boutonParametres);

    int abandonX = paramX + LARGEUR_BOUTON + ESPACEMENT_BOUTONS;
    boutonAbandon = new BoutonJeuAction(abandonX, cy, LARGEUR_BOUTON, HAUTEUR_BOUTON, labelAbandon, this::abandonnerAction);
    boutons.add(boutonAbandon);

    initBoutonsNumeriques();
    updateLabelModeCandidat();
  }

  public void chargerNiveau(String idNiveau) {
    System.out.println("Chargement du niveau : " + idNiveau);
    if (isSameUnfinishedLevel(idNiveau)) {
      restaurerNiveauEnMemoire();
      return;
    }

    sauvegarderNiveauCourantSiDifferent(idNiveau);

    String joueurActuel = getJoueurActuelOuDefaut();
    Grille grilleChargee = new Grille(joueurActuel, idNiveau);
    grille = reinitialiserSiNiveauDejaComplete(joueurActuel, idNiveau, grilleChargee);

    victoireAnnoncee = false;
    vueGrille = new VueGrille(grille);
    resetTimerDepuisGrille();
    resetUIApresChargement();
  }

  private boolean isSameUnfinishedLevel(String idNiveau) {
    if (grille == null) {
      return false;
    }
    if (!idNiveau.equals(grille.getIdNiveau())) {
      return false;
    }
    return !grille.estComplete();
  }

  private void restaurerNiveauEnMemoire() {
    resetTimerDepuisGrille();
    resetUIApresChargement();
  }

  private void sauvegarderNiveauCourantSiDifferent(String idNiveau) {
    if (grille == null) {
      return;
    }
    if (idNiveau.equals(grille.getIdNiveau())) {
      return;
    }
    sauvegarderEtatNiveauCourant();
  }

  private String getJoueurActuelOuDefaut() {
    String joueurActuel = game.getJoueurCourant();
    if (joueurActuel == null) {
      return "testUser";
    }
    return joueurActuel;
  }

  private Grille reinitialiserSiNiveauDejaComplete(String joueurActuel, String idNiveau, Grille grilleChargee) {
    if (!grilleChargee.estComplete()) {
      return grilleChargee;
    }

    long tempsTermine = grilleChargee.getTempsEcoule();
    if (tempsTermine > 0) {
      SaveManager.enregistrerMeilleurTemps(joueurActuel, idNiveau, tempsTermine);
    }
    return new Grille(joueurActuel, idNiveau, true);
  }

  private void resetTimerDepuisGrille() {
    timerPaused = false;
    pausedElapsedMillis = 0;
    baseElapsedMillis = grille.getTempsEcoule();
    startTimerMillis = System.currentTimeMillis();
  }

  private void resetUIApresChargement() {
    if (boutonAide != null) {
      boutonAide.resetProgression();
    }
    messagesAide.clear();
    messagesScrollOffset = 0;
    initBoutonsNumeriques();
    updateLabelModeCandidat();
    lastLayoutWidth = -1;
    lastLayoutHeight = -1;
  }

  // getters pour la save
  public Grille getGrille() {
    return grille;
  }

  public void showAideOverlay(String titre, String texte) {
    this.overlayAideTitre = titre != null ? titre : "";
    this.overlayAideTexte = texte != null ? texte : "";
    this.overlayAideVisible = true;
    this.overlayAideTexteCacheKey = "";
    this.overlayAideMaxWidthCache = -1;
    this.overlayAideLinesCache.clear();
  }

  public void hideAideOverlay() {
    this.overlayAideVisible = false;
  }

  public void addAideMessage(AideTextuel msg) {
    if (msg != null) {
      messagesAide.add(new AideTextuel(msg));
      messagesScrollOffset = Integer.MAX_VALUE; // sera clampé au prochain dessin
    }
  }

  @Override
  public void update() {
    if (timerPaused && !victoireAnnoncee) {
      resumeTimer();
    }
    // Mettre à jour la logique du jeu si nécessaire
    if (grille != null) {
      grille.setTempsEcoule(getElapsedMillis());
    }
    verifierVictoire();
    getFond().update();
  }

  @Override
  public void updateLayout(int gameWidth, int gameHeight) {
    applyLayout(gameWidth, gameHeight);
  }

  @Override
  protected void applyLayout(int w, int h) {
    // Repositionner le bouton retour en fonction de la taille de l'écran
    int x = 50;
    int cy = h - 130; // 130px du bas

    if (boutons.size() >= 6) {
      boutons.get(0).setX(x);
      boutons.get(0).setY(cy);

      int aideX = x + LARGEUR_BOUTON + ESPACEMENT_BOUTONS;
      boutons.get(1).setX(aideX);
      boutons.get(1).setY(cy);

      int undoX = aideX + LARGEUR_BOUTON + ESPACEMENT_BOUTONS;
      boutons.get(2).setX(undoX);
      boutons.get(2).setY(cy);

      int redoX = undoX + LARGEUR_BOUTON_UNDO + ESPACEMENT_BOUTONS;
      boutons.get(3).setX(redoX);
      boutons.get(3).setY(cy);

      int candidatX = redoX + LARGEUR_BOUTON_UNDO + ESPACEMENT_BOUTONS;
      boutons.get(4).setX(candidatX);
      boutons.get(4).setY(cy);

      int panelX = Math.max(20, w - 250);
      int panelY = Math.max(130, (h - 360) / 2);
      boutons.get(4).setX(panelX);
      boutons.get(4).setY(panelY + 120);
      boutons.get(4).setLargeur(200);
      boutons.get(4).setHauteur(HAUTEUR_BOUTON);

      boutons.get(5).setX(panelX);
      boutons.get(5).setY(panelY + 70);
      boutons.get(5).setLargeur(200);
      boutons.get(5).setHauteur(HAUTEUR_BOUTON);
    }

    int panelX = Math.max(20, w - 250);
    int panelY = Math.max(130, (h - 360) / 2);
    for (int i = 0; i < boutonsNumeriques.size(); i++) {
      int col = i % 2;
      int row = i / 2;
      BoutonJeuAction b = boutonsNumeriques.get(i);
      b.setX(panelX + col * (LARGEUR_BOUTON_NUM + ESPACEMENT_NUM));
      b.setY(panelY + 180 + row * (HAUTEUR_BOUTON_NUM + ESPACEMENT_NUM));
      b.setLargeur(LARGEUR_BOUTON_NUM);
      b.setHauteur(HAUTEUR_BOUTON_NUM);
    }

    vueGrille.applyLayout(w, h);
  }

  @Override
  public void draw(Graphics g) {
    ensureLayoutUpToDate();

    getFond().draw(g);

    // Déléguer l'affichage à la vue grille
    vueGrille.draw(g, getFond());

    drawPanelDroit(g);
    drawAideMessages(g);

    // Dessiner les boutons
    for (Bouton b : boutons) {
      b.draw(g, getFond());
    }

    if (overlayAideVisible) {
      drawAideOverlay(g);
    }

    if(overlayAbandonVisible){
      drawAbandonOverlay(g);
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // Déléguer le clic à la vue grille
    vueGrille.mouseClicked(e);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    for (Bouton b : boutons) {
      if (isIn(e, b)) {
        b.setSourisEnfonce(true);
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if(overlayAbandonVisible){
      int mx = e.getX(), my = e.getY();
      if(rectBtnSolution != null && rectBtnSolution.contains(mx, my)) voirSolutionAction();
      else if(rectBtnRecommencer != null && rectBtnRecommencer.contains(mx, my)) recommencerPartie();
      else if(rectBtnAnnuler != null && rectBtnAnnuler.contains(mx, my)){
        overlayAbandonVisible = false;
        resumeTimer();
      }
      return;
    }
    if (overlayAideVisible) {
      hideAideOverlay();
      if (victoireAnnoncee) {
        EtatJeu.setEtatActuel(EtatJeu.SELECTION);
      }
      return;
    }
    for (Bouton b : boutons) {
      if (b.isSourisEnfonce() && isIn(e, b)) {
        b.appliquerAction();
      }
      b.setSourisEnfonce(false);
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    for (Bouton b : boutons) {
      b.setSourisSurvol(isIn(e, b));
    }
    if (vueGrille != null) {
      vueGrille.mouseMoved(e);
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
    vueGrille.keyTyped(e);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
      undoAction();
      return;
    }

    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
      redoAction();
      return;
    }

    if (e.getKeyCode() == KeyEvent.VK_C && vueGrille != null) {
      vueGrille.toggleModeCandidat();
      updateLabelModeCandidat();
      return;
    }

    if (vueGrille != null) {
      vueGrille.keyPressed(e);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Pas d'action spécifique pour le moment
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // Pas d'action spécifique pour le moment
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    int w = Constants.game_width;
    int h = Constants.game_height;
    int tailleGrille = (int) (h / 1.5f);
    int gridOffsetX = (w - tailleGrille) / 2;
    int panelW = Math.min(280, gridOffsetX - 40);
    if (panelW < 80) return;
    int panelX = 20;
    int panelY = 100;
    int panelH = h - 200;
    int mx = e.getX();
    int my = e.getY();
    if (mx >= panelX && mx <= panelX + panelW && my >= panelY && my <= panelY + panelH) {
      messagesScrollOffset += e.getWheelRotation() * 30;
    }
  }

  @Override
  public void updateTexts() {
    labelRetour = LangManager.get("common.retour");
    labelAide = LangManager.get("jeu.aide");
    labelUndo = LangManager.get("jeu.undo");
    labelRedo = LangManager.get("jeu.redo");
    labelCandidatOn = LangManager.get("jeu.candidat.on");
    labelCandidatOff = LangManager.get("jeu.candidat.off");
    labelParametres = LangManager.get("menu.parametres");
    labelTimer = LangManager.get("jeu.timer");
    labelVictoireTitre = LangManager.get("jeu.victoire.titre");
    labelVictoireTexte = LangManager.get("jeu.victoire.texte");
    labelAbandon = LangManager.get("jeu.abandon");
    labelRecommencer = LangManager.get("jeu.recommencer");

    if (boutons == null || boutons.isEmpty()) {
      return;
    }
    if (boutonRetour != null) {
      boutonRetour.setLabel(labelRetour);
    }
    if (boutons.size() > 1 && boutons.get(1) instanceof BoutonAide) {
      ((BoutonAide) boutons.get(1)).setLabel(labelAide);
    }
    if (boutonUndo != null) {
      boutonUndo.setLabel(labelUndo);
    }
    if (boutonRedo != null) {
      boutonRedo.setLabel(labelRedo);
    }
    if (boutonParametres != null) {
      boutonParametres.setLabel(labelParametres);
    }
    updateLabelModeCandidat();
  }

  private void undoAction() {
    int typeAction = grille.retourArriere();
    if (typeAction == Grille.ACTION_AIDE && boutonAide != null) {
      boutonAide.undoAide();
    }
  }

  private void redoAction() {
    int typeAction = grille.retourAvant();
    if (typeAction == Grille.ACTION_AIDE && boutonAide != null) {
      boutonAide.redoAide();
    }
  }

  private void abandonnerAction(){
    if(grille == null) return;
    if(grille.isSolutionAffichee()){
      recommencerPartie();
      return;
    }
    pauseTimer();
    overlayAbandonVisible = true;
  }

  private void voirSolutionAction(){
    overlayAbandonVisible = false;
    Groupe6.save.Niveau niveau = SaveManager.chargerNiveau(grille.getIdNiveau());
    if(niveau == null) return;
    grille.afficherSolution(niveau.getMatriceCorrection());
    boutonAbandon.setLabel(labelRecommencer);
  }

  private void recommencerPartie(){
    overlayAbandonVisible = false;
    String idNiveau = grille.getIdNiveau();
    String joueur = getJoueurActuelOuDefaut();

    grille = new Grille(joueur, idNiveau, true);
    victoireAnnoncee = false;
    vueGrille = new VueGrille(grille);
    baseElapsedMillis = 0;
    startTimerMillis = System.currentTimeMillis();
    timerPaused = false;
    pausedElapsedMillis = 0;
    resetUIApresChargement();
    boutonAbandon.setLabel(labelAbandon);
  }

  private void updateLabelModeCandidat() {
    if (boutonModeCandidat == null) {
      return;
    }
    boolean actif = vueGrille != null && vueGrille.isModeCandidat();
    boutonModeCandidat.setLabel(actif ? labelCandidatOn : labelCandidatOff);
  }

  private void initBoutonsNumeriques() {
    for (BoutonJeuAction b : boutonsNumeriques) {
      boutons.remove(b);
    }
    boutonsNumeriques.clear();

    int max = Math.min(9, Math.max(1, grille.getTaille()));
    for (int i = 1; i <= max; i++) {
      final int valeur = i;
      BoutonJeuAction btn =
          new BoutonJeuAction(
              0,
              0,
              LARGEUR_BOUTON_NUM,
              HAUTEUR_BOUTON_NUM,
              String.valueOf(i),
              () -> {
                if (vueGrille != null) {
                  vueGrille.saisirValeur(valeur);
                }
              });
      boutonsNumeriques.add(btn);
      boutons.add(btn);
    }
  }

  private void drawAideOverlay(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    int w = Constants.game_width;
    int h = Constants.game_height;

    // Fond semi-transparent
    g2d.setColor(new Color(0, 0, 0, 160));
    g2d.fillRect(0, 0, w, h);

    // Boîte centrale
    int boxW = Math.min(480, w - 80);
    int boxH = 220;
    int boxX = (w - boxW) / 2;
    int boxY = (h - boxH) / 2;

    g2d.setColor(getFond().getCouleurFondBouton());
    g2d.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

    // Titre
    g2d.setColor(getFond().getCouleurTexte());
    g2d.setFont(FONT_OVERLAY_TITRE);
    FontMetrics fmTitre = g2d.getFontMetrics();
    int titreX = boxX + (boxW - fmTitre.stringWidth(overlayAideTitre)) / 2;
    g2d.drawString(overlayAideTitre, titreX, boxY + 42);

    // Séparateur
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawLine(boxX + 20, boxY + 55, boxX + boxW - 20, boxY + 55);

    // Texte (multi-lignes)
    g2d.setColor(getFond().getCouleurTexte());
    g2d.setFont(FONT_OVERLAY_TEXTE);
    FontMetrics fmTexte = g2d.getFontMetrics();
    int textX = boxX + 24;
    int textY = boxY + 80;
    int maxWidth = boxW - 48;
    List<String> lines = getOverlayLines(fmTexte, maxWidth);
    for (String line : lines) {
      g2d.drawString(line, textX, textY);
      textY += fmTexte.getHeight() + 2;
    }

    // Bouton fermer
    int btnW = 120;
    int btnH = 36;
    int btnX = boxX + (boxW - btnW) / 2;
    int btnY = boxY + boxH - 52;
    g2d.setColor(getFond().getCouleurFondCellule());
    g2d.fillRoundRect(btnX, btnY, btnW, btnH, 10, 10);
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawRoundRect(btnX, btnY, btnW, btnH, 10, 10);
    g2d.setColor(getFond().getCouleurTexte());
    g2d.setFont(FONT_LABEL);
    FontMetrics fmBtn = g2d.getFontMetrics();
    String labelOk = "OK";
    g2d.drawString(
        labelOk,
        btnX + (btnW - fmBtn.stringWidth(labelOk)) / 2,
        btnY + (btnH + fmBtn.getAscent()) / 2 - 2);
  }

  private void drawAbandonOverlay(Graphics g){
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    int w = Constants.game_width;
    int h = Constants.game_height;

    g2d.setColor(new Color(0, 0, 0, 160));
    g2d.fillRect(0, 0, w, h);

    int boxW = Math.min(480, w - 80);
    int boxH = 160;
    int boxX = (w - boxW) / 2;
    int boxY = (h - boxH) / 2;

    g2d.setColor(getFond().getCouleurFondBouton());
    g2d.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

    g2d.setFont(FONT_OVERLAY_TITRE);
    g2d.setColor(getFond().getCouleurTexte());
    String titre = LangManager.get("jeu.abandon.titre");
    FontMetrics fm = g2d.getFontMetrics();
    g2d.drawString(titre, boxX + (boxW - fm.stringWidth(titre)) / 2, boxY + 42);

    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawLine(boxX + 20, boxY + 55, boxX + boxW - 20, boxY + 55);

    int btnW = 130, btnH = 36, gap = 16;
    int totalW = 3 * btnW + 2 * gap;
    int bx = boxX + (boxW - totalW) / 2;
    int by = boxY + boxH - 52;

    rectBtnSolution = new java.awt.Rectangle(bx, by, btnW, btnH);
    rectBtnRecommencer = new java.awt.Rectangle(bx + btnW + gap, by, btnW, btnH);
    rectBtnAnnuler = new java.awt.Rectangle(bx + 2 * (btnW+gap), by, btnW, btnH);

    dessinerBoutonPopup(g2d, rectBtnSolution, LangManager.get("jeu.abandon.solution"));
    dessinerBoutonPopup(g2d, rectBtnRecommencer, LangManager.get("jeu.recommencer"));
    dessinerBoutonPopup(g2d, rectBtnAnnuler, LangManager.get("common.annuler"));
  }

  private void dessinerBoutonPopup(Graphics2D g2d, java.awt.Rectangle r, String label){
    g2d.setColor(getFond().getCouleurFondCellule());
    g2d.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);
    g2d.setColor(getFond().getCouleurTexte());
    g2d.setFont(FONT_LABEL);
    FontMetrics fm = g2d.getFontMetrics();
    g2d.drawString(label, r.x + (r.width - fm.stringWidth(label)) / 2, r.y + (r.height + fm.getAscent()) / 2 - 2);
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

  private List<String> getOverlayLines(FontMetrics fm, int maxWidth) {
    if (!overlayAideTexte.equals(overlayAideTexteCacheKey) || maxWidth != overlayAideMaxWidthCache) {
      overlayAideLinesCache = wrapText(overlayAideTexte, fm, maxWidth);
      overlayAideTexteCacheKey = overlayAideTexte;
      overlayAideMaxWidthCache = maxWidth;
    }
    return overlayAideLinesCache;
  }

  private void drawAideMessages(Graphics g) {
    int w = Constants.game_width;
    int h = Constants.game_height;
    int tailleGrille = (int) (h / 1.5f);
    int gridOffsetX = (w - tailleGrille) / 2;
    int panelW = Math.min(280, gridOffsetX - 40);
    if (panelW < 80 || messagesAide.isEmpty()) return;

    int panelX = 20;
    int panelY = 100;
    int panelH = h - 200;

    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Fond du panneau
    g2d.setColor(getFond().getCouleurFondBouton());
    g2d.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);

    // En-tête du panneau
    g2d.setFont(FONT_MSG_TITRE);
    FontMetrics fmTitre = g2d.getFontMetrics();
    g2d.setColor(getFond().getCouleurTexte());
    String panelLabel = LangManager.get("jeu.aide");
    g2d.drawString(panelLabel, panelX + (panelW - fmTitre.stringWidth(panelLabel)) / 2,
        panelY + 10 + fmTitre.getAscent());
    int headerH = 10 + fmTitre.getHeight() + 6;
    g2d.setColor(getFond().getCouleurBordreBouton());
    g2d.drawLine(panelX + 8, panelY + headerH, panelX + panelW - 8, panelY + headerH);

    int contentY = panelY + headerH + 4;
    int contentH = panelH - headerH - 8;

    // Pré-calculer les hauteurs de chaque carte
    g2d.setFont(FONT_MSG_TEXTE);
    FontMetrics fmTexte = g2d.getFontMetrics();
    int cardPad = 8;
    int cardMargin = 6;
    int maxTextW = panelW - 2 * cardPad - 8;
    int titreLineH = fmTitre.getHeight();

    int[] cardHeights = new int[messagesAide.size()];
    int totalH = 0;
    for (int i = 0; i < messagesAide.size(); i++) {
      List<String> lines = wrapText(messagesAide.get(i).getTexte(), fmTexte, maxTextW);
      cardHeights[i] = cardPad + titreLineH + 4 + 1 + 4
          + lines.size() * (fmTexte.getHeight() + 2) + cardPad;
      if (i > 0) totalH += cardMargin;
      totalH += cardHeights[i];
    }

    // Clamp du scroll
    int maxScroll = Math.max(0, totalH - contentH);
    if (messagesScrollOffset > maxScroll) messagesScrollOffset = maxScroll;
    if (messagesScrollOffset < 0) messagesScrollOffset = 0;

    // Clip sur la zone de contenu
    Shape oldClip = g2d.getClip();
    g2d.setClip(panelX + 2, contentY, panelW - 4, contentH);

    int cardX = panelX + 4;
    int cardW = panelW - 8;
    int drawY = contentY - messagesScrollOffset;

    for (int i = 0; i < messagesAide.size(); i++) {
      if (i > 0) drawY += cardMargin;
      int cardH = cardHeights[i];

      if (drawY + cardH >= contentY && drawY <= contentY + contentH) {
        // Fond de la carte
        g2d.setColor(getFond().getCouleurFondCellule());
        g2d.fillRoundRect(cardX, drawY, cardW, cardH, 10, 10);
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.drawRoundRect(cardX, drawY, cardW, cardH, 10, 10);

        // Titre de la carte
        g2d.setFont(FONT_MSG_TITRE);
        fmTitre = g2d.getFontMetrics();
        g2d.setColor(getFond().getCouleurTexte());
        g2d.drawString(messagesAide.get(i).getTitre(), cardX + cardPad,
            drawY + cardPad + fmTitre.getAscent());

        // Séparateur
        int sepY = drawY + cardPad + titreLineH + 4;
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.drawLine(cardX + 4, sepY, cardX + cardW - 4, sepY);

        // Texte de la carte
        g2d.setFont(FONT_MSG_TEXTE);
        fmTexte = g2d.getFontMetrics();
        g2d.setColor(getFond().getCouleurTexte());
        List<String> lines = wrapText(messagesAide.get(i).getTexte(), fmTexte, maxTextW);
        int textY = sepY + 4 + fmTexte.getAscent();
        for (String line : lines) {
          g2d.drawString(line, cardX + cardPad, textY);
          textY += fmTexte.getHeight() + 2;
        }
      }
      drawY += cardH;
    }

    g2d.setClip(oldClip);

    // Barre de défilement
    if (totalH > contentH && maxScroll > 0) {
      int thumbH = Math.max(20, contentH * contentH / totalH);
      int thumbY = contentY + (int) ((long) (contentH - thumbH) * messagesScrollOffset / maxScroll);
      g2d.setColor(getFond().getCouleurBordreBouton());
      g2d.fillRoundRect(panelX + panelW - 6, thumbY, 4, thumbH, 4, 4);
    }
  }

  private void drawPanelDroit(Graphics g) {
    int panelX = Math.max(20, Constants.game_width - 250);
    int panelY = Math.max(130, (Constants.game_height - 360) / 2);

    g.setColor(getFond().getCouleurFondBouton());
    g.fillRoundRect(panelX - 8, panelY - 12, 230, 320, 18, 18);
    g.setColor(getFond().getCouleurBordreBouton());
    g.drawRoundRect(panelX - 8, panelY - 12, 230, 320, 18, 18);

    g.setColor(getFond().getCouleurTexte());
    g.setFont(FONT_LABEL);
    g.drawString(labelTimer, panelX, panelY + 14);

    g.setColor(getFond().getCouleurFondCellule());
    g.fillRoundRect(panelX, panelY + 22, 120, 40, 10, 10);
    g.setColor(getFond().getCouleurBordreBouton());
    g.drawRoundRect(panelX, panelY + 22, 120, 40, 10, 10);

    g.setColor(getFond().getCouleurTexte());
    g.setFont(FONT_TIMER);
    g.drawString(formatTimer(), panelX + 13, panelY + 50);
  }

  private String formatTimer() {
    long elapsed = getElapsedMillis();
    long totalSeconds = elapsed / 1000;
    if (totalSeconds != lastTimerSecond) {
      lastTimerSecond = totalSeconds;
      cachedTimerText = formatHms(totalSeconds);
    }
    return cachedTimerText;
  }

  private String formatHms(long totalSeconds) {
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;
    return twoDigits(hours) + ":" + twoDigits(minutes) + ":" + twoDigits(seconds);
  }

  private String twoDigits(long value) {
    if (value >= 0 && value < 10) {
      return TWO_DIGITS[(int) value];
    }
    if (value >= 10) {
      return Long.toString(value);
    }
    return "00";
  }

  private long getElapsedMillis() {
    if (timerPaused) {
      return pausedElapsedMillis;
    }
    return baseElapsedMillis + Math.max(0L, System.currentTimeMillis() - startTimerMillis);
  }

  private void pauseTimer() {
    if (!timerPaused) {
      pausedElapsedMillis = getElapsedMillis();
      timerPaused = true;
    }
  }

  private void resumeTimer() {
    if (timerPaused) {
      baseElapsedMillis = pausedElapsedMillis;
      startTimerMillis = System.currentTimeMillis();
      timerPaused = false;
    }
  }

  private void quitterNiveauVersMenu() {
    sauvegarderEtatNiveauCourant();
    pauseTimer();
    EtatJeu.setEtatActuel(EtatJeu.SELECTION);
  }

  private void verifierVictoire() {
    if (grille == null || victoireAnnoncee || !grille.estComplete()) {
      return;
    }
    if(grille.isSolutionAffichee()) return;

    pauseTimer();
    long tempsFinal = getElapsedMillis();
    grille.setTempsEcoule(tempsFinal);
    grille.saveGrille();

    SaveManager.enregistrerMeilleurTemps(grille.getNomJoueur(), grille.getIdNiveau(), tempsFinal);

    showAideOverlay(labelVictoireTitre, labelVictoireTexte + " " + formatTimer());
    victoireAnnoncee = true;
  }

  private void ouvrirParametresDepuisJeu() {
    sauvegarderEtatNiveauCourant();
    pauseTimer();
    Parametres.setEtatSource(EtatJeu.GRILLE);
    EtatJeu.setEtatActuel(EtatJeu.PARAMETRES);
  }

  public void sauvegarderEtatNiveauCourant() {
    if (grille != null) {
      grille.setTempsEcoule(getElapsedMillis());
      grille.saveGrille();
    }
  }
}
