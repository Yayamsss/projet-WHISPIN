import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente un instantané complet d'une partie Sokoban.
 */
public class EtatPartie {
    private final String niveau;
    private final int coups;
    private final long horodatage;
    private final List<String> lignesPlateau;
    private final List<Mouvement> mouvementsChemin;

    /**
     * Construit un état de partie valide.
     *
     * @param niveau nom du niveau
     * @param coups nombre de coups joués
     * @param horodatage date/heure de l'état
     * @param plateau lignes du plateau au format texte
     * @param chemin liste des mouvements effectués
     */
    public EtatPartie(String niveau, int coups, long horodatage, List<String> plateau, List<Mouvement> chemin) {
        if (niveau == null || niveau.isBlank()) {
            throw new IllegalArgumentException("Le nom du niveau est obligatoire");
        }
        if (coups < 0) {
            throw new IllegalArgumentException("Le nombre de coups ne peut pas être négatif");
        }
        if (plateau == null || plateau.isEmpty()) {
            throw new IllegalArgumentException("Le plateau ne peut pas être vide");
        }
        this.niveau = niveau;
        this.coups = coups;
        this.horodatage = horodatage;
        this.lignesPlateau = Collections.unmodifiableList(new ArrayList<>(plateau));
        this.mouvementsChemin = (new ArrayList<>(chemin == null ? List.of() : chemin));
    }

    /**
     * @return le nom du niveau
     */
    public String getNiveau() {
        return niveau;
    }

    /**
     * @return le nombre de coups
     */
    public int getCoups() {
        return coups;
    }

    /**
     * @return l'horodatage de la sauvegarde
     */
    public long getHorodatage() {
        return horodatage;
    }

    /**
     * @return le plateau au format texte (lignes ASCII)
     */
    public List<String> getPlateau() {
        return lignesPlateau;
    }

    /**
     * @return la liste des mouvements enregistrés
     */
    public ArrayList<Mouvement> getChemin() {
        return new ArrayList<>(mouvementsChemin);
    }
}
