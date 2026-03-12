package Groupe6.save;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class SaveManager {
    
    private static final String SAVE_FOLDER = "saveGame/";
    private static final String SETTINGS_FILE = "settings.json";
    private static final String ANNUAIRE_FILE = "annuaire.json";
    private static final String BEST_TIMES_FILE = "meilleurs_temps.json";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static Map<String, String> chargerAnnuaire() {
        String cheminFichier = SAVE_FOLDER + ANNUAIRE_FILE;
        
        if (Files.exists(Paths.get(cheminFichier))) {
            try (FileReader reader = new FileReader(cheminFichier)) {
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String, String> annuaire = gson.fromJson(reader, type);
                if (annuaire != null) {
                    return annuaire;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return new HashMap<>();
    }

    private static void sauvegarderAnnuaire(Map<String, String> annuaire) {
        try {
            Files.createDirectories(Paths.get(SAVE_FOLDER));
            
            try (FileWriter writer = new FileWriter(SAVE_FOLDER + ANNUAIRE_FILE)) {
                gson.toJson(annuaire, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getOuCreerIdJoueur(String nomJoueur) {
        Map<String, String> annuaire = chargerAnnuaire();
        
        if (annuaire.containsKey(nomJoueur)) {
            return annuaire.get(nomJoueur);
        }

        int maxId = 0;
        for (String idStr : annuaire.values()) {
            try {
                int id = Integer.parseInt(idStr);
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String nouvelId = String.format("%03d", maxId + 1);
        annuaire.put(nomJoueur, nouvelId);
        sauvegarderAnnuaire(annuaire);

        return nouvelId;
    }

    public static void sauvegarderParametres(ParametresJoueur parametres) {
        String idDossier = getOuCreerIdJoueur(parametres.getNomJoueur());
        String dossierJoueur = SAVE_FOLDER + idDossier + "/";
        String cheminFichier = dossierJoueur + SETTINGS_FILE;

        try {
            Files.createDirectories(Paths.get(dossierJoueur));
            
            try (FileWriter writer = new FileWriter(cheminFichier)) {
                gson.toJson(parametres, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ParametresJoueur chargerParametres(String nomJoueur) {
        Map<String, String> annuaire = chargerAnnuaire();
        
        if (!annuaire.containsKey(nomJoueur)) {
            return null;
        }

        String idDossier = annuaire.get(nomJoueur);
        String cheminFichier = SAVE_FOLDER + idDossier + "/" + SETTINGS_FILE;

        if (Files.exists(Paths.get(cheminFichier))) {
            try (FileReader reader = new FileReader(cheminFichier)) {
                return gson.fromJson(reader, ParametresJoueur.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    public static void sauvegarderPartie(String nomJoueur, String nomSauvegarde, PartieSauvegardee partie) {
        Map<String, String> annuaire = chargerAnnuaire();
        
        if (!annuaire.containsKey(nomJoueur)) {
            return;
        }

        String idDossier = annuaire.get(nomJoueur);
        String dossierJoueur = SAVE_FOLDER + idDossier + "/";
        String cheminFichier = dossierJoueur + nomSauvegarde + ".json";

        try {
            Files.createDirectories(Paths.get(dossierJoueur));
            
            try (FileWriter writer = new FileWriter(cheminFichier)) {
                gson.toJson(partie, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static PartieSauvegardee chargerPartie(String nomJoueur, String nomSauvegarde) {
        Map<String, String> annuaire = chargerAnnuaire();
        
        if (!annuaire.containsKey(nomJoueur)) {
            return null;
        }

        String idDossier = annuaire.get(nomJoueur);
        String cheminFichier = SAVE_FOLDER + idDossier + "/" + nomSauvegarde + ".json";

        if (Files.exists(Paths.get(cheminFichier))) {
            try (FileReader reader = new FileReader(cheminFichier)) {
                return gson.fromJson(reader, PartieSauvegardee.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    public static Map<String, Long> chargerMeilleursTemps(String nomJoueur) {
        Map<String, String> annuaire = chargerAnnuaire();
        
        if (!annuaire.containsKey(nomJoueur)) {
            return new HashMap<>();
        }

        String idDossier = annuaire.get(nomJoueur);
        String cheminFichier = SAVE_FOLDER + idDossier + "/" + BEST_TIMES_FILE;

        if (Files.exists(Paths.get(cheminFichier))) {
            try (FileReader reader = new FileReader(cheminFichier)) {
                Type type = new TypeToken<Map<String, Long>>(){}.getType();
                Map<String, Long> temps = gson.fromJson(reader, type);
                if (temps != null) {
                    return temps;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return new HashMap<>();
    }

    private static void sauvegarderMeilleursTemps(String nomJoueur, Map<String, Long> meilleursTemps) {
        Map<String, String> annuaire = chargerAnnuaire();
        
        if (!annuaire.containsKey(nomJoueur)) {
            return;
        }

        String idDossier = annuaire.get(nomJoueur);
        String dossierJoueur = SAVE_FOLDER + idDossier + "/";
        String cheminFichier = dossierJoueur + BEST_TIMES_FILE;

        try {
            Files.createDirectories(Paths.get(dossierJoueur));
            
            try (FileWriter writer = new FileWriter(cheminFichier)) {
                gson.toJson(meilleursTemps, writer);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void enregistrerMeilleurTemps(String nomJoueur, String idNiveau, long nouveauTemps) {
        Map<String, Long> tempsActuels = chargerMeilleursTemps(nomJoueur);
        
        if (!tempsActuels.containsKey(idNiveau) || nouveauTemps < tempsActuels.get(idNiveau)) {
            tempsActuels.put(idNiveau, nouveauTemps);
            sauvegarderMeilleursTemps(nomJoueur, tempsActuels);
        }
    }
}