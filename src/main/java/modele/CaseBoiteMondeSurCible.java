import javafx.scene.paint.Color;

/**
 * Représente une boîte-monde positionnée sur une cible.
 */
public class CaseBoiteMondeSurCible extends CaseBoiteMonde {
    /**
     * Construit une case boîte-monde sur cible.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     * @param identifiantMonde lettre d'identification du monde
     */
    public CaseBoiteMondeSurCible(int x, int y, char identifiantMonde) {
        super(x, y, identifiantMonde);
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
        return Character.toUpperCase(getIdentifiantMonde());
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#F3B8FF");
    }
}
