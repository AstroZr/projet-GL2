package Groupe6.save;

public class ParametresJoueur {

    private String nomJoueur;
    private String language;
    private int volumeEffet;
    private int volumeMusique;
    private int modeSombre;

    public ParametresJoueur() {
    }

    public ParametresJoueur(String nomJoueur, String language, int volumeEffet, int volumeMusique, int modeSombre) {
        this.nomJoueur = nomJoueur;
        this.language = language;
        this.volumeEffet = volumeEffet;
        this.volumeMusique = volumeMusique;
        this.modeSombre = modeSombre;
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
}