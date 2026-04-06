package Groupe6.etats;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import Groupe6.aide.Aide;
import Groupe6.aide.AideManager;
import Groupe6.aide.AideVisuel;
import Groupe6.aide.EffetVisuel;
import Groupe6.aide.TypeEffect;
import Groupe6.aide.techniques.BlocageUnique;
import Groupe6.aide.techniques.CandidatUnique;
import Groupe6.aide.techniques.NakedN;
import Groupe6.aide.techniques.Reste;
import Groupe6.aide.techniques.Singleton;
import Groupe6.aide.techniques.UniqueCacheeColonne;
import Groupe6.aide.techniques.UniqueCacheeLigne;
import Groupe6.game.Game;
import Groupe6.models.Cellule;
import Groupe6.models.Grille;
import Groupe6.models.TypeOperation;
import Groupe6.models.ZoneCalcul;
import Groupe6.save.Niveau;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.FontCache;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * Etat ASTUCES avec 2 vues:
 * - Liste des techniques (boutons lisibles)
 * - Detail d'une technique (mini-grille + description complete)
 */
public class Astuces extends Etats {

    private enum VueAstuces {
        LISTE,
        DETAIL
    }

    private static final int LARGEUR_BTN = 230;
    private static final int HAUTEUR_BTN = 46;

    private static final BasicStroke STROKE_UI = new BasicStroke(1.5f);
    private static final BasicStroke STROKE_MINI_FINE = new BasicStroke(0.8f);
    private static final BasicStroke STROKE_MINI_ZONE = new BasicStroke(2f);
    private static final Color COULEUR_MINI_ROUGE = new Color(220, 60, 60, 200);

    private static final String[] EXEMPLE_IDS = {
        "exemple_Singleton",
        "exemple_reste",
        "exemple_UniqueCacheeLigne",
        "exemple_UniqueCacheeColonne",
        "exemple_BlocageUnique",
        "exemple_CandidatUnique",
        "exemple_Naked2",
        "exemple_Naked3"
    };

    private static EtatJeu etatSource = EtatJeu.MENU;

    private final LayoutScale layoutScale;

    private List<Niveau> exemplesNiveaux;
    private List<AideVisuel> exemplesVisuels;

    private VueAstuces vue = VueAstuces.LISTE;
    private int indexAideSelectionnee = 0;
    private EtatJeu etatSourcePrecedent = null;

    private String titrePage;
    private String labelRetour;
    private String labelRetourListe;
    private String labelPrecedent;
    private String labelSuivant;
    private String titreListe;

    private Font fontTitre;
    private Font fontSousTitre;
    private Font fontDesc;

    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    private int detailCardX;
    private int detailCardY;
    private int detailCardW;
    private int detailCardH;

    public Astuces(Game game) {
        super(game);
        this.layoutScale = LayoutScale.getInstance();
        this.boutons = new ArrayList<>();
        chargerExemples();
        updateTexts();
        layoutScale.update(Constants.game_width, Constants.game_height);
        calculerPositions();
    }

    public static void setEtatSource(EtatJeu etat) {
        etatSource = etat;
    }

    private void chargerExemples() {
        exemplesNiveaux = new ArrayList<>();
        exemplesVisuels = new ArrayList<>();

        Aide[] freshTechniques = {
            new Singleton(),
            new Reste(),
            new UniqueCacheeLigne(),
            new UniqueCacheeColonne(),
            new BlocageUnique(),
            new CandidatUnique(),
            new NakedN(8, 2),
            new NakedN(9, 3)
        };

        for (int i = 0; i < EXEMPLE_IDS.length; i++) {
            Niveau n = SaveManager.chargerNiveau(EXEMPLE_IDS[i]);
            exemplesNiveaux.add(n);
            if (n == null) {
                exemplesVisuels.add(null);
                continue;
            }

            Grille g = new Grille(n);
            g.autoRemplissage();
            Aide tech = freshTechniques[i];
            AideVisuel visuel = null;

            if (tech.check(g)) {
                tech.setNbUtilisation(2);
                tech.load(g, freshTechniques.length);
                visuel = tech.getAideVisuel();
            }
            exemplesVisuels.add(visuel);
        }
    }

    private void calculerPositions() {
        int cx = layoutScale.ratioX(0.5f);
        int cy = layoutScale.ratioY(0.5f);

        fontTitre = FontCache.get("Berlin Sans FB Demi", Font.BOLD, layoutScale.scaleUniform(34));
        fontSousTitre = FontCache.get("Berlin Sans FB Demi", Font.BOLD, layoutScale.scaleUniform(22));
        fontDesc = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(16));

        panelWidth = Math.min(layoutScale.scaleX(1020), Constants.game_width - layoutScale.scaleX(50));
        panelHeight = Math.min(layoutScale.scaleY(610), Constants.game_height - layoutScale.scaleY(130));
        panelX = cx - panelWidth / 2;
        panelY = cy - panelHeight / 2 - layoutScale.scaleY(10);

        detailCardX = panelX + layoutScale.scaleX(22);
        detailCardY = panelY + layoutScale.scaleY(70);
        detailCardW = panelWidth - layoutScale.scaleX(44);
        detailCardH = panelHeight - layoutScale.scaleY(120);

        reconstruireBoutons();
    }

    private void reconstruireBoutons() {
        boutons.clear();
        clearFocusClavier();

        int cx = layoutScale.ratioX(0.5f);
        int bw = layoutScale.scaleX(LARGEUR_BTN);
        int bh = layoutScale.scaleY(HAUTEUR_BTN);

        if (vue == VueAstuces.LISTE) {
            construireBoutonsListe();
            int by = panelY + panelHeight + layoutScale.scaleY(14);
            boutons.add(new BoutonChangeurEtat(cx - bw / 2, by, bw, bh, etatSource, labelRetour));
            return;
        }

        int yBottom = panelY + panelHeight + layoutScale.scaleY(14);
        int gap = layoutScale.scaleX(12);
        int smallW = layoutScale.scaleX(170);

        boutons.add(new BoutonAction(cx - bw / 2, yBottom, bw, bh, labelRetourListe, this::retourListe));
        boutons.add(new BoutonAction(cx - bw / 2 - gap - smallW, yBottom, smallW, bh, labelPrecedent, this::selectionnerPrecedent));
        boutons.add(new BoutonAction(cx + bw / 2 + gap, yBottom, smallW, bh, labelSuivant, this::selectionnerSuivant));
    }

    private void construireBoutonsListe() {
        List<Aide> aides = AideManager.getInstance().getAides();
        int n = aides.size();
        if (n == 0) return;

        int padding = layoutScale.scaleY(18);
        int gapY = layoutScale.scaleY(10);
        int availableH = panelHeight - layoutScale.scaleY(120);

        int listBtnH = Math.max(layoutScale.scaleY(56),
            (availableH - (n - 1) * gapY - 2 * padding) / n);

        int x = panelX + layoutScale.scaleX(34);
        int y = panelY + layoutScale.scaleY(70);
        int w = panelWidth - layoutScale.scaleX(68);

        for (int i = 0; i < n; i++) {
            Aide aide = aides.get(i);
            String label = (i + 1) + ". " + aide.getTitre();
            final int index = i;
            boutons.add(new BoutonAction(x, y + i * (listBtnH + gapY), w, listBtnH, label, () -> ouvrirDetail(index)));
        }
    }

    private void ouvrirDetail(int index) {
        indexAideSelectionnee = index;
        vue = VueAstuces.DETAIL;
        calculerPositions();
    }

    private void retourListe() {
        vue = VueAstuces.LISTE;
        calculerPositions();
    }

    private void selectionnerSuivant() {
        List<Aide> aides = AideManager.getInstance().getAides();
        if (aides.isEmpty()) return;
        indexAideSelectionnee = (indexAideSelectionnee + 1) % aides.size();
    }

    private void selectionnerPrecedent() {
        List<Aide> aides = AideManager.getInstance().getAides();
        if (aides.isEmpty()) return;
        indexAideSelectionnee = (indexAideSelectionnee - 1 + aides.size()) % aides.size();
    }

    @Override
    public void update() {
        getFond().update();
        if (etatSourcePrecedent != etatSource) {
            calculerPositions();
            etatSourcePrecedent = etatSource;
        }
    }

    @Override
    protected void applyLayout(int w, int h) {
        calculerPositions();
    }

    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        getFond().draw(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setFont(fontTitre);
        g2d.setColor(getFond().getCouleurTexte());
        int tw = g2d.getFontMetrics().stringWidth(titrePage);
        g2d.drawString(titrePage, layoutScale.centerX() - tw / 2, panelY - layoutScale.scaleY(16));

        dessinerPanneau(g2d);

        if (vue == VueAstuces.LISTE) {
            dessinerListe(g2d);
        } else {
            dessinerDetail(g2d);
        }

        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
    }

    private void dessinerPanneau(Graphics2D g2d) {
        Color panelBase = getFond().getCouleurFondBouton();
        g2d.setColor(new Color(panelBase.getRed(), panelBase.getGreen(), panelBase.getBlue(), 218));
        g2d.fill(new RoundRectangle2D.Float(panelX, panelY, panelWidth, panelHeight, 20, 20));
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.setStroke(STROKE_UI);
        g2d.draw(new RoundRectangle2D.Float(panelX, panelY, panelWidth, panelHeight, 20, 20));
    }

    private void dessinerListe(Graphics2D g2d) {
        g2d.setFont(fontSousTitre);
        g2d.setColor(getFond().getCouleurTexte());
        int x = panelX + layoutScale.scaleX(30);
        int y = panelY + layoutScale.scaleY(45);
        g2d.drawString(titreListe, x, y);
    }

    private void dessinerDetail(Graphics2D g2d) {
        List<Aide> aides = AideManager.getInstance().getAides();
        if (aides.isEmpty()) return;

        int index = Math.max(0, Math.min(indexAideSelectionnee, aides.size() - 1));
        Aide aide = aides.get(index);

        g2d.setFont(fontSousTitre);
        g2d.setColor(getFond().getCouleurTexte());
        String titre = aide.getTitre();
        int tw = g2d.getFontMetrics().stringWidth(titre);
        g2d.drawString(titre, panelX + (panelWidth - tw) / 2, panelY + layoutScale.scaleY(44));

        g2d.setColor(getFond().getCouleurFondCellule());
        g2d.fill(new RoundRectangle2D.Float(detailCardX, detailCardY, detailCardW, detailCardH, 14, 14));
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.setStroke(STROKE_UI);
        g2d.draw(new RoundRectangle2D.Float(detailCardX, detailCardY, detailCardW, detailCardH, 14, 14));

        int miniSize = Math.min(layoutScale.scaleY(240), detailCardH - layoutScale.scaleY(42));
        int miniX = detailCardX + detailCardW - miniSize - layoutScale.scaleX(20);
        int miniY = detailCardY + (detailCardH - miniSize) / 2;
        Niveau niveau = index < exemplesNiveaux.size() ? exemplesNiveaux.get(index) : null;
        AideVisuel visuel = index < exemplesVisuels.size() ? exemplesVisuels.get(index) : null;
        dessinerMiniGrille(g2d, niveau, visuel, miniX, miniY, miniSize);

        int textX = detailCardX + layoutScale.scaleX(24);
        int textW = miniX - textX - layoutScale.scaleX(22);

        g2d.setFont(fontDesc);
        Color texte = getFond().getCouleurTexte();
        g2d.setColor(new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 210));

        int bodyY = detailCardY + layoutScale.scaleY(34);
        int lineHeight = g2d.getFontMetrics().getHeight() + layoutScale.scaleY(2);

        dessinerTexteWrap(g2d, aide.getDescription(), textX, bodyY, textW, lineHeight);

        FontMetrics fmLimit = g2d.getFontMetrics();
        int footerY = detailCardY + detailCardH - layoutScale.scaleY(22);
        String limite = aide.getMaxUtilisation() > 0
            ? (LangManager.get("astuces.limite") + " " + aide.getMaxUtilisation())
            : LangManager.get("astuces.illimite");

        g2d.setColor(new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 150));
        g2d.drawString(limite, textX, footerY + fmLimit.getAscent() / 2);
    }

    private void dessinerTexteWrap(Graphics2D g2d, String texte, int x, int y, int maxWidth, int lineHeight) {
        if (texte == null || texte.isEmpty() || maxWidth <= 0) return;
        FontMetrics fm = g2d.getFontMetrics();
        String[] mots = texte.trim().split("\\s+");
        StringBuilder ligne = new StringBuilder();
        int currentY = y;

        for (String mot : mots) {
            String test = ligne.length() == 0 ? mot : ligne + " " + mot;
            if (fm.stringWidth(test) <= maxWidth) {
                if (ligne.length() > 0) ligne.append(' ');
                ligne.append(mot);
            } else {
                if (ligne.length() > 0) {
                    g2d.drawString(ligne.toString(), x, currentY);
                    currentY += lineHeight;
                }
                ligne = new StringBuilder(mot);
            }
        }

        if (ligne.length() > 0) {
            g2d.drawString(ligne.toString(), x, currentY);
        }
    }

    private void dessinerMiniGrille(Graphics2D g2d, Niveau niveau, AideVisuel visuel, int gx, int gy, int gsize) {
        if (niveau == null) return;

        int n = niveau.getTaille();
        int cellSize = Math.max(4, gsize / n);
        int totalSize = cellSize * n;

        Cellule[][] cells = niveau.getMatriceCellules();
        int[][] preRemplie = niveau.getMatricePreRemplie();

        boolean[][] rouge = new boolean[n][n];
        if (visuel != null) {
            for (EffetVisuel effet : visuel) {
                if (effet.getType() == TypeEffect.CASE_NEGATIVE || effet.getType() == TypeEffect.CANDIDAT_POSITIF) {
                    int r = effet.getX();
                    int c = effet.getY();
                    if (r >= 0 && r < n && c >= 0 && c < n) {
                        rouge[r][c] = true;
                    }
                }
            }
        }

        g2d.setColor(getFond().getCouleurFondCellule());
        g2d.fillRect(gx, gy, totalSize, totalSize);

        int fontSize = Math.max(6, cellSize * 2 / 3);
        Font fontVal = FontCache.get("Arial", Font.BOLD, fontSize);
        g2d.setFont(fontVal);
        FontMetrics fmVal = g2d.getFontMetrics();

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int cx = gx + col * cellSize;
                int cy = gy + row * cellSize;

                if (rouge[row][col]) {
                    g2d.setColor(COULEUR_MINI_ROUGE);
                    g2d.fillRect(cx, cy, cellSize, cellSize);
                }

                g2d.setColor(getFond().getCouleurTexte());
                g2d.setStroke(STROKE_MINI_FINE);
                g2d.drawRect(cx, cy, cellSize, cellSize);

                if (preRemplie != null && preRemplie[row][col] != 0) {
                    String v = String.valueOf(preRemplie[row][col]);
                    int vw = fmVal.stringWidth(v);
                    g2d.setColor(rouge[row][col] ? Color.WHITE : getFond().getCouleurTexte());
                    g2d.drawString(v,
                        cx + (cellSize - vw) / 2,
                        cy + (cellSize + fmVal.getAscent() - fmVal.getDescent()) / 2);
                }
            }
        }

        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                ZoneCalcul zone = cells[row][col].getZoneCalcul();
                int cx = gx + col * cellSize;
                int cy = gy + row * cellSize;
                g2d.setStroke(STROKE_MINI_ZONE);
                g2d.setColor(getFond().getCouleurBordureZone());
                if (row == 0 || cells[row - 1][col].getZoneCalcul() != zone)
                    g2d.drawLine(cx, cy, cx + cellSize, cy);
                if (row == n - 1 || cells[row + 1][col].getZoneCalcul() != zone)
                    g2d.drawLine(cx, cy + cellSize, cx + cellSize, cy + cellSize);
                if (col == 0 || cells[row][col - 1].getZoneCalcul() != zone)
                    g2d.drawLine(cx, cy, cx, cy + cellSize);
                if (col == n - 1 || cells[row][col + 1].getZoneCalcul() != zone)
                    g2d.drawLine(cx + cellSize, cy, cx + cellSize, cy + cellSize);
            }
        }

        int labelSize = Math.max(5, cellSize / 3);
        Font fontLabel = FontCache.get("Arial", Font.BOLD, labelSize);
        g2d.setFont(fontLabel);
        FontMetrics fmLabel = g2d.getFontMetrics();
        Color etiquette = getFond().getCouleurEtiquetteZone();

        for (ZoneCalcul zone : niveau.getListeZones()) {
            Cellule premiere = zone.getListeCellules().get(0);
            int cx = gx + premiere.getColonne() * cellSize;
            int cy = gy + premiere.getLigne() * cellSize;
            TypeOperation op = zone.getTypeOperation();
            String label = (op == TypeOperation.AUCUNE)
                ? String.valueOf(zone.getValeurCible())
                : zone.getValeurCible() + op.getSymbole();
            g2d.setColor(etiquette);
            g2d.drawString(label, cx + 1, cy + fmLabel.getAscent() + 1);
        }
    }

    @Override
    public void updateTexts() {
        titrePage = LangManager.get("astuces.titre");
        titreListe = LangManager.get("astuces.liste.titre");
        labelRetour = LangManager.get("common.retour");
        labelRetourListe = LangManager.get("astuces.retour.liste");
        labelPrecedent = LangManager.get("common.precedent");
        labelSuivant = LangManager.get("common.suivant");

        for(Aide aide : AideManager.getInstance().getAides()){
            aide.refreshTexts();
        }

        if (boutons != null && !boutons.isEmpty()) {
            reconstruireBoutons();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_BACK_SPACE) {
            if (vue == VueAstuces.DETAIL) {
                retourListe();
            } else {
                EtatJeu.setEtatActuel(etatSource);
            }
            return;
        }

        if (vue == VueAstuces.DETAIL) {
            if (code == KeyEvent.VK_PAGE_DOWN || code == KeyEvent.VK_RIGHT) {
                selectionnerSuivant();
                return;
            }
            if (code == KeyEvent.VK_PAGE_UP || code == KeyEvent.VK_LEFT) {
                selectionnerPrecedent();
                return;
            }
        }

        gererNavigationClavier(e);
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        clearFocusClavier();
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
    }

    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        for (Bouton b : boutons) {
            if (isIn(e, b)) b.setSourisEnfonce(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (Bouton b : boutons) {
            if (b.isSourisEnfonce() && isIn(e, b)) {
                b.appliquerAction();
            }
            b.setSourisEnfonce(false);
        }
    }

    private class BoutonAction extends Bouton {
        private final Runnable action;
        private final String label;
        private final Font font;

        BoutonAction(int x, int y, int largeur, int hauteur, String label, Runnable action) {
            super(x, y, largeur, hauteur);
            this.action = action;
            this.label = label;
            this.font = FontCache.get("Berlin Sans FB Demi", Font.BOLD, layoutScale.scaleUniform(16));
        }

        @Override
        public void appliquerAction() {
            action.run();
        }

        @Override
        public void draw(Graphics g, Groupe6.fond.Fond fond) {
            super.draw(g, fond);
            g.setColor(fond.getCouleurTexte());
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            int tx = getX() + (getLargeur() - fm.stringWidth(label)) / 2;
            int ty = getY() + (getHauteur() + fm.getAscent()) / 2 - 2;
            g.drawString(label, tx, ty);
        }
    }
}
