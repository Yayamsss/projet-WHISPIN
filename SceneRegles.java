import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Cree une scene dediee aux regles du jeu.
 */
public final class SceneRegles {
    private SceneRegles() {
    }

    /**
     * Construit la scene des regles.
     *
     * @param largeur largeur de la fenetre
     * @param hauteur hauteur de la fenetre
     * @param onRetour callback de retour
     * @return scene prete a afficher
     */
    public static Scene creer(double largeur, double hauteur, Runnable onRetour) {
        BorderPane panneau = new BorderPane();
        panneau.setStyle("-fx-background-color: rgba(8,14,30,0.74);");
        panneau.setPadding(new Insets(28));

        Label titre = new Label("Regles du jeu");
        titre.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

        Label sousTitre = new Label("Objectif et controles");
        sousTitre.setStyle("-fx-font-size: 15px; -fx-text-fill: #bfdbfe;");

        VBox entete = new VBox(8, titre, sousTitre);
        panneau.setTop(entete);

        String contenu = "But : pousser toutes les boites sur les cibles.\n\n"
            + "Controles clavier :\n"
            + "- Fleches / ZQSD pour se deplacer\n"
            + "- CTRL+Z pour annuler le dernier coup\n"
            + "- CTRL+S pour sauvegarder pendant une partie\n"
            + "- ESC pour revenir au menu principal\n\n"
            + "Regles Sokoban :\n"
            + "- Une seule boite peut etre poussee a la fois\n"
            + "- Les murs bloquent les deplacements\n"
            + "- La victoire arrive quand toutes les boites sont sur des cibles.";

        Label texteRegles = new Label(contenu);
        texteRegles.setWrapText(true);
        texteRegles.setMaxWidth(780);
        texteRegles.setStyle(
            "-fx-font-size: 18px;"
                + "-fx-text-fill: #e2e8f0;"
                + "-fx-background-color: rgba(15,23,42,0.78);"
                + "-fx-background-radius: 16;"
                + "-fx-padding: 22;"
        );

        VBox centre = new VBox(texteRegles);
        centre.setAlignment(Pos.CENTER);
        panneau.setCenter(centre);

        Button btnRetour = Menu.creerBoutonMenu("Retour");
        btnRetour.setOnAction(event -> {
            if (onRetour != null) {
                onRetour.run();
            }
        });

        Label aide = new Label("Navigation: Fleches/ZQSD ou Tab   Valider: Entree/Espace   Retour: ESC");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: #93c5fd;");

        VBox bas = new VBox(8, aide, btnRetour);
        bas.setAlignment(Pos.CENTER_RIGHT);
        bas.setPadding(new Insets(16, 0, 0, 0));
        panneau.setBottom(bas);

        StackPane racine = new StackPane();
        racine.setStyle("-fx-background-color: #0e172a;");
        ImageView fond = FondEcran.creerVueFond();
        if (fond != null) {
            racine.getChildren().add(fond);
        }
        racine.getChildren().add(panneau);

        Scene scene = new Scene(racine, largeur, hauteur);
        if (fond != null) {
            FondEcran.lierAScene(fond, scene);
        }

        NavigationClavierUI.installerNavigationBoutons(scene, List.of(btnRetour), onRetour);

        return scene;
    }
}