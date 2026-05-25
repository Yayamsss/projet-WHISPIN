import javafx.scene.paint.Color;

/**
 * Représente une boîte spéciale qui pointe vers un sous-monde.
 */
public class CaseBoiteMonde extends CaseBoite {
    private final char identifiantMonde;

    /**
     * Construit une case boîte-monde.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     * @param identifiantMonde lettre d'identification du monde
     */
    public CaseBoiteMonde(int x, int y, char identifiantMonde) {
        super(x, y);
        if (!Character.isLetter(identifiantMonde)) {
            throw new IllegalArgumentException("L'identifiant de monde doit être une lettre.");
        }
        this.identifiantMonde = Character.toUpperCase(identifiantMonde);
    }

    /**
     * Retourne l'identifiant alphabétique du monde ciblé.
     */
    public char getIdentifiantMonde() {
        return identifiantMonde;
    }

    @Override
    public boolean estMonde() {
        return true;
    }

    @Override
    public char getSymbole() {
        return Character.toLowerCase(identifiantMonde);
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#9EC5FE");
    }
}
