package Groupe6.aide;

public abstract class Aide{
    protected int id;
    protected int nbUtilisation = 0;
    protected AideTextuel aideTextuel;
    protected AideVisuel aideVisuel;

    public Aide(int id){
        this.id = id;
    }

    public int getid(){
        return id;
    }

    public int getNbUtilisation(){
        return nbUtilisation;
    }

    public AideTextuel getAideTextuel(){
        return new AideTextuel(this.aideTextuel);
    }

    public AideVisuel getAideVisuel(){
        return new AideVisuel(this.aideVisuel);
    }
}