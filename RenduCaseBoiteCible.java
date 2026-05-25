import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu d'une case boite sur cible avec sprite tree.
 */
public class RenduCaseBoiteCible implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteArbre = Animation.getArbre();
        RenduFonction.dessinerSpriteOuFond(gc, spriteArbre, Color.web("#2E7D32"), x, y, taille);
    }
}