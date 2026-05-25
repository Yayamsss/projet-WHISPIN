import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Utilitaire de rendu de ruche.
 */
public final class RenduRuche {
    /**
     * Géométrie calculée de la ruche rendue.
     */
    public static final class Geometrie {
        private final double x;
        private final double y;
        private final double largeur;
        private final double hauteur;

        private Geometrie(double x, double y, double largeur, double hauteur) {
            this.x = x;
            this.y = y;
            this.largeur = largeur;
            this.hauteur = hauteur;
        }

        public double getEntreeX() {
            return x + largeur * 0.50;
        }

        public double getEntreeY() {
            return y + hauteur * 0.52;
        }
    }

    private RenduRuche() {
    }

    /**
     * Dessine une ruche standard et retourne sa géométrie utile.
     */
    public static Geometrie dessiner(GraphicsContext gc, double x, double y, double taille) {
        double rucheX = x + taille * 0.22;
        double rucheY = y + taille * 0.24;
        double rucheL = taille * 0.56;
        double rucheH = taille * 0.56;

        gc.setFill(Color.web("#E4A948"));
        gc.fillRoundRect(rucheX, rucheY, rucheL, rucheH, taille * 0.18, taille * 0.18);

        gc.setStroke(Color.web("#C1842E"));
        gc.setLineWidth(Math.max(2.0, taille * 0.04));
        gc.strokeRoundRect(rucheX, rucheY, rucheL, rucheH, taille * 0.18, taille * 0.18);
        gc.strokeLine(rucheX + rucheL * 0.06, rucheY + rucheH * 0.28, rucheX + rucheL * 0.94, rucheY + rucheH * 0.28);
        gc.strokeLine(rucheX + rucheL * 0.06, rucheY + rucheH * 0.47, rucheX + rucheL * 0.94, rucheY + rucheH * 0.47);
        gc.strokeLine(rucheX + rucheL * 0.06, rucheY + rucheH * 0.66, rucheX + rucheL * 0.94, rucheY + rucheH * 0.66);

        gc.setFill(Color.web("#2B1A08"));
        gc.fillOval(rucheX + rucheL * 0.40, rucheY + rucheH * 0.44, rucheL * 0.20, rucheH * 0.18);

        return new Geometrie(rucheX, rucheY, rucheL, rucheH);
    }
}