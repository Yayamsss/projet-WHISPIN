import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu d'une boîte-monde sur cible avec la texture de portail.
 */
public class RenduCaseBoiteMondeSurCible implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spritePortal = Animation.getPortal();
        RenduFonction.dessinerSpriteOuFond(gc, spritePortal, Color.web("#6C63FF"), x, y, taille);
    }
}
