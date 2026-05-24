import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel de la boîte en style cube arrondi.
 */
public final class RenduBoite {
    private RenduBoite() {
    }

    public static void dessiner(
        GraphicsContext gc,
        double x,
        double y,
        double taille,
        boolean surCible,
        long surCibleDepuisNs,
        ControleurAnimation controleurAnimation,
        long maintenantNs
    ) {
        dessinerCubeArrondi(gc, x, y, taille, surCible);
    }

    private static void dessinerCubeArrondi(GraphicsContext gc, double x, double y, double taille, boolean surCible) {
        double coin = Math.max(6.0, taille * 0.18);
        double bord = Math.max(1.5, taille * 0.055);
        double h2   = bord / 2.0;

        // Boîte normale : orange vif ; sur cible : vert clair
        Color base = surCible ? Color.web("#a8e06a") : Color.web("#e87c2a");

        // Fill inseté de bord/2 pour ne pas déborder hors du contour
        gc.setFill(base);
        gc.fillRoundRect(x + h2, y + h2, taille - bord, taille - bord, coin, coin);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(bord);
        gc.strokeRoundRect(x + h2, y + h2, taille - bord, taille - bord, coin, coin);
    }
}