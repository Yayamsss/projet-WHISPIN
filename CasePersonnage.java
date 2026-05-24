import javafx.scene.paint.Color;

/**
 * Représente une case contenant le personnage.
 */
public class CasePersonnage extends Case {
    /**
     * Construit une case personnage.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public CasePersonnage(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estTraversable() {
        return false;
    }

    @Override
    public char getSymbole() {
        return '@';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#AB3428");
    }
}
