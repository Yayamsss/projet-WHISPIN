import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Contrôleur principal d'une partie en cours.
 * Gère la boucle d'animation JavaFX, les entrées clavier,
 * les sauvegardes automatiques et le passage au niveau suivant.
 */
public class ControleurPartie {

    private static final Color FOND = Color.web("#ece8dc");

    /** Décrit le niveau suivant à charger après la victoire. */
    public record NiveauSuivant(
        Plateau plateau,
        Multivers multivers,
        Supplier<NiveauSuivant> apres
    ) {}

    private final Stage stage;
    private final Scene sceneMenu;
    private final Plateau plateau;
    private final Supplier<NiveauSuivant> niveauSuivantFournisseur;
    private final ControleurAnimation controleurAnimation;
    private final FeuArtifice feuArtifice;
    private final Canvas canvas;
    private final Scene scene;
    private final AnimationTimer timer;
    private final Image imageFond;

    /** Multivers courant (null si niveau classique sans boîtes-monde). */
    private Multivers multivers;

    private boolean retourMenuDemande;
    private boolean sauvegardeAutoEffectuee;

    public ControleurPartie(Stage stage, Scene sceneMenu, Plateau plateau,
                             Supplier<NiveauSuivant> niveauSuivant) {
        this.stage                    = stage;
        this.sceneMenu                = sceneMenu;
        this.plateau                  = plateau;
        this.niveauSuivantFournisseur = niveauSuivant;
        this.controleurAnimation      = new ControleurAnimation();
        this.feuArtifice              = new FeuArtifice();
        this.canvas                   = new Canvas(900, 700);

        // Charger l'image de fond depuis les ressources (optionnel)
        Image img = null;
        try {
            var url = ControleurPartie.class.getResource("/fond_principale_ecran-frame0.png");
            if (url != null) img = new Image(url.toExternalForm());
        } catch (Exception ignored) {}
        this.imageFond = img;

        StackPane racine = new StackPane(canvas);
        racine.setStyle("-fx-background-color: #1a2b26;");
        this.scene = new Scene(racine, 900, 700);
        this.scene.setFill(FOND);

        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");

        this.timer = creerBouclePrincipale();

        configurerScene();
    }

    /** Associe un Multivers à cette partie (pour le rendu des boîtes-monde). */
    public void setMultivers(Multivers multivers) {
        this.multivers = multivers;
    }

    public Scene getScene() { return scene; }

