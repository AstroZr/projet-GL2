package Groupe6.view;

import Groupe6.fond.Fond;
import Groupe6.models.Cellule;
import Groupe6.models.Grille;
import Groupe6.models.TypeOperation;
import Groupe6.models.ZoneCalcul;
import Groupe6.utilz.FontCache;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Vue (Renderer) responsable du rendu graphique de la grille de jeu CalcuDoku.
 *
 * <p>
 * Responsabilités: - Dessiner la grille (cellules, valeurs, candidats) -
 * Afficher les bordures
 * des zones (couleurs différentes par zone) - Colorer les cellules selon l'état
 * (sélection, erreur,
 * validité) - Appliquer les thèmes de couleur (via Fond) - Gérer le
 * positionnement responsive et
 * taille des cellules
 *
 * <p>
 * Optimisations: - Fonts et Colors pré-alloués (pas de création per-frame) -
 * BasicStrokes
 * immuables réutilisés - Rendu en deux passes: fond des cellules → bordures →
 * texte
 *
 * <p>
 * Observer Pattern: - Enregistrée comme GrilleObserver sur Grille - Appelée à
 * chaque changement
 * (redessine l'affichage)
 */
public class VueGrille {

  // ====== COULEURS PRÉ-ALLOUÉES ======
  private static final Color COULEUR_SELECTION = new Color(100, 150, 255, 150); // Bleu semi-transparent pour cellule
                                                                                // sélectionnée
  private static final Color COULEUR_ERREUR = new Color(255, 100, 100, 180); // Rouge semi-transparent pour erreur
  private static final Color COULEUR_SOLUTION = new Color(120, 200, 120, 160);

  // ====== FONTS PRÉ-ALLOUÉES ======
  private static final Font FONT_VALEUR = FontCache.get("Arial", Font.BOLD, 32); // Chiffres dans les cellules
  private static final Font FONT_ZONE = FontCache.get("Arial", Font.BOLD, 14); // Label zone (target + opération)
  private static final Font FONT_CANDIDAT = FontCache.get("Arial", Font.BOLD, 16); // Candidats (petits chiffres)
  private static final Font FONT_AIDE_ZONE = FontCache.get("Arial", Font.PLAIN, 13); // Aide tooltip survol

  // ====== TRAITS/STROKES PRÉ-ALLOUÉS ======
  private static final BasicStroke STROKE_CONTOUR_GRILLE = new BasicStroke(4); // Bordure externe grille
  private static final BasicStroke STROKE_ZONE_SOUS_COUCHE = new BasicStroke(5); // Sous-couche zones
  private static final BasicStroke STROKE_EPAISSE = new BasicStroke(3); // Bordures épaisses
  private static final BasicStroke STROKE_FINE = new BasicStroke(1); // Lignes fines

  private static final int MAX_LIGNES_TOOLTIP = 8;

  // ====== DIMENSIONS DE RÉFÉRENCE ======
  private static final int MARGE_CASE = 2; // Marge entre cellules (pixels)
  private static final int TAILLE_GRILLE_DEFAULT = (int) (1080 / 1.5f); // Taille grille par défaut
  private static final int OFFSET_X_DEFAULT = (1920 - TAILLE_GRILLE_DEFAULT) / 2; // Centrage horizontal
  private static final int OFFSET_Y_DEFAULT = (1080 - TAILLE_GRILLE_DEFAULT) / 2; // Centrage vertical

  // ====== INSTANCE ======
  private final Grille grille; // Référence au modèle (lecture seule)

  // ====== LAYOUT COURANT ======
  private int tailleCellule; // Taille d'une cellule (pixels)
  private int tailleGrille; // Taille totale grille (N×N cellules)
  private int offsetX; // Position X de la grille sur l'écran
  private int offsetY; // Position Y de la grille sur l'écran
  private boolean modeCandidat; // Mode candidat actif (affiche petits chiffres)
  private int sourisX = -1;
  private int sourisY = -1;
  private Color contourGrilleCache;
  private Fond contourFondCache;

  public VueGrille(Grille grille) {
    this.grille = grille;
    tailleCellule = TAILLE_GRILLE_DEFAULT / grille.getTaille();
    offsetX = OFFSET_X_DEFAULT;
    offsetY = OFFSET_Y_DEFAULT;
    tailleGrille = TAILLE_GRILLE_DEFAULT;
    modeCandidat = false;
  }

  /**
   * Dessine la grille à l'écran en appliquant les couleurs du thème actif.
   */
  public void draw(Graphics g, Fond fond) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    dessinerGrille(g2d, fond);
    dessinerBorduresZones(g2d, fond);
    dessinerContourGrille(g2d, fond);
    dessinerZones(g2d, fond);

    Cellule hover = cellSousSouris();
    if (hover != null) {
      ZoneCalcul zone = hover.getZoneCalcul();
      if (zone != null) {
        drawTipZoneHaut(g2d, fond, zone);
      }
    }
  }

  private void dessinerContourGrille(Graphics2D g2d, Fond fond) {
    g2d.setStroke(STROKE_CONTOUR_GRILLE);
    if (contourGrilleCache == null || contourFondCache != fond) {
      Color texte = fond.getCouleurTexte();
      contourFondCache = fond;
      contourGrilleCache = new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 220);
    }
  }

  private void dessinerGrille(Graphics2D g2d, Fond fond) {
    int taille = grille.getTaille();

    g2d.setFont(FONT_VALEUR);
    FontMetrics fm = g2d.getFontMetrics();
    int fontAscent = fm.getAscent();

    for (int ligne = 0; ligne < taille; ligne++) {
      for (int col = 0; col < taille; col++) {
        int x = offsetX + col * tailleCellule;
        int y = offsetY + ligne * tailleCellule;
        int xCase = x + MARGE_CASE / 2;
        int yCase = y + MARGE_CASE / 2;
        int tailleCase = Math.max(1, tailleCellule - MARGE_CASE);
        Cellule cellule = grille.getCellule(ligne, col);

        dessinerFondCellule(g2d, cellule, xCase, yCase, tailleCase, fond);
        dessinerBordureCellule(g2d, cellule, xCase, yCase, tailleCase, fond);
        dessinerValeurCellule(g2d, cellule, xCase, yCase, tailleCase, fm, fontAscent, fond);
      }
    }
  }

  /**
   * Remplit le fond de la cellule selon son état (erreur, sélection, normal).
   */
  private void dessinerFondCellule(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase, Fond fond) {
    boolean erreurAideVisible = cellule.estErreurDuplique() && cellule.estVide();
    boolean erreurVisible = (grille.isAfficherErreurDouble() && cellule.estErreurDuplique()) || erreurAideVisible;
    if (grille.isSolutionAffichee()) {
      g2d.setColor(COULEUR_SOLUTION);
    } else if (erreurVisible || !cellule.estValide()) {
      g2d.setColor(COULEUR_ERREUR);
    } else if (cellule.estSelectionnee()) {
      g2d.setColor(COULEUR_SELECTION);
    } else {
      g2d.setColor(fond.getCouleurFondCellule());
    }
    g2d.fillRect(x, y, tailleCase, tailleCase);
  }

  /**
   * Dessine la bordure fine de la cellule.
   */
  private void dessinerBordureCellule(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase, Fond fond) {
    g2d.setColor(fond.getCouleurTexte());
    g2d.setStroke(STROKE_FINE);
    g2d.drawRect(x, y, tailleCase, tailleCase);
  }

  /**
   * Affiche la valeur numérique centrée dans la cellule, si présente.
   */
  private void dessinerValeurCellule(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase,
      FontMetrics fm, int fontAscent, Fond fond) {
    int valeur = cellule.getValeur();
    if (valeur == 0) {
      dessinerCandidats(g2d, cellule, x, y, tailleCase, fond);
      return;
    }

    g2d.setColor(fond.getCouleurTexte());
    g2d.setFont(FONT_VALEUR);
    String valeurStr = String.valueOf(valeur);
    int strWidth = fm.stringWidth(valeurStr);
    g2d.drawString(
        valeurStr,
        x + (tailleCase - strWidth) / 2,
        y + (tailleCase + fontAscent) / 2 - 5);
  }

  private void dessinerCandidats(Graphics2D g2d, Cellule cellule, int x, int y, int tailleCase, Fond fond) {
    List<Integer> candidats = cellule.getListeCandidat();
    if (candidats == null || candidats.isEmpty()) {
      return;
    }

    g2d.setColor(fond.getCouleurTexte());
    g2d.setFont(FONT_CANDIDAT);
    int nbMaxValeurs = grille.getTaille();
    int colonnes = (int) Math.ceil(Math.sqrt(nbMaxValeurs));
    int lignes = (int) Math.ceil((double) nbMaxValeurs / colonnes);
    int marge = Math.max(6, tailleCase / 12);
    int zoneLargeur = tailleCase - 2 * marge;
    int zoneHauteur = tailleCase - 2 * marge;
    int pasX = Math.max(14, zoneLargeur / Math.max(1, colonnes));
    int pasY = Math.max(14, zoneHauteur / Math.max(1, lignes));
    FontMetrics fm = g2d.getFontMetrics();

    for (Integer candidat : candidats) {
      if (candidat == null || candidat < 1 || candidat > nbMaxValeurs) {
        continue;
      }
      int index = candidat - 1;
      int col = index % colonnes;
      int row = index / colonnes;
      String texte = String.valueOf(candidat);
      int tx = x + marge + col * pasX + (pasX - fm.stringWidth(texte)) / 2;
      int ty = y + marge + row * pasY + fm.getAscent();
      g2d.drawString(texte, tx, ty);
    }
  }

  /**
   * Dessine les bordures épaisses délimitant chaque zone de calcul. Pour chaque
   * côté d'une cellule,
   * un trait épais est tracé si la cellule voisine appartient à une zone
   * différente (ou est hors
   * grille).
   */
  private void dessinerBorduresZones(Graphics2D g2d, Fond fond) {
    int taille = grille.getTaille();

    for (int ligne = 0; ligne < taille; ligne++) {
      for (int col = 0; col < taille; col++) {
        Cellule cellule = grille.getCellule(ligne, col);
        ZoneCalcul zone = cellule.getZoneCalcul();
        int x = offsetX + col * tailleCellule;
        int y = offsetY + ligne * tailleCellule;

        // Bord du haut
        if (ligne == 0 || grille.getCellule(ligne - 1, col).getZoneCalcul() != zone) {
          dessinerTraitZone(g2d, fond, x, y, x + tailleCellule, y);
        }
        // Bord du bas
        if (ligne == taille - 1 || grille.getCellule(ligne + 1, col).getZoneCalcul() != zone) {
          dessinerTraitZone(g2d, fond, x, y + tailleCellule, x + tailleCellule, y + tailleCellule);
        }
        // Bord gauche
        if (col == 0 || grille.getCellule(ligne, col - 1).getZoneCalcul() != zone) {
          dessinerTraitZone(g2d, fond, x, y, x, y + tailleCellule);
        }
        // Bord droit
        if (col == taille - 1 || grille.getCellule(ligne, col + 1).getZoneCalcul() != zone) {
          dessinerTraitZone(g2d, fond, x + tailleCellule, y, x + tailleCellule, y + tailleCellule);
        }
      }
    }
  }

  private void dessinerTraitZone(Graphics2D g2d, Fond fond, int x1, int y1, int x2, int y2) {
    g2d.setStroke(STROKE_ZONE_SOUS_COUCHE);
    g2d.setColor(fond.getCouleurTexte());
    g2d.drawLine(x1, y1, x2, y2);

    g2d.setStroke(STROKE_EPAISSE);
    g2d.setColor(fond.getCouleurBordureZone());
    g2d.drawLine(x1, y1, x2, y2);
  }

  private void dessinerZones(Graphics2D g2d, Fond fond) {
    g2d.setFont(FONT_ZONE);
    FontMetrics fm = g2d.getFontMetrics();
    Color accent = fond.getCouleurEtiquetteZone();
    for (ZoneCalcul zone : grille.getListeZones()) {
      Cellule premiere = zone.getListeCellules().get(0);
      int x = offsetX + premiere.getColonne() * tailleCellule;
      int y = offsetY + premiere.getLigne() * tailleCellule;
      String label = zone.getValeurCible() + zone.getTypeOperation().getSymbole();
      int tw = fm.stringWidth(label);
      // Badge de fond semi-transparent
      int bx = x + 4, by = y + 3;
      int bw = tw + 6, bh = fm.getHeight();
      g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
      g2d.fillRoundRect(bx, by, bw, bh, 4, 4);
      // Texte étiquette
      g2d.setColor(accent);
      g2d.drawString(label, bx + 3, by + fm.getAscent());
    }
  }

  private Cellule cellSousSouris() {
    if (sourisX < 0 || sourisY < 0) {
      return null;
    }

    int col = (sourisX - offsetX) / tailleCellule;
    int ligne = (sourisY - offsetY) / tailleCellule;

    int t = grille.getTaille();
    if (ligne < 0 || ligne >= t || col < 0 || col >= t) {
      return null;
    }

    return grille.getCellule(ligne, col);
  }

  private boolean cellAfficheChiffre(Cellule c) {
    if (c == null) {
      return false;
    }
    if (c.getValeur() != 0) {
      return true;
    }
    List<Integer> cand = c.getListeCandidat();
    return cand != null && !cand.isEmpty();
  }

  private void drawTipZoneHaut(Graphics2D g2d, Fond fond, ZoneCalcul zone) {
    // Ne pas afficher les possibilités d'équations si le paramètre est désactivé
    if (!grille.isAfficherPossibilites()) {
      return;
    }

    List<String> lignes = construireLignesPossibilites(zone);
    int nbAff = Math.min(MAX_LIGNES_TOOLTIP, lignes.size());

    g2d.setFont(FONT_AIDE_ZONE);
    FontMetrics fm = g2d.getFontMetrics();

    String titre = "Possibilites " + zone.getValeurCible() + zone.getTypeOperation().getSymbole();

    int w = fm.stringWidth(titre);
    for (int i = 0; i < nbAff; i++) {
      w = Math.max(w, fm.stringWidth(lignes.get(i)));
    }
    if (lignes.size() > MAX_LIGNES_TOOLTIP) {
      w = Math.max(w, fm.stringWidth("..."));
    }

    int pad = 8;
    int lineH = fm.getHeight();
    int nbL = 1 + nbAff + (lignes.size() > MAX_LIGNES_TOOLTIP ? 1 : 0);
    int h = nbL * lineH + pad * 2;
    int tipW = w + 2 * pad;

    int x = offsetX + (tailleGrille - tipW) / 2;
    x = Math.max(offsetX, Math.min(x, offsetX + tailleGrille - tipW));
    int y = offsetY - h - 8;
    y = Math.max(0, y);

    Color bg = fond.getCouleurFondCellule();
    Color bgT = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 240);

    g2d.setColor(bgT);
    g2d.fillRoundRect(x, y, tipW, h, 12, 12);

    g2d.setColor(fond.getCouleurBordureZone());
    g2d.setStroke(STROKE_FINE);
    g2d.drawRoundRect(x, y, tipW, h, 12, 12);

    g2d.setColor(fond.getCouleurTexte());
    int ty = y + pad + fm.getAscent();
    g2d.drawString(titre, x + pad, ty);

    for (int i = 0; i < nbAff; i++) {
      ty += lineH;
      g2d.drawString(lignes.get(i), x + pad, ty);
    }
    if (lignes.size() > MAX_LIGNES_TOOLTIP) {
      ty += lineH;
      g2d.drawString("...", x + pad, ty);
    }
  }

  private List<String> construireLignesPossibilites(ZoneCalcul zone) {
    List<Cellule> cells = zone.getListeCellules();
    List<Cellule> vides = new ArrayList<>();
    for (Cellule c : cells) {
      if (c.estVide()) {
        vides.add(c);
      }
    }

    List<String> out = new ArrayList<>();
    if (vides.isEmpty()) {
      List<Integer> vals = new ArrayList<>();
      for (Cellule c : cells) {
        vals.add(c.getValeur());
      }
      out.add(formaterEquation(vals, zone.getTypeOperation(), zone.getValeurCible()));
      return out;
    }

    List<List<Integer>> combs = zone.trouverCombinaisons(grille.getTaille());
    Set<String> uniques = new LinkedHashSet<>();

    for (List<Integer> comb : combs) {
      List<Integer> vals = new ArrayList<>();
      int idxVide = 0;
      for (Cellule c : cells) {
        if (c.estVide()) {
          vals.add(comb.get(idxVide++));
        } else {
          vals.add(c.getValeur());
        }
      }
      uniques.add(formaterEquation(vals, zone.getTypeOperation(), zone.getValeurCible()));
    }

    if (uniques.isEmpty()) {
      out.add("Aucune combinaison");
    } else {
      out.addAll(uniques);
    }

    return out;
  }

  private String formaterEquation(List<Integer> vals, TypeOperation op, int cible) {
    List<Integer> v = new ArrayList<>(vals);

    // Pour les opérations commutatives (+, *), normaliser l'ordre
    if (op == TypeOperation.ADDITION || op == TypeOperation.MULTIPLICATION) {
      v.sort(null); // Ordre croissant
    } else if (op == TypeOperation.SOUSTRACTION || op == TypeOperation.DIVISION) {
      v.sort(Collections.reverseOrder()); // Ordre décroissant
    }

    if (op == TypeOperation.AUCUNE) {
      return v.get(0) + " = " + cible;
    }

    String sep = " " + op.getSymbole() + " ";
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < v.size(); i++) {
      if (i > 0) {
        sb.append(sep);
      }
      sb.append(v.get(i));
    }
    sb.append(" = ").append(cible);
    return sb.toString();
  }

  /** Gère le clic de souris sur la grille. */
  public void mouseClicked(MouseEvent e) {
    int col = (e.getX() - offsetX) / tailleCellule;
    int ligne = (e.getY() - offsetY) / tailleCellule;

    int taille = grille.getTaille();
    if (ligne >= 0 && ligne < taille && col >= 0 && col < taille) {
      grille.selectionnerCellule(ligne, col);
    }
  }

  public void mouseMoved(MouseEvent e) {
    sourisX = e.getX();
    sourisY = e.getY();
  }

  /** Gère la saisie de caractères (chiffres). */
  public void keyTyped(KeyEvent e) {
    // keyTyped mit sous commentaire pour régler le problème du undo
    // à cause d'une redondance de l'appel saisirValeur(valeur) déjà fait dans
    // keyPressed
    // ce qui provoque un double enregistrement à chaque fois (d'o`u le double undo)

    /*
     * char c = e.getKeyChar();
     * if (Character.isDigit(c)) {
     * int valeur = Character.getNumericValue(c);
     * saisirValeur(valeur);
     * }
     */
  }

  /** Gère les touches pressées (suppression). */
  public void keyPressed(KeyEvent e) {
    int valeur = extraireValeurNumerique(e);
    if (valeur >= 1 && valeur <= 9) {
      saisirValeur(valeur);
      return;
    }

    if (isArrowKey(e.getKeyCode())) {
      gererDeplacementSelection(e.getKeyCode());
      return;
    }

    if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
      Cellule cellule = grille.getCelluleSelectionnee();
      if (cellule != null) {
        if (modeCandidat) {
          supprimerTousCandidats(cellule);
        } else {
          grille.supprimerChiffre(cellule.getLigne(), cellule.getColonne());
        }
      }
    }

    // Support du minus du pavé numérique pour supprimer
    if (e.getKeyCode() == KeyEvent.VK_SUBTRACT) {
      Cellule cellule = grille.getCelluleSelectionnee();
      if (cellule != null) {
        if (modeCandidat) {
          supprimerTousCandidats(cellule);
        } else {
          grille.supprimerChiffre(cellule.getLigne(), cellule.getColonne());
        }
      }
    }
  }

  private boolean isArrowKey(int code) {
    return code == KeyEvent.VK_UP
        || code == KeyEvent.VK_DOWN
        || code == KeyEvent.VK_LEFT
        || code == KeyEvent.VK_RIGHT;
  }

  private void gererDeplacementSelection(int code) {
    int dLigne = 0;
    int dColonne = 0;

    switch (code) {
      case KeyEvent.VK_UP:
        dLigne = -1;
        break;
      case KeyEvent.VK_DOWN:
        dLigne = 1;
        break;
      case KeyEvent.VK_LEFT:
        dColonne = -1;
        break;
      case KeyEvent.VK_RIGHT:
        dColonne = 1;
        break;
      default:
        return;
    }

    Cellule selection = grille.getCelluleSelectionnee();
    if (selection == null) {
        grille.selectionnerCellule(0, 0);
        return;
    }

    int taille = grille.getTaille();
    int ligne = Math.max(0, Math.min(taille - 1, selection.getLigne() + dLigne));
    int colonne = Math.max(0, Math.min(taille - 1, selection.getColonne() + dColonne));
    grille.selectionnerCellule(ligne, colonne);
  }

  private int extraireValeurNumerique(KeyEvent e) {
    int code = e.getKeyCode();

    if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
      return code - KeyEvent.VK_0;
    }

    if (code >= KeyEvent.VK_NUMPAD1 && code <= KeyEvent.VK_NUMPAD9) {
      return code - KeyEvent.VK_NUMPAD0;
    }

    if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD) {
      switch (code) {
        case KeyEvent.VK_END:
          return 1;
        case KeyEvent.VK_DOWN:
          return 2;
        case KeyEvent.VK_PAGE_DOWN:
          return 3;
        case KeyEvent.VK_LEFT:
          return 4;
        case KeyEvent.VK_CLEAR:
          return 5;
        case KeyEvent.VK_RIGHT:
          return 6;
        case KeyEvent.VK_HOME:
          return 7;
        case KeyEvent.VK_UP:
          return 8;
        case KeyEvent.VK_PAGE_UP:
          return 9;
        default:
          break;
      }
    }

    char c = e.getKeyChar();
    if (Character.isDigit(c)) {
      return Character.getNumericValue(c);
    }

    return -1;
  }

  private void supprimerTousCandidats(Cellule cellule) {
    List<Integer> candidats = new ArrayList<>(cellule.getListeCandidat());
    for (Integer candidat : candidats) {
      grille.supprimerCandidat(cellule.getLigne(), cellule.getColonne(), candidat);
    }
  }

  public void saisirValeur(int valeur) {
    if (valeur < 1 || valeur > grille.getTaille()) {
      return;
    }

    Cellule cellule = grille.getCelluleSelectionnee();
    if (cellule == null) {
      return;
    }

    if (modeCandidat) {
      if (cellule.getListeCandidat().contains(valeur)) {
        grille.supprimerCandidat(cellule.getLigne(), cellule.getColonne(), valeur);
      } else {
        grille.ajouterCandidat(cellule.getLigne(), cellule.getColonne(), valeur);
      }
    } else {
      grille.ajouterChiffre(cellule.getLigne(), cellule.getColonne(), valeur);
    }
  }

  public void toggleModeCandidat() {
    modeCandidat = !modeCandidat;
  }

  public boolean isModeCandidat() {
    return modeCandidat;
  }

  public void applyLayout(int w, int h) {
    int n = grille.getTaille();
    // Calculer tailleCellule en premier pour éviter le désalignement
    // dû à la troncature entière : tailleGrille/n puis n*tailleCellule <
    // tailleGrille
    tailleCellule = Math.max(1, (int) (h / 1.5f) / n);
    tailleGrille = tailleCellule * n; // multiple exact : contour = cellules réelles
    offsetX = (w - tailleGrille) / 2;
    offsetY = (h - tailleGrille) / 2;
  }
}
