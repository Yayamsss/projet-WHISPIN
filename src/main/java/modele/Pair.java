/**
 * Coordonnees de grille (ligne, colonne).
 */
public final class Pair {
    private final int first;
    private final int second;

    public Pair(int first, int second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pair
            && first == ((Pair) obj).first
            && second == ((Pair) obj).second;
    }

    @Override
    public int hashCode() {
        return 31 * first + second;
    }
}
