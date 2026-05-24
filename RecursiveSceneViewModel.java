import java.util.ArrayList;
import java.util.List;

/**
 * Construit des couches de rendu pour visualiser la navigation recursive.
 *
 * Cette classe est uniquement destinee a la presentation:
 * elle lit l'etat du multivers, puis calcule des valeurs visuelles
 * (scale/offset/opacite) sans modifier la logique metier.
 */
public final class RecursiveSceneViewModel {

    /**
     * Donnees d'une couche pretes a etre dessinees.
     */
    public static final class Layer {
        public final char worldId;
        public final Plateau plateau;
        public final double scale;
        public final double offsetX;
        public final double offsetY;
        public final double opacity;

        public Layer(char worldId, Plateau plateau, double scale,
                     double offsetX, double offsetY, double opacity) {
            this.worldId = worldId;
            this.plateau = plateau;
            this.scale = scale;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.opacity = opacity;
        }
    }

    private static final double MIN_SCALE = 0.55;
    private static final double SCALE_STEP = 0.08;
    private static final double PARENT_BASE_OPACITY = 0.50;
    private static final double PARENT_OPACITY_STEP = 0.08;
    private static final double MIN_PARENT_OPACITY = 0.20;
    private static final double OFFSET_RATIO = 0.02;

    /**
     * Construit les couches de rendu dans l'ordre parent -> courant.
     */
    public List<Layer> buildLayers(Multivers multivers, double width, double height) {
        if (multivers == null) {
            throw new IllegalArgumentException("multivers ne peut pas etre null");
        }

        double boundedWidth = Math.max(1.0, width);
        double boundedHeight = Math.max(1.0, height);
        double baseOffset = Math.min(boundedWidth, boundedHeight) * OFFSET_RATIO;

        List<Multivers.MondeView> path = multivers.getCheminVisible();
        List<Layer> layers = new ArrayList<>(path.size());

        int lastIndex = path.size() - 1;
        for (int i = 0; i < path.size(); i++) {
            Multivers.MondeView node = path.get(i);
            int distanceFromCurrent = lastIndex - i;

            double scale = clamp(MIN_SCALE, 1.0, 1.0 - (distanceFromCurrent * SCALE_STEP));
            double opacity = (distanceFromCurrent == 0)
                ? 1.0
                : clamp(MIN_PARENT_OPACITY, 1.0,
                    PARENT_BASE_OPACITY - ((distanceFromCurrent - 1) * PARENT_OPACITY_STEP));

            double offset = distanceFromCurrent * baseOffset;
            double offsetX = -offset;
            double offsetY = -offset;

            layers.add(new Layer(node.id, node.plateau, scale, offsetX, offsetY, opacity));
        }

        return layers;
    }

    private static double clamp(double min, double max, double value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
