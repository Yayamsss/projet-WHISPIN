import java.util.LinkedHashMap;
import java.util.List;

/**
 * Tests de non-regression sur les fichiers de niveaux.
 */
public final class TestsNiveaux {
    private TestsNiveaux() {
    }

    public static void main(String[] args) {
        testChargementTousLesNiveaux();
        System.out.println("[OK] TestsNiveaux");
    }

    private static void testChargementTousLesNiveaux() {
        List<String> niveaux = ParcourirFichiers.listerFichiers("niveau", ".txt");
        verifier(!niveaux.isEmpty(), "Aucun niveau detecte dans le dossier niveau");

        for (String nomNiveau : niveaux) {
            ChargeurNiveau.NiveauCharge niveau = ChargeurNiveau.chargerNiveauRecursifDepuisFichier("niveau/" + nomNiveau);
            verifier(niveau != null, "Chargement echoue pour " + nomNiveau);
            verifier(niveau.getPlateauRacine() != null, "Plateau racine absent pour " + nomNiveau);

            LinkedHashMap<Character, Case[][]> mondes = niveau.getMondes();
            verifier(mondes != null && !mondes.isEmpty(), "Aucun monde charge pour " + nomNiveau);

            for (java.util.Map.Entry<Character, Case[][]> entree : mondes.entrySet()) {
                char idMonde = Character.toUpperCase(entree.getKey());
                Case[][] plateau = entree.getValue();
                verifier(plateau != null && plateau.length > 0, "Monde vide " + idMonde + " dans " + nomNiveau);
                verifier(plateau[0] != null && plateau[0].length > 0, "Monde sans largeur " + idMonde + " dans " + nomNiveau);

                for (int y = 0; y < plateau.length; y++) {
                    for (int x = 0; x < plateau[y].length; x++) {
                        Case caseJeu = plateau[y][x];
                        if (caseJeu == null) {
                            continue;
                        }

                        char symbole = caseJeu.getSymbole();
                        if (Character.isLetter(symbole)) {
                            char reference = Character.toUpperCase(symbole);
                            verifier(
                                mondes.containsKey(reference),
                                "Reference monde inconnue " + reference + " depuis " + idMonde + " dans " + nomNiveau
                            );
                        }
                    }
                }
            }

            LogiqueSokoban moteur = new LogiqueSokoban(niveau);
            Case[][] export = moteur.exporterPlateau();
            verifier(export != null, "Export moteur null pour " + nomNiveau);
            verifier(moteur.rejouerCoupsSokobano(""), "Rejeu vide Sokobano doit reussir pour " + nomNiveau);
        }
    }

    private static void verifier(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}