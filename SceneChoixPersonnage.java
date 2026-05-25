import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * Cree une scene dediee a la previsualisation et au choix du personnage.
 */
public final class SceneChoixPersonnage {
    private SceneChoixPersonnage() {
    }

    /**
     * Construit la scene de choix de personnage.
     *
     * @param largeur largeur de la fenetre
     * @param hauteur hauteur de la fenetre
     * @param personnages personnages disponibles
     * @param personnageActuel personnage actuellement actif
     * @param onValider callback appele avec l'id selectionne
     * @param onRetour callback appele au retour sans validation
     * @return scene prete a afficher
     */
    public static Scene creer(
        double largeur,
        double hauteur,
        List<String> personnages,
        String personnageActuel,
        Consumer<String> onValider,
        Runnable onRetour
    ) {
        if (personnages == null || personnages.isEmpty()) {
            throw new IllegalArgumentException("La liste des personnages ne peut pas etre vide.");
        }

        final String[] selectionCourante = {
            personnages.contains(personnageActuel) ? personnageActuel : personnages.get(0)
        };
        final int[] indexSelection = { personnages.indexOf(selectionCourante[0]) };

        BorderPane panneau = new BorderPane();
        panneau.setStyle("-fx-background-color: rgba(8,14,30,0.72);");
        panneau.setPadding(new Insets(28));

        Label titre = new Label("Choix du personnage");
        titre.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

        Label sousTitre = new Label("Selectionne un skin et valide pour l'appliquer");
        sousTitre.setStyle("-fx-font-size: 15px; -fx-text-fill: #bfdbfe;");

        VBox entete = new VBox(8, titre, sousTitre);
        panneau.setTop(entete);

        ImageView apercu = new ImageView();
        apercu.setFitWidth(180);
        apercu.setFitHeight(180);
        apercu.setPreserveRatio(true);
        apercu.setSmooth(false);

        Label nomApercu = new Label();
        nomApercu.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

        Label aide = new Label("Navigation: Fleches/ZQSD ou Tab   Valider: Entree/Espace   Retour: ESC");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: #93c5fd;");

        VBox panneauApercu = new VBox(14, apercu, nomApercu, aide);
        panneauApercu.setAlignment(Pos.CENTER);
        panneauApercu.setPadding(new Insets(20));
        panneauApercu.setStyle("-fx-background-color: rgba(15,23,42,0.75); -fx-background-radius: 16;");
        panneauApercu.setPrefWidth(300);

        Runnable rafraichirApercu = () -> {
            String id = selectionCourante[0];
            apercu.setImage(Animation.getApercuPersonnage(id));
            nomApercu.setText(id);
        };
        rafraichirApercu.run();

        TilePane grille = new TilePane();
        grille.setHgap(14);
        grille.setVgap(14);
        grille.setPrefColumns(3);
        grille.setPadding(new Insets(4));
        List<VBox> cartesPersonnages = new ArrayList<>();

        for (int i = 0; i < personnages.size(); i++) {
            final int index = i;
            String id = personnages.get(i);
            ImageView vignette = new ImageView(Animation.getApercuPersonnage(id));
            vignette.setFitWidth(72);
            vignette.setFitHeight(72);
            vignette.setPreserveRatio(true);
            vignette.setSmooth(false);

            Label nom = new Label(id);
            nom.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

            Button btnVoir = new Button("Voir");
            btnVoir.setStyle("-fx-font-size: 13px; -fx-background-color: #2563eb; -fx-text-fill: #f8fafc;");
            btnVoir.setOnAction(event -> {
                selectionCourante[0] = id;
                indexSelection[0] = index;
                rafraichirApercu.run();
                mettreAJourSelectionCartes(cartesPersonnages, indexSelection[0]);
            });

            VBox carte = new VBox(8, vignette, nom, btnVoir);
            carte.setAlignment(Pos.CENTER);
            carte.setPadding(new Insets(12));
            carte.setStyle("-fx-background-color: rgba(30,41,59,0.84); -fx-background-radius: 12;");
            carte.setOnMouseClicked(event -> {
                selectionCourante[0] = id;
                indexSelection[0] = index;
                rafraichirApercu.run();
                mettreAJourSelectionCartes(cartesPersonnages, indexSelection[0]);
            });
            cartesPersonnages.add(carte);
            grille.getChildren().add(carte);
        }
        mettreAJourSelectionCartes(cartesPersonnages, indexSelection[0]);

        ScrollPane scroll = new ScrollPane(grille);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        HBox centre = new HBox(18, panneauApercu, scroll);
        centre.setAlignment(Pos.CENTER_LEFT);
        panneau.setCenter(centre);

        Button btnRetour = new Button("Retour");
        btnRetour.setStyle("-fx-font-size: 14px; -fx-background-color: #334155; -fx-text-fill: #f8fafc;");

        Button btnValider = new Button("Valider le personnage");
        btnValider.setStyle("-fx-font-size: 14px; -fx-background-color: #16a34a; -fx-text-fill: #f8fafc; -fx-font-weight: bold;");

        btnRetour.setOnAction(event -> {
            if (onRetour != null) {
                onRetour.run();
            }
        });

        btnValider.setOnAction(event -> {
            if (onValider != null) {
                onValider.accept(selectionCourante[0]);
            }
        });

        HBox actions = new HBox(12, btnRetour, btnValider);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(16, 0, 0, 0));
        panneau.setBottom(actions);

        StackPane racine = new StackPane();
        racine.setStyle("-fx-background-color: #0e172a;");

        ImageView fond = FondEcran.creerVueFond();
        if (fond != null) {
            racine.getChildren().add(fond);
        }
        racine.getChildren().add(panneau);

        Scene sceneChoix = new Scene(racine, largeur, hauteur);
        if (fond != null) {
            FondEcran.lierAScene(fond, sceneChoix);
        }
        NavigationClavierUI.installerNavigationGrille(
            sceneChoix,
            personnages.size(),
            3,
            indexSelection[0],
            nouvelIndex -> {
                indexSelection[0] = nouvelIndex;
                selectionCourante[0] = personnages.get(nouvelIndex);
                rafraichirApercu.run();
                mettreAJourSelectionCartes(cartesPersonnages, indexSelection[0]);
            },
            btnValider::fire,
            onRetour,
            true
        );

        return sceneChoix;
    }

    private static void mettreAJourSelectionCartes(List<VBox> cartes, int indexSelection) {
        for (int i = 0; i < cartes.size(); i++) {
            VBox carte = cartes.get(i);
            if (i == indexSelection) {
                carte.setStyle(
                    "-fx-background-color: rgba(59,130,246,0.88);"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-color: #bfdbfe;"
                        + "-fx-border-width: 2;"
                        + "-fx-border-radius: 12;"
                );
            } else {
                carte.setStyle("-fx-background-color: rgba(30,41,59,0.84); -fx-background-radius: 12;");
            }
        }
    }
}
