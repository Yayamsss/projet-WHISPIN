import java.util.LinkedList;
import javafx.scene.paint.Color;

public abstract class GraphicMenuBase extends GameObjectEx 
{
    protected GraphicsModule mGfxModule;
    protected GameCamera mCamera;
    
    protected GraphicBackGround mBackGround;
    protected GraphicTitle mTitle;
    protected LinkedList<GraphicButton> mButtonList;
    protected SceneType mScene;

    /* Attribut pour le titre */
    protected static final Color sTitleColor = GraphicHelper.makeColorFromHex(0xe0c0ff);
    protected static final int sTitleLayer = 31;
    protected static final float sTitleSize = 80;
    protected static final Vector2 sTitlePosition = new Vector2(
        GraphicsModule.getWitdh() / 2,
        140
    );

    /* Attribut pour les boutons */
    private static final float sButtonOpacity = 0.01f;
    private static final float sButtonTextOpacity = 0.1f;
    private static final float sButtonLineOpacity = 0.1f;
    
    protected static final GameObjectState<GraphicMenuBase> sWait = new GameObjectState<GraphicMenuBase>() {
        public void execute(GraphicMenuBase obj) { obj.exeWait(); }
    };

    @Override public void onCreate()
    {
        mGfxModule = GraphicsModule.getInstance();
        mCamera = GameCamera.getInstance();

        mTitle = GameLoop.getInstance().addObject(GraphicTitle.class);
        mTitle.startBlink();
        
        mButtonList = new LinkedList<>();

        this.initScene();
        this.initBackground();
        this.initButton();

        this.setEnabled(false);
        this.setHidden(true);

        setState(sWait);
    }

    @Override protected void onEnabled()
    {
        this.updateEnableState();
    }

    @Override protected void onDisabled() 
    {
        this.updateEnableState();
    }

    @Override public void setHidden(boolean hidden)
    {
        mTitle.setHidden(hidden);

        for (GraphicButton button : mButtonList)
        {
            button.setHidden(hidden);
        }

        for (GameObject obj : mBackGround.getObjectGroup())
        {
            if (obj.getGraphicShape() == null)
            {
                continue;
            }

            obj.setHidden(hidden);
        }
    }

    private void updateEnableState()
    {
        mTitle.setEnabled(isEnabled());

        for (GraphicButton button : mButtonList)
        {
            button.setEnabled(isEnabled());
        }

        for (GameObject obj : mBackGround.getObjectGroup())
        {
            if (obj.getGraphicShape() == null)
            {
                continue;
            }

            obj.setEnabled(isEnabled());
        }
    }

    public GraphicBackGround getBackGround()
    {
        return mBackGround;
    }

    public GraphicTitle getTitleObject()
    {
        return mTitle;
    }

    public GraphicText getTitleText()
    {
        return mTitle.getText();
    }
    
    public LinkedList<GraphicButton> getButtonList()
    {
        return mButtonList;
    }

    public SceneType getScene()
    {
        return mScene;
    }

    /* Fonction a Override */
    protected abstract void initScene();
    protected abstract void initBackground();

    /* Fonction a Override si besoin */
    protected void initButton() {}
    protected void exeWait() {}

    protected boolean increaseOpacity()
    {
        if (mButtonList == null)
        {
            return false;
        }

        GraphicButton button = mButtonList.getFirst();
        float opacityButton = button.getShape().getOpacity() + sButtonOpacity;
        float opacityButtonText = button.getGraphicText().getOpacity() + sButtonTextOpacity;
        float opacityLine = button.getTopLine().getOpacity() + sButtonLineOpacity;

        if (opacityButton < GraphicButton.getMinOpacity())
        {
            for (GraphicButton buttonInList : mButtonList)
                buttonInList.getShape().setOpacity(opacityButton);
        }
        else
        {
            for (GraphicButton buttonInList : mButtonList)
                buttonInList.getShape().setOpacity(GraphicButton.getMinOpacity());
        }

        if (opacityButtonText < 1)
        {
            for (GraphicButton buttonInList : mButtonList)
                buttonInList.getGraphicText().setOpacity(opacityButtonText);
        }
        else
        {
            for (GraphicButton buttonInList : mButtonList)
                buttonInList.getGraphicText().setOpacity(1);
        }

        if (opacityLine < 1)
        {
            for (GraphicButton buttonInList : mButtonList)
            {
                buttonInList.getTopLine().setOpacity(opacityLine);
                buttonInList.getBottomLine().setOpacity(opacityLine);
            }
        }
        else
        {
            for (GraphicButton buttonInList : mButtonList)
            {
                buttonInList.getTopLine().setOpacity(1);
                buttonInList.getBottomLine().setOpacity(1);
            }
        }

        return opacityButton >= GraphicButton.getMinOpacity() && opacityButtonText >= 1.0f && opacityLine >= 1.0f;
    }
}