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
import Groupe6.game.Game;
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

    private LayoutScale layoutScale;

    private static EtatJeu etatSource = EtatJeu.MENU;

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
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();
        updateTexts();
        layoutScale.update(Constants.game_width, Constants.game_height);
        calculerPositions();
    }

    private void calculerPositions() {
        int cx = layoutScale.ratioX(0.5f);
        int cy = layoutScale.ratioY(0.5f);

        fontTitre    = FontCache.get("Berlin Sans FB Demi", Font.BOLD,  layoutScale.scaleUniform(34));
        fontCardTitre = FontCache.get("Berlin Sans FB Demi", Font.BOLD,  layoutScale.scaleUniform(17));
        fontCardDesc  = FontCache.get("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14));
        fontBadge     = FontCache.get("Berlin Sans FB Demi", Font.BOLD,  layoutScale.scaleUniform(16));

        badgeSize  = layoutScale.scaleUniform(36);
        cardHeight = layoutScale.scaleY(110);
        cardGap    = layoutScale.scaleY(12);

        List<Aide> aides = AideManager.getInstance().getAides();
        int n = aides.size();
        panelWidth  = layoutScale.scaleX(760);
        panelHeight = n * cardHeight + (n - 1) * cardGap + layoutScale.scaleY(40);
        panelX = cx - panelWidth / 2;
        panelY = cy - panelHeight / 2 - layoutScale.scaleY(30);

        cardX      = panelX + layoutScale.scaleX(20);
        cardWidth  = panelWidth - layoutScale.scaleX(40);
        cardStartY = panelY + layoutScale.scaleY(20);

        boutons.clear();
        int bw = layoutScale.scaleX(LARGEUR_BTN);
        int bh = layoutScale.scaleY(HAUTEUR_BTN);
        int by = panelY + panelHeight + layoutScale.scaleY(24);
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, by, bw, bh, etatSource, labelRetour));
    }

    @Override
    public void update() {
        getFond().update();
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

        // Zone texte à droite du badge
        int textX = badgeX + badgeSize + layoutScale.scaleX(16);
        int textW = w - (textX - x) - layoutScale.scaleX(16);

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

        // Badge de limite d'utilisation (coin bas-droite)
        int padding = layoutScale.scaleX(12);
        g2d.setFont(fontCardDesc);
        FontMetrics fmLimit = g2d.getFontMetrics();
        if (aide.getMaxUtilisation() > 0) {
            String labelLimite = LangManager.get("astuces.limite") + " " + aide.getMaxUtilisation();
            int pillW = fmLimit.stringWidth(labelLimite) + layoutScale.scaleX(16);
            int pillH = fmLimit.getHeight() + layoutScale.scaleY(4);
            int pillX = x + w - padding - pillW;
            int pillY = y + h - padding - pillH;
            // Fond du pill (20% de la couleur accent)
            g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 51));
            g2d.fillRoundRect(pillX, pillY, pillW, pillH, 8, 8);
            // Texte du pill
            g2d.setColor(accent);
            g2d.drawString(labelLimite,
                pillX + layoutScale.scaleX(8),
                pillY + fmLimit.getAscent() + layoutScale.scaleY(2));
        } else {
            String labelIllimite = LangManager.get("astuces.illimite");
            int textLimiteX = x + w - padding - fmLimit.stringWidth(labelIllimite);
            int textLimiteY = y + h - padding - fmLimit.getDescent();
            g2d.setColor(new Color(texte.getRed(), texte.getGreen(), texte.getBlue(), 120));
            g2d.drawString(labelIllimite, textLimiteX, textLimiteY);
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
