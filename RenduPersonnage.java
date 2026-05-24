import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Rendu visuel du personnage en style minimaliste.
 * Cette classe contient uniquement de la logique d'affichage.
 */
public final class RenduPersonnage {
    private RenduPersonnage() {
    }

    public static void dessiner(
        GraphicsContext gc,
        double x,
        double y,
        double taille,
        boolean surCible,
        ControleurAnimation controleurAnimation,
        long maintenantNs
    ) {
        double cx = x + taille * 0.5;
        double cy = y + taille * 0.5;

        double temps = maintenantNs / 1_000_000_000.0;
        double ouvertureYeux = 0.24 + 0.10 * (0.5 + 0.5 * Math.sin(temps * 7.5));

        Direction direction = controleurAnimation.getDirectionRegard();
        dessinerAvatar(gc, cx, cy, taille, surCible, direction, ouvertureYeux);
    }

    private static void dessinerAvatar(
        GraphicsContext gc,
        double cx,
        double cy,
        double taille,
        boolean surCible,
        Direction direction,
        double ouvertureYeux
    ) {
        double corpsTaille = taille * 0.78;
        double coin = Math.max(6.0, corpsTaille * 0.18);
        double bord = Math.max(1.5, taille * 0.055);
        double h2   = bord / 2.0;
        double corpsX = cx - corpsTaille / 2.0;
        double corpsY = cy - corpsTaille / 2.0;

        // Magenta vif (Patrick's Parabox player color), vert si sur cible
        Color base = surCible ? Color.web("#88dd44") : Color.web("#dd44aa");

        // Fill inseté de bord/2 pour ne pas déborder hors du contour
        gc.setFill(base);
        gc.fillRoundRect(corpsX + h2, corpsY + h2, corpsTaille - bord, corpsTaille - bord, coin, coin);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(bord);
        gc.strokeRoundRect(corpsX + h2, corpsY + h2, corpsTaille - bord, corpsTaille - bord, coin, coin);

        // Yeux : deux ovales noirs animés (sinus)
        double regardX = 0.0;
        double regardY = 0.0;
        if (direction != null) {
            regardX = direction.getDeltaX() * corpsTaille * 0.06;
            regardY = direction.getDeltaY() * corpsTaille * 0.06;
        }
        // Rayon plus grand pour des yeux bien visibles
        double dotR  = Math.max(4.0, corpsTaille * 0.15) * ouvertureYeux;
        double oeilCy = cy + regardY * 0.5;
        double ecart = corpsTaille * 0.20;
        gc.setFill(Color.web("#111111"));
        gc.fillOval(cx - ecart + regardX - dotR, oeilCy - dotR, dotR * 2, dotR * 2);
        gc.fillOval(cx + ecart + regardX - dotR, oeilCy - dotR, dotR * 2, dotR * 2);
    }

    // Les yeux sont maintenant de simples points dessinés directement dans dessinerAvatar.
}