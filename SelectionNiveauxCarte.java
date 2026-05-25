import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Affiche une scène de sélection de niveaux sur la fenêtre principale.
 */
public final class SelectionNiveauxCarte {
    private static final double TAILLE_PERSONNAGE_NAVIGATEUR = 56.0;
    private static final double DECALAGE_VERTICAL_PERSONNAGE = 0.92;
    private static final Duration DUREE_ETAPE_DEPLACEMENT = Duration.millis(380);

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
     * Ouvre la carte des niveaux dans une nouvelle scène du même stage.
     *
     * @param proprietaire stage principal
     * @param niveauActuel niveau courant pour positionner le personnage
     * @param onSelection callback appelé avec le niveau choisi
     * @param onCancel callback appelé à l'annulation
     */
    public static void ouvrir(Stage proprietaire, String niveauActuel, Consumer<String> onSelection, Runnable onCancel) {
        if (proprietaire == null) {
            if (onCancel != null) {
                onCancel.run();
            }
            return;
        }

        List<String> niveaux = ParcourirFichiers.listerFichiers("niveau", ".txt");
        if (niveaux.isEmpty()) {
            DialoguesMenu.afficherInformation("Niveau", "Aucun niveau detecte dans le dossier niveau.");
            if (onCancel != null) {
                onCancel.run();
            }
            return;
        }

        Scene scenePrecedente = proprietaire.getScene();

        int indexActuel = Math.max(0, niveaux.indexOf(niveauActuel));

        double largeur = scenePrecedente != null ? scenePrecedente.getWidth() : 0.0;
        double hauteur = scenePrecedente != null ? scenePrecedente.getHeight() : 0.0;
        if (largeur <= 0 || hauteur <= 0) {
            largeur = Screen.getPrimary().getVisualBounds().getWidth();
            hauteur = Screen.getPrimary().getVisualBounds().getHeight();
        }

        Pane carte = new Pane();
        carte.setPrefSize(largeur, hauteur);

        Text titre = new Text("Choix du niveau");
        titre.setFill(Color.web("#2b3d57"));
        titre.setFont(Font.font("Verdana", FontWeight.BOLD, 38));
        titre.setLayoutX(24);
        titre.setLayoutY(52);
        carte.getChildren().add(titre);

        List<Point2D> pointsNiveaux = creerPointsChemin(niveaux.size(), largeur, hauteur);
        dessinerChemin(carte, pointsNiveaux);

        ImageView personnage = creerPersonnageNavigateur();
        Point2D depart = pointsNiveaux.get(indexActuel);
        personnage.setLayoutX(depart.getX() - personnage.getFitWidth() / 2.0);
        personnage.setLayoutY(depart.getY() - personnage.getFitHeight() * DECALAGE_VERTICAL_PERSONNAGE);
        carte.getChildren().add(personnage);

        final int[] indexPersonnage = new int[] { indexActuel };
        final boolean[] animationEnCours = new boolean[] { false };

        Runnable annulerSelection = () -> {
            proprietaire.setScene(scenePrecedente);
            if (onCancel != null) {
                onCancel.run();
            }
        };

        Consumer<String> validerSelection = niveau -> {
            proprietaire.setScene(scenePrecedente);
            if (onSelection != null) {
                onSelection.accept(niveau);
            }
        };

        ArrayList<Button> boutons = new ArrayList<>();
        for (int i = 0; i < niveaux.size(); i++) {
            Point2D point = pointsNiveaux.get(i);
            Button bouton = creerBoutonNiveau(niveaux.get(i));
            bouton.setLayoutX(point.getX() - 36);
            bouton.setLayoutY(point.getY() - 24);
            bouton.setFocusTraversable(false);

            final int indexCible = i;
            bouton.setOnAction(event -> {
                if (indexCible == indexPersonnage[0]) {
                    validerSelection.accept(niveaux.get(indexCible));
                    return;
                }

                if (animationEnCours[0]) {
                    return;
                }

                animationEnCours[0] = true;

                animerPersonnage(personnage, pointsNiveaux, indexPersonnage[0], indexCible, () -> {
                    indexPersonnage[0] = indexCible;
                    appliquerStylesNiveaux(boutons, indexPersonnage[0]);
                    animationEnCours[0] = false;
                });
            });

            boutons.add(bouton);
            carte.getChildren().add(bouton);
        }
        appliquerStylesNiveaux(boutons, indexPersonnage[0]);

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
        annuler.setOnAction(event -> annulerSelection.run());
        carte.getChildren().add(annuler);

        StackPane racine = new StackPane();
        ImageView fond = FondEcran.creerVueFond();
        if (fond != null) {
            racine.getChildren().add(fond);
        }
        racine.getChildren().add(carte);
        racine.setFocusTraversable(true);
        Scene scene = new Scene(racine, largeur, hauteur);
        if (fond != null) {
            FondEcran.lierAScene(fond, scene);
        }
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                annulerSelection.run();
                return;
            }

            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                event.consume();
                validerSelection.accept(niveaux.get(indexPersonnage[0]));
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

            int indexCible = indexPersonnage[0] + delta;
            if (indexCible < 0 || indexCible >= niveaux.size()) {
                return;
            }

            animationEnCours[0] = true;

            animerPersonnage(personnage, pointsNiveaux, indexPersonnage[0], indexCible, () -> {
                indexPersonnage[0] = indexCible;
                appliquerStylesNiveaux(boutons, indexPersonnage[0]);
                animationEnCours[0] = false;
            });
        });

        proprietaire.setScene(scene);
        racine.requestFocus();
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

    private static ImageView creerPersonnageNavigateur() {
        Animation.reinitialiserAnimationPersonnage();
        Image image = Animation.getPersonnage();
        ImageView personnage = new ImageView(image);
        personnage.setFitWidth(TAILLE_PERSONNAGE_NAVIGATEUR);
        personnage.setFitHeight(TAILLE_PERSONNAGE_NAVIGATEUR);
        personnage.setPreserveRatio(true);
        personnage.setMouseTransparent(true);
        return personnage;
    }

    private static void animerPersonnage(
        ImageView personnage,
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
            Direction direction = directionEntre(from, to);
            mettreAJourSpriteNavigation(personnage, direction);

            Timeline etape = new Timeline(
                new KeyFrame(
                    Duration.ZERO,
                    new KeyValue(personnage.layoutXProperty(), from.getX() - personnage.getFitWidth() / 2.0),
                    new KeyValue(personnage.layoutYProperty(), from.getY() - personnage.getFitHeight() * DECALAGE_VERTICAL_PERSONNAGE)
                ),
                new KeyFrame(
                    DUREE_ETAPE_DEPLACEMENT,
                    new KeyValue(personnage.layoutXProperty(), to.getX() - personnage.getFitWidth() / 2.0, Interpolator.EASE_BOTH),
                    new KeyValue(personnage.layoutYProperty(), to.getY() - personnage.getFitHeight() * DECALAGE_VERTICAL_PERSONNAGE, Interpolator.EASE_BOTH)
                )
            );
            sequence.getChildren().add(etape);
            i = suivant;
        }

        sequence.setOnFinished(event -> onTermine.run());
        sequence.play();
    }

    private static Direction directionEntre(Point2D from, Point2D to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        if (Math.abs(dx) >= Math.abs(dy)) {
            return dx >= 0 ? Direction.DROITE : Direction.GAUCHE;
        }
        return dy >= 0 ? Direction.BAS : Direction.HAUT;
    }

    private static void mettreAJourSpriteNavigation(ImageView personnage, Direction direction) {
        Animation.orienterPersonnage(direction);
        Animation.avancerAnimationPersonnage(direction);
        Image image = Animation.getPersonnage();
        if (image != null) {
            personnage.setImage(image);
        }
    }
}
