/* Classe représentant une case contenant un personnage dans le jeu.
 * Le personnage peut se déplacer sur les cases vides et pousser les boîtes.
 */

import javafx.scene.paint.Color;

public class CasePersonnage extends Case {
    
    /**
     * @requires x>=0
     * @requires y>=0
     */
    public CasePersonnage(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estTraversable() {
        return false;
    }

    @Override
    public char getSymbole() {
        return '@';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#AB3428");
    }
}
