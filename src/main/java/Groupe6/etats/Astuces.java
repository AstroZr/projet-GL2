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
 * État « ASTUCES » : liste toutes les techniques d'aide disponibles.
 *
 * Affiche une carte par technique avec son titre et sa description.
 * Accessible depuis le menu principal.
 */
public class Astuces extends Etats {

    private static final int LARGEUR_BTN = 200;
    private static final int HAUTEUR_BTN = 44;
    private static final BasicStroke STROKE_UI = new BasicStroke(1.5f);
    private static final BasicStroke STROKE_MINI_FINE = new BasicStroke(0.8f);
    private static final BasicStroke STROKE_MINI_ZONE = new BasicStroke(2f);

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

    private List<Niveau> exemplesNiveaux;
    private List<AideVisuel> exemplesVisuels;

    private LayoutScale layoutScale;

    private static EtatJeu etatSource = EtatJeu.MENU;
    private static Astuces instance = null;
    private EtatJeu etatSourcePrecedent = null;

    public static void setEtatSource(EtatJeu etat) {
        etatSource = etat;
    }

    private String titrePage;
    private String labelRetour;

    private Font fontTitre;
    private Font fontCardTitre;
    private Font fontCardDesc;
    private Font fontBadge;

    private int panelX, panelY, panelWidth, panelHeight;
    private int cardStartY;
    private int cardHeight;
    private int cardGap;
    private int cardX, cardWidth;
    private int badgeSize;

    public Astuces(Game game) {
        super(game);
        instance = this;
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();
        chargerExemples();
        updateTexts();
        layoutScale.update(Constants.game_width, Constants.game_height);
        calculerPositions();
    }

    private void chargerExemples() {
        exemplesNiveaux = new ArrayList<>();
        exemplesVisuels = new ArrayList<>();
        Aide[] freshTechniques = {
            new Singleton(), new Reste(), new UniqueCacheeLigne(),
            new UniqueCacheeColonne(), new BlocageUnique(), new CandidatUnique(),
            new Groupe6.aide.techniques.NakedN(8, 2),
            new Groupe6.aide.techniques.NakedN(9, 3)
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
            AideVisuel visuel = null;
            Aide tech = freshTechniques[i];
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

        fontTitre    = FontCache.get("Berlin Sans FB Demi", Font.BOLD,  layoutScale.scaleUniform(34));
        fontCardTitre = FontCache.get("Berlin Sans FB Demi", Font.BOLD,  layoutScale.scaleUniform(17));
        fontCardDesc  = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14));

        cardGap = layoutScale.scaleY(12);

        List<Aide> aides = AideManager.getInstance().getAides();
        int n = Math.max(aides.size(), 1);

        int paddingPanel = layoutScale.scaleY(20);

        // Hauteur préférée (cardHeight fixe de 110) réduite de 50px
        int preferredCardHeight = layoutScale.scaleY(110);
        int gapsTotal = (n - 1) * cardGap;
        int preferredPanelH = n * preferredCardHeight + gapsTotal + 2 * paddingPanel - layoutScale.scaleY(50);

        // Hauteur max disponible : laisser de la place pour le titre et le bouton
        int maxAvailable = Constants.game_height - layoutScale.scaleY(180);

        // On prend le minimum pour ne pas déborder
        panelHeight = Math.min(preferredPanelH, maxAvailable);

        // cardHeight calculé dynamiquement depuis la hauteur du panel et les espacements
        cardHeight = (panelHeight - 2 * paddingPanel - gapsTotal) / n;
        cardHeight = Math.max(cardHeight, layoutScale.scaleY(50));

        // badgeSize dynamique proportionnel à la hauteur de carte
        badgeSize = cardHeight - layoutScale.scaleY(20);
        badgeSize = Math.min(badgeSize, layoutScale.scaleUniform(36));
        badgeSize = Math.max(badgeSize, layoutScale.scaleUniform(16));

        fontBadge = FontCache.get("Berlin Sans FB Demi", Font.BOLD, Math.max(badgeSize / 2, layoutScale.scaleUniform(10)));

        panelWidth  = layoutScale.scaleX(760);
        panelX = cx - panelWidth / 2;
        panelY = cy - panelHeight / 2 - layoutScale.scaleY(30);

        cardX      = panelX + layoutScale.scaleX(20);
        cardWidth  = panelWidth - layoutScale.scaleX(40);
        cardStartY = panelY + paddingPanel;

        boutons.clear();
        int bw = layoutScale.scaleX(LARGEUR_BTN);
        int bh = layoutScale.scaleY(HAUTEUR_BTN);
        int by = panelY + panelHeight + layoutScale.scaleY(24);
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, by, bw, bh, etatSource, labelRetour));
    }

    @Override
    public void update() {
        getFond().update();
        
        // Si etatSource a changé depuis la dernière fois, recréer les boutons
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Titre de la page
        g2d.setFont(fontTitre);
        g2d.setColor(getFond().getCouleurTexte());
        int tw = g2d.getFontMetrics().stringWidth(titrePage);
        g2d.drawString(titrePage, layoutScale.centerX() - tw / 2, panelY - layoutScale.scaleY(16));

        // Panneau de fond
        Color panelBase = getFond().getCouleurFondBouton();
        g2d.setColor(new Color(panelBase.getRed(), panelBase.getGreen(), panelBase.getBlue(), 210));
        g2d.fill(new RoundRectangle2D.Float(panelX, panelY, panelWidth, panelHeight, 20, 20));
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.setStroke(STROKE_UI);
        g2d.draw(new RoundRectangle2D.Float(panelX, panelY, panelWidth, panelHeight, 20, 20));

        // Cartes des techniques
        List<Aide> aides = AideManager.getInstance().getAides();
        for (int i = 0; i < aides.size(); i++) {
            int cardY = cardStartY + i * (cardHeight + cardGap);
            dessinerCarteAide(g2d, aides.get(i), i, cardX, cardY, cardWidth, cardHeight);
        }

        // Bouton retour
        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
    }

    private void dessinerCarteAide(Graphics2D g2d, Aide aide, int index, int x, int y, int w, int h) {
        Color accent = getFond().getCouleurAccent();

        // Fond de la carte
        g2d.setColor(getFond().getCouleurFondCellule());
        g2d.fill(new RoundRectangle2D.Float(x, y, w, h, 12, 12));

        // Barre d'accent à gauche
        int barW = layoutScale.scaleX(4);
        g2d.setColor(accent);
        g2d.fillRoundRect(x, y + layoutScale.scaleY(12), barW, h - layoutScale.scaleY(24), barW, barW);

        // Bordure
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.setStroke(STROKE_UI);
        g2d.draw(new RoundRectangle2D.Float(x, y, w, h, 12, 12));

        // Mini-grille exemple (côté droit)
        int miniPadding = layoutScale.scaleY(8);
        int miniSize = h - 2 * miniPadding;
        int miniX = x + w - layoutScale.scaleX(12) - miniSize;
        int miniY = y + miniPadding;
        Niveau niveau = (index < exemplesNiveaux.size()) ? exemplesNiveaux.get(index) : null;
        AideVisuel visuel = (index < exemplesVisuels.size()) ? exemplesVisuels.get(index) : null;
        dessinerMiniGrille(g2d, niveau, visuel, miniX, miniY, miniSize);

        // Badge numéroté (cercle accent)
        int badgeX = x + layoutScale.scaleX(18);
        int badgeY = y + (h - badgeSize) / 2;
        g2d.setColor(accent);
        g2d.fillOval(badgeX, badgeY, badgeSize, badgeSize);
        g2d.setFont(fontBadge);
        g2d.setColor(Color.WHITE);
        String num = String.valueOf(index + 1);
        FontMetrics fmBadge = g2d.getFontMetrics();
        g2d.drawString(num,
            badgeX + (badgeSize - fmBadge.stringWidth(num)) / 2,
            badgeY + (badgeSize + fmBadge.getAscent() - fmBadge.getDescent()) / 2);

        // Zone texte entre badge et mini-grille
        int textX = badgeX + badgeSize + layoutScale.scaleX(16);
        int textW = miniX - textX - layoutScale.scaleX(12);

        // Titre de la technique
        g2d.setFont(fontCardTitre);
        g2d.setColor(getFond().getCouleurTexte());
        int titleY = y + layoutScale.scaleY(32);
        g2d.drawString(aide.getTitre(), textX, titleY);

        // Description avec retour à la ligne automatique
        g2d.setFont(fontCardDesc);
        Color texte = getFond().getCouleurTexte();
        g2d.setColor(new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 180));
        dessinerTexteEnveloppe(g2d, aide.getDescription(), textX, titleY + layoutScale.scaleY(20), textW, layoutScale.scaleY(18));

        // Badge de limite d'utilisation (coin bas-gauche de la zone texte)
        int padding = layoutScale.scaleX(12);
        g2d.setFont(fontCardDesc);
        FontMetrics fmLimit = g2d.getFontMetrics();
        if (aide.getMaxUtilisation() > 0) {
            String labelLimite = LangManager.get("astuces.limite") + " " + aide.getMaxUtilisation();
            int pillW = fmLimit.stringWidth(labelLimite) + layoutScale.scaleX(16);
            int pillH = fmLimit.getHeight() + layoutScale.scaleY(4);
            int pillX = textX;
            int pillY = y + h - padding - pillH;
            g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 51));
            g2d.fillRoundRect(pillX, pillY, pillW, pillH, 8, 8);
            g2d.setColor(accent);
            g2d.drawString(labelLimite,
                pillX + layoutScale.scaleX(8),
                pillY + fmLimit.getAscent() + layoutScale.scaleY(2));
        } else {
            String labelIllimite = LangManager.get("astuces.illimite");
            int textLimiteY = y + h - padding - fmLimit.getDescent();
            g2d.setColor(new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 120));
            g2d.drawString(labelIllimite, textX, textLimiteY);
        }
    }

    private static final Color COULEUR_MINI_ROUGE = new Color(220, 60, 60, 200);

    private void dessinerMiniGrille(Graphics2D g2d, Niveau niveau, AideVisuel visuel, int gx, int gy, int gsize) {
        if (niveau == null) return;
        int n = niveau.getTaille();
        int cellSize = Math.max(4, gsize / n);
        int totalSize = cellSize * n;

        Cellule[][] cells = niveau.getMatriceCellules();
        int[][] preRemplie = niveau.getMatricePreRemplie();

        // Construire la carte des cases à surligner en rouge
        boolean[][] rouge = new boolean[n][n];
        if (visuel != null) {
            for (EffetVisuel effet : visuel) {
                if (effet.getType() == TypeEffect.CASE_NEGATIVE || effet.getType() == TypeEffect.CANDIDAT_POSITIF) {
                    int r = effet.getX(), c = effet.getY();
                    if (r >= 0 && r < n && c >= 0 && c < n)
                        rouge[r][c] = true;
                }
            }
        }

        // Fond global de la mini-grille
        g2d.setColor(getFond().getCouleurFondCellule());
        g2d.fillRect(gx, gy, totalSize, totalSize);

        // Fond des cellules et valeurs pré-remplies
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

        // Bordures de zones (traits épais)
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

        // Étiquettes des zones (valeur + opération) dans la première cellule de chaque zone
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

    private void dessinerTexteEnveloppe(Graphics2D g2d, String texte, int x, int y, int maxWidth, int lineHeight) {
        if (texte == null || texte.isEmpty()) return;
        FontMetrics fm = g2d.getFontMetrics();
        String[] mots = texte.split(" ");
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

    @Override
    public void updateTexts() {
        titrePage   = LangManager.get("astuces.titre");
        labelRetour = LangManager.get("common.retour");

        if (boutons == null || boutons.isEmpty()) return;
        ((BoutonChangeurEtat) boutons.get(0)).setLabel(labelRetour);
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        gererNavigationClavier(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        clearFocusClavier();
        for (Bouton b : boutons) b.setSourisSurvol(isIn(e, b));
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
            if (b.isSourisEnfonce() && isIn(e, b)) b.appliquerAction();
            b.setSourisEnfonce(false);
        }
    }
}
