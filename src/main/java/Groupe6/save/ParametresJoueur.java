package Groupe6.save;

/**
 * Conteneur de données pour les préférences d'un joueur.
 * 
 * Responsabilités:
 * - Mémoriser les paramètres personnels du joueur
 * - Êatre sérialisée/désérialisée en JSON par SaveManager (GSON)
 * - Êatre chargée au démarrage et mise en cache en RAM
 * 
 * Emmagasine:
 * - Nom/pseudo du joueur
 * - Langue de l'interface ("fr", "en")
 * - Volumes: effets sonores et musique (0-100)
 * - Préférence de thème (0=clair, 1=foncé, 2=catppuccin)
 * 
 * Sauvegarde:
 * - saveGame/001/settings.json (pour joueur 001)
 * - saveGame/002/settings.json (pour joueur 002), etc.
 * 
 * Flux:
 * 1. Connexion: SaveManager.chargerParametres(nomJoueur) → ParametresJoueur
 * 2. Utilisation: Game.parametres charge/affiche ces valeurs
 * 3. Changement: Parametres.java met à jour puis appelle SaveManager.sauvegarder()
 */
public class ParametresJoueur {

    // ====== IDENTIFICATION ======
    
    /** Nom ou pseudo unique du joueur. */
    private String nomJoueur;
    
    // ====== LOCALISATION ======
    
    /** Code de la langue sélectionnée pour l'interface (ex: "fr", "en"). */
    private String language;
    
    // ====== AUDIO ======
    
    /** Volume des effets sonores de l'interface et du jeu, compris entre 0 et 100. */
    private int volumeEffet;
    
    /** Volume de la musique de fond, compris entre 0 et 100. */
    private int volumeMusique;
    
    // ====== THÈME VISUEL ======
    
    /** * Identifiant du thème visuel sélectionné :
     * 0 = Thème Clair, 1 = Thème Foncé, 2 = Thème Catppuccin.
     */
    private int modeSombre;

    /**
     * Constructeur par défaut.
     * Indispensable pour permettre à la bibliothèque GSON de désérialiser 
     * l'objet correctement à partir du fichier JSON.
     */
    public ParametresJoueur() {
    }

    /**
     * Constructeur paramétré utilisé pour initialiser les préférences d'un nouveau joueur 
     * ou pour écraser les paramètres existants.
     * * @param nomJoueur     Le pseudo du joueur.
     * @param language      Le code de la langue (ex: "fr", "en").
     * @param volumeEffet   Le niveau du volume des bruitages (0 à 100).
     * @param volumeMusique Le niveau du volume de la musique (0 à 100).
     * @param modeSombre    L'ID du thème visuel (0 = clair, 1 = foncé, 2 = catppuccin).
     */
    public ParametresJoueur(String nomJoueur, String language, int volumeEffet, int volumeMusique, int modeSombre) {
        this.nomJoueur = nomJoueur;
        this.language = language;
        this.volumeEffet = volumeEffet;
        this.volumeMusique = volumeMusique;
        this.modeSombre = modeSombre;
    }

    /**
     * Obtient le nom du joueur.
     * @return Le pseudo du joueur.
     */
    public String getNomJoueur() {
        return nomJoueur;
    }

    /**
     * Définit le nom du joueur.
     * @param nomJoueur Le nouveau pseudo du joueur.
     */
    public void setNomJoueur(String nomJoueur) {
        this.nomJoueur = nomJoueur;
    }

    /**
     * Obtient la langue de l'interface.
     * @return Le code de la langue (ex: "fr").
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Définit la langue de l'interface.
     * @param language Le code de la nouvelle langue.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Obtient le volume des effets sonores.
     * @return Le volume des effets (de 0 à 100).
     */
    public int getVolumeEffet() {
        return volumeEffet;
    }

    /**
     * Définit le volume des effets sonores.
     * @param volumeEffet Le nouveau volume des effets (de 0 à 100).
     */
    public void setVolumeEffet(int volumeEffet) {
        this.volumeEffet = volumeEffet;
    }

    /**
     * Obtient le volume de la musique.
     * @return Le volume de la musique (de 0 à 100).
     */
    public int getVolumeMusique() {
        return volumeMusique;
    }

    /**
     * Définit le volume de la musique.
     * @param volumeMusique Le nouveau volume de la musique (de 0 à 100).
     */
    public void setVolumeMusique(int volumeMusique) {
        this.volumeMusique = volumeMusique;
    }

    /**
     * Obtient l'identifiant du thème visuel choisi.
     * @return 0 pour Clair, 1 pour Foncé, 2 pour Catppuccin.
     */
    public int getModeSombre() {
        return modeSombre;
    }

    /**
     * Définit le thème visuel de l'interface.
     * @param modeSombre L'ID du thème (0 = Clair, 1 = Foncé, 2 = Catppuccin).
     */
    public void setModeSombre(int modeSombre) {
        this.modeSombre = modeSombre;
    }
}