/**
 * Gere les transitions visuelles d'entree/sortie de monde.
 *
 * Le controleur est purement visuel: il n'impacte pas la logique de deplacement,
 * il ne fait qu'exposer des valeurs interpolees utilisables par le rendu.
 */
public final class RecursiveTransitionController {

    public enum TransitionType {
        ENTER,
        EXIT,
        NONE
    }

    private static final long DURATION_NS = 1_200_000_000L;

    private TransitionType type = TransitionType.NONE;
    private char fromWorld = '\0';
    private char toWorld = '\0';

    private long startNs = 0L;
    private double progress = 1.0;

    public void startEnter(char fromWorld, char toWorld, long nowNs) {
        this.type = TransitionType.ENTER;
        this.fromWorld = fromWorld;
        this.toWorld = toWorld;
        this.startNs = nowNs;
        this.progress = 0.0;
    }

    public void startExit(char fromWorld, char toWorld, long nowNs) {
        this.type = TransitionType.EXIT;
        this.fromWorld = fromWorld;
        this.toWorld = toWorld;
        this.startNs = nowNs;
        this.progress = 0.0;
    }

    public void update(long nowNs) {
        if (type == TransitionType.NONE) {
            progress = 1.0;
            return;
        }

        long elapsed = Math.max(0L, nowNs - startNs);
        double t = Math.min(1.0, elapsed / (double) DURATION_NS);
        progress = easeOutCubic(t);

        if (t >= 1.0) {
            type = TransitionType.NONE;
            progress = 1.0;
        }
    }

    public boolean isActive() {
        return type != TransitionType.NONE;
    }

    public double getProgress() {
        return progress;
    }

    /**
     * Facteur de scale a appliquer a la couche active.
     */
    public double getScaleLerp() {
        if (type == TransitionType.ENTER) {
            return lerp(0.88, 1.0, progress);
        }
        if (type == TransitionType.EXIT) {
            return lerp(1.12, 1.0, progress);
        }
        return 1.0;
    }

    public double getOffsetXLerp() {
        if (type == TransitionType.ENTER) {
            return lerp(26.0, 0.0, progress);
        }
        if (type == TransitionType.EXIT) {
            return lerp(-26.0, 0.0, progress);
        }
        return 0.0;
    }

    public double getOffsetYLerp() {
        if (type == TransitionType.ENTER) {
            return lerp(18.0, 0.0, progress);
        }
        if (type == TransitionType.EXIT) {
            return lerp(-18.0, 0.0, progress);
        }
        return 0.0;
    }

    public TransitionType getType() {
        return type;
    }

    public char getFromWorld() {
        return fromWorld;
    }

    public char getToWorld() {
        return toWorld;
    }

    private static double easeOutCubic(double t) {
        double u = 1.0 - t;
        return 1.0 - (u * u * u);
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}
