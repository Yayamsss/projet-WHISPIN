import javafx.scene.paint.Color;

/**
 * Représente une case contenant une boîte sur une cible.
 */
public class CaseBoiteCible extends CaseBoite {
    /**
     * Construit une case boîte sur cible.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public CaseBoiteCible(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estCible() {
        return true;
    }

    @Override
    public boolean estBoiteCible() {
        return true;
    }

    @Override
    public char getSymbole() {
        return '*';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#F3B8FF");
    }
}
