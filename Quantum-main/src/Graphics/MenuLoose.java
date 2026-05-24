import java.util.Random;

public class MenuLoose extends MenuEndGame
{
    private static MenuLoose sInstance = null;

    /* Attribut pour le titre */
    private static final String sTitle = "DÉFAITE";
    /* Attribut pour description */
    private static final String[] sTab = {
        "L'obscurité vous a réclamé...",
        "Le donjon garde vos os pour l'éternité.",
        "Vos cris se perdent dans les profondeurs.",
        "Le silence du donjon engloutit votre dernier souffle.",
        "Une âme de plus pour les ténèbres.",
        "Le donjon se souvient. Vous, non.",
        "D'autres ont échoué ici avant vous. D'autres échoueront après.",
        "Vous pensiez être le héros de cette histoire...",
        "Le donjon n'attendait que ça.",
        "Encore un aventurier de moins.",
        "Les ténèbres l'emportent.",
        "Fin du voyage."
    };

    @Override public void onCreate() 
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();
        }
        super.onCreate();
        sInstance = this;

        mTitle.setText(sTitle);
    }

    public static MenuLoose getInstance()
    {
        return sInstance;
    }

    @Override protected void initScene() 
    {
        mScene = SceneType.Loose;    
    }

    @Override protected void initBackground() 
    {
        mBackGround = new GraphicBackGround();
        mBackGround.setColor(GraphicHelper.makeColorFromHex(0x0d0000));

        mBackGround.addObjects(GraphicHelper.generateLineBackground(0xff0000));
        
        int[] ovalColor = {
            0x290000,
            0x660202,
            0xb50404,
            0xff0000
        };
        mBackGround.addObjects(GraphicHelper.generateOvalBackground(ovalColor));
        
        GameObject textObj = GameLoop.getInstance().addObject(GameObject.class);
        textObj.setPosition(new Vector2(
            GraphicsModule.getWitdh() / 2,
            GraphicsModule.getHeight() / 3 - 200
        ));
        GraphicText text = textObj.setGraphicShape(GraphicText.class);
        text.init(
            "-   GameOver   -", 
            textObj, 
            GraphicHelper.makeColorFromHex(0xc40000), 
            5, 
            40
        );
        mBackGround.addObject(textObj);

        GameObject lineObj0 = GameLoop.getInstance().addObject(GameObject.class);
        lineObj0.setPosition(new Vector2(0, 220));
        GraphicLine line0 = lineObj0.setGraphicShape(GraphicLine.class);
        line0.init(
            lineObj0, 
            GraphicsModule.getWitdh() / 2 - 300, 
            12, 
            0, 
            GraphicHelper.makeColorFromHex(0xff0000), 
            5
        );

        GameObject lineObj1 = GameLoop.getInstance().addObject(GameObject.class);
        lineObj1.setPosition(new Vector2(
            GraphicsModule.getWitdh(), 
            220));
        GraphicLine line1 = lineObj1.setGraphicShape(GraphicLine.class);
        line1.init(
            lineObj1, 
            GraphicsModule.getWitdh() / 2 - 300, 
            12, 
            90, 
            GraphicHelper.makeColorFromHex(0xff0000), 
            5
        );
        mBackGround.addObject(lineObj0);
        mBackGround.addObject(lineObj1);
    }

    @Override protected void onEnabled() 
    {
        Random random = new Random();
        mGameMessage.setText(sTab[random.nextInt(sTab.length)]);

        super.onEnabled();
    }
}