import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Tests minimaux de persistance JSON, exécutables sans framework externe.
 */
public final class TestsPersistance {
    private TestsPersistance() {
    }

    public static void main(String[] args) throws Exception {
        testRoundTripSauvegardeJson();
        testListingSauvegardes();
        testChargementJsonInvalide();
        System.out.println("[OK] TestsPersistance");
    }

    private static void testRoundTripSauvegardeJson() throws IOException {
        Path chemin = Path.of("sauvegardes", "_tmp_test_roundtrip.json");
        try {
            Case[][] plateau = creerPlateauMinimal();
            String solution = "2r1l";
            ServicePersistance.sauvegarderSessionJson(chemin, "niveau_01.txt", plateau, solution);

            ServicePersistance.SauvegardeChargee chargee = ServicePersistance.chargerSauvegardeJson(chemin);
            verifier("niveau_01.txt".equals(chargee.getNiveau()), "Niveau sauvegarde non conserve");
            verifier(solution.equals(chargee.getSolutionSokobano()), "Solution Sokobano non conservee");
            verifier(chargee.getPlateau() != null, "Plateau charge ne doit pas etre null");
            verifier(chargee.getPlateau().length == plateau.length, "Hauteur plateau incoherente");
            verifier(chargee.getPlateau()[0].length == plateau[0].length, "Largeur plateau incoherente");
        } finally {
            Files.deleteIfExists(chemin);
        }
    }

    private static void testListingSauvegardes() throws IOException {
        Path chemin = Path.of("sauvegardes", "_tmp_test_listing.json");
        try {
            ServicePersistance.sauvegarderSessionJson(
                chemin,
                "niveau_02.txt",
                creerPlateauMinimal(),
                ""
            );

            List<ServicePersistance.SauvegardeInfo> infos = ServicePersistance.listerSauvegardesInfos();
            boolean trouve = false;
            for (ServicePersistance.SauvegardeInfo info : infos) {
                if ("_tmp_test_listing.json".equals(info.getNomFichier())) {
                    trouve = true;
                    break;
                }
            }
            verifier(trouve, "La sauvegarde de test doit apparaitre dans le listing");
        } finally {
            Files.deleteIfExists(chemin);
        }
    }

    private static void testChargementJsonInvalide() throws IOException {
        Path chemin = Path.of("sauvegardes", "_tmp_test_invalide.json");
        try {
            Files.writeString(chemin, "{\"version\":1,\"type\":\"plateau\"}");
            boolean erreurDetectee = false;
            try {
                ServicePersistance.chargerSauvegardeJson(chemin);
            } catch (IllegalArgumentException e) {
                erreurDetectee = true;
            }
            verifier(erreurDetectee, "Le chargement JSON invalide doit lever IllegalArgumentException");
        } finally {
            Files.deleteIfExists(chemin);
        }
    }

    private static Case[][] creerPlateauMinimal() {
        Case[][] plateau = new Case[3][3];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (x == 0 || y == 0 || x == 2 || y == 2) {
                    plateau[y][x] = new CaseMur(x, y);
                } else {
                    plateau[y][x] = new CasePersonnage(x, y);
                }
            }
        }
        return plateau;
    }

    private static void verifier(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}