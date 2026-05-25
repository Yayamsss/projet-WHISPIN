/**
 * Moteur de logique Sokoban : déplacements, poussée de boîtes et victoire.
 */
public class LogiqueSokoban {
    // Limite mémoire volontaire pour l'annulation: assez large pour jouer confortablement,
    // sans conserver un historique infini de grilles potentiellement récursives.
    private static final int HISTORIQUE_MAX = 300;

    private enum ResultatResolution {
        ECHEC,
        CONTINUER,
        FINALISE
    }

    private static final class ContexteRecursif {
        private final char mondeParent;
        private final int xRetour;
        private final int yRetour;

        private ContexteRecursif(char mondeParent, int xRetour, int yRetour) {
            this.mondeParent = mondeParent;
            this.xRetour = xRetour;
            this.yRetour = yRetour;
        }
    }

    private static final class Instantane {
        private final java.util.LinkedHashMap<Character, char[][]> mondes;
        private final char identifiantRacine;
        private final char mondeActuel;
        private final java.util.ArrayList<ContexteRecursif> pileMonde;
        private final int coups;
        private final boolean victoire;
        private final String sequenceCoups;

        private Instantane(
            java.util.LinkedHashMap<Character, char[][]> mondes,
            char identifiantRacine,
            char mondeActuel,
            java.util.ArrayList<ContexteRecursif> pileMonde,
            int coups,
            boolean victoire,
            String sequenceCoups
        ) {
            this.mondes = mondes;
            this.identifiantRacine = identifiantRacine;
            this.mondeActuel = mondeActuel;
            this.pileMonde = pileMonde;
            this.coups = coups;
            this.victoire = victoire;
            this.sequenceCoups = sequenceCoups;
        }
    }

    private static final class HistoriqueInstantanes {
        private final java.util.ArrayList<Instantane> elements = new java.util.ArrayList<>();

        private void vider() {
            elements.clear();
        }

        private boolean estVide() {
            return elements.isEmpty();
        }

        private void pousser(Instantane instantane) {
            elements.add(instantane);
            if (elements.size() > HISTORIQUE_MAX) {
                elements.remove(0);
            }
        }

        private Instantane extraireDernier() {
            if (elements.isEmpty()) {
                return null;
            }
            return elements.remove(elements.size() - 1);
        }
    }

    private final class ResolveurMouvement {
        private boolean resoudre(Direction direction, Instantane avantCoup) {
            char[][] grille = grilleCourante();
            int[] positionJoueur = localiserJoueur(grille);
            if (positionJoueur == null) {
                return false;
            }

            int joueurX = positionJoueur[0];
            int joueurY = positionJoueur[1];

            int nx = joueurX + direction.getDeltaX();
            int ny = joueurY + direction.getDeltaY();
            if (!dansGrille(nx, ny)) {
                return essayerSortiePuisFinalisation(joueurX, joueurY, direction, avantCoup);
            }

            char destination = grille[ny][nx];
            if (destination == '#') {
                return essayerSortieDepuisMurBordurePuisFinalisation(
                    grille,
                    nx,
                    ny,
                    joueurX,
                    joueurY,
                    direction,
                    avantCoup
                );
            }

            if (estBoiteMondeSurCible(destination)) {
                return essayerEntreeMondePuisFinalisation(destination, joueurX, joueurY, direction, avantCoup);
            }

            if (estObjetPoussable(destination)) {
                ResultatResolution resultatObjet =
                    gererPousseeOuEntreeMonde(grille, destination, nx, ny, joueurX, joueurY, direction, avantCoup);
                if (resultatObjet == ResultatResolution.ECHEC) {
                    return false;
                }
                if (resultatObjet == ResultatResolution.FINALISE) {
                    return true;
                }
            } else if (estLibre(destination)) {
                grille[ny][nx] = (destination == '.') ? '+' : '@';
            } else {
                return false;
            }

            grille[joueurY][joueurX] = (grille[joueurY][joueurX] == '+') ? '.' : ' ';
            return finaliserDeplacement(direction, avantCoup);
        }

        private boolean essayerSortiePuisFinalisation(int joueurX, int joueurY, Direction direction, Instantane avantCoup) {
            if (!essayerSortirVersParent(joueurX, joueurY)) {
                return false;
            }
            return finaliserDeplacement(direction, avantCoup);
        }

        private boolean essayerSortieDepuisMurBordurePuisFinalisation(
            char[][] grille,
            int nx,
            int ny,
            int joueurX,
            int joueurY,
            Direction direction,
            Instantane avantCoup
        ) {
            if (!peutSortirVersParent() || !estMurDeBordure(grille, nx, ny)) {
                return false;
            }
            if (!essayerSortirVersParent(joueurX, joueurY)) {
                return false;
            }
            return finaliserDeplacement(direction, avantCoup);
        }

        private boolean essayerEntreeMondePuisFinalisation(
            char destination,
            int joueurX,
            int joueurY,
            Direction direction,
            Instantane avantCoup
        ) {
            if (!essayerEntrerDansMonde(destination, joueurX, joueurY)) {
                return false;
            }
            return finaliserDeplacement(direction, avantCoup);
        }

        private ResultatResolution gererPousseeOuEntreeMonde(
            char[][] grille,
            char destination,
            int nx,
            int ny,
            int joueurX,
            int joueurY,
            Direction direction,
            Instantane avantCoup
        ) {
            int bx = nx + direction.getDeltaX();
            int by = ny + direction.getDeltaY();
            if (dansGrille(bx, by) && estLibre(grille[by][bx])) {
                char caseApresObjet = grille[by][bx];
                grille[by][bx] = convertirObjetPousse(destination, caseApresObjet);
                grille[ny][nx] = estCelluleSupportCible(destination) ? '+' : '@';
                return ResultatResolution.CONTINUER;
            }

            if (!estMonde(destination)) {
                return ResultatResolution.ECHEC;
            }

            return essayerEntreeMondePuisFinalisation(destination, joueurX, joueurY, direction, avantCoup)
                ? ResultatResolution.FINALISE
                : ResultatResolution.ECHEC;
        }
    }

    private java.util.LinkedHashMap<Character, char[][]> mondes;
    private char identifiantRacine;
    private char mondeActuel;
    private java.util.ArrayList<ContexteRecursif> pileMonde;
    private int coups;
    private boolean victoire;
    private StringBuilder sequenceCoups;
    private final HistoriqueInstantanes historique;
    private final ResolveurMouvement resolveurMouvement;

    /**
     * Construit un moteur à partir d'un plateau de cases.
     *
     * @param plateau plateau source
     */
    public LogiqueSokoban(Case[][] plateau) {
        historique = new HistoriqueInstantanes();
        resolveurMouvement = new ResolveurMouvement();
        mondes = new java.util.LinkedHashMap<>();
        pileMonde = new java.util.ArrayList<>();
        sequenceCoups = new StringBuilder();
        chargerPlateau(plateau);
    }

    /**
     * Construit un moteur à partir d'un niveau récursif complet.
     */
    public LogiqueSokoban(ChargeurNiveau.NiveauCharge niveau) {
        historique = new HistoriqueInstantanes();
        resolveurMouvement = new ResolveurMouvement();
        mondes = new java.util.LinkedHashMap<>();
        pileMonde = new java.util.ArrayList<>();
        sequenceCoups = new StringBuilder();
        chargerNiveauRecursif(niveau);
    }

    /**
     * Recharge complètement le moteur avec un nouveau plateau.
     *
     * @param plateau plateau source
     */
    public final void chargerPlateau(Case[][] plateau) {
        mondes.clear();
        pileMonde.clear();

        char[][] grille = convertirCaseVersSymboles(plateau);
        identifiantRacine = 'A';
        mondeActuel = identifiantRacine;
        mondes.put(identifiantRacine, grille);
        reinitialiserEtatPartie();
    }

    /**
     * Recharge complètement le moteur avec un niveau récursif.
     */
    public final void chargerNiveauRecursif(ChargeurNiveau.NiveauCharge niveau) {
        mondes.clear();
        pileMonde.clear();

        if (niveau == null || niveau.getMondes() == null || niveau.getMondes().isEmpty()) {
            identifiantRacine = 'A';
            mondeActuel = identifiantRacine;
            mondes.put(identifiantRacine, new char[0][0]);
        } else {
            identifiantRacine = Character.toUpperCase(niveau.getIdentifiantRacine());
            for (java.util.Map.Entry<Character, Case[][]> entree : niveau.getMondes().entrySet()) {
                char identifiant = Character.toUpperCase(entree.getKey());
                mondes.put(identifiant, convertirCaseVersSymboles(entree.getValue()));
            }

            if (!mondes.containsKey(identifiantRacine)) {
                identifiantRacine = mondes.keySet().iterator().next();
            }
            mondeActuel = identifiantRacine;
        }
        reinitialiserEtatPartie();
    }

    /**
     * Tente d'appliquer un déplacement.
     *
     * @param direction direction demandée
     * @return true si le déplacement a été appliqué, false sinon
     */
    public boolean deplacer(Direction direction) {
        if (direction == null || victoire || mondes.isEmpty()) {
            return false;
        }

        Instantane avantCoup = creerInstantane();

        return resolveurMouvement.resoudre(direction, avantCoup);
    }

    private void reinitialiserEtatPartie() {
        coups = 0;
        victoire = verifierVictoire();
        sequenceCoups.setLength(0);
        historique.vider();
    }

    private boolean finaliserDeplacement(Direction direction, Instantane avantCoup) {
        coups++;
        sequenceCoups.append(directionVersSymbole(direction));
        victoire = verifierVictoire();
        historique.pousser(avantCoup);
        return true;
    }

    /**
     * Annule le dernier coup joué.
     *
     * @return true si un état précédent a été restauré, false sinon
     */
    public boolean annulerDernierCoup() {
        if (historique.estVide()) {
            return false;
        }

        Instantane precedent = historique.extraireDernier();
        if (precedent == null) {
            return false;
        }
        mondes = copierMondes(precedent.mondes);
        identifiantRacine = precedent.identifiantRacine;
        mondeActuel = precedent.mondeActuel;
        pileMonde = copierPile(precedent.pileMonde);
        coups = precedent.coups;
        victoire = precedent.victoire;
        sequenceCoups = new StringBuilder(precedent.sequenceCoups == null ? "" : precedent.sequenceCoups);
        return true;
    }

    /**
     * Retourne le plateau courant sous forme d'objets Case.
     */
    public Case[][] exporterPlateau() {
        char[][] grille = grilleCourante();
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
        * Retourne les coups au format solution Sokobano (compression RLE simple).
     */
    public String exporterCoupsSokobano() {
        if (sequenceCoups == null || sequenceCoups.length() == 0) {
            return "";
        }

        StringBuilder resultat = new StringBuilder();
        int i = 0;
        while (i < sequenceCoups.length()) {
            char mouvement = sequenceCoups.charAt(i);
            int j = i + 1;
            while (j < sequenceCoups.length() && sequenceCoups.charAt(j) == mouvement) {
                j++;
            }

            int repetition = j - i;
            if (repetition > 1) {
                resultat.append(repetition);
            }
            resultat.append(mouvement);
            i = j;
        }

        return resultat.toString();
    }

    /**
        * Rejoue une séquence de coups encodés au format Sokobano (RLE simple, ex: 3r2u).
     *
        * @param solutionSokobano séquence des mouvements
        * @return true si toute la séquence a été rejouée avec succès
     */
    public boolean rejouerCoupsSokobano(String solutionSokobano) {
        if (solutionSokobano == null || solutionSokobano.isBlank()) {
            return true;
        }

        int repetitions = 0;
        for (int i = 0; i < solutionSokobano.length(); i++) {
            char c = solutionSokobano.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (Character.isDigit(c)) {
                repetitions = repetitions * 10 + (c - '0');
                continue;
            }

            Direction direction = directionDepuisSymbole(c);
            if (direction == null) {
                return false;
            }

            int total = repetitions > 0 ? repetitions : 1;
            repetitions = 0;

            for (int n = 0; n < total; n++) {
                if (!deplacer(direction)) {
                    return false;
                }
            }
        }

        return repetitions == 0;
    }

    /**
     * Indique si le niveau est terminé.
     */
    public boolean estVictoire() {
        return victoire;
    }

    public char getMondeActuel() {
        return mondeActuel;
    }

    public int getProfondeurRecursion() {
        return pileMonde.size();
    }

    public boolean peutSortirVersParent() {
        return !pileMonde.isEmpty();
    }

    private Instantane creerInstantane() {
        return new Instantane(
            copierMondes(mondes),
            identifiantRacine,
            mondeActuel,
            copierPile(pileMonde),
            coups,
            victoire,
            sequenceCoups == null ? "" : sequenceCoups.toString()
        );
    }

    private char directionVersSymbole(Direction direction) {
        if (direction == null) {
            return 'u';
        }

        switch (direction) {
            case HAUT:
                return 'u';
            case BAS:
                return 'd';
            case GAUCHE:
                return 'l';
            case DROITE:
                return 'r';
            default:
                return 'u';
        }
    }

    private Direction directionDepuisSymbole(char symbole) {
        switch (Character.toLowerCase(symbole)) {
            case 'u':
                return Direction.HAUT;
            case 'd':
                return Direction.BAS;
            case 'l':
                return Direction.GAUCHE;
            case 'r':
                return Direction.DROITE;
            default:
                return null;
        }
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

    private static java.util.LinkedHashMap<Character, char[][]> copierMondes(
        java.util.LinkedHashMap<Character, char[][]> source
    ) {
        java.util.LinkedHashMap<Character, char[][]> copie = new java.util.LinkedHashMap<>();
        for (java.util.Map.Entry<Character, char[][]> entree : source.entrySet()) {
            copie.put(entree.getKey(), copierGrille(entree.getValue()));
        }
        return copie;
    }

    private static java.util.ArrayList<ContexteRecursif> copierPile(java.util.ArrayList<ContexteRecursif> source) {
        java.util.ArrayList<ContexteRecursif> copie = new java.util.ArrayList<>(source.size());
        for (ContexteRecursif contexte : source) {
            copie.add(new ContexteRecursif(contexte.mondeParent, contexte.xRetour, contexte.yRetour));
        }
        return copie;
    }

    private int[] localiserJoueur(char[][] grille) {
        if (grille == null) {
            return null;
        }
        for (int y = 0; y < grille.length; y++) {
            for (int x = 0; x < grille[y].length; x++) {
                if (grille[y][x] == '@' || grille[y][x] == '+') {
                    return new int[] {x, y};
                }
            }
        }
        return null;
    }

    private boolean verifierVictoire() {
        for (char[][] grilleMonde : mondes.values()) {
            for (char[] ligne : grilleMonde) {
                for (char c : ligne) {
                    if (c == '$' || (Character.isLetter(c) && Character.isLowerCase(c))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean essayerEntrerDansMonde(char caseMonde, int joueurX, int joueurY) {
        char identifiantMonde = Character.toUpperCase(caseMonde);
        char[][] enfant = mondes.get(identifiantMonde);
        if (enfant == null) {
            return false;
        }

        char[][] parent = grilleCourante();
        char depart = parent[joueurY][joueurX];
        parent[joueurY][joueurX] = (depart == '+') ? '.' : ' ';

        nettoyerJoueurMonde(enfant);

        int[] spawn = trouverSpawnEntree(enfant);
        if (spawn == null) {
            spawn = trouverPremiereCaseVide(enfant);
        }
        if (spawn == null) {
            spawn = trouverPremiereCaseLibre(enfant);
            if (spawn == null) {
                parent[joueurY][joueurX] = depart;
                return false;
            }
        }

        enfant[spawn[1]][spawn[0]] = '@';

        pileMonde.add(new ContexteRecursif(mondeActuel, joueurX, joueurY));
        mondeActuel = identifiantMonde;
        return true;
    }

    private void nettoyerJoueurMonde(char[][] grille) {
        if (grille == null) {
            return;
        }

        for (int y = 0; y < grille.length; y++) {
            for (int x = 0; x < grille[y].length; x++) {
                if (grille[y][x] == '@') {
                    grille[y][x] = ' ';
                } else if (grille[y][x] == '+') {
                    grille[y][x] = '.';
                }
            }
        }
    }

    private int[] trouverPremiereCaseVide(char[][] grille) {
        if (grille == null) {
            return null;
        }

        for (int y = 0; y < grille.length; y++) {
            for (int x = 0; x < grille[y].length; x++) {
                if (grille[y][x] == ' ') {
                    return new int[] {x, y};
                }
            }
        }
        return null;
    }

    private boolean essayerSortirVersParent(int joueurX, int joueurY) {
        if (pileMonde.isEmpty()) {
            return false;
        }

        ContexteRecursif contexte = pileMonde.get(pileMonde.size() - 1);
        char[][] enfant = grilleCourante();

        char[][] parent = mondes.get(contexte.mondeParent);
        if (!dansGrille(parent, contexte.xRetour, contexte.yRetour)) {
            return false;
        }

        char destination = parent[contexte.yRetour][contexte.xRetour];
        if (!estLibre(destination)) {
            return false;
        }

        pileMonde.remove(pileMonde.size() - 1);
        enfant[joueurY][joueurX] = (enfant[joueurY][joueurX] == '+') ? '.' : ' ';
        mondeActuel = contexte.mondeParent;
        parent[contexte.yRetour][contexte.xRetour] = (destination == '.') ? '+' : '@';
        return true;
    }

    private int[] trouverPremiereCaseLibre(char[][] grille) {
        if (grille == null) {
            return null;
        }

        for (int y = 0; y < grille.length; y++) {
            for (int x = 0; x < grille[y].length; x++) {
                if (estLibre(grille[y][x])) {
                    return new int[] {x, y};
                }
            }
        }
        return null;
    }

    /**
        * Cherche une case d'apparition pertinente: avec poussée atteignable si le monde contient des objets,
        * sinon avec au moins une possibilité de circulation ou de sortie.
     */
    private int[] trouverSpawnEntree(char[][] grille) {
        if (grille == null) {
            return null;
        }

        boolean contientObjetPoussable = contientObjetPoussable(grille);
        int[] meilleurSpawn = null;
        int meilleurScore = -1;

        for (int y = 0; y < grille.length; y++) {
            for (int x = 0; x < grille[y].length; x++) {
                if (!estLibre(grille[y][x])) {
                    continue;
                }

                int score = evaluerQualiteSpawn(grille, x, y, contientObjetPoussable);
                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleurSpawn = new int[] {x, y};
                }
            }
        }

        return meilleurScore > 0 ? meilleurSpawn : null;
    }

    /**
        * Évalue la qualité d'une case d'apparition selon les actions atteignables depuis sa zone accessible.
     */
    private int evaluerQualiteSpawn(char[][] grille, int x, int y, boolean contientObjetPoussable) {
        int[][] directions = new int[][] {
            {0, -1},
            {0, 1},
            {-1, 0},
            {1, 0}
        };

        boolean[][] visites = new boolean[grille.length][];
        for (int i = 0; i < grille.length; i++) {
            visites[i] = new boolean[grille[i].length];
        }

        java.util.ArrayDeque<int[]> file = new java.util.ArrayDeque<>();
        file.add(new int[] {x, y});
        visites[y][x] = true;

        int casesAccessibles = 0;
        int pousseesPossibles = 0;
        boolean sortiePossible = false;

        while (!file.isEmpty()) {
            int[] position = file.removeFirst();
            int px = position[0];
            int py = position[1];
            casesAccessibles++;

            for (int[] direction : directions) {
                int nx = px + direction[0];
                int ny = py + direction[1];

                if (!dansGrille(grille, nx, ny)) {
                    sortiePossible = true;
                    continue;
                }

                char destination = grille[ny][nx];
                if (destination == '#' && estMurDeBordure(grille, nx, ny)) {
                    sortiePossible = true;
                }

                if (estLibre(destination) && !visites[ny][nx]) {
                    visites[ny][nx] = true;
                    file.add(new int[] {nx, ny});
                    continue;
                }

                if (!estObjetPoussable(destination)) {
                    continue;
                }

                int bx = nx + direction[0];
                int by = ny + direction[1];
                if (dansGrille(grille, bx, by) && estLibre(grille[by][bx])) {
                    pousseesPossibles++;
                }
            }
        }

        if (contientObjetPoussable) {
            if (pousseesPossibles == 0) {
                return 0;
            }
            return 1000 + pousseesPossibles * 10 + casesAccessibles;
        }

        if (!sortiePossible && casesAccessibles <= 1) {
            return 0;
        }

        return (sortiePossible ? 500 : 100) + casesAccessibles;
    }

    private boolean contientObjetPoussable(char[][] grille) {
        if (grille == null) {
            return false;
        }

        for (char[] ligne : grille) {
            for (char cellule : ligne) {
                if (estObjetPoussable(cellule)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean estLibre(char c) {
        return c == ' ' || c == '.';
    }

    private boolean estObjetPoussable(char c) {
        return c == '$' || c == '*' || estBoiteMonde(c);
    }

    private boolean estMonde(char c) {
        return Character.isLetter(c);
    }

    private boolean estBoiteMonde(char c) {
        return Character.isLetter(c) && Character.isLowerCase(c);
    }

    private boolean estBoiteMondeSurCible(char c) {
        return Character.isLetter(c) && Character.isUpperCase(c);
    }

    private boolean estCelluleSupportCible(char c) {
        return c == '.' || c == '*' || (Character.isLetter(c) && Character.isUpperCase(c));
    }

    private char convertirObjetPousse(char objet, char destination) {
        if (!estLibre(destination)) {
            return objet;
        }

        if (estMonde(objet)) {
            char identifiant = Character.toUpperCase(objet);
            return destination == '.' ? identifiant : Character.toLowerCase(identifiant);
        }

        return destination == '.' ? '*' : '$';
    }

    private char[][] grilleCourante() {
        return mondes.get(mondeActuel);
    }

    private boolean dansGrille(int x, int y) {
        return dansGrille(grilleCourante(), x, y);
    }

    private boolean dansGrille(char[][] grille, int x, int y) {
        return grille != null && y >= 0 && y < grille.length && x >= 0 && x < grille[y].length;
    }

    private boolean estMurDeBordure(char[][] grille, int x, int y) {
        if (!dansGrille(grille, x, y) || grille[y][x] != '#') {
            return false;
        }

        int derniereLigne = grille.length - 1;
        int derniereColonne = grille[y].length - 1;
        return x == 0 || y == 0 || y == derniereLigne || x == derniereColonne;
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
