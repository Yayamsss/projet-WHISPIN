import javafx.scene.input.KeyCode;

/**
 * Contrôleur d'état du menu principal.
 * Centralise la navigation clavier/souris et les actions à exécuter.
 */
public class ControleurMenu {
    public enum Ecran {
        MENU,
        NIVEAUX,
        REGLES,
        PARAMETRES,
        SAUVEGARDE
    }

    public enum Action {
        AUCUNE,
        REDESSINER,
        JOUER,
        QUITTER
    }

    public enum ActionSauvegarde {
        AUCUNE,
        REDESSINER,
        RETOUR,
        LANCER_SELECTION,
        SUPPRIMER_SELECTION,
        RAFRAICHIR
    }

    public enum ActionNiveau {
        AUCUNE,
        REDESSINER,
        RETOUR,
        LANCER_SELECTION,
        RAFRAICHIR
    }

    private static final class OptionMenu {
        private final String texte;
        private final Ecran destination;
        private final Action action;

        private OptionMenu(String texte, Ecran destination, Action action) {
            this.texte = texte;
            this.destination = destination;
            this.action = action;
        }
    }

    private final OptionMenu[] optionsMenu = new OptionMenu[] {
        new OptionMenu("Jouer", Ecran.NIVEAUX, Action.REDESSINER),
        new OptionMenu("Regles du jeu", Ecran.REGLES, Action.REDESSINER),
        new OptionMenu("Parametre", Ecran.PARAMETRES, Action.REDESSINER),
        new OptionMenu("Sauvegarde", Ecran.SAUVEGARDE, Action.REDESSINER),
        new OptionMenu("Quitter", null, Action.QUITTER)
    };

    private Ecran ecranActuel = Ecran.MENU;
    private int indexSelectionne = 0;
    private int indexSelectionNiveau = 0;
    private int indexSelectionSauvegarde = 0;

    public Ecran getEcranActuel() {
        return ecranActuel;
    }

    public boolean estSurMenuPrincipal() {
        return ecranActuel == Ecran.MENU;
    }

    public int getIndexSelectionne() {
        return indexSelectionne;
    }

    public int getNombreOptions() {
        return optionsMenu.length;
    }

    public int getIndexSelectionSauvegarde() {
        return indexSelectionSauvegarde;
    }

    public int getIndexSelectionNiveau() {
        return indexSelectionNiveau;
    }

    public void definirIndexSelectionNiveau(int index) {
        this.indexSelectionNiveau = Math.max(0, index);
    }

    public void normaliserIndexSelectionNiveau(int nombreNiveauxAffiches) {
        int total = Math.max(1, nombreNiveauxAffiches + 1);
        if (indexSelectionNiveau >= total) {
            indexSelectionNiveau = total - 1;
        }
    }

    public void definirIndexSelectionSauvegarde(int index) {
        this.indexSelectionSauvegarde = Math.max(0, index);
    }

    public void normaliserIndexSelectionSauvegarde(int nombreSauvegardesAffichees) {
        int total = Math.max(1, nombreSauvegardesAffichees + 1);
        if (indexSelectionSauvegarde >= total) {
            indexSelectionSauvegarde = total - 1;
        }
    }

    public String getTexteOption(int index) {
        return optionsMenu[index].texte;
    }

    public String getTitreEcranSecondaire() {
        return switch (ecranActuel) {
            case NIVEAUX -> "Selection du niveau";
            case REGLES -> "Regles du jeu";
            case PARAMETRES -> "Parametre";
            case SAUVEGARDE -> "Sauvegarde";
            default -> "";
        };
    }

    public String[] getLignesEcranSecondaire() {
        return switch (ecranActuel) {
            case NIVEAUX -> new String[] {
                "Choisis un niveau dans le dossier niveau/.",
                "Entree lance le niveau selectionne."
            };
            case REGLES -> new String[] {
                "- Deplace le cube joueur avec ZQSD.",
                "- Pousse les cubes vers les portails.",
                "- Un cube ne peut pas traverser un mur.",
                "- Gagne quand tous les cubes sont dans les portails.",
                "- Ctrl+Z pour annuler le dernier coup."
            };
            case PARAMETRES -> new String[] {
                "Parametres graphiques et sonores",
                "(pas encore implémenté).",
                "",
                "Conseil: lance une partie via 'Jouer'",
                "pour tester le rendu Parabox."
            };
            case SAUVEGARDE -> new String[] {
                "Sauvegardes disponibles dans le dossier:",
                "sauvegardes/",
                "",
                "Selectionne une sauvegarde et appuie sur Entree",
                "pour lancer la partie correspondante."
            };
            default -> new String[0];
        };
    }

    public Action gererTouche(KeyCode code) {
        if (estSurMenuPrincipal()) {
            if (code == KeyCode.Z || code == KeyCode.UP || code == KeyCode.Q || code == KeyCode.LEFT) {
                indexSelectionne = (indexSelectionne - 1 + optionsMenu.length) % optionsMenu.length;
                return Action.REDESSINER;
            }
            if (code == KeyCode.S || code == KeyCode.DOWN || code == KeyCode.D || code == KeyCode.RIGHT) {
                indexSelectionne = (indexSelectionne + 1) % optionsMenu.length;
                return Action.REDESSINER;
            }
            if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
                return validerSelectionCourante();
            }
            if (code == KeyCode.ESCAPE) {
                return Action.QUITTER;
            }
            return Action.AUCUNE;
        }

        if (code == KeyCode.ENTER || code == KeyCode.SPACE || code == KeyCode.ESCAPE
                || code == KeyCode.BACK_SPACE || code == KeyCode.Q || code == KeyCode.LEFT) {
            ecranActuel = Ecran.MENU;
            return Action.REDESSINER;
        }

        return Action.AUCUNE;
    }

    public Action gererSelectionSouris(int index) {
        indexSelectionne = index;
        return validerSelectionCourante();
    }

    public Action gererRetourSecondaire() {
        ecranActuel = Ecran.MENU;
        return Action.REDESSINER;
    }

    public ActionSauvegarde gererToucheSauvegarde(KeyCode code, int nombreSauvegardesAffichees) {
        int total = Math.max(1, nombreSauvegardesAffichees + 1);

        if (code == KeyCode.ESCAPE || code == KeyCode.BACK_SPACE || code == KeyCode.Q || code == KeyCode.LEFT) {
            return ActionSauvegarde.RETOUR;
        }
        if (code == KeyCode.R) {
            return ActionSauvegarde.RAFRAICHIR;
        }
        if (code == KeyCode.TAB || code == KeyCode.S || code == KeyCode.DOWN) {
            deplacerSelectionSauvegarde(1, total);
            return ActionSauvegarde.REDESSINER;
        }
        if (code == KeyCode.Z || code == KeyCode.UP) {
            deplacerSelectionSauvegarde(-1, total);
            return ActionSauvegarde.REDESSINER;
        }
        if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
            return estRetourSauvegardeSelectionne(nombreSauvegardesAffichees)
                ? ActionSauvegarde.RETOUR
                : ActionSauvegarde.LANCER_SELECTION;
        }
        if (code == KeyCode.DELETE) {
            if (estRetourSauvegardeSelectionne(nombreSauvegardesAffichees)) {
                return ActionSauvegarde.AUCUNE;
            }
            return ActionSauvegarde.SUPPRIMER_SELECTION;
        }

        return ActionSauvegarde.AUCUNE;
    }

    public ActionNiveau gererToucheNiveau(KeyCode code, int nombreNiveauxAffiches) {
        int total = Math.max(1, nombreNiveauxAffiches + 1);

        if (code == KeyCode.ESCAPE || code == KeyCode.BACK_SPACE || code == KeyCode.Q || code == KeyCode.LEFT) {
            return ActionNiveau.RETOUR;
        }
        if (code == KeyCode.R) {
            return ActionNiveau.RAFRAICHIR;
        }
        if (code == KeyCode.TAB || code == KeyCode.S || code == KeyCode.DOWN) {
            deplacerSelectionNiveau(1, total);
            return ActionNiveau.REDESSINER;
        }
        if (code == KeyCode.Z || code == KeyCode.UP) {
            deplacerSelectionNiveau(-1, total);
            return ActionNiveau.REDESSINER;
        }
        if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
            return estRetourNiveauSelectionne(nombreNiveauxAffiches)
                ? ActionNiveau.RETOUR
                : ActionNiveau.LANCER_SELECTION;
        }

        return ActionNiveau.AUCUNE;
    }

    public boolean estRetourNiveauSelectionne(int nombreNiveauxAffiches) {
        return indexSelectionNiveau >= Math.max(0, nombreNiveauxAffiches);
    }

    private void deplacerSelectionNiveau(int delta, int total) {
        indexSelectionNiveau = (indexSelectionNiveau + delta) % total;
        if (indexSelectionNiveau < 0) {
            indexSelectionNiveau += total;
        }
    }

    public boolean estRetourSauvegardeSelectionne(int nombreSauvegardesAffichees) {
        return indexSelectionSauvegarde >= Math.max(0, nombreSauvegardesAffichees);
    }

    private void deplacerSelectionSauvegarde(int delta, int total) {
        indexSelectionSauvegarde = (indexSelectionSauvegarde + delta) % total;
        if (indexSelectionSauvegarde < 0) {
            indexSelectionSauvegarde += total;
        }
    }

    private Action validerSelectionCourante() {
        OptionMenu option = optionsMenu[indexSelectionne];
        if (option.action == Action.REDESSINER && option.destination != null) {
            ecranActuel = option.destination;
            if (option.destination == Ecran.NIVEAUX) {
                indexSelectionNiveau = 0;
            }
            if (option.destination == Ecran.SAUVEGARDE) {
                indexSelectionSauvegarde = 0;
            }
        }
        return option.action;
    }
}