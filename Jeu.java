import java.nio.file.Path;
import java.util.ArrayList;

public class Jeu {
    private static final Path CHEMIN_CARTE_PAR_DEFAUT = Path.of("carte_niveau_simple.txt");

    private final Plateau plateau;

    public Jeu() {
        this.plateau = chargerPlateauDeTest();
    }

    private static Plateau chargerPlateauDeTest() {
        try {
            ArrayList<ArrayList<Case>> grille = PlateauTexteFichier.chargerDepuisFichierTexte(CHEMIN_CARTE_PAR_DEFAUT);
            return new Plateau(grille);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur : impossible de charger la carte !", e);
        }
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public void lancer() {
        System.out.println("Bienvenue dans le jeu de Sokoban !");
        System.out.println(plateau);
    }
}