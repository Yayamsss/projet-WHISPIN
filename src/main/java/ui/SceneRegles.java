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
 * Crée une scène dédiée aux règles du jeu.
 */
public final class SceneRegles {
    private SceneRegles() {
    }

    /**
        * Construit la scène des règles.
     *
        * @param largeur largeur de la fenêtre
        * @param hauteur hauteur de la fenêtre
        * @param onRetour action de retour
        * @return scène prête à afficher
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

        String contenu = """
            Notre Jardinier est perdu au milieu d'un terrain en friche.
            Aide-le a faire pousser ses arbres en arrosant chaque carre de terre.

            Pour reussir, tu auras besoin de pluie, d'un bon placement et d'un peu de logique :
            - Pousse les nuages jusqu'aux cases de terre pour faire grandir les arbres
            - Tu ne peux pousser qu'un seul nuage a la fois
            - Les murs et les obstacles bloquent le passage
            - Le niveau est termine quand tous les arbres ont pousse sur leur carre

            Controles clavier :
            - Fleches / ZQSD pour te deplacer
            - CTRL+Z pour annuler le dernier coup
            - CTRL+S pour sauvegarder pendant une partie
            - ESC pour revenir au menu principal
            """;

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