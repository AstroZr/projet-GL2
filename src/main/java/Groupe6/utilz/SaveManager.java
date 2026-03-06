package Groupe6.utilz;

import Groupe6.models.SaveData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class SaveManager {

    private static SaveManager instance;
    private static final String SAVE_FOLDER = "saves/";
    private File fichierSauvegardeActuel;
    private Gson gson;

    private SaveManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        File directory = new File(SAVE_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    public boolean sauvegarderJeu(SaveData data) {
        if (data == null || data.pseudonyme == null) return false;
        fichierSauvegardeActuel = new File(SAVE_FOLDER + data.pseudonyme + ".json");
        try (Writer writer = new FileWriter(fichierSauvegardeActuel)) {
            gson.toJson(data, writer);
            System.out.println("Sauvegarde reussie pour : " + data.pseudonyme);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur de sauvegarde : " + e.getMessage());
            return false;
        }
    }

    public SaveData chargementDonnees(String username) {
        fichierSauvegardeActuel = new File(SAVE_FOLDER + username + ".json");
        if (!fichierSauvegardeActuel.exists()) {
            return new SaveData(username);
        }
        try (Reader reader = new FileReader(fichierSauvegardeActuel)) {
            return gson.fromJson(reader, SaveData.class);
        } catch (IOException e) {
            return new SaveData(username);
        }
    }
}
