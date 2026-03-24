package Groupe6.etats;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import Groupe6.fond.Fond;
import Groupe6.game.Game;
import Groupe6.save.SaveManager;
import Groupe6.ui.Bouton;
import Groupe6.ui.BoutonChangeurEtat;
import Groupe6.utilz.HelpMethods;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * État « connexion » : grille de cartes de profil.
 * Cliquer sur une carte charge le joueur et passe au menu principal.
 */
public class Connexion extends Etats {

    private static final int LARGEUR_BOUTON_RETOUR = 200;
    private static final int HAUTEUR_BOUTON_RETOUR = 44;
    private static final int CARTE_REF_SIZE = 160;
    private static final int CARTE_GAP_REF = 24;
    private static final int COLONNES = 3;

    /** Carte de profil joueur : avatar coloré + pseudo. */
    private class CarteJoueur extends Bouton {
        private final String pseudo;

        CarteJoueur(int x, int y, int size, String pseudo) {
            super(x, y, size, size);
            this.pseudo = pseudo;
        }

        @Override
        public void appliquerAction() {
            game.setJoueurCourant(pseudo);
            EtatJeu.setEtatActuel(EtatJeu.MENU);
        }

        @Override
        public void draw(Graphics g, Fond fond) {
            super.draw(g, fond); // fond arrondi avec hover/clic

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Cercle avatar (55 % de la largeur de la carte)
            int avatarSize = largeur * 55 / 100;
            int avatarX = x + (largeur - avatarSize) / 2;
            int avatarY = y + hauteur / 8;

            g2d.setColor(avatarColor(pseudo));
            g2d.fillOval(avatarX, avatarY, avatarSize, avatarSize);

            // Initiale
            int fontSize = Math.max(12, avatarSize * 45 / 100);
            g.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, fontSize));
            g.setColor(Color.WHITE);
            String initiale = pseudo.isEmpty() ? "?" : String.valueOf(Character.toUpperCase(pseudo.charAt(0)));
            int iw = g.getFontMetrics().stringWidth(initiale);
            int ih = g.getFontMetrics().getAscent();
            g.drawString(initiale,
                avatarX + (avatarSize - iw) / 2,
                avatarY + (avatarSize + ih) / 2 - 2);

            // Pseudo sous l'avatar (tronqué si trop long)
            int pseudoFontSize = Math.max(10, largeur * 14 / 100);
            g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, pseudoFontSize));
            g.setColor(fond.getCouleurTexte());
            String nom = pseudo;
            while (g.getFontMetrics().stringWidth(nom) > largeur - 10 && nom.length() > 1) {
                nom = nom.substring(0, nom.length() - 1);
            }
            if (!nom.equals(pseudo)) nom += "…";
            int pw = g.getFontMetrics().stringWidth(nom);
            int textAreaTop = avatarY + avatarSize;
            int textY = textAreaTop + (y + hauteur - textAreaTop + g.getFontMetrics().getAscent()) / 2 - 2;
            g.drawString(nom, x + (largeur - pw) / 2, textY);
        }

        /** Couleur déterministe basée sur le hash du pseudo. */
        private Color avatarColor(String name) {
            float hue = (Math.abs(name.hashCode()) % 360) / 360f;
            return Color.getHSBColor(hue, 0.55f, 0.72f);
        }
    }

    private BufferedImage nameAppImage;
    private int nameAppX, nameAppY, nameAppWidth, nameAppHeight;
    private int titleY, titleFontSize;
    private int grilleDepartY;
    private int centerX;

    private LayoutScale layoutScale;
    private List<String> joueurs = new ArrayList<>();
    private String labelTitre;
    private String labelRetour;
    private String labelAucunProfil;
    private boolean synchroniseDepuisEntree = false;

    public Connexion(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        boutons = new ArrayList<>();
        nameAppImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "NameApp.png");
        updateTexts();
    }

    private void rechargerJoueurs() {
        joueurs = SaveManager.listerJoueurs();
        lastLayoutWidth = -1; // force recalcul du layout
    }

    @Override
    public void update() {
        if (!synchroniseDepuisEntree) {
            rechargerJoueurs();
            synchroniseDepuisEntree = true;
        }
        getFond().update();
    }

    @Override
    protected void applyLayout(int w, int h) {
        layoutScale.update(w, h);
        centerX = layoutScale.centerX();

        // NameApp compact en haut
        nameAppHeight = layoutScale.scaleUniform(60);
        nameAppHeight = Math.max(1, nameAppHeight);
        nameAppWidth = (nameAppImage != null && nameAppImage.getHeight() > 0)
                ? nameAppHeight * nameAppImage.getWidth() / nameAppImage.getHeight()
                : nameAppHeight;
        nameAppWidth = Math.max(1, nameAppWidth);
        nameAppX = centerX - nameAppWidth / 2;
        nameAppY = layoutScale.scaleUniform(40);

        // Titre
        titleFontSize = layoutScale.scaleUniform(28);
        titleY = nameAppY + nameAppHeight + layoutScale.scaleUniform(20);

        // Grille de cartes
        grilleDepartY = titleY + titleFontSize + layoutScale.scaleUniform(30);

        int carteSize = layoutScale.scaleUniform(CARTE_REF_SIZE);
        int gap = layoutScale.scaleUniform(CARTE_GAP_REF);

        int cols = Math.min(COLONNES, Math.max(1, joueurs.size()));
        int rows = joueurs.isEmpty() ? 0 : (int) Math.ceil((double) joueurs.size() / cols);
        int totalWidth = cols * carteSize + (cols - 1) * gap;
        int startX = centerX - totalWidth / 2;

        boutons.clear();

        for (int i = 0; i < joueurs.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cx = startX + col * (carteSize + gap);
            int cy = grilleDepartY + row * (carteSize + gap);
            boutons.add(new CarteJoueur(cx, cy, carteSize, joueurs.get(i)));
        }

        // Bouton retour sous la grille
        int retourY = grilleDepartY + rows * (carteSize + gap) + layoutScale.scaleUniform(20);
        if (joueurs.isEmpty()) retourY = grilleDepartY + layoutScale.scaleUniform(50);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON_RETOUR);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON_RETOUR);
        boutons.add(new BoutonChangeurEtat(centerX - bw / 2, retourY, bw, bh, EtatJeu.START, labelRetour));
    }

    @Override
    public void draw(Graphics g) {
        ensureLayoutUpToDate();
        getFond().draw(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (nameAppImage != null) {
            g.drawImage(nameAppImage, nameAppX, nameAppY, nameAppWidth, nameAppHeight, null);
        }

        g.setFont(new Font("Berlin Sans FB Demi", Font.BOLD, Math.max(12, titleFontSize)));
        g.setColor(getFond().getCouleurTexte());
        int tw = g.getFontMetrics().stringWidth(labelTitre);
        g.drawString(labelTitre, centerX - tw / 2, titleY + g.getFontMetrics().getAscent());

        if (joueurs.isEmpty()) {
            g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, 18));
            g.setColor(Color.GRAY);
            int aw = g.getFontMetrics().stringWidth(labelAucunProfil);
            g.drawString(labelAucunProfil, centerX - aw / 2, grilleDepartY + layoutScale.scaleUniform(20));
        }

        for (Bouton b : boutons) {
            b.draw(g, getFond());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        for (Bouton b : boutons) {
            b.setSourisSurvol(isIn(e, b));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

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
        for (Bouton b : boutons) {
            if (b.isSourisEnfonce() && isIn(e, b)) {
                synchroniseDepuisEntree = false; // prêt pour la prochaine entrée
                b.appliquerAction();
            }
            b.setSourisEnfonce(false);
        }
    }

    @Override
    public void updateTexts() {
        labelTitre = LangManager.get("connexion.titre");
        labelRetour = LangManager.get("connexion.retour");
        labelAucunProfil = LangManager.get("connexion.aucun_profil");

        rechargerJoueurs();

        // Mettre à jour le label du bouton retour s'il existe déjà
        if (boutons != null && !boutons.isEmpty()) {
            Bouton dernier = boutons.get(boutons.size() - 1);
            if (dernier instanceof BoutonChangeurEtat) {
                ((BoutonChangeurEtat) dernier).setLabel(labelRetour);
            }
        }
    }
}
