import javafx.scene.paint.Color;

/**
 * Représente une case contenant le personnage sur une cible.
 */
public class CasePersonnageCible extends CasePersonnage {
    /**
     * Construit une case personnage sur cible.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public CasePersonnageCible(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estCible() {
        return true;
    }

    @Override
    public boolean estPersonnageCible() {
        return true;
    }

    @Override
    public char getSymbole() {
        return '+';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#D79B66");
    }
}
