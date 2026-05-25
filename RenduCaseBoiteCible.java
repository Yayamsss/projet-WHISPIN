import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu d'une case boîte sur cible: la ruche absorbe la boîte.
 */
public class RenduCaseBoiteCible implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        gc.setFill(Color.web("#FFF3C4"));
        gc.fillRect(x, y, taille, taille);
        RenduRuche.Geometrie geometrie = RenduRuche.dessiner(gc, x, y, taille);
        double entreeX = geometrie.getEntreeX();
        double entreeY = geometrie.getEntreeY();

        gc.setStroke(Color.web("#FF7AA2", 0.75));
        gc.setLineWidth(Math.max(1.5, taille * 0.03));
        gc.strokeLine(entreeX - taille * 0.18, entreeY - taille * 0.10, entreeX - taille * 0.05, entreeY - taille * 0.03);
        gc.strokeLine(entreeX - taille * 0.17, entreeY + taille * 0.03, entreeX - taille * 0.04, entreeY + taille * 0.01);
        gc.strokeLine(entreeX + taille * 0.18, entreeY - taille * 0.10, entreeX + taille * 0.05, entreeY - taille * 0.03);
        gc.strokeLine(entreeX + taille * 0.17, entreeY + taille * 0.03, entreeX + taille * 0.04, entreeY + taille * 0.01);

    }
}