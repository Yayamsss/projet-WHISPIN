import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu de la case mur sous forme d'arbres.
 */
public class RenduCaseMur implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#BFD8A5"));
        gc.fillRect(x, y, taille, taille);

        dessinerArbre(gc, x + taille * 0.30, y + taille * 0.72, taille * 0.22);
        dessinerArbre(gc, x + taille * 0.66, y + taille * 0.75, taille * 0.20);
    }

    private void dessinerArbre(GraphicsContext gc, double centreX, double baseY, double largeurTronc) {
        double hauteurTronc = largeurTronc * 1.7;
        double rayonFeuillage = largeurTronc * 1.7;

        gc.setFill(Color.web("#7C4A21"));
        gc.fillRect(
            centreX - largeurTronc / 2,
            baseY - hauteurTronc,
            largeurTronc,
            hauteurTronc
        );

        gc.setFill(Color.web("#2E7D32"));
        gc.fillOval(
            centreX - rayonFeuillage,
            baseY - hauteurTronc - rayonFeuillage * 1.25,
            rayonFeuillage * 2,
            rayonFeuillage * 1.9
        );
    }
}
