import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Point d'entrée JavaFX de l'application WHISPIN.
 */
public class InterfacePrincipale extends Application {
    private static final double WINDOW_WIDTH = 1280;
    private static final double WINDOW_HEIGHT = 720;

    /**
     * Initialise et affiche l'interface principale.
     *
     * @param stage fenêtre principale JavaFX
     */
    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0e172a;");

        ImageView backgroundView = VisuelsFond.creerVueFond();
        if (backgroundView != null) {
            root.getChildren().add(backgroundView);
        }

        VBox menuBox = VisuelsMenu.creerConteneurMenu();
        Text titre = VisuelsMenu.creerTitre("WHISPIN");
        Button btnNiveau = VisuelsMenu.creerBoutonMenu("Niveau");
        Button btnRegles = VisuelsMenu.creerBoutonMenu("Regles du jeu");
        Button btnSauvegarde = VisuelsMenu.creerBoutonMenu("Sauvegarde");
        Button btnParamettre = VisuelsMenu.creerBoutonMenu("Paramettre");
        Button btnQuitter = VisuelsMenu.creerBoutonMenu("Quitter");

        btnNiveau.setOnAction(event -> DialoguesMenu.ouvrirDialogueNiveau());
        btnRegles.setOnAction(event -> DialoguesMenu.afficherReglesDuJeu());
        btnSauvegarde.setOnAction(event -> DialoguesMenu.afficherSauvegardes());
        btnParamettre.setOnAction(event -> DialoguesMenu.afficherParametres());
        btnQuitter.setOnAction(event -> Platform.exit());

        menuBox.getChildren().addAll(
            titre,
            btnNiveau,
            btnRegles,
            btnSauvegarde,
            btnParamettre,
            btnQuitter
        );

        root.getChildren().add(menuBox);
        StackPane.setAlignment(menuBox, Pos.CENTER_LEFT);
        StackPane.setMargin(menuBox, Insets.EMPTY);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        if (backgroundView != null) {
            VisuelsFond.lierAScene(backgroundView, scene);
        }
        stage.setTitle("WHISPIN - Menu principal");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(540);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();

        // Sous Linux/Wayland, une demande de plein écran avant show() peut être ignorée.
        Platform.runLater(() -> {
            stage.setMaximized(true);
            stage.setFullScreen(true);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
