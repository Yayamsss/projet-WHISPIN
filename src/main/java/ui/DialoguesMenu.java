import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;

/**
 * Regroupe les boîtes de dialogue et messages utilisés par le menu principal.
 */
public final class DialoguesMenu {
    private DialoguesMenu() {
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
