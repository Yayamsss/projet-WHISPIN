import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Utilitaires partages pour le rendu des cases.
 */
public final class RenduFonction {
    private RenduFonction() {
    }

    /**
     * Dessine un sprite si present, sinon remplit la case avec une couleur.
     *
     * @return true si le sprite a ete dessine, false sinon
     */
    public static boolean dessinerSpriteOuFond(
        GraphicsContext gc,
        Image sprite,
        Color couleurFond,
        double x,
        double y,
        double taille
    ) {
        if (sprite != null) {
            gc.drawImage(sprite, x, y, taille, taille);
            return true;
        }

        gc.setFill(couleurFond);
        gc.fillRect(x, y, taille, taille);
        return false;
    }
}
