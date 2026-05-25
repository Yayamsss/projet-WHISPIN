/**
 * Metadonnees d'une case pour A*.
 */
public final class Cell {
    public Pair parent;
    public double f;
    public double g;
    public double h;

    public Cell() {
        this.parent = new Pair(-1, -1);
        this.f = -1;
        this.g = -1;
        this.h = -1;
    }
}
