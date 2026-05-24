import javafx.scene.paint.Color;

public abstract class MenuEndGame extends GraphicMenuBase
{
    protected GraphicText mGameMessage;

    protected final static GameObjectState<MenuEndGame> sEndGame = new GameObjectState<MenuEndGame>() {
        public void execute(MenuEndGame obj) { obj.exeFadeAnimation(); }
    };

    /* Attribut pour les boutons */
    private static final String sRestartButtonText = "REJOUER";
    private static final Vector2 sRestartButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2.0f - 215, 
        GraphicsModule.getHeight() / 1.8f
    );
    private static final String sNextButtonText = "SUIVANT";
    private static final Vector2 sNextButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2.0f + 215, 
        GraphicsModule.getHeight() / 1.8f
    );
    /* Attribut pour le titre */
    protected static final String sWinTitle = "VICTOIRE";
    protected static final String sLooseTitle = "DÉFAITE";
    private static final Vector2 sTitlePosition = new Vector2(
        GraphicsModule.getWitdh() / 2,
        GraphicsModule.getHeight() / 3.5f
    );
    /* Attribut pour le message */
    protected static final Vector2 sPositionMessage = new Vector2(
        GraphicsModule.getWitdh() / 2,
        GraphicsModule.getHeight() / 2
    );
    protected static final Color sMessageColor = GraphicHelper.makeColorFromHex(0xe0c0ff);
    protected static final int sMessageLayer = 40;
    protected static final float sMessageSize = 30;
    protected static final float sMessageOpacityIncreaseSpeed = 0.05f;

    @Override public void onCreate() 
    {
        GameObject gameMessageObj = GameLoop.getInstance().addObject(GameObject.class);

        mGameMessage = gameMessageObj.setGraphicShape(GraphicText.class);
        mGameMessage.init(
            "", 
            gameMessageObj, 
            sMessageColor, 
            sMessageLayer, 
            sMessageSize
        );
        gameMessageObj.setPosition(sPositionMessage);
        
        super.onCreate();

        mTitle.setPosition(sTitlePosition);

        if (mScene == SceneType.Win)
        {
            mTitle.startBobbing();
        }
        else
        {
            mTitle.startShaking();
        }
    }

    @Override protected void initButton() 
    {
        mButtonList.addLast(
            GraphicHelper.createButton(
                sRestartButtonText, 
                sRestartButtonPosition.x,
                sRestartButtonPosition.y 
            )
        );
        mButtonList.getLast().setCallBack(() -> onRestartClick());

        mButtonList.addLast(
            GraphicHelper.createButton(
                sNextButtonText, 
                sNextButtonPosition.x, 
                sNextButtonPosition.y
            )
        );
        mButtonList.getLast().setCallBack(() -> onNextClick());
    }

    private void onRestartClick()
    {
        GameManager.getInstance().restartGame();

        sendMsg(MenuManager.getInstance(), SceneType.Game.ordinal());

        setState(sWait);
    }

    private void onNextClick()
    {
        sendMsg(
            MenuManager.getInstance(),
            GameManager.getInstance().isCustomLevel() ? SceneType.Edit.ordinal() : SceneType.Level.ordinal());
        setState(sWait);
    }

    @Override protected void receiveMsg(GameObject from, int msg) 
    {
        if (from.getClass().getName().equals("GameManager"))
        {   
            if (msg == GameManager.MessageType.PlayerWin.ordinal())
            {
                sendMsg(MenuManager.getInstance(), SceneType.Win.ordinal());
                mGameMessage.setText("Temps :     " 
                    + MenuGame.getInstance().getTimer().getText());
            } 
            else if (msg == GameManager.MessageType.PlayerDefeat.ordinal())
            {
                sendMsg(MenuManager.getInstance(), SceneType.Loose.ordinal());
            }
        }
    }

    @Override protected void onEnabled()
    {
        super.onEnabled();
        
        mGameMessage.setOpacity(0);
        for (GraphicButton button : mButtonList)
        {
            button.setOpacity(0);
        }

        mGameMessage.setHiden(false);
        setState(sEndGame);
    }

    @Override protected void onDisabled()
    {
        super.onDisabled();
        mGameMessage.setHiden(true);
        setState(sWait);
    }

    protected void exeFadeAnimation()
    {
        if (isFirstStep())
        {
            mGameMessage.setOpacity(0);
            for (GraphicButton button : mButtonList)
            {
                button.setOpacity(0.0f);
            }
        }

        if (GraphicHelper.addOpacityWithClamp(mGameMessage, sMessageOpacityIncreaseSpeed) && increaseOpacity())
        {
            setState(sWait);
        }
    }
}