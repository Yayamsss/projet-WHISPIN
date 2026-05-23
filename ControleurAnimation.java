public class ControleurAnimation {
    private static final double NANOSECONDES_PAR_SECONDE = 1_000_000_000.0;

    public static final double DUREE_MARCHE_SECONDES = 0.20;
    public static final double DUREE_POUSSEE_SECONDES = 0.28;
    public static final double DUREE_BLOCAGE_SECONDES = 0.12;

    public enum Etat {
        REPOS,
        MARCHE,
        POUSSEE,
        BLOQUE,
        VICTOIRE
    }

    private Etat etat = Etat.REPOS;
    private Direction directionRegard = Direction.BAS;
    private long horodatageEtatNs = 0L;

    public void initialiserSiNecessaire(long maintenantNs) {
        if (horodatageEtatNs == 0L) {
            horodatageEtatNs = maintenantNs;
        }
    }

    public void notifierDeplacementReussi(Direction direction, boolean avecPoussee, boolean gagne, long maintenantNs) {
        this.directionRegard = direction;
        this.etat = gagne ? Etat.VICTOIRE : (avecPoussee ? Etat.POUSSEE : Etat.MARCHE);
        this.horodatageEtatNs = maintenantNs;
    }

    public void notifierDeplacementBloque(Direction direction, long maintenantNs) {
        this.directionRegard = direction;
        if (etat != Etat.VICTOIRE) {
            this.etat = Etat.BLOQUE;
            this.horodatageEtatNs = maintenantNs;
        }
    }

    public void notifierAnnulation(boolean gagne, long maintenantNs) {
        this.etat = gagne ? Etat.VICTOIRE : Etat.REPOS;
        this.horodatageEtatNs = maintenantNs;
    }

    public void mettreAJour(boolean gagne, long maintenantNs) {
        if (gagne) {
            if (etat != Etat.VICTOIRE) {
                etat = Etat.VICTOIRE;
                horodatageEtatNs = maintenantNs;
            }
            return;
        }

        double ecoule = dureeEcouleeEnSecondes(maintenantNs);
        if (etat == Etat.MARCHE && ecoule > DUREE_MARCHE_SECONDES) {
            etat = Etat.REPOS;
            horodatageEtatNs = maintenantNs;
        } else if (etat == Etat.POUSSEE && ecoule > DUREE_POUSSEE_SECONDES) {
            etat = Etat.REPOS;
            horodatageEtatNs = maintenantNs;
        } else if (etat == Etat.BLOQUE && ecoule > DUREE_BLOCAGE_SECONDES) {
            etat = Etat.REPOS;
            horodatageEtatNs = maintenantNs;
        }
    }

    public Etat getEtat() {
        return etat;
    }

    public Direction getDirectionRegard() {
        return directionRegard;
    }

    public double dureeEcouleeEnSecondes(long maintenantNs) {
        return Math.max(0.0, (maintenantNs - horodatageEtatNs) / NANOSECONDES_PAR_SECONDE);
    }
}
