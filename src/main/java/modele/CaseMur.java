import javafx.scene.paint.Color;

/**
 * Représente une case mur du plateau.
 */
public class CaseMur extends Case {
    /**
     * Construit une case mur.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public CaseMur(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estMur() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return false;
    }

    @Override
    public char getSymbole() {
        return '#';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#517664");
    }
}
