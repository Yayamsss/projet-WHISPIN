/*
    Auteurs: Ryane Menaï
*/

/* Class permetant de gérer facilement les boîtes du jeu. */

public class BoxManager extends GameObject
{
    private BoxRecursive mRootBox;

    @Override public void onCreate()
    {
        mRootBox = null;
    }

    @Override public void onDestroy()
    {
        if (mRootBox != null)
            mRootBox.destroy();
    }

    public BoxRecursive getRootBox()
    {
        return mRootBox;
    }

    /* Initialise une boîte racine avec des attribut par défault */
    public void init()
    {
        if (mRootBox != null)
            mRootBox.destroy();

        mRootBox = GameLoop.getInstance().addObject(BoxRecursive.class);
        mRootBox.setPosition(new Vector2(GraphicsModule.getWitdh() / 2, GraphicsModule.getHeight() / 2));
        mRootBox.setSideSize(2);
    }

    /* Fonction d'initialisation pour débogage */
    public boolean initDebug()
    {
        if (mRootBox != null)
        {
            mRootBox.destroy();
        }

        mRootBox = GameLoop.getInstance().addObject(BoxRecursive.class);
        mRootBox.setSideSize(8);
        mRootBox.setPosition(new Vector2(
            1920.0f / 2.0f,
            1080.0f / 2.0f));

        for (int i = 0; i < mRootBox.getSideSize(); i++)
        {
            for (int j = 0; j < mRootBox.getSideSize(); j++)
            {
                if (i == 0 || i == mRootBox.getSideSize() - 1
                 || j == 0 || j == mRootBox.getSideSize() - 1)
                {
                    mRootBox.addBox(BoxStatic.class, i, j);
                }
            }
        }

        mRootBox.addBox(BoxRegular.class, 2, 1);
        mRootBox.addBox(BoxRegular.class, 2, 2);
        mRootBox.addBox(BoxRegular.class, 3, 2);
        mRootBox.addBox(BoxRegular.class, 3, 3);
        mRootBox.addBox(BoxRegular.class, 3, 4);
        mRootBox.addBox(BoxRegular.class, 3, 5);
        mRootBox.addBox(BoxRegular.class, 4, 3);
        mRootBox.addBox(BoxRegular.class, 5, 5);
        mRootBox.addBox(BoxRegular.class, 6, 5);

        BoxRecursive testRec = GameLoop.getInstance().addObject(BoxRecursive.class);
        mRootBox.setBox(testRec, 6, 4);
        testRec.setSideSize(5);
        testRec.addBox(BoxStatic.class, 0, 0);
        testRec.addBox(BoxStatic.class, 1, 0);
        testRec.addBox(BoxStatic.class, 0, 1);
        testRec.addBox(BoxStatic.class, 0, 3);
        testRec.addBox(BoxStatic.class, 0, 4);

        BoxRecursive testRec1 = GameLoop.getInstance().addObject(BoxRecursive.class);
        testRec.setBox(testRec1, 4, 3);
        testRec1.setSideSize(4);
        testRec1.addBox(BoxStatic.class, 0, 0);
        testRec1.addBox(BoxStatic.class, 1, 0);
        testRec1.addBox(BoxStatic.class, 0, 1);

        BoxRecursive testRec2 = GameLoop.getInstance().addObject(BoxRecursive.class);
        testRec1.setBox(testRec2, 1, 1);
        testRec2.setSideSize(3);
        testRec2.addBox(BoxStatic.class, 1, 1);

        GameManager.getInstance().addNewGoal(testRec, 3, 1);
        GameManager.getInstance().addNewGoal(testRec1, 3, 1);

        GameManager.getInstance().getGoal(0).setGoalType(BoxGoal.GoalType.Box);
        GameManager.getInstance().getGoal(1).setGoalType(BoxGoal.GoalType.Player);

        GameLoop.getInstance().addObject(BoxSpawn.class).setCell(mRootBox, 1, 1);

        return true;
    }
}