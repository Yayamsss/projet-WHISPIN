import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Charge un niveau texte en matrice de cases.
 */
public final class ChargeurNiveau {
    private static final String MOTIF_ENTETE_NIVEAU = "^[A-Za-z]\\s+\\d+$";

    private ChargeurNiveau() {
    }

    /**
     * Charge un niveau depuis un fichier texte.
     *
     * @param cheminFichier chemin vers le fichier de niveau
     * @return matrice de cases, ou null en cas d'erreur
     */
    public static Case[][] chargerDepuisFichier(String cheminFichier) {
        try {
            List<String> lignes = Files.readAllLines(Path.of(cheminFichier), StandardCharsets.UTF_8);
            if (!lignes.isEmpty() && lignes.get(0).trim().matches(MOTIF_ENTETE_NIVEAU)) {
                lignes = lignes.subList(1, lignes.size());
            }

            if (lignes.isEmpty()) {
                return null;
            }

            int hauteur = lignes.size();
            int largeur = 0;
            for (String ligne : lignes) {
                largeur = Math.max(largeur, ligne.length());
            }
            if (largeur == 0) {
                return null;
            }

            Case[][] plateau = new Case[hauteur][largeur];
            for (int y = 0; y < hauteur; y++) {
                String ligne = lignes.get(y);
                for (int x = 0; x < largeur; x++) {
                    char c = x < ligne.length() ? ligne.charAt(x) : ' ';
                    plateau[y][x] = ConvertisseurCases.depuisSymboleTolérant(c, x, y);
                }
            }
            return plateau;
        } catch (IOException e) {
            return null;
        }
    }
}
