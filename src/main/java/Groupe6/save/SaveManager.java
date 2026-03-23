package Groupe6.save;

import Groupe6.models.Cellule;
import Groupe6.models.TypeOperation;
import Groupe6.models.ZoneCalcul;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Type type = new TypeToken<Map<String, String>>() {
                }.getType();
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

    public static void sauvegarderPartie(
            String nomJoueur, String idSauvegarde, PartieSauvegardee partie) {
        Map<String, String> annuaire = chargerAnnuaire();

        if (!annuaire.containsKey(nomJoueur)) {
            return;
        }

        String idDossier = annuaire.get(nomJoueur);
        String dossierJoueur = SAVE_FOLDER + idDossier + "/";
        String cheminFichier = dossierJoueur + idSauvegarde + ".json";

        try {
            Files.createDirectories(Paths.get(dossierJoueur));

            try (FileWriter writer = new FileWriter(cheminFichier)) {
                gson.toJson(partie, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PartieSauvegardee chargerPartie(String nomJoueur, String idSauvegarde) {
        Map<String, String> annuaire = chargerAnnuaire();

        if (!annuaire.containsKey(nomJoueur)) {
            return null;
        }

        String idDossier = annuaire.get(nomJoueur);
        String cheminFichier = SAVE_FOLDER + idDossier + "/" + idSauvegarde + ".json";

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
                Type type = new TypeToken<Map<String, Long>>() {
                }.getType();
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

    private static void sauvegarderMeilleursTemps(
            String nomJoueur, Map<String, Long> meilleursTemps) {
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

    public static void enregistrerMeilleurTemps(
            String nomJoueur, String idNiveau, long nouveauTemps) {
        Map<String, Long> tempsActuels = chargerMeilleursTemps(nomJoueur);

        if (!tempsActuels.containsKey(idNiveau) || nouveauTemps < tempsActuels.get(idNiveau)) {
            tempsActuels.put(idNiveau, nouveauTemps);
            sauvegarderMeilleursTemps(nomJoueur, tempsActuels);
        }
    }

    /**
     * Liste les niveaux en récupérant le contenu de index.txt.
     *
     * @return List<String>
     */
    /** Retourne la liste des pseudos enregistrés dans l'annuaire. */
    public static List<String> listerJoueurs() {
        return new ArrayList<>(chargerAnnuaire().keySet());
    }

    /**
     * Retourne le classement global pour un niveau donné :
     * liste de (nomJoueur, tempsMs) triée du meilleur au moins bon.
     */
    public static List<Map.Entry<String, Long>> chargerClassementGlobal(String idNiveau) {
        Map<String, String> annuaire = chargerAnnuaire();
        List<Map.Entry<String, Long>> classement = new ArrayList<>();
        for (String nomJoueur : annuaire.keySet()) {
            Map<String, Long> temps = chargerMeilleursTemps(nomJoueur);
            if (temps.containsKey(idNiveau)) {
                classement.add(new java.util.AbstractMap.SimpleEntry<>(nomJoueur, temps.get(idNiveau)));
            }
        }
        classement.sort(Map.Entry.comparingByValue());
        return classement;
    }

    public static List<String> listerIdsNiveaux() {
        List<String> ids = new ArrayList<>();
        InputStream is = SaveManager.class.getResourceAsStream("/niveaux/index.txt");
        if (is == null)
            return ids;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty())
                    ids.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public static Niveau chargerNiveau(String idNiveau) {
        String cheminRessource = "/niveaux/" + idNiveau + ".json";
        java.io.InputStream is = SaveManager.class.getResourceAsStream(cheminRessource);

        if (is != null) {
            try (java.io.InputStreamReader reader = new java.io.InputStreamReader(is)) {
                JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

                String id = jsonObject.get("id").getAsString();
                int taille = jsonObject.get("taille").getAsInt();

                Cellule[][] matrice = new Cellule[taille][taille];
                for (int i = 0; i < taille; i++) {
                    for (int j = 0; j < taille; j++) {
                        matrice[i][j] = new Cellule(i, j);
                    }
                }

                List<ZoneCalcul> listeZones = new ArrayList<>();
                JsonArray jsonZones = jsonObject.getAsJsonArray("listeZones");

                for (JsonElement element : jsonZones) {
                    JsonObject zoneJson = element.getAsJsonObject();
                    int cible = zoneJson.get("valeurCible").getAsInt();
                    TypeOperation op = TypeOperation.valueOf(zoneJson.get("typeOperation").getAsString());

                    ZoneCalcul zone = new ZoneCalcul(cible, op);

                    JsonArray jsonCellules = zoneJson.getAsJsonArray("listeCellules");
                    for (JsonElement cellElement : jsonCellules) {
                        JsonObject cellJson = cellElement.getAsJsonObject();
                        int l = cellJson.get("ligne").getAsInt();
                        int c = cellJson.get("colonne").getAsInt();

                        zone.ajouterCellule(matrice[l][c]);
                    }
                    listeZones.add(zone);
                }

                return new Niveau(id, taille, matrice, listeZones);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Fichier de niveau introuvable : " + cheminRessource);
        }

        return null;
    }
}
