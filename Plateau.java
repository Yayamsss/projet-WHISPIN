import java.util.ArrayList;

/**
 * Représente le plateau de jeu Sokoban.
 * Gère la grille, la position du personnage et la logique de déplacement.
 */
public class Plateau {

    /** Grille du plateau (liste de lignes, chaque ligne est une liste de cases). */
    private ArrayList<ArrayList<Case>> grille;

    /** Position actuelle du personnage. */
    private Position positionPersonnage;

    /** Nombre de lignes. */
    private int hauteur;

    /** Nombre de colonnes. */
    private int largeur;

    /** Historique des mouvements pour le Ctrl+Z. */
    private ArrayList<Mouvement> historique;

    /** Constructeur d'un plateau vide (sans grille définie). */
    public Plateau() {
        this.grille             = new ArrayList<>();
        this.historique         = new ArrayList<>();
        this.positionPersonnage = null;
        this.hauteur            = 0;
        this.largeur            = 0;
    }

    /**
     * Construit un plateau à partir d'une grille existante.
     *
     * @param grille grille de cases (non nulle, non vide)
     * @throws IllegalArgumentException si la grille est invalide ou sans personnage
     */
    public Plateau(ArrayList<ArrayList<Case>> grille) {
        if (grille == null || grille.isEmpty()) {
            throw new IllegalArgumentException("La grille ne peut pas être vide");
        }
        this.grille             = grille;
        this.hauteur            = grille.size();
        this.largeur            = grille.get(0).size();
        this.historique         = new ArrayList<>();
        this.positionPersonnage = trouverPositionPersonnage();

        if (this.positionPersonnage == null) {
            throw new IllegalArgumentException("Le plateau doit contenir un personnage");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Recherche interne
    // ─────────────────────────────────────────────────────────────────────────

    private Position trouverPositionPersonnage() {
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (grille.get(y).get(x).estPersonnageCible()) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Accesseurs
    // ─────────────────────────────────────────────────────────────────────────

    public Case getCase(Position pos) {
        return getCase(pos.getx(), pos.gety());
    }

    public Case getCase(int x, int y) {
        if (!estDansLimites(x, y)) {
            throw new IndexOutOfBoundsException("Position hors limites: (" + x + ", " + y + ")");
        }
        return grille.get(y).get(x);
    }

    public void setCase(Position pos, Case nouvelleCase) {
        setCase(pos.getx(), pos.gety(), nouvelleCase);
    }

    public void setCase(int x, int y, Case nouvelleCase) {
        if (!estDansLimites(x, y)) {
            throw new IndexOutOfBoundsException("Position hors limites: (" + x + ", " + y + ")");
        }
        grille.get(y).set(x, nouvelleCase);
    }

    public boolean estDansLimites(int x, int y) {
        return x >= 0 && x < largeur && y >= 0 && y < hauteur;
    }

    public boolean estDansLimites(Position pos) {
        return estDansLimites(pos.getx(), pos.gety());
    }

    /** Indique si une position est sur la bordure extérieure (première ou dernière ligne/colonne). */
    public boolean estSurBordure(Position pos) {
        return pos.getx() == 0 || pos.getx() == largeur - 1
            || pos.gety() == 0 || pos.gety() == hauteur - 1;
    }

    public Position getPositionPersonnage()           { return positionPersonnage; }
    public ArrayList<ArrayList<Case>> getGrille()     { return grille; }
    public int getHauteur()                           { return hauteur; }
    public int getLargeur()                           { return largeur; }
    public ArrayList<Mouvement> getHistorique()       { return historique; }

    // ─────────────────────────────────────────────────────────────────────────
    // Logique de déplacement
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Vérifie si un déplacement dans la direction donnée est possible depuis la position actuelle.
     */
    public boolean peutSeDeplacer(Direction direction) {
        Position prochainePos  = positionPersonnage.deplacer(direction);
        if (!estDansLimites(prochainePos)) return false;

        Case prochaineCase = getCase(prochainePos);

        if (prochaineCase.estTraversable()) return true;
        if (prochaineCase.estBoite())       return peutPousserBoite(prochainePos, direction);

        return false;
    }

    /**
     * Indique si le prochain déplacement poussera une boîte.
     */
    public boolean vaPousserBoite(Direction direction) {
        if (!peutSeDeplacer(direction)) return false;
        Position prochainePos = positionPersonnage.deplacer(direction);
        if (!estDansLimites(prochainePos)) return false;
        return getCase(prochainePos).estBoite();
    }

    private boolean peutPousserBoite(Position posBoite, Direction direction) {
        Position posApresBoite = posBoite.deplacer(direction);
        if (!estDansLimites(posApresBoite)) return false;
        return getCase(posApresBoite).estTraversable();
    }

    /**
     * Effectue un déplacement dans la direction donnée.
     * Gère la poussée de boîtes et met à jour l'historique pour le Ctrl+Z.
     *
     * @return true si le déplacement a été effectué, false sinon
     */
    public boolean deplacer(Direction direction) {
        if (!peutSeDeplacer(direction)) return false;

        Position prochainePos  = positionPersonnage.deplacer(direction);
        Case prochaineCase     = getCase(prochainePos);
        Case caseActuelle      = getCase(positionPersonnage);

        boolean personnageEtaitSurCible = caseActuelle instanceof Personnage p && p.estSurCible();

        Mouvement mouvement;

        if (prochaineCase.estBoite()) {
            // ── Pousser une boîte ──────────────────────────────────────────
            Position posApresBoite = prochainePos.deplacer(direction);
            Case caseApresBoite    = getCase(posApresBoite);
            CaseBoite boite        = (CaseBoite) prochaineCase;
            boolean boiteEtaitSurCible = boite.estSurCible();

            // Créer la nouvelle boîte à destination (préserve CaseBoiteMonde si besoin)
            boolean boiteSurNouvelleCible = caseApresBoite.estCible();
            CaseBoite nouvelleBoite = boite instanceof CaseBoiteMonde bm
                ? new CaseBoiteMonde(bm.getIdentifiantMonde(), boiteSurNouvelleCible)
                : new CaseBoite(boiteSurNouvelleCible);
            setCase(posApresBoite, nouvelleBoite);

            // Le personnage prend la place de la boîte
            setCase(prochainePos, new Personnage(boiteEtaitSurCible));

            // Restaurer la case d'origine du personnage
            setCase(positionPersonnage, personnageEtaitSurCible
                ? CaseCible.getInstance() : CaseVide.getInstance());

            mouvement = new Mouvement(
                direction, positionPersonnage, prochainePos, personnageEtaitSurCible,
                true, prochainePos, posApresBoite, boiteEtaitSurCible, boite
            );

        } else {
            // ── Déplacement simple ────────────────────────────────────────
            setCase(prochainePos, new Personnage(prochaineCase.estCible()));
            setCase(positionPersonnage, personnageEtaitSurCible
                ? CaseCible.getInstance() : CaseVide.getInstance());

            mouvement = new Mouvement(
                direction, positionPersonnage, prochainePos, personnageEtaitSurCible
            );
        }

        positionPersonnage = prochainePos;
        historique.add(mouvement);
        return true;
    }

    /**
     * Annule le dernier mouvement effectué (Ctrl+Z).
     *
     * @return true si l'annulation a réussi, false si l'historique est vide
     */
    public boolean annulerDernierMouvement() {
        if (historique.isEmpty()) return false;

        Mouvement dernierMouvement = historique.remove(historique.size() - 1);
        Position posDepart  = dernierMouvement.getPositionDepart();
        Position posArrivee = dernierMouvement.getPositionArrivee();

        if (dernierMouvement.aPousseeBoite()) {
            // ── Annuler une poussée de boîte ──────────────────────────────
            Position posBoiteAvant = dernierMouvement.getPositionBoiteAvant();
            Position posBoiteApres = dernierMouvement.getPositionBoiteApres();

            // Remettre la boîte à sa position d'origine (sous-type préservé)
            CaseBoite orig  = dernierMouvement.getCaseBoitePoussee();
            CaseBoite boite = orig instanceof CaseBoiteMonde bm
                ? new CaseBoiteMonde(bm.getIdentifiantMonde(), dernierMouvement.boiteEtaitSurCible())
                : new CaseBoite(dernierMouvement.boiteEtaitSurCible());
            setCase(posBoiteAvant, boite);

            // Restaurer la case où était la boîte après la poussée
            Case caseApres = getCase(posBoiteApres);
            if (caseApres instanceof CaseBoite boiteApres) {
                setCase(posBoiteApres, boiteApres.estSurCible()
                    ? CaseCible.getInstance() : CaseVide.getInstance());
            }

        } else {
            // ── Annuler un déplacement simple ─────────────────────────────
            Case caseArrivee = getCase(posArrivee);
            if (caseArrivee instanceof Personnage p && p.estSurCible()) {
                setCase(posArrivee, CaseCible.getInstance());
            } else {
                setCase(posArrivee, CaseVide.getInstance());
            }
        }

        // Remettre le personnage à sa position de départ
        setCase(posDepart, new Personnage(dernierMouvement.personnageEtaitSurCible()));
        positionPersonnage = posDepart;
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Conditions de victoire
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Vérifie si ce plateau est résolu : toutes les boîtes simples sont sur une cible.
     *
     * Les CaseBoiteMonde (boîtes qui contiennent un autre monde) ne comptent pas ici —
     * leur monde interne est vérifié séparément par {@link Multivers#estGagne()}.
     */
    public boolean estGagne() {
        boolean aUneBoiteSimple = false;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case c = grille.get(y).get(x);
                // On ne considère que les boîtes simples (pas les boîtes-monde)
                if (c instanceof CaseBoite && !(c instanceof CaseBoiteMonde)) {
                    aUneBoiteSimple = true;
                    if (!((CaseBoite) c).estSurCible()) {
                        return false; // une boîte simple pas sur sa cible → pas gagné
                    }
                }
            }
        }
        // Si le plateau n'a aucune boîte simple, il est triviallement résolu
        // (les mondes purement récursifs peuvent ne pas avoir de boîtes simples)
        return true;
    }

    /**
     * Compte les boîtes simples actuellement sur une cible.
     */
    public int compterBoitesSurCibles() {
        int compte = 0;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case c = grille.get(y).get(x);
                if (c instanceof CaseBoite && !(c instanceof CaseBoiteMonde)
                        && ((CaseBoite) c).estSurCible()) {
                    compte++;
                }
            }
        }
        return compte;
    }

    /**
     * Compte le nombre total de cibles dans ce plateau
     * (cases cible vides + boîtes sur cible + personnage sur cible).
     */
    public int compterCibles() {
        int compte = 0;
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Case c = grille.get(y).get(x);
                if (c.estCible()
                        || (c instanceof CaseBoite  && ((CaseBoite)  c).estSurCible())
                        || (c instanceof Personnage  && ((Personnage) c).estSurCible())) {
                    compte++;
                }
            }
        }
        return compte;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Historique
    // ─────────────────────────────────────────────────────────────────────────

    public void reinitialiserHistorique() {
        historique.clear();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Chargement à chaud / téléportation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Remplace l'état du plateau par une nouvelle grille. L'historique est réinitialisé.
     */
    public void chargerDepuisGrille(ArrayList<ArrayList<Case>> nouvelleGrille) {
        if (nouvelleGrille == null || nouvelleGrille.isEmpty()) {
            throw new IllegalArgumentException("La grille chargée ne peut pas être vide");
        }
        int largeurRef = nouvelleGrille.get(0).size();
        if (largeurRef == 0) {
            throw new IllegalArgumentException("La grille chargée est invalide (ligne vide)");
        }

        ArrayList<ArrayList<Case>> copie    = new ArrayList<>();
        int nombrePersonnages = 0;
        for (ArrayList<Case> ligne : nouvelleGrille) {
            if (ligne == null || ligne.size() != largeurRef) {
                throw new IllegalArgumentException("La grille chargée doit être rectangulaire");
            }
            ArrayList<Case> copieLigne = new ArrayList<>(ligne);
            for (Case c : copieLigne) {
                if (c != null && c.estPersonnageCible()) nombrePersonnages++;
            }
            copie.add(copieLigne);
        }

        if (nombrePersonnages != 1) {
            throw new IllegalArgumentException(
                "La grille chargée doit contenir exactement un personnage");
        }

        this.grille             = copie;
        this.hauteur            = copie.size();
        this.largeur            = largeurRef;
        this.positionPersonnage = trouverPositionPersonnage();
        this.historique.clear();
    }

    /**
     * Téléporte le personnage à une nouvelle position (utilisé pour entrer/sortir d'un monde).
     * L'ancienne position du personnage est restaurée (vide ou cible selon l'état).
     */
    public void teleporterPersonnage(Position nouvellePos) {
        Case actuelle = getCase(positionPersonnage);
        boolean etaitSurCible = actuelle instanceof Personnage p && p.estSurCible();
        setCase(positionPersonnage, etaitSurCible ? CaseCible.getInstance() : CaseVide.getInstance());

        boolean surCible = getCase(nouvellePos).estCible();
        setCase(nouvellePos, new Personnage(surCible));
        positionPersonnage = nouvellePos;
    }

    /**
     * Calcule la position d'entrée depuis le bord opposé à la direction d'arrivée.
     * Utilisé quand le joueur entre dans une boîte-monde.
     *
     * @param directionArrivee direction dans laquelle le joueur se déplaçait pour entrer
     * @return première case libre depuis le bon côté
     */
    public Position positionEntreeDepuis(Direction directionArrivee) {
        return switch (directionArrivee) {
            case DROITE -> premiereLibreDansColonne(1);            // entre par la gauche
            case GAUCHE -> premiereLibreDansColonne(largeur - 2);  // entre par la droite
            case BAS    -> premiereLibreDansLigne(1);              // entre par le haut
            case HAUT   -> premiereLibreDansLigne(hauteur - 2);    // entre par le bas
        };
    }

    private Position premiereLibreDansColonne(int x) {
        for (int y = 1; y < hauteur - 1; y++) {
            if (!(getCase(new Position(x, y)) instanceof CaseMur)) return new Position(x, y);
        }
        return new Position(largeur / 2, hauteur / 2);
    }

    private Position premiereLibreDansLigne(int y) {
        for (int x = 1; x < largeur - 1; x++) {
            if (!(getCase(new Position(x, y)) instanceof CaseMur)) return new Position(x, y);
        }
        return new Position(largeur / 2, hauteur / 2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Debug
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                sb.append(grille.get(y).get(x).getSymbole());
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
