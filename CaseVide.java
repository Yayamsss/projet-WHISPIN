import javafx.scene.paint.Color;

/**
 * Représente une case vide du plateau.
 */
public class CaseVide extends Case {
    /**
     * Construit une case vide.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public CaseVide(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estVide() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return true;
    }

    @Override
    public char getSymbole() {
        return ' ';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#E2DBBE");
    }
}
