import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Construit la scène de victoire.
 */
public final class SceneVictoire {
    private SceneVictoire() {
    }

    /**
     * Crée la scène de victoire avec actions de navigation.
        *
        * @param stagePrincipal fenêtre principale
        * @param coups nombre de coups effectués
        * @param niveauSuivant identifiant du niveau suivant, ou null s'il n'existe pas
        * @param actionProchain action de passage au niveau suivant
        * @param actionRetourMenu action de retour au menu principal
        * @return scène de victoire prête à afficher
     */
    public static Scene creer(
        Stage stagePrincipal,
        int coups,
        String niveauSuivant,
        Runnable actionProchain,
        Runnable actionRetourMenu
    ) {
        VBox panneau = new VBox(18);
        panneau.setAlignment(Pos.CENTER);
        panneau.setPadding(new Insets(34));
        panneau.setMaxWidth(760);
        panneau.setStyle(
            "-fx-background-color: rgba(20, 18, 24, 0.82);"
            + "-fx-background-radius: 18;"
            + "-fx-border-color: rgba(255,255,255,0.25);"
            + "-fx-border-width: 1.5;"
            + "-fx-border-radius: 18;"
        );

        Text titre = new Text("Niveau réussi !");
        titre.setFill(Color.WHITE);
        titre.setFont(Font.font("Verdana", FontWeight.BOLD, 54));

        Text score = new Text("Score : " + coups + " coups");
        score.setFill(Color.web("#FFDFA2"));
        score.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 28));

        Button boutonProchain = Menu.creerBoutonMenu("Prochain niveau");
        if (niveauSuivant == null) {
            boutonProchain.setText("Dernier niveau atteint");
            boutonProchain.setDisable(true);
            boutonProchain.setOpacity(0.7);
        }

        Button boutonMenu = Menu.creerBoutonMenu("Retour au menu");

        boutonProchain.setOnAction(event -> {
            if (actionProchain != null) {
                actionProchain.run();
            }
        });

        boutonMenu.setOnAction(event -> {
            if (actionRetourMenu != null) {
                actionRetourMenu.run();
            }
        });

        panneau.getChildren().addAll(titre, score, boutonProchain, boutonMenu);

        StackPane fond = new StackPane();
        ImageView fondVictoire = FondEcran.creerVueFond();
        if (fondVictoire != null) {
            fond.getChildren().add(fondVictoire);
        } else {
            fond.setStyle("-fx-background-color: #0e172a;");
        }
        fond.getChildren().add(panneau);

        double largeur = 900;
        double hauteur = 620;
        if (stagePrincipal != null && stagePrincipal.getScene() != null) {
            largeur = Math.max(900, stagePrincipal.getScene().getWidth() * 0.86);
            hauteur = Math.max(620, stagePrincipal.getScene().getHeight() * 0.86);
        }

        Scene scene = new Scene(fond, largeur, hauteur);
        if (fondVictoire != null) {
            FondEcran.lierAScene(fondVictoire, scene);
        }
        return scene;
    }
}