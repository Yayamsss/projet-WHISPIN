public class MenuManager extends GameObjectEx
{
    private static MenuManager sInstance = null;

    private MenuStart mMenuStart;
    private MenuMain mMenuMain;
    private MenuCredit mMenuCredit;
    private MenuLevel mMenuLevel;
    private MenuGame mMenuGame;
    private MenuPause mMenuPause;
    private MenuWin mMenuWin;
    private MenuLoose mMenuLoose;
    private MenuEdit mMenuEdit;

    private GraphicMenuBase mCurrentMenu;
    private SceneType mCurrentScene;

    @Override public void onCreate() 
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();
        }

        sInstance = this;
        
        mMenuStart = GameLoop.getInstance().addObject(MenuStart.class);
        mMenuMain = GameLoop.getInstance().addObject(MenuMain.class);
        mMenuCredit = GameLoop.getInstance().addObject(MenuCredit.class);
        mMenuLevel = GameLoop.getInstance().addObject(MenuLevel.class);
        mMenuGame = GameLoop.getInstance().addObject(MenuGame.class);
        mMenuPause = GameLoop.getInstance().addObject(MenuPause.class);
        mMenuWin = GameLoop.getInstance().addObject(MenuWin.class);
        mMenuLoose = GameLoop.getInstance().addObject(MenuLoose.class);
        mMenuEdit= GameLoop.getInstance().addObject(MenuEdit.class);

        mMenuStart.setEnabled(false);
        mMenuMain.setEnabled(false);
        mMenuCredit.setEnabled(false);
        mMenuLevel.setEnabled(false);
        mMenuGame.setEnabled(false);
        mMenuPause.setEnabled(false);
        mMenuWin.setEnabled(false);
        mMenuLoose.setEnabled(false);
        mMenuEdit.setEnabled(false);

        this.setScene(SceneType.Start);
    }

    public static MenuManager getInstance()
    {
        return sInstance;
    } 

    public SceneType getCurrentScene()
    {
        return mCurrentScene;
    }

    public void setScene(SceneType newScene)
    {
        if (mCurrentMenu != null)
        {
            mCurrentMenu.setEnabled(false);
            mCurrentMenu.setHidden(true);
        }

        mCurrentScene = newScene;
        mCurrentMenu = getMenuFromScene(newScene);
        mCurrentMenu.setEnabled(true);
        mCurrentMenu.setHidden(false);
        
        GraphicsModule.getInstance().setClearColor(mCurrentMenu.getBackGround().getColor());
    }

    private GraphicMenuBase getMenuFromScene(SceneType scene)
    {
        switch (scene)
        {
            case SceneType.Start: return mMenuStart;
            case SceneType.Menu: return mMenuMain;
            case SceneType.Credit: return mMenuCredit;
            case SceneType.Level: return mMenuLevel;
            case SceneType.Game: return mMenuGame;
            case SceneType.PauseMenu: return mMenuPause;
            case SceneType.Win: return mMenuWin;
            case SceneType.Loose: return mMenuLoose;
            case SceneType.Edit: return mMenuEdit;

            default: return null;
        }
    }

    @Override protected void receiveMsg(GameObject from, int msg) 
    {
        setScene(SceneType.values()[msg]);
    }
}