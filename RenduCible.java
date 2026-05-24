import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel d'une cible de Sokoban (objectif pour les boites).
 */
public final class RenduCible {
    private RenduCible() {
    }

    public static void dessiner(GraphicsContext gc, double x, double y, double taille, long maintenantNs, boolean gagne) {
        double centreX = x + taille * 0.5;
        double centreY = y + taille * 0.5;
        double rayonExterne = taille * 0.32;
        double rayonInterne = taille * 0.18;
        double rayonNoyau = taille * 0.07;
        double epaisseurAnneau = Math.max(2.0, taille * 0.06);

        // Halo de victoire léger
        if (gagne) {
            gc.setFill(Color.web("#a8e06a", 0.18));
            gc.fillOval(
                centreX - rayonExterne * 1.45,
                centreY - rayonExterne * 1.45,
                rayonExterne * 2.9,
                rayonExterne * 2.9
            );
        }

        // Anneau externe de cible
        gc.setStroke(Color.web("#f4e6a6"));
        gc.setLineWidth(epaisseurAnneau);
        gc.strokeOval(
            centreX - rayonExterne,
            centreY - rayonExterne,
            rayonExterne * 2,
            rayonExterne * 2
        );

        // Disque interne
        gc.setFill(Color.web("#d7c46e", 0.45));
        gc.fillOval(
            centreX - rayonInterne,
            centreY - rayonInterne,
            rayonInterne * 2,
            rayonInterne * 2
        );

        // Point central
        gc.setFill(Color.web("#f6f2d8"));
        gc.fillOval(
            centreX - rayonNoyau,
            centreY - rayonNoyau,
            rayonNoyau * 2,
            rayonNoyau * 2
        );
    }
}