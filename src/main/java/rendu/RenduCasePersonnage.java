import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu de la case personnage avec texture directionnelle.
 */
public class RenduCasePersonnage implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteSol = Animation.getHerbe();
        RenduFonction.dessinerSpriteOuFond(gc, spriteSol, Color.web("#E7F2D5"), x, y, taille);

        Image spriteJoueur = Animation.getPersonnage();
        if (spriteJoueur == null) {
            return;
        }
        gc.drawImage(spriteJoueur, x, y, taille, taille);
    }
}
