/**
 * Associe un type de case à son rendu.
 */
public final class FabriqueRenduCase {
    private static final RenduCase RENDU_VIDE = new RenduCaseVide();
    private static final RenduCase RENDU_MUR = new RenduCaseMur();
    private static final RenduCase RENDU_MONDE = new RenduCaseMonde();
    private static final RenduCase RENDU_BOITE = new RenduCaseBoite();
    private static final RenduCase RENDU_BOITE_CIBLE = new RenduCaseBoiteCible();
    private static final RenduCase RENDU_PERSONNAGE = new RenduCasePersonnage();
    private static final RenduCase RENDU_CIBLE = new RenduCaseCible();

    private FabriqueRenduCase() {
    }

    /**
     * Retourne le rendu adapté à une case donnée.
     *
     * @param caseJeu case à dessiner
     * @return rendu correspondant
     */
    public static RenduCase depuisCase(Case caseJeu) {
        if (caseJeu instanceof CaseMur) {
            return RENDU_MUR;
        }
        if (caseJeu instanceof CaseMonde) {
            return RENDU_MONDE;
        }
        if (caseJeu instanceof CaseBoiteCible) {
            return RENDU_BOITE_CIBLE;
        }
        if (caseJeu instanceof CaseBoite) {
            return RENDU_BOITE;
        }
        if (caseJeu instanceof CasePersonnage) {
            return RENDU_PERSONNAGE;
        }
        if (caseJeu instanceof CaseCible) {
            return RENDU_CIBLE;
        }
        return RENDU_VIDE;
    }
}
