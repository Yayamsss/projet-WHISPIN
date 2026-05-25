/**
 * Moteur de logique Sokoban : déplacements, poussée de boîtes et victoire.
 */
public class MoteurSokoban {
    private static final int HISTORIQUE_MAX = 300;

    private static final class Instantane {
        private final char[][] grille;
        private final int joueurX;
        private final int joueurY;
        private final int coups;
        private final boolean victoire;

        private Instantane(char[][] grille, int joueurX, int joueurY, int coups, boolean victoire) {
            this.grille = grille;
            this.joueurX = joueurX;
            this.joueurY = joueurY;
            this.coups = coups;
            this.victoire = victoire;
        }
    }

    private char[][] grille;
    private int joueurX;
    private int joueurY;
    private int coups;
    private boolean victoire;
    private final java.util.ArrayList<Instantane> historique;

    /**
     * Construit un moteur à partir d'un plateau de cases.
     *
     * @param plateau plateau source
     */
    public MoteurSokoban(Case[][] plateau) {
        historique = new java.util.ArrayList<>();
        chargerPlateau(plateau);
    }

    /**
     * Recharge complètement le moteur avec un nouveau plateau.
     *
     * @param plateau plateau source
     */
    public final void chargerPlateau(Case[][] plateau) {
        grille = convertirCaseVersSymboles(plateau);
        coups = 0;
        localiserJoueur();
        victoire = verifierVictoire();
        historique.clear();
    }

    /**
     * Tente d'appliquer un déplacement.
     *
     * @param direction direction demandée
     * @return true si le déplacement a été appliqué, false sinon
     */
    public boolean deplacer(Direction direction) {
        if (direction == null || victoire || grille == null) {
            return false;
        }

        Instantane avantCoup = creerInstantane();

        int nx = joueurX + direction.getDeltaX();
        int ny = joueurY + direction.getDeltaY();
        if (!dansGrille(nx, ny)) {
            return false;
        }

        char destination = grille[ny][nx];
        if (destination == '#' ) {
            return false;
        }

        if (estBoite(destination)) {
            int bx = nx + direction.getDeltaX();
            int by = ny + direction.getDeltaY();
            if (!dansGrille(bx, by)) {
                return false;
            }

            char caseApresBoite = grille[by][bx];
            if (!estLibre(caseApresBoite)) {
                return false;
            }

            grille[by][bx] = (caseApresBoite == '.') ? '*' : '$';
            grille[ny][nx] = (destination == '*') ? '+' : '@';
        }

        if (estLibre(destination)) {
            grille[ny][nx] = (destination == '.') ? '+' : '@';
        }

        grille[joueurY][joueurX] = (grille[joueurY][joueurX] == '+') ? '.' : ' ';
        joueurX = nx;
        joueurY = ny;
        coups++;
        victoire = verifierVictoire();
        pousserHistorique(avantCoup);
        return true;
    }

    /**
     * Annule le dernier coup joué.
     *
     * @return true si un état précédent a été restauré, false sinon
     */
    public boolean annulerDernierCoup() {
        if (historique.isEmpty()) {
            return false;
        }

        Instantane precedent = historique.remove(historique.size() - 1);
        grille = copierGrille(precedent.grille);
        joueurX = precedent.joueurX;
        joueurY = precedent.joueurY;
        coups = precedent.coups;
        victoire = precedent.victoire;
        return true;
    }

    /**
     * Retourne le plateau courant sous forme d'objets Case.
     */
    public Case[][] exporterPlateau() {
        if (grille == null || grille.length == 0) {
            return null;
        }

        int hauteur = grille.length;
        int largeur = grille[0].length;
        Case[][] plateau = new Case[hauteur][largeur];
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                plateau[y][x] = convertirSymboleVersCase(grille[y][x], x, y);
            }
        }
        return plateau;
    }

    /**
     * Retourne le nombre de coups joués.
     */
    public int getCoups() {
        return coups;
    }

    /**
     * Indique si le niveau est terminé.
     */
    public boolean estVictoire() {
        return victoire;
    }

    private void pousserHistorique(Instantane instantane) {
        historique.add(instantane);
        if (historique.size() > HISTORIQUE_MAX) {
            historique.remove(0);
        }
    }

    private Instantane creerInstantane() {
        return new Instantane(copierGrille(grille), joueurX, joueurY, coups, victoire);
    }

    private static char[][] copierGrille(char[][] source) {
        if (source == null) {
            return null;
        }

        char[][] copie = new char[source.length][];
        for (int y = 0; y < source.length; y++) {
            copie[y] = source[y] == null ? null : source[y].clone();
        }
        return copie;
    }

    private void localiserJoueur() {
        joueurX = -1;
        joueurY = -1;
        for (int y = 0; y < grille.length; y++) {
            for (int x = 0; x < grille[y].length; x++) {
                if (grille[y][x] == '@' || grille[y][x] == '+') {
                    joueurX = x;
                    joueurY = y;
                    return;
                }
            }
        }
    }

    private boolean verifierVictoire() {
        for (char[] ligne : grille) {
            for (char c : ligne) {
                if (c == '$') {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean estLibre(char c) {
        return c == ' ' || c == '.';
    }

    private boolean estBoite(char c) {
        return c == '$' || c == '*';
    }

    private boolean dansGrille(int x, int y) {
        return y >= 0 && y < grille.length && x >= 0 && x < grille[y].length;
    }

    private static char[][] convertirCaseVersSymboles(Case[][] plateau) {
        if (plateau == null || plateau.length == 0 || plateau[0] == null) {
            return new char[0][0];
        }

        int hauteur = plateau.length;
        int largeur = plateau[0].length;
        char[][] symboles = new char[hauteur][largeur];
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case caseJeu = plateau[y][x];
                symboles[y][x] = ConvertisseurCases.versSymbole(caseJeu);
            }
        }
        return symboles;
    }

    private static Case convertirSymboleVersCase(char c, int x, int y) {
        return ConvertisseurCases.depuisSymbole(c, x, y);
    }
}
