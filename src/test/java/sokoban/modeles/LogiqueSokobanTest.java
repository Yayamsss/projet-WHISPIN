package sokoban.modeles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le moteur de logique Sokoban.
 * Teste les déplacements, la poussée de boîtes et les conditions de victoire.
 */
@DisplayName("LogiqueSokoban - Tests unitaires")
class LogiqueSokobanTest {

    private LogiqueSokoban logique;

    @BeforeEach
    void setUp() {
        logique = new LogiqueSokoban();
    }

    @Test
    @DisplayName("Initialisation : le joueur commence à la position de départ")
    void testInitialisation() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@  #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        
        assertEquals(1, logique.getXJoueur());
        assertEquals(1, logique.getYJoueur());
        assertEquals(0, logique.getCoups());
        assertFalse(logique.estVictoire());
    }

    @Test
    @DisplayName("Déplacement simple vers la droite")
    void testDeplacementSimpleDroite() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@  #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.DROITE);
        
        assertEquals(LogiqueSokoban.RESULTAT_CONTINUER, resultat);
        assertEquals(2, logique.getXJoueur());
        assertEquals(1, logique.getYJoueur());
        assertEquals(1, logique.getCoups());
    }

    @Test
    @DisplayName("Déplacement bloqué par un mur")
    void testDeplacementBloqueParMur() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@# #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.DROITE);
        
        assertEquals(LogiqueSokoban.RESULTAT_BLOCAGE, resultat);
        assertEquals(1, logique.getXJoueur()); // Position inchangée
        assertEquals(1, logique.getYJoueur());
        assertEquals(0, logique.getCoups()); // Aucun coup comptabilisé
    }

    @Test
    @DisplayName("Pousser une boîte sur case vide")
    void testPousserBoiteCaseVide() {
        char[][] niveau = {
            "######".toCharArray(),
            "#@B  #".toCharArray(),
            "######".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.DROITE);
        
        assertEquals(LogiqueSokoban.RESULTAT_CONTINUER, resultat);
        assertEquals(2, logique.getXJoueur()); // Joueur à la place de la boîte
        assertEquals(1, logique.getYJoueur());
        assertEquals(1, logique.getCoups());
        // La boîte devrait être en (3, 1)
    }

    @Test
    @DisplayName("Pousser une boîte bloquée par un mur")
    void testPousserBoiteBloqueeParMur() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@B#".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.DROITE);
        
        assertEquals(LogiqueSokoban.RESULTAT_BLOCAGE, resultat);
        assertEquals(1, logique.getXJoueur()); // Position inchangée
        assertEquals(1, logique.getYJoueur());
        assertEquals(0, logique.getCoups());
    }

    @Test
    @DisplayName("Pousser une boîte sur une cible")
    void testPousserBoiteSurCible() {
        char[][] niveau = {
            "######".toCharArray(),
            "#@B* #".toCharArray(),
            "######".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.DROITE);
        
        assertEquals(LogiqueSokoban.RESULTAT_CONTINUER, resultat);
        assertEquals(2, logique.getXJoueur());
        // Vérifier que la boîte est maintenant sur la cible
    }

    @Test
    @DisplayName("Condition de victoire : toutes les boîtes sur les cibles")
    void testVictoire() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@B#".toCharArray(),
            "# * #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        
        // Déplacer vers le bas pour pousser la boîte sur la cible
        logique.deplacer(Direction.BAS);
        
        assertTrue(logique.estVictoire());
    }

    @Test
    @DisplayName("Pas de victoire si une boîte n'est pas sur une cible")
    void testPasVictoire() {
        char[][] niveau = {
            "######".toCharArray(),
            "#@B  #".toCharArray(),
            "# *  #".toCharArray(),
            "######".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        logique.deplacer(Direction.DROITE);
        
        assertFalse(logique.estVictoire());
    }

    @Test
    @DisplayName("Déplacement vers le haut")
    void testDeplacementHaut() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#   #".toCharArray(),
            "#@  #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.HAUT);
        
        assertEquals(LogiqueSokoban.RESULTAT_CONTINUER, resultat);
        assertEquals(1, logique.getXJoueur());
        assertEquals(2, logique.getYJoueur()); // Y diminue quand on monte
    }

    @Test
    @DisplayName("Déplacement vers le bas")
    void testDeplacementBas() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@  #".toCharArray(),
            "#   #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.BAS);
        
        assertEquals(LogiqueSokoban.RESULTAT_CONTINUER, resultat);
        assertEquals(1, logique.getXJoueur());
        assertEquals(2, logique.getYJoueur()); // Y augmente quand on descend
    }

    @Test
    @DisplayName("Déplacement vers la gauche")
    void testDeplacementGauche() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#  @#".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        int resultat = logique.deplacer(Direction.GAUCHE);
        
        assertEquals(LogiqueSokoban.RESULTAT_CONTINUER, resultat);
        assertEquals(2, logique.getXJoueur()); // X diminue quand on va à gauche
        assertEquals(1, logique.getYJoueur());
    }

    @Test
    @DisplayName("Séquence de coups enregistrée")
    void testSequenceCoups() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@  #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        logique.deplacer(Direction.DROITE);
        logique.deplacer(Direction.DROITE);
        logique.deplacer(Direction.BAS);
        
        String sequence = logique.getSequenceCoups();
        assertNotNull(sequence);
        assertTrue(sequence.length() > 0);
        // La séquence devrait contenir les directions jouées
    }

    @Test
    @DisplayName("Exporter le plateau retourne une grille valide")
    void testExporterPlateau() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@  #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        char[][] export = logique.exporterPlateau();
        
        assertNotNull(export);
        assertEquals(3, export.length);
        assertEquals(5, export[0].length);
    }

    @Test
    @DisplayName("Réinitialisation remet à zéro les coups et la position")
    void testReinitialiser() {
        char[][] niveau = {
            "#####".toCharArray(),
            "#@  #".toCharArray(),
            "#####".toCharArray()
        };
        
        logique.initialiserNiveau(niveau);
        logique.deplacer(Direction.DROITE);
        logique.deplacer(Direction.DROITE);
        
        logique.reinitialiser();
        
        assertEquals(0, logique.getCoups());
        assertEquals(1, logique.getXJoueur());
        assertEquals(1, logique.getYJoueur());
        assertFalse(logique.estVictoire());
    }
}
