import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel du sol (case vide).
 */
public final class RenduVide {
    private RenduVide() {
    }

    public static void dessiner(GraphicsContext gc, double x, double y, double taille) {
        // Le champ de jeu sombre est le fond — les cases vides ne dessinent rien.
    }
}