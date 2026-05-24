import java.util.ArrayList;
import javafx.scene.paint.Color;

public class MenuStart extends GraphicMenuBase
{
    private static MenuStart sInstance = null;

    private GraphicText mStartText;

    /* Attribut pour le message */
    private static final String sStartMessage = "A Quantum Project";
    private static final Vector2 sPositionStartMessage = new Vector2(
        GraphicsModule.getWitdh() / 2,
        GraphicsModule.getHeight() / 2
    );
    private static final Color sMessageColor = Color.WHITE;
    private static final int sMessageLayer = 31;
    private static final float sMessageSize = 75;
    private static final float sMessageOpacityIncreaseSpeed = 0.01f;

    private static final GameObjectState<MenuStart> sAppear = new GameObjectState<MenuStart>() {
        public void execute(MenuStart obj) { obj.exeAppear(); }
    };
    private static final GameObjectState<MenuStart> sDisappear = new GameObjectState<MenuStart>() {
        public void execute(MenuStart obj) { obj.exeDisappear(); }
    };

    public static MenuStart getInstance()
    {
        return sInstance;
    }

    private static ArrayList<GameObject> createAntennas(float offsetX, float offsetY)
    {
        ArrayList<GameObject> antennaList = new ArrayList<>();

        for (int i = 0; i < 4; i++)
        {
            ElementAntenna antenna = GameLoop.getInstance().addObject(ElementAntenna.class);

            antenna.setPosition(new Vector2(
                offsetX + antenna.getBuilding().getWitdh() / 2,
                offsetY - antenna.getBuilding().getHeight() / 2));

            if (i == 1)
            {
                offsetX += GraphicsModule.getWitdh() / 1.7f;
            }

            offsetX += 45 + antenna.getBuilding().getWitdh();
            antennaList.add(antenna);
        }

        return antennaList;
    }

    @Override public void onCreate() 
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();
        }

        GameObject startTextObj = GameLoop.getInstance().addObject(GameObject.class);
        startTextObj.setGraphicShape(GraphicText.class);
        startTextObj.setPosition(sPositionStartMessage);

        mStartText = startTextObj.getGraphicShapeAs(GraphicText.class);
        mStartText.init(
            sStartMessage, 
            startTextObj, 
            sMessageColor, 
            sMessageLayer, 
            sMessageSize
        );

        super.onCreate();
        sInstance = this;

        setState(sAppear);
    }

    @Override protected void onEnabled()
    {
        super.onEnabled();
        
        if (mStartText == null)
            return;
        mStartText.setHiden(false);
    }

    @Override protected void onDisabled()
    {
        super.onDisabled();

        if (mStartText == null)
            return;
        mStartText.setHiden(true);
    }
    
    @Override protected void initScene() 
    {
        mScene = SceneType.Start;
    }

    @Override protected void initBackground()
    {
        mBackGround = new GraphicBackGround();

        mBackGround.addObjects(GraphicHelper.createStarSky(25));
        mBackGround.addObjects(GraphicHelper.createBuildings(780.0f, 10.0f));
        mBackGround.addObjects(GraphicHelper.createRain(6));

        GameObject lineObj = GameLoop.getInstance().addObject(GameObject.class);
        GraphicLine lineShape = lineObj.setGraphicShape(GraphicLine.class);

        lineObj.setPosition(new Vector2(0, 780));
        lineShape.init(
            lineObj,
            GraphicsModule.getWitdh(),
            5,
            0,
            GraphicHelper.makeColorFromHex(0xA020F0), 
            0);

        mBackGround.addObject(lineObj);
        mBackGround.addObjects(MenuStart.createAntennas(45.0f, 780.0f));
        mBackGround.addObjects(GraphicHelper.createCars());

        ElementMoon moon = GameLoop.getInstance().addObject(ElementMoon.class);
        moon.setPosition(new Vector2(222, 84));

        mBackGround.addObject(moon);
        mBackGround.addObjects(GraphicHelper.createRoad(90.0f, 780.0f));
    }

    public GraphicText getStartText()
    {
        return mStartText;
    }

    private boolean skipStartAnimation()
    {
        if (mGfxModule.isMouseJustPressed())
        {
            sendMsg(MenuManager.getInstance(), SceneType.Menu.ordinal());
            return true;
        }

        return false;
    }

    private void exeAppear()
    {
        if (isFirstStep())
        {
            mStartText.setOpacity(0);
        }
        
        if (skipStartAnimation())
        {
            return;
        }

        if (isLessOrEqualStep(this.timeToStep(1)))
        {
            return;
        }

        if (GraphicHelper.addOpacityWithClamp(mStartText, sMessageOpacityIncreaseSpeed))
        {
            setState(sDisappear);
        }
    }

    private void exeDisappear()
    {
        if (skipStartAnimation())
        {
            return; 
        }

        if (isLessOrEqualStep(this.timeToStep(3)))
        {
            return;
        }

        if (GraphicHelper.addOpacityWithClamp(mStartText, -sMessageOpacityIncreaseSpeed))
        {
            sendMsg(MenuManager.getInstance(), SceneType.Menu.ordinal());
        }
    }
}
