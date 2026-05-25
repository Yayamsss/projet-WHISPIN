import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu de la case cible en terre simple.
 */
public class RenduCaseCible implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteTerre = Animation.getTerre();
        RenduFonction.dessinerSpriteOuFond(gc, spriteTerre, Color.web("#B8814A"), x, y, taille);
    }
}
