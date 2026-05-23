import javafx.scene.paint.Color;

/**
 * Représente une boîte dans le jeu Sokoban.
 * Une boîte peut être poussée si la case suivante le permet.
 * Une boîte peut être sur une cible ou non.
 */
public class CaseBoite extends Case {

    /** indique si la boîte est actuellement sur une cible */
    private boolean surCible;

    /** horodatage (nanoTime) du moment où la boîte est arrivée sur une cible */
    private long surCibleDepuisNs;
    
    /**
     * Construit une boîte hors cible.
     */
    public CaseBoite() {
        this(false);
    }
    
    /**
     * Constructeur avec état initial.
     *
     * @param surCible true si la boîte est initialement sur une cible, false sinon
     */
    public CaseBoite(boolean surCible) {
        super(0, 0); // les coordonnées de la boîte sont gérées par la map
        this.surCible = surCible;
        this.surCibleDepuisNs = surCible ? System.nanoTime() : 0L;
    }
    
    /**
     * Une boîte n'est pas traversable.
     *
     * @return false
     */
    @Override
    public boolean estTraversable() {
        return false;
    }
    
    /**
     * Indique qu'une boîte est poussable.
     * La validation de la case suivante est gérée par le plateau.
     *
     * @return true
     */
    @Override
    public boolean estPoussable() {
        return true;
    }
    
    /**
     * Indique que cette case contient une boîte.
     *
     * @return true
     */
    @Override
    public boolean estBoite() {
        return true;
    }
    
    /**
     * Indique si la boîte est actuellement sur une cible.
     *
     * @return true si la boîte est sur une cible, false sinon
     */
    public boolean estSurCible() {
        return surCible;
    }

    public long getSurCibleDepuisNs() {
        return surCibleDepuisNs;
    }
    
    /**
     * Met à jour l'état "sur cible" de la boîte.
     *
     * @param surCible true si la boîte est sur une cible, false sinon
     */
    public void setSurCible(boolean surCible) {
        this.surCible = surCible;
    }
    
    /**
     * Retourne le symbole ASCII de la boîte.
     * '$' si la boîte n'est pas sur une cible, '*' sinon.
     *
     * @return '$' ou '*'
     */
    @Override
    public char getSymbole() {
        return surCible ? '*' : '$';
    }
    
    /**
     * Retourne une représentation textuelle de la boîte.
     *
     * @return "Boite[$]" ou "Boite[*]" selon l'état
     */
    @Override
    public String toString() {
        return surCible ? "Boite[*]" : "Boite[$]";
    }
    
    /**
     * Compare cette boîte avec un autre objet.
     * Deux boîtes sont égales si leur état "sur cible" est identique.
     *
     * @param obj objet à comparer
     * @return true si obj est une boîte avec le même état, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CaseBoite)) return false;
        CaseBoite autre = (CaseBoite) obj;
        return this.surCible == autre.surCible;
    }
    
    /**
     * Retourne le hashCode de la boîte.
     *
     * @return hashCode basé sur l'état surCible
     */
    @Override
    public int hashCode() {
        return Boolean.hashCode(surCible);
    }

    @Override
    public Color getCouleurSol() {
        return surCible ? Color.web("#e8ddb7") : Color.web("#e9d8a6");
    }

}