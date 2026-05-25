import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;

/**
 * Point d'entrée JavaFX de l'application WHISPIN.
 */
public class InterfacePrincipale extends Application {
    private static final double WINDOW_WIDTH = 1280;
    private static final double WINDOW_HEIGHT = 720;
    private static final long DUREE_TRANSITION_VICTOIRE_NS = 2_300_000_000L;

    private Case[][] plateauActuel;
    private Canvas canvasPlateau;
    private String nomNiveauActuel;
    private LogiqueSokoban moteurJeu;
    private Stage stagePrincipal;
    private Scene scenePrincipale;
    private StackPane racinePrincipale;
    private VBox menuPrincipal;
    private Button boutonMenuReduit;
    private List<Button> boutonsMenuPrincipal;
    private int indexBoutonMenuPrincipal;
    private boolean modeJeuActif;
    private final FeuArtifice feuArtifice = new FeuArtifice();
    private AnimationTimer animationVictoire;
    private boolean victoireAnimee;
    private long tempsAnimationVictoireNs;
    private long debutAnimationVictoireNs;
    private boolean transitionVictoireEnCours;
    private Runnable actionFinVictoire;
    private ControleurPartie controleurPartie;

    /**
     * Initialise et affiche l'interface principale.
     *
     * @param stage fenêtre principale JavaFX
     */
    @Override
    public void start(Stage stage) {
        stagePrincipal = stage;
        racinePrincipale = new StackPane();
        racinePrincipale.setStyle("-fx-background-color: #0e172a;");
        modeJeuActif = false;
        victoireAnimee = false;
        tempsAnimationVictoireNs = 0L;
        debutAnimationVictoireNs = 0L;
        transitionVictoireEnCours = false;
        actionFinVictoire = null;

        plateauActuel = creerPlateauApercu();
        nomNiveauActuel = "aperçu";
        moteurJeu = new LogiqueSokoban(plateauActuel);
        Animation.reinitialiserAnimationPersonnage();

        ImageView backgroundView = FondEcran.creerVueFond();
        if (backgroundView != null) {
            racinePrincipale.getChildren().add(backgroundView);
        }

        canvasPlateau = new Canvas(560, 560);
        canvasPlateau.setMouseTransparent(false);
        racinePrincipale.getChildren().add(canvasPlateau);

        menuPrincipal = Menu.creerConteneurMenu();
        Text titre = Menu.creerTitre("WHISPIN");
        Button btnNiveau = Menu.creerBoutonMenu("Niveau");
        Button btnRegles = Menu.creerBoutonMenu("Regles du jeu");
        Button btnSauvegarde = Menu.creerBoutonMenu("Sauvegarde");
        Button btnParamettre = Menu.creerBoutonMenu("Paramettre");
        Button btnQuitter = Menu.creerBoutonMenu("Quitter");

        boutonsMenuPrincipal = List.of(
            btnNiveau,
            btnRegles,
            btnSauvegarde,
            btnParamettre,
            btnQuitter
        );
        indexBoutonMenuPrincipal = 0;

        for (int i = 0; i < boutonsMenuPrincipal.size(); i++) {
            final int index = i;
            Button bouton = boutonsMenuPrincipal.get(i);
            bouton.setOnMouseEntered(event -> indexBoutonMenuPrincipal = index);
            bouton.focusedProperty().addListener((obs, oldValue, isFocused) -> {
                if (isFocused) {
                    indexBoutonMenuPrincipal = index;
                }
            });
        }

        btnNiveau.setOnAction(event -> chargerNiveauSelectionne());
        btnRegles.setOnAction(event -> DialoguesMenu.afficherReglesDuJeu());
        btnSauvegarde.setOnAction(event -> gererSauvegarde());
        btnParamettre.setOnAction(event -> DialoguesMenu.afficherParametres());
        btnQuitter.setOnAction(event -> {
            sauvegarderEtatCourantAutomatiqueSiPossible();
            Platform.exit();
        });

        menuPrincipal.getChildren().addAll(
            titre,
            btnNiveau,
            btnRegles,
            btnSauvegarde,
            btnParamettre,
            btnQuitter
        );

        racinePrincipale.getChildren().add(menuPrincipal);
        StackPane.setAlignment(menuPrincipal, Pos.CENTER_LEFT);
        StackPane.setMargin(menuPrincipal, Insets.EMPTY);

        boutonMenuReduit = Menu.creerBoutonMenuReduit("☰");
        boutonMenuReduit.setVisible(false);
        boutonMenuReduit.setManaged(false);
        boutonMenuReduit.setOnAction(event -> {
            if (menuPrincipal.isVisible()) {
                replierMenuJeu();
            } else {
                deplierMenuJeu();
            }
        });
        racinePrincipale.getChildren().add(boutonMenuReduit);
        StackPane.setAlignment(boutonMenuReduit, Pos.TOP_LEFT);
        StackPane.setMargin(boutonMenuReduit, new Insets(16, 0, 0, 16));

        mettreEnPagePlateau(true);
        mettreAJourVisibilitePlateau();

        scenePrincipale = new Scene(racinePrincipale, WINDOW_WIDTH, WINDOW_HEIGHT);

        scenePrincipale.setOnKeyPressed(event -> {
            if (transitionVictoireEnCours) {
                event.consume();
                return;
            }

            if (controleurPartie != null) {
                controleurPartie.arreterDeplacementAutomatique();
            }

            if (event.getCode() == KeyCode.ESCAPE && modeJeuActif) {
                quitterPartieEnCours();
                event.consume();
                return;
            }

            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                if (modeJeuActif) {
                    sauvegarderEtatCourantPersonnalise();
                }
                event.consume();
                return;
            }

            if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                if (moteurJeu != null && moteurJeu.annulerDernierCoup()) {
                    plateauActuel = moteurJeu.exporterPlateau();
                    dessinerPlateauActuel();
                }
                event.consume();
                return;
            }

            if (!modeJeuActif) {
                if (gererNavigationMenuPrincipal(event)) {
                    event.consume();
                }
                return;
            }

            Direction direction = convertirToucheEnDirection(event.getCode());
            if (direction == null || moteurJeu == null) {
                return;
            }

            Animation.orienterPersonnage(direction);

            if (moteurJeu.deplacer(direction)) {
                Animation.avancerAnimationPersonnage(direction);
                plateauActuel = moteurJeu.exporterPlateau();
                dessinerPlateauActuel();
                if (moteurJeu.estVictoire()) {
                    sauvegarderEtatCourantAutomatiqueSiPossible();
                    lancerAnimationVictoire(this::afficherSceneVictoire);
                }
            } else {
                dessinerPlateauActuel();
            }
        });

        canvasPlateau.widthProperty().bind(scenePrincipale.widthProperty().multiply(0.42));
        canvasPlateau.heightProperty().bind(scenePrincipale.heightProperty().multiply(0.80));
        canvasPlateau.widthProperty().addListener((obs, oldVal, newVal) -> dessinerPlateauActuel());
        canvasPlateau.heightProperty().addListener((obs, oldVal, newVal) -> dessinerPlateauActuel());
        controleurPartie = new ControleurPartie(
            canvasPlateau,
            () -> modeJeuActif,
            () -> transitionVictoireEnCours,
            () -> plateauActuel,
            () -> moteurJeu,
            () -> {
                if (moteurJeu != null) {
                    plateauActuel = moteurJeu.exporterPlateau();
                }
                dessinerPlateauActuel();
            },
            () -> {
                sauvegarderEtatCourantAutomatiqueSiPossible();
                lancerAnimationVictoire(this::afficherSceneVictoire);
            }
        );
        controleurPartie.installerGestionClic();
        dessinerPlateauActuel();

        if (backgroundView != null) {
            FondEcran.lierAScene(backgroundView, scenePrincipale);
        }
        stage.setTitle("WHISPIN - Menu principal");
        stage.setScene(scenePrincipale);
        stage.setMinWidth(960);
        stage.setMinHeight(540);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
        Platform.runLater(() -> {
            if (!modeJeuActif) {
                focusBoutonMenuPrincipal(indexBoutonMenuPrincipal);
            } else if (racinePrincipale != null) {
                racinePrincipale.requestFocus();
            }
        });

        // Sous Linux/Wayland, une demande de plein écran avant show() peut être ignorée.
        activerPleinEcranEtFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static Case[][] creerPlateauApercu() {
        int largeur = 12;
        int hauteur = 10;
        Case[][] plateau = new Case[hauteur][largeur];

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (x == 0 || y == 0 || x == largeur - 1 || y == hauteur - 1) {
                    plateau[y][x] = new CaseMur(x, y);
                } else {
                    plateau[y][x] = new CaseVide(x, y);
                }
            }
        }

        plateau[2][3] = new CaseBoite(3, 2);
        plateau[5][4] = new CaseBoite(4, 5);
        plateau[6][8] = new CaseBoite(8, 6);

        plateau[2][8] = new CaseCible(8, 2);
        plateau[4][8] = new CaseCible(8, 4);
        plateau[6][3] = new CaseCible(3, 6);

        plateau[4][5] = new CasePersonnage(5, 4);
        return plateau;
    }

    private void dessinerPlateauActuel() {
        GraphicsContext gc = canvasPlateau.getGraphicsContext2D();
        double largeur = canvasPlateau.getWidth();
        double hauteur = canvasPlateau.getHeight();

        gc.clearRect(0, 0, largeur, hauteur);
        if (plateauActuel == null || plateauActuel.length == 0 || plateauActuel[0] == null || plateauActuel[0].length == 0) {
            return;
        }

        int lignes = plateauActuel.length;
        int colonnes = plateauActuel[0].length;
        double tailleCase = Math.min(largeur / colonnes, hauteur / lignes);
        double origineX = (largeur - colonnes * tailleCase) / 2.0;
        double origineY = (hauteur - lignes * tailleCase) / 2.0;

        RenduPlateau.dessinerPlateau(gc, plateauActuel, origineX, origineY, tailleCase);

        if (victoireAnimee) {
            feuArtifice.dessiner(gc, tempsAnimationVictoireNs);
        }
    }

    private void chargerNiveauSelectionne() {
        SelectionNiveauxCarte.ouvrir(
            stagePrincipal,
            nomNiveauActuel,
            this::chargerNiveauParNom,
            () -> {
                activerPleinEcranEtFocus();
                if (!modeJeuActif) {
                    focusBoutonMenuPrincipal(indexBoutonMenuPrincipal);
                } else if (racinePrincipale != null) {
                    racinePrincipale.requestFocus();
                }
            }
        );
    }

    private boolean chargerNiveauParNom(String niveau) {
        if (niveau == null || niveau.isBlank()) {
            return false;
        }

        if (modeJeuActif) {
            sauvegarderEtatCourantAutomatiqueSiPossible();
        }
        if (controleurPartie != null) {
            controleurPartie.arreterDeplacementAutomatique();
        }

        ChargeurNiveau.NiveauCharge niveauCharge = ChargeurNiveau.chargerNiveauRecursifDepuisFichier("niveau/" + niveau);
        if (niveauCharge == null || niveauCharge.getPlateauRacine() == null) {
            DialoguesMenu.afficherInformation(
                "Erreur de chargement",
                "Impossible de charger le niveau : " + niveau
            );
            return false;
        }

        moteurJeu = new LogiqueSokoban(niveauCharge);
        plateauActuel = moteurJeu.exporterPlateau();
        nomNiveauActuel = niveau;
        Animation.reinitialiserAnimationPersonnage();
        arreterAnimationVictoire();
        transitionVictoireEnCours = false;
        modeJeuActif = true;
        mettreAJourVisibilitePlateau();
        replierMenuJeu();
        dessinerPlateauActuel();
        activerPleinEcranEtFocus();

        return true;
    }

    private void quitterPartieEnCours() {
        sauvegarderEtatCourantAutomatiqueSiPossible();
        if (controleurPartie != null) {
            controleurPartie.arreterDeplacementAutomatique();
        }
        arreterAnimationVictoire();
        transitionVictoireEnCours = false;
        if (stagePrincipal != null && scenePrincipale != null && stagePrincipal.getScene() != scenePrincipale) {
            stagePrincipal.setScene(scenePrincipale);
        }
        plateauActuel = creerPlateauApercu();
        moteurJeu = new LogiqueSokoban(plateauActuel);
        nomNiveauActuel = "aperçu";
        Animation.reinitialiserAnimationPersonnage();
        modeJeuActif = false;
        mettreAJourVisibilitePlateau();
        deplierMenuJeu();
        dessinerPlateauActuel();
    }

    private void afficherSceneVictoire() {
        if (stagePrincipal == null) {
            return;
        }

        String niveauSuivant = trouverNiveauSuivant();
        Scene sceneVictoire = SceneVictoire.creer(
            stagePrincipal,
            moteurJeu.getCoups(),
            niveauSuivant,
            () -> {
                stagePrincipal.setScene(scenePrincipale);
                chargerNiveauParNom(niveauSuivant);
            },
            () -> {
                stagePrincipal.setScene(scenePrincipale);
                quitterPartieEnCours();
            }
        );

        sceneVictoire.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stagePrincipal.setScene(scenePrincipale);
                quitterPartieEnCours();
                event.consume();
            }
        });
        stagePrincipal.setScene(sceneVictoire);
        activerPleinEcranEtFocus();
    }

    private String trouverNiveauSuivant() {
        List<String> niveaux = ParcourirFichiers.listerFichiers("niveau", ".txt");
        int indexActuel = niveaux.indexOf(nomNiveauActuel);
        if (indexActuel < 0 || indexActuel + 1 >= niveaux.size()) {
            return null;
        }
        return niveaux.get(indexActuel + 1);
    }

    private void mettreEnPagePlateau(boolean menuVisible) {
        if (menuVisible) {
            StackPane.setAlignment(canvasPlateau, Pos.CENTER_RIGHT);
            StackPane.setMargin(canvasPlateau, new Insets(0, 36, 0, 0));
            return;
        }

        StackPane.setAlignment(canvasPlateau, Pos.CENTER);
        StackPane.setMargin(canvasPlateau, Insets.EMPTY);
    }

    private void replierMenuJeu() {
        menuPrincipal.setVisible(false);
        menuPrincipal.setManaged(false);
        boutonMenuReduit.setVisible(modeJeuActif);
        boutonMenuReduit.setManaged(modeJeuActif);
        mettreEnPagePlateau(false);
        if (racinePrincipale != null) {
            racinePrincipale.requestFocus();
        }
    }

    private void deplierMenuJeu() {
        menuPrincipal.setVisible(true);
        menuPrincipal.setManaged(true);
        boutonMenuReduit.setVisible(modeJeuActif);
        boutonMenuReduit.setManaged(modeJeuActif);
        mettreEnPagePlateau(true);
        Platform.runLater(() -> focusBoutonMenuPrincipal(indexBoutonMenuPrincipal));
    }

    private void gererSauvegarde() {
        String action = DialoguesMenu.ouvrirDialogueActionSauvegarde();
        if (action == null) {
            return;
        }

        switch (action) {
            case "Sauvegarder":
                sauvegarderEtatCourant();
                break;
            case "Charger":
                chargerSauvegarde();
                break;
            case "Lister":
                DialoguesMenu.afficherSauvegardes();
                break;
            default:
                break;
        }
    }

    private void sauvegarderEtatCourant() {
        try {
            Path chemin = ServicePersistance.creerCheminSauvegardeAuto();
            ServicePersistance.sauvegarderSessionJson(chemin, nomNiveauActuel, plateauActuel);
            DialoguesMenu.afficherInformation("Sauvegarde", "Sauvegarde creee : " + chemin.getFileName());
        } catch (Exception e) {
            DialoguesMenu.afficherInformation("Erreur", "Echec de la sauvegarde.");
        }
    }

    private void sauvegarderEtatCourantPersonnalise() {
        if (!modeJeuActif || plateauActuel == null) {
            return;
        }

        String nomSouhaite = DialoguesMenu.ouvrirDialogueNomSauvegardePersonnalisee();
        if (nomSouhaite == null) {
            return;
        }

        try {
            Path chemin = ServicePersistance.creerCheminSauvegardePersonnalisee(nomSouhaite);
            ServicePersistance.sauvegarderSessionJson(chemin, nomNiveauActuel, plateauActuel);
            DialoguesMenu.afficherInformation("Sauvegarde", "Sauvegarde creee : " + chemin.getFileName());
        } catch (Exception e) {
            DialoguesMenu.afficherInformation("Erreur", "Echec de la sauvegarde personnalisee.");
        }
    }

    private void sauvegarderEtatCourantAutomatiqueSiPossible() {
        if (!modeJeuActif || plateauActuel == null) {
            return;
        }

        try {
            Path chemin = ServicePersistance.creerCheminSauvegardeAuto();
            ServicePersistance.sauvegarderSessionJson(chemin, nomNiveauActuel, plateauActuel);
        } catch (Exception e) {
            // Les sauvegardes auto ne doivent pas interrompre le flux de jeu.
        }
    }

    private void chargerSauvegarde() {
        if (controleurPartie != null) {
            controleurPartie.arreterDeplacementAutomatique();
        }
        Path chemin = DialoguesMenu.ouvrirDialogueChargementSauvegarde();
        if (chemin == null) {
            return;
        }

        try {
            ServicePersistance.SauvegardeChargee sauvegarde = ServicePersistance.chargerSauvegardeJson(chemin);
            if (sauvegarde.getPlateau() == null) {
                DialoguesMenu.afficherInformation("Erreur", "Sauvegarde invalide.");
                return;
            }

            plateauActuel = sauvegarde.getPlateau();
            moteurJeu = new LogiqueSokoban(plateauActuel);
            nomNiveauActuel = sauvegarde.getNiveau();
            Animation.reinitialiserAnimationPersonnage();
            arreterAnimationVictoire();
            transitionVictoireEnCours = false;
            modeJeuActif = true;
            mettreAJourVisibilitePlateau();
            if (stagePrincipal != null && scenePrincipale != null && stagePrincipal.getScene() != scenePrincipale) {
                stagePrincipal.setScene(scenePrincipale);
            }
            replierMenuJeu();
            dessinerPlateauActuel();
            activerPleinEcranEtFocus();
            DialoguesMenu.afficherInformation("Sauvegarde", "Sauvegarde chargee : " + chemin.getFileName());
        } catch (Exception e) {
            DialoguesMenu.afficherInformation("Erreur", "Echec du chargement.");
        }
    }

    private void lancerAnimationVictoire(Runnable actionApresAnimation) {
        if (victoireAnimee) {
            return;
        }

        if (controleurPartie != null) {
            controleurPartie.arreterDeplacementAutomatique();
        }

        transitionVictoireEnCours = true;
        victoireAnimee = true;
        debutAnimationVictoireNs = 0L;
        actionFinVictoire = actionApresAnimation;
        if (animationVictoire == null) {
            animationVictoire = new AnimationTimer() {
                @Override
                public void handle(long maintenantNs) {
                    if (debutAnimationVictoireNs == 0L) {
                        debutAnimationVictoireNs = maintenantNs;
                    }
                    tempsAnimationVictoireNs = maintenantNs;
                    feuArtifice.mettreAJour(true, canvasPlateau.getWidth(), canvasPlateau.getHeight(), maintenantNs);
                    dessinerPlateauActuel();
                    if (feuArtifice.doitFermer(maintenantNs)
                        || feuArtificeDureeAtteinte(maintenantNs)) {
                        Runnable action = actionFinVictoire;
                        arreterAnimationVictoire();
                        dessinerPlateauActuel();
                        transitionVictoireEnCours = false;
                        if (action != null) {
                            action.run();
                        }
                    }
                }
            };
        }
        animationVictoire.start();
    }

    private boolean feuArtificeDureeAtteinte(long maintenantNs) {
        return debutAnimationVictoireNs > 0L
            && (maintenantNs - debutAnimationVictoireNs) >= DUREE_TRANSITION_VICTOIRE_NS;
    }

    private void arreterAnimationVictoire() {
        victoireAnimee = false;
        actionFinVictoire = null;
        if (animationVictoire != null) {
            animationVictoire.stop();
        }
        feuArtifice.mettreAJour(false, canvasPlateau.getWidth(), canvasPlateau.getHeight(), System.nanoTime());
        tempsAnimationVictoireNs = 0L;
        debutAnimationVictoireNs = 0L;
    }

    private void activerPleinEcranEtFocus() {
        if (stagePrincipal == null) {
            return;
        }

        Platform.runLater(() -> {
            stagePrincipal.setMaximized(true);
            stagePrincipal.setFullScreen(true);
            if (stagePrincipal.getScene() != null && stagePrincipal.getScene().getRoot() != null) {
                stagePrincipal.getScene().getRoot().requestFocus();
            }
        });
    }

    private void mettreAJourVisibilitePlateau() {
        if (canvasPlateau == null) {
            return;
        }

        canvasPlateau.setVisible(modeJeuActif);
        canvasPlateau.setManaged(modeJeuActif);
    }

    private boolean gererNavigationMenuPrincipal(KeyEvent event) {
        if (event == null || menuPrincipal == null || !menuPrincipal.isVisible()) {
            return false;
        }
        if (boutonsMenuPrincipal == null || boutonsMenuPrincipal.isEmpty()) {
            return false;
        }

        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z || code == KeyCode.W) {
            focusBoutonMenuPrincipal(indexBoutonMenuPrincipal - 1);
            return true;
        }
        if (code == KeyCode.DOWN || code == KeyCode.S) {
            focusBoutonMenuPrincipal(indexBoutonMenuPrincipal + 1);
            return true;
        }
        if (code == KeyCode.TAB) {
            focusBoutonMenuPrincipal(indexBoutonMenuPrincipal + (event.isShiftDown() ? -1 : 1));
            return true;
        }
        if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
            boutonsMenuPrincipal.get(indexBoutonMenuPrincipal).fire();
            return true;
        }
        return false;
    }

    private void focusBoutonMenuPrincipal(int indexSouhaite) {
        if (boutonsMenuPrincipal == null || boutonsMenuPrincipal.isEmpty()) {
            return;
        }

        int taille = boutonsMenuPrincipal.size();
        indexBoutonMenuPrincipal = ((indexSouhaite % taille) + taille) % taille;
        Button bouton = boutonsMenuPrincipal.get(indexBoutonMenuPrincipal);
        if (bouton != null) {
            bouton.requestFocus();
        }
    }

    private Direction convertirToucheEnDirection(KeyCode code) {
        if (code == null) {
            return null;
        }

        switch (code) {
            case UP:
            case Z:
            case W:
                return Direction.HAUT;
            case DOWN:
            case S:
                return Direction.BAS;
            case LEFT:
            case Q:
            case A:
                return Direction.GAUCHE;
            case RIGHT:
            case D:
                return Direction.DROITE;
            default:
                return null;
        }
    }
}
