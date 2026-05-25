import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Charge un niveau texte en matrice de cases.
 */
public final class ChargeurNiveau {
    private static final String MOTIF_ENTETE_NIVEAU = "^[A-Za-z]\\s+\\d+$";

    private ChargeurNiveau() {
    }

    /**
     * Contient un niveau potentiellement récursif (plusieurs mondes nommés).
     */
    public static final class NiveauCharge {
        private final char identifiantRacine;
        private final LinkedHashMap<Character, Case[][]> mondes;

        public NiveauCharge(char identifiantRacine, LinkedHashMap<Character, Case[][]> mondes) {
            this.identifiantRacine = Character.toUpperCase(identifiantRacine);
            this.mondes = mondes;
        }

        public char getIdentifiantRacine() {
            return identifiantRacine;
        }

        public LinkedHashMap<Character, Case[][]> getMondes() {
            return mondes;
        }

        public Case[][] getPlateauRacine() {
            return mondes.get(identifiantRacine);
        }
    }

    /**
     * Charge un niveau depuis un fichier texte.
     *
     * @param cheminFichier chemin vers le fichier de niveau
     * @return matrice de cases, ou null en cas d'erreur
     */
    public static Case[][] chargerDepuisFichier(String cheminFichier) {
        NiveauCharge niveau = chargerNiveauRecursifDepuisFichier(cheminFichier);
        return niveau == null ? null : niveau.getPlateauRacine();
    }

    /**
     * Charge un niveau (simple ou récursif) depuis un fichier texte.
     *
     * @param cheminFichier chemin vers le fichier de niveau
     * @return structure complète du niveau, ou null en cas d'erreur
     */
    public static NiveauCharge chargerNiveauRecursifDepuisFichier(String cheminFichier) {
        try {
            List<String> lignes = Files.readAllLines(Path.of(cheminFichier), StandardCharsets.UTF_8);
            if (lignes.isEmpty()) {
                return null;
            }

            if (!lignes.get(0).trim().matches(MOTIF_ENTETE_NIVEAU)) {
                Case[][] plateauSimple = convertirLignesEnPlateau(lignes);
                if (plateauSimple == null) {
                    return null;
                }
                LinkedHashMap<Character, Case[][]> mondes = new LinkedHashMap<>();
                mondes.put('A', plateauSimple);
                return new NiveauCharge('A', mondes);
            }

            LinkedHashMap<Character, String[]> grillesAscii = new LinkedHashMap<>();
            int index = 0;
            while (index < lignes.size()) {
                String entete = lignes.get(index).trim();
                if (entete.isEmpty()) {
                    index++;
                    continue;
                }

                if (!entete.matches(MOTIF_ENTETE_NIVEAU)) {
                    return null;
                }

                String[] parties = entete.split("\\s+");
                char identifiant = Character.toUpperCase(parties[0].charAt(0));
                int hauteurMonde = Integer.parseInt(parties[1]);
                index++;

                if (hauteurMonde <= 0 || index + hauteurMonde > lignes.size()) {
                    return null;
                }

                String[] grille = new String[hauteurMonde];
                for (int y = 0; y < hauteurMonde; y++) {
                    grille[y] = lignes.get(index + y);
                }
                grillesAscii.put(identifiant, grille);
                index += hauteurMonde;
            }

            if (grillesAscii.isEmpty()) {
                return null;
            }

            Set<Character> racinesCandidates = new LinkedHashSet<>(grillesAscii.keySet());
            for (String[] grille : grillesAscii.values()) {
                for (String ligne : grille) {
                    for (int i = 0; i < ligne.length(); i++) {
                        char c = ligne.charAt(i);
                        if (Character.isLetter(c)) {
                            racinesCandidates.remove(Character.toUpperCase(c));
                        }
                    }
                }
            }

            char racine = racinesCandidates.isEmpty()
                ? grillesAscii.keySet().iterator().next()
                : racinesCandidates.iterator().next();

            LinkedHashMap<Character, Case[][]> mondes = new LinkedHashMap<>();
            for (Map.Entry<Character, String[]> entree : grillesAscii.entrySet()) {
                Case[][] monde = convertirGrilleAsciiEnPlateau(entree.getValue());
                if (monde == null) {
                    return null;
                }
                mondes.put(entree.getKey(), monde);
            }

            return new NiveauCharge(racine, mondes);
        } catch (IOException e) {
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Case[][] convertirLignesEnPlateau(List<String> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            return null;
        }

        int hauteur = lignes.size();
        int largeur = 0;
        for (String ligne : lignes) {
            largeur = Math.max(largeur, ligne == null ? 0 : ligne.length());
        }
        if (largeur == 0) {
            return null;
        }

        Case[][] plateau = new Case[hauteur][largeur];
        for (int y = 0; y < hauteur; y++) {
            String ligne = lignes.get(y) == null ? "" : lignes.get(y);
            for (int x = 0; x < largeur; x++) {
                char c = x < ligne.length() ? ligne.charAt(x) : ' ';
                plateau[y][x] = ConvertisseurCases.depuisSymboleTolerant(c, x, y);
            }
        }
        return plateau;
    }

    private static Case[][] convertirGrilleAsciiEnPlateau(String[] grille) {
        if (grille == null) {
            return null;
        }

        int hauteur = grille.length;
        int largeur = 0;
        for (String ligne : grille) {
            largeur = Math.max(largeur, ligne == null ? 0 : ligne.length());
        }
        if (hauteur == 0 || largeur == 0) {
            return null;
        }

        Case[][] plateau = new Case[hauteur][largeur];
        for (int y = 0; y < hauteur; y++) {
            String ligne = grille[y] == null ? "" : grille[y];
            for (int x = 0; x < largeur; x++) {
                char c = x < ligne.length() ? ligne.charAt(x) : ' ';
                plateau[y][x] = ConvertisseurCases.depuisSymboleTolerant(c, x, y);
            }
        }
        return plateau;
    }
}
