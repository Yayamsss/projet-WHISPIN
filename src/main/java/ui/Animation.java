import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.image.Image;

/**
 * Centralise le chargement des textures du projet.
 */
public final class Animation {
    private static final int NOMBRE_FRAMES_MARCHE = 3;
    private static final String DOSSIER_PERSONNAGES = "assets/player";

    private static Image[] personnageHaut;
    private static Image[] personnageBas;
    private static Image[] personnageGauche;
    private static Image[] personnageDroite;
    private static String personnageActuel;

    private static Direction directionPersonnage = Direction.BAS;
    private static int framePersonnage = 1;

    private static final Image HERBE = chargerDepuisCandidats(
        "assets/tiles/grass.png"
    );

    private static final Image ARBRE = chargerDepuisCandidats(
        "assets/tiles/tree.png"
    );

    private static final Image BLOC = chargerDepuisCandidats(
        "assets/tiles/wall.png"
    );

    private static final Image TERRE = chargerDepuisCandidats(
        "assets/tiles/earth.png"
    );

    private static final Image EAU = chargerDepuisCandidats(
        "assets/tiles/water.png"
    );

    private static final Image RAIN = chargerDepuisCandidats(
        "assets/tiles/pngegg.png"
    );

    private static final Image PORTAL = chargerDepuisCandidats(
        "assets/tiles/portal.jpg"
    );

    static {
        initialiserPersonnageParDefaut();
    }

    private Animation() {
    }

    public static Image getHerbe() {
        return HERBE;
    }

    public static Image getArbre() {
        return ARBRE;
    }

    public static Image getBloc() {
        return BLOC;
    }

    public static Image getTerre() {
        return TERRE;
    }

    public static Image getEau() {
        return EAU;
    }

    public static Image getRain() {
        return RAIN;
    }

    public static Image getPortal() {
        return PORTAL;
    }

    public static synchronized void reinitialiserAnimationPersonnage() {
        directionPersonnage = Direction.BAS;
        framePersonnage = 1;
    }

    public static synchronized void orienterPersonnage(Direction direction) {
        if (direction != null) {
            directionPersonnage = direction;
        }
    }

    public static synchronized void avancerAnimationPersonnage(Direction direction) {
        if (direction != null) {
            directionPersonnage = direction;
        }

        framePersonnage++;
        if (framePersonnage >= NOMBRE_FRAMES_MARCHE) {
            framePersonnage = 0;
        }
    }

    public static synchronized Image getPersonnage() {
        Image[] frames = framesPour(directionPersonnage);
        if (frames == null || frames.length == 0) {
            return null;
        }

        int index = Math.max(0, Math.min(framePersonnage, frames.length - 1));
        Image image = frames[index];
        if (image != null) {
            return image;
        }

        for (Image frame : frames) {
            if (frame != null) {
                return frame;
            }
        }
        return null;
    }

    private static Image[] framesPour(Direction direction) {
        if (direction == null) {
            return personnageBas;
        }

        switch (direction) {
            case HAUT:
                return personnageHaut;
            case BAS:
                return personnageBas;
            case GAUCHE:
                return personnageGauche;
            case DROITE:
                return personnageDroite;
            default:
                return personnageBas;
        }
    }

    public static synchronized List<String> getPersonnagesDisponibles() {
        File dossier = new File(DOSSIER_PERSONNAGES);
        File[] enfants = dossier.listFiles(File::isDirectory);
        if (enfants == null || enfants.length == 0) {
            return Collections.emptyList();
        }

        ArrayList<String> ids = new ArrayList<>();
        for (File enfant : enfants) {
            String id = enfant.getName();
            String prefixe = trouverPrefixeFrames(id);
            if (prefixe == null) {
                continue;
            }
            ids.add(id);
        }

        Collections.sort(ids);
        return ids;
    }

    public static synchronized String getPersonnageActuel() {
        return personnageActuel;
    }

    public static synchronized Image getApercuPersonnage(String idPersonnage) {
        if (idPersonnage == null || idPersonnage.isBlank()) {
            return null;
        }

        String prefixe = trouverPrefixeFrames(idPersonnage);
        if (prefixe == null) {
            return null;
        }

        return chargerDepuisCandidats(
            DOSSIER_PERSONNAGES + "/" + idPersonnage + "/" + prefixe + "_down_1.png",
            DOSSIER_PERSONNAGES + "/" + idPersonnage + "/" + prefixe + "_down_0.png",
            DOSSIER_PERSONNAGES + "/" + idPersonnage + "/" + prefixe + "_up_1.png"
        );
    }

    public static synchronized boolean selectionnerPersonnage(String idPersonnage) {
        if (idPersonnage == null || idPersonnage.isBlank()) {
            return false;
        }

        String prefixe = trouverPrefixeFrames(idPersonnage);
        if (prefixe == null) {
            return false;
        }

        Image[] haut = chargerFramesDirection(idPersonnage, prefixe, "up");
        Image[] bas = chargerFramesDirection(idPersonnage, prefixe, "down");
        Image[] gauche = chargerFramesDirection(idPersonnage, prefixe, "left");
        Image[] droite = chargerFramesDirection(idPersonnage, prefixe, "right");

        if (!framesValides(haut) || !framesValides(bas) || !framesValides(gauche) || !framesValides(droite)) {
            return false;
        }

        personnageActuel = idPersonnage;
        personnageHaut = haut;
        personnageBas = bas;
        personnageGauche = gauche;
        personnageDroite = droite;
        reinitialiserAnimationPersonnage();
        return true;
    }

    private static void initialiserPersonnageParDefaut() {
        List<String> ids = getPersonnagesDisponibles();
        if (ids.isEmpty()) {
            personnageActuel = "";
            personnageHaut = new Image[0];
            personnageBas = new Image[0];
            personnageGauche = new Image[0];
            personnageDroite = new Image[0];
            return;
        }

        String id = ids.contains("p1") ? "p1" : ids.get(0);
        if (!selectionnerPersonnage(id)) {
            String fallback = ids.get(0);
            selectionnerPersonnage(fallback);
        }
    }

    private static boolean framesValides(Image[] frames) {
        if (frames == null || frames.length != NOMBRE_FRAMES_MARCHE) {
            return false;
        }
        return Arrays.stream(frames).allMatch(image -> image != null && !image.isError());
    }

    private static String trouverPrefixeFrames(String idPersonnage) {
        File dossier = new File(DOSSIER_PERSONNAGES, idPersonnage);
        if (!dossier.isDirectory()) {
            return null;
        }

        File[] fichiers = dossier.listFiles((dir, nom) -> nom.endsWith("_up_0.png"));
        if (fichiers == null || fichiers.length == 0) {
            return null;
        }

        Pattern pattern = Pattern.compile("(.+)_up_0\\.png");
        for (File fichier : fichiers) {
            Matcher matcher = pattern.matcher(fichier.getName());
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private static Image[] chargerFramesDirection(String idPersonnage, String prefixe, String direction) {
        Image[] frames = new Image[NOMBRE_FRAMES_MARCHE];
        for (int i = 0; i < NOMBRE_FRAMES_MARCHE; i++) {
            String chemin = DOSSIER_PERSONNAGES
                + "/"
                + idPersonnage
                + "/"
                + prefixe
                + "_"
                + direction
                + "_"
                + i
                + ".png";
            Image frame = chargerDepuisCandidats(chemin);

            // Certains jeux de textures n'ont pas toutes les images (ex: index 0 manquant).
            if (frame == null) {
                for (int secours = 0; secours < NOMBRE_FRAMES_MARCHE; secours++) {
                    if (secours == i) {
                        continue;
                    }
                    String cheminSecours = DOSSIER_PERSONNAGES
                        + "/"
                        + idPersonnage
                        + "/"
                        + prefixe
                        + "_"
                        + direction
                        + "_"
                        + secours
                        + ".png";
                    frame = chargerDepuisCandidats(cheminSecours);
                    if (frame != null) {
                        break;
                    }
                }
            }

            frames[i] = frame;
        }
        return frames;
    }

    private static Image chargerDepuisCandidats(String... chemins) {
        for (String chemin : chemins) {
            File fichier = new File(chemin);
            if (!fichier.exists()) {
                continue;
            }

            Image image = new Image(fichier.toURI().toString(), false);
            if (!image.isError()) {
                return image;
            }
        }

        return null;
    }
}