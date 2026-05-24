import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.ArrayList;
import java.util.List;

public final class RenduPlateau {

    private static final Color FOND_GENERAL  = Color.web("#d0d0d0");
    private static final Color CADRE_BLANC   = Color.web("#ffffff");
    private static final Color ANNEAU_SOMBRE = Color.web("#444444");
    private static final Color CHAMP_JEU     = Color.web("#585858");

    private static final double BORD_CADRE       = 4.0;
    private static final double EPAISSEUR_ANNEAU = 10.0;

    private RenduPlateau() {}

    public static void redessiner(
        Canvas canvas,
        double largeur,
        double hauteur,
        Plateau plateau,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        Image imageFond,
        long maintenantNs
    ) {
        redessiner(canvas, largeur, hauteur, plateau, null, controleurAnimation, feuArtifice, imageFond, maintenantNs);
    }

    public static void redessiner(
        Canvas canvas,
        double largeur,
        double hauteur,
        Plateau plateau,
        Multivers multivers,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        Image imageFond,
        long maintenantNs
    ) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));
        dessiner(canvas.getGraphicsContext2D(), plateau, multivers, controleurAnimation, feuArtifice, maintenantNs);
    }

    public static void redessinerRecursif(
        Canvas canvas,
        double largeur,
        double hauteur,
        Plateau plateau,
        Multivers multivers,
        RecursiveSceneViewModel viewModel,
        RecursiveTransitionController transition,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        Image imageFond,
        long maintenantNs
    ) {
        canvas.setWidth(Math.max(largeur, 1));
        canvas.setHeight(Math.max(hauteur, 1));

        if (multivers == null || viewModel == null) {
            dessiner(canvas.getGraphicsContext2D(), plateau, null, controleurAnimation, feuArtifice, maintenantNs);
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double W = gc.getCanvas().getWidth();
        double H = gc.getCanvas().getHeight();

        gc.setFill(FOND_GENERAL);
        gc.fillRect(0, 0, W, H);

        double marge     = Math.min(W, H) * 0.06;
        double cadreX    = marge;
        double cadreY    = marge;
        double cadreW    = W - marge * 2;
        double cadreH    = H - marge * 2;
        double coinCadre = Math.max(16, Math.min(cadreW, cadreH) * 0.06);

        gc.setFill(Color.web("#000000", 0.18));
        gc.fillRoundRect(cadreX + 4, cadreY + 6, cadreW, cadreH, coinCadre, coinCadre);

        gc.setFill(CADRE_BLANC);
        gc.fillRoundRect(cadreX, cadreY, cadreW, cadreH, coinCadre, coinCadre);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(BORD_CADRE);
        gc.strokeRoundRect(cadreX + BORD_CADRE / 2, cadreY + BORD_CADRE / 2,
                           cadreW - BORD_CADRE, cadreH - BORD_CADRE, coinCadre, coinCadre);

        double pad        = BORD_CADRE + 8;
        double anneauX    = cadreX + pad;
        double anneauY    = cadreY + pad;
        double anneauW    = cadreW - pad * 2;
        double anneauH    = cadreH - pad * 2;
        double coinAnneau = Math.max(10, coinCadre * 0.7);

        gc.setFill(ANNEAU_SOMBRE);
        gc.fillRoundRect(anneauX, anneauY, anneauW, anneauH, coinAnneau, coinAnneau);

        double champX    = anneauX + EPAISSEUR_ANNEAU;
        double champY    = anneauY + EPAISSEUR_ANNEAU;
        double champW    = anneauW - EPAISSEUR_ANNEAU * 2;
        double champH    = anneauH - EPAISSEUR_ANNEAU * 2;
        double coinChamp = Math.max(6, coinAnneau * 0.5);

        gc.setFill(CHAMP_JEU);
        gc.fillRoundRect(champX, champY, champW, champH, coinChamp, coinChamp);

        List<RecursiveSceneViewModel.Layer> layers = viewModel.buildLayers(multivers, champW, champH);
        if (layers.isEmpty()) {
            int nLignes   = plateau.getGrille().size();
            int nColonnes = plateau.getGrille().isEmpty() ? 1 : plateau.getGrille().get(0).size();
            double tailleCase = Math.min(champW / nColonnes, champH / nLignes);
            double origineX   = champX + (champW - nColonnes * tailleCase) / 2.0;
            double origineY   = champY + (champH - nLignes * tailleCase) / 2.0;
            dessinerGrille(gc, plateau, multivers, controleurAnimation, maintenantNs,
                           nLignes, nColonnes, tailleCase, origineX, origineY);
        } else {
            gc.save();
            gc.beginPath();
            gc.rect(champX, champY, champW, champH);
            gc.clip();

            if (transition != null && transition.isActive()) {
                Plateau mondeSource = multivers.getPlateau(transition.getFromWorld());
                Plateau mondeCible = multivers.getPlateau(transition.getToWorld());
                if (mondeSource == null) mondeSource = plateau;
                if (mondeCible == null) mondeCible = plateau;

                double p = clamp(0.0, 1.0, transition.getProgress());

                if (transition.getType() == RecursiveTransitionController.TransitionType.ENTER) {
                    dessinerLayerDansViewport(
                        gc, mondeSource, multivers, controleurAnimation, maintenantNs,
                        champX, champY, champW, champH,
                        lerp(1.0, 0.94, p),
                        lerp(0.0, -18.0, p),
                        lerp(0.0, -12.0, p),
                        lerp(1.0, 0.45, p)
                    );
                    dessinerLayerDansViewport(
                        gc, mondeCible, multivers, controleurAnimation, maintenantNs,
                        champX, champY, champW, champH,
                        clamp(0.2, 4.0, 1.22 - (0.22 * p) * transition.getScaleLerp()),
                        transition.getOffsetXLerp(),
                        transition.getOffsetYLerp(),
                        lerp(0.30, 1.0, p)
                    );
                } else {
                    // EXIT
                    dessinerLayerDansViewport(
                        gc, mondeSource, multivers, controleurAnimation, maintenantNs,
                        champX, champY, champW, champH,
                        lerp(1.0, 1.10, p),
                        lerp(0.0, 20.0, p),
                        lerp(0.0, 14.0, p),
                        lerp(1.0, 0.30, p)
                    );
                    dessinerLayerDansViewport(
                        gc, mondeCible, multivers, controleurAnimation, maintenantNs,
                        champX, champY, champW, champH,
                        clamp(0.2, 4.0, 0.88 + (0.12 * p)),
                        transition.getOffsetXLerp(),
                        transition.getOffsetYLerp(),
                        lerp(0.45, 1.0, p)
                    );
                }
            } else {
                // Hors transition: on dessine UNIQUEMENT le monde courant (pas de superposition).
                RecursiveSceneViewModel.Layer layerCourant = layers.get(layers.size() - 1);
                dessinerLayerDansViewport(
                    gc, layerCourant.plateau, multivers, controleurAnimation, maintenantNs,
                    champX, champY, champW, champH,
                    1.0,
                    0.0,
                    0.0,
                    1.0
                );
            }

            gc.restore();
        }

        int surCibles   = plateau.compterBoitesSurCibles();
        int totalCibles = plateau.compterCibles();
        double tailleFont = Math.max(14, Math.min(cadreW, cadreH) * 0.042);
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, tailleFont));
        gc.setTextAlign(TextAlignment.CENTER);
        double scoreY = cadreY + cadreH - (cadreH - anneauH) / 2.0 + tailleFont * 0.35;
        gc.fillText(surCibles + " / " + totalCibles, cadreX + cadreW / 2.0, scoreY);
        gc.setTextAlign(TextAlignment.LEFT);

        if (multivers.getProfondeur() > 0) {
            dessinerContexteNavigation(gc, W, H, multivers);
        }

        boolean gagne = multivers.estGagne();
        if (gagne) {
            gc.setFill(Color.web("#000000", 0.38));
            gc.fillRoundRect(cadreX, cadreY, cadreW, cadreH * 0.14, coinCadre, coinCadre);
            gc.setFill(Color.web("#f2d974"));
            gc.setFont(Font.font("SansSerif", FontWeight.BOLD, Math.max(14, cadreW * 0.038)));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Victoire !", cadreX + cadreW / 2.0, cadreY + cadreH * 0.10);
            gc.setTextAlign(TextAlignment.LEFT);
            feuArtifice.dessiner(gc, maintenantNs);
        }
    }

    private static void dessiner(
        GraphicsContext gc,
        Plateau plateau,
        Multivers multivers,
        ControleurAnimation controleurAnimation,
        FeuArtifice feuArtifice,
        long maintenantNs
    ) {
        double W = gc.getCanvas().getWidth();
        double H = gc.getCanvas().getHeight();

        gc.setFill(FOND_GENERAL);
        gc.fillRect(0, 0, W, H);

        double marge     = Math.min(W, H) * 0.06;
        double cadreX    = marge;
        double cadreY    = marge;
        double cadreW    = W - marge * 2;
        double cadreH    = H - marge * 2;
        double coinCadre = Math.max(16, Math.min(cadreW, cadreH) * 0.06);

        gc.setFill(Color.web("#000000", 0.18));
        gc.fillRoundRect(cadreX + 4, cadreY + 6, cadreW, cadreH, coinCadre, coinCadre);

        gc.setFill(CADRE_BLANC);
        gc.fillRoundRect(cadreX, cadreY, cadreW, cadreH, coinCadre, coinCadre);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(BORD_CADRE);
        gc.strokeRoundRect(cadreX + BORD_CADRE / 2, cadreY + BORD_CADRE / 2,
                           cadreW - BORD_CADRE, cadreH - BORD_CADRE, coinCadre, coinCadre);

        double pad        = BORD_CADRE + 8;
        double anneauX    = cadreX + pad;
        double anneauY    = cadreY + pad;
        double anneauW    = cadreW - pad * 2;
        double anneauH    = cadreH - pad * 2;
        double coinAnneau = Math.max(10, coinCadre * 0.7);

        gc.setFill(ANNEAU_SOMBRE);
        gc.fillRoundRect(anneauX, anneauY, anneauW, anneauH, coinAnneau, coinAnneau);

        double champX   = anneauX + EPAISSEUR_ANNEAU;
        double champY   = anneauY + EPAISSEUR_ANNEAU;
        double champW   = anneauW - EPAISSEUR_ANNEAU * 2;
        double champH   = anneauH - EPAISSEUR_ANNEAU * 2;
        double coinChamp = Math.max(6, coinAnneau * 0.5);

        gc.setFill(CHAMP_JEU);
        gc.fillRoundRect(champX, champY, champW, champH, coinChamp, coinChamp);

        int nLignes   = plateau.getGrille().size();
        int nColonnes = plateau.getGrille().isEmpty() ? 1 : plateau.getGrille().get(0).size();
        double tailleCase = Math.min(champW / nColonnes, champH / nLignes);
        double origineX   = champX + (champW - nColonnes * tailleCase) / 2.0;
        double origineY   = champY + (champH - nLignes * tailleCase) / 2.0;

        dessinerGrille(gc, plateau, multivers, controleurAnimation, maintenantNs,
                       nLignes, nColonnes, tailleCase, origineX, origineY);

        int surCibles   = plateau.compterBoitesSurCibles();
        int totalCibles = plateau.compterCibles();
        double tailleFont = Math.max(14, Math.min(cadreW, cadreH) * 0.042);
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, tailleFont));
        gc.setTextAlign(TextAlignment.CENTER);
        // Positionner le score dans la bande blanche en bas (sous l'anneau)
        double scoreY = cadreY + cadreH - (cadreH - anneauH) / 2.0 + tailleFont * 0.35;
        gc.fillText(surCibles + " / " + totalCibles, cadreX + cadreW / 2.0, scoreY);
        gc.setTextAlign(TextAlignment.LEFT);

        if (multivers != null && multivers.getProfondeur() > 0) {
            dessinerContexteNavigation(gc, W, H, multivers);
        }

        boolean gagne = (multivers != null) ? multivers.estGagne() : plateau.estGagne();
        if (gagne) {
            gc.setFill(Color.web("#000000", 0.38));
            gc.fillRoundRect(cadreX, cadreY, cadreW, cadreH * 0.14, coinCadre, coinCadre);
            gc.setFill(Color.web("#f2d974"));
            gc.setFont(Font.font("SansSerif", FontWeight.BOLD, Math.max(14, cadreW * 0.038)));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Victoire !", cadreX + cadreW / 2.0, cadreY + cadreH * 0.10);
            gc.setTextAlign(TextAlignment.LEFT);
            feuArtifice.dessiner(gc, maintenantNs);
        }
    }

    /**
     * Dessine en bas de l'écran les vignettes des mondes parents (fil d'Ariane).
     * Chaque vignette montre le plateau parent en miniature, avec la boîte active
     * mise en surbrillance par un halo coloré.
     */
    private static void dessinerContexteNavigation(GraphicsContext gc, double W, double H, Multivers multivers) {
        List<Multivers.ContexteNavigation> contextes = multivers.getContextesOrdonnes();
        int n = contextes.size();
        if (n == 0) return;

        double vigH     = Math.min(H * 0.18, 120);
        double vigW     = vigH * 1.2;
        double pad      = 8;
        double totalW   = n * (vigW + pad) - pad;
        double startX   = (W - totalW) / 2.0;
        double startY   = H - vigH - 6;

        // Fond semi-transparent derrière toutes les vignettes
        double bandX = startX - 8;
        double bandY = startY - 6;
        double bandW = totalW + 16;
        double bandH = vigH + 12;
        double bandCoin = 10;
        gc.setFill(Color.web("#000000", 0.55));
        gc.fillRoundRect(bandX, bandY, bandW, bandH, bandCoin, bandCoin);

        // Un char pour identifier le monde de chaque niveau de la pile :
        // contextes[i].mondePrecedent est le monde à afficher dans la vignette i
        for (int i = 0; i < n; i++) {
            Multivers.ContexteNavigation ctx = contextes.get(i);
            double vx = startX + i * (vigW + pad);
            double vy = startY;

            Plateau plateauParent = multivers.getPlateau(ctx.mondePrecedent);
            Position posBoite     = ctx.positionBoite;

            // Cadre de la vignette
            double bord = 2.0;
            gc.setFill(Color.web("#222222"));
            gc.fillRoundRect(vx, vy, vigW, vigH, 7, 7);
            gc.setStroke(Color.web("#cccccc", 0.7));
            gc.setLineWidth(bord);
            gc.strokeRoundRect(vx + bord / 2, vy + bord / 2, vigW - bord, vigH - bord, 7, 7);

            if (plateauParent == null) continue;

            // Mini-grille du monde parent
            double innerPad = 6;
            double innerX = vx + innerPad;
            double innerY = vy + innerPad;
            double innerW = vigW - innerPad * 2;
            double innerH = vigH - innerPad * 2;

            gc.setFill(CHAMP_JEU);
            gc.fillRect(innerX, innerY, innerW, innerH);

            gc.save();
            gc.beginPath();
            gc.rect(innerX, innerY, innerW, innerH);
            gc.clip();

            ArrayList<ArrayList<Case>> grille = plateauParent.getGrille();
            int nL = grille.size();
            int nC = grille.isEmpty() ? 1 : grille.get(0).size();
            double cellSize = Math.min(innerW / nC, innerH / nL);
            double gX = innerX + (innerW - nC * cellSize) / 2.0;
            double gY = innerY + (innerH - nL * cellSize) / 2.0;

            for (int row = 0; row < nL; row++) {
                for (int col = 0; col < nC; col++) {
                    double cx = gX + col * cellSize;
                    double cy = gY + row * cellSize;
                    Case c = grille.get(row).get(col);

                    if (c instanceof CaseMur) {
                        gc.setFill(Color.web("#7a746e"));
                        gc.fillRect(cx, cy, cellSize, cellSize);
                    } else if (c instanceof CaseCible) {
                        double d = cellSize * 0.35;
                        gc.setFill(Color.web("#e8d44d", 0.85));
                        gc.fillOval(cx + d / 2, cy + d / 2, cellSize - d, cellSize - d);
                    } else if (c instanceof CaseBoiteMonde bm) {
                        // La boîte active = celle dans laquelle on est entré
                        boolean active = posBoite != null
                            && posBoite.getx() == col && posBoite.gety() == row;
                        double p = cellSize * 0.1;
                        gc.setFill(bm.getCouleur());
                        gc.fillRoundRect(cx + p, cy + p, cellSize - p * 2, cellSize - p * 2,
                                         cellSize * 0.15, cellSize * 0.15);
                        if (active) {
                            // Halo lumineux autour de la boîte active
                            gc.setStroke(Color.WHITE);
                            gc.setLineWidth(Math.max(1.5, cellSize * 0.12));
                            gc.strokeRoundRect(cx + p, cy + p, cellSize - p * 2, cellSize - p * 2,
                                               cellSize * 0.15, cellSize * 0.15);
                        }
                    } else if (c instanceof CaseBoite boite) {
                        gc.setFill(boite.estSurCible() ? Color.web("#a8e06a") : Color.web("#e87c2a"));
                        double p = cellSize * 0.1;
                        gc.fillRoundRect(cx + p, cy + p, cellSize - p * 2, cellSize - p * 2,
                                         cellSize * 0.12, cellSize * 0.12);
                    } else if (c instanceof Personnage) {
                        gc.setFill(Color.web("#6ab0e8"));
                        double r = cellSize * 0.35;
                        gc.fillOval(cx + cellSize / 2 - r, cy + cellSize / 2 - r, r * 2, r * 2);
                    }
                }
            }
            gc.restore();

            // Lettre du monde parent en bas de la vignette
            double fontSize = Math.max(7, vigH * 0.14);
            gc.setFill(Color.web("#dddddd"));
            gc.setFont(Font.font("SansSerif", FontWeight.BOLD, fontSize));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(ctx.mondePrecedent), vx + vigW / 2, vy + vigH - 2);
            gc.setTextAlign(TextAlignment.LEFT);
        }
    }

    private static void dessinerGrille(
        GraphicsContext gc,
        Plateau plateau,
        Multivers multivers,
        ControleurAnimation controleurAnimation,
        long maintenantNs,
        int nLignes, int nColonnes,
        double tailleCase,
        double origineX, double origineY
    ) {
        boolean gagne = plateau.estGagne();

        for (int i = 0; i < nLignes; i++) {
            for (int j = 0; j < nColonnes; j++) {
                double x = origineX + j * tailleCase;
                double y = origineY + i * tailleCase;
                Case element = plateau.getGrille().get(i).get(j);

                if (element instanceof CaseVide) {
                    continue;
                }
                if (element instanceof CaseMur) {
                    RenduMur.dessiner(gc, x, y, tailleCase);
                    continue;
                }

                boolean surCible = (element instanceof CaseCible)
                    || (element instanceof CaseBoite && ((CaseBoite) element).estSurCible())
                    || (element instanceof Personnage && ((Personnage) element).estSurCible());

                if (element instanceof CaseCible || surCible) {
                    RenduCible.dessiner(gc, x, y, tailleCase, maintenantNs, gagne);
                }

                if (element instanceof CaseBoiteMonde bm) {
                    Plateau mondePlateau = (multivers != null) ? multivers.getPlateau(bm.getIdentifiantMonde()) : null;
                    dessinerBoiteMonde(gc, x, y, tailleCase, bm, mondePlateau);
                    continue;
                }

                if (element instanceof CaseBoite boite) {
                    RenduBoite.dessiner(gc, x, y, tailleCase,
                        boite.estSurCible(), boite.getSurCibleDepuisNs(), controleurAnimation, maintenantNs);
                    continue;
                }

                if (element instanceof Personnage personnage) {
                    RenduPersonnage.dessiner(gc, x, y, tailleCase,
                        personnage.estSurCible(), controleurAnimation, maintenantNs);
                }
            }
        }
    }

    /**
     * Dessine une boîte-monde : cadre coloré + miniature du plateau contenu.
     */
    private static void dessinerBoiteMonde(GraphicsContext gc, double x, double y, double taille,
                                           CaseBoiteMonde bm, Plateau mondePlateau) {
        Color couleur = bm.getCouleur();
        double bord   = Math.max(2.5, taille * 0.07);
        double coin   = Math.max(5.0, taille * 0.15);
        double h2     = bord / 2.0;

        // Fond coloré de la boîte
        Color fondBoite = bm.estSurCible()
            ? couleur.interpolate(Color.WHITE, 0.35)
            : couleur.deriveColor(0, 1, 0.55, 1);
        gc.setFill(fondBoite);
        gc.fillRoundRect(x + h2, y + h2, taille - bord, taille - bord, coin, coin);

        // Contenu miniaturisé
        double innerPad = bord + Math.max(1.5, taille * 0.04);
        double innerX = x + innerPad;
        double innerY = y + innerPad;
        double innerW = taille - innerPad * 2;
        double innerH = taille - innerPad * 2;

        if (mondePlateau != null && innerW > 4 && innerH > 4) {
            // Fond sombre du mini-monde
            gc.setFill(CHAMP_JEU);
            gc.fillRect(innerX, innerY, innerW, innerH);

            gc.save();
            gc.beginPath();
            gc.rect(innerX, innerY, innerW, innerH);
            gc.clip();

            ArrayList<ArrayList<Case>> grille = mondePlateau.getGrille();
            int nL = grille.size();
            int nC = grille.isEmpty() ? 1 : grille.get(0).size();
            double cellSize = Math.min(innerW / nC, innerH / nL);
            double gX = innerX + (innerW - nC * cellSize) / 2.0;
            double gY = innerY + (innerH - nL * cellSize) / 2.0;

            for (int i = 0; i < nL; i++) {
                for (int j = 0; j < nC; j++) {
                    double cx = gX + j * cellSize;
                    double cy = gY + i * cellSize;
                    Case c = grille.get(i).get(j);

                    if (c instanceof CaseMur) {
                        gc.setFill(Color.web("#7a746e"));
                        gc.fillRect(cx, cy, cellSize, cellSize);
                    } else if (c instanceof CaseCible) {
                        double d = cellSize * 0.35;
                        gc.setFill(Color.web("#e8d44d", 0.85));
                        gc.fillOval(cx + d / 2, cy + d / 2, cellSize - d, cellSize - d);
                    } else if (c instanceof CaseBoiteMonde bm2) {
                        double p = cellSize * 0.1;
                        gc.setFill(bm2.getCouleur());
                        gc.fillRoundRect(cx + p, cy + p, cellSize - p * 2, cellSize - p * 2,
                                         cellSize * 0.15, cellSize * 0.15);
                    } else if (c instanceof CaseBoite boite) {
                        if (boite.estSurCible()) {
                            double d = cellSize * 0.35;
                            gc.setFill(Color.web("#e8d44d", 0.5));
                            gc.fillOval(cx + d / 2, cy + d / 2, cellSize - d, cellSize - d);
                        }
                        gc.setFill(boite.estSurCible() ? Color.web("#a8e06a") : Color.web("#e87c2a"));
                        double p = cellSize * 0.1;
                        gc.fillRoundRect(cx + p, cy + p, cellSize - p * 2, cellSize - p * 2,
                                         cellSize * 0.12, cellSize * 0.12);
                    } else if (c instanceof Personnage perso) {
                        if (perso.estSurCible()) {
                            double d = cellSize * 0.35;
                            gc.setFill(Color.web("#e8d44d", 0.5));
                            gc.fillOval(cx + d / 2, cy + d / 2, cellSize - d, cellSize - d);
                        }
                        gc.setFill(Color.web("#6ab0e8"));
                        double r = cellSize * 0.35;
                        gc.fillOval(cx + cellSize / 2 - r, cy + cellSize / 2 - r, r * 2, r * 2);
                    }
                }
            }
            gc.restore();
        } else {
            // Pas de plateau disponible : lettre-identifiant centrée
            double fontSize = Math.max(8, taille * 0.38);
            gc.setFill(couleur.brighter());
            gc.setFont(Font.font("SansSerif", FontWeight.BOLD, fontSize));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(bm.getIdentifiantMonde()), x + taille / 2, y + taille / 2 + fontSize * 0.35);
            gc.setTextAlign(TextAlignment.LEFT);
        }

        // Contour coloré
        gc.setStroke(couleur.brighter());
        gc.setLineWidth(bord);
        gc.strokeRoundRect(x + h2, y + h2, taille - bord, taille - bord, coin, coin);
    }

    private static double clamp(double min, double max, double value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static void dessinerLayerDansViewport(
        GraphicsContext gc,
        Plateau layerPlateau,
        Multivers multivers,
        ControleurAnimation controleurAnimation,
        long maintenantNs,
        double champX,
        double champY,
        double champW,
        double champH,
        double scale,
        double offsetX,
        double offsetY,
        double alpha
    ) {
        if (layerPlateau == null) return;

        double centreX = champX + champW / 2.0;
        double centreY = champY + champH / 2.0;

        gc.save();
        gc.setGlobalAlpha(clamp(0.0, 1.0, alpha));
        gc.translate(centreX + offsetX, centreY + offsetY);
        gc.scale(clamp(0.2, 4.0, scale), clamp(0.2, 4.0, scale));
        gc.translate(-centreX, -centreY);

        int nLignes = layerPlateau.getGrille().size();
        int nColonnes = layerPlateau.getGrille().isEmpty() ? 1 : layerPlateau.getGrille().get(0).size();
        double tailleCase = Math.min(champW / nColonnes, champH / nLignes);
        double origineX = champX + (champW - nColonnes * tailleCase) / 2.0;
        double origineY = champY + (champH - nLignes * tailleCase) / 2.0;

        dessinerGrille(gc, layerPlateau, multivers, controleurAnimation, maintenantNs,
                       nLignes, nColonnes, tailleCase, origineX, origineY);

        gc.restore();
    }
}
