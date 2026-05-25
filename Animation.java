import java.io.File;

import javafx.scene.image.Image;

/**
 * Centralise le chargement des sprites du projet.
 */
public final class Animation {
    private static final int NOMBRE_FRAMES_MARCHE = 3;

    private static final Image[] PERSONNAGE_HAUT = chargerFramesDirection("up");
    private static final Image[] PERSONNAGE_BAS = chargerFramesDirection("down");
    private static final Image[] PERSONNAGE_GAUCHE = chargerFramesDirection("left");
    private static final Image[] PERSONNAGE_DROITE = chargerFramesDirection("right");

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
        "assets/tiles/pngegg.png",
        "assets/tiles/rain.png",
        "assets/tiles/rain.jpg"
    );

    private static final Image PORTAL = chargerDepuisCandidats(
        "assets/tiles/portal.jpg"
    );

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
            return PERSONNAGE_BAS;
        }

        switch (direction) {
            case HAUT:
                return PERSONNAGE_HAUT;
            case BAS:
                return PERSONNAGE_BAS;
            case GAUCHE:
                return PERSONNAGE_GAUCHE;
            case DROITE:
                return PERSONNAGE_DROITE;
            default:
                return PERSONNAGE_BAS;
        }
    }

    private static Image[] chargerFramesDirection(String direction) {
        Image[] frames = new Image[NOMBRE_FRAMES_MARCHE];
        for (int i = 0; i < NOMBRE_FRAMES_MARCHE; i++) {
            frames[i] = chargerDepuisCandidats("assets/player/p1/p1_" + direction + "_" + i + ".png");
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