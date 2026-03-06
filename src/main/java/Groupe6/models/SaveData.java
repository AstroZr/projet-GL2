package Groupe6.models;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    public String pseudonyme;
    public Parametres parametresGlobaux;
    public List<LevelData> historiqueNiveau;

    public SaveData(String pseudonyme) {
        this.pseudonyme = pseudonyme;
        this.parametresGlobaux = new Parametres();
        this.historiqueNiveau = new ArrayList<>();
    }

    public void addLevelResult(LevelData lvl) {
        this.historiqueNiveau.add(lvl);
    }
}
