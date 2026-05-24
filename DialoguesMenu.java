import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;

/**
 * Regroupe les boîtes de dialogue et messages utilisés par le menu principal.
 */
public final class DialoguesMenu {
    private DialoguesMenu() {
    }

    /**
     * Ouvre la boîte de sélection de niveau.
     */
    public static void ouvrirDialogueNiveau() {
        List<String> niveaux = ParcourirFichiers.listerFichiers("niveau", ".txt");
        if (niveaux.isEmpty()) {
            afficherInfo("Niveau", "Aucun niveau detecte dans le dossier niveau.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(niveaux.get(0), niveaux);
        dialog.setTitle("Choix du niveau");
        dialog.setHeaderText("Selectionne un niveau");
        dialog.setContentText("Niveau :");
        dialog.showAndWait().ifPresent(selection -> {
            afficherInfo("Niveau", "Niveau selectionne : " + selection);
        });
    }

    /**
     * Affiche les règles principales du jeu.
     */
    public static void afficherReglesDuJeu() {
        String regles = "But : pousser toutes les boites sur les cibles.\n\n"
            + "Controles :\n"
            + "- Deplace le personnage case par case\n"
            + "- Une seule boite peut etre poussee a la fois\n"
            + "- Les murs bloquent les deplacements\n\n"
            + "Tu gagnes quand toutes les boites sont sur des cibles.";
        afficherInfo("Regles du jeu", regles);
    }

    /**
     * Affiche la liste des sauvegardes détectées.
     */
    public static void afficherSauvegardes() {
        List<String> sauvegardes = ParcourirFichiers.listerFichiers("sauvegardes", null);
        if (sauvegardes.isEmpty()) {
            afficherInfo("Sauvegarde", "Aucune sauvegarde disponible.");
            return;
        }

        afficherListeInfo("Sauvegardes", "Fichiers detectes", sauvegardes);
    }

    /**
     * Affiche la boîte de dialogue des paramètres (placeholder).
     */
    public static void afficherParametres() {
        afficherInfo(
            "Paramettre",
            "Zone parametres prete.\n\n"
                + "Tu pourras y ajouter ensuite :\n"
                + "- Volume\n"
                + "- Taille de fenetre\n"
                + "- Raccourcis clavier"
        );
    }

    /**
     * Affiche un message d'information simple.
     *
     * @param titre titre de la boîte
     * @param message contenu textuel
     */
    private static void afficherInfo(String titre, String message) {
        Alert alert = creerAlerteInfo(titre, null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une boîte d'information contenant une liste.
     *
     * @param titre titre de la boîte
     * @param header en-tête de la boîte
     * @param elements éléments à afficher
     */
    private static void afficherListeInfo(String titre, String header, List<String> elements) {
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(elements);

        Alert alert = creerAlerteInfo(titre, header);
        alert.getDialogPane().setContent(listView);
        alert.getDialogPane().setPrefWidth(520);
        alert.getDialogPane().setPrefHeight(460);
        alert.showAndWait();
    }

    /**
     * Crée une boîte d'information stylisée.
     *
     * @param titre titre de la boîte
     * @param header en-tête de la boîte
     * @return alerte configurée
     */
    private static Alert creerAlerteInfo(String titre, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(header);
        styliserDialogue(alert.getDialogPane());
        return alert;
    }

    /**
     * Applique le style de base des boîtes de dialogue.
     *
     * @param pane panneau de dialogue
     */
    private static void styliserDialogue(DialogPane pane) {
        pane.setStyle("-fx-font-family: 'Verdana'; -fx-font-size: 14;");
    }
}
