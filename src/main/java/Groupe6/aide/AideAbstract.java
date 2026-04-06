package Groupe6.aide;

/**
 * Classe abstraite représentant une implémentation de base d'une {@link Aide}.
 * <p>
 * Cette classe fournit une gestion commune :
 * <ul>
 *     <li>de l'identifiant de l'aide</li>
 *     <li>du nombre d'utilisations</li>
 *     <li>des contenus textuels et visuels</li>
 *     <li>du titre et de la description</li>
 * </ul>
 * Les classes concrètes doivent implémenter les comportements spécifiques,
 * notamment les méthodes {@code check} et {@code load}.
 * </p>
 */
public abstract class AideAbstract implements Aide {

    /** Identifiant unique de l'aide */
    protected int numero;

    /** Nombre de fois où l'aide a été utilisée */
    protected int nbUtilisation = 0;

    /** Contenu textuel de l'aide */
    protected AideTextuel aideTextuel;

    /** Contenu visuel de l'aide */
    protected AideVisuel aideVisuel;

    /** Titre de l'aide */
    protected String titre;

    /** Description courte de l'aide */
    protected String description;

    /** Explication détaillée de l'aide */
    protected String explication;

    /**
     * Constructeur de base de l'aide.
     *
     * @param numero l'identifiant unique de l'aide
     */
    public AideAbstract(int numero){
        this.numero = numero;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId(){
        return numero;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNbUtilisation(){
        return nbUtilisation;
    }

    /**
     * Définit le nombre d'utilisations de l'aide.
     * <p>
     * La valeur est forcée à être positive ou nulle.
     * </p>
     *
     * @param nbUtilisation le nouveau nombre d'utilisations
     */
    @Override
    public void setNbUtilisation(int nbUtilisation){
        this.nbUtilisation = Math.max(0, nbUtilisation);
    }

    /**
     * Retourne une copie de l'aide textuelle.
     * <p>
     * Une nouvelle instance est créée pour éviter toute modification externe
     * de l'état interne.
     * </p>
     *
     * @return une copie de {@link AideTextuel}
     */
    @Override
    public AideTextuel getAideTextuel(){
        return new AideTextuel(this.aideTextuel);
    }

    /**
     * Retourne une copie de l'aide visuelle.
     * <p>
     * Une nouvelle instance est créée pour éviter toute modification externe
     * de l'état interne.
     * </p>
     *
     * @return une copie de {@link AideVisuel}
     */
    @Override
    public AideVisuel getAideVisuel(){
        return new AideVisuel(this.aideVisuel);
    }

    /**
     * Calcule le coût d'utilisation de l'aide.
     *
     * @param nbAides le nombre total d'aides disponibles
     * @return le coût calculé en fonction du numéro et du nombre d'utilisations
     */
    public int getCost(int nbAides){
        return (nbAides - numero) * nbUtilisation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitre(){
        return this.titre;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(){
        return this.description;
    }
}