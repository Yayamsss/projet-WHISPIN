/**
 * Représente les quatre directions de déplacement du jeu.
 * Chaque direction contient son delta en x et en y.
 */
public enum Direction {
    
    /** vers le haut (y diminue) */
    HAUT(0, -1, "Haut", "↑"),
    
    /** vers le bas (y augmente) */
    BAS(0, 1, "Bas", "↓"),
    
    /** vers la gauche (x diminue) */
    GAUCHE(-1, 0, "Gauche", "←"),
    
    /** vers la droite (x augmente) */
    DROITE(1, 0, "Droite", "→");
    
    /** Déplacement sur l'axe x. */
    private final int deltaX;
    
    /** Déplacement sur l'axe y. */
    private final int deltaY;
    
    /** Nom lisible de la direction. */
    private final String nom;
    
    /** Symbole de la direction. */
    private final String symbole;
    
    /**
     * Construit une direction.
     *
     * @param deltaX déplacement en x
     * @param deltaY déplacement en y
     * @param nom nom de la direction
     * @param symbole symbole de la direction
     */
    Direction(int deltaX, int deltaY, String nom, String symbole) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.nom = nom;
        this.symbole = symbole;
    }
    
    /**
     * Retourne le déplacement en x.
     *
     * @return valeur x (-1, 0 ou 1)
     */
    public int getDeltaX() {
        return deltaX;
    }
    
    /**
     * Retourne le déplacement en y.
     *
     * @return valeur y (-1, 0 ou 1)
     */
    public int getDeltaY() {
        return deltaY;
    }

    /**
     * @deprecated Utiliser getDeltaX().
     */
    @Deprecated
    public int getgX() {
        return getDeltaX();
    }

    /**
     * @deprecated Utiliser getDeltaY().
     */
    @Deprecated
    public int getgY() {
        return getDeltaY();
    }
    
    /**
     * Retourne le nom de la direction.
     *
     * @return nom ("Haut", "Bas", "Gauche", "Droite")
     */
    public String getNom() {
        return nom;
    }
    
    /**
     * Retourne le symbole de la direction.
     *
     * @return symbole (↑, ↓, ←, →)
     */
    public String getSymbole() {
        return symbole;
    }
    
    /**
     * Retourne la direction opposée.
     *
     * @return direction opposée (HAUT↔BAS, GAUCHE↔DROITE)
     */
    public Direction getOpposee() {
        switch (this) {
            case HAUT: return BAS;
            case BAS: return HAUT;
            case GAUCHE: return DROITE;
            case DROITE: return GAUCHE;
            default: throw new IllegalStateException("Direction inconnue");
        }
    }
    
    /**
     * Indique si la direction est horizontale.
     *
     * @return true pour GAUCHE ou DROITE, false sinon
     */
    public boolean estHorizontale() {
        return this == GAUCHE || this == DROITE;
    }
    
    /**
     * Indique si la direction est verticale.
     *
     * @return true pour HAUT ou BAS, false sinon
     */
    public boolean estVerticale() {
        return this == HAUT || this == BAS;
    }
    
    /**
     * Convertit une lettre en direction (format Sokoban: u, d, l, r).
     *
     * @param lettre lettre représentant la direction
     * @return direction correspondante
     * @throws IllegalArgumentException si la lettre n'est pas valide
     */
    public static Direction fromLettre(char lettre) {
        switch (Character.toLowerCase(lettre)) {
            case 'u': return HAUT;
            case 'd': return BAS;
            case 'l': return GAUCHE;
            case 'r': return DROITE;
            default: throw new IllegalArgumentException("Lettre de direction invalide : " + lettre);
        }
    }
    
    /**
     * Convertit cette direction en lettre (format Sokoban: u, d, l, r).
     *
     * @return lettre représentant la direction
     */
    public char toLettre() {
        switch (this) {
            case HAUT: return 'u';
            case BAS: return 'd';
            case GAUCHE: return 'l';
            case DROITE: return 'r';
            default: throw new IllegalStateException("Direction inconnue");
        }
    }
    
    /**
     * Retourne une représentation textuelle de la direction.
     *
     * @return symbole et nom de la direction
     */
    @Override
    public String toString() {
        return symbole + " " + nom;
    }
}