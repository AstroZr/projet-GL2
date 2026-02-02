package Groupe6.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

/**
 * Champ de saisie texte : zone rectangulaire, focus, placeholder, limite de caractères.
 * update() prépare les couleurs/texte ; draw() affiche ; handleKeyTyped() gère la saisie.
 */
public class TextInput {

    private final Rectangle bounds;
    private final StringBuilder text;
    private final String placeholder;
    private final int maxLength;
    private boolean focused;
   
    /** Valeurs précalculées par update() pour draw() */
    private Color borderColor;
    private Color textColor;
    private String displayText;

    /**
     * @param x          Position X (pixels)
     * @param y          Position Y (pixels)
     * @param width      Largeur (pixels)
     * @param height     Hauteur (pixels)
     * @param placeholder Texte affiché quand le champ est vide
     * @param maxLength  Nombre max de caractères (0 = illimité)
     */
    public TextInput(int x, int y, int width, int height, String placeholder, int maxLength) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = new StringBuilder();
        this.placeholder = placeholder != null ? placeholder : "";
        this.maxLength = maxLength > 0 ? maxLength : Integer.MAX_VALUE;
        this.focused = false;
        this.borderColor = Color.GRAY;
        this.textColor = Color.GRAY;
        this.displayText = this.placeholder;
    }

    /** Met à jour les valeurs affichées (couleurs, texte) ; à appeler par l’état avant draw. */
    public void update() {
        borderColor = focused ? Color.BLUE : Color.GRAY;
        displayText = text.length() > 0 ? text.toString() : placeholder;
        textColor = text.length() == 0 ? Color.GRAY : Color.BLACK;
    }

    /** Dessine le champ à partir des valeurs précalculées par update(). */
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setColor(borderColor);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setFont(new Font("Berlin Sans FB Demi", Font.PLAIN, 18));
        g.setColor(textColor);
        int fy = bounds.y + (bounds.height + g.getFontMetrics().getAscent()) / 2 - 2;
        g.drawString(displayText, bounds.x + 8, fy);
    }

    /** Indique si le point (x, y) est dans la zone du champ. */
    public boolean contains(int x, int y) {
        return bounds.contains(x, y);
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    /**
     * Traite un caractère saisi (backspace ou imprimable) si le champ a le focus.
     * À appeler depuis l’état dans keyTyped.
     */
    public void handleKeyTyped(KeyEvent e) {
        if (!focused) {
            return;
        }
        char c = e.getKeyChar();
        if (c == '\b') {
            if (text.length() > 0) {
                text.setLength(text.length() - 1);
            }
        } else if (c >= 32 && c < 127 && text.length() < maxLength) {
            text.append(c);
        }
    }

    /** Texte saisi (brut). */
    public String getText() {
        return text.toString();
    }

    /** Texte saisi sans espaces en début/fin. */
    public String getTextTrimmed() {
        return text.toString().trim();
    }

    /** Vide le champ. */
    public void clear() {
        text.setLength(0);
    }

    public void setText(String s) {
        text.setLength(0);
        if (s != null) {
            int len = Math.min(s.length(), maxLength);
            text.append(s, 0, len);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
