import javafx.scene.paint.Color;

/**
 * Représente une case générique du plateau.
 *
 * <p>Chaque type concret de case (mur, vide, boîte, personnage) spécialise
 * ce comportement en redéfinissant les méthodes nécessaires.</p>
 */
public class Case {
    private final int x;
    private final int y;

    /**
     * Construit une case à une position donnée.
     *
     * @param x coordonnée en abscisse (>= 0)
     * @param y coordonnée en ordonnée (>= 0)
     * @throws IllegalArgumentException si une coordonnée est négative
     */
    public Case(int x, int y) {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Les coordonnées doivent être positives.");
        }
        this.x = x;
        this.y = y;
    }

    /**
     * Indique si la case est un mur.
     *
     * @return true si la case est un mur, false sinon
     */
    public boolean estMur() {
        return false;
    }

    /**
     * Indique si la case contient une boîte.
     *
     * @return true si la case contient une boîte, false sinon
     */
    public boolean estBoite() {
        return false;
    }

    /**
     * Indique si la case est vide.
     *
     * @return true si la case est vide, false sinon
     */
    public boolean estVide() {
        return false;
    }

    /**
     * Indique si la case est une cible.
     *
     * @return true si la case est une cible, false sinon
     */
    public boolean estCible() {
        return false;
    }

    /**
     * Indique si la case représente un monde récursif.
     *
     * @return true si la case est un monde récursif, false sinon
     */
    public boolean estMonde() {
        return false;
    }

    /**
     * Indique si la case contient un personnage (potentiellement sur cible).
     *
     * @return true si la case contient le personnage, false sinon
     */
    public boolean estPersonnageCible() {
        return false;
    }

    /**
     * Indique si la case contient une boîte sur une cible.
     *
     * @return true si la boîte est sur une cible, false sinon
     */
    public boolean estBoiteCible() {
        return false;
    }

    /**
     * Indique si la case peut être traversée.
     *
     * @return true si traversable, false sinon
     */
    public boolean estTraversable() {
        return false;
    }

    /**
     * Indique si la case peut être poussée.
     *
     * @return true si la case est poussable, false sinon
     */
    public boolean estPoussable() {
        return false;
    }

    /**
     * Retourne le symbole ASCII associé à la case.
     *
     * @return symbole ASCII de la case
     */
    public char getSymbole() {
        return ' ';
    }

    /**
     * Retourne la couleur de fond utilisée pour l'affichage de la case.
     *
     * @return couleur de sol de la case
     */
    public Color getCouleurSol() {
        return Color.web("#d62828");
    }

    /**
     * Retourne l'abscisse de la case.
     *
     * @return abscisse de la case
     */
    public int getX() {
        return x;
    }

    /**
     * Retourne l'ordonnée de la case.
     *
     * @return ordonnée de la case
     */
    public int getY() {
        return y;
    }
}