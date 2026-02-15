package Groupe6.aide;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class AideVisuel implements Iterable<EffetVisuel>{
   private List<EffetVisuel> effetsVisuels;

    public AideVisuel(){
        this.effetsVisuels = new ArrayList<>();
    }

    public void add(EffetVisuels effetVisuels){
        this.effetsVisuels.add(effetVisuels);
    }

    public Iterator<Effetvisuel> iterator(){
        return this.effetVisuels.iterator();
    }
}
