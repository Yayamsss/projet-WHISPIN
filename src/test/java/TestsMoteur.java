/**
 * Tests minimaux du moteur, exécutables sans framework externe.
 */
public final class TestsMoteur {
    private TestsMoteur() {
    }

    public static void main(String[] args) {
        testConversionTolerante();
        testPushVictoireEtUndo();
        testExportEtRejeuSokobano();
        testSokobanoInvalide();
        testEntreeSousMonde();
        System.out.println("[OK] TestsMoteur");
    }

    private static void testConversionTolerante() {
        Case videDepuisTiret = ConvertisseurCases.depuisSymboleTolerant('-', 0, 0);
        Case videDepuisUnderscore = ConvertisseurCases.depuisSymboleTolerant('_', 1, 0);
        verifier(videDepuisTiret instanceof CaseVide, "'-' doit produire CaseVide");
        verifier(videDepuisUnderscore instanceof CaseVide, "'_' doit produire CaseVide");
    }

    private static void testPushVictoireEtUndo() {
        Case[][] plateau = creerPlateauSimpleVictoire();
        LogiqueSokoban moteur = new LogiqueSokoban(plateau);

        verifier(!moteur.estVictoire(), "Le plateau initial ne doit pas etre deja gagne");
        verifier(moteur.deplacer(Direction.DROITE), "Le joueur doit pouvoir pousser la boite vers la cible");
        verifier(moteur.estVictoire(), "La partie doit etre gagnee apres la poussee");
        verifier(moteur.getCoups() == 1, "Le compteur de coups doit valoir 1");

        verifier(moteur.annulerDernierCoup(), "L'undo doit reussir apres un coup");
        verifier(!moteur.estVictoire(), "Apres undo, la victoire doit etre annulee");
        verifier(moteur.getCoups() == 0, "Apres undo, le compteur de coups doit revenir a 0");
    }

    private static void testExportEtRejeuSokobano() {
        Case[][] plateau = creerPlateauDeuxMouvements();
        LogiqueSokoban moteur = new LogiqueSokoban(plateau);

        verifier(moteur.deplacer(Direction.DROITE), "Premier mouvement valide attendu");
        verifier(moteur.deplacer(Direction.GAUCHE), "Second mouvement valide attendu");

        String solution = moteur.exporterCoupsSokobano();
        verifier("rl".equals(solution), "Export Sokobano attendu: rl");

        LogiqueSokoban moteurRejeu = new LogiqueSokoban(creerPlateauDeuxMouvements());
        verifier(moteurRejeu.rejouerCoupsSokobano(solution), "Le rejeu Sokobano doit reussir");
        verifier(moteurRejeu.getCoups() == 2, "Le rejeu doit produire 2 coups");
    }

    private static void testSokobanoInvalide() {
        LogiqueSokoban moteur = new LogiqueSokoban(creerPlateauDeuxMouvements());
        verifier(!moteur.rejouerCoupsSokobano("2x"), "Une direction invalide doit echouer");
    }

    private static void testEntreeSousMonde() {
        java.util.LinkedHashMap<Character, Case[][]> mondes = new java.util.LinkedHashMap<>();
        mondes.put('A', creerMondeRacineAvecPortail());
        mondes.put('B', creerSousMondeSimple());

        ChargeurNiveau.NiveauCharge niveau = new ChargeurNiveau.NiveauCharge('A', mondes);
        LogiqueSokoban moteur = new LogiqueSokoban(niveau);

        verifier(moteur.getMondeActuel() == 'A', "Le monde initial doit etre A");
        verifier(moteur.getProfondeurRecursion() == 0, "La profondeur initiale doit etre 0");
        verifier(moteur.deplacer(Direction.DROITE), "L'entree dans le sous-monde doit reussir");
        verifier(moteur.getMondeActuel() == 'B', "Le monde courant doit devenir B");
        verifier(moteur.getProfondeurRecursion() == 1, "La profondeur recursive doit valoir 1");
    }

    private static Case[][] creerPlateauSimpleVictoire() {
        int largeur = 5;
        int hauteur = 5;
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

        plateau[1][1] = new CasePersonnage(1, 1);
        plateau[1][2] = new CaseBoite(2, 1);
        plateau[1][3] = new CaseCible(3, 1);
        return plateau;
    }

    private static Case[][] creerPlateauDeuxMouvements() {
        int largeur = 5;
        int hauteur = 5;
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

        plateau[2][2] = new CasePersonnage(2, 2);
        plateau[1][1] = new CaseBoite(1, 1);
        plateau[3][3] = new CaseCible(3, 3);
        return plateau;
    }

    private static Case[][] creerMondeRacineAvecPortail() {
        int largeur = 5;
        int hauteur = 5;
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

        plateau[1][1] = new CaseBoite(1, 1);
        plateau[2][1] = new CasePersonnage(1, 2);
        plateau[2][2] = new CaseBoiteMondeSurCible(2, 2, 'B');
        return plateau;
    }

    private static Case[][] creerSousMondeSimple() {
        int largeur = 5;
        int hauteur = 5;
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

        plateau[2][2] = new CaseVide(2, 2);
        return plateau;
    }

    private static void verifier(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}