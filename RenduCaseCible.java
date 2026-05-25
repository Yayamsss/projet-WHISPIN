import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu de la case cible sous forme de ruche.
 */
public class RenduCaseCible implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#FFF3C4"));
        gc.fillRect(x, y, taille, taille);
        RenduRuche.dessiner(gc, x, y, taille);
    }
}
