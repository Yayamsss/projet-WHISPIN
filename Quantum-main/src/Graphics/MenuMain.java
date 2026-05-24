public class MenuMain extends GraphicMenuBase 
{
    private static MenuMain sInstance = null;

    /* Attribut pour le titre */
    private static final String sTitle = "LUMIX";
    private static final float sTitleOpacityIncreaseSpeed = 0.02f;
    /* Attribut pour les bouttons */
    private static final String sPlayButtonText = "JOUER";
    private static final Vector2 sPlayButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2, 
        500
    );
    private static final String sCreditButtonText = "CREDITS";
    private static final Vector2 sCreditButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2, 
        575
    );
    private static final String sQuitApplicationButtonText = "QUITTER";
    private static final Vector2 sQuitButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2, 
        650
    );

    private static final GameObjectState<MenuMain> sStartAnimation = new GameObjectState<MenuMain>() {
        public void execute(MenuMain obj) { obj.exeStartAnimation(); }
    };
    
    public static MenuMain getInstance()
    {
        return sInstance;
    }

    @Override public void onCreate() 
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();
        }

        sInstance = this;

        super.onCreate();
        this.getTitleText().setText(sTitle);
    }

    @Override public void onDestroy()
    {
        if (this == sInstance)
        {
            sInstance = null;
        }
    }

    @Override protected void receiveMsg(GameObject from, int msg) 
    {
        if (from.getClass().getName().equals("MenuCredit") 
         && msg == SceneType.Menu.ordinal())
        {
            this.setState(sStartAnimation);
        }
        else if (from.getClass().getName().equals("MenuLevel")
         && msg == SceneType.Menu.ordinal())
        {
            setState(sStartAnimation);
        }
    }

    @Override protected void initScene() 
    {
        mScene = SceneType.Menu;
    }

    @Override protected void initBackground()
    {
        mBackGround = MenuStart.getInstance().getBackGround();
    }
    
    @Override protected void initButton() 
    {
        mButtonList.addLast(
            GraphicHelper.createButton(
                sPlayButtonText,
                sPlayButtonPosition.x,
                sPlayButtonPosition.y
            )
        );
        mButtonList.getLast().setCallBack(() -> onPlayClick());

        mButtonList.addLast(
            GraphicHelper.createButton(
                sCreditButtonText,
                sCreditButtonPosition.x,
                sCreditButtonPosition.y
            )
        );
        mButtonList.getLast().setCallBack(() -> onCreditClick());

        mButtonList.addLast(
            GraphicHelper.createButton(
                sQuitApplicationButtonText,
                sQuitButtonPosition.x,
                sQuitButtonPosition.y
            )
        );
        mButtonList.getLast().setCallBack(() -> onQuitApplicationClick());
    }

    public void setOpacityScene(float opacity)
    {
        this.getTitleText().setOpacity(opacity);
        for (GraphicButton button : mButtonList)
        {
            button.getShape().setOpacity(opacity);
            button.getGraphicText().setOpacity(opacity);
            button.getTopLine().setOpacity(opacity);
            button.getBottomLine().setOpacity(opacity);
        }
    }

    private void onPlayClick()
    {
        sendMsg(MenuManager.getInstance(), SceneType.Level.ordinal());
        sendMsg(MenuLevel.getInstance(), 1);
    }

    private void onCreditClick()
    {
        sendMsg(MenuManager.getInstance(), SceneType.Credit.ordinal());
        sendMsg(MenuCredit.getInstance(), SceneType.Credit.ordinal());
        //setState(sStartAnimation);
    }

    private void onQuitApplicationClick()
    {
        GameLoop.getInstance().quit();
    }

    private void exeStartAnimation()
    {
        if (isFirstStep())
        {
            this.setOpacityScene(0);
            // this.disableButtons();
        }

        if (increaseOpacity() && GraphicHelper.addOpacityWithClamp(mTitle.getText(), sTitleOpacityIncreaseSpeed))
        {
            // this.enabledButtons();
            setState(sWait);
        }
    }
}