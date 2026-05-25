import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu de la case boîte sous forme de fleur.
 */
public class RenduCaseBoite implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#EADCC8"));
        gc.fillRect(x, y, taille, taille);

        double centreX = x + taille * 0.5;
        double centreY = y + taille * 0.42;
        double rayonPetale = taille * 0.12;

        gc.setStroke(Color.web("#5A8F3A"));
        gc.setLineWidth(Math.max(2.0, taille * 0.06));
        gc.strokeLine(centreX, y + taille * 0.92, centreX, centreY + taille * 0.1);

        gc.setFill(Color.web("#FF7AA2"));
        for (int i = 0; i < 6; i++) {
            double angle = (Math.PI * 2.0 * i) / 6.0;
            double px = centreX + Math.cos(angle) * taille * 0.16;
            double py = centreY + Math.sin(angle) * taille * 0.16;
            gc.fillOval(px - rayonPetale, py - rayonPetale, rayonPetale * 2, rayonPetale * 2);
        }

        gc.setFill(Color.web("#F8D34A"));
        double coeur = taille * 0.12;
        gc.fillOval(centreX - coeur / 2, centreY - coeur / 2, coeur, coeur);
    }
}
