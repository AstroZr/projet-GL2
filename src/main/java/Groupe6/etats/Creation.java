package Groupe6.etats;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import Groupe6.game.Game;
import Groupe6.save.ParametresJoueur;
import Groupe6.save.SaveManager;
import Groupe6.ui.BoutonCreation;
import Groupe6.ui.TextInput;
import Groupe6.utilz.Constants;
import Groupe6.utilz.HelpMethods;
import Groupe6.utilz.LangManager;
import Groupe6.utilz.LayoutScale;

/**
 * État création de compte : logo, titre app, titre « Création », champ identifiant, bouton. Fond inchangé.
 */
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
    /** Centre horizontal calculé dans applyLayout, cohérent avec logo/nom. */
    private int centerX;

    private LayoutScale layoutScale;
    private TextInput textInput;
    private BoutonCreation bouton;
    private String titreCreation;
    private String placeholderIdentifiant;
    private String labelBoutonCreationProfil;

    public Creation(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        layoutScale = LayoutScale.getInstance();
        logo = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "Logo_Rect.png");
        nameAppImage = HelpMethods.getSpriteAtlas(HelpMethods.LOGO + "NameApp.png");
        updateTexts();

        textInput = new TextInput(0, 0, LARGEUR_CHAMP, HAUTEUR_CHAMP, placeholderIdentifiant, MAX_PSEUDO);
        bouton = new BoutonCreation(0, 0, LARGEUR_BOUTON, HAUTEUR_BOUTON);
        bouton.setLabel(labelBoutonCreationProfil);
    }

    @Override
    public void update() {
        getFond().update();
        textInput.update(getFond());
    }

    /** Bloc logo → nom app → titre « Création » → champ → bouton, centré, scaling depuis Constants. */
    @Override
    protected void applyLayout(int w, int h) {
        layoutScale.update(w,h);
        centerX = layoutScale.centerX();
        int cx = centerX;

        logoSize = layoutScale.scaleUniform(LOGO_DEFAULT_SIZE);
        logoX = cx - logoSize / 2;
        logoY = layoutScale.ratioY(Constants.Ratios.Creation.RATIO_LOGO_Y);
        int gapLogoName = layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_LOGO_NAME_REF);
        nameAppHeight = layoutScale.scaleUniform(Constants.Ratios.Creation.NAME_APP_REF_HEIGHT);
        nameAppHeight = Math.max(1, nameAppHeight);
        nameAppWidth = (nameAppImage != null && nameAppImage.getHeight() > 0)
                ? nameAppHeight * nameAppImage.getWidth() / nameAppImage.getHeight()
                : nameAppHeight;
        nameAppWidth = Math.max(1, nameAppWidth);
        nameAppX = cx - nameAppWidth / 2;
        nameAppY = logoY + logoSize + gapLogoName;

        int gapNameTitle = layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_NAME_TITLE_REF);
        titleFontSize = layoutScale.scaleUniform(Constants.Ratios.Creation.TITLE_FONT_SIZE_REF);
        titleY = nameAppY + nameAppHeight + gapNameTitle;

        int gapTitleChamp = layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_TITLE_CHAMP_REF);
        int gapChampBouton = layoutScale.scaleUniform(Constants.Ratios.Creation.ESPACEMENT_CHAMP_BOUTON_REF);
        int cw = layoutScale.scaleX(LARGEUR_CHAMP);
        int ch = layoutScale.scaleY(HAUTEUR_CHAMP);
        int bw = layoutScale.scaleX(LARGEUR_BOUTON);
        int bh = layoutScale.scaleY(HAUTEUR_BOUTON);

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
        getFond().draw(g);

        if (logo != null) {
            g.drawImage(logo, logoX, logoY, logoSize, logoSize, null);
        }
        if (nameAppImage != null) {
            g.drawImage(nameAppImage, nameAppX, nameAppY, nameAppWidth, nameAppHeight, null);
        }
        g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, Math.max(12, titleFontSize)));
        g.setColor(java.awt.Color.BLACK);
            int tw = g.getFontMetrics().stringWidth(titreCreation);
                g.drawString(titreCreation, centerX - tw / 2, titleY + g.getFontMetrics().getAscent());

        textInput.draw(g, getFond());
        bouton.draw(g, getFond());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        textInput.handleKeyTyped(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        bouton.setSourisSurvol(isIn(e, bouton));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

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
        // On empêche le changement d'écran (et donc la création d'une sauvegarde)
        // -> si le pseudo est vide, en revanche si il est valide on autorise
        if (bouton.isSourisEnfonce() && isIn(e, bouton)){
          String pseudo = getPseudoSaisi();
          if (pseudo != null && !pseudo.trim().isEmpty()){
              if (SaveManager.chargerParametres(pseudo) == null) {
                  SaveManager.sauvegarderParametres(new ParametresJoueur(pseudo, "fr", 0, 0, 0));
              }
              game.setJoueurCourant(pseudo);
              bouton.appliquerAction();
          }
      }
      bouton.setSourisEnfonce(false);
    }

    

    @Override
    public void updateTexts() {
        titreCreation = LangManager.get("creation.titre");
        placeholderIdentifiant = LangManager.get("creation.identifiant");
        labelBoutonCreationProfil = LangManager.get("creation.bouton");

        if (textInput != null) {
            textInput.setPlaceholder(placeholderIdentifiant);
        }
        if (bouton != null) {
            bouton.setLabel(labelBoutonCreationProfil);
        }
    }

    public String getPseudoSaisi() {
        return textInput.getTextTrimmed();
    }
}
