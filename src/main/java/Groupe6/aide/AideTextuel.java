package Groupe6.aide;

/**
 * Représente une aide sous forme textuelle.
 * <p>
 * Une aide textuelle est composée d’un titre et d’un texte explicatif
 * destiné à être affiché à l’utilisateur.
 * </p>
 */
public class AideTextuel {

    /** Titre de l'aide */
    private String titre;

    /** Texte descriptif de l'aide */
    private String texte;

    /**
     * Construit une aide textuelle avec un titre et un contenu.
     *
     * @param titre le titre de l'aide
     * @param texte le texte explicatif associé
     */
    public AideTextuel(String titre, String texte){
        this.titre = titre;
        this.texte = texte;
    }

    /**
     * Constructeur de copie.
     * <p>
     * Crée une nouvelle instance en copiant les données
     * d'une autre {@code AideTextuel}.
     * </p>
     *
     * @param aideTextuel l'objet à copier
     */
    public AideTextuel(AideTextuel aideTextuel){
        this.titre = aideTextuel.getTitre();
        this.texte = aideTextuel.getTexte();
    }

    /**
     * Retourne le titre de l'aide.
     * <p>
     * Une nouvelle instance de {@link String} est retournée
     * afin d'éviter toute modification externe.
     * </p>
     *
     * @return le titre de l'aide
     */
    public String getTitre(){
        return new String(this.titre);
    }

    /**
     * Retourne le texte de l'aide.
     * <p>
     * Une nouvelle instance de {@link String} est retournée
     * afin d'éviter toute modification externe.
     * </p>
     *
     * @return le texte de l'aide
     */
    public String getTexte(){
        return new String(this.texte);
    }
}