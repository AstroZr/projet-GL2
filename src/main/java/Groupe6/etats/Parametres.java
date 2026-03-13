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
    private int boutonModeX, boutonModeY, boutonModeWidth, boutonModeHeight;
    
    // Valeurs des paramètres
    private float volumeEffets = 0.5f;
    private float volumeMusique = 0.5f;
    private List<String> langues = Arrays.asList("Français", "English");
    private int langueSelectionnee = 0;
    private boolean dropdownOuvert = false;
    private boolean modeSombre = false;
    
    // État de drag des sliders
    private boolean draggingEffets = false;
    private boolean draggingMusique = false;
    
    // Hover états
    private boolean hoverDropdown = false;
    private boolean hoverModeSombre = false;
    private boolean hoverModeClaire = false;

    public Parametres(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();

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
        
        // Boutons mode
        boutonModeX = panelX + panelWidth / 2 + layoutScale.scaleX(50);
        boutonModeY = panelY + layoutScale.scaleY(150);
        boutonModeWidth = layoutScale.scaleX(200);
        boutonModeHeight = layoutScale.scaleY(50);
        
        // Boutons de validation, de réinitialisation ou de retour 
        boutons.clear();
        int by = panelY + panelHeight + layoutScale.scaleY(30);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);
        int gap = layoutScale.scaleX(20);
        
        int totalWidth = bw * 3 + gap * 2;
        int startX = cx - totalWidth / 2;
        
        boutons.add(new BoutonChangeurEtat(startX, by, bw, bh, EtatJeu.MENU, "Retour"));
        boutons.add(new BoutonChangeurEtat(startX + bw + gap, by, bw, bh, EtatJeu.PARAMETRES, "Défaut"));
        boutons.add(new BoutonChangeurEtat(startX + 2 * (bw + gap), by, bw, bh, EtatJeu.PARAMETRES, "Appliquer"));
    }

    /** Met à jour le fond animé (nuages). */
    @Override
    public void update() {
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
        String titre = "Paramètres";
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
        dessinerSlider(g, sliderEffetsX, sliderEffetsY, sliderWidth, "Effets sonores", "🔊", volumeEffets);
        dessinerSlider(g, sliderMusiqueX, sliderMusiqueY, sliderWidth, "Musique", "🎵", volumeMusique);
        dessinerDropdown(g);
        dessinerBoutonsModes(g);

        // Dessiner les boutons du bas
        for (Bouton b : boutons) {
            b.draw(g);
        }
    }
    
    private void dessinerSlider(Graphics g, int x, int y, int largeur, String label, String icon, float valeur) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Label et icône
        g.setColor(Color.BLACK);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, layoutScale.scaleUniform(20)));
        g.drawString(icon, x, y + layoutScale.scaleY(20));
        
        g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14)));
        g.drawString(label, x + layoutScale.scaleX(40), y + layoutScale.scaleY(18));
        
        // Piste du slider
        int sliderY = y + layoutScale.scaleY(30);
        int sliderX = x + layoutScale.scaleX(40);
        int sliderH = layoutScale.scaleY(8);
        
        // Fond de la piste
        g2d.setColor(new Color(200, 200, 200));
        RoundRectangle2D piste = new RoundRectangle2D.Float(sliderX, sliderY, largeur - layoutScale.scaleX(40), sliderH, sliderH, sliderH);
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
        g.drawString("Langue", dropdownX + layoutScale.scaleX(40), dropdownY + layoutScale.scaleY(18));
        
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
        g.drawString(langues.get(langueSelectionnee), dropX + layoutScale.scaleX(10), dropY + layoutScale.scaleY(22));
        
        // Flèche
        int arrowX = dropX + dropW - layoutScale.scaleX(20);
        int arrowY = dropY + dropH / 2;
        dessinerFleche(g2d, arrowX, arrowY, dropdownOuvert);
        
        // Options si ouvert
        if (dropdownOuvert) {
            int optionY = dropY + dropH;
            for (int i = 0; i < langues.size(); i++) {
                if (i != langueSelectionnee) {
                    g2d.setColor(new Color(240, 240, 240, 200));
                    RoundRectangle2D optionBox = new RoundRectangle2D.Float(dropX, optionY + layoutScale.scaleY(2), dropW, dropH, 10, 10);
                    g2d.fill(optionBox);
                    g2d.setColor(Color.GRAY);
                    g2d.draw(optionBox);
                    
                    g.setColor(Color.BLACK);
                    g.drawString(langues.get(i), dropX + layoutScale.scaleX(10), optionY + layoutScale.scaleY(24));
                    optionY += dropH + layoutScale.scaleY(2);
                }
            }
        }
    }
    
    private void dessinerFleche(Graphics2D g2d, int x, int y, boolean up) {
        int size = layoutScale.scaleUniform(5);
        int[] xPoints = {x - size, x + size, x};
        int[] yPoints;
        if (up) {
            yPoints = new int[]{y + size / 2, y + size / 2, y - size / 2};
        } else {
            yPoints = new int[]{y - size / 2, y - size / 2, y + size / 2};
        }
        g2d.setColor(Color.BLACK);
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    private void dessinerBoutonsModes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Bouton Mode Sombre
        g2d.setColor(hoverModeSombre ? new Color(100, 100, 100, 180) : new Color(80, 80, 80, 180));
        RoundRectangle2D btnSombre = new RoundRectangle2D.Float(boutonModeX, boutonModeY, boutonModeWidth, boutonModeHeight, 10, 10);
        g2d.fill(btnSombre);
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(btnSombre);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, layoutScale.scaleUniform(14)));
        String texteSombre = "Mode Sombre";
        int tsLargeur = g.getFontMetrics().stringWidth(texteSombre);
        g.drawString(texteSombre, boutonModeX + (boutonModeWidth - tsLargeur) / 2, boutonModeY + boutonModeHeight / 2 + layoutScale.scaleY(5));
        
        // Bouton Mode Claire
        int btnClaireY = boutonModeY + boutonModeHeight + layoutScale.scaleY(15);
        g2d.setColor(hoverModeClaire ? new Color(250, 250, 250, 200) : new Color(230, 230, 230, 180));
        RoundRectangle2D btnClaire = new RoundRectangle2D.Float(boutonModeX, btnClaireY, boutonModeWidth, boutonModeHeight, 10, 10);
        g2d.fill(btnClaire);
        g2d.setColor(Color.GRAY);
        g2d.draw(btnClaire);
        
        g.setColor(Color.BLACK);
        String texteClaire = "Mode Claire";
        int tcLargeur = g.getFontMetrics().stringWidth(texteClaire);
        g.drawString(texteClaire, boutonModeX + (boutonModeWidth - tcLargeur) / 2, btnClaireY + boutonModeHeight / 2 + layoutScale.scaleY(5));
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
        
        // Vérifier hover boutons modes
        hoverModeSombre = mx >= boutonModeX && mx <= boutonModeX + boutonModeWidth && 
                         my >= boutonModeY && my <= boutonModeY + boutonModeHeight;
        
        int btnClaireY = boutonModeY + boutonModeHeight + layoutScale.scaleY(15);
        hoverModeClaire = mx >= boutonModeX && mx <= boutonModeX + boutonModeWidth && 
                         my >= btnClaireY && my <= btnClaireY + boutonModeHeight;
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

    /** Marque les boutons comme enfoncés et gère les clics sur les contrôles. */
    @Override
    public void mousePressed(MouseEvent e) {
        for (Bouton b : boutons) {
            if (isIn(e, b)) {
                b.setSourisEnfonce(true);
            }
        }
        
        int mx = e.getX();
        int my = e.getY();
        
        // Vérifier clic sur slider effets
        int sliderY = sliderEffetsY + layoutScale.scaleY(20);
        int sliderX = sliderEffetsX + layoutScale.scaleX(40);
        int sliderW = sliderWidth - layoutScale.scaleX(40);
        if (mx >= sliderX && mx <= sliderX + sliderW && my >= sliderY && my <= sliderY + layoutScale.scaleY(30)) {
            draggingEffets = true;
            float newVal = (float) (mx - sliderX) / sliderW;
            volumeEffets = Math.max(0, Math.min(1, newVal));
        }
        
        // Vérifier clic sur slider musique
        sliderY = sliderMusiqueY + layoutScale.scaleY(20);
        sliderX = sliderMusiqueX + layoutScale.scaleX(40);
        if (mx >= sliderX && mx <= sliderX + sliderW && my >= sliderY && my <= sliderY + layoutScale.scaleY(30)) {
            draggingMusique = true;
            float newVal = (float) (mx - sliderX) / sliderW;
            volumeMusique = Math.max(0, Math.min(1, newVal));
        }
        
        // Vérifier clic sur dropdown
        int dropY = dropdownY + layoutScale.scaleY(28);
        int dropX = dropdownX + layoutScale.scaleX(40);
        int dropW = dropdownWidth - layoutScale.scaleX(40);
        int dropH = layoutScale.scaleY(35);
        if (mx >= dropX && mx <= dropX + dropW && my >= dropY && my <= dropY + dropH) {
            dropdownOuvert = !dropdownOuvert;
        } else if (dropdownOuvert) {
            // Vérifier clic sur option
            int optionY = dropY + dropH;
            for (int i = 0; i < langues.size(); i++) {
                if (i != langueSelectionnee) {
                    int optY = optionY + layoutScale.scaleY(2);
                    if (mx >= dropX && mx <= dropX + dropW && my >= optY && my <= optY + dropH) {
                        langueSelectionnee = i;
                        dropdownOuvert = false;
                        break;
                    }
                    optionY += dropH + layoutScale.scaleY(2);
                }
            }
        }
        
        // Vérifier clic sur boutons modes
        if (hoverModeSombre) {
            modeSombre = true;
        }
        
        if (hoverModeClaire) {
            modeSombre = false;
        }
    }

    /** Déclenche l'action des boutons si le clic est valide. */
    @Override
    public void mouseReleased(MouseEvent e) {
        for (Bouton b : boutons) {
            if (b.isSourisEnfonce() && isIn(e, b)) {
                // Gérer le bouton Défaut
                if (boutons.indexOf(b) == 1) {
                    // Réinitialiser aux valeurs par défaut
                    volumeEffets = 0.5f;
                    volumeMusique = 0.5f;
                    langueSelectionnee = 0;
                    modeSombre = false;
                } else {
                    b.appliquerAction();
                }
            }
            b.setSourisEnfonce(false);
        }
        
        // Arrêter le drag des sliders
        draggingEffets = false;
        draggingMusique = false;
    }

    @Override
    public void updateTexts() {
        // Non implémenté
    }
}
