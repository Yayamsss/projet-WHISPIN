import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * Rendu de la case boite avec sprite pluie (fallback arrosoir).
 */
public class RenduCaseBoite implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteFond = Animation.getHerbe();
        RenduFonction.dessinerSpriteOuFond(gc, spriteFond, Color.web("#EADCC8"), x, y, taille);

        Image spriteBoite = Animation.getRain();
        if (spriteBoite != null) {
            gc.drawImage(spriteBoite, x, y, taille, taille);
            return;
        }

        double corpsX = x + taille * 0.22;
        double corpsY = y + taille * 0.34;
        double corpsL = taille * 0.48;
        double corpsH = taille * 0.36;

        gc.setFill(Color.web("#57A3D6"));
        gc.fillRoundRect(corpsX, corpsY, corpsL, corpsH, taille * 0.10, taille * 0.10);

        gc.setStroke(Color.web("#2E6E99"));
        gc.setLineWidth(Math.max(2.0, taille * 0.04));
        gc.strokeRoundRect(corpsX, corpsY, corpsL, corpsH, taille * 0.10, taille * 0.10);

        gc.strokeArc(
            corpsX + corpsL * 0.16,
            corpsY - corpsH * 0.55,
            corpsL * 0.54,
            corpsH * 0.85,
            205,
            155,
            ArcType.OPEN
        );

        gc.setLineWidth(Math.max(2.0, taille * 0.035));
        gc.strokeLine(
            corpsX + corpsL,
            corpsY + corpsH * 0.36,
            x + taille * 0.86,
            y + taille * 0.47
        );
        gc.strokeLine(
            x + taille * 0.86,
            y + taille * 0.47,
            x + taille * 0.92,
            y + taille * 0.43
        );

        gc.setFill(Color.web("#3D7AA5"));
        gc.fillOval(corpsX + corpsL * 0.36, corpsY + corpsH * 0.26, corpsL * 0.28, corpsH * 0.32);

        gc.setFill(Color.web("#AEE3FF", 0.6));
        gc.fillOval(corpsX + corpsL * 0.16, corpsY + corpsH * 0.14, corpsL * 0.22, corpsH * 0.20);
    }
}
