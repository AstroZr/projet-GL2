package Groupe6.utilz;

/**
 * Constantes globales du jeu (taille fenêtre, animation, ratios de layout).
 */
public class Constants {
    public static boolean animationBG = false;
    /** Largeur courante de la fenêtre (mise à jour au resize). */
    public static int game_width = 1920;
    /** Hauteur courante de la fenêtre (mise à jour au resize). */
    public static int game_height = 1080;

    public static class Ratios {
        /** Ratio horizontal pour centrer un élément (centre = 0.5). */
        public static final float RATIO_CENTER_X = 0.5f;
        public static class Start {
            /** Ratio vertical pour le formulaire Start / Connexion (≈ 48,1 %). */
            public static final float RATIO_START_FORM_Y = 520f / 1080f;
        }
        public static class Menu {
            /** Ratio vertical pour la base des boutons du menu (≈ 41,7 % de la hauteur). */
            public static final float RATIO_MENU_BUTTONS_Y = 450f / 1080f;
        }
        public static class Parametres {
            /** Ratio vertical pour le bouton Retour des paramètres (≈ 74 %). */
            public static final float RATIO_PARAMETRES_BUTTON_Y = 800f / 1080f;
        }
    }
}
