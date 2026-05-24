/* Classe représentant une case contenant une boîte dans le jeu.
 * Une boîte peut être poussée par le personnage si elle n'est pas sur un mur.
 */

import javafx.scene.paint.Color;

public class CaseBoite extends Case {
    
    /**
     * @requires x>=0
     * @requires y>=0
     */
    public CaseBoite(int x, int y) {
        super(x, y);
    }

    @Override
    public boolean estBoite() {
        return true;
    }

    @Override
    public boolean estTraversable() {
        return false;
    }

    @Override
    public boolean estPoussable() {
        return true;
    }

    @Override
    public char getSymbole() {
        return '$';
    }

    @Override
    public Color getCouleurSol() {
        return Color.web("#D6B4FC");
    }
}
