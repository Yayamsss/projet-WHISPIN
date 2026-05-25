import javafx.scene.canvas.GraphicsContext;

/**
 * Dessine un plateau de jeu en s'appuyant sur les rendus de cases.
 */
public final class RenduPlateau {
    private RenduPlateau() {
    }

    /**
     * Dessine un plateau à partir d'une matrice de cases.
     *
     * @param gc contexte graphique JavaFX
     * @param plateau matrice de cases à dessiner
     * @param origineX position x de départ en pixels
     * @param origineY position y de départ en pixels
     * @param tailleCase taille d'une case en pixels
     */
    public static void dessinerPlateau(
        GraphicsContext gc,
        Case[][] plateau,
        double origineX,
        double origineY,
        double tailleCase
    ) {
        if (gc == null || plateau == null || tailleCase <= 0) {
            return;
        }

        for (int y = 0; y < plateau.length; y++) {
            if (plateau[y] == null) {
                continue;
            }
            for (int x = 0; x < plateau[y].length; x++) {
                Case caseJeu = plateau[y][x];
                if (caseJeu == null) {
                    continue;
                }

                double px = origineX + x * tailleCase;
                double py = origineY + y * tailleCase;
                FabriqueRenduCase.depuisCase(caseJeu).dessiner(gc, px, py, tailleCase);
            }
        }
    }
}
