import java.io.File;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Gère le chargement et l'animation du fond du menu principal.
 */
public final class VisuelsFond {
    private VisuelsFond() {
    }

    /**
     * Crée la vue d'arrière-plan si une image valide est trouvée.
     *
     * @return vue de fond animée, ou null si aucune image n'est disponible
     */
    public static ImageView creerVueFond() {
        Image image = chargerImageFond();
        if (image == null) {
            return null;
        }

        ImageView backgroundView = new ImageView(image);
        backgroundView.setPreserveRatio(false);
        backgroundView.setSmooth(true);
        backgroundView.setCache(true);
        appliquerEffetFlottant(backgroundView);
        return backgroundView;
    }

    /**
     * Lie la taille de l'image de fond à la taille de la scène.
     *
     * @param backgroundView vue de fond
     * @param scene scène JavaFX
     */
    public static void lierAScene(ImageView backgroundView, Scene scene) {
        backgroundView.fitWidthProperty().bind(scene.widthProperty());
        backgroundView.fitHeightProperty().bind(scene.heightProperty());
    }

    /**
     * Charge la première image de fond valide trouvée dans les chemins candidats.
     *
     * @return image de fond, ou null si aucune image valide n'est trouvée
     */
    private static Image chargerImageFond() {
        String[] candidats = new String[] {
            "fond_principal_ecran.png",
            "fond_principale_ecran.png",
            "fond_principale_ecran-frame0.png",
            "bin/fond_principale_ecran-frame0.png"
        };

        for (String candidat : candidats) {
            File file = new File(candidat);
            if (!file.exists()) {
                continue;
            }

            Image image = new Image(file.toURI().toString(), false);
            if (!image.isError()) {
                return image;
            }
        }

        return null;
    }

    /**
     * Applique un effet flottant continu sur la vue de fond.
     *
     * @param backgroundView vue de fond à animer
     */
    private static void appliquerEffetFlottant(ImageView backgroundView) {
        backgroundView.setScaleX(1.05);
        backgroundView.setScaleY(1.05);

        TranslateTransition floatMove = new TranslateTransition(Duration.seconds(7), backgroundView);
        floatMove.setFromY(-12);
        floatMove.setToY(12);
        floatMove.setAutoReverse(true);
        floatMove.setCycleCount(Animation.INDEFINITE);

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(7), backgroundView);
        pulse.setFromX(1.05);
        pulse.setFromY(1.05);
        pulse.setToX(1.08);
        pulse.setToY(1.08);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);

        ParallelTransition floatingEffect = new ParallelTransition(floatMove, pulse);
        floatingEffect.play();
    }
}
