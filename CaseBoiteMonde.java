import javafx.scene.paint.Color;

/**
 * Représente une boîte-monde dans le Sokoban récursif.
 * Une boîte-monde est une boîte classique qui référence un autre monde (Plateau).
 * Sa lettre indique quel monde elle contient : 'b' ou 'B' → monde B.
 *
 * Exemple de symbole dans un fichier niveau :
 *   lettre minuscule → boîte-monde hors cible
 *   lettre majuscule → boîte-monde sur cible
 */
public class CaseBoiteMonde extends CaseBoite {

    /** Identifiant du monde contenu (lettre majuscule, ex: 'B'). */
    private final char identifiantMonde;

    /** Couleur visuelle associée à ce monde (affichage récursif). */
    private final Color couleur;

    /**
     * Palette de couleurs prédéfinies pour distinguer les mondes visuellement.
     * Attribuées par ordre d'apparition via {@link #couleurPourMonde(char)}.
     */
    private static final Color[] PALETTE = {
        Color.web("#e87c2a"), // A - orange
        Color.web("#4eb8e0"), // B - bleu ciel
        Color.web("#a8e06a"), // C - vert clair
        Color.web("#e06ab0"), // D - rose
        Color.web("#f2d974"), // E - jaune
        Color.web("#c07ae0"), // F - violet
        Color.web("#e04040"), // G - rouge
        Color.web("#40e0a0"), // H - turquoise
    };

    /**
     * Retourne une couleur fixe associée à un identifiant de monde.
     *
     * @param identifiant lettre du monde (A–Z)
     * @return couleur associée
     */
    public static Color couleurPourMonde(char identifiant) {
        int index = (Character.toUpperCase(identifiant) - 'A') % PALETTE.length;
        return PALETTE[Math.max(0, index)];
    }

    /**
     * Construit une boîte-monde.
     *
     * @param identifiantMonde lettre du monde référencé (sera mis en majuscule)
     * @param surCible         true si la boîte est sur une cible
     */
    public CaseBoiteMonde(char identifiantMonde, boolean surCible) {
        super(surCible);
        this.identifiantMonde = Character.toUpperCase(identifiantMonde);
        this.couleur = couleurPourMonde(this.identifiantMonde);
    }

    /**
     * Retourne l'identifiant (lettre) du monde contenu.
     *
     * @return lettre majuscule, ex: 'B'
     */
    public char getIdentifiantMonde() {
        return identifiantMonde;
    }

    /**
     * Retourne la couleur associée à ce monde.
     *
     * @return couleur JavaFX
     */
    public Color getCouleur() {
        return couleur;
    }

    /**
     * Le symbole de sauvegarde : minuscule si hors cible, majuscule si sur cible.
     *
     * @return symbole ASCII de cette boîte-monde
     */
    @Override
    public char getSymbole() {
        return estSurCible()
            ? Character.toUpperCase(identifiantMonde)
            : Character.toLowerCase(identifiantMonde);
    }

    @Override
    public String toString() {
        return "CaseBoiteMonde{monde='" + identifiantMonde + "', surCible=" + estSurCible() + "}";
    }
}
