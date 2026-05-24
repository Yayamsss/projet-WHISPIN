import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fournit des utilitaires simples d'accès aux fichiers du projet.
 */
public final class ParcourirFichiers {
    private ParcourirFichiers() {
    }

    /**
     * Liste les fichiers d'un dossier, avec filtrage optionnel par extension.
     *
     * @param dossier chemin du dossier à parcourir
     * @param extension extension exigée (ex: ".txt"), ou null pour tout accepter
     * @return liste triée des noms de fichiers
     */
    public static List<String> listerFichiers(String dossier, String extension) {
        ArrayList<String> elements = new ArrayList<>();
        File repertoire = new File(dossier);
        if (!repertoire.exists() || !repertoire.isDirectory()) {
            return elements;
        }

        File[] fichiers = repertoire.listFiles();
        if (fichiers == null) {
            return elements;
        }

        for (File fichier : fichiers) {
            if (!fichier.isFile()) {
                continue;
            }
            if (extension != null && !fichier.getName().endsWith(extension)) {
                continue;
            }
            elements.add(fichier.getName());
        }

        elements.sort(String::compareToIgnoreCase);
        return elements;
    }
}
