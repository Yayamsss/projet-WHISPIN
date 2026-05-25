import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Gère la persistance des sauvegardes et des niveaux.
 */
public final class ServicePersistance {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Path DOSSIER_SAUVEGARDES = Path.of("sauvegardes");
    private static final DateTimeFormatter FORMAT_HORODATE = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter FORMAT_AFFICHAGE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String CLE_VERSION = "version";
    private static final String CLE_TYPE = "type";
    private static final String CLE_NIVEAU = "niveau";
    private static final String CLE_HORODATAGE = "horodatage";
    private static final String CLE_PLATEAU = "plateau";

    private static final int VERSION_SAUVEGARDE_JSON = 1;
    private static final String TYPE_SAUVEGARDE_PLATEAU = "plateau";

    private ServicePersistance() {
    }

    /**
     * Métadonnées d'une sauvegarde.
     */
    public static final class SauvegardeInfo {
        private final Path chemin;
        private final long tailleOctets;
        private final LocalDateTime dateModification;

        public SauvegardeInfo(Path chemin, long tailleOctets, LocalDateTime dateModification) {
            this.chemin = chemin;
            this.tailleOctets = tailleOctets;
            this.dateModification = dateModification;
        }

        public Path getChemin() {
            return chemin;
        }

        public String getNomFichier() {
            return chemin.getFileName().toString();
        }

        public long getTailleOctets() {
            return tailleOctets;
        }

        public LocalDateTime getDateModification() {
            return dateModification;
        }

        public String getDateModificationFormatee() {
            return dateModification.format(FORMAT_AFFICHAGE);
        }
    }

    /**
     * Contenu chargé depuis une sauvegarde JSON.
     */
    public static final class SauvegardeChargee {
        private final String niveau;
        private final Case[][] plateau;

        public SauvegardeChargee(String niveau, Case[][] plateau) {
            this.niveau = niveau;
            this.plateau = plateau;
        }

        public String getNiveau() {
            return niveau;
        }

        public Case[][] getPlateau() {
            return plateau;
        }
    }

    /**
     * Crée un chemin de sauvegarde automatique horodaté.
     */
    public static Path creerCheminSauvegardeAuto() {
        String nom = "auto_" + LocalDateTime.now().format(FORMAT_HORODATE) + ".json";
        return DOSSIER_SAUVEGARDES.resolve(nom);
    }

    /**
     * Crée un chemin de sauvegarde personnalisé (nom nettoyé + extension .json).
     */
    public static Path creerCheminSauvegardePersonnalisee(String nomSouhaite) {
        String nomBase = nomSouhaite == null ? "" : nomSouhaite.trim();
        if (nomBase.isEmpty()) {
            nomBase = "manual_" + LocalDateTime.now().format(FORMAT_HORODATE);
        }

        nomBase = nomBase.replaceAll("[^A-Za-z0-9._-]", "_");
        if (nomBase.isEmpty()) {
            nomBase = "manual_" + LocalDateTime.now().format(FORMAT_HORODATE);
        }

        if (!nomBase.toLowerCase().endsWith(".json")) {
            nomBase = nomBase + ".json";
        }

        return DOSSIER_SAUVEGARDES.resolve(nomBase);
    }

    /**
     * Liste les sauvegardes JSON triées de la plus récente à la plus ancienne.
     */
    public static ArrayList<SauvegardeInfo> listerSauvegardesInfos() throws IOException {
        if (!Files.exists(DOSSIER_SAUVEGARDES)) {
            return new ArrayList<>();
        }

        ArrayList<SauvegardeInfo> sauvegardes = new ArrayList<>();
        try (Stream<Path> flux = Files.list(DOSSIER_SAUVEGARDES)) {
            flux
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".json"))
                .sorted(Comparator.comparing((Path p) -> {
                    try {
                        return Files.getLastModifiedTime(p);
                    } catch (IOException e) {
                        return FileTime.fromMillis(0L);
                    }
                }).reversed())
                .forEach(p -> {
                    try {
                        long taille = Files.size(p);
                        FileTime derniereModif = Files.getLastModifiedTime(p);
                        LocalDateTime date = LocalDateTime.ofInstant(derniereModif.toInstant(), ZoneId.systemDefault());
                        sauvegardes.add(new SauvegardeInfo(p, taille, date));
                    } catch (IOException e) {
                        // Ignore les entrées illisibles pour ne pas bloquer toute la liste.
                    }
                });
        }
        return sauvegardes;
    }

    /**
     * Sauvegarde un plateau en JSON.
     *
     * @param cheminFichier fichier cible
     * @param nomNiveau nom du niveau courant
     * @param plateau plateau courant
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderSessionJson(Path cheminFichier, String nomNiveau, Case[][] plateau) throws IOException {
        Map<String, Object> donneesJson = new LinkedHashMap<>();
        donneesJson.put(CLE_VERSION, VERSION_SAUVEGARDE_JSON);
        donneesJson.put(CLE_TYPE, TYPE_SAUVEGARDE_PLATEAU);
        donneesJson.put(CLE_NIVEAU, nomNiveau == null ? "inconnu" : nomNiveau);
        donneesJson.put(CLE_HORODATAGE, System.currentTimeMillis());
        donneesJson.put(CLE_PLATEAU, convertirPlateauVersLignes(plateau));

        ecrireDansFichierJson(cheminFichier, donneesJson);
    }

    /**
     * Charge une sauvegarde JSON.
     *
     * @param cheminFichier fichier source
     * @return contenu chargé
     * @throws IOException en cas d'erreur de lecture
     */
    public static SauvegardeChargee chargerSauvegardeJson(Path cheminFichier) throws IOException {
        Map<String, Object> donneesJson = MAPPER.readValue(
            Files.readString(cheminFichier),
            new TypeReference<Map<String, Object>>() {
            }
        );

        String niveau = lireChampTexte(donneesJson.get(CLE_NIVEAU), CLE_NIVEAU);
        ArrayList<String> lignes = lireChampListeTexte(donneesJson.get(CLE_PLATEAU), CLE_PLATEAU);
        Case[][] plateau = convertirLignesVersPlateau(lignes);
        return new SauvegardeChargee(niveau, plateau);
    }

    private static void ecrireDansFichierJson(Path cheminFichier, Map<String, Object> donneesJson) throws IOException {
        if (cheminFichier.getParent() != null) {
            Files.createDirectories(cheminFichier.getParent());
        }
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(cheminFichier.toFile(), donneesJson);
    }

    private static String lireChampTexte(Object valeurChamp, String nomChamp) {
        if (valeurChamp instanceof String texte) {
            return texte;
        }
        throw new IllegalArgumentException("Champ JSON invalide (String attendu): " + nomChamp);
    }

    private static ArrayList<String> lireChampListeTexte(Object valeurChamp, String nomChamp) {
        if (!(valeurChamp instanceof List<?> listeValeurs)) {
            throw new IllegalArgumentException("Champ JSON invalide (liste attendue): " + nomChamp);
        }

        ArrayList<String> resultat = new ArrayList<>();
        for (Object valeurElement : listeValeurs) {
            if (!(valeurElement instanceof String texte)) {
                throw new IllegalArgumentException("Champ JSON invalide (liste de String attendue): " + nomChamp);
            }
            resultat.add(texte);
        }
        return resultat;
    }

    private static List<String> convertirPlateauVersLignes(Case[][] plateau) {
        ArrayList<String> lignes = new ArrayList<>();
        if (plateau == null) {
            return lignes;
        }

        for (Case[] ligne : plateau) {
            if (ligne == null) {
                lignes.add("");
                continue;
            }

            StringBuilder sb = new StringBuilder();
            for (Case caseJeu : ligne) {
                sb.append(caseJeu == null ? ' ' : caseJeu.getSymbole());
            }
            lignes.add(sb.toString());
        }
        return lignes;
    }

    private static Case[][] convertirLignesVersPlateau(List<String> lignes) {
        if (lignes == null || lignes.isEmpty()) {
            return null;
        }

        int hauteur = lignes.size();
        int largeur = 0;
        for (String ligne : lignes) {
            largeur = Math.max(largeur, ligne == null ? 0 : ligne.length());
        }

        Case[][] plateau = new Case[hauteur][largeur];
        for (int y = 0; y < hauteur; y++) {
            String ligne = lignes.get(y) == null ? "" : lignes.get(y);
            for (int x = 0; x < largeur; x++) {
                char c = x < ligne.length() ? ligne.charAt(x) : ' ';
                plateau[y][x] = ConvertisseurCases.depuisSymbole(c, x, y);
            }
        }
        return plateau;
    }
}
