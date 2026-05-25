import javafx.scene.paint.Color;

/**
 * Représente une case de monde récursif.
 */
public class CaseMonde extends Case {
    private final char identifiantMonde;

    /**
     * Construit une case monde.
     *
     * @param x abscisse de la case
     * @param y ordonnée de la case
     * @param identifiantMonde lettre d'identification du monde
     */
    public CaseMonde(int x, int y, char identifiantMonde) {
        super(x, y);
        if (!Character.isLetter(identifiantMonde)) {
            throw new IllegalArgumentException("L'identifiant de monde doit être une lettre.");
        }
        this.identifiantMonde = Character.toUpperCase(identifiantMonde);
    }

    /**
     * Retourne l'identifiant alphabétique du monde ciblé.
     *
     * @return identifiant du monde
     */
    public char getIdentifiantMonde() {
        return identifiantMonde;
    }

    @Override
    public boolean estMonde() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return false;
    }

    @Override
    public char getSymbole() {
        return identifiantMonde;
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#9EC5FE");
    }
}