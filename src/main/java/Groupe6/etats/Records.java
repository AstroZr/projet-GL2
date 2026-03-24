package Groupe6.etats;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Groupe6.game.Game;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * État « meilleurs temps » : records du joueur courant + classement global par niveau.
 */
public class Records extends Etats {

    private static final Font FONT_TITRE = new Font("Berlin Sans FB Demi", Font.BOLD,  28);
    private static final Font FONT_SOUS  = new Font("Berlin Sans FB Demi", Font.BOLD,  18);
    private static final Font FONT_TEXTE = new Font("Berlin Sans FB Demi", Font.PLAIN, 15);

    private static final int LARGEUR_BTN = 200;
    private static final int HAUTEUR_BTN = 44;
    private static final int SCROLL_SPEED = 20;

    private LayoutScale layoutScale;

    private int scrollLeft  = 0;
    private int scrollRight = 0;
    private int maxScrollLeft  = 0;
    private int maxScrollRight = 0;

    private String labelTitre;
    private String labelMesTemps;
    private String labelClassement;
    private String labelAucun;
    private String labelRetour;
    private String labelNiveau;

    public Records(Game game) {
        super(game);
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();
        updateTexts();
        initBoutons();
    }

    private void initBoutons() {
        boutons.clear();
        layoutScale.update(Constants.game_width, Constants.game_height);
        int cx = layoutScale.centerX();
        int by = layoutScale.ratioY(880f / 1080f);
        int bw = layoutScale.scaleX(LARGEUR_BTN);
        int bh = layoutScale.scaleY(HAUTEUR_BTN);
        boutons.add(new BoutonChangeurEtat(cx - bw / 2, by, bw, bh, EtatJeu.MENU, labelRetour));
    }

    @Override
    protected void applyLayout(int w, int h) {
        layoutScale.update(w, h);
        int cx = layoutScale.centerX();
        int by = layoutScale.ratioY(880f / 1080f);
        int bw = layoutScale.scaleX(LARGEUR_BTN);
        int bh = layoutScale.scaleY(HAUTEUR_BTN);
        if (!boutons.isEmpty()) {
            boutons.get(0).setX(cx - bw / 2);
            boutons.get(0).setY(by);
            boutons.get(0).setLargeur(bw);
            boutons.get(0).setHauteur(bh);
        }
    }

    @Override
    public void update() {
        getFond().update();
    }

    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        getFond().draw(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = Constants.game_width;

        // ── Titre ──
        g2d.setFont(FONT_TITRE);
        g2d.setColor(getFond().getCouleurTexte());
        FontMetrics fmT = g2d.getFontMetrics();
        int titreY = layoutScale.ratioY(100f / 1080f) + fmT.getAscent();
        g2d.drawString(labelTitre, (w - fmT.stringWidth(labelTitre)) / 2, titreY);

        List<String> niveaux = SaveManager.listerIdsNiveaux();
        String joueur = game != null ? game.getJoueurCourant() : null;

        int panelW = layoutScale.scaleX(420);
        int panelH = layoutScale.scaleY(580);
        int panelY = layoutScale.ratioY(140f / 1080f);
        int leftX  = layoutScale.ratioX(0.08f);
        int rightX = w - leftX - panelW;
        int rowH   = layoutScale.scaleY(36);

        int visibleH = panelH - 60;

        // ── Panel gauche : mes records ──
        dessinerPanel(g2d, leftX, panelY, panelW, panelH);
        g2d.setFont(FONT_SOUS);
        g2d.setColor(getFond().getCouleurTexte());
        g2d.drawString(labelMesTemps, leftX + 16, panelY + 30);

        Map<String, Long> mesTemps = joueur != null ? SaveManager.chargerMeilleursTemps(joueur) : new HashMap<>();

        int cLeftH = niveaux.size() * (rowH + 6);
        maxScrollLeft = Math.max(0, cLeftH - visibleH);
        scrollLeft    = Math.max(0, Math.min(scrollLeft, maxScrollLeft));

        // Sauvegarder le clip original
        java.awt.Shape clipOriginal = g2d.getClip();

        // Appliquer le clip au contenu du panneau gauche
        g2d.setClip(leftX, panelY + 50, panelW, visibleH);
        g2d.translate(0, -scrollLeft);

        int rowY = panelY + 60;
        for (String niv : niveaux) {
            String temps = mesTemps.containsKey(niv) ? formaterTemps(mesTemps.get(niv)) : "\u2014";
            dessinerLigne(g2d, leftX + 12, rowY, panelW - 24, rowH, niv, temps, false);
            rowY += rowH + 6;
        }
        if (niveaux.isEmpty()) {
            g2d.setFont(FONT_TEXTE);
            g2d.setColor(getFond().getCouleurTexte());
            g2d.drawString(labelAucun, leftX + 16, panelY + 70);
        }

        // Restaurer la translation et le clip
        g2d.translate(0, scrollLeft);
        g2d.setClip(clipOriginal);

        // Indicateur scroll bas (gradient) si contenu tronqué
        if (scrollLeft < maxScrollLeft) {
            dessinerIndicateurScroll(g2d, leftX, panelY + 50 + visibleH - 24, panelW, 24, false);
        }
        // Indicateur scroll haut
        if (scrollLeft > 0) {
            dessinerIndicateurScroll(g2d, leftX, panelY + 50, panelW, 24, true);
        }

        // ── Panel droit : classement global ──
        dessinerPanel(g2d, rightX, panelY, panelW, panelH);
        g2d.setFont(FONT_SOUS);
        g2d.setColor(getFond().getCouleurTexte());
        g2d.drawString(labelClassement, rightX + 16, panelY + 30);

        // Charger tous les classements une seule fois
        Map<String, List<Map.Entry<String, Long>>> classements = new HashMap<>();
        for (String niv : niveaux) {
            classements.put(niv, SaveManager.chargerClassementGlobal(niv));
        }

        int cRightH = 0;
        for (String niv : niveaux) {
            cRightH += 28; // titre du niveau
            List<Map.Entry<String, Long>> cl = classements.get(niv);
            int entries = cl.isEmpty() ? 1 : Math.min(cl.size(), 5);
            cRightH += entries * (rowH + 4) + 10;
        }
        maxScrollRight = Math.max(0, cRightH - visibleH);
        scrollRight    = Math.max(0, Math.min(scrollRight, maxScrollRight));

        g2d.setClip(rightX, panelY + 50, panelW, visibleH);
        g2d.translate(0, -scrollRight);

        int rRowY = panelY + 60;
        for (String niv : niveaux) {
            g2d.setFont(FONT_SOUS);
            g2d.setColor(getFond().getCouleurTexte());
            g2d.drawString(labelNiveau + " : " + niv, rightX + 12, rRowY + 16);
            rRowY += 28;

            List<Map.Entry<String, Long>> classement = classements.get(niv);
            g2d.setFont(FONT_TEXTE);
            if (classement.isEmpty()) {
                g2d.setColor(getFond().getCouleurTexte());
                g2d.drawString(labelAucun, rightX + 16, rRowY + 16);
                rRowY += rowH;
            } else {
                int rang = 1;
                for (Map.Entry<String, Long> entry : classement) {
                    boolean moi = entry.getKey().equals(joueur);
                    String rangStr = "#" + rang + "  " + entry.getKey();
                    String tempsStr = formaterTemps(entry.getValue());
                    dessinerLigne(g2d, rightX + 12, rRowY, panelW - 24, rowH, rangStr, tempsStr, moi);
                    rRowY += rowH + 4;
                    rang++;
                    if (rang > 5) break;
                }
            }
            rRowY += 10;
        }

        g2d.translate(0, scrollRight);
        g2d.setClip(clipOriginal);

        if (scrollRight < maxScrollRight) {
            dessinerIndicateurScroll(g2d, rightX, panelY + 50 + visibleH - 24, panelW, 24, false);
        }
        if (scrollRight > 0) {
            dessinerIndicateurScroll(g2d, rightX, panelY + 50, panelW, 24, true);
        }

        // ── Boutons ──
        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
    }

