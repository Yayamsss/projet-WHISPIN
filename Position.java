/**
 * Représente une position (x, y) sur le plateau.
 */
public class Position {

    private final int x;
    private final int y;

    /**
     * Construit une position immuable.
     *
     * @param x abscisse
     * @param y ordonnée
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retourne l'abscisse.
     *
     * @return abscisse
     */
    public int getX() {
        return x;
    }

    /**
     * Retourne l'ordonnée.
     *
     * @return ordonnée
     */
    public int getY() {
        return y;
    }

    /**
     * Retourne une nouvelle position déplacée dans une direction donnée.
     *
     * @param direction direction de déplacement
     * @return nouvelle position déplacée
     * @throws IllegalArgumentException si la direction est nulle
     */
    public Position deplacer(Direction direction) {
        if (direction == null) {
            throw new IllegalArgumentException("La direction ne peut pas être nulle.");
        }
        return new Position(x + direction.getDeltaX(), y + direction.getDeltaY());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Position)) {
            return false;
        }
        Position autre = (Position) obj;
        return x == autre.x && y == autre.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
