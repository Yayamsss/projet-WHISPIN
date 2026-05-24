import javafx.scene.text.Font;

public class MenuGame extends GraphicMenuBase
{
    private static MenuGame sInstance = null;
    
    /* Attribut pour les bouttons */
    private static final String sFont = "Arial";
    private static final float sGameButtonsWitdh = 60; 
    private static final float sGameButtonsHeight = 70;
    private static final float sGameButtonsOpacity = 0;
    private static final float sGameButtonsTextSize = 75;
    private static final Vector2 sGameButtonsTextOffset = new Vector2(0, 27.5f);
    private static final String sPauseMenuButtonText = "\u2630"; 
    private static final Vector2 sPauseMenuButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 15.0f, 
        GraphicsModule.getHeight() / 9.7f
    ); 
    private static final String sResetButtonText = "\u27F3"; 
    private static final Vector2 sResetButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 15.0f, 
        GraphicsModule.getHeight() / 9.7f + 85
    );
    private static final float sResetMaxRotation = (float) Math.toDegrees(2*Math.PI);

    private GraphicHeart[] mHearts;
    private GraphicTimer mTimer;
    
    public static MenuGame getInstance()
    {
        return sInstance;
    }

    @Override public void onCreate() 
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();    
        }

        mHearts = GraphicHelper.createHearts(BoxPlayer.getNumLivesAtStart());
        mTimer = GameLoop.getInstance().addObject(GraphicTimer.class);

        super.onCreate();

        sInstance = this;
    }

    public GraphicText getTimer()
    {
        return mTimer.getTime();
    }

    @Override protected void initScene()
    {
        mScene = SceneType.Game;
    }

    @Override protected void initBackground()
    {
        mBackGround = new GraphicBackGround();

        ElementMoon moon = GameLoop.getInstance().addObject(ElementMoon.class);
        ElementAntenna antenna = GameLoop.getInstance().addObject(ElementAntenna.class);
        ElementBuilding building1 = GameLoop.getInstance().addObject(ElementBuilding.class);
        ElementBuilding building2 = GameLoop.getInstance().addObject(ElementBuilding.class);

        GraphicLine line = GraphicHelper.createLineObj(
            GraphicsModule.getWitdh(),
            5,
            0.0f,
            GraphicHelper.makeColorFromHex(0xA020F0),
            0);

        moon.setPosition(new Vector2(444, 204));
        line.setPosition(new Vector2(0, 780));
        antenna.setPosition(new Vector2(
            300,
            780 - antenna.getBuilding().getHeight() / 2));
        building1.setPosition(new Vector2(
            350,
            780 - building1.getBuilding().getHeight() / 2));
        building2.setPosition(new Vector2(
            1630,
            780 - building2.getBuilding().getHeight() / 2));

        mBackGround.addObject(moon);
        mBackGround.addObject(line.getGameObject());
        mBackGround.addObjects(GraphicHelper.createStarSky(75));
        mBackGround.addObjects(GraphicHelper.createCars());
        mBackGround.addObject(antenna);
        mBackGround.addObject(building1);
        mBackGround.addObject(building2);
        mBackGround.addObjects(GraphicHelper.createRoad(90.0f, 780.0f));
    }

    @Override protected void initButton()
    {
        mButtonList.addLast(
            GraphicHelper.createButton(
                GraphicIconeButton.class, 
                sPauseMenuButtonText, 
                sPauseMenuButtonPosition.x, 
                sPauseMenuButtonPosition.y, 
                sGameButtonsWitdh, 
                sGameButtonsHeight));

        mButtonList.getLast().getShape().setOpacity(sGameButtonsOpacity);
        mButtonList.getLast().getGraphicText().setFont(new Font(sFont, sGameButtonsTextSize));
        mButtonList.getLast().getGraphicText().setOffset(sGameButtonsTextOffset.x, sGameButtonsTextOffset.y);
        mButtonList.getLast().setCallBack(() -> onPauseClick());
        
        mButtonList.addLast(
            GraphicHelper.createButton(
                GraphicIconeButton.class, 
                sResetButtonText, 
                sResetButtonPosition.x, 
                sResetButtonPosition.y, 
                sGameButtonsWitdh, 
                sGameButtonsHeight
            )
        );

        GraphicIconeButton tmp = (GraphicIconeButton) mButtonList.getLast();
        tmp.getShape().setOpacity(sGameButtonsOpacity);
        tmp.getGraphicText().setFont(new Font(sFont, sGameButtonsTextSize));
        tmp.getGraphicText().setOffset(sGameButtonsTextOffset.x, sGameButtonsTextOffset.y);
        tmp.setMaxRotation(sResetMaxRotation);
        tmp.setCallBack(() -> onResetClick());
    }

    public void onPauseClick()
    {
        sendMsg(MenuManager.getInstance(), SceneType.PauseMenu.ordinal());
    }

    public void onResetClick()
    {
        GameManager.getInstance().restartGame();
    }

    @Override protected void onEnabled() 
    {
        super.onEnabled();

        for (int i = 0; i < mHearts.length; i++)
        {
            mHearts[i].setEnabled(true);
        }

        mTimer.setHideMode(false);
        mTimer.startTimer();
    }

    @Override protected void onDisabled() 
    {
        super.onDisabled();
        
        for (int i = 0; i < mHearts.length; i++)
        {
            mHearts[i].setEnabled(false);
        }

        mTimer.setHideMode(true);
    }

    @Override public void setHidden(boolean hidden)
    {
        super.setHidden(hidden);

        for (int i = 0; i < mHearts.length; i++)
        {
            mHearts[i].setHidden(hidden);
        }
    }
}
