/**
 * Centralise les conversions entre symboles Sokoban et objets Case.
 */
public final class ConvertisseurCases {
    private ConvertisseurCases() {
    }

    /**
     * Convertit un symbole Sokoban en case.
     */
    public static Case depuisSymbole(char symbole, int x, int y) {
        if (Character.isLetter(symbole)) {
            if (Character.isUpperCase(symbole)) {
                return new CaseBoiteMondeSurCible(x, y, symbole);
            }
            return new CaseBoiteMonde(x, y, symbole);
        }

        switch (symbole) {
            case '#':
                return new CaseMur(x, y);
            case '$':
                return new CaseBoite(x, y);
            case '*':
                return new CaseBoiteCible(x, y);
            case '.':
                return new CaseCible(x, y);
            case '@':
                return new CasePersonnage(x, y);
            case '+':
                return new CasePersonnageCible(x, y);
            default:
                return new CaseVide(x, y);
        }
    }

    /**
     * Convertit un symbole tolérant certains alias de vide.
     */
    public static Case depuisSymboleTolerant(char symbole, int x, int y) {
        if (symbole == '-' || symbole == '_') {
            return new CaseVide(x, y);
        }
        return depuisSymbole(symbole, x, y);
    }

    /**
     * @deprecated Utiliser depuisSymboleTolerant pour eviter les accents dans les API.
     */
    @Deprecated
    public static Case depuisSymboleTolérant(char symbole, int x, int y) {
        return depuisSymboleTolerant(symbole, x, y);
    }

    /**
     * Convertit une case en symbole Sokoban.
     */
    public static char versSymbole(Case caseJeu) {
        return caseJeu == null ? ' ' : caseJeu.getSymbole();
    }
}