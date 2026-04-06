package Groupe6.aide;

/**
 * Représente un effet visuel appliqué sur une grille de jeu.
 * <p>
 * Un effet visuel est défini par :
 * <ul>
 *     <li>un {@link TypeEffect} indiquant la nature (positive ou négative)</li>
 *     <li>une position (x, y) sur la grille</li>
 *     <li>une valeur associée (ex : "3", "-2")</li>
 * </ul>
 * </p>
 */
public class EffetVisuel {

    /** Type de l'effet visuel */
    private TypeEffect type;

    /**
     * Valeur associée à l'effet.
     * <p>
     * Représente généralement un numéro sous forme de chaîne
     * (ex : "3", "-2").
     * </p>
     */
    private String value;

    /** Coordonnée X sur la grille */
    private int x;

    /** Coordonnée Y sur la grille */
    private int y;

    /**
     * Construit un effet visuel avec ses propriétés.
     *
     * @param type le type d'effet visuel
     * @param x la position X sur la grille
     * @param y la position Y sur la grille
     * @param value la valeur associée (ex : "3", "-2")
     */
    public EffetVisuel(TypeEffect type, int x, int y, String value){
        this.type = type;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    /**
     * Constructeur de copie.
     * <p>
     * Crée une nouvelle instance en copiant les valeurs
     * d'un autre {@code EffetVisuel}.
     * </p>
     *
     * @param effetVisuel l'effet visuel à copier
     */
    public EffetVisuel(EffetVisuel effetVisuel){
        this.type = effetVisuel.getType();
        this.x = effetVisuel.getX();
        this.y = effetVisuel.getY();
        this.value = effetVisuel.getValue();
    }

    /**
     * Retourne la coordonnée X de l'effet.
     *
     * @return la position X
     */
    public int getX(){
        return this.x;
    }

    /**
     * Retourne la coordonnée Y de l'effet.
     *
     * @return la position Y
     */
    public int getY(){
        return this.y;
    }

    /**
     * Retourne la valeur associée à l'effet.
     * <p>
     * Une nouvelle instance de {@link String} est retournée
     * afin d'éviter toute modification externe.
     * </p>
     *
     * @return la valeur de l'effet
     */
    public String getValue(){
        return new String(this.value);
    }

    /**
     * Retourne le type de l'effet visuel.
     *
     * @return le {@link TypeEffect}
     */
    public TypeEffect getType(){
        return this.type;
    }
}