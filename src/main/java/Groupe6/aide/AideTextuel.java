package Groupe6.aide;

public class AideTextuel{
    private String titre;
    private String texte;

    public AideTextuel(String titre, String texte){
        this.titre = titre;
        this.texte = texte;
    }

    public AideTextuel(AideTextuel AideTextuel){
        this.titre = AideTextuel.getTitre();
        this.texte = AideTextuel.getTexte();
    }

    public String getTitre(){
        return new String(this.titre);
    }

    public String getTexte(){
        return new String(this.texte);
    }
}