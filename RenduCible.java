import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel de la cible sous forme de portail carré arrondi.
 */
public final class RenduCible {
    private RenduCible() {
    }

    public static void dessiner(GraphicsContext gc, double x, double y, double taille, long maintenantNs, boolean gagne) {
        // Cible = carré contour blanc (ghost box) avec deux points noirs
        double marge = taille * 0.12;
        double s     = taille - marge * 2;
        double coin  = Math.max(5.0, s * 0.16);
        double bord  = Math.max(2.0, taille * 0.055);

        // Fond très légèrement teinté si gagné
        if (gagne) {
            gc.setFill(Color.web("#a8e06a", 0.25));
            gc.fillRoundRect(x + marge, y + marge, s, s, coin, coin);
        }

        // Contour blanc épais
        gc.setStroke(Color.web("#d8d8d8"));
        gc.setLineWidth(bord);
        gc.strokeRoundRect(x + marge + bord / 2.0, y + marge + bord / 2.0,
                           s - bord, s - bord, coin, coin);

        // Deux petits points blancs (ghost eyes)
        double dotR = Math.max(2.0, taille * 0.045);
        double cx   = x + taille * 0.5;
        double cy   = y + taille * 0.52;
        double ecart = taille * 0.14;
        gc.setFill(Color.web("#c0c0c0"));
        gc.fillOval(cx - ecart - dotR, cy - dotR, dotR * 2, dotR * 2);
        gc.fillOval(cx + ecart - dotR, cy - dotR, dotR * 2, dotR * 2);
    }
}