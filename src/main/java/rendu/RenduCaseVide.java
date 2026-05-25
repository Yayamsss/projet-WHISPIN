import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu de la case vide.
 */
public class RenduCaseVide implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteSol = Animation.getHerbe();
        RenduFonction.dessinerSpriteOuFond(gc, spriteSol, Color.web("#E2DBBE"), x, y, taille);
    }
}
