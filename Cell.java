// Rôle : Représente une case de la grille avec toutes les informations mathématiques pour l'algorithme A*.
/* Attributs clés :

    parent : Stocke les coordonnées de la case précédente. C'est crucial pour reconstruire le chemin inverse une fois l'arrivée atteinte.

    f, g, h : Ce sont les coûts du chemin.

        g : Coût réel du départ jusqu'à cette case.

        h (Heuristique) : Estimation du coût de cette case jusqu'à l'arrivée (distance "à vol d'oiseau").

        f : Coût total (f=g+h). L'algorithme choisit toujours la case avec le plus petit f.
*/

public class Cell {
    public Pair parent;
    // f = g + h, where h is heuristic (check to the definition if you don't know what is it)
    public double f, g, h;

    public Cell(){
        parent = new Pair(-1, -1);
        f = -1;
        g = -1;
        h = -1;
    }

    public Cell(Pair parent, double f, double g, double h){
        this.parent = parent;
        this.f = f;
        this.g = g;
        this.h = h;
    }
}