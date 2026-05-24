import javafx.scene.paint.Color;

/**
 * Représente un mur dans le jeu Sokoban.
 * Un mur est un élément statique et immuable qui bloque tous les déplacements.
 * Il ne peut être ni traversé ni poussé.
 */
public final class CaseMur extends Case {
    
    // Comme tous les murs sont identiques, une seule instance suffit.
    private static final CaseMur INSTANCE = new CaseMur();
    
    /**
     * Constructeur privé pour empêcher l'instanciation directe.
     * Utiliser getInstance() à la place.
     */
    private CaseMur() {
        super(0, 0);
        // Les murs sont immuables.
    }
    
    /**
     * Retourne l'unique instance de mur.
     *
     * @return l'instance partagée de mur
     */
    public static CaseMur getInstance() {
        return INSTANCE;
    }
    
    /**
     * Indique qu'un mur n'est pas traversable.
     *
     * @return false
     */
    @Override
    public boolean estTraversable() {
        return false;
    }
    
    /**
     * Indique qu'un mur n'est pas poussable.
     *
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * Retourne le symbole ASCII du mur.
     *
     * @return '#'
     */
    @Override
    public char getSymbole() {
        return '#';
    }
    
    /**
     * Retourne une représentation textuelle du mur.
     *
     * @return "Mur[#]"
     */
    @Override
    public String toString() {
        return "Mur[#]";
    }
    
    /**
     * Compare ce mur à un autre objet.
     *
     * @param obj objet à comparer
     * @return true si obj est un mur, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseMur;
    }
    
    /**
     * Retourne un hashCode constant pour les murs.
     *
     * @return hashCode de la classe
     */
    @Override
    public int hashCode() {
        return CaseMur.class.hashCode();
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#85929a");
    }

}