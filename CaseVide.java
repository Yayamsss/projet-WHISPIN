import javafx.scene.paint.Color;

/**
 * Représente une case vide dans le jeu Sokoban.
 * Une case vide est traversable par le personnage et les boîtes.
 */
public final class CaseVide extends Case {
    
    // Une instance unique partagée suffit pour les cases vides.
    private static final CaseVide INSTANCE = new CaseVide();
    
    /**
     * Constructeur privé pour empêcher l'instanciation directe.
     * Utiliser getInstance() à la place.
     */
    private CaseVide() {
        super(0, 0);
        // Les cases vides sont immuables.
        
    }
    
    /**
     * Retourne l'unique instance de case vide.
     *
     * @return l'instance partagée de case vide
     */
    public static CaseVide getInstance() {
        return INSTANCE;
    }
    
    /**
     * Une case vide est toujours traversable.
     * 
     * @return true
     */
    @Override
    public boolean estTraversable() {
        return true;
    }
    
    /**
     * Indique qu'une case vide n'est pas poussable.
     *
     * @return false
     */
    @Override
    public boolean estPoussable() {
        return false;
    }
    
    /**
     * Retourne le symbole ASCII de la case vide.
     *
     * @return ' ' (espace)
     */
    @Override
    public char getSymbole() {
        return ' ';
    }
    
    /**
     * Retourne une représentation textuelle de la case vide.
     *
     * @return "CaseVide[ ]"
     */
    @Override
    public String toString() {
        return "CaseVide[ ]";
    }
    
    /**
     * Compare cette case vide à un autre objet.
     *
     * @param obj objet à comparer
     * @return true si obj est une case vide, false sinon
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseVide;
    }
    
    /**
     * Retourne un hashCode constant pour les cases vides.
     *
     * @return hashCode de la classe
     */
    @Override
    public int hashCode() {
        return CaseVide.class.hashCode();
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#e9d8a6");
    }

}