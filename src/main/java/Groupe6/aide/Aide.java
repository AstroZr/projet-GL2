package Groupe6.aide;

public abstract class Aide{
    protected int numero;
    protected int nbUtilisation;
    protected AideTextuel aideTextuel;
    protected AideVisuel AideVisuel;

    Aide(int numero){
        this.numero = numero // to continue
    }

    public getNumero(){
        return numero;
    }

    public getNbUtilisation(){
        return nbUtilisation;
    }

    public getAideTextuel(){
        return new AideTextuel(this.aideTextuel);
    }

    public getAideVisuel(){
        return new AideVisuel(this.aideVisuel);
    }
}