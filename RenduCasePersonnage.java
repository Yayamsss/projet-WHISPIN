import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Rendu de la case personnage avec sprite directionnel (fallback abeille).
 */
public class RenduCasePersonnage implements RenduCase {
    @Override
    public void dessiner(GraphicsContext gc, double x, double y, double taille) {
        Image spriteSol = Animation.getHerbe();
        RenduFonction.dessinerSpriteOuFond(gc, spriteSol, Color.web("#E7F2D5"), x, y, taille);

        Image spriteJoueur = Animation.getPersonnage();
        if (spriteJoueur != null) {
            gc.drawImage(spriteJoueur, x, y, taille, taille);
            return;
        }

        double corpsX = x + taille * 0.26;
        double corpsY = y + taille * 0.36;
        double corpsL = taille * 0.48;
        double corpsH = taille * 0.30;

        gc.setFill(Color.web("#F9C62E"));
        gc.fillOval(corpsX, corpsY, corpsL, corpsH);

        gc.setFill(Color.web("#1F1F1F"));
        gc.fillRect(corpsX + corpsL * 0.20, corpsY + corpsH * 0.08, corpsL * 0.10, corpsH * 0.84);
        gc.fillRect(corpsX + corpsL * 0.43, corpsY + corpsH * 0.08, corpsL * 0.10, corpsH * 0.84);
        gc.fillRect(corpsX + corpsL * 0.66, corpsY + corpsH * 0.08, corpsL * 0.10, corpsH * 0.84);

        gc.setFill(Color.web("#BEE7FF", 0.75));
        gc.fillOval(x + taille * 0.30, y + taille * 0.18, taille * 0.18, taille * 0.15);
        gc.fillOval(x + taille * 0.50, y + taille * 0.18, taille * 0.18, taille * 0.15);

        gc.setFill(Color.web("#FFFFFF"));
        double oeil = taille * 0.045;
        gc.fillOval(corpsX + corpsL * 0.73, corpsY + corpsH * 0.28, oeil, oeil);
        gc.fillOval(corpsX + corpsL * 0.73, corpsY + corpsH * 0.58, oeil, oeil);
    }
}
