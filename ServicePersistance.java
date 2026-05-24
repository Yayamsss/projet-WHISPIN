import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class ServicePersistance {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Path DOSSIER_NIVEAUX = Path.of("niveau");
    private static final Path DOSSIER_SAUVEGARDES = Path.of("sauvegardes");
    private static final DateTimeFormatter FORMAT_HORODATE = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter FORMAT_AFFICHAGE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String CLE_NIVEAU = "niveau";
    private static final String CLE_COUPS = "coups";
    private static final String CLE_HORODATAGE = "horodatage";
    private static final String CLE_PLATEAU = "plateau";
    private static final String CLE_CHEMIN = "chemin";
    private static final String CLE_MOUVEMENTS = "mouvements";
    private static final String CLE_VERSION = "version";
    private static final String CLE_TYPE = "type";
    private static final String CLE_MONDES_TEXTE = "mondesTexte";
    private static final String CLE_MONDE_COURANT = "mondeCourant";
    private static final int VERSION_SAUVEGARDE_JSON = 1;
    private static final String TYPE_SAUVEGARDE_PLATEAU = "plateau";
    private static final String TYPE_SAUVEGARDE_MULTIVERS = "multivers";

    private ServicePersistance() {
    }

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

    public static final class SauvegardeChargee {
        private final Plateau plateau;
        private final Multivers multivers;

        public SauvegardeChargee(Plateau plateau, Multivers multivers) {
            this.plateau = plateau;
            this.multivers = multivers;
        }

        public Plateau getPlateau() {
            return plateau;
        }

        public Multivers getMultivers() {
            return multivers;
        }
    }

    public static Path getDossierSauvegardes() {
        return DOSSIER_SAUVEGARDES;
    }

    public static Path getDossierNiveaux() {
        return DOSSIER_NIVEAUX;
    }

    public static Path creerCheminSauvegardeAuto() {
        String nom = "auto_" + LocalDateTime.now().format(FORMAT_HORODATE) + ".json";
        return DOSSIER_SAUVEGARDES.resolve(nom);
    }

    public static Path creerCheminSauvegardeNommee(String nomUtilisateur) {
        if (nomUtilisateur == null || nomUtilisateur.isBlank()) {
            throw new IllegalArgumentException("Le nom de sauvegarde est vide");
        }
        String nomNettoye = nomUtilisateur.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        if (nomNettoye.isBlank()) {
            throw new IllegalArgumentException("Le nom de sauvegarde est invalide");
        }
        return DOSSIER_SAUVEGARDES.resolve(nomNettoye + ".json");
    }

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
                        LocalDateTime date = LocalDateTime.ofInstant(derniereModif.toInstant(), java.time.ZoneId.systemDefault());
                        sauvegardes.add(new SauvegardeInfo(p, taille, date));
                    } catch (IOException e) {
                        // Ignore un fichier illisible pour ne pas bloquer toute la liste.
                    }
                });
        }
        return sauvegardes;
    }

    public static ArrayList<Path> listerNiveauxTexte() throws IOException {
        if (!Files.exists(DOSSIER_NIVEAUX)) {
            return new ArrayList<>();
        }

        ArrayList<Path> niveaux = new ArrayList<>();
        try (Stream<Path> flux = Files.list(DOSSIER_NIVEAUX)) {
            flux
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".txt"))
                .sorted(Comparator.comparing((Path p) -> p.getFileName().toString()))
                .forEach(niveaux::add);
        }
        return niveaux;
    }

    /**
     * Détecte si un fichier niveau contient plusieurs mondes (Sokoban récursif).
     * Un fichier est considéré multi-monde s'il contient au moins 2 en-têtes "lettre taille".
     */
    public static boolean estFichierMultiMonde(Path cheminFichier) throws IOException {
        java.util.regex.Pattern pat = java.util.regex.Pattern.compile("^([A-Za-z])\\s+(\\d+)$");
        long compteEntetes = Files.lines(cheminFichier)
            .map(String::trim)
            .filter(l -> pat.matcher(l).matches())
            .count();
        return compteEntetes >= 2;
    }

    /**
     * Charge tous les mondes d'un fichier multi-monde et retourne un {@link Multivers}.
     * Le premier monde du fichier est le monde racine (plateau de jeu affiché par défaut).
     */
    public static Multivers chargerMultivers(Path cheminFichier) throws IOException {
        java.util.Map<Character, ArrayList<ArrayList<Case>>> grilles =
            PlateauTexteFichier.chargerTousLesMondes(cheminFichier);
        return Multivers.depuisGrilles(grilles);
    }

    public static boolean supprimerSauvegarde(Path cheminFichier) throws IOException {
        if (cheminFichier == null) {
            return false;
        }
        if (!cheminFichier.normalize().startsWith(DOSSIER_SAUVEGARDES.normalize())) {
            throw new IllegalArgumentException("Suppression interdite hors dossier sauvegardes");
        }
        return Files.deleteIfExists(cheminFichier);
    }

    /**
     * Sauvegarde une grille Sokoban dans un fichier texte.
     *
     * @param chemin chemin du fichier cible
     * @param grille grille à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderPlateauDansFichierTexte(Path cheminFichier, ArrayList<ArrayList<Case>> grillePlateau) throws IOException {
        PlateauTexteFichier.sauvegarderDansFichierTexte(cheminFichier, grillePlateau);
    }

    /**
     * Sauvegarde une grille Sokoban dans un fichier texte avec en-tete "lettre taille".
     *
     * @param cheminFichier chemin du fichier cible
     * @param grillePlateau grille a sauvegarder
     * @param lettreNiveau identifiant de niveau (ex: A, B, C)
     * @throws IOException en cas d'erreur d'ecriture
     */
    public static void sauvegarderPlateauDansFichierTexte(
        Path cheminFichier,
        ArrayList<ArrayList<Case>> grillePlateau,
        char lettreNiveau
    ) throws IOException {
        PlateauTexteFichier.sauvegarderDansFichierTexte(cheminFichier, grillePlateau, lettreNiveau);
    }

    /**
     * Sauvegarde tous les mondes d'un Multivers dans un fichier texte multi-monde.
     *
     * @param cheminFichier chemin du fichier cible
     * @param multivers     le Multivers à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderMultivers(Path cheminFichier, Multivers multivers) throws IOException {
        Map<Character, ArrayList<ArrayList<Case>>> grilles = new java.util.LinkedHashMap<>();
        for (Map.Entry<Character, Plateau> entree : multivers.getTousLesMondes().entrySet()) {
            grilles.put(entree.getKey(), entree.getValue().getGrille());
        }
        PlateauTexteFichier.sauvegarderTousLesMondes(cheminFichier, grilles);
    }

    /**
     * Sauvegarde la session courante en JSON.
     * Le contenu plateau/multivers est stocke sous forme de lignes ASCII multi-monde.
     */
    public static void sauvegarderSessionJson(Path cheminFichier, Plateau plateau, Multivers multivers) throws IOException {
        Map<Character, ArrayList<ArrayList<Case>>> mondes = new LinkedHashMap<>();
        String type;
        char mondeCourant;

        if (multivers != null) {
            for (Map.Entry<Character, Plateau> entree : multivers.getTousLesMondes().entrySet()) {
                mondes.put(entree.getKey(), entree.getValue().getGrille());
            }
            type = TYPE_SAUVEGARDE_MULTIVERS;
            mondeCourant = multivers.getMondeCourantId();
        } else {
            mondes.put('A', plateau.getGrille());
            type = TYPE_SAUVEGARDE_PLATEAU;
            mondeCourant = 'A';
        }

        Map<String, Object> donneesJson = new LinkedHashMap<>();
        donneesJson.put(CLE_VERSION, VERSION_SAUVEGARDE_JSON);
        donneesJson.put(CLE_TYPE, type);
        donneesJson.put(CLE_MONDE_COURANT, String.valueOf(Character.toUpperCase(mondeCourant)));
        donneesJson.put(CLE_MONDES_TEXTE, PlateauTexteFichier.convertirTousLesMondesVersLignes(mondes));

        ecrireDansFichierJson(cheminFichier, donneesJson);
    }

    /**
     * Charge une sauvegarde utilisateur.
     * Format cible: JSON. Fallback legacy: texte multi-monde / simple monde.
     */
    public static SauvegardeChargee chargerSauvegarde(Path cheminFichier) throws IOException {
        String nom = cheminFichier.getFileName().toString().toLowerCase();
        if (nom.endsWith(".json")) {
            return chargerSauvegardeJson(cheminFichier);
        }

        // Fallback legacy pour d'anciennes sauvegardes texte
        if (estFichierMultiMonde(cheminFichier)) {
            Multivers mv = chargerMultivers(cheminFichier);
            return new SauvegardeChargee(mv.getPlateauCourant(), mv);
        }

        Plateau plateau = new Plateau(chargerPlateauDepuisFichierTexte(cheminFichier));
        return new SauvegardeChargee(plateau, null);
    }

    private static SauvegardeChargee chargerSauvegardeJson(Path cheminFichier) throws IOException {
        Map<String, Object> donneesJson = MAPPER.readValue(
            Files.readString(cheminFichier),
            new TypeReference<Map<String, Object>>() {
            }
        );

        String type = lireChampTexte(donneesJson.get(CLE_TYPE), CLE_TYPE);
        ArrayList<String> lignesMondes = lireChampListeTexte(donneesJson.get(CLE_MONDES_TEXTE), CLE_MONDES_TEXTE);
        Map<Character, ArrayList<ArrayList<Case>>> mondes = PlateauTexteFichier.chargerTousLesMondesDepuisLignes(lignesMondes);

        if (TYPE_SAUVEGARDE_MULTIVERS.equals(type)) {
            Multivers multivers = Multivers.depuisGrilles(mondes);
            return new SauvegardeChargee(multivers.getPlateauCourant(), multivers);
        }

        Plateau plateau = new Plateau(mondes.values().iterator().next());
        return new SauvegardeChargee(plateau, null);
    }

    /**
     * Charge une grille Sokoban depuis un fichier texte.
     *
     * @param chemin chemin du fichier source
     * @return la grille chargée
     * @throws IOException en cas d'erreur de lecture
     */
    public static ArrayList<ArrayList<Case>> chargerPlateauDepuisFichierTexte(Path cheminFichier) throws IOException {
        return PlateauTexteFichier.chargerDepuisFichierTexte(cheminFichier);
    }

    /**
     * Sauvegarde l'état complet d'une partie dans un fichier JSON.
     *
     * @param chemin chemin du fichier cible
     * @param etat état de partie à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderPartieDansFichierJson(Path cheminFichier, EtatPartie etatPartie) throws IOException {
        Map<String, Object> donneesJson = new LinkedHashMap<>();
        donneesJson.put(CLE_NIVEAU, etatPartie.getNiveau());
        donneesJson.put(CLE_COUPS, etatPartie.getCoups());
        donneesJson.put(CLE_HORODATAGE, etatPartie.getHorodatage());
        donneesJson.put(CLE_PLATEAU, new ArrayList<>(etatPartie.getPlateau()));

        donneesJson.put(CLE_CHEMIN, convertirMouvementsEnCodes(etatPartie.getChemin()));

        ecrireDansFichierJson(cheminFichier, donneesJson);
    }

    /**
     * Charge l'état complet d'une partie depuis un fichier JSON.
     *
     * @param chemin chemin du fichier source
     * @return état de partie reconstruit
     * @throws IOException en cas d'erreur de lecture
     */
    public static EtatPartie chargerPartieDepuisFichierJson(Path cheminFichier) throws IOException {
        Map<String, Object> donneesJson = MAPPER.readValue(
            Files.readString(cheminFichier),
            new TypeReference<Map<String, Object>>() {
            }
        );

        String niveau = lireChampTexte(donneesJson.get(CLE_NIVEAU), CLE_NIVEAU);
        int coups = lireChampEntier(donneesJson.get(CLE_COUPS), CLE_COUPS);
        long horodatage = lireChampLong(donneesJson.get(CLE_HORODATAGE), CLE_HORODATAGE);

        ArrayList<String> lignesPlateau = lireChampListeTexte(donneesJson.get(CLE_PLATEAU), CLE_PLATEAU);
        ArrayList<String> codesMouvements = lireChampListeTexte(donneesJson.get(CLE_CHEMIN), CLE_CHEMIN);
        ArrayList<Mouvement> mouvementsChemin = new ArrayList<>();
        for (String codeMouvement : codesMouvements) {
            if (codeMouvement.length() != 1) {
                throw new IllegalArgumentException("Code mouvement invalide: " + codeMouvement);
            }
            mouvementsChemin.add(Mouvement.depuisCode(codeMouvement.charAt(0)));
        }

        return new EtatPartie(niveau, coups, horodatage, lignesPlateau, mouvementsChemin);
    }

    /**
     * Sauvegarde une liste de mouvements dans un fichier JSON.
     *
     * @param chemin chemin du fichier cible
     * @param mouvements mouvements à sauvegarder
     * @throws IOException en cas d'erreur d'écriture
     */
    public static void sauvegarderCheminDansFichierJson(Path cheminFichier, ArrayList<Mouvement> listeMouvements) throws IOException {
        Map<String, Object> donneesJson = new LinkedHashMap<>();

        ArrayList<String> codesMouvements = new ArrayList<>();
        for (Mouvement mouvement : listeMouvements) {
            codesMouvements.add(String.valueOf(mouvement.obtenirCode()));
        }
        donneesJson.put(CLE_MOUVEMENTS, codesMouvements);
        ecrireDansFichierJson(cheminFichier, donneesJson);
    }

    /**
     * Charge une liste de mouvements depuis un fichier JSON.
     *
     * @param chemin chemin du fichier source
     * @return liste des mouvements chargés
     * @throws IOException en cas d'erreur de lecture
     */
    public static ArrayList<Mouvement> chargerCheminDepuisFichierJson(Path cheminFichier) throws IOException {
        Map<String, Object> donneesJson = MAPPER.readValue(
            Files.readString(cheminFichier),
            new TypeReference<Map<String, Object>>() {
            }
        );
        ArrayList<String> codesMouvements = lireChampListeTexte(donneesJson.get(CLE_MOUVEMENTS), CLE_MOUVEMENTS);

        ArrayList<Mouvement> mouvementsCharges = new ArrayList<>();
        for (String codeMouvement : codesMouvements) {
            if (codeMouvement.length() != 1) {
                throw new IllegalArgumentException("Code mouvement invalide: " + codeMouvement);
            }
            mouvementsCharges.add(Mouvement.depuisCode(codeMouvement.charAt(0)));
        }
        return mouvementsCharges;
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

    private static int lireChampEntier(Object valeurChamp, String nomChamp) {
        if (valeurChamp instanceof Number nombre) {
            return nombre.intValue();
        }
        throw new IllegalArgumentException("Champ JSON invalide (int attendu): " + nomChamp);
    }

    private static long lireChampLong(Object valeurChamp, String nomChamp) {
        if (valeurChamp instanceof Number nombre) {
            return nombre.longValue();
        }
        throw new IllegalArgumentException("Champ JSON invalide (long attendu): " + nomChamp);
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

    private static List<String> convertirMouvementsEnCodes(List<Mouvement> mouvements) {
        List<String> codesMouvements = new ArrayList<>();
        for (Mouvement mouvement : mouvements) {
            codesMouvements.add(String.valueOf(mouvement.obtenirCode()));
        }
        return codesMouvements;
    }
}
