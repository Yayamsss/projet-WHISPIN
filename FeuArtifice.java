import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class FeuArtifice {
    private static final double DUREE_FEU_ARTIFICE_SECONDES = 4.0;
    private static final double DUREE_EXPLOSION_SECONDES = 1.1;
    private static final long INTERVALLE_EXPLOSION_NS = 190_000_000L;
    private static final int NOMBRE_ETINCELLES = 18;

    private static final class Explosion {
        private final double centreX;
        private final double centreY;
        private final double rayonMax;
        private final Color couleur;
        private final long debutNs;

        private Explosion(double centreX, double centreY, double rayonMax, Color couleur, long debutNs) {
            this.centreX = centreX;
            this.centreY = centreY;
            this.rayonMax = rayonMax;
            this.couleur = couleur;
            this.debutNs = debutNs;
        }
    }

    private final ArrayList<Explosion> explosions = new ArrayList<>();
    private long debutVictoireNs = 0L;
    private long dernierLancementExplosionNs = 0L;

    public void mettreAJour(boolean victoire, double largeurScene, double hauteurScene, long maintenantNs) {
        if (!victoire) {
            reinitialiser();
            return;
        }

        if (debutVictoireNs == 0L) {
            debutVictoireNs = maintenantNs;
            dernierLancementExplosionNs = maintenantNs - INTERVALLE_EXPLOSION_NS;
        }

        if (maintenantNs - dernierLancementExplosionNs >= INTERVALLE_EXPLOSION_NS) {
            ajouterExplosion(largeurScene, hauteurScene, maintenantNs);
            dernierLancementExplosionNs = maintenantNs;
        }

        explosions.removeIf(explosion -> progressionExplosion(explosion, maintenantNs) >= 1.0);
    }

    public boolean doitFermer(long maintenantNs) {
        if (debutVictoireNs == 0L) {
            return false;
        }
        double tempsVictoire = (maintenantNs - debutVictoireNs) / 1_000_000_000.0;
        return tempsVictoire >= DUREE_FEU_ARTIFICE_SECONDES;
    }

    public void dessiner(GraphicsContext gc, long maintenantNs) {
        for (Explosion explosion : explosions) {
            double progression = progressionExplosion(explosion, maintenantNs);
            if (progression >= 1.0) {
                continue;
            }

            double opacite = Math.max(0.0, 1.0 - progression);
            double rayon = explosion.rayonMax * progression;

            gc.setLineWidth(Math.max(1.0, rayon * 0.045));
            for (int i = 0; i < NOMBRE_ETINCELLES; i++) {
                double angle = (Math.PI * 2.0 * i) / NOMBRE_ETINCELLES;
                double x1 = explosion.centreX + Math.cos(angle) * (rayon * 0.35);
                double y1 = explosion.centreY + Math.sin(angle) * (rayon * 0.35);
                double x2 = explosion.centreX + Math.cos(angle) * rayon;
                double y2 = explosion.centreY + Math.sin(angle) * rayon;

                gc.setStroke(
                    Color.color(
                        explosion.couleur.getRed(),
                        explosion.couleur.getGreen(),
                        explosion.couleur.getBlue(),
                        opacite
                    )
                );
                gc.strokeLine(x1, y1, x2, y2);
            }

            double rayonNoyau = Math.max(2.0, rayon * 0.08);
            gc.setFill(Color.color(1.0, 1.0, 1.0, opacite * 0.9));
            gc.fillOval(
                explosion.centreX - rayonNoyau,
                explosion.centreY - rayonNoyau,
                rayonNoyau * 2,
                rayonNoyau * 2
            );
        }
    }

    private void ajouterExplosion(double largeurScene, double hauteurScene, long maintenantNs) {
        double largeur = Math.max(largeurScene, 1.0);
        double hauteur = Math.max(hauteurScene, 1.0);
        double centreX = largeur * (0.15 + Math.random() * 0.70);
        double centreY = hauteur * (0.12 + Math.random() * 0.45);
        double rayonMax = Math.min(largeur, hauteur) * (0.06 + Math.random() * 0.07);

        Color[] palette = new Color[] {
            Color.web("#ff595e"),
            Color.web("#ffca3a"),
            Color.web("#8ac926"),
            Color.web("#1982c4"),
            Color.web("#f15bb5")
        };
        int indexCouleur = (int) (Math.random() * palette.length);
        explosions.add(new Explosion(centreX, centreY, rayonMax, palette[indexCouleur], maintenantNs));
    }

    private static double progressionExplosion(Explosion explosion, long maintenantNs) {
        return Math.max(0.0, (maintenantNs - explosion.debutNs) / (DUREE_EXPLOSION_SECONDES * 1_000_000_000.0));
    }

    private void reinitialiser() {
        explosions.clear();
        debutVictoireNs = 0L;
        dernierLancementExplosionNs = 0L;
    }
}
