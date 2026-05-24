/* Classe représentant un mur dans le jeu.
 * Un mur est infranchissable et ne peut pas être poussé.
 */

import javafx.scene.paint.Color;

public class CaseMur extends Case {
    
    /**
     * @requires x>=0
     * @requires y>=0
     */
    public CaseMur(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estMur() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return false;
    }

    @Override
    public char getSymbole() {
        return '#';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#517664");
    }
}
