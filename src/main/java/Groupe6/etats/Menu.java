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
import java.util.ArrayList;
import java.util.List;

import Groupe6.game.Game;
import Groupe6.models.Cellule;
import Groupe6.models.Grille;
import Groupe6.models.ZoneCalcul;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.Constants;
import Groupe6.utilz.FontCache;
import Groupe6.utilz.HelpMethods;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;
import java.awt.image.BufferedImage;

/**
 * État « MENU PRINCIPAL » : écran d'accueil avec boutons à gauche et preview
 * du tutoriel à droite.
 */
public class Menu extends Etats {

    // ====== DIMENSIONS DE RÉFÉRENCE ======
    private static final int HAUTEUR_BOUTON       = 55;
    private static final int ESPACEMENT_BOUTONS_REF = 64;

    // ====== FONTS ======
    private static final Font FONT_TITRE_PREVIEW =
            FontCache.get("Berlin Sans FB Demi", Font.BOLD, 28);
    private static final Font FONT_LANCER =
            FontCache.get("Berlin Sans FB Demi", Font.BOLD, 20);
    private static final Font FONT_ZONE_LABEL =
            FontCache.get("Berlin Sans FB Demi", Font.BOLD, 11);

    // ====== STROKES ======
    private static final BasicStroke STROKE_ZONE_BORDER = new BasicStroke(3f);
    private static final BasicStroke STROKE_INNER       = new BasicStroke(1f);
    private static final BasicStroke STROKE_OUTER       = new BasicStroke(4f);

    // ====== SCALING ======
    private LayoutScale layoutScale;

    // ====== TEXTES LOCALISÉS ======
    private String labelJouer;
    private String labelParametres;
    private String labelRecords;
    private String labelAstuces;
    private String labelQuitter;
    private String labelTitreTutoriel;
    private String labelLancerTutoriel;

    // ====== LOGO ======
    private BufferedImage logoImage;
    private int logoX, logoY, logoW, logoH;

    // ====== PREVIEW TUTORIEL ======
    private Grille grillePreview;
    private boolean previewHovered = false;
    private boolean previewPressed = false;
    private int previewX, previewY, previewW, previewH;

    // ====== CONSTRUCTEUR ======

    public Menu(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();
        updateTexts();

        logoImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Name.png");

        layoutScale.update(Constants.game_width, Constants.game_height);
        int w = Constants.game_width;
        int h = Constants.game_height;

        // Créer les boutons à (0,0) — computeLayout les repositionne juste après
        int bh  = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);
        boutons.add(new BoutonChangeurEtat(0, 0, 0, bh, EtatJeu.SELECTION,  labelJouer));
        boutons.add(new BoutonChangeurEtat(0, 0, 0, bh, EtatJeu.PARAMETRES, labelParametres));
        boutons.add(new BoutonChangeurEtat(0, 0, 0, bh, EtatJeu.RECORDS,    labelRecords));
        boutons.add(new BoutonChangeurEtat(0, 0, 0, bh, EtatJeu.ASTUCES,    labelAstuces));
        boutons.add(new BoutonChangeurEtat(0, 0, 0, bh, EtatJeu.QUITTER,    labelQuitter));

        computeLayout(w, h);
        loadGrillePreview();
    }

    private void loadGrillePreview() {
        try {
            grillePreview = new Grille("Default", "Tutoriel", true);
        } catch (Exception ex) {
            grillePreview = null;
        }
    }

    // ====== LAYOUT ======

    /**
     * Calcule toutes les positions : logo, boutons (colonne gauche centrée
     * verticalement) et panneau preview tutoriel (droite).
     */
    private void computeLayout(int w, int h) {
        // ---- 1. Taille du logo (max 22% largeur, 32% hauteur) ----
        logoX = Math.max(30, (int)(w * 0.03));
        int maxLogoW = (int)(w * 0.22);
        int maxLogoH = (int)(h * 0.32);
        if (logoImage != null && logoImage.getHeight() > 0) {
            float aspect = (float) logoImage.getWidth() / logoImage.getHeight();
            logoW = maxLogoW;
            logoH = (int)(logoW / aspect);
            if (logoH > maxLogoH) {
                logoH = maxLogoH;
                logoW = (int)(logoH * aspect);
            }
        } else {
            logoW = maxLogoW;
            logoH = (int)(maxLogoW / 1.8f);
        }

        // ---- 2. Dimensions boutons ----
        int bh  = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleUniform(ESPACEMENT_BOUTONS_REF);
        int nbBtn = boutons != null ? boutons.size() : 5;
        int logoToBtn = Math.max(14, (int)(h * 0.03));

        // ---- 3. Centrage vertical de la colonne (logo + boutons) ----
        int totalColH = logoH + logoToBtn + (nbBtn - 1) * gap + bh;
        logoY = Math.max(20, (h - totalColH) / 2);
        int cy = logoY + logoH + logoToBtn;

        if (boutons != null) {
            for (int i = 0; i < boutons.size(); i++) {
                boutons.get(i).setX(logoX);
                boutons.get(i).setY(cy + i * gap);
                boutons.get(i).setLargeur(logoW);
                boutons.get(i).setHauteur(bh);
            }
        }

        // ---- 4. Preview tutoriel (côté droit, centré verticalement) ----
        int pBoxSize = Math.min((int)(w * 0.32), (int)(h * 0.60));
        previewW = pBoxSize;
        previewH = pBoxSize + 65;
        int pCenterX = (int)(w * 0.73);
        previewX = pCenterX - previewW / 2;
        previewY = Math.max(50, (h - previewH) / 2);
    }

    @Override
    public void update() {
        getFond().update();
    }

    @Override
    protected void applyLayout(int w, int h) {
        computeLayout(w, h);
    }

    // ====== DESSIN ======

    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        getFond().draw(g);
        if (logoImage != null) {
            g.drawImage(logoImage, logoX, logoY, logoW, logoH, null);
        }
        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
        drawPreviewTutoriel(g);
    }

    private void drawPreviewTutoriel(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int centerX = previewX + previewW / 2;

        // ---- Titre "Tutoriel" au-dessus de la boîte ----
        g2d.setFont(FONT_TITRE_PREVIEW);
        FontMetrics fmT = g2d.getFontMetrics();
        g2d.setColor(getFond().getCouleurTexte());
        g2d.drawString(labelTitreTutoriel,
                centerX - fmT.stringWidth(labelTitreTutoriel) / 2,
                previewY - 14);

        // ---- Fond de la boîte ----
        Color bg = previewPressed
                ? getFond().getCouleurFondBoutonClic()
                : previewHovered
                ? getFond().getCouleurFondBoutonSurvol()
                : getFond().getCouleurFondBouton();
        g2d.setColor(bg);
        g2d.fillRoundRect(previewX, previewY, previewW, previewH, 16, 16);

        // ---- Bordure accent ----
        Color accent = getFond().getCouleurAccent();
        g2d.setStroke(new BasicStroke(previewHovered ? 3f : 2f));
        g2d.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                previewHovered ? 255 : 180));
        g2d.drawRoundRect(previewX, previewY, previewW, previewH, 16, 16);
        g2d.setStroke(STROKE_INNER);

        // ---- Mini grille ----
        int pad = 16;
        int gridSize = previewW - pad * 2;
        drawMiniGrille(g2d, previewX + pad, previewY + pad, gridSize);

        // ---- "Lancer tutoriel" en bas ----
        g2d.setFont(FONT_LANCER);
        FontMetrics fmL = g2d.getFontMetrics();
        g2d.setColor(getFond().getCouleurTexte());
        g2d.drawString(labelLancerTutoriel,
                centerX - fmL.stringWidth(labelLancerTutoriel) / 2,
                previewY + previewH - 16);
    }

    private void drawMiniGrille(Graphics2D g2d, int startX, int startY, int gridSize) {
        if (grillePreview == null) return;
        int taille = grillePreview.getTaille();
        if (taille <= 0) return;
        int cellSize = gridSize / taille;

        // 1. Fonds des cellules
        for (int r = 0; r < taille; r++) {
            for (int c = 0; c < taille; c++) {
                g2d.setColor(getFond().getCouleurFondCellule());
                g2d.fillRect(startX + c * cellSize, startY + r * cellSize, cellSize, cellSize);
            }
        }

        // 2. Séparateurs (épais si changement de zone, fin sinon)
        Color borderColor = getFond().getCouleurBordureZone();
        for (int r = 0; r < taille; r++) {
            for (int c = 0; c < taille; c++) {
                int cx = startX + c * cellSize;
                int cy = startY + r * cellSize;
                ZoneCalcul zone = grillePreview.getCellule(r, c).getZoneCalcul();

                if (c < taille - 1) {
                    boolean sameZone = zone == grillePreview.getCellule(r, c + 1).getZoneCalcul();
                    g2d.setStroke(sameZone ? STROKE_INNER : STROKE_ZONE_BORDER);
                    g2d.setColor(borderColor);
                    g2d.drawLine(cx + cellSize, cy + 1, cx + cellSize, cy + cellSize - 1);
                }
                if (r < taille - 1) {
                    boolean sameZone = zone == grillePreview.getCellule(r + 1, c).getZoneCalcul();
                    g2d.setStroke(sameZone ? STROKE_INNER : STROKE_ZONE_BORDER);
                    g2d.setColor(borderColor);
                    g2d.drawLine(cx + 1, cy + cellSize, cx + cellSize - 1, cy + cellSize);
                }
            }
        }

        // 3. Bordure extérieure
        g2d.setStroke(STROKE_OUTER);
        g2d.setColor(borderColor);
        g2d.drawRect(startX, startY, gridSize, gridSize);
        g2d.setStroke(STROKE_INNER);

        // 4. Étiquettes de zone (valeur + symbole opération) dans le coin haut-gauche
        g2d.setFont(FONT_ZONE_LABEL);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(getFond().getCouleurEtiquetteZone());
        for (ZoneCalcul zone : grillePreview.getListeZones()) {
            List<Cellule> cells = zone.getListeCellules();
            if (cells.isEmpty()) continue;
            // Cellule la plus en haut à gauche
            Cellule first = cells.get(0);
            for (Cellule cell : cells) {
                if (cell.getLigne() < first.getLigne()
                        || (cell.getLigne() == first.getLigne()
                            && cell.getColonne() < first.getColonne())) {
                    first = cell;
                }
            }
            int lx = startX + first.getColonne() * cellSize + 3;
            int ly = startY + first.getLigne() * cellSize + fm.getAscent() + 1;
            String etiquette = zone.getValeurCible() + zone.getTypeOperation().getSymbole();
            g2d.drawString(etiquette, lx, ly);
        }
    }

    // ====== INPUTS ======

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if ((code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE)
                && indiceFocusClavierBouton == 1) {
            Parametres.setEtatSource(EtatJeu.MENU);
        }
        gererNavigationClavier(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        clearFocusClavier();
        for (Bouton b : boutons) b.setSourisSurvol(isIn(e, b));
        previewHovered = isInPreview(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        for (Bouton b : boutons) {
            if (isIn(e, b)) b.setSourisEnfonce(true);
        }
        if (isInPreview(e.getX(), e.getY())) previewPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        boolean handled = false;
        for (int i = 0; i < boutons.size(); i++) {
            Bouton b = boutons.get(i);
            if (b.isSourisEnfonce() && isIn(e, b)) {
                if (i == 1) Parametres.setEtatSource(EtatJeu.MENU);
                b.appliquerAction();
                handled = true;
            }
            b.setSourisEnfonce(false);
        }
        if (!handled && previewPressed && isInPreview(e.getX(), e.getY())) {
            EtatJeu.setEtatActuel(EtatJeu.TUTORIEL);
        }
        previewPressed = false;
    }

    private boolean isInPreview(int mx, int my) {
        return mx >= previewX && mx <= previewX + previewW
                && my >= previewY && my <= previewY + previewH;
    }

    // ====== TEXTES ======

    @Override
    public void updateTexts() {
        labelJouer           = LangManager.get("menu.jouer");
        labelParametres      = LangManager.get("menu.parametres");
        labelRecords         = LangManager.get("menu.records");
        labelAstuces         = LangManager.get("menu.astuces");
        labelQuitter         = LangManager.get("menu.quitter");
        labelTitreTutoriel   = LangManager.get("menu.tutoriel");
        labelLancerTutoriel  = LangManager.get("menu.lancer.tutoriel");

        if (boutons == null || boutons.size() < 5) return;

        ((BoutonChangeurEtat) boutons.get(0)).setLabel(labelJouer);
        ((BoutonChangeurEtat) boutons.get(1)).setLabel(labelParametres);
        ((BoutonChangeurEtat) boutons.get(2)).setLabel(labelRecords);
        ((BoutonChangeurEtat) boutons.get(3)).setLabel(labelAstuces);
        ((BoutonChangeurEtat) boutons.get(4)).setLabel(labelQuitter);
    }
}
