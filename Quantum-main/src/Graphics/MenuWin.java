public class MenuWin extends MenuEndGame
{
    private static MenuWin sInstance = null;
    
    private GraphicStars mStars;

    /* Attribut pour le titre */
    private static final String sTitle = "VICTOIRE";
    /* Attribut pour les étoiles */
    private static final Vector2 sStarsPosition = new Vector2(
        GraphicsModule.getWitdh() / 2,
        GraphicsModule.getHeight() / 2 - 75
    );

    @Override public void onCreate() 
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();
        }

        mStars = GameLoop.getInstance().addObject(GraphicStars.class);
        mStars.setPosition(sStarsPosition);

        super.onCreate();
        sInstance = this;

        mTitle.setText(sTitle);
    }

    public static MenuWin getInstance()
    {
        return sInstance;
    }

    @Override protected void initScene() 
    {
        mScene = SceneType.Win;    
    }

    @Override protected void initBackground() 
    {
        mBackGround = new GraphicBackGround();
        mBackGround.setColor(GraphicHelper.makeColorFromHex(0x06000a));

        mBackGround.addObjects(GraphicHelper.generateLineBackground(0xae2bff));

        int[] ovalColor = {
            0x09000f,
            0x2c0147,
            0x6403a1,
            0x9f08fc
        };
        mBackGround.addObjects(GraphicHelper.generateOvalBackground(ovalColor));    

        float offsetBuilding = 21;
        for (int i = 0; i < 6; i++)
        {
            ElementBuilding building = GameLoop.getInstance().addObject(ElementBuilding.class);
            building.setPosition(new Vector2(
                offsetBuilding + building.getBuilding().getWitdh() / 2,
                GraphicsModule.getHeight() - building.getBuilding().getHeight() / 3 
            ));
            if (i == 2)
            {
                offsetBuilding += GraphicsModule.getWitdh() / 1.78f;
            }

            offsetBuilding += 21 + building.getBuilding().getWitdh();
            mBackGround.addObject(building);

            building.getBuilding().setLayer(35);
        }

        GameObject textObj = GameLoop.getInstance().addObject(GameObject.class);
        textObj.setPosition(new Vector2(
            GraphicsModule.getWitdh() / 2,
            GraphicsModule.getHeight() / 3 - 200
        ));
        

        GraphicText text = textObj.setGraphicShape(GraphicText.class);
        text.init(
            "-   Niveau Terminé   -", 
            textObj, 
            GraphicHelper.makeColorFromHex(0xe0c0ff), 
            36, 
            40
        );
        mBackGround.addObject(textObj);

        GameObject lineObj0 = GameLoop.getInstance().addObject(GameObject.class);
        lineObj0.setGraphicShape(GraphicLine.class);
        lineObj0.setPosition(new Vector2(0, 220));

        GraphicLine line0 = lineObj0.getGraphicShapeAs(GraphicLine.class);
        line0.init(
            lineObj0, 
            GraphicsModule.getWitdh() / 2 - 300, 
            12, 
            0, 
            GraphicHelper.makeColorFromHex(0x9d00ff), 
            36
        );

        GameObject lineObj1 = GameLoop.getInstance().addObject(GameObject.class);
        lineObj1.setGraphicShape(GraphicLine.class);
        lineObj1.setPosition(new Vector2(
            GraphicsModule.getWitdh(), 
            220));

        GraphicLine line1 = lineObj1.getGraphicShapeAs(GraphicLine.class);
        line1.init(
            lineObj1, 
            GraphicsModule.getWitdh() / 2 - 300, 
            12, 
            90, 
            GraphicHelper.makeColorFromHex(0x9d00ff), 
            36
        );
        mBackGround.addObject(lineObj0);
        mBackGround.addObject(lineObj1);
    }

    @Override protected void onEnabled() {
        super.onEnabled();

        mGameMessage.setText("Temps :     " 
            + MenuGame.getInstance().getTimer().getText());
        for (int i = 0; i<3; i++)
        {
            mStars.getStars(i).setHiden(false);
        }
        sendMsg(mStars, (int)GameManager.getInstance().getCompletionTime());
    }

    @Override protected void onDisabled() 
    {
        super.onDisabled();

        for (int i = 0; i<3; i++)
        {
            mStars.getStars(i).setHiden(true);
        }
    }
}