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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestionnaire centralisé de la persistance (save/load) pour CalcuDoku.
 * 
 * Utilise GSON pour la sérialisation JSON. Structure fichiers:
 * saveGame/
 *   annuaire.json                    (map: nomJoueur -> idJoueur)
 *   meilleurs_temps.json             (best times tracking)
 *   001/                             (dossier joueur)
 *     settings.json                  (ParametresJoueur)
 *     facile1.json, facile2.json ... (PartieSauvegardee per level)
 *   002/, 003/, ...
 * 
 * Responsabilités:
 * - Charger/sauvegarder les paramètres joueur (volume, langue, theme, etc.)
 * - Charger/sauvegarder les parties en cours (grille, historique, temps, etc.)
 * - Gérer l'annuaire joueur (créer IDs uniques 001, 002, 003...)
 * - Charger les niveaux depuis resources/ (niveaux/facile1.json, etc.)
 * - Tracking des meilleurs temps par joueur et niveau
 * \n * Threading: Les opérations I/O bloquent le thread appelant (pas async)
 */
public class SaveManager {

    private static final Logger logger = LoggerFactory.getLogger(SaveManager.class);

    // ====== CHEMINS & FICHIERS ======
    private static final String SAVE_FOLDER = "saveGame/";       // Dossier racine des sauvegardes
    private static final String SETTINGS_FILE = "settings.json";  // Fichier des paramètres du joueur
    private static final String ANNUAIRE_FILE = "annuaire.json";  // Annuaire nom -> ID dossier
    private static final String BEST_TIMES_FILE = "meilleurs_temps.json";  // Fichier des meilleurs temps

    // ====== GSON (JSON) ======
    /** Instance GSON avec pretty-printing pour lisibilité JSON. */
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Charge l'annuaire des joueurs depuis le fichier JSON.
     * * @return Une Map associant le nom du joueur (clé) à son identifiant de dossier (valeur). 
     * Retourne une Map vide si le fichier n'existe pas ou en cas d'erreur.
     */
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
            } catch (Exception e) {
                logger.error("Erreur lors du chargement de l'annuaire", e);
            }
        }

        return new HashMap<>();
    }

    /**
     * Sauvegarde l'annuaire des joueurs dans le fichier JSON.
     * * @param annuaire La Map contenant les associations nomJoueur -> idDossier à sauvegarder.
     */
    private static void sauvegarderAnnuaire(Map<String, String> annuaire) {
        try {
            Files.createDirectories(Paths.get(SAVE_FOLDER));

            try (FileWriter writer = new FileWriter(SAVE_FOLDER + ANNUAIRE_FILE)) {
                gson.toJson(annuaire, writer);
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de l'annuaire", e);
        }
    }

    /**
     * Récupère l'identifiant de dossier d'un joueur. S'il n'existe pas, un nouvel ID 
     * incrémental (format "001", "002") est créé, ajouté à l'annuaire et sauvegardé.
     * * @param nomJoueur Le nom du joueur recherché.
     * @return L'identifiant du dossier du joueur (ex: "001").
     */
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

    /**
     * Sauvegarde les paramètres globaux d'un joueur dans son dossier personnel.
     * * @param parametres L'objet contenant les paramètres du joueur à sérialiser.
     */
    public static void sauvegarderParametres(ParametresJoueur parametres) {
        String idDossier = getOuCreerIdJoueur(parametres.getNomJoueur());
        String dossierJoueur = SAVE_FOLDER + idDossier + "/";
        String cheminFichier = dossierJoueur + SETTINGS_FILE;

        try {
            Files.createDirectories(Paths.get(dossierJoueur));

            try (FileWriter writer = new FileWriter(cheminFichier)) {
                gson.toJson(parametres, writer);
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde des paramètres pour joueur: {}", parametres.getNomJoueur(), e);
        }
    }

    /**
     * Charge les paramètres globaux d'un joueur depuis son dossier personnel.
     * * @param nomJoueur Le nom du joueur dont on veut charger les paramètres.
     * @return L'objet {@link ParametresJoueur} désérialisé, ou null si le joueur n'existe pas 
     * ou en cas d'erreur de lecture.
     */
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
                logger.error("Erreur lors du chargement des paramètres pour joueur: {}", nomJoueur, e);
            }
        }

        return null;
    }

    /**
     * Sauvegarde l'état d'une partie en cours pour un joueur donné.
     * * @param nomJoueur    Le nom du joueur propriétaire de la sauvegarde.
     * @param idSauvegarde L'identifiant de la sauvegarde (généralement le nom du niveau, ex: "facile1").
     * @param partie       L'objet {@link PartieSauvegardee} contenant la grille et l'historique.
     */
    public static void sauvegarderPartie(String nomJoueur, String idSauvegarde, PartieSauvegardee partie) {
        String idDossier = getOuCreerIdJoueur(nomJoueur);
        String dossierJoueur = SAVE_FOLDER + idDossier + "/";
        String cheminFichier = dossierJoueur + idSauvegarde + ".json";

        try {
            Files.createDirectories(Paths.get(dossierJoueur));

            try (FileWriter writer = new FileWriter(cheminFichier)) {
                gson.toJson(partie, writer);
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde de la partie pour joueur: {}, niveau: {}", nomJoueur, idSauvegarde, e);
        }
    }

    /**
     * Charge une partie en cours préalablement sauvegardée par un joueur.
     * * @param nomJoueur    Le nom du joueur.
     * @param idSauvegarde L'identifiant de la sauvegarde (nom du niveau).
     * @return L'objet {@link PartieSauvegardee} désérialisé, ou null si la sauvegarde n'existe pas.
     */
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
            } catch (Exception e) {
                logger.error("Erreur lors du chargement de la partie pour joueur: {}, niveau: {}", nomJoueur, idSauvegarde, e);
            }
        }

        return null;
    }

    /**
     * Charge l'ensemble des meilleurs temps enregistrés pour un joueur.
     * * @param nomJoueur Le nom du joueur.
     * @return Une Map associant l'ID du niveau (clé) au meilleur temps en millisecondes (valeur). 
     * Retourne une Map vide si aucun temps n'est enregistré.
     */
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
            } catch (Exception e) {
                logger.error("Erreur lors du chargement des meilleurs temps pour joueur: {}", nomJoueur, e);
            }
        }

        return new HashMap<>();
    }

    /**
     * Sauvegarde la Map des meilleurs temps d'un joueur dans son dossier.
     * * @param nomJoueur      Le nom du joueur.
     * @param meilleursTemps La Map des temps à sérialiser.
     */
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

        } catch (Exception e) {
            logger.error("Erreur lors de la sauvegarde des meilleurs temps pour joueur: {}", nomJoueur, e);
        }
    }

    /**
     * Enregistre un nouveau temps pour un joueur sur un niveau spécifique. 
     * Le temps n'est sauvegardé que s'il est meilleur (inférieur) au temps précédemment enregistré.
     * * @param nomJoueur    Le nom du joueur.
     * @param idNiveau     L'identifiant du niveau terminé.
     * @param nouveauTemps Le temps réalisé en millisecondes.
     */
    public static void enregistrerMeilleurTemps(String nomJoueur, String idNiveau, long nouveauTemps) {
        Map<String, Long> tempsActuels = chargerMeilleursTemps(nomJoueur);

        if (!tempsActuels.containsKey(idNiveau) || nouveauTemps < tempsActuels.get(idNiveau)) {
            tempsActuels.put(idNiveau, nouveauTemps);
            sauvegarderMeilleursTemps(nomJoueur, tempsActuels);
        }
    }

    /**
     * Retourne la liste des noms (pseudos) de tous les joueurs enregistrés dans l'annuaire.
     * * @return Une liste contenant les noms des joueurs.
     */
    public static List<String> listerJoueurs() {
        return new ArrayList<>(chargerAnnuaire().keySet());
    }

    /**
     * Construit et retourne le classement global pour un niveau donné.
     * * @param idNiveau L'identifiant du niveau pour lequel on veut le classement.
     * @return Une liste d'entrées (NomJoueur -> Temps) triée par ordre croissant (du meilleur au moins bon).
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
        
        // Tri du meilleur (plus petit) au moins bon (plus grand)
        classement.sort(Map.Entry.comparingByValue());
        return classement;
    }

    /**
     * Liste les identifiants de niveaux disponibles en lisant le fichier d'index des ressources.
     * * @return Une liste des identifiants des niveaux prêts à être joués.
     */
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
        } catch (Exception e) {
            logger.error("Erreur lors du chargement de l'index des niveaux", e);
        }
        return ids;
    }

    /**
     * Construit et charge un objet {@link Niveau} complet à partir d'un fichier JSON inclus
     * dans les ressources de l'application (JAR). Reconstruit les liens entre les zones 
     * de calcul et les cellules de la matrice.
     * * @param idNiveau L'identifiant du niveau à charger (correspond au nom du fichier JSON).
     * @return L'objet {@link Niveau} instancié et prêt à être passé à la grille, ou null en cas d'erreur.
     */
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

                int[][] matriceCorrection = new int[taille][taille];
                JsonArray jsonCorrection = jsonObject.getAsJsonArray("matriceCorrection");
                for (int i = 0; i < taille; i++) {
                    JsonArray ligneJson = jsonCorrection.get(i).getAsJsonArray();
                    for (int j = 0; j < taille; j++) {
                        matriceCorrection[i][j] = ligneJson.get(j).getAsInt();
                    }
                }

                return new Niveau(id, taille, matrice, listeZones, matriceCorrection);

            } catch (Exception e) {
                logger.error("Erreur lors du chargement du niveau: {}", idNiveau, e);
            }
        } else {
            logger.error("Fichier de niveau introuvable: {}", cheminRessource);
        }

        return null;
    }
}