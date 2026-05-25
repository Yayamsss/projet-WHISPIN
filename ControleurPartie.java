import java.util.ArrayDeque;
import java.util.List;
import java.util.function.Supplier;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

/**
 * Controle le deplacement automatique du personnage apres clic sur une case.
 */
public final class ControleurPartie {
    private static final long DELAI_PAS_NS = 120_000_000L;

    private final Canvas canvas;
    private final Supplier<Boolean> modeJeuActif;
    private final Supplier<Boolean> transitionVictoire;
    private final Supplier<Case[][]> plateauSupplier;
    private final Supplier<LogiqueSokoban> moteurSupplier;
    private final Runnable rafraichirAffichage;
    private final Runnable onVictoire;
    private final AAsterix asterix;

    private final ArrayDeque<Direction> cheminAutomatique;
    private AnimationTimer animationDeplacementAutomatique;
    private long dernierPasAutomatiqueNs;

    public ControleurPartie(
        Canvas canvas,
        Supplier<Boolean> modeJeuActif,
        Supplier<Boolean> transitionVictoire,
        Supplier<Case[][]> plateauSupplier,
        Supplier<LogiqueSokoban> moteurSupplier,
        Runnable rafraichirAffichage,
        Runnable onVictoire
    ) {
        this.canvas = canvas;
        this.modeJeuActif = modeJeuActif;
        this.transitionVictoire = transitionVictoire;
        this.plateauSupplier = plateauSupplier;
        this.moteurSupplier = moteurSupplier;
        this.rafraichirAffichage = rafraichirAffichage;
        this.onVictoire = onVictoire;
        this.asterix = new AAsterix();
        this.cheminAutomatique = new ArrayDeque<>();
        this.dernierPasAutomatiqueNs = 0L;
    }

    public void installerGestionClic() {
        if (canvas != null) {
            canvas.setOnMouseClicked(event -> gererClicPlateau(event.getX(), event.getY()));
        }
    }

    public void arreterDeplacementAutomatique() {
        cheminAutomatique.clear();
        dernierPasAutomatiqueNs = 0L;
        if (animationDeplacementAutomatique != null) {
            animationDeplacementAutomatique.stop();
        }
    }

    private void gererClicPlateau(double pixelX, double pixelY) {
        if (!Boolean.TRUE.equals(modeJeuActif.get()) || Boolean.TRUE.equals(transitionVictoire.get())) {
            return;
        }

        Case[][] plateau = plateauSupplier.get();
        LogiqueSokoban moteur = moteurSupplier.get();
        if (plateau == null || moteur == null) {
            return;
        }

        int[] caseCliquee = convertirPixelVersCase(pixelX, pixelY, plateau);
        if (caseCliquee == null) {
            return;
        }

        int cibleX = caseCliquee[0];
        int cibleY = caseCliquee[1];
        Case caseCible = plateau[cibleY][cibleX];
        if (caseCible == null) {
            return;
        }

        boolean caseAccessible = caseCible.estTraversable()
            || caseCible.estPersonnageCible()
            || caseCible instanceof CasePersonnage;
        if (!caseAccessible) {
            return;
        }

        int[] positionJoueur = localiserJoueurPlateau(plateau);
        if (positionJoueur == null) {
            return;
        }

        if (positionJoueur[0] == cibleX && positionJoueur[1] == cibleY) {
            return;
        }

        List<Direction> chemin = trouverCheminVersCase(plateau, positionJoueur[0], positionJoueur[1], cibleX, cibleY);
        if (chemin == null || chemin.isEmpty()) {
            return;
        }

        arreterDeplacementAutomatique();
        cheminAutomatique.addAll(chemin);
        demarrerDeplacementAutomatique();
    }

    private int[] convertirPixelVersCase(double pixelX, double pixelY, Case[][] plateau) {
        if (plateau.length == 0 || plateau[0] == null || plateau[0].length == 0) {
            return null;
        }

        int lignes = plateau.length;
        int colonnes = plateau[0].length;
        double largeur = canvas.getWidth();
        double hauteur = canvas.getHeight();

        double tailleCase = Math.min(largeur / colonnes, hauteur / lignes);
        double origineX = (largeur - colonnes * tailleCase) / 2.0;
        double origineY = (hauteur - lignes * tailleCase) / 2.0;

        if (pixelX < origineX || pixelY < origineY) {
            return null;
        }

        int x = (int) ((pixelX - origineX) / tailleCase);
        int y = (int) ((pixelY - origineY) / tailleCase);
        if (x < 0 || y < 0 || y >= lignes || x >= colonnes) {
            return null;
        }
        return new int[] {x, y};
    }

    private int[] localiserJoueurPlateau(Case[][] plateau) {
        for (int y = 0; y < plateau.length; y++) {
            if (plateau[y] == null) {
                continue;
            }
            for (int x = 0; x < plateau[y].length; x++) {
                Case caseJeu = plateau[y][x];
                if (caseJeu == null) {
                    continue;
                }
                char symbole = caseJeu.getSymbole();
                if (symbole == '@' || symbole == '+') {
                    return new int[] {x, y};
                }
            }
        }
        return null;
    }

    private List<Direction> trouverCheminVersCase(Case[][] plateau, int departX, int departY, int cibleX, int cibleY) {
        int rows = plateau.length;
        int cols = plateau[0].length;
        int[][] grid = new int[rows][cols];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Case c = plateau[y][x];
                boolean traversable = c != null && (c.estTraversable() || c.estPersonnageCible() || c instanceof CasePersonnage);
                grid[y][x] = traversable ? 1 : 0;
            }
        }

        Pair src = new Pair(departY, departX);
        Pair dest = new Pair(cibleY, cibleX);
        List<Pair> path = asterix.aStarSearch(grid, rows, cols, src, dest);
        if (path == null) {
            return java.util.Collections.emptyList();
        }

        ArrayDeque<Direction> directions = new ArrayDeque<>();
        int currentY = departY;
        int currentX = departX;
        for (Pair next : path) {
            int ny = next.getFirst();
            int nx = next.getSecond();
            directions.add(directionEntre(currentX, currentY, nx, ny));
            currentX = nx;
            currentY = ny;
        }

        return new java.util.ArrayList<>(directions);
    }

    private static Direction directionEntre(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        if (dx == 1 && dy == 0) {
            return Direction.DROITE;
        }
        if (dx == -1 && dy == 0) {
            return Direction.GAUCHE;
        }
        if (dx == 0 && dy == 1) {
            return Direction.BAS;
        }
        if (dx == 0 && dy == -1) {
            return Direction.HAUT;
        }
        throw new IllegalArgumentException("Direction invalide pour le chemin automatique.");
    }

    private void demarrerDeplacementAutomatique() {
        if (cheminAutomatique.isEmpty()) {
            return;
        }

        if (animationDeplacementAutomatique == null) {
            animationDeplacementAutomatique = new AnimationTimer() {
                @Override
                public void handle(long maintenantNs) {
                    if (Boolean.TRUE.equals(transitionVictoire.get())
                        || !Boolean.TRUE.equals(modeJeuActif.get())
                        || moteurSupplier.get() == null
                        || cheminAutomatique.isEmpty()) {
                        arreterDeplacementAutomatique();
                        return;
                    }

                    if (dernierPasAutomatiqueNs != 0L && (maintenantNs - dernierPasAutomatiqueNs) < DELAI_PAS_NS) {
                        return;
                    }

                    Direction direction = cheminAutomatique.poll();
                    if (direction == null) {
                        arreterDeplacementAutomatique();
                        return;
                    }

                    LogiqueSokoban moteur = moteurSupplier.get();
                    Animation.orienterPersonnage(direction);
                    if (moteur.deplacer(direction)) {
                        Animation.avancerAnimationPersonnage(direction);
                        rafraichirAffichage.run();
                        if (moteur.estVictoire()) {
                            arreterDeplacementAutomatique();
                            onVictoire.run();
                            return;
                        }
                    } else {
                        arreterDeplacementAutomatique();
                        return;
                    }

                    dernierPasAutomatiqueNs = maintenantNs;
                    if (cheminAutomatique.isEmpty()) {
                        arreterDeplacementAutomatique();
                    }
                }
            };
        }

        dernierPasAutomatiqueNs = 0L;
        animationDeplacementAutomatique.start();
    }
}
