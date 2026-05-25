import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu de la case boite avec sprite pluie.
 */
public class RenduCaseBoite implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteFond = Animation.getHerbe();
        RenduFonction.dessinerSpriteOuFond(gc, spriteFond, Color.web("#EADCC8"), x, y, taille);

        Image spriteBoite = Animation.getRain();
        if (spriteBoite == null) {
            return;
        }
        gc.drawImage(spriteBoite, x, y, taille, taille);
    }
}
