package Groupe6.aide;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Représente un ensemble d'effets visuels associés à une aide.
 * <p>
 * Cette classe agit comme un conteneur d'objets {@link EffetVisuel}
 * et permet de les parcourir grâce à l'interface {@link Iterable}.
 * </p>
 */
public class AideVisuel implements Iterable<EffetVisuel> {

    /** Liste des effets visuels */
    private List<EffetVisuel> effetsVisuels;

    /**
     * Construit une aide visuelle vide.
     */
    public AideVisuel(){
        this.effetsVisuels = new ArrayList<>();
    }

    /**
     * Constructeur de copie.
     * <p>
     * Crée une nouvelle instance en copiant les effets visuels
     * d'une autre aide visuelle.
     * </p>
     *
     * @param aideVisuel l'aide visuelle à copier
     */
    public AideVisuel(AideVisuel aideVisuel){
        this.effetsVisuels = new ArrayList<>(aideVisuel.getEffeftsVisuels());
    }

    /**
     * Ajoute un effet visuel à la liste.
     *
     * @param effetVisuels l'effet visuel à ajouter
     */
    public void add(EffetVisuel effetVisuels){
        this.effetsVisuels.add(effetVisuels);
    }

    /**
     * Retourne une copie de la liste des effets visuels.
     * <p>
     * Une nouvelle liste est créée afin d'éviter toute modification
     * externe de l'état interne.
     * </p>
     *
     * @return une copie de la liste des {@link EffetVisuel}
     */
    public List<EffetVisuel> getEffeftsVisuels(){
        return new ArrayList<EffetVisuel>(this.effetsVisuels);
    }

    /**
     * Retourne un itérateur sur les effets visuels.
     *
     * @return un {@link Iterator} permettant de parcourir les effets
     */
    @Override
    public Iterator<EffetVisuel> iterator(){
        return this.effetsVisuels.iterator();
    }
}