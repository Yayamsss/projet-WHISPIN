import javafx.scene.paint.Color;

/**
 * Représente une cible dans le jeu Sokoban.
 * Une cible est traversable et indique où les boîtes doivent être placées.
 * Le jeu est gagné quand toutes les cibles ont une boîte dessus.
 */
public final class CaseCible extends Case {
    
    // Une instance unique partagée suffit pour toutes les cibles.
    private static final CaseCible INSTANCE = new CaseCible();
    
    /**
     * Constructeur privé pour empêcher l'instanciation directe.
     * Utiliser getInstance() à la place.
     */
    private CaseCible() {
        super(0, 0);
        // Les cibles sont immuables.
    }
    
    /**
     * Retourne l'unique instance de cible.
     *
     * @return l'instance partagée de cible
     */
    public static CaseCible getInstance() {
        return INSTANCE;
    }
    
    /**
     * Indique qu'une cible est traversable.
     *
     * @return true
     */
    @Override
    public boolean estTraversable() {
        return true;
    }
    
    /**
     * Indique qu'une cible n'est pas poussable.
     *
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * Indique que cette case est une cible.
     *
     * @return true
     */
    @Override
    public boolean estCible() {
        return true;
    }
    
    /**
     * Retourne le symbole ASCII de la cible.
     *
     * @return '.'
     */
    @Override
    public char getSymbole() {
        return '.';
    }
    
    /**
     * Retourne une représentation textuelle de la cible.
     *
     * @return "Cible[.]"
     */
    @Override
    public String toString() {
        return "Cible[.]";
    }
    
    /**
     * Compare cette cible à un autre objet.
     *
     * @param obj objet à comparer
     * @return true si obj est une cible, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseCible;
    }
    
    /**
     * Retourne un hashCode constant pour les cibles.
     *
     * @return hashCode de la classe
     */
    @Override
    public int hashCode() {
        return CaseCible.class.hashCode();
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#e8ddb7");
    }

}