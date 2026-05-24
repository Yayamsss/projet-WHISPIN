import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel des murs en blocs massifs arrondis.
 */
public final class RenduMur {
    private RenduMur() {
    }

    public static void dessiner(GraphicsContext gc, double x, double y, double taille) {
        double bord = Math.max(1.5, taille * 0.045);
        double h2   = bord / 2.0;
        double coin = Math.max(4.0, taille * 0.12);

        // Mur : gris très sombre, clairement distinct du champ de jeu
        gc.setFill(Color.web("#2a2a2a"));
        gc.fillRoundRect(x + h2, y + h2, taille - bord, taille - bord, coin, coin);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(bord);
        gc.strokeRoundRect(x + h2, y + h2, taille - bord, taille - bord, coin, coin);
    }
}