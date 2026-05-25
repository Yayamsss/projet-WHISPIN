import java.util.ArrayList;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Affiche une carte de sélection de niveaux avec déplacement animé de l'abeille.
 */
public final class SelectionNiveauxCarte {
    private static final String STYLE_BOUTON_ACTUEL =
        "-fx-background-color: linear-gradient(to bottom, #ffd96f, #ffab3c);"
        + "-fx-text-fill: #4b2a00;"
        + "-fx-background-radius: 24;"
        + "-fx-border-color: #fff4d0;"
        + "-fx-border-width: 2;"
        + "-fx-border-radius: 24;"
        + "-fx-cursor: hand;";

    private static final String STYLE_BOUTON_NORMAL =
        "-fx-background-color: linear-gradient(to bottom, #f78cf0, #8f7bff);"
        + "-fx-text-fill: white;"
        + "-fx-background-radius: 24;"
        + "-fx-border-color: #f8e6ff;"
        + "-fx-border-width: 2;"
        + "-fx-border-radius: 24;"
        + "-fx-cursor: hand;";

    private SelectionNiveauxCarte() {
    }

    /**
     * Ouvre la carte des niveaux et retourne le niveau sélectionné.
     *
     * @param proprietaire fenêtre parente
     * @param niveauActuel niveau courant pour positionner l'abeille
     * @return nom du niveau choisi, ou null si annulé
     */
    public static String ouvrir(Stage proprietaire, String niveauActuel) {
        List<String> niveaux = ParcourirFichiers.listerFichiers("niveau", ".txt");
        if (niveaux.isEmpty()) {
            DialoguesMenu.afficherInformation("Niveau", "Aucun niveau detecte dans le dossier niveau.");
            return null;
        }

        Stage dialogue = new Stage();
        dialogue.initModality(Modality.APPLICATION_MODAL);
        if (proprietaire != null) {
            dialogue.initOwner(proprietaire);
        }
        dialogue.setTitle("Carte des niveaux");

        int indexActuel = Math.max(0, niveaux.indexOf(niveauActuel));

        double largeur = Screen.getPrimary().getVisualBounds().getWidth();
        double hauteur = Screen.getPrimary().getVisualBounds().getHeight();

        Pane carte = new Pane();
        carte.setPrefSize(largeur, hauteur);
        carte.setBackground(
            new Background(
                new BackgroundFill(Color.web("#c7ebff"), CornerRadii.EMPTY, Insets.EMPTY)
            )
        );

        Text titre = new Text("Choix du niveau");
        titre.setFill(Color.web("#2b3d57"));
        titre.setFont(Font.font("Verdana", FontWeight.BOLD, 38));
        titre.setLayoutX(24);
        titre.setLayoutY(52);
        carte.getChildren().add(titre);

        List<Point2D> pointsNiveaux = creerPointsChemin(niveaux.size(), largeur, hauteur);
        dessinerChemin(carte, pointsNiveaux);

        Group abeille = creerAbeille();
        Point2D depart = pointsNiveaux.get(indexActuel);
        abeille.setLayoutX(depart.getX());
        abeille.setLayoutY(depart.getY() - 42);
        carte.getChildren().add(abeille);

        final int[] indexAbeille = new int[] { indexActuel };
        final String[] selection = new String[] { null };
        final boolean[] animationEnCours = new boolean[] { false };

        ArrayList<Button> boutons = new ArrayList<>();
        for (int i = 0; i < niveaux.size(); i++) {
            Point2D point = pointsNiveaux.get(i);
            Button bouton = creerBoutonNiveau(niveaux.get(i));
            bouton.setLayoutX(point.getX() - 36);
            bouton.setLayoutY(point.getY() - 24);
            bouton.setFocusTraversable(false);

            final int indexCible = i;
            bouton.setOnAction(event -> {
                if (indexCible == indexAbeille[0]) {
                    selection[0] = niveaux.get(indexCible);
                    dialogue.close();
                    return;
                }

                if (animationEnCours[0]) {
                    return;
                }

                animationEnCours[0] = true;

                animerAbeille(abeille, pointsNiveaux, indexAbeille[0], indexCible, () -> {
                    indexAbeille[0] = indexCible;
                    appliquerStylesNiveaux(boutons, indexAbeille[0]);
                    animationEnCours[0] = false;
                });
            });

            boutons.add(bouton);
            carte.getChildren().add(bouton);
        }
        appliquerStylesNiveaux(boutons, indexAbeille[0]);

        Button annuler = new Button("Annuler");
        annuler.setStyle(
            "-fx-background-color: #ffffffcc;"
            + "-fx-text-fill: #3d3d3d;"
            + "-fx-font-size: 16;"
            + "-fx-background-radius: 12;"
            + "-fx-cursor: hand;"
        );
        annuler.setLayoutX(largeur - 130);
        annuler.setLayoutY(24);
        annuler.setFocusTraversable(false);
        annuler.setOnAction(event -> dialogue.close());
        carte.getChildren().add(annuler);

        StackPane racine = new StackPane(carte);
        racine.setFocusTraversable(true);
        Scene scene = new Scene(racine, largeur, hauteur);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                dialogue.close();
                return;
            }

            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                event.consume();
                selection[0] = niveaux.get(indexAbeille[0]);
                dialogue.close();
                return;
            }

            int delta = 0;
            switch (event.getCode()) {
                case LEFT:
                case Q:
                case A:
                case UP:
                case Z:
                case W:
                    delta = -1;
                    break;
                case RIGHT:
                case D:
                case DOWN:
                case S:
                    delta = 1;
                    break;
                default:
                    break;
            }

            if (delta == 0) {
                return;
            }

            event.consume();

            if (animationEnCours[0]) {
                return;
            }

            int indexCible = indexAbeille[0] + delta;
            if (indexCible < 0 || indexCible >= niveaux.size()) {
                return;
            }

            animationEnCours[0] = true;

            animerAbeille(abeille, pointsNiveaux, indexAbeille[0], indexCible, () -> {
                indexAbeille[0] = indexCible;
                appliquerStylesNiveaux(boutons, indexAbeille[0]);
                animationEnCours[0] = false;
            });
        });

        dialogue.setScene(scene);
        dialogue.setResizable(true);
        dialogue.setFullScreenExitHint("");
        dialogue.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        dialogue.setOnShown(event -> {
            dialogue.setFullScreen(true);
            racine.requestFocus();
        });
        dialogue.showAndWait();

        return selection[0];
    }

    private static List<Point2D> creerPointsChemin(int nombreNiveaux, double largeur, double hauteur) {
        ArrayList<Point2D> points = new ArrayList<>();
        if (nombreNiveaux <= 0) {
            return points;
        }

        int lignes = (int) Math.ceil(nombreNiveaux / 5.0);
        double margeX = Math.max(70, largeur * 0.08);
        double margeY = Math.max(110, hauteur * 0.18);
        double pasY = lignes <= 1 ? 0 : (hauteur - 2 * margeY) / (lignes - 1);

        int index = 0;
        for (int ligne = 0; ligne < lignes && index < nombreNiveaux; ligne++) {
            int restant = nombreNiveaux - index;
            int elementsLigne = Math.min(5, restant);
            double y = margeY + ligne * pasY;

            for (int i = 0; i < elementsLigne && index < nombreNiveaux; i++) {
                double ratio = elementsLigne == 1 ? 0.5 : (double) i / (elementsLigne - 1);
                if (ligne % 2 == 1) {
                    ratio = 1.0 - ratio;
                }
                double x = margeX + ratio * (largeur - 2 * margeX);
                points.add(new Point2D(x, y));
                index++;
            }
        }

        return points;
    }

    private static void dessinerChemin(Pane carte, List<Point2D> points) {
        Polyline chemin = new Polyline();
        for (Point2D point : points) {
            chemin.getPoints().addAll(point.getX(), point.getY());
        }
        chemin.setStroke(Color.web("#f6fbff"));
        chemin.setStrokeWidth(24);
        chemin.setStrokeLineCap(StrokeLineCap.ROUND);

        Polyline bord = new Polyline();
        bord.getPoints().addAll(chemin.getPoints());
        bord.setStroke(Color.web("#5ca2ff"));
        bord.setStrokeWidth(10);
        bord.setStrokeLineCap(StrokeLineCap.ROUND);

        carte.getChildren().addAll(chemin, bord);
    }

    private static Button creerBoutonNiveau(String nomNiveau) {
        Button bouton = new Button(extraireNumeroAffichage(nomNiveau));
        bouton.setMinSize(72, 48);
        bouton.setMaxSize(72, 48);
        bouton.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        bouton.setStyle(STYLE_BOUTON_NORMAL);
        return bouton;
    }

    private static void appliquerStylesNiveaux(List<Button> boutons, int indexActuel) {
        for (int i = 0; i < boutons.size(); i++) {
            boutons.get(i).setStyle(i == indexActuel ? STYLE_BOUTON_ACTUEL : STYLE_BOUTON_NORMAL);
        }
    }

    private static String extraireNumeroAffichage(String nomNiveau) {
        String sansExtension = nomNiveau;
        int pointIndex = sansExtension.lastIndexOf('.');
        if (pointIndex > 0) {
            sansExtension = sansExtension.substring(0, pointIndex);
        }

        String chiffres = sansExtension.replaceAll("[^0-9]", "");
        if (!chiffres.isEmpty()) {
            return chiffres;
        }
        return sansExtension;
    }

    private static Group creerAbeille() {
        Circle aileGauche = new Circle(-10, -8, 10, Color.web("#d9f3ff", 0.85));
        Circle aileDroite = new Circle(10, -8, 10, Color.web("#d9f3ff", 0.85));

        Circle corps = new Circle(0, 0, 14, Color.web("#ffd74a"));
        Circle bande1 = new Circle(-5, 0, 3.3, Color.web("#222222"));
        Circle bande2 = new Circle(1, 0, 3.3, Color.web("#222222"));
        Circle bande3 = new Circle(7, 0, 3.3, Color.web("#222222"));

        Circle tete = new Circle(14, 0, 6, Color.web("#ffcc59"));
        Circle oeil = new Circle(16, -1.5, 1.2, Color.web("#111111"));

        return new Group(aileGauche, aileDroite, corps, bande1, bande2, bande3, tete, oeil);
    }

    private static void animerAbeille(
        Group abeille,
        List<Point2D> points,
        int indexDepart,
        int indexArrivee,
        Runnable onTermine
    ) {
        int pas = indexArrivee > indexDepart ? 1 : -1;
        SequentialTransition sequence = new SequentialTransition();

        int i = indexDepart;
        while (i != indexArrivee) {
            int suivant = i + pas;
            Point2D from = points.get(i);
            Point2D to = points.get(suivant);

            Timeline etape = new Timeline(
                new KeyFrame(
                    Duration.ZERO,
                    new KeyValue(abeille.layoutXProperty(), from.getX()),
                    new KeyValue(abeille.layoutYProperty(), from.getY() - 42)
                ),
                new KeyFrame(
                    Duration.millis(260),
                    new KeyValue(abeille.layoutXProperty(), to.getX(), Interpolator.EASE_BOTH),
                    new KeyValue(abeille.layoutYProperty(), to.getY() - 42, Interpolator.EASE_BOTH)
                )
            );
            sequence.getChildren().add(etape);
            i = suivant;
        }

        sequence.setOnFinished(event -> onTermine.run());
        sequence.play();
    }
}
