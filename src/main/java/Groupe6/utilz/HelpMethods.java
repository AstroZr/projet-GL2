package Groupe6.utilz;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utilitaires partagés : chemins d’assets et chargement d’images depuis les ressources.
 */
public class HelpMethods {

    /** Racine des ressources (classpath). */
    public static final String ASSETS = "/assets/";
    public static final String BOUTONS = ASSETS + "boutons/";
    public static final String NUAGES = ASSETS + "nuages/";

    /**
     * Charge une image depuis le classpath (ex. /assets/nuages/xxx.png).
     *
     * @param path Chemin relatif aux ressources
     * @return L’image ou null si le chemin est invalide ou la lecture échoue
     */
    public static BufferedImage getSpriteAtlas(String path) {
        InputStream is = HelpMethods.class.getResourceAsStream(path);
        if (is == null) {
            System.out.println("Le chemin spécifié est invalide : " + path);
            return null;
        }
        try {
            return javax.imageio.ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de l'image : " + path);
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de l'InputStream : " + e.getMessage());
            }
        }
    }
}
