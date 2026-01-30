package Groupe6.utilz;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


public class HelpMethods{
    
    public static final String ASSETS = "/assets/";
    public static final String BOUTONS = ASSETS + "boutons/";


    public static BufferedImage GetSpriteAtlas(String path){
    BufferedImage img = null;
    InputStream is = HelpMethods.class.getResourceAsStream(path);

    if(is == null){
        System.out.println("Le chemin spécifié est invalide : " + path);
        return null;
    }
    
    try {
        img = javax.imageio.ImageIO.read(is);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
    }
    }
    return img;
}
}
