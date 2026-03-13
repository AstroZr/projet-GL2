package Groupe6.aide;

public abstract class AideAbstract implements Aide{
    protected int id;
    protected int nbUtilisation = 0;
    protected AideTextuel aideTextuel;
    protected AideVisuel aideVisuel;

    public AideAbstract(int id){
        this.id = id;
    }

    @Override
    public int getId(){
        return id;
    }

    @Override
    public int getNbUtilisation(){
        return nbUtilisation;
    }

    @Override
    public AideTextuel getAideTextuel(){
        return new AideTextuel(this.aideTextuel);
    }

    @Override
    public AideVisuel getAideVisuel(){
        return new AideVisuel(this.aideVisuel);
    }
}