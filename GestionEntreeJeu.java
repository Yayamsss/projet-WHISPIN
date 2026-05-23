import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Gère les entrées clavier du joueur et les traduit en actions de jeu.
 * Prend en charge deux modes : plateau seul et multivers (Sokoban récursif).
 */
public final class GestionEntreeJeu {

    private GestionEntreeJeu() {}

    // ─────────────────────────────────────────────────────────────────────────
    // Mode plateau simple
    // ─────────────────────────────────────────────────────────────────────────

    public static void gererTouche(KeyEvent evenement, Plateau plateau,
                                    ControleurAnimation controleurAnimation, long maintenantNs) {
        boolean actionEffectuee = false;
        KeyCode touche = evenement.getCode();
        Direction directionTentee = null;
        boolean vaPousser = false;

        if (evenement.isControlDown() && touche == KeyCode.Z) {
            // Ctrl+Z : annuler le dernier coup
            actionEffectuee = plateau.annulerDernierMouvement();
            if (actionEffectuee) {
                controleurAnimation.notifierAnnulation(plateau.estGagne(), maintenantNs);
            }
            return;
        }

        directionTentee = directionDepuisTouche(touche);
        if (directionTentee == null) return;

        vaPousser = plateau.vaPousserBoite(directionTentee);
        actionEffectuee = plateau.deplacer(directionTentee);

        if (actionEffectuee) {
            controleurAnimation.notifierDeplacementReussi(
                directionTentee, vaPousser, plateau.estGagne(), maintenantNs);
        } else {
            controleurAnimation.notifierDeplacementBloque(directionTentee, maintenantNs);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mode multivers (Sokoban récursif)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Gère une touche dans le contexte d'un Multivers.
     *
     * Le Ctrl+Z annule le dernier mouvement sur le plateau courant.
     * Si le joueur est entré dans un monde (profondeur > 0) et que l'historique
     * du monde courant est vide, on remonte automatiquement dans le monde parent.
     */
    public static void gererTouche(KeyEvent evenement, Multivers multivers,
                                    ControleurAnimation controleurAnimation, long maintenantNs) {
        KeyCode touche = evenement.getCode();

        if (evenement.isControlDown() && touche == KeyCode.Z) {
            gererAnnulation(multivers, controleurAnimation, maintenantNs);
            return;
        }

        Direction direction = directionDepuisTouche(touche);
        if (direction == null) return;

        Plateau courant = multivers.getPlateauCourant();
        boolean vaPousser = courant.vaPousserBoite(direction);

        Multivers.ResultatDeplacement resultat = multivers.deplacer(direction);

        if (resultat != Multivers.ResultatDeplacement.BLOQUE) {
            controleurAnimation.notifierDeplacementReussi(
                direction, vaPousser, multivers.estGagne(), maintenantNs);
        } else {
            controleurAnimation.notifierDeplacementBloque(direction, maintenantNs);
        }
    }

    /**
     * Annule le dernier mouvement.
     * Si l'historique du monde courant est vide et qu'on est dans un sous-monde,
     * on remonte dans le monde parent (on « annule » l'entrée).
     */
    private static void gererAnnulation(Multivers multivers,
                                         ControleurAnimation controleurAnimation,
                                         long maintenantNs) {
        Plateau courant = multivers.getPlateauCourant();

        if (!courant.getHistorique().isEmpty()) {
            // Il reste des mouvements à annuler dans ce monde
            courant.annulerDernierMouvement();
            controleurAnimation.notifierAnnulation(multivers.estGagne(), maintenantNs);
        } else if (multivers.peutSortir()) {
            // Historique vide dans un sous-monde → on remonte dans le parent
            // (équivaut à annuler l'entrée dans la boîte-monde)
            Multivers.ContexteNavigation contexte = multivers.sortir();
            Plateau parent = multivers.getPlateauCourant();

            // Replacer le joueur là où il était dans le monde parent (sur la boîte)
            Position posBoite = contexte.positionBoite;
            if (parent.estDansLimites(posBoite)) {
                Case caseBoite = parent.getCase(posBoite);
                // La boîte-monde est encore là, on place le joueur juste avant
                Direction dirRetour = contexte.directionEntree;
                Position avantBoite = posBoite.deplacer(dirRetour.getOpposee());
                if (parent.estDansLimites(avantBoite) && parent.getCase(avantBoite).estTraversable()) {
                    parent.teleporterPersonnage(avantBoite);
                } else {
                    // Fallback : n'importe quelle case libre autour de la boîte
                    Position libre = trouverCaseLibreAutour(parent, posBoite);
                    if (libre != null) parent.teleporterPersonnage(libre);
                }
            }
            controleurAnimation.notifierAnnulation(multivers.estGagne(), maintenantNs);
        }
        // Sinon : historique vide dans le monde racine → rien à faire
    }

    /** Cherche la première case traversable autour d'une position donnée. */
    private static Position trouverCaseLibreAutour(Plateau plateau, Position centre) {
        for (Direction d : Direction.values()) {
            Position p = centre.deplacer(d);
            if (plateau.estDansLimites(p) && plateau.getCase(p).estTraversable()) {
                return p;
            }
        }
        return null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilitaire partagé
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Traduit un KeyCode en Direction de jeu.
     * Supporte ZQSD (clavier FR), WASD (clavier US) et les flèches directionnelles.
     *
     * @return la Direction correspondante, ou null si la touche n'est pas mappée
     */
    private static Direction directionDepuisTouche(KeyCode touche) {
        return switch (touche) {
            case UP,    Z, W -> Direction.HAUT;
            case DOWN,  S    -> Direction.BAS;
            case LEFT,  Q, A -> Direction.GAUCHE;
            case RIGHT, D    -> Direction.DROITE;
            default          -> null;
        };
    }
}
