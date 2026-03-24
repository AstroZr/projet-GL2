package Groupe6.aide;

public abstract class AideAbstract implements Aide{
    protected int numero;
    protected int nbUtilisation = 0;
    protected AideTextuel aideTextuel;
    protected AideVisuel aideVisuel;

    public AideAbstract(int numero){
        this.numero = numero;
    }

    @Override
    public int getId(){
        return numero;
    }

    @Override
    public int getNbUtilisation(){
        return nbUtilisation;
    }

    @Override
    public void setNbUtilisation(int nbUtilisation){
        this.nbUtilisation = Math.max(0, nbUtilisation);
    }

    @Override
    public AideTextuel getAideTextuel(){
        return new AideTextuel(this.aideTextuel);
    }

    @Override
    public AideVisuel getAideVisuel(){
        return new AideVisuel(this.aideVisuel);
    }

    public int getCost(int nbAides){
        return (nbAides - numero) * nbUtilisation;
    }
}