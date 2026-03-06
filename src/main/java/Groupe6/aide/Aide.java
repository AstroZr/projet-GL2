package Groupe6.aide;

public abstract class Aide{
    protected int numero;
    protected int nbUtilisation;
    protected AideTextuel aideTextuel;
    protected AideVisuel aideVisuel;

    Aide(int numero){
        this.numero = numero; // to continue
    }

    public int getNumero(){
        return numero;
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