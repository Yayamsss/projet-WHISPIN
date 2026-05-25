import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu de la case mur avec la texture de mur.
 */
public class RenduCaseMur implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteMur = Animation.getBloc();
        if (RenduFonction.dessinerSpriteOuFond(gc, spriteMur, Color.web("#5D5D5D"), x, y, taille)) {
            return;
        }

        gc.setStroke(Color.web("#7A7A7A"));
        gc.setLineWidth(Math.max(1.0, taille * 0.03));
        gc.strokeLine(x + taille * 0.08, y + taille * 0.34, x + taille * 0.92, y + taille * 0.34);
        gc.strokeLine(x + taille * 0.08, y + taille * 0.66, x + taille * 0.92, y + taille * 0.66);
    }
}
