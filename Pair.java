// Rôle : Représente simplement des coordonnées (x,y) ou (ligne,colonne).
// Fonctionnement : Elle stocke deux entiers (first, second). Elle contient une méthode equals importante pour vérifier si deux positions sont identiques (par exemple : "Est-ce que ma position actuelle est égale à la destination ?").

public class Pair{
    int first;
    int second;

    public Pair(int first, int second){
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object obj){
        return obj instanceof Pair && this.first == ((Pair)obj).first && this.second == ((Pair)obj).second;
    }

    @Override
    public int hashCode(){
        return 31 * first + second;
    }

    public int getFirst(){
        return this.first;
    }

    public int getSecond(){
        return this.second;
    }
}