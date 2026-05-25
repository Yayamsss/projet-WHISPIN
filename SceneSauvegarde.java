import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Cree une scene dediee aux actions de sauvegarde/chargement.
 */
public final class SceneSauvegarde {
    private SceneSauvegarde() {
    }

    /**
     * Construit la scene sauvegarde.
     *
     * @param largeur largeur de la fenetre
     * @param hauteur hauteur de la fenetre
     * @param partieActive indique si une partie est en cours
     * @param onSauvegarder callback action sauvegarder, retourne un message de statut
     * @param listerSauvegardes callback pour recuperer la liste des sauvegardes
     * @param onCharger callback action charger, retourne un message de statut
     * @param onRetour callback retour
     * @return scene prete a afficher
     */
    public static Scene creer(
        double largeur,
        double hauteur,
        boolean partieActive,
        Supplier<String> onSauvegarder,
        Supplier<List<ServicePersistance.SauvegardeInfo>> listerSauvegardes,
        Function<Path, String> onCharger,
        Runnable onRetour
    ) {
        BorderPane panneau = new BorderPane();
        panneau.setStyle("-fx-background-color: rgba(8,14,30,0.74);");
        panneau.setPadding(new Insets(28));

        Label titre = new Label("Sauvegarde");
        titre.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #f8fafc;");

        Label sousTitre = new Label(partieActive
            ? "Choisis une action de session"
            : "Aucune partie active : sauvegarde indisponible");
        sousTitre.setStyle("-fx-font-size: 15px; -fx-text-fill: #bfdbfe;");

        VBox entete = new VBox(8, titre, sousTitre);
        panneau.setTop(entete);

        Button btnSauvegarder = Menu.creerBoutonMenu("Sauvegarder");
        Button btnCharger = Menu.creerBoutonMenu("Charger");
        Button btnLister = Menu.creerBoutonMenu("Rafraichir la liste");

        btnSauvegarder.setVisible(partieActive);
        btnSauvegarder.setManaged(partieActive);

        List<ServicePersistance.SauvegardeInfo> sauvegardes = new ArrayList<>();
        ListView<String> listeSauvegardes = new ListView<>();
        listeSauvegardes.setPrefWidth(620);
        listeSauvegardes.setPrefHeight(260);
        listeSauvegardes.setStyle("-fx-font-size: 14px;");

        Label statut = new Label("Selectionne une sauvegarde puis charge-la.");
        statut.setWrapText(true);
        statut.setMaxWidth(620);
        statut.setStyle("-fx-font-size: 14px; -fx-text-fill: #cbd5e1;");

        Runnable rafraichirListe = () -> {
            sauvegardes.clear();
            listeSauvegardes.getItems().clear();
            if (listerSauvegardes == null) {
                statut.setText("Liste indisponible.");
                return;
            }

            List<ServicePersistance.SauvegardeInfo> infos = listerSauvegardes.get();
            if (infos != null) {
                sauvegardes.addAll(infos);
            }

            if (sauvegardes.isEmpty()) {
                statut.setText("Aucune sauvegarde disponible.");
                return;
            }

            for (ServicePersistance.SauvegardeInfo info : sauvegardes) {
                listeSauvegardes.getItems().add(
                    info.getNomFichier() + "  |  " + info.getDateModificationFormatee() + "  |  "
                        + info.getTailleOctets() + " octets"
                );
            }
            listeSauvegardes.getSelectionModel().select(0);
            statut.setText("" + sauvegardes.size() + " sauvegarde(s) detectee(s).");
        };

        btnSauvegarder.setOnAction(event -> {
            if (onSauvegarder == null) {
                statut.setText("Sauvegarde indisponible.");
                return;
            }
            String resultat = onSauvegarder.get();
            statut.setText(resultat == null || resultat.isBlank() ? "Operation terminee." : resultat);
            rafraichirListe.run();
        });

        btnLister.setOnAction(event -> rafraichirListe.run());

        btnCharger.setOnAction(event -> {
            int indexSelection = listeSauvegardes.getSelectionModel().getSelectedIndex();
            if (indexSelection < 0 || indexSelection >= sauvegardes.size()) {
                statut.setText("Choisis une sauvegarde dans la liste.");
                return;
            }
            if (onCharger == null) {
                statut.setText("Chargement indisponible.");
                return;
            }
            Path chemin = sauvegardes.get(indexSelection).getChemin();
            String resultat = onCharger.apply(chemin);
            statut.setText(resultat == null || resultat.isBlank() ? "Chargement termine." : resultat);
        });

        rafraichirListe.run();

        Label aide = new Label("Navigation: Fleches/ZQSD ou Tab   Valider: Entree/Espace   Retour: ESC");
        aide.setStyle("-fx-font-size: 13px; -fx-text-fill: #93c5fd;");

        VBox centre = new VBox(14, btnSauvegarder, btnCharger, btnLister, listeSauvegardes, statut, aide);
        centre.setAlignment(Pos.CENTER_LEFT);
        centre.setStyle("-fx-background-color: rgba(15,23,42,0.78); -fx-background-radius: 16; -fx-padding: 24;");
        panneau.setCenter(centre);

        Button btnRetour = Menu.creerBoutonMenu("Retour");
        btnRetour.setOnAction(event -> {
            if (onRetour != null) {
                onRetour.run();
            }
        });

        HBox bas = new HBox(btnRetour);
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

        List<Button> boutonsNavigables = new ArrayList<>();
        if (partieActive) {
            boutonsNavigables.add(btnSauvegarder);
        }
        boutonsNavigables.add(btnCharger);
        boutonsNavigables.add(btnLister);
        boutonsNavigables.add(btnRetour);

        NavigationClavierUI.installerNavigationBoutons(scene, boutonsNavigables, onRetour);

        return scene;
    }
}