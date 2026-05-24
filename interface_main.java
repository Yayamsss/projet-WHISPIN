import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;

public class interface_main extends Application {
    private MenuPrincipal menuPrincipal;

    @Override
    public void start(Stage stage) {
        menuPrincipal = new MenuPrincipal((sceneMenu, plateauCharge, multivers, niveauSuivant) -> {
            if (plateauCharge == null) {
                return;
            }
            Scene sceneJeu = DeuxiemeScene.creerScene(stage, sceneMenu, plateauCharge, multivers, niveauSuivant);
            stage.setScene(sceneJeu);
            stage.setFullScreen(true);
        });

        stage.setTitle("Whispin Parabox");

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setScene(menuPrincipal.getScene());
        stage.show();
        menuPrincipal.reprendreFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}