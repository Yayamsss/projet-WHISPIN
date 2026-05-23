import java.nio.file.Path;
import java.util.ArrayList;

public final class TestPersistanceSmoke {
    private TestPersistanceSmoke() {
    }

    public static void main(String[] args) throws Exception {
        Path source = Path.of("carte_niveau_simple.txt");
        ArrayList<ArrayList<Case>> grille = ServicePersistance.chargerPlateauDepuisFichierTexte(source);

        Path sauvegarde = ServicePersistance.creerCheminSauvegardeNommee("smoke_test");
        ServicePersistance.sauvegarderPlateauDansFichierTexte(sauvegarde, grille, 'T');

        ArrayList<ArrayList<Case>> grilleChargee = ServicePersistance.chargerPlateauDepuisFichierTexte(sauvegarde);

        if (grille.size() != grilleChargee.size() || grille.get(0).size() != grilleChargee.get(0).size()) {
            throw new IllegalStateException("Round-trip persistance invalide: dimensions differentes");
        }

        for (int y = 0; y < grille.size(); y++) {
            for (int x = 0; x < grille.get(y).size(); x++) {
                char a = grille.get(y).get(x).getSymbole();
                char b = grilleChargee.get(y).get(x).getSymbole();
                if (a != b) {
                    throw new IllegalStateException("Round-trip persistance invalide en " + x + "," + y + " : " + a + " != " + b);
                }
            }
        }

        boolean vueDansListe = false;
        for (ServicePersistance.SauvegardeInfo info : ServicePersistance.listerSauvegardesInfos()) {
            if (info.getChemin().equals(sauvegarde)) {
                vueDansListe = true;
                break;
            }
        }
        if (!vueDansListe) {
            throw new IllegalStateException("La sauvegarde n'apparait pas dans la liste");
        }

        ServicePersistance.supprimerSauvegarde(sauvegarde);
        System.out.println("TestPersistanceSmoke OK");
    }
}
