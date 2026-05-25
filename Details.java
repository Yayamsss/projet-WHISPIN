/**
 * Entree de la liste ouverte de A*.
 */
public final class Details {
    private final double value;
    private final int i;
    private final int j;

    public Details(double value, int i, int j) {
        this.value = value;
        this.i = i;
        this.j = j;
    }

    public double getValue() {
        return value;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
