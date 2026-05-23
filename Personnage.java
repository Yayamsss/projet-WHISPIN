import javafx.scene.paint.Color;

/**
 * Représente le personnage (joueur) dans le jeu Sokoban.
 * Il n'y a qu'un seul personnage par niveau.
 * Le personnage peut se déplacer et pousser des boîtes.
 * Il peut être sur une cible ou non.
 */
public class Personnage extends Case {
    
    /** indique si le personnage est actuellement sur une cible */
    private boolean surCible;
    
    /**
     * Construit un personnage hors cible.
     */
    public Personnage() {
        this(false);
    }
    
    /**
     * Construit un personnage avec son état initial.
     *
     * @param surCible true si le personnage est initialement sur une cible, false sinon
     */
    public Personnage(boolean surCible) {
        super(0, 0); // les coordonnées du personnage sont gérées par la map
        this.surCible = surCible;
    }
    
    /**
     * Le personnage n'est pas traversable.
     *
     * @return false
     */
    @Override
    public boolean estTraversable() {
        return false;
    }
    
    /**
     * Le personnage n'est pas poussable.
     *
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * Indique que cette case contient le personnage.
     *
     * @return true
     */
    @Override
    public boolean estPersonnageCible() {
        return true;
    }
    
    /**
     * Indique si le personnage est actuellement sur une cible.
     *
     * @return true si le personnage est sur une cible, false sinon
     */
    public boolean estSurCible() {
        return surCible;
    }
    
    /**
     * Met à jour l'état "sur cible" du personnage.
     *
     * @param surCible true si le personnage est sur une cible, false sinon
     */
    public void setSurCible(boolean surCible) {
        this.surCible = surCible;
    }
    
    /**
     * Retourne le symbole ASCII du personnage.
     * '@' si le personnage n'est pas sur une cible, '+' sinon.
     *
     * @return '@' ou '+'
     */
    @Override
    public char getSymbole() {
        return surCible ? '+' : '@';
    }
    
    /**
     * Retourne une représentation textuelle du personnage.
     *
     * @return "Personnage[@]" ou "Personnage[+]" selon l'état
     */
    @Override
    public String toString() {
        return surCible ? "Personnage[+]" : "Personnage[@]";
    }
    
    /**
     * Compare ce personnage avec un autre objet.
     * Deux personnages sont égaux si leur état "sur cible" est identique.
     *
     * @param obj objet à comparer
     * @return true si obj est un personnage avec le même état, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Personnage)) return false;
        Personnage autre = (Personnage) obj;
        return this.surCible == autre.surCible;
    }
    
    /**
     * Retourne le hashCode du personnage.
     *
     * @return hashCode basé sur l'état surCible
     */
    @Override
    public int hashCode() {
        return Boolean.hashCode(surCible) * 31 + Personnage.class.hashCode();
    }

    @Override
    public Color getCouleurSol() {
        return surCible ? Color.web("#e8ddb7") : Color.web("#e9d8a6");
    }

}