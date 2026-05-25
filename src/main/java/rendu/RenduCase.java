import javafx.scene.canvas.GraphicsContext;

/**
 * Définit le contrat de rendu d'une case.
 */
public interface RenduCase {
    /**
     * Dessine une case dans une zone carrée.
     *
     * @param gc contexte graphique JavaFX
     * @param x position x en pixels
     * @param y position y en pixels
     * @param taille taille de la case en pixels
     */
    void dessiner(GraphicsContext gc, double x, double y, double taille);
}
