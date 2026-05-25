import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Path;

import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;

/**
 * Regroupe les boîtes de dialogue et messages utilisés par le menu principal.
 */
public final class DialoguesMenu {
    private DialoguesMenu() {
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
        try {
            ArrayList<ServicePersistance.SauvegardeInfo> infos = ServicePersistance.listerSauvegardesInfos();
            if (infos.isEmpty()) {
                afficherInfo("Sauvegarde", "Aucune sauvegarde disponible.");
                return;
            }

            ArrayList<String> lignes = new ArrayList<>();
            for (ServicePersistance.SauvegardeInfo info : infos) {
                lignes.add(info.getNomFichier() + "  |  " + info.getDateModificationFormatee() + "  |  " + info.getTailleOctets() + " octets");
            }
            afficherListeInfo("Sauvegardes", "Fichiers detectes", lignes);
        } catch (IOException e) {
            afficherInfo("Erreur", "Impossible de lire les sauvegardes.");
        }
    }

    /**
     * Ouvre la boîte de sélection d'action de sauvegarde.
     *
     * @return action choisie, ou null si annulé
     */
    public static String ouvrirDialogueActionSauvegarde() {
        List<String> actions = List.of("Sauvegarder", "Charger", "Lister");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(actions.get(0), actions);
        dialog.setTitle("Sauvegarde");
        dialog.setHeaderText("Choisis une action");
        dialog.setContentText("Action :");
        return dialog.showAndWait().orElse(null);
    }

    /**
     * Ouvre la boîte de sélection d'un fichier de sauvegarde.
     *
     * @return chemin choisi, ou null si annulé/indisponible
     */
    public static Path ouvrirDialogueChargementSauvegarde() {
        try {
            ArrayList<ServicePersistance.SauvegardeInfo> infos = ServicePersistance.listerSauvegardesInfos();
            if (infos.isEmpty()) {
                afficherInfo("Sauvegarde", "Aucune sauvegarde disponible.");
                return null;
            }

            ArrayList<String> choix = new ArrayList<>();
            for (ServicePersistance.SauvegardeInfo info : infos) {
                choix.add(info.getNomFichier());
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(choix.get(0), choix);
            dialog.setTitle("Chargement");
            dialog.setHeaderText("Choisis une sauvegarde");
            dialog.setContentText("Fichier :");
            String selection = dialog.showAndWait().orElse(null);
            if (selection == null) {
                return null;
            }

            for (ServicePersistance.SauvegardeInfo info : infos) {
                if (info.getNomFichier().equals(selection)) {
                    return info.getChemin();
                }
            }
            return null;
        } catch (IOException e) {
            afficherInfo("Erreur", "Impossible de lire les sauvegardes.");
            return null;
        }
    }

    /**
     * Ouvre un dialogue pour saisir un nom de sauvegarde personnalisé.
     *
     * @return nom saisi, ou null si annulé
     */
    public static String ouvrirDialogueNomSauvegardePersonnalisee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sauvegarde personnalisee");
        dialog.setHeaderText("Nom du fichier de sauvegarde");
        dialog.setContentText("Nom :");
        styliserDialogue(dialog.getDialogPane());
        return dialog.showAndWait().orElse(null);
    }

    /**
     * Affiche un message d'information simple.
     *
     * @param titre titre de la boîte
     * @param message contenu textuel
     */
    public static void afficherInformation(String titre, String message) {
        afficherInfo(titre, message);
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
