import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Utilitaires de navigation clavier reutilisables pour les scenes JavaFX.
 */
public final class NavigationClavierUI {
    private NavigationClavierUI() {
    }

    /**
     * Installe une navigation clavier cyclique sur une liste de boutons.
     */
    public static void installerNavigationBoutons(Scene scene, List<Button> boutons, Runnable onEscape) {
        installerNavigationBoutons(scene, boutons, onEscape, () -> true, false);
    }

    /**
     * Installe une navigation clavier cyclique sur une liste de boutons.
     */
    public static void installerNavigationBoutons(
        Scene scene,
        List<Button> boutons,
        Runnable onEscape,
        Supplier<Boolean> navigationActive,
        boolean supportAzerty
    ) {
        if (scene == null || boutons == null || boutons.isEmpty()) {
            return;
        }

        final int[] indexFocus = new int[] { trouverIndexSelectionnable(boutons, 0, 1) };
        for (int i = 0; i < boutons.size(); i++) {
            final int index = i;
            Button bouton = boutons.get(i);
            bouton.focusedProperty().addListener((obs, oldValue, isFocused) -> {
                if (isFocused) {
                    indexFocus[0] = index;
                }
            });
            bouton.setOnMouseEntered(event -> indexFocus[0] = index);
        }

        Platform.runLater(() -> {
            int indexInitial = trouverIndexSelectionnable(boutons, indexFocus[0], 1);
            if (indexInitial >= 0) {
                indexFocus[0] = indexInitial;
                boutons.get(indexInitial).requestFocus();
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (navigationActive != null && !navigationActive.get()) {
                return;
            }

            KeyCode code = event.getCode();

            if (code == KeyCode.ESCAPE) {
                if (onEscape != null) {
                    onEscape.run();
                }
                event.consume();
                return;
            }

            if (code == KeyCode.UP || code == KeyCode.LEFT
                || (supportAzerty && (code == KeyCode.Z || code == KeyCode.W || code == KeyCode.Q || code == KeyCode.A))
                || (code == KeyCode.TAB && event.isShiftDown())) {
                int indexPrecedent = trouverIndexSelectionnable(boutons, indexFocus[0] - 1, -1);
                if (indexPrecedent >= 0) {
                    indexFocus[0] = indexPrecedent;
                    boutons.get(indexPrecedent).requestFocus();
                }
                event.consume();
                return;
            }

            if (code == KeyCode.DOWN || code == KeyCode.RIGHT
                || (supportAzerty && code == KeyCode.S)
                || code == KeyCode.TAB) {
                int indexSuivant = trouverIndexSelectionnable(boutons, indexFocus[0] + 1, 1);
                if (indexSuivant >= 0) {
                    indexFocus[0] = indexSuivant;
                    boutons.get(indexSuivant).requestFocus();
                }
                event.consume();
                return;
            }

            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                if (indexFocus[0] >= 0 && indexFocus[0] < boutons.size()) {
                    Button bouton = boutons.get(indexFocus[0]);
                    if (!bouton.isDisable()) {
                        bouton.fire();
                    }
                }
                event.consume();
            }
        });
    }

    /**
     * Installe une navigation clavier pour une grille logique d'elements.
     */
    public static void installerNavigationGrille(
        Scene scene,
        int nombreElements,
        int colonnes,
        int indexInitial,
        IntConsumer onSelection,
        Runnable onValider,
        Runnable onEscape,
        boolean supportAzerty
    ) {
        if (scene == null || nombreElements <= 0 || colonnes <= 0) {
            return;
        }

        final int[] indexSelection = new int[] { borner(indexInitial, 0, nombreElements - 1) };
        if (onSelection != null) {
            onSelection.accept(indexSelection[0]);
        }

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();

            if (code == KeyCode.ESCAPE) {
                if (onEscape != null) {
                    onEscape.run();
                }
                event.consume();
                return;
            }

            int indexCible = indexSelection[0];
            if (code == KeyCode.LEFT
                || (supportAzerty && (code == KeyCode.Q || code == KeyCode.A))
                || (code == KeyCode.TAB && event.isShiftDown())) {
                indexCible = indexSelection[0] - 1;
            } else if (code == KeyCode.RIGHT
                || (supportAzerty && code == KeyCode.D)
                || code == KeyCode.TAB) {
                indexCible = indexSelection[0] + 1;
            } else if (code == KeyCode.UP
                || (supportAzerty && (code == KeyCode.Z || code == KeyCode.W))) {
                indexCible = indexSelection[0] - colonnes;
            } else if (code == KeyCode.DOWN
                || (supportAzerty && code == KeyCode.S)) {
                indexCible = indexSelection[0] + colonnes;
            }

            if (indexCible != indexSelection[0] && indexCible >= 0 && indexCible < nombreElements) {
                indexSelection[0] = indexCible;
                if (onSelection != null) {
                    onSelection.accept(indexSelection[0]);
                }
                event.consume();
                return;
            }

            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                if (onValider != null) {
                    onValider.run();
                }
                event.consume();
            }
        });
    }

    private static int trouverIndexSelectionnable(List<Button> boutons, int depart, int direction) {
        int taille = boutons.size();
        int index = ((depart % taille) + taille) % taille;
        for (int i = 0; i < taille; i++) {
            Button bouton = boutons.get(index);
            if (!bouton.isDisable() && bouton.isVisible() && bouton.isManaged()) {
                return index;
            }
            index = ((index + direction) % taille + taille) % taille;
        }
        return -1;
    }

    private static int borner(int valeur, int min, int max) {
        return Math.max(min, Math.min(max, valeur));
    }
}