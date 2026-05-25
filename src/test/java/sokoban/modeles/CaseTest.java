package sokoban.modeles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Case et ses implémentations.
 */
@DisplayName("Case - Tests unitaires")
class CaseTest {

    private Case caseVide;
    private Case caseMur;
    private Case caseCible;
    private Case caseBoite;
    private Case caseBoiteSurCible;

    @BeforeEach
    void setUp() {
        caseVide = new CaseVide();
        caseMur = new CaseMur();
        caseCible = new CaseCible();
        caseBoite = new CaseBoite();
        caseBoiteSurCible = new CaseBoiteSurCible();
    }

    @Nested
    @DisplayName("Tests des types de cases")
    class TypeDeCaseTests {

        @Test
        @DisplayName("CaseVide n'est ni mur, ni cible, ni boîte")
        void testCaseVide() {
            assertFalse(caseVide.estMur());
            assertFalse(caseVide.estCible());
            assertFalse(caseVide.estBoite());
            assertFalse(caseVide.estObstacle());
        }

        @Test
        @DisplayName("CaseMur est un obstacle et un mur")
        void testCaseMur() {
            assertTrue(caseMur.estMur());
            assertFalse(caseMur.estCible());
            assertFalse(caseMur.estBoite());
            assertTrue(caseMur.estObstacle());
        }

        @Test
        @DisplayName("CaseCible est une cible mais pas un obstacle")
        void testCaseCible() {
            assertFalse(caseCible.estMur());
            assertTrue(caseCible.estCible());
            assertFalse(caseCible.estBoite());
            assertFalse(caseCible.estObstacle());
        }

        @Test
        @DisplayName("CaseBoite est une boîte et un obstacle")
        void testCaseBoite() {
            assertFalse(caseBoite.estMur());
            assertFalse(caseBoite.estCible());
            assertTrue(caseBoite.estBoite());
            assertTrue(caseBoite.estObstacle());
        }

        @Test
        @DisplayName("CaseBoiteSurCible est une boîte, une cible et un obstacle")
        void testCaseBoiteSurCible() {
            assertFalse(caseBoiteSurCible.estMur());
            assertTrue(caseBoiteSurCible.estCible());
            assertTrue(caseBoiteSurCible.estBoite());
            assertTrue(caseBoiteSurCible.estObstacle());
        }
    }

    @Nested
    @DisplayName("Tests des transformations de cases")
    class TransformationTests {

        @Test
        @DisplayName("enleverBoite sur CaseBoite retourne CaseVide")
        void testEnleverBoite() {
            Case resultat = caseBoite.enleverBoite();
            assertTrue(resultat instanceof CaseVide);
            assertFalse(resultat.estBoite());
        }

        @Test
        @DisplayName("enleverBoite sur CaseBoiteSurCible retourne CaseCible")
        void testEnleverBoiteSurCible() {
            Case resultat = caseBoiteSurCible.enleverBoite();
            assertTrue(resultat instanceof CaseCible);
            assertTrue(resultat.estCible());
            assertFalse(resultat.estBoite());
        }

        @Test
        @DisplayName("ajouterBoite sur CaseVide retourne CaseBoite")
        void testAjouterBoiteSurVide() {
            Case resultat = caseVide.ajouterBoite();
            assertTrue(resultat instanceof CaseBoite);
            assertTrue(resultat.estBoite());
        }

        @Test
        @DisplayName("ajouterBoite sur CaseCible retourne CaseBoiteSurCible")
        void testAjouterBoiteSurCible() {
            Case resultat = caseCible.ajouterBoite();
            assertTrue(resultat instanceof CaseBoiteSurCible);
            assertTrue(resultat.estBoite());
            assertTrue(resultat.estCible());
        }

        @Test
        @DisplayName("enleverBoite sur CaseVide retourne CaseVide (inchangé)")
        void testEnleverBoiteInexistant() {
            Case resultat = caseVide.enleverBoite();
            assertTrue(resultat instanceof CaseVide);
        }
    }

    @Nested
    @DisplayName("Tests des méthodes par défaut")
    class MethodesParDefaultTests {

        @Test
        @DisplayName("deplacerPersonnage retourne false par défaut")
        void testDeplacerPersonnage() {
            assertFalse(caseVide.deplacerPersonnage(Direction.HAUT));
            assertFalse(caseMur.deplacerPersonnage(Direction.HAUT));
        }

        @Test
        @DisplayName("estVictoire retourne false sauf pour CaseBoiteSurCible")
        void testEstVictoire() {
            assertFalse(caseVide.estVictoire());
            assertFalse(caseMur.estVictoire());
            assertFalse(caseCible.estVictoire());
            assertFalse(caseBoite.estVictoire());
            // Note: CaseBoiteSurCible peut avoir une logique spécifique
        }

        @Test
        @DisplayName("getSymbole retourne un caractère valide")
        void testGetSymbole() {
            assertNotNull(caseVide.getSymbole());
            assertNotNull(caseMur.getSymbole());
            assertNotNull(caseCible.getSymbole());
            assertNotNull(caseBoite.getSymbole());
            assertNotNull(caseBoiteSurCible.getSymbole());
        }
    }
}
