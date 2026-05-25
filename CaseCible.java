import javafx.scene.paint.Color;

/**
 * Représente une case cible du plateau.
 */
public class CaseCible extends Case {
    /**
     * Construit une case cible.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     */
    public CaseCible(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estCible() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return true;
    }

    @Override
    public char getSymbole() {
        return '.';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#F4E285");
    }
}
