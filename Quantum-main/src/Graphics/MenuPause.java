import javafx.scene.paint.Color;

public class MenuPause extends GraphicMenuBase 
{
    private static MenuPause sInstance = null;

    /* Attribut pour le titre */
    private static final String sTitle = "MENU";
    private static final Vector2 sTitlePosition = new Vector2(
        GraphicsModule.getWitdh() / 2,
        200
    );
    /* Attribut pour les boutons */
    private static final String sContinueButtonText = "CONTINUER";
    private static final Vector2 sContinueButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2.0f,
        GraphicsModule.getHeight() / 2.0f + 100
    );
    private static final String sSaveButtonText = "SAUVEGARDER";
    private static final Vector2 sSaveButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2.0f,
        GraphicsModule.getHeight() / 2.0f + 200
    );
    private static final String sQuitGameButtonText = "MENU PRINCIPAL";
    private static final String sQuitCustomGameButtonText = "MENU D'EDITION";
    private static final Vector2 sQuitApplicationButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 2.0f,
        GraphicsModule.getHeight() / 2.0f + 300
    );
    
    private static final float sWidth = GraphicsModule.getWitdh();
    private static final float sHeight = GraphicsModule.getHeight();
    private static final Color sColor = GraphicHelper.makeColorWithOpacity(Color.BLACK, 0.7f);
    private static final int sLayer = 30;

    private GraphicRectangle mShape;

    public static MenuPause getInstance()
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
        
        this.setPosition(GraphicsModule.getScreenCenter());

        mShape = this.setGraphicShape(GraphicRectangle.class);
        mShape.init(
            sWidth,
            sHeight,
            this,
            sColor,
            sLayer,
            0);

        super.onCreate();

        mTitle.setText(sTitle);
        mTitle.setPosition(sTitlePosition);
    }

    @Override protected void onEnabled() 
    {
        super.onEnabled();
        mShape.setHiden(false);

        GameManager.getInstance().pauseGame();
        mButtonList.getLast().setText(
            GameManager.getInstance().isCustomLevel() ? sQuitCustomGameButtonText : sQuitGameButtonText);
    }

    @Override protected void onDisabled() 
    {
        super.onDisabled();
        mShape.setHiden(true);

        if (GameManager.getInstance().isGamePaused())
        {
            GameManager.getInstance().resumeGame();
        }
    }

    @Override protected void initButton() 
    {
        mButtonList.addLast(
            GraphicHelper.createButton(
                sContinueButtonText,
                sContinueButtonPosition.x,
                sContinueButtonPosition.y ));
        mButtonList.getLast().setCallBack(() -> onContinueClick());

        mButtonList.addLast(
            GraphicHelper.createButton(
                sSaveButtonText,
                sSaveButtonPosition.x,
                sSaveButtonPosition.y));
        mButtonList.getLast().setCallBack(() -> onSaveClick());

        mButtonList.addLast(
            GraphicHelper.createButton(
                sQuitGameButtonText,
                sQuitApplicationButtonPosition.x,
                sQuitApplicationButtonPosition.y));
        mButtonList.getLast().setCallBack(() -> onQuitClick());
    }

    @Override protected void initScene() 
    {
        mScene = SceneType.PauseMenu;
    }

    @Override protected void initBackground()
    {
        mBackGround = MenuGame.getInstance().getBackGround();
    }

    private void onContinueClick()
    {
        sendMsg(MenuManager.getInstance(), SceneType.Game.ordinal());
    }

    private void onSaveClick()
    {
        GameManager.getInstance().saveLevelProgress();
    }

    private void onQuitClick()
    {
        GameManager.getInstance().forceEndGame();

        sendMsg(MenuManager.getInstance(),
            GameManager.getInstance().isCustomLevel() ? SceneType.Edit.ordinal() : SceneType.Menu.ordinal());
    }
}
