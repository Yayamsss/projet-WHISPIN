import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MenuCredit extends GraphicMenuBase
{
    private static MenuCredit sInstance = null;

    private GraphicText mCreditText;

    /* Attribut pour camera */
    private static final float sCreditPositionY = -3000;
    /* Attribut pour le titre */
    private static final String sTitle = "CREDITS";
    private static final float sTitleOpacityToAdd = 0.02f;
    /* Attribut pour le boutton */
    private static final String sBackButtonText = "\u2190";
    private static final Vector2 sBackButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 15,
        GraphicsModule.getHeight() / 9.7f
    );
    private static final float sBackButtonWitdh = 100;
    private static final float sBackButtonHeight = 50; 
    private static final String sBackButtonTextFont = "Arial";
    private static final float sBackButtonTextSize = 75;
    private static final Vector2 sBackButtonTextOffset = new Vector2(0, 25);
    /* Attribut pour le credit */
    private static final Vector2 sCreditPosition = new Vector2(
        GraphicsModule.getWitdh() / 5,
        sCreditPositionY + 350
    );
    private static final Color sCreditColor = GraphicHelper.makeColorFromHex(0xead9fa);
    private static final int sCreditLayer = 1;
    private static final float sCreditSize = 55;
    private static final TextAlignment sCreditAlignement = TextAlignment.LEFT;
    private static final float sCreditOpacityToAdd = 0.02f;

    private static final GameObjectState<MenuCredit> sCreditOpening = new GameObjectState<MenuCredit>() {
        public void execute(MenuCredit obj) { obj.exeCreditOpening(); }
    };
    private static final GameObjectState<MenuCredit> sCreditEnding = new GameObjectState<MenuCredit>() {
        public void execute(MenuCredit obj) { obj.exeCreditEnding(); }
    };

    @Override public void onCreate() 
    {
        if (sInstance != null)
        {
            throw new IllegalAccessError();
        }
        super.onCreate();

        sInstance = this;

        GraphicText text = this.getTitleText();
        text.setText(sTitle);
        initCreditText();
    }

    public static MenuCredit getInstance()
    {
        return sInstance;
    }

    public GraphicText getCreditText()
    {
        return mCreditText;
    }

    @Override protected void initButton() 
    {
        mButtonList.add(
            GraphicHelper.createButton(
                GraphicIconeButton.class, 
                sBackButtonText, 
                sBackButtonPosition.x, 
                sBackButtonPosition.y, 
                sBackButtonWitdh, 
                sBackButtonHeight
            )
        );
        mButtonList.getLast().getGraphicText().setOffset(sBackButtonTextOffset.x, sBackButtonTextOffset.y);
        mButtonList.getLast().getGraphicText().setFont(new Font(sBackButtonTextFont, sBackButtonTextSize));
        ((GraphicIconeButton)mButtonList.getLast()).disableRotation();
        mButtonList.getLast().setCallBack(() -> onBackClick());
    }

    private void onBackClick()
    {
        setState(sCreditEnding);
    }

    private void exeCreditOpening()
    {
        GraphicText title = this.getTitleText();

        if (isFirstStep())
        {
            title.setOpacity(0);
            mCreditText.setOpacity(0);
            mCamera.move(new Vector2(0, sCreditPositionY));
        }

        if (GraphicHelper.addOpacityWithClamp(mCreditText, sCreditOpacityToAdd)
         && GraphicHelper.addOpacityWithClamp(title, sTitleOpacityToAdd))
        {
            setState(sWait);
        }
    }

    private void exeCreditEnding()
    {
        if (isFirstStep())
        {
            mTitle.getText().setOpacity(0);
            mCamera.move(new Vector2(0,0));
        }

        MenuMain.getInstance().setOpacityScene(0);

        if (isAboveOrEqualStep(timeToStep(0.8f)))
        {
            sendMsg(MenuMain.getInstance(), SceneType.Menu.ordinal());
            sendMsg(MenuManager.getInstance(), SceneType.Menu.ordinal());
            setState(sWait);
        }
    }

    public void initCreditText()
    {
        GameObject creditTextObj = GameLoop.getInstance().addObject(GameObject.class);
        creditTextObj.setGraphicShape(GraphicText.class);
        creditTextObj.setPosition(sCreditPosition);

        mCreditText = creditTextObj.getGraphicShapeAs(GraphicText.class);

        String text = "Coordinateur : Aleksandar KREMIC\n" +
            "Interface graphique : Ali GÜRKAN\n" +
            "Modèle : Ryane MENAÏ\n" +
            "Persistance : Yanis ACHAB\n" +
            "Installation : Arthur IBARRA\n" +
            "Chemins : Abdelrahmane ISSA\n" +
            "Plateau : Sami AIT MEDDOUR\n" +
            "Résolution automatique : Abderrachid BELLOUM\n";

        mCreditText.init(
            text, 
            creditTextObj, 
            sCreditColor, 
            sCreditLayer, 
            sCreditSize
        );
        mCreditText.setTextAlignment(sCreditAlignement);
    }

    @Override protected void onEnabled() 
    {
        super.onEnabled();

        if (mCreditText==null)
            return;

        this.getCreditText().setHiden(false);    
    }

    @Override protected void onDisabled() 
    {
        super.onDisabled();

        if (mCreditText==null)
            return;
        
        this.getCreditText().setHiden(true);
    }

    @Override protected void receiveMsg(GameObject from, int msg)
    {
        if (!from.getClass().getName().equals("MenuMain"))
        {
            return;
        }

        if (msg == SceneType.Credit.ordinal())
        {
            setState(sCreditOpening);
        }
    }

    @Override protected void initScene() 
    {
        mScene = SceneType.Credit;
    }

    @Override protected void initBackground()
    {
        mBackGround = new GraphicBackGround();

        mBackGround.addObjects(MenuStart.getInstance().getBackGround().getObjectGroup());
        mBackGround.addObjects(GraphicHelper.createStarSky(
            175,
            new Vector2(0 ,GraphicsModule.getWitdh()), 
            new Vector2(- 3000 - GraphicsModule.getHeight() / 2, 0)));
    }
}
