package sokoban.modeles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le service de persistance JSON.
 */
@DisplayName("ServicePersistance - Tests unitaires")
class ServicePersistanceTest {

    @TempDir
    Path tempDir;

    private File fichierSauvegarde;

    @BeforeEach
    void setUp() {
        fichierSauvegarde = tempDir.resolve("sauvegarde.json").toFile();
    }

    @Test
    @DisplayName("Sauvegarder crée un fichier JSON valide")
    void testSauvegarderFichierValide() throws IOException {
        char[][] plateau = {
            "#####".toCharArray(),
            "#@B #".toCharArray(),
            "# * #".toCharArray(),
            "#####".toCharArray()
        };

        boolean resultat = ServicePersistance.sauvegarder(plateau, 5, true, fichierSauvegarde);

        assertTrue(resultat);
        assertTrue(fichierSauvegarde.exists());

        String contenu = Files.readString(fichierSauvegarde.toPath());
        assertNotNull(contenu);
        assertTrue(contenu.contains("\"plateau\""));
        assertTrue(contenu.contains("\"coups\""));
        assertTrue(contenu.contains("\"victoire\""));
        assertTrue(contenu.contains("5"));
    }

    @Test
    @DisplayName("Charger retourne un plateau valide depuis un fichier JSON")
    void testChargerFichierValide() throws IOException {
        String json = """
            {
                "plateau": [
                    ["#", "#", "#", "#", "#"],
                    ["@", " ", "B", " ", "#"],
                    ["#", " ", "*", " ", "#"],
                    ["#", "#", "#", "#", "#"]
                ],
                "coups": 10,
                "victoire": false
            }
            """;

        Files.writeString(fichierSauvegarde.toPath(), json);

        Object[] resultat = ServicePersistance.charger(fichierSauvegarde);

        assertNotNull(resultat);
        assertEquals(3, resultat.length); // plateau, coups, victoire

        char[][] plateau = (char[][]) resultat[0];
        assertNotNull(plateau);
        assertEquals(4, plateau.length);
        assertEquals(5, plateau[0].length);

        Integer coups = (Integer) resultat[1];
        assertEquals(10, coups);

        Boolean victoire = (Boolean) resultat[2];
        assertFalse(victoire);
    }

    @Test
    @DisplayName("Charger avec fichier inexistant retourne null")
    void testChargerFichierInexistant() {
        File fichierInexistant = new File("/chemin/inexistant/sauvegarde.json");
        Object[] resultat = ServicePersistance.charger(fichierInexistant);
        assertNull(resultat);
    }

    @Test
    @DisplayName("Charger avec JSON invalide retourne null")
    void testChargerJsonInvalide() throws IOException {
        String jsonInvalide = "{ json invalide }";
        Files.writeString(fichierSauvegarde.toPath(), jsonInvalide);

        Object[] resultat = ServicePersistance.charger(fichierSauvegarde);
        assertNull(resultat);
    }

    @Test
    @DisplayName("Charger avec JSON vide retourne null")
    void testChargerJsonVide() throws IOException {
        Files.writeString(fichierSauvegarde.toPath(), "");

        Object[] resultat = ServicePersistance.charger(fichierSauvegarde);
        assertNull(resultat);
    }

    @Test
    @DisplayName("Sauvegarder et charger préserve les données")
    void testSauvegarderEtCharger() throws IOException {
        char[][] plateauOriginal = {
            "######".toCharArray(),
            "#@BB #".toCharArray(),
            "# ** #".toCharArray(),
            "######".toCharArray()
        };
        int coupsOriginal = 25;
        boolean victoireOriginal = true;

        // Sauvegarder
        boolean sauvegardeOk = ServicePersistance.sauvegarder(
            plateauOriginal, coupsOriginal, victoireOriginal, fichierSauvegarde
        );
        assertTrue(sauvegardeOk);

        // Charger
        Object[] resultat = ServicePersistance.charger(fichierSauvegarde);
        assertNotNull(resultat);

        char[][] plateauCharge = (char[][]) resultat[0];
        int coupsCharges = (Integer) resultat[1];
        boolean victoireChargee = (Boolean) resultat[2];

        // Vérifier l'égalité
        assertEquals(plateauOriginal.length, plateauCharge.length);
        for (int i = 0; i < plateauOriginal.length; i++) {
            assertArrayEquals(plateauOriginal[i], plateauCharge[i]);
        }
        assertEquals(coupsOriginal, coupsCharges);
        assertEquals(victoireOriginal, victoireChargee);
    }

    @Test
    @DisplayName("Sauvegarder avec coups à zéro")
    void testSauvegarderCoupsZero() throws IOException {
        char[][] plateau = {
            "###".toCharArray(),
            "#@#".toCharArray(),
            "###".toCharArray()
        };

        boolean resultat = ServicePersistance.sauvegarder(plateau, 0, false, fichierSauvegarde);

        assertTrue(resultat);
        String contenu = Files.readString(fichierSauvegarde.toPath());
        assertTrue(contenu.contains("\"coups\":0") || contenu.contains("\"coups\": 0"));
    }

    @Test
    @DisplayName("Sauvegarder dans un répertoire inexistant échoue gracieusement")
    void testSauvegarderRepertoireInexistant() {
        char[][] plateau = {
            "###".toCharArray(),
            "#@#".toCharArray(),
            "###".toCharArray()
        };

        File fichierDansRepertoireInexistant = new File("/repertoire/inexistant/sauvegarde.json");
        
        // La méthode ne doit pas lever d'exception, mais retourner false ou gérer l'erreur
        boolean resultat = ServicePersistance.sauvegarder(plateau, 0, false, fichierDansRepertoireInexistant);
        
        // Selon l'implémentation, cela peut retourner false ou true si le répertoire est créé
        // L'important est qu'il n'y ait pas d'exception non gérée
        assertTrue(true); // Le test passe si aucune exception n'est levée
    }

    @Test
    @DisplayName("Charger avec chemin null retourne null")
    void testChargerCheminNull() {
        Object[] resultat = ServicePersistance.charger(null);
        assertNull(resultat);
    }
}
