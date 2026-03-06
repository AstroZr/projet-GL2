package Groupe6.etats;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import Groupe6.game.Game;
import Groupe6.ui.BoutonCreation;
import Groupe6.ui.TextInput;
import Groupe6.utilz.Constants;
import Groupe6.utilz.HelpMethods;
import Groupe6.models.SaveData;
import Groupe6.utilz.SaveManager;

public class Creation extends Etats implements MethodesEtats {

    private static final int LOGO_DEFAULT_SIZE = 320;
    private static final int LARGEUR_BOUTON = 400;
    private static final int HAUTEUR_BOUTON = 55;
    private static final int LARGEUR_CHAMP = 400;
    private static final int HAUTEUR_CHAMP = 36;
    private static final int MAX_PSEUDO = 20;

    private BufferedImage logo;
    private BufferedImage nameAppImage;
    private int logoX, logoY, logoSize;
    private int nameAppX, nameAppY, nameAppWidth, nameAppHeight;
    private int titleY;
    private int titleFontSize;
    private int centerX;

    private TextInput textInput;
    private FondDegrade fond;
    private BoutonCreation bouton;

    public Creation(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        fond = FondDegrade.getInstance();
        logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
        nameAppImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "NameApp.png");

        textInput = new TextInput(0, 0, LARGEUR_CHAMP, HAUTEUR_CHAMP, "Identifiant", MAX_PSEUDO);
        bouton = new BoutonCreation(0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON);
    }

    @Override
    public void update() {
        fond.update();
        textInput.update();
    }

    @Override
    public void updateLayout(int gameWidth, int gameHeight) {
        applyLayout(gameWidth, gameHeight);
    }

    @Override
    protected void applyLayout(int w, int h) {
        float scaleX = (float) w / Constants.REF_WIDTH;
        float scaleY = (float) h / Constants.REF_HEIGHT;
        float scale = Math.min(scaleX, scaleY);
        centerX = (int) (w * Constants.Ratios.RATIO_CENTER_X);
        int cx = centerX;

        logoSize = (int) (LOGO_DEFAULT_SIZE * scale);
        logoX = cx - logoSize / 2;
        logoY = (int) (h * Constants.Ratios.Creation.RATIO_LOGO_Y);

        int gapLogoName = (int) (Constants.Ratios.Creation.ESPACEMENT_LOGO_NAME_REF * scale);
        nameAppHeight = (int) (Constants.Ratios.Creation.NAME_APP_REF_HEIGHT * scale);
        nameAppHeight = Math.max(1, nameAppHeight);
        nameAppWidth = (nameAppImage != null && nameAppImage.getHeight() > 0)
                ? nameAppHeight * nameAppImage.getWidth() / nameAppImage.getHeight()
                : nameAppHeight;
        nameAppWidth = Math.max(1, nameAppWidth);
        nameAppX = cx - nameAppWidth / 2;
        nameAppY = logoY + logoSize + gapLogoName;

        int gapNameTitle = (int) (Constants.Ratios.Creation.ESPACEMENT_NAME_TITLE_REF * scale);
        titleFontSize = (int) (Constants.Ratios.Creation.TITLE_FONT_SIZE_REF * scale);
        titleY = nameAppY + nameAppHeight + gapNameTitle;

        int gapTitleChamp = (int) (Constants.Ratios.Creation.ESPACEMENT_TITLE_CHAMP_REF * scale);
        int gapChampBouton = (int) (Constants.Ratios.Creation.ESPACEMENT_CHAMP_BOUTON_REF * scale);

        int cw = (int) (LARGEUR_CHAMP * scaleX);
        int ch = (int) (HAUTEUR_CHAMP * scaleY);
        int bw = (int) (LARGEUR_BOUTON * scaleX);
        int bh = (int) (HAUTEUR_BOUTON * scaleY);

        int champY = titleY + titleFontSize + gapTitleChamp;
        int buttonY = champY + ch + gapChampBouton;

        textInput.setBounds(cx - cw / 2, champY, cw, ch);
        bouton.setX(cx - bw / 2);
        bouton.setY(buttonY);
        bouton.setLargeur(bw);
        bouton.setHauteur(bh);
    }

    private void ensureImagesLoaded() {
        boolean needLayout = false;
        if (logo == null) {
            logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
            if (logo != null) needLayout = true;
        }
        if (nameAppImage == null) {
            nameAppImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "NameApp.png");
            if (nameAppImage != null) needLayout = true;
        }
        if (needLayout) {
            lastLayoutWidth = -1;
        }
    }

    @Override
    public void draw(Graphics g) {
        ensureImagesLoaded();
        ensureLayoutUpToDate();
        if (lastLayoutWidth == -1) {
            ensureLayoutUpToDate();
        }
        fond.draw(g);

        if (logo != null) {
            g.drawImage(logo, logoX, logoY, logoSize, logoSize, null);
        }
        if (nameAppImage != null) {
            g.drawImage(nameAppImage, nameAppX, nameAppY, nameAppWidth, nameAppHeight, null);
        }
        g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, Math.max(12, titleFontSize)));
        g.setColor(java.awt.Color.BLACK);
        int tw = g.getFontMetrics().stringWidth("Création");
        g.drawString("Création", centerX - tw / 2, titleY + g.getFontMetrics().getAscent());

        textInput.draw(g);
        bouton.draw(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        textInput.handleKeyTyped(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        bouton.setSourisSurvol(isIn(e, bouton));
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (isIn(e, bouton)) {
            bouton.setSourisEnfonce(true);
        }
        if (textInput.contains(e.getX(), e.getY())) {
            textInput.setFocused(true);
            return;
        }
        textInput.setFocused(false);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (bouton.isSourisEnfonce() && isIn(e, bouton)) {
            
            // --- LA MAGIE DE LA SAUVEGARDE SE PASSE ICI ---
            String pseudo = getPseudoSaisi();
            if (!pseudo.isEmpty()) {
                // 1. On crée les données pour ce joueur
                SaveData nouvellePartie = new SaveData(pseudo);
                
                // 2. On sauvegarde en JSON
                SaveManager.getInstance().sauvegarderJeu(nouvellePartie);
                
                // 3. On prévient le jeu global que c'est ce joueur qui joue
                game.setCurrentSave(nouvellePartie);
            }
            // ----------------------------------------------
            
            bouton.appliquerAction();
        }
        bouton.setSourisEnfonce(false);
    }

    @Override
    public void updateTexts() {}

    public String getPseudoSaisi() {
        return textInput.getTextTrimmed();
    }
}
