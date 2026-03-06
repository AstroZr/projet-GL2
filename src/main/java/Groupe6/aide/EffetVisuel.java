package Groupe6.aide;

public class EffetVisuel{
    private TypeEffect type;
    private String value; // value contient des numéro de la forme : "3" ou "-2"
    private int x, y; // Position sur la grille de jeu

    EffetVisuel(TypeEffect type, int x, int y, String value){
        this.type = type;
        this.x = x; this.y = y;
        this.value = value;
    }

    // constructeur de copie
    EffetVisuel(EffetVisuel effetVisuel){
        this.type = effetVisuel.getType();
        this.x = effetVisuel.getX();
        this.y = effetVisuel.getY();
        this.value = effetVisuel.getValue();
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public String getValue(){
        return new String(this.value);
    }

    public TypeEffect getType(){
        return this.type;
    }
}
