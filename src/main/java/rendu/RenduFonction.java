import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Utilitaires partagés pour le rendu des cases.
 */
public final class RenduFonction {
    private RenduFonction() {
    }

    /**
        * Dessine une texture si elle est présente, sinon remplit la case avec une couleur.
     *
        * @param gc contexte graphique JavaFX
        * @param texture texture à dessiner
        * @param couleurFond couleur de fond de repli
        * @param x position x en pixels
        * @param y position y en pixels
        * @param taille taille de la case en pixels
        * @return true si la texture a été dessinée, false sinon
     */
    public static boolean dessinerSpriteOuFond(
        GraphicsContext gc,
        Image texture,
        Color couleurFond,
        double x,
        double y,
        double taille
    ) {
        if (texture != null) {
            gc.drawImage(texture, x, y, taille, taille);
            return true;
        }

        gc.setFill(couleurFond);
        gc.fillRect(x, y, taille, taille);
        return false;
    }
}
