/*
    Auteur : AIT MEDDOUR Sami
*/

import javafx.scene.paint.Color;
/*
    Vue graphique de l'éditeur de niveau.
    S'intègre dans la fenêtre du jeu existante.
    - Affiche la grille du niveau
    - Affiche une barre d'outils en bas (mur, caisse, joueur, objectif, gomme)
    - Gère les clics souris pour placer/effacer des cases
*/
public class EditorView extends GameObjectEx
{
    private LevelEditor mEditor;

    // Position et taille de la grille dans l'espace UI (1920x1080)
    private static final float sGridOriginX = 160f;
    private static final float sGridOriginY = 130f;
    private static final float sGridWidth   = 800f;
    private static final float sGridHeight  = 800f;

    // Paramètres de la barre d'outils (5 boutons : mur, caisse, joueur, objectif, gomme)
    private static final int   sNumTools            = 5;
    private static final float sToolBarY            = 990f;
    private static final float sToolBarBouttonWidth  = 120f;
    private static final float sToolBarBouttonHeight = 80f;
    private static final float sToolBarGap           = 20f;
    private static final int   sLayer               = 30;

    private GraphicButton[] mToolButtons;
    private Tool[]          mTools;

    // Rectangles graphiques représentant le fond et chaque cellule de la grille
    private GraphicRectangle   mGridBg;
    private GameObject         mGridBgObj;
    private GraphicRectangle[] mCellShapes;
    private GameObject[]       mCellObjects;

    // État actif : la vue tourne en boucle et traite les clics + le rendu
    private static final GameObjectState<EditorView> sActive =
        new GameObjectState<EditorView>() {
            @Override public void execute(EditorView obj) { obj.exeActive(); }
        };

    // Initialise l'éditeur avec une grille 8x8, construit l'interface, charge le niveau sauvegardé si disponible
    @Override public void onCreate()
    {
        mEditor = new LevelEditor(8);
        buildGrid();
        buildToolbar();
        setState(sActive);

        loadCustomLevel();
    }

    // Libère les rectangles graphiques et les boutons à la destruction de la vue
    @Override public void onDestroy()
    {
        if (mGridBg != null)
            mGridBg.destroy();

        if (mCellShapes != null)
            for (GraphicRectangle r : mCellShapes) if (r != null) r.destroy();

        if (mToolButtons != null)
            for (GraphicButton b : mToolButtons) if (b != null) b.destroy();
    }

    // Appelée chaque frame : gère les clics sur la grille, met à jour les couleurs et surligne l'outil actif
    private void exeActive()
    {
        handleGridClick();
        refreshCells();
        highlightSelectedTool();
    }

    // Crée le fond de la grille et un rectangle graphique pour chaque cellule
    private void buildGrid()
    {
        int size = mEditor.getLevel().getSideSize();

        // Rectangle de fond légèrement plus grand que la grille pour faire une bordure visible
        mGridBgObj = new GameObject();
        mGridBgObj.setPosition(new Vector2(
            sGridOriginX + sGridWidth / 2f,
            sGridOriginY + sGridHeight / 2f));
        mGridBg = new GraphicRectangle();
        mGridBg.init(sGridWidth + 4, sGridHeight + 4, mGridBgObj,
                     Color.color(0.2, 0.2, 0.2), sLayer, 4f);

        int total = size * size; // nombre total de cellules dans la grille
        mCellShapes  = new GraphicRectangle[total];
        mCellObjects = new GameObject[total];

        float cellW = sGridWidth / size;  // largeur d'une cellule en pixels UI
        float cellH = sGridHeight / size; // hauteur d'une cellule en pixels UI
        float pad   = 2f; // espace entre les cellules pour les séparer visuellement

        for (int i = 0; i < size; i++) // i = ligne
        {
            for (int j = 0; j < size; j++) // j = colonne
            {
                int idx = i * size + j; // index dans le tableau 1D qui correspond à la case (i, j)

                // Position du centre de la cellule en coordonnées UI
                float cx = sGridOriginX + j * cellW + cellW / 2f;
                float cy = sGridOriginY + i * cellH + cellH / 2f;

                mCellShapes[idx] = GraphicHelper.createRectangleObj(
                    cellW - pad,
                    cellH - pad,
                    0.0f,
                    colorForChar(' '),
                    sLayer);
                mCellObjects[idx] = mCellShapes[idx].getGameObject();
                mCellObjects[idx].setPosition(new Vector2(cx, cy));
            }
        }
    }

    // Crée les 5 boutons de la barre d'outils, centrés horizontalement en bas de l'écran
    private void buildToolbar()
    {
        mTools = new Tool[]{ Tool.WALL, Tool.BOX, Tool.PLAYER, Tool.GOAL, Tool.ERASE };
        mToolButtons = new GraphicButton[sNumTools];

        // Largeur totale occupée par tous les boutons + les espaces entre eux
        float totalW = sNumTools * sToolBarBouttonWidth + (sNumTools - 1) * sToolBarGap;
        // X du centre du premier bouton pour que la barre soit centrée sur 960 (milieu de 1920)
        float startX = 960f - totalW / 2f + sToolBarBouttonWidth / 2f;

        for (int k = 0; k < sNumTools; k++)
        {
            int toolIdx = k; // copie locale nécessaire pour la capture dans le lambda
            // X du centre de ce bouton : on décale de (largeur + gap) à chaque itération
            float cx = startX + k * (sToolBarBouttonWidth + sToolBarGap);

            GraphicButton btn = GameLoop.getInstance().addObject(GraphicButton.class);
            btn.setPosition(new Vector2(cx, sToolBarY));
            btn.setWidth(sToolBarBouttonWidth);
            btn.setHeight(sToolBarBouttonHeight);
            btn.setText(labelForTool(mTools[k]));
            btn.getGraphicText().setColor(Color.WHITE);

            // Le clic change l'outil actif dans l'éditeur
            btn.setCallBack(() -> { mEditor.setSelectedTool(mTools[toolIdx].getChar()); });

            mToolButtons[k] = btn;
        }
    }

    // Détecte un clic de souris dans la grille et applique l'outil sélectionné sur la cellule visée
    private void handleGridClick()
    {
        GraphicsModule gfx = GameLoop.getInstance().getGraphicsModule();

        if (!gfx.isMousePressed())
            return;

        Vector2 mouse = gfx.getMousePosition();
        float mx = mouse.x;
        float my = mouse.y;

        int size  = mEditor.getLevel().getSideSize();
        float cellW = sGridWidth / size;  // largeur d'une cellule
        float cellH = sGridHeight / size; // hauteur d'une cellule

        // Ignore le clic s'il est en dehors de la grille
        if (mx < sGridOriginX || mx > sGridOriginX + sGridWidth) return;
        if (my < sGridOriginY || my > sGridOriginY + sGridHeight) return;

        // Convertit la position souris en indice de ligne/colonne dans la grille
        int j = (int) ((mx - sGridOriginX) / cellW);
        int i = (int) ((my - sGridOriginY) / cellH);

        mEditor.applyTool(i, j);
    }

    // Met à jour la couleur de chaque cellule selon son contenu dans le niveau
    private void refreshCells()
    {
        Level level = mEditor.getLevel();
        int size = level.getSideSize();

        for (int i = 0; i < size; i++)
        {
            for (int j = 0; j < size; j++)
            {
                int idx = i * size + j;
                char c = level.getCase(i, j);
                mCellShapes[idx].setColor(colorForChar(c));
            }
        }
    }

    // Met le bouton de l'outil actif en pleine opacité, les autres en semi-transparent
    private void highlightSelectedTool()
    {
        char selected = mEditor.getSelectedTool();
        for (int k = 0; k < sNumTools; k++)
        {
            boolean active = mTools[k].getChar() == selected;
            Color base = colorForTool(mTools[k]);
            mToolButtons[k].getShape().setColor(
                active ? base : Color.color(base.getRed(), base.getGreen(), base.getBlue(), 0.45));
        }
    }

    // Retourne la couleur associée à un caractère de niveau (mur, caisse, joueur, objectif, vide)
    private static Color colorForChar(char c)
    {
        switch (c)
        {
            case '#': return Color.color(0.55, 0.55, 0.60); // mur
            case '$': return Color.color(0.85, 0.65, 0.10); // caisse
            case '@': return Color.color(0.20, 0.60, 0.90); // joueur
            case '.': return Color.color(0.20, 0.80, 0.35); // objectif
            default:  return Color.color(0.12, 0.12, 0.14); // vide
        }
    }

    // Récupère la couleur d'un outil en passant par son caractère associé
    private static Color colorForTool(Tool t)
    {
        return colorForChar(t.getChar() == ' ' ? ' ' : t.getChar());
    }

    // Retourne le label français affiché sur le bouton d'un outil
    private static String labelForTool(Tool t)
    {
        switch (t)
        {
            case WALL:   return "MUR";
            case BOX:    return "CAISSE";
            case PLAYER: return "JOUEUR";
            case GOAL:   return "OBJECTIF";
            case ERASE:  return "GOMME";
            default:     return "?";
        }
    }

    // Sauvegarde le niveau courant dans le fichier de niveau personnalisé
    public boolean saveCustomLevel()
    {
        return mEditor.save(GameManager.getCustomLevelPath());
    }

    // Charge un niveau depuis le fichier de niveau personnalisé, si il existe
    public boolean loadCustomLevel()
    {
        return mEditor.load(GameManager.getCustomLevelPath());
    }

    // Indique si le niveau actuel est jouable (un joueur, au moins une caisse et un objectif)
    public boolean canPlayCustomLevel()
    {
        return mEditor.canBePlayed();
    }

    // Retourne le message d'erreur si le niveau n'est pas jouable
    public String getPlayabilityError()
    {
        return mEditor.getPlayabilityError();
    }

    public LevelEditor getEditor()
    {
        return mEditor;
    }
}
