package Groupe6.aide;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class AideVisuel implements Iterable<EffetVisuel>{
   private List<EffetVisuel> effetsVisuels;

    AideVisuel(){
        this.effetsVisuels = new ArrayList<>();
    }

    AideVisuel(AideVisuel aideVisuel){
        this.effetsVisuels = new ArrayList<>(aideVisuel.getEffeftsVisuels());
    }

    public void add(EffetVisuels effetVisuels){
        this.effetsVisuels.add(effetVisuels);
    }

    public List<EffetVisuel> getEffeftsVisuels(){
        return new ArrayList<EffetVisuel>(this.effetsVisuels);
    }

    public Iterator<Effetvisuel> iterator(){
        return this.effetVisuels.iterator();
    }
}
