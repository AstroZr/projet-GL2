package Groupe6.utilz;

/**
 * Constantes globales du jeu (dimensions, animations, ratios responsive de layout).
 * 
 * Points clés:
 * - REF_WIDTH/REF_HEIGHT (1920x1080): résolution de référence pour le scaling
 * - game_width/game_height: dimensions ACTUELLES (mises à jour au resize GamePanel)
 * - Ratios: positions relatives (0.0 à 1.0) pour adaptation responsive
 * - Chaque écran (Start, Menu, Selection, etc.) a sa classe Ratios interne
 * 
 * Usage: Layout calcule les positions finales en: position_pixels = ratio * game_height (ex)
 */
public class Constants {
    // ====== ANIMATIONS ======
    public static boolean animationBG = false;  // Active/désactive animations fond
    
    // ====== RÉSOLUTION DE RÉFÉRENCE ======
    public static final int REF_WIDTH = 1920;   // Largeur de référence (1080p)
    public static final int REF_HEIGHT = 1080;  // Hauteur de référence (1080p)

    // ====== DIMENSIONS COURANTES (dynamiques) ======
    /** Largeur courante de la fenêtre (mise à jour au resize du GamePanel). */
    public static int game_width = REF_WIDTH;
    /** Hauteur courante de la fenêtre (mise à jour au resize du GamePanel). */
    public static int game_height = REF_HEIGHT;
    
    // ====== RATIOS RESPONSIFS (position relative, 0.0 à 1.0) ======
    public static class Ratios {
        /** Ratio horizontal pour centrer un élément horizontalement (0.5 = centre). */
        public static final float RATIO_CENTER_X = 0.5f;
        
        /** Ratios pour l'écran START (splash screen / formulaire joueur). */
        public static class Start {
            /** Ratio vertical pour le formulaire Start / Connexion (≈ 48,1 % de la hauteur). */
            public static final float RATIO_START_FORM_Y = 520f / 1080f;
            /** Ratio vertical pour la position du logo (≈ 12 % du haut). */
            public static final float RATIO_LOGO_Y = 140f / 1080f;
            /** Espacement en pixels de référence entre les deux boutons (Création / Connexion). */
            public static final int ESPACEMENT_BOUTONS_REF = 30;
            /** Espacement en pixels de référence entre le logo et l’image du nom (NameApp). */
            public static final int ESPACEMENT_LOGO_NAME_REF = 40;
            /** Hauteur de référence pour l’image du nom du jeu (NameApp.png) en 1080p. */
            /** Espacement en pixels de référence entre l'image du nom et les boutons. */
            public static final int ESPACEMENT_NAME_BOUTONS_REF = 50;
            /** Hauteur de référence pour l'image du nom du jeu (NameApp.png) en 1080p. */
            public static final int NAME_APP_REF_HEIGHT = 80;
        }
        public static class Creation {
            public static final float RATIO_LOGO_Y = 140f / 1080f;
            public static final int ESPACEMENT_LOGO_NAME_REF = 40;
            public static final int ESPACEMENT_NAME_TITLE_REF = 24;
            public static final int ESPACEMENT_TITLE_CHAMP_REF = 32;
            public static final int ESPACEMENT_CHAMP_BOUTON_REF = 24;
            public static final int NAME_APP_REF_HEIGHT = 80;
            public static final int TITLE_FONT_SIZE_REF = 24;
        }
        public static class Menu {
            /** Ratio vertical pour la base des boutons du menu (≈ 41,7 % de la hauteur). */
            public static final float RATIO_MENU_BUTTONS_Y = 450f / 1080f;
        }
        public static class Parametres {
            /** Ratio vertical pour le bouton Retour des paramètres (≈ 74 %). */
            public static final float RATIO_PARAMETRES_BUTTON_Y = 800f / 1080f;
        }
        public static class Selection {
            public static final float RATIO_SELECTION_BUTTONS_Y = 400f / 1080f;
            public static final float RATIO_SELECTION_RETOUR_Y  = 900f / 1080f;
        }
        public static class Connexion {
            public static final float RATIO_LOGO_Y = 140f / 1080f;
            public static final int ESPACEMENT_LOGO_NAME_REF = 40;
            public static final int ESPACEMENT_NAME_TITLE_REF = 24;
            public static final int ESPACEMENT_TITLE_BOUTONS_REF = 40;
            public static final int ESPACEMENT_BOUTONS_REF = 16;
            public static final int NAME_APP_REF_HEIGHT = 80;
            public static final int TITLE_FONT_SIZE_REF = 24;
        }
    }

}
