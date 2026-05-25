import javafx.scene.paint.Color;

/**
 * Représente une case contenant une boîte.
 */
public class CaseBoite extends Case {
    /**
     * Construit une case boîte.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public CaseBoite(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estBoite() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return false;
    }

    @Override
    public boolean estPoussable() {
        return true;
    }

    @Override
    public char getSymbole() {
        return '$';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#D6B4FC");
    }
}
