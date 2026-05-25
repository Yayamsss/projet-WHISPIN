import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Centralise la construction et le style visuel du menu principal.
 */
public final class VisuelsMenu {
    private static final String STYLE_PANNEAU_MENU =
        "-fx-background-color: rgba(20, 18, 24, 0.70);"
        + "-fx-background-radius: 18;"
        + "-fx-border-color: rgba(255, 255, 255, 0.20);"
        + "-fx-border-radius: 18;"
        + "-fx-border-width: 1;";

    private static final String STYLE_BOUTON_BASE =
        "-fx-background-color: linear-gradient(to right, #2f7ad8, #58a7ff);"
        + "-fx-text-fill: white;"
        + "-fx-background-radius: 14;"
        + "-fx-cursor: hand;"
        + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0.2, 0, 2);";

    private static final String STYLE_BOUTON_SURBRILLANCE =
        "-fx-background-color: linear-gradient(to right, #ffe18a, #ffc64d);"
        + "-fx-text-fill: #d90429;"
        + "-fx-font-weight: 800;"
        + "-fx-background-radius: 14;"
        + "-fx-cursor: hand;"
        + "-fx-border-color: rgba(255,255,255,0.65);"
        + "-fx-border-width: 1.5;"
        + "-fx-border-radius: 14;"
        + "-fx-effect: dropshadow(gaussian, rgba(255,220,120,0.65), 20, 0.35, 0, 0);";

    private VisuelsMenu() {
    }

    /**
     * Construit le conteneur du menu.
     *
     * @return conteneur vertical du menu
     */
    public static VBox creerConteneurMenu() {
        VBox menuBox = new VBox(16);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(24));
        menuBox.setMaxWidth(420);
        menuBox.setStyle(STYLE_PANNEAU_MENU);
        return menuBox;
    }

    /**
     * Construit le titre principal du menu.
     *
     * @param texte texte du titre
     * @return nœud texte stylisé
     */
    public static Text creerTitre(String texte) {
        Text titre = new Text(texte);
        titre.setFill(Color.WHITE);
        titre.setFont(Font.font("Verdana", FontWeight.BOLD, 52));
        return titre;
    }

    /**
     * Construit un bouton de menu avec son style interactif.
     *
     * @param texte libellé du bouton
     * @return bouton de menu stylisé
     */
    public static Button creerBoutonMenu(String texte) {
        Button button = new Button(texte);
        button.setPrefWidth(300);
        button.setPrefHeight(56);
        button.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 22));
        button.setStyle(STYLE_BOUTON_BASE);

        button.hoverProperty().addListener((obs, oldValue, isHover) -> {
            mettreAJourSurlignageBouton(button, isHover || button.isFocused());
        });
        button.focusedProperty().addListener((obs, oldValue, isFocused) -> {
            mettreAJourSurlignageBouton(button, isFocused || button.isHover());
        });

        return button;
    }

    /**
     * Construit le bouton réduit de menu (icône hamburger).
     *
     * @param texte icône ou libellé
     * @return bouton stylisé
     */
    public static Button creerBoutonMenuReduit(String texte) {
        Button button = new Button(texte);
        button.setPrefWidth(60);
        button.setPrefHeight(50);
        button.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        button.setStyle(STYLE_BOUTON_BASE + "-fx-padding: 3 14 5 14;");
        return button;
    }

    /**
     * Met à jour le style du bouton selon son état de surlignage.
     *
     * @param button bouton concerné
     * @param estSurligne true si surligné, false sinon
     */
    private static void mettreAJourSurlignageBouton(Button button, boolean estSurligne) {
        button.setStyle(estSurligne ? STYLE_BOUTON_SURBRILLANCE : STYLE_BOUTON_BASE);
        button.setScaleX(estSurligne ? 1.04 : 1.0);
        button.setScaleY(estSurligne ? 1.04 : 1.0);
    }
}