    public void demarrer() {
        timer.start();
        canvas.requestFocus();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Boucle principale
    // ─────────────────────────────────────────────────────────────────────────

    private AnimationTimer creerBouclePrincipale() {
        return new AnimationTimer() {
            @Override
            public void handle(long maintenantNs) {
                boolean gagne = estGagne();

                controleurAnimation.initialiserSiNecessaire(maintenantNs);
                controleurAnimation.mettreAJour(gagne, maintenantNs);

                // Sauvegarde automatique une seule fois à la victoire
                if (gagne && !sauvegardeAutoEffectuee) {
                    sauvegarderPartieAutomatique();
                    sauvegardeAutoEffectuee = true;
                }

                feuArtifice.mettreAJour(gagne, scene.getWidth(), scene.getHeight(), maintenantNs);

                // Après le feu d'artifice, passer au niveau suivant ou revenir au menu
                if (gagne && feuArtifice.doitFermer(maintenantNs) && !retourMenuDemande) {
                    stop();
                    passerAuNiveauSuivantOuMenu();
                    return;
                }

                redessiner(maintenantNs);
            }
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Configuration de la scène
    // ─────────────────────────────────────────────────────────────────────────

    private void configurerScene() {
        scene.widthProperty().addListener((obs, o, n) -> redessiner(System.nanoTime()));
        scene.heightProperty().addListener((obs, o, n) -> redessiner(System.nanoTime()));
        scene.setOnKeyPressed(e -> gererTouche(e, System.nanoTime()));
        canvas.setFocusTraversable(true);
        redessiner(System.nanoTime());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Gestion des touches
    // ─────────────────────────────────────────────────────────────────────────

    private void gererTouche(javafx.scene.input.KeyEvent evenement, long maintenantNs) {
        // Ctrl+S → sauvegarde manuelle nommée
        if (evenement.isControlDown() && evenement.getCode() == KeyCode.S) {
            sauvegarderPartieNommee();
            redessiner(maintenantNs);
            return;
        }

        // Échap → retour au menu (avec sauvegarde automatique)
        if (evenement.getCode() == KeyCode.ESCAPE) {
            sauvegarderAvantRetour();
            retournerAuMenu();
            return;
        }

        // Si la partie est déjà gagnée, on n'accepte plus de mouvements
        if (estGagne()) return;

        // Déléguer au gestionnaire d'entrées
        if (multivers != null) {
            GestionEntreeJeu.gererTouche(evenement, multivers, controleurAnimation, maintenantNs);
        } else {
            GestionEntreeJeu.gererTouche(evenement, plateau, controleurAnimation, maintenantNs);
        }

        redessiner(maintenantNs);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Rendu
    // ─────────────────────────────────────────────────────────────────────────

    private void redessiner(long maintenantNs) {
        Plateau aAfficher = (multivers != null) ? multivers.getPlateauCourant() : plateau;
        RenduPlateau.redessiner(canvas, scene.getWidth(), scene.getHeight(),
                                 aAfficher, multivers, controleurAnimation,
                                 feuArtifice, imageFond, maintenantNs);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Persistance
    // ─────────────────────────────────────────────────────────────────────────

    private void sauvegarderPartieAutomatique() {
        Path chemin = ServicePersistance.creerCheminSauvegardeAuto();
        sauvegarderVersChemin(chemin, "Auto-sauvegarde de fin de partie");
    }

    private void sauvegarderPartieNommee() {
        TextInputDialog dialogue = new TextInputDialog("partie_1");
        dialogue.setTitle("Sauvegarde");
        dialogue.setHeaderText("Nommer la sauvegarde");
        dialogue.setContentText("Nom :");

        Optional<String> resultat = dialogue.showAndWait();
        if (resultat.isEmpty()) return;

        Path chemin = ServicePersistance.creerCheminSauvegardeNommee(resultat.get());
        sauvegarderVersChemin(chemin, "Sauvegarde nommée");
    }

    private void sauvegarderAvantRetour() {
        Path chemin = ServicePersistance.creerCheminSauvegardeAuto();
        sauvegarderVersChemin(chemin, "Sauvegarde avant retour menu");
    }

    private void sauvegarderVersChemin(Path chemin, String libelle) {
        try {
            if (multivers != null) {
                ServicePersistance.sauvegarderMultivers(chemin, multivers);
            } else {
                ServicePersistance.sauvegarderPlateauDansFichierTexte(chemin, plateau.getGrille(), 'A');
            }
            System.out.println("[Persistance] " + libelle + " : " + chemin);
        } catch (Exception e) {
            System.err.println("[Persistance] Échec " + libelle + " : " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navigation entre niveaux
    // ─────────────────────────────────────────────────────────────────────────

    private boolean estGagne() {
        return (multivers != null) ? multivers.estGagne() : plateau.estGagne();
    }

    private void passerAuNiveauSuivantOuMenu() {
        retourMenuDemande = true;
        timer.stop();

        stage.setScene(sceneMenu);
        stage.setFullScreen(true);
        sceneMenu.getRoot().requestFocus();
        if (niveauSuivantFournisseur != null) {
            NiveauSuivant ns = niveauSuivantFournisseur.get();
            if (ns != null) {
                stage.setScene(DeuxiemeScene.creerScene(
                    stage, sceneMenu, ns.plateau(), ns.multivers(), ns.apres()));
                Platform.runLater(() -> stage.setFullScreen(true));
                return;
            }
        }
        stage.setScene(sceneMenu);
        sceneMenu.getRoot().requestFocus();
    }

    private void retournerAuMenu() {
        if (retourMenuDemande) return;
        stage.setScene(sceneMenu);
        stage.setFullScreen(true);
        sceneMenu.getRoot().requestFocus(); 
        retourMenuDemande = true;
        timer.stop();
        stage.setScene(sceneMenu);
        sceneMenu.getRoot().requestFocus();
    }
}