    private void dessinerPanel(Graphics2D g2d, int x, int y, int w, int h) {
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillRoundRect(x, y, w, h, 18, 18);
        g2d.setColor(getFond().getCouleurBordreBouton());
        g2d.drawRoundRect(x, y, w, h, 18, 18);
    }

    private void dessinerIndicateurScroll(Graphics2D g2d, int x, int y, int w, int h, boolean versHaut) {
        GradientPaint gp;
        Color transparent = new Color(0, 0, 0, 0);
        Color opaque      = new Color(0, 0, 0, 90);
        if (versHaut) {
            gp = new GradientPaint(x, y, opaque, x, y + h, transparent);
        } else {
            gp = new GradientPaint(x, y, transparent, x, y + h, opaque);
        }
        g2d.setPaint(gp);
        g2d.fillRect(x, y, w, h);
        g2d.setPaint(null);
    }

    private void dessinerLigne(Graphics2D g2d, int x, int y, int w, int h,
                                String gauche, String droite, boolean surbrillance) {
        if (surbrillance) {
            g2d.setColor(new Color(70, 130, 180, 80));
            g2d.fillRoundRect(x, y, w, h, 8, 8);
        }
        g2d.setFont(FONT_TEXTE);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(getFond().getCouleurTexte());
        g2d.drawString(gauche,  x + 8,                                     y + (h + fm.getAscent()) / 2 - 2);
        g2d.drawString(droite, x + w - fm.stringWidth(droite) - 8, y + (h + fm.getAscent()) / 2 - 2);
    }

    private String formaterTemps(long ms) {
        long s = ms / 1000;
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }

    @Override
    public void updateTexts() {
        labelTitre      = LangManager.get("records.titre");
        labelMesTemps   = LangManager.get("records.mestemps");
        labelClassement = LangManager.get("records.classement");
        labelAucun      = LangManager.get("records.aucun");
        labelRetour     = LangManager.get("common.retour");
        labelNiveau     = LangManager.get("records.niveau");

        if (boutons != null && !boutons.isEmpty()) {
            ((BoutonChangeurEtat) boutons.get(0)).setLabel(labelRetour);
        }
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int delta = e.getUnitsToScroll() * layoutScale.scaleY(SCROLL_SPEED);
        int cx = layoutScale.centerX();
        if (e.getX() < cx) {
            scrollLeft  = Math.max(0, Math.min(scrollLeft  + delta, maxScrollLeft));
        } else {
            scrollRight = Math.max(0, Math.min(scrollRight + delta, maxScrollRight));
        }
    }

    @Override
    public void onEnter() {
        scrollLeft  = 0;
        scrollRight = 0;
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
