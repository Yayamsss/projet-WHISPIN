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

import java.io.IOException;
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

        plateauActuel = null;
        nomNiveauActuel = null;
        moteurJeu = null;
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
        btnRegles.setOnAction(event -> ouvrirSceneRegles());
        btnSauvegarde.setOnAction(event -> ouvrirSceneSauvegarde());
        btnParamettre.setOnAction(event -> gererParametres());
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

        NavigationClavierUI.installerNavigationBoutons(
            scenePrincipale,
            boutonsMenuPrincipal,
            null,
            () -> !modeJeuActif && menuPrincipal != null && menuPrincipal.isVisible(),
            true
        );

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

        appliquerEtatPartieChargee(new LogiqueSokoban(niveauCharge), niveau);

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
        plateauActuel = null;
        moteurJeu = null;
        nomNiveauActuel = null;
        Animation.reinitialiserAnimationPersonnage();
        modeJeuActif = false;
        mettreAJourVisibilitePlateau();
        deplierMenuJeu();
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

    private void ouvrirSceneRegles() {
        if (stagePrincipal == null || scenePrincipale == null) {
            return;
        }

        Scene sceneRegles = SceneRegles.creer(
            scenePrincipale.getWidth(),
            scenePrincipale.getHeight(),
            creerActionRetourScenePrincipale()
        );
        afficherSceneSecondaire(sceneRegles);
    }

    private void ouvrirSceneSauvegarde() {
        if (stagePrincipal == null || scenePrincipale == null) {
            return;
        }

        Scene sceneSauvegarde = SceneSauvegarde.creer(
            scenePrincipale.getWidth(),
            scenePrincipale.getHeight(),
            modeJeuActif,
            this::sauvegarderEtatCourantDepuisScene,
            this::listerSauvegardesPourScene,
            this::chargerSauvegardeDepuisChemin,
            creerActionRetourScenePrincipale()
        );
        afficherSceneSecondaire(sceneSauvegarde);
    }

    private void gererParametres() {
        List<String> personnages = Animation.getPersonnagesDisponibles();
        if (personnages.isEmpty()) {
            DialoguesMenu.afficherInformation("Paramettre", "Aucun personnage trouve dans assets/player.");
            return;
        }

        ouvrirSceneChoixPersonnage(personnages);
    }

    private void ouvrirSceneChoixPersonnage(List<String> personnages) {
        if (stagePrincipal == null || scenePrincipale == null) {
            return;
        }

        Runnable retourScenePrincipale = creerActionRetourScenePrincipale();

        Scene sceneChoix = SceneChoixPersonnage.creer(
            scenePrincipale.getWidth(),
            scenePrincipale.getHeight(),
            personnages,
            Animation.getPersonnageActuel(),
            choisi -> {
                if (!Animation.selectionnerPersonnage(choisi)) {
                    DialoguesMenu.afficherInformation("Paramettre", "Impossible de charger le personnage choisi.");
                    return;
                }
                dessinerPlateauActuel();
                retourScenePrincipale.run();
            },
            retourScenePrincipale
        );

        afficherSceneSecondaire(sceneChoix);
    }

    private String sauvegarderEtatCourantDepuisScene() {
        if (!modeJeuActif || plateauActuel == null) {
            return "Aucune partie active a sauvegarder.";
        }

        try {
            Path chemin = ServicePersistance.creerCheminSauvegardeAuto();
            String solutionSokobano = moteurJeu == null ? "" : moteurJeu.exporterCoupsSokobano();
            ServicePersistance.sauvegarderSessionJson(chemin, nomNiveauActuel, plateauActuel, solutionSokobano);
            return "Sauvegarde creee : " + chemin.getFileName();
        } catch (IOException e) {
            return "Echec de la sauvegarde.";
        }
    }

    private List<ServicePersistance.SauvegardeInfo> listerSauvegardesPourScene() {
        try {
            return ServicePersistance.listerSauvegardesInfos();
        } catch (IOException e) {
            return List.of();
        }
    }

    private String chargerSauvegardeDepuisChemin(Path chemin) {
        if (controleurPartie != null) {
            controleurPartie.arreterDeplacementAutomatique();
        }
        if (chemin == null) {
            return "Aucune sauvegarde selectionnee.";
        }

        try {
            ServicePersistance.SauvegardeChargee sauvegarde = ServicePersistance.chargerSauvegardeJson(chemin);
            if (sauvegarde.getPlateau() == null) {
                return "Sauvegarde invalide.";
            }

            String niveauSauvegarde = sauvegarde.getNiveau();
            boolean etatReconstruit = false;
            String solutionSokobano = sauvegarde.getSolutionSokobano();
            if (niveauSauvegarde != null && !niveauSauvegarde.isBlank()
                && solutionSokobano != null && !solutionSokobano.isBlank()) {
                ChargeurNiveau.NiveauCharge niveauCharge =
                    ChargeurNiveau.chargerNiveauRecursifDepuisFichier("niveau/" + niveauSauvegarde);
                if (niveauCharge != null) {
                    LogiqueSokoban moteurReconstruit = new LogiqueSokoban(niveauCharge);
                    if (moteurReconstruit.rejouerCoupsSokobano(solutionSokobano)) {
                        moteurJeu = moteurReconstruit;
                        plateauActuel = moteurJeu.exporterPlateau();
                        etatReconstruit = true;
                    }
                }
            }

            if (!etatReconstruit) {
                moteurJeu = new LogiqueSokoban(sauvegarde.getPlateau());
            }

            appliquerEtatPartieChargee(moteurJeu, sauvegarde.getNiveau());
            return "Sauvegarde chargee : " + chemin.getFileName();
        } catch (IOException | IllegalArgumentException e) {
            return "Echec du chargement.";
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
            String solutionSokobano = moteurJeu == null ? "" : moteurJeu.exporterCoupsSokobano();
            ServicePersistance.sauvegarderSessionJson(chemin, nomNiveauActuel, plateauActuel, solutionSokobano);
            DialoguesMenu.afficherInformation("Sauvegarde", "Sauvegarde creee : " + chemin.getFileName());
        } catch (IOException e) {
            DialoguesMenu.afficherInformation("Erreur", "Echec de la sauvegarde personnalisee.");
        }
    }

    private void sauvegarderEtatCourantAutomatiqueSiPossible() {
        if (!modeJeuActif || plateauActuel == null) {
            return;
        }

        try {
            Path chemin = ServicePersistance.creerCheminSauvegardeAuto();
            String solutionSokobano = moteurJeu == null ? "" : moteurJeu.exporterCoupsSokobano();
            ServicePersistance.sauvegarderSessionJson(chemin, nomNiveauActuel, plateauActuel, solutionSokobano);
        } catch (IOException e) {
            // Les sauvegardes auto ne doivent pas interrompre le flux de jeu.
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

    private Runnable creerActionRetourScenePrincipale() {
        return () -> {
            if (stagePrincipal != null && scenePrincipale != null) {
                stagePrincipal.setScene(scenePrincipale);
            }
            activerPleinEcranEtFocus();
            if (!modeJeuActif) {
                focusBoutonMenuPrincipal(indexBoutonMenuPrincipal);
            } else if (racinePrincipale != null) {
                racinePrincipale.requestFocus();
            }
        };
    }

    private void afficherSceneSecondaire(Scene scene) {
        if (stagePrincipal == null || scene == null) {
            return;
        }
        stagePrincipal.setScene(scene);
        activerPleinEcranEtFocus();
    }

    private void appliquerEtatPartieChargee(LogiqueSokoban moteurCharge, String nomNiveau) {
        if (moteurCharge == null) {
            return;
        }

        moteurJeu = moteurCharge;
        plateauActuel = moteurJeu.exporterPlateau();
        nomNiveauActuel = nomNiveau;
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
    }

    private void mettreAJourVisibilitePlateau() {
        if (canvasPlateau == null) {
            return;
        }

        canvasPlateau.setVisible(modeJeuActif);
        canvasPlateau.setManaged(modeJeuActif);
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
