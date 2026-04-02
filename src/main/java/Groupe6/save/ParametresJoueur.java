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
    private String nomJoueur;         // Nom/pseudo du joueur
    
    // ====== LOCALISATION ======
    private String language;          // Code langue ("fr", "en", etc.)
    
    // ====== AUDIO ======
    private int volumeEffet;          // Volume des effets sonores (0-100)
    private int volumeMusique;        // Volume de la musique (0-100)
    
    // ====== THÈME VISUEL ======
    private int modeSombre;           // Choix thème: 0=clair, 1=foncé, 2=catppuccin
    
    // ====== AIDE DE JEU ======
    private boolean afficherPossibilites;  // Afficher/masquer les possibilités d'équations au survol
    private boolean afficherErreurDouble;  // Afficher/masquer les cases rouge en cas de doublon

    public ParametresJoueur() {
    }

    public ParametresJoueur(String nomJoueur, String language, int volumeEffet, int volumeMusique, int modeSombre) {
        this.nomJoueur = nomJoueur;
        this.language = language;
        this.volumeEffet = volumeEffet;
        this.volumeMusique = volumeMusique;
        this.modeSombre = modeSombre;
        this.afficherPossibilites = false;  // Par défaut, afficher les possibilités
        this.afficherErreurDouble = false; // Par défaut, ne pas afficher les doublons
    }

    public String getNomJoueur() {
        return nomJoueur;
    }

    public void setNomJoueur(String nomJoueur) {
        this.nomJoueur = nomJoueur;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getVolumeEffet() {
        return volumeEffet;
    }

    public void setVolumeEffet(int volumeEffet) {
        this.volumeEffet = volumeEffet;
    }

    public int getVolumeMusique() {
        return volumeMusique;
    }

    public void setVolumeMusique(int volumeMusique) {
        this.volumeMusique = volumeMusique;
    }

    public int getModeSombre() {
        return modeSombre;
    }

    public void setModeSombre(int modeSombre) {
        this.modeSombre = modeSombre;
    }

    public boolean isAfficherPossibilites() {
        return afficherPossibilites;
    }

    public void setAfficherPossibilites(boolean afficherPossibilites) {
        this.afficherPossibilites = afficherPossibilites;
    }

    public boolean isAfficherErreurDouble() {
        return afficherErreurDouble;
    }

    public void setAfficherErreurDouble(boolean afficherErreurDouble) {
        this.afficherErreurDouble = afficherErreurDouble;
    }
}