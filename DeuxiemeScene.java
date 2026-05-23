import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.function.Supplier;

public class DeuxiemeScene {
    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau, Supplier<ControleurPartie.NiveauSuivant> niveauSuivant) {
        return creerScene(stage, scenePrecedente, plateau, null, niveauSuivant);
    }

    public static Scene creerScene(Stage stage, Scene scenePrecedente, Plateau plateau, Multivers multivers, Supplier<ControleurPartie.NiveauSuivant> niveauSuivant) {
        ControleurPartie controleurPartie = new ControleurPartie(stage, scenePrecedente, plateau, niveauSuivant);
        if (multivers != null) {
            controleurPartie.setMultivers(multivers);
        }
        controleurPartie.demarrer();
        return controleurPartie.getScene();
    }
}
