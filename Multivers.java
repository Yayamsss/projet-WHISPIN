import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contient l'ensemble des mondes du Sokoban récursif et l'état de navigation du joueur.
 *
 * Le joueur se trouve toujours dans un "monde courant".
 * Quand il entre dans une boîte-monde, le contexte actuel est empilé
 * et on bascule dans le monde référencé. Quand il sort, on dépile.
 *
 * Format d'un fichier multi-monde attendu :
 * <pre>
 *   A 6
 *   ######
 *   # @  #
 *   # b. #
 *   ######
 *   B 4
 *   ####
 *   #$.#
 *   ####
 * </pre>
 */
public class Multivers {

    /**
     * Vue légère d'un monde pour les couches de rendu.
     */
    public static final class MondeView {
        public final char id;
        public final Plateau plateau;

        public MondeView(char id, Plateau plateau) {
            this.id = id;
            this.plateau = plateau;
        }
    }

    /**
     * Contexte sauvegardé à chaque entrée dans une boîte-monde.
     * Permet de retrouver le monde parent et l'endroit où réapparaître.
     */
    public static final class ContexteNavigation {
        /** Identifiant du monde parent (celui qu'on a quitté). */
        public final char mondePrecedent;
        /** Position de la boîte-monde dans le monde parent (pour la surbrillance). */
        public final Position positionBoite;
        /** Case dans le monde parent où le joueur réapparaît en sortant. */
        public final Position positionSortie;
        /** Direction utilisée pour entrer (utile pour choisir le bon côté en sortant). */
        public final Direction directionEntree;

        public ContexteNavigation(char mondePrecedent, Position positionBoite,
                                   Position positionSortie, Direction directionEntree) {
            this.mondePrecedent  = mondePrecedent;
            this.positionBoite   = positionBoite;
            this.positionSortie  = positionSortie;
            this.directionEntree = directionEntree;
        }

        // Constructeur de compatibilité sans direction (ancienne API)
        public ContexteNavigation(char mondePrecedent, Position positionBoite, Position positionSortie) {
            this(mondePrecedent, positionBoite, positionSortie, Direction.DROITE);
        }
    }

    /** Résultat d'une tentative de déplacement. */
    public enum ResultatDeplacement {
        DEPLACE,
        ENTRE,
        SORTI,
        BLOQUE
    }

    /** Tous les mondes chargés, indexés par leur lettre. */
    private final Map<Character, Plateau> mondes;

    /** Lettre du monde dans lequel le joueur se trouve actuellement. */
    private char mondeCourant;

    /** Pile de contextes pour pouvoir revenir dans le monde parent. */
    private final Deque<ContexteNavigation> pileContextes;

    /** Historique des transitions de contexte (entree/sortie) pour Ctrl+Z inter-monde. */
    private final ArrayList<Mouvement> historiqueTransitions;

    /** Dernier resultat de deplacement observe dans le multivers. */
    private ResultatDeplacement dernierResultatDeplacement;

    /** Lettre du monde racine (le tout premier, jamais changée). */
    private final char mondeRacine;

    public Multivers(Map<Character, Plateau> mondes) {
        if (mondes == null || mondes.isEmpty()) {
            throw new IllegalArgumentException("Un Multivers doit contenir au moins un monde.");
        }
        this.mondes       = new LinkedHashMap<>(mondes);
        this.mondeCourant = mondes.keySet().iterator().next();
        this.mondeRacine  = this.mondeCourant;
        this.pileContextes = new ArrayDeque<>();
        this.historiqueTransitions = new ArrayList<>();
        this.dernierResultatDeplacement = ResultatDeplacement.BLOQUE;
    }

    /**
     * Construit un Multivers directement depuis des grilles brutes.
     * Le premier monde de la map est le monde de départ.
     */
    public static Multivers depuisGrilles(Map<Character, ArrayList<ArrayList<Case>>> grilles) {
        Map<Character, Plateau> plateaux = new LinkedHashMap<>();
        for (Map.Entry<Character, ArrayList<ArrayList<Case>>> entree : grilles.entrySet()) {
            plateaux.put(entree.getKey(), new Plateau(entree.getValue()));
        }
        return new Multivers(plateaux);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Accesseurs
    // ─────────────────────────────────────────────────────────────────────────

    public char getMondeCourant() { return mondeCourant; }

    /** Alias explicite pour la couche de rendu. */
    public char getMondeCourantId() { return mondeCourant; }

    public Plateau getPlateauCourant() { return mondes.get(mondeCourant); }

    /** Alias explicite pour la couche de rendu. */
    public Plateau getMondeCourantPlateau() { return getPlateauCourant(); }

    public Plateau getPlateau(char lettre) { return mondes.get(Character.toUpperCase(lettre)); }

    /** Retourne un monde optionnel (lecture pure, sans exception). */
    public Optional<Plateau> getMonde(char lettre) {
        return Optional.ofNullable(getPlateau(lettre));
    }

    public boolean existeMonde(char lettre) {
        return mondes.containsKey(Character.toUpperCase(lettre));
    }

    public Map<Character, Plateau> getTousLesMondes() {
        return java.util.Collections.unmodifiableMap(mondes);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navigation entre mondes
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Entre dans une boîte-monde : empile le contexte courant et bascule vers le monde cible.
     */
    public void entrerDans(char identifiantCible, Position positionBoite,
                            Position positionSortie, Direction directionEntree) {
        char cible = Character.toUpperCase(identifiantCible);
        if (!existeMonde(cible)) {
            throw new IllegalArgumentException("Le monde '" + cible + "' n'existe pas.");
        }
        pileContextes.push(new ContexteNavigation(mondeCourant, positionBoite,
                                                   positionSortie, directionEntree));
        mondeCourant = cible;
    }

    // Compatibilité ancienne API
    public void entrerDans(char identifiantCible, Position positionBoite, Position positionSortie) {
        entrerDans(identifiantCible, positionBoite, positionSortie, Direction.DROITE);
    }

    /**
     * Sort du monde courant : dépile le contexte et revient dans le monde parent.
     */
    public ContexteNavigation sortir() {
        if (pileContextes.isEmpty()) {
            throw new IllegalStateException("Impossible de sortir : déjà dans le monde racine.");
        }
        ContexteNavigation contexte = pileContextes.pop();
        mondeCourant = contexte.mondePrecedent;
        return contexte;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Déplacement multi-monde
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Tente un déplacement dans la direction donnée en gérant les transitions entre mondes.
     *
     * Ordre de priorité :
     * 1. Hors-limites du plateau → tente une sortie
     * 2. Mur de bordure → tente une sortie
     * 3. La case cible est une boîte-monde → tente une entrée
     * 4. Une boîte simple poussée vers l'extérieur → sortie avec la boîte
     * 5. Déplacement classique sur le plateau courant
     */
    public ResultatDeplacement deplacer(Direction direction) {
        Plateau plateau = getPlateauCourant();
        Position posActuelle  = plateau.getPositionPersonnage();
        Position prochainePos = posActuelle.deplacer(direction);

        // Hors-limites → sortir
        if (!plateau.estDansLimites(prochainePos)) {
            ResultatDeplacement resultat = tenterSortie(direction);
            dernierResultatDeplacement = resultat;
            return resultat;
        }

        Case prochaineCase = plateau.getCase(prochainePos);

        // Mur de bordure → sortir (les mondes-boîtes sont entourés de murs)
        if (prochaineCase instanceof CaseMur && plateau.estSurBordure(prochainePos)) {
            return tenterSortie(direction);
        }

        // Boîte-monde détectée
        if (prochaineCase instanceof CaseBoiteMonde bm) {
            // On ne peut entrer que si on ne peut pas pousser la boîte normalement
            // (i.e. la case derrière la boîte n'est pas libre ou c'est un mur de fond)
            if (!plateau.peutSeDeplacer(direction)) {
                ResultatDeplacement resultat = tenterEntree(bm, prochainePos, direction);
                dernierResultatDeplacement = resultat;
                return resultat;
            }
            // Sinon, la boîte-monde est poussable normalement
        }

        // Boîte simple poussée hors du monde courant (vers un mur de bordure)
        if (prochaineCase instanceof CaseBoite boite
                && !(prochaineCase instanceof CaseBoiteMonde)
                && !plateau.peutSeDeplacer(direction)
                && pousseVersExterieur(plateau, prochainePos, direction)) {
            ResultatDeplacement resultat = tenterSortieAvecBoite(prochainePos, direction);
            dernierResultatDeplacement = resultat;
            return resultat;
        }

        // Déplacement classique
        boolean deplace = plateau.deplacer(direction);
        ResultatDeplacement resultat = deplace ? ResultatDeplacement.DEPLACE : ResultatDeplacement.BLOQUE;
        dernierResultatDeplacement = resultat;
        return resultat;
    }

    /** Vérifie si la boîte à la position donnée est contre un mur de bordure. */
    private boolean pousseVersExterieur(Plateau plateau, Position posBoite, Direction direction) {
        return switch (direction) {
            case GAUCHE -> posBoite.getx() == 1;
            case DROITE -> posBoite.getx() == plateau.getLargeur() - 2;
            case HAUT   -> posBoite.gety() == 1;
            case BAS    -> posBoite.gety() == plateau.getHauteur() - 2;
        };
    }

    /**
     * Tente d'entrer dans une boîte-monde.
     * Calcule la position de sortie (case juste après la boîte dans le parent),
     * empile le contexte et téléporte le joueur côté opposé dans le monde enfant.
     */
    private ResultatDeplacement tenterEntree(CaseBoiteMonde boite, Position posBoite, Direction direction) {
        char mondeAvant = mondeCourant;
        ArrayList<ContexteNavigation> pileAvant = copierContextes(getContextesOrdonnes());
        Position joueurAvant = getPlateauCourant().getPositionPersonnage();

        char identifiant = boite.getIdentifiantMonde();
        if (!existeMonde(identifiant)) {
            // La boîte-monde pointe sur un monde inexistant → bloqué
            return ResultatDeplacement.BLOQUE;
        }

        // La position de sortie est la case juste derrière la boîte dans le monde parent.
        // Si elle est hors limites ou bloquée, on pourra gérer ça à la sortie.
        Position positionSortie = posBoite.deplacer(direction);

        entrerDans(identifiant, posBoite, positionSortie, direction);

        // Placer le joueur du côté opposé dans le nouveau monde
        Plateau nouveauPlateau = getPlateauCourant();
        Position entree = nouveauPlateau.positionEntreeDepuis(direction);
        nouveauPlateau.teleporterPersonnage(entree);

        enregistrerTransitionRecursive(
            direction,
            mondeAvant,
            mondeCourant,
            pileAvant,
            copierContextes(getContextesOrdonnes()),
            joueurAvant,
            entree
        );

        return ResultatDeplacement.ENTRE;
    }

    /**
     * Tente de sortir du monde courant.
     * La direction d'entrée mémorisée sert à trouver la bonne position de sortie.
     */
    private ResultatDeplacement tenterSortie(Direction directionSortie) {
        if (!peutSortir()) {
            return ResultatDeplacement.BLOQUE;
        }

        char mondeAvant = mondeCourant;
        ArrayList<ContexteNavigation> pileAvant = copierContextes(getContextesOrdonnes());
        Position joueurAvant = getPlateauCourant().getPositionPersonnage();

        ContexteNavigation contexte = sortir();
        Plateau plateauParent = getPlateauCourant();

        // Essayer la position de sortie prévue
        Position sortie = contexte.positionSortie;

        // Fallback : si la position prévue est invalide, on cherche à partir du bon côté
        if (!plateauParent.estDansLimites(sortie)
                || plateauParent.getCase(sortie) instanceof CaseMur) {
            // On réutilise la direction d'entrée mémorisée pour trouver le bon bord
            sortie = trouverSortieValideDepuisBord(plateauParent,
                                                    contexte.positionBoite,
                                                    contexte.directionEntree);
        }

        // Dernier recours : première case libre du plateau
        if (sortie == null || !plateauParent.estDansLimites(sortie)
                || plateauParent.getCase(sortie) instanceof CaseMur) {
            sortie = plateauParent.positionEntreeDepuis(Direction.DROITE);
        }

        plateauParent.teleporterPersonnage(sortie);

        enregistrerTransitionRecursive(
            directionSortie,
            mondeAvant,
            mondeCourant,
            pileAvant,
            copierContextes(getContextesOrdonnes()),
            joueurAvant,
            sortie
        );

        return ResultatDeplacement.SORTI;
    }

    /**
     * Cherche une case libre autour de la boîte-monde dans le monde parent,
     * du côté de la direction d'entrée.
     */
    private Position trouverSortieValideDepuisBord(Plateau plateau, Position posBoite, Direction dirEntree) {
        // La sortie naturelle est la case après la boîte dans la direction d'entrée
        Position candidate = posBoite.deplacer(dirEntree);
        if (plateau.estDansLimites(candidate) && !(plateau.getCase(candidate) instanceof CaseMur)) {
            return candidate;
        }
        // Sinon, parcourir autour
        for (Direction d : Direction.values()) {
            Position p = posBoite.deplacer(d);
            if (plateau.estDansLimites(p) && !(plateau.getCase(p) instanceof CaseMur)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Sort du monde courant en exportant une boîte simple dans le monde parent.
     */
    private ResultatDeplacement tenterSortieAvecBoite(Position posBoite, Direction direction) {
        if (!peutSortir()) {
            return ResultatDeplacement.BLOQUE;
        }

        char mondeAvant = mondeCourant;
        ArrayList<ContexteNavigation> pileAvant = copierContextes(getContextesOrdonnes());
        Position joueurAvant = getPlateauCourant().getPositionPersonnage();

        Plateau plateauEnfant = getPlateauCourant();
        Case caseBoite = plateauEnfant.getCase(posBoite);
        if (!(caseBoite instanceof CaseBoite boite) || caseBoite instanceof CaseBoiteMonde) {
            return ResultatDeplacement.BLOQUE;
        }

        ContexteNavigation contexte = pileContextes.peek();
        if (contexte == null) return ResultatDeplacement.BLOQUE;

        Plateau plateauParentAvantSortie = getPlateau(contexte.mondePrecedent);
        Position exportSouhaite = calculerPositionExportSouhaitee(
            contexte, plateauEnfant, posBoite, direction);
        Position positionExport = trouverPositionExportDisponible(
            plateauParentAvantSortie, contexte.positionBoite, exportSouhaite, direction);

        if (positionExport == null) {
            return ResultatDeplacement.BLOQUE;
        }

        // Retirer la boîte du monde enfant
        plateauEnfant.setCase(posBoite, boite.estSurCible()
            ? CaseCible.getInstance() : CaseVide.getInstance());

        // Revenir dans le parent
        ContexteNavigation contexteSortie = sortir();
        Plateau plateauParent = getPlateauCourant();

        // Placer la boîte exportée dans le parent
        Case caseExport = plateauParent.getCase(positionExport);
        plateauParent.setCase(positionExport, new CaseBoite(caseExport.estCible()));

        // Trouver où mettre le joueur dans le parent
        Position sortiJoueur = trouverSortieJoueurValide(
            plateauParent, contexteSortie.positionSortie, positionExport);
        if (sortiJoueur == null) return ResultatDeplacement.BLOQUE;

        plateauParent.teleporterPersonnage(sortiJoueur);

        enregistrerTransitionRecursive(
            direction,
            mondeAvant,
            mondeCourant,
            pileAvant,
            copierContextes(getContextesOrdonnes()),
            joueurAvant,
            sortiJoueur
        );

        return ResultatDeplacement.SORTI;
    }

    /** Calcule la position d'export désirée autour de la boîte-monde. */
    private Position calculerPositionExportSouhaitee(
        ContexteNavigation contexte,
        Plateau plateauEnfant,
        Position posBoiteEnfant,
        Direction direction
    ) {
        int cx = plateauEnfant.getLargeur() / 2;
        int cy = plateauEnfant.getHauteur() / 2;
        int dx = posBoiteEnfant.getx() - cx;
        int dy = posBoiteEnfant.gety() - cy;
        int xPortail = contexte.positionBoite.getx();
        int yPortail = contexte.positionBoite.gety();

        return switch (direction) {
            case GAUCHE -> new Position(xPortail - 1, yPortail + dy);
            case DROITE -> new Position(xPortail + 1, yPortail + dy);
            case HAUT   -> new Position(xPortail + dx, yPortail - 1);
            case BAS    -> new Position(xPortail + dx, yPortail + 1);
        };
    }

    /** Cherche une case libre disponible pour poser la boîte exportée. */
    private Position trouverPositionExportDisponible(
        Plateau plateauParent,
        Position posPortail,
        Position souhaitee,
        Direction direction
    ) {
        int limite = Math.max(plateauParent.getLargeur(), plateauParent.getHauteur());
        for (int d = 0; d < limite; d++) {
            int[] essais = (d == 0) ? new int[]{0} : new int[]{d, -d};
            for (int decalage : essais) {
                Position candidate = switch (direction) {
                    case GAUCHE, DROITE ->
                        new Position(souhaitee.getx(), posPortail.gety() + decalage);
                    case HAUT, BAS ->
                        new Position(posPortail.getx() + decalage, souhaitee.gety());
                };
                if (!plateauParent.estDansLimites(candidate)) continue;
                if (candidate.equals(posPortail)) continue;
                if (plateauParent.getCase(candidate).estTraversable()) return candidate;
            }
        }
        return null;
    }

    /** Cherche une position valide pour le joueur dans le monde parent après une sortie. */
    private Position trouverSortieJoueurValide(Plateau plateauParent,
                                                Position sortiePreferree,
                                                Position caseOccupee) {
        if (plateauParent.estDansLimites(sortiePreferree)
                && !sortiePreferree.equals(caseOccupee)
                && plateauParent.getCase(sortiePreferree).estTraversable()) {
            return sortiePreferree;
        }
        // Parcourir tout le plateau pour trouver une case libre
        for (int y = 0; y < plateauParent.getHauteur(); y++) {
            for (int x = 0; x < plateauParent.getLargeur(); x++) {
                Position p = new Position(x, y);
                if (p.equals(caseOccupee)) continue;
                if (plateauParent.getCase(p).estTraversable()) return p;
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // État global
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retourne la pile de navigation dans l'ordre racine → parent immédiat.
     */
    public List<ContexteNavigation> getContextesOrdonnes() {
        List<ContexteNavigation> liste = new ArrayList<>(pileContextes);
        Collections.reverse(liste);
        return liste;
    }

    /** Alias explicite pour les consommateurs externes (rendu/persistance/tests). */
    public List<ContexteNavigation> getPileContextesSnapshot() {
        return new ArrayList<>(getContextesOrdonnes());
    }

    /**
     * Victoire globale : toutes les boîtes de CHAQUE monde sont sur une cible.
     *
     * Note : un monde sans boîtes est considéré gagné (condition vacuellement vraie).
     * Cela ne pose pas de problème car un niveau bien conçu a toujours des boîtes
     * dans au moins le monde principal.
     */
    public boolean estGagne() {
        for (Plateau p : mondes.values()) {
            if (!p.estGagne()) return false;
        }
        return true;
    }

    /** Indique si le joueur peut sortir du monde courant (n'est pas dans le monde racine). */
    public boolean peutSortir() {
        return !pileContextes.isEmpty();
    }

    /**
     * Annule la derniere transition de contexte (entree/sortie de monde).
     */
    public boolean annulerDerniereTransitionContexte() {
        if (historiqueTransitions.isEmpty()) {
            return false;
        }

        Mouvement transition = historiqueTransitions.remove(historiqueTransitions.size() - 1);
        if (!transition.estTransitionRecursive()) {
            return false;
        }

        Character mondeCible = transition.getMondeAvant();
        ArrayList<ContexteNavigation> pileCible = transition.getPileAvantSnapshot();
        if (mondeCible == null || pileCible == null) {
            return false;
        }

        appliquerEtatNavigation(mondeCible, pileCible);

        Position posJoueur = transition.getPositionJoueurMondeAvant();
        Plateau plateau = getPlateauCourant();
        if (posJoueur != null && plateau.estDansLimites(posJoueur)
                && !(plateau.getCase(posJoueur) instanceof CaseMur)) {
            plateau.teleporterPersonnage(posJoueur);
        }

        dernierResultatDeplacement = ResultatDeplacement.BLOQUE;

        return true;
    }

    public ResultatDeplacement getDernierResultatDeplacement() {
        return dernierResultatDeplacement;
    }

    /** Profondeur de navigation (0 = monde racine). */
    public int getProfondeur() {
        return pileContextes.size();
    }

    /** Alias explicite pour la couche de rendu. */
    public int getProfondeurCourante() {
        return getProfondeur();
    }

    /**
     * Retourne la chaîne visible racine -> monde courant.
     * Cette méthode est dédiée à la présentation (rendu) et ne modifie aucun état.
     */
    public List<MondeView> getCheminVisible() {
        List<Character> ids = new ArrayList<>();
        ids.add(mondeRacine);

        for (ContexteNavigation contexte : getContextesOrdonnes()) {
            char parent = contexte.mondePrecedent;
            if (ids.isEmpty() || ids.get(ids.size() - 1) != parent) {
                ids.add(parent);
            }
        }

        if (ids.isEmpty() || ids.get(ids.size() - 1) != mondeCourant) {
            ids.add(mondeCourant);
        }

        List<MondeView> vues = new ArrayList<>(ids.size());
        for (char id : ids) {
            Plateau plateau = mondes.get(id);
            if (plateau != null) {
                vues.add(new MondeView(id, plateau));
            }
        }
        return vues;
    }

    /**
     * Réinitialise la navigation : vide la pile et revient au monde racine.
     * Utile pour un retour au menu ou un reset complet.
     */
    public void reinitialiserNavigation() {
        pileContextes.clear();
        historiqueTransitions.clear();
        dernierResultatDeplacement = ResultatDeplacement.BLOQUE;
        // On revient toujours au monde racine original, pas juste au parent du dernier contexte.
        mondeCourant = mondeRacine;
    }

    private void enregistrerTransitionRecursive(
        Direction direction,
        char mondeAvant,
        char mondeApres,
        ArrayList<ContexteNavigation> pileAvant,
        ArrayList<ContexteNavigation> pileApres,
        Position joueurAvant,
        Position joueurApres
    ) {
        historiqueTransitions.add(Mouvement.transitionRecursive(
            direction,
            mondeAvant,
            mondeApres,
            pileAvant,
            pileApres,
            joueurAvant,
            joueurApres
        ));
    }

    private void appliquerEtatNavigation(char monde, List<ContexteNavigation> contextesOrdonnes) {
        pileContextes.clear();

        if (contextesOrdonnes != null) {
            // contextesOrdonnes est fourni dans l'ordre racine -> parent immediat.
            // La pile interne, elle, doit avoir le parent immediat en tete (comme push/pop).
            for (ContexteNavigation c : contextesOrdonnes) {
                if (c != null) {
                    pileContextes.push(copieContexte(c));
                }
            }
        }

        mondeCourant = Character.toUpperCase(monde);
    }

    private static ArrayList<ContexteNavigation> copierContextes(List<ContexteNavigation> source) {
        ArrayList<ContexteNavigation> copie = new ArrayList<>();
        if (source == null) {
            return copie;
        }
        for (ContexteNavigation c : source) {
            if (c != null) {
                copie.add(copieContexte(c));
            }
        }
        return copie;
    }

    private static ContexteNavigation copieContexte(ContexteNavigation c) {
        Position positionBoite = c.positionBoite == null
            ? null
            : new Position(c.positionBoite.getx(), c.positionBoite.gety());
        Position positionSortie = c.positionSortie == null
            ? null
            : new Position(c.positionSortie.getx(), c.positionSortie.gety());
        return new ContexteNavigation(
            c.mondePrecedent,
            positionBoite,
            positionSortie,
            c.directionEntree
        );
    }

    @Override
    public String toString() {
        return "Multivers{mondeCourant='" + mondeCourant
            + "', profondeur=" + getProfondeur()
            + ", mondes=" + mondes.keySet() + "}";
    }
}
