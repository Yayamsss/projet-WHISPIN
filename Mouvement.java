/**
 * Représente un mouvement effectué dans le jeu Sokoban.
 * Un mouvement peut être un déplacement simple ou une poussée de boîte.
 * Cette classe stocke les informations nécessaires pour annuler un coup.
 */
public class Mouvement {

    /** Direction du mouvement. */
    private final Direction direction;

    /** Position de départ du personnage (nullable pour la persistance). */
    private final Position positionDepart;

    /** Position d'arrivée du personnage (nullable pour la persistance). */
    private final Position positionArrivee;

    /** Indique si ce mouvement pousse une boîte. */
    private final boolean aPousseeBoite;

    /** Position de la boîte avant le mouvement (null si non applicable). */
    private final Position positionBoiteAvant;

    /** Position de la boîte après le mouvement (null si non applicable). */
    private final Position positionBoiteApres;

    /** Indique si le personnage était sur une cible avant le mouvement. */
    private final boolean personnageSurCibleAvant;

    /** Indique si la boîte était sur une cible avant le mouvement. */
    private final boolean boiteSurCibleAvant;

    /** Case boîte originale poussée (conservée pour restaurer le sous-type exact lors du Ctrl+Z). */
    private final CaseBoite caseBoitePoussee;

    /**
     * Construit un mouvement simple (sans poussée).
     */
    public Mouvement(Direction direction, Position positionDepart,
                     Position positionArrivee, boolean personnageSurCibleAvant) {
        this(direction, positionDepart, positionArrivee, personnageSurCibleAvant,
             false, null, null, false, null);
    }

    /**
     * Construit un mouvement complet, avec ou sans poussée.
     */
    public Mouvement(Direction direction, Position positionDepart,
                     Position positionArrivee, boolean personnageSurCibleAvant,
                     boolean aPousseeBoite, Position positionBoiteAvant,
                     Position positionBoiteApres, boolean boiteSurCibleAvant) {
        this(direction, positionDepart, positionArrivee, personnageSurCibleAvant,
             aPousseeBoite, positionBoiteAvant, positionBoiteApres, boiteSurCibleAvant, null);
    }

    /**
     * Construit un mouvement complet avec la boîte originale poussée (préserve le sous-type pour l'annulation).
     */
    public Mouvement(Direction direction, Position positionDepart,
                     Position positionArrivee, boolean personnageSurCibleAvant,
                     boolean aPousseeBoite, Position positionBoiteAvant,
                     Position positionBoiteApres, boolean boiteSurCibleAvant,
                     CaseBoite caseBoitePoussee) {
        if (direction == null) {
            throw new IllegalArgumentException("La direction ne peut pas etre nulle");
        }
        this.direction = direction;
        this.positionDepart = positionDepart;
        this.positionArrivee = positionArrivee;
        this.personnageSurCibleAvant = personnageSurCibleAvant;
        this.aPousseeBoite = aPousseeBoite;
        this.positionBoiteAvant = positionBoiteAvant;
        this.positionBoiteApres = positionBoiteApres;
        this.boiteSurCibleAvant = boiteSurCibleAvant;
        this.caseBoitePoussee = caseBoitePoussee;
    }

    /**
     * Construit un mouvement minimal pour les données de persistance.
     */
    public Mouvement(Direction direction, boolean aPousseeBoite) {
        this(direction, null, null, false, aPousseeBoite, null, null, false);
    }

    public Direction getDirection() {
        return direction;
    }

    public Position getPositionDepart() {
        return positionDepart;
    }

    public Position getPositionArrivee() {
        return positionArrivee;
    }

    public boolean aPousseeBoite() {
        return aPousseeBoite;
    }

    public Position getPositionBoiteAvant() {
        return positionBoiteAvant;
    }

    public Position getPositionBoiteApres() {
        return positionBoiteApres;
    }

    public boolean personnageEtaitSurCible() {
        return personnageSurCibleAvant;
    }

    public boolean boiteEtaitSurCible() {
        return boiteSurCibleAvant;
    }

    public CaseBoite getCaseBoitePoussee() {
        return caseBoitePoussee;
    }

    /**
     * Retourne le code moderne de persistance (u/d/l/r, majuscule si poussée).
     */
    public char toLettre() {
        char lettre = direction.toLettre();
        return aPousseeBoite ? Character.toUpperCase(lettre) : lettre;
    }

    /**
     * Retourne le code de persistance en français :
     * minuscule = déplacement seul, majuscule = poussée de boîte.
     * h/b/g/d et H/B/G/D.
     */
    public char obtenirCode() {
        char base = switch (direction) {
            case HAUT    -> 'h';
            case BAS     -> 'b';
            case GAUCHE  -> 'g';
            case DROITE  -> 'd';
        };
        return aPousseeBoite ? Character.toUpperCase(base) : base;
    }

    /**
     * Construit un mouvement depuis un code français (h/b/g/d, majuscule = poussée).
     */
    public static Mouvement depuisCode(char code) {
        boolean poussee = Character.isUpperCase(code);
        char normalise = Character.toLowerCase(code);
        Direction direction;

        switch (normalise) {
            case 'h':
                direction = Direction.HAUT;
                break;
            case 'b':
                direction = Direction.BAS;
                break;
            case 'g':
                direction = Direction.GAUCHE;
                break;
            case 'd':
                direction = Direction.DROITE;
                break;
            default:
                throw new IllegalArgumentException("Code de mouvement invalide: " + code);
        }

        return new Mouvement(direction, poussee);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mouvement[").append(direction.getSymbole());
        if (positionDepart != null && positionArrivee != null) {
            sb.append(" ").append(positionDepart).append(" -> ").append(positionArrivee);
        }
        if (aPousseeBoite) {
            sb.append(", pousse boite");
            if (positionBoiteAvant != null && positionBoiteApres != null) {
                sb.append(" ").append(positionBoiteAvant).append(" -> ").append(positionBoiteApres);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mouvement)) {
            return false;
        }
        Mouvement autre = (Mouvement) obj;
        return this.direction == autre.direction
            && this.aPousseeBoite == autre.aPousseeBoite
            && (this.positionDepart == null ? autre.positionDepart == null : this.positionDepart.equals(autre.positionDepart))
            && (this.positionArrivee == null ? autre.positionArrivee == null : this.positionArrivee.equals(autre.positionArrivee))
            && (this.positionBoiteAvant == null ? autre.positionBoiteAvant == null : this.positionBoiteAvant.equals(autre.positionBoiteAvant))
            && (this.positionBoiteApres == null ? autre.positionBoiteApres == null : this.positionBoiteApres.equals(autre.positionBoiteApres));
    }

    @Override
    public int hashCode() {
        int result = direction.hashCode();
        result = 31 * result + Boolean.hashCode(aPousseeBoite);
        result = 31 * result + (positionDepart == null ? 0 : positionDepart.hashCode());
        result = 31 * result + (positionArrivee == null ? 0 : positionArrivee.hashCode());
        result = 31 * result + (positionBoiteAvant == null ? 0 : positionBoiteAvant.hashCode());
        result = 31 * result + (positionBoiteApres == null ? 0 : positionBoiteApres.hashCode());
        return result;
    }
}
