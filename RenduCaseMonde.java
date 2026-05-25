import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import javafx.scene.image.Image;

/**
 * Rendu d'une case monde récursif.
 */
public class RenduCaseMonde implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteEau = Animation.getEau();
        RenduFonction.dessinerSpriteOuFond(gc, spriteEau, Color.web("#D6E8FF"), x, y, taille);

        double cadre = taille * 0.14;
        gc.setFill(Color.web("#5B8DEF"));
        gc.fillRoundRect(
            x + cadre,
            y + cadre,
            taille - cadre * 2,
            taille - cadre * 2,
            taille * 0.22,
            taille * 0.22
        );

        gc.setStroke(Color.web("#2E5AAD"));
        gc.setLineWidth(Math.max(2.0, taille * 0.05));
        gc.strokeRoundRect(
            x + cadre,
            y + cadre,
            taille - cadre * 2,
            taille - cadre * 2,
            taille * 0.22,
            taille * 0.22
        );

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", FontWeight.BOLD, Math.max(14.0, taille * 0.34)));
        gc.fillText("M", x + taille * 0.5, y + taille * 0.53);
    }
}