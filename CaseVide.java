/* Classe représentant une case vide dans le jeu.
 * Une case vide est traversable mais ne contient aucun objet particulier.
 */

import javafx.scene.paint.Color;

public class CaseVide extends Case {
    
    /**
     * @requires x>=0
     * @requires y>=0
     */
    public CaseVide(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estVide() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return true;
    }

    @Override
    public char getSymbole() {
        return ' ';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#E2DBBE");
    }
}
