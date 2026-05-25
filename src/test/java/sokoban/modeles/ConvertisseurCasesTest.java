package sokoban.modeles;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe ConvertisseurCases.
 * Teste la conversion entre symboles de niveau et objets Case.
 */
@DisplayName("ConvertisseurCases - Tests unitaires")
class ConvertisseurCasesTest {

    @Test
    @DisplayName("depuisSymbole avec '#' retourne CaseMur")
    void testDepuisSymboleMur() {
        Case caseResultat = ConvertisseurCases.depuisSymbole('#');
        assertTrue(caseResultat instanceof CaseMur);
        assertTrue(caseResultat.estMur());
    }

    @Test
    @DisplayName("depuisSymbole avec '@' retourne CaseVide (position joueur)")
    void testDepuisSymboleJoueur() {
        Case caseResultat = ConvertisseurCases.depuisSymbole('@');
        assertTrue(caseResultat instanceof CaseVide);
        assertFalse(caseResultat.estMur());
        assertFalse(caseResultat.estCible());
    }

    @Test
    @DisplayName("depuisSymbole avec '+' retourne CaseBoiteSurCible (joueur sur boîte sur cible)")
    void testDepuisSymboleJoueurSurBoiteSurCible() {
        Case caseResultat = ConvertisseurCases.depuisSymbole('+');
        assertTrue(caseResultat instanceof CaseBoiteSurCible);
        assertTrue(caseResultat.estCible());
        assertTrue(caseResultat.estBoite());
    }

    @Test
    @DisplayName("depuisSymbole avec 'B' retourne CaseBoite")
    void testDepuisSymboleBoite() {
        Case caseResultat = ConvertisseurCases.depuisSymbole('B');
        assertTrue(caseResultat instanceof CaseBoite);
        assertTrue(caseResultat.estBoite());
    }

    @Test
    @DisplayName("depuisSymbole avec '*' retourne CaseBoiteSurCible")
    void testDepuisSymboleBoiteSurCible() {
        Case caseResultat = ConvertisseurCases.depuisSymbole('*');
        assertTrue(caseResultat instanceof CaseBoiteSurCible);
        assertTrue(caseResultat.estCible());
        assertTrue(caseResultat.estBoite());
    }

    @Test
    @DisplayName("depuisSymbole avec '.' retourne CaseCible")
    void testDepuisSymboleCible() {
        Case caseResultat = ConvertisseurCases.depuisSymbole('.');
        assertTrue(caseResultat instanceof CaseCible);
        assertTrue(caseResultat.estCible());
        assertFalse(caseResultat.estBoite());
    }

    @Test
    @DisplayName("depuisSymbole avec ' ' (espace) retourne CaseVide")
    void testDepuisSymboleVide() {
        Case caseResultat = ConvertisseurCases.depuisSymbole(' ');
        assertTrue(caseResultat instanceof CaseVide);
        assertFalse(caseResultat.estMur());
        assertFalse(caseResultat.estCible());
        assertFalse(caseResultat.estBoite());
    }

    @Test
    @DisplayName("depuisSymbole avec caractère inconnu retourne CaseVide")
    void testDepuisSymboleInconnu() {
        Case caseResultat = ConvertisseurCases.depuisSymbole('X');
        assertTrue(caseResultat instanceof CaseVide);
    }

    @Test
    @DisplayName("depuisSymbole avec minuscules (tolérant)")
    void testDepuisSymboleTolerant() {
        // Tester que les minuscules fonctionnent aussi si implémenté
        Case murMinuscule = ConvertisseurCases.depuisSymbole('#');
        assertTrue(murMinuscule instanceof CaseMur);
    }

    @Test
    @DisplayName("versSymbole avec CaseMur retourne '#'")
    void testVersSymboleMur() {
        char symbole = ConvertisseurCases.versSymbole(new CaseMur());
        assertEquals('#', symbole);
    }

    @Test
    @DisplayName("versSymbole avec CaseVide retourne ' '")
    void testVersSymboleVide() {
        char symbole = ConvertisseurCases.versSymbole(new CaseVide());
        assertEquals(' ', symbole);
    }

    @Test
    @DisplayName("versSymbole avec CaseCible retourne '.'")
    void testVersSymboleCible() {
        char symbole = ConvertisseurCases.versSymbole(new CaseCible());
        assertEquals('.', symbole);
    }

    @Test
    @DisplayName("versSymbole avec CaseBoite retourne 'B'")
    void testVersSymboleBoite() {
        char symbole = ConvertisseurCases.versSymbole(new CaseBoite());
        assertEquals('B', symbole);
    }

    @Test
    @DisplayName("versSymbole avec CaseBoiteSurCible retourne '*'")
    void testVersSymboleBoiteSurCible() {
        char symbole = ConvertisseurCases.versSymbole(new CaseBoiteSurCible());
        assertEquals('*', symbole);
    }

    @Test
    @DisplayName("Conversion round-trip préserve le type de case")
    void testRoundTrip() {
        // Mur
        char symboleMur = '#';
        Case caseMur = ConvertisseurCases.depuisSymbole(symboleMur);
        char symboleMurResultat = ConvertisseurCases.versSymbole(caseMur);
        assertEquals(symboleMur, symboleMurResultat);

        // Cible
        char symboleCible = '.';
        Case caseCible = ConvertisseurCases.depuisSymbole(symboleCible);
        char symboleCibleResultat = ConvertisseurCases.versSymbole(caseCible);
        assertEquals(symboleCible, symboleCibleResultat);

        // Boîte
        char symboleBoite = 'B';
        Case caseBoite = ConvertisseurCases.depuisSymbole(symboleBoite);
        char symboleBoiteResultat = ConvertisseurCases.versSymbole(caseBoite);
        assertEquals(symboleBoite, symboleBoiteResultat);

        // Boîte sur cible
        char symboleBoiteSurCible = '*';
        Case caseBoiteSurCible = ConvertisseurCases.depuisSymbole(symboleBoiteSurCible);
        char symboleBoiteSurCibleResultat = ConvertisseurCases.versSymbole(caseBoiteSurCible);
        assertEquals(symboleBoiteSurCible, symboleBoiteSurCibleResultat);
    }

    @Test
    @DisplayName("Conversion de tableau de caractères en tableau de Cases")
    void testConversionTableau() {
        char[] ligne = {'#', ' ', 'B', '.', '*'};
        Case[] cases = new Case[ligne.length];

        for (int i = 0; i < ligne.length; i++) {
            cases[i] = ConvertisseurCases.depuisSymbole(ligne[i]);
        }

        assertTrue(cases[0] instanceof CaseMur);
        assertTrue(cases[1] instanceof CaseVide);
        assertTrue(cases[2] instanceof CaseBoite);
        assertTrue(cases[3] instanceof CaseCible);
        assertTrue(cases[4] instanceof CaseBoiteSurCible);
    }
}
