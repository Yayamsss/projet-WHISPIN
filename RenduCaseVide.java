import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu de la case vide.
 */
public class RenduCaseVide implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#E2DBBE"));
        gc.fillRect(x, y, taille, taille);
    }
}
