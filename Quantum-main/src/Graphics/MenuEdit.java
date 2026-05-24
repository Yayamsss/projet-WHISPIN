public class MenuEdit extends GraphicMenuBase
{
    private static MenuEdit sInstance = null;

    private static final float sButtonWidth = 260;
    private static final float sButtonHeight = 55;

    private static final String sLoadButtonText = "CHARGER";
    private static final Vector2 sLoadButtonPosition = new Vector2(
        GraphicsModule.getWitdh() - 230,
        150
    );
    private static final String sSaveButtonText = "SAUVEGARDER";
    private static final Vector2 sSaveButtonPosition = new Vector2(
        GraphicsModule.getWitdh() - 230,
        230
    );
    private static final String sPlayButtonText = "JOUER";
    private static final Vector2 sPlayButtonPosition = new Vector2(
        GraphicsModule.getWitdh() - 230,
        310
    );
    private static final String sBackButtontext = "RETOUR";
    private static final Vector2 sBackButtonPosition = new Vector2(
        GraphicsModule.getWitdh() - 230,
        390
    );
    
    private EditorView mEditorView;

    public static MenuEdit getInstance()
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
        mEditorView = null;
        super.onCreate();
        mTitle.setPosition(new Vector2(sTitlePosition.x, 80.0f));
    }

    @Override protected void initButton()
    {
        this.addButton(sLoadButtonText, sLoadButtonPosition, () -> onLoadClick());
        this.addButton(sSaveButtonText, sSaveButtonPosition, () -> onSaveClick());
        this.addButton(sPlayButtonText, sPlayButtonPosition, () -> onPlayClick());
        this.addButton(sBackButtontext, sBackButtonPosition, () -> onBackClick());
    }

    @Override protected void initScene() 
    {
        mScene = SceneType.Edit;
    }

    @Override protected void initBackground()
    {
        mBackGround = new GraphicBackGround();
    }

    @Override protected void onEnabled() 
    {
        super.onEnabled();
        mTitle.getText().setText("EDITION");

        if (mEditorView == null)
        {
            mEditorView = GameLoop.getInstance().addObject(EditorView.class);
        }
    }

    @Override protected void onDisabled()
    {
        super.onDisabled();

        if (mEditorView != null)
        {
            mEditorView.destroy();
            mEditorView = null;
        }
    }

    private void addButton(String text, Vector2 position, ButtonCallBack callBack)
    {
        GraphicButton newButton = GraphicHelper.createButton(
            GraphicButton.class,
            text,
            position.x,
            position.y,
            sButtonWidth,
            sButtonHeight);

        newButton.setCallBack(callBack);
        mButtonList.add(newButton);
    }

    private void onLoadClick()
    {
        if (mEditorView.loadCustomLevel())
        {
            mTitle.getText().setText("Chargé : " + GameManager.getCustomLevelPath());
        }
        else
        {
            mTitle.getText().setText("Erreur lors du chargement");
        }
    }

    private void onSaveClick()
    {
        if (mEditorView.saveCustomLevel())
        {
            mTitle.getText().setText("Sauvegardé : " + GameManager.getCustomLevelPath());
        }
        else
        {
            mTitle.getText().setText("Erreur lors de la sauvegarde");
        }
    }

    private void onPlayClick()
    {
        if (!mEditorView.canPlayCustomLevel())
        {
            mTitle.getText().setText(mEditorView.getPlayabilityError());
            return;
        }

        if (!mEditorView.saveCustomLevel())
        {
            mTitle.getText().setText("Erreur sauvegarde");
            return;
        }

        sendMsg(MenuManager.getInstance(), SceneType.Game.ordinal());
        sendMsg(MenuLevel.getInstance(), 0);
        GameManager.getInstance().setNextLevel(GameManager.getCustomLevelId());
        GameManager.getInstance().startNewGame();
    }

    private void onBackClick()
    {
        sendMsg(MenuManager.getInstance(), SceneType.Level.ordinal());
        sendMsg(MenuLevel.getInstance(), SceneType.Level.ordinal());
    }
}