import java.util.LinkedList;
import javafx.scene.text.Font;

public class MenuLevel extends GraphicMenuBase 
{
    private static MenuLevel sInstance = null;

    private static final GameObjectState<MenuLevel> sSlide = new GameObjectState<MenuLevel>() {
        public void execute(MenuLevel obj) { obj.exeSlide(); } 
    };

    /* Attribut pour le titre */
    private static final String sTitle = "LEVEL";
    private static final String[] sLevelTitles = new String[]
    {
        "L'Éveil",
        "Le Premier Pas",
        "Un Autre Monde",
        "Le Passage Secret",
        "No Man's Land",
        "Le Point de Non-Retour", // A changé
        "L'Ascension",
        "Terres Inconnues", // A changé
        "Le Labyrinthe",
        "Une Épreuve de Force",
        "Face aux Mondes",
        "The End"
    };

    private static final float sLevelButtonGapX = 350.0f;
    private static final float sMiddleLevelButtonX = GraphicsModule.getWitdh() / 2;
    private static final float sLevelButtonTopRowY = GraphicsModule.getHeight() / 2.5f;
    private static final float sLevelButtonBottompRowY = GraphicsModule.getHeight() / 1.7f;

    private static final String sFont = "Arial";
    private static final String sBackButtonText = "\u2190";
    private static final Vector2 sBackButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 15,
        GraphicsModule.getHeight() / 9.7f
    );
    private static final float sBackButtonWitdh = 100;
    private static final float sBackButtonHeight = 50; 
    private static final float sBackButtonTextSize = 75;
    private static final Vector2 sBackButtonTextOffset = new Vector2(0, 25);

    private static final String sEditButtonText = "🖊️";
    private static final Vector2 sEditButtonPosition = new Vector2(
        GraphicsModule.getWitdh() / 15,
        GraphicsModule.getHeight() / 6
    );
    private static final float sEditButtonWitdh = 100;
    private static final float sEditButtonHeight = 50;
    private static final float sEditButtonTextSize = 45;
    private static final Vector2 sEditButtonTextOffset = new Vector2(0, 17);
    
    private static final float sResetSaveButtonWidth = 250;
    private static final float sResetSaveButtonHeight = 50;

    private static final String sResetSaveButtonText = "RESET";
    private static final Vector2 sResetLevelButtonPosition = new Vector2(
        GraphicsModule.getWitdh() - 280,
        GraphicsModule.getHeight() - 200
    );

    private static final String sUnlockLevelButtonText = "UNLOCK";
    private static final Vector2 sUnlockLevelButtonPosition = new Vector2(
        GraphicsModule.getWitdh() - 280,
        GraphicsModule.getHeight() - 140
    );

    private static final String sRestoreProgressButtonText = "RESTORE PROGRESS";
    private static final Vector2 sRestoreProgressButtonPosition = new Vector2(
        GraphicsModule.getWitdh() - 280,
        GraphicsModule.getHeight() - 260
    );

    private static final String sSlideButtonTextToRight = "\u27A5";
    private static final Vector2 sSlideButtonPositionRight = new Vector2(
        GraphicsModule.getWitdh() - 150,
        GraphicsModule.getHeight() / 2.03f
    );
    private static final Vector2 sSlideButtonPositionLeft = new Vector2(
        150,
        GraphicsModule.getHeight() / 2.03f
    );
    private static final float sSlideButtonWitdh = 100;
    private static final float sSlideButtonHeight = 50; 
    private static final float sSlideButtonTextSize = 75;
    private static final Vector2 sSlideButtonTextOffset = new Vector2(0, 25);
    
    /* Attribut pour l'animation */
    private static final int sSlideDepartureTimeOffset = 4;
    private static final int sSlideTime = 20;
    private static final int sAnimationTime = sSlideTime + GameManager.getNumLevels() * sSlideDepartureTimeOffset;

    private float mSlideDirection;
    private LinkedList<Vector2> mButtonStartPositionList;
    private LinkedList<Vector2> mButtonEndPositionList;

    @Override public void onCreate() 
    {
       if (sInstance != null)
        {
            throw new IllegalAccessError();
        } 

        mButtonStartPositionList = new LinkedList<>();
        mButtonEndPositionList = new LinkedList<>();
        super.onCreate();

        sInstance = this;

        GraphicText text = this.getTitleText();
        text.setText(sTitle);

        mSlideDirection = -1.0f;
    }

    public static MenuLevel getInstance()
    {
        return sInstance;
    }

    private GraphicPlayButton getPlayButton(int i)
    {
        return (GraphicPlayButton) mButtonList.get(i);
    }

    private GraphicPlayButton getLastPlayButton()
    {
        if (mButtonList.getLast() instanceof GraphicPlayButton)
        {
            return (GraphicPlayButton) mButtonList.getLast();
        }
        
        return null;
    }

    private GraphicIconeButton getSlideButton()
    {
        if (mButtonList.getLast() instanceof GraphicIconeButton)
        {
            return (GraphicIconeButton) mButtonList.getLast();
        }
        
        return null;
    }

    private void addPlayButton(String text, String description, Vector2 position, int rank)
    {
        mButtonList.addLast(
            GraphicHelper.createButton(
                GraphicPlayButton.class, 
                text, 
                position.x, 
                position.y
            )
        );
        this.initButtonStartAndEndPositions(this.getLastPlayButton());
        this.getLastPlayButton().setDescriptionText(description);
        this.getLastPlayButton().setLevelId(rank);
        mButtonList.getLast().setCallBack(() -> startGame(rank));
    }

    private void addMiscButton(
        float width,
        float heigth,
        String text,
        float textSize,
        Vector2 textOffset,
        Vector2 position,
        ButtonCallBack callBack)
    {
        GraphicIconeButton miscButton = GraphicHelper.createButton(
            GraphicIconeButton.class, 
            text, 
            position.x, 
            position.y, 
            width, 
            heigth);
        miscButton.disableRotation();
        miscButton.setCallBack(callBack);
        mButtonList.addLast(miscButton);
        
        GraphicText miscButtonText = miscButton.getGraphicText();

        miscButtonText.setFont(new Font(sFont, textSize));
        miscButtonText.setOffset(textOffset.x, textOffset.y);
    }

    @Override protected void initButton()
    {
        Vector2 currentPosition = new Vector2();
        for (int i = 0; i < 2; i++)
        {
            float pageOffsetX = i * GraphicsModule.getWitdh();

            for (int j = 0; j < 6; j++)
            {
                int index = i * 6 + j;
                String levelIndexString = String.format(index < 9 ? "0%d" : "%d", index + 1);

                currentPosition.x = sMiddleLevelButtonX + sLevelButtonGapX * (float)((j % 3) - 1) + pageOffsetX;
                currentPosition.y = j < 3 ? sLevelButtonTopRowY : sLevelButtonBottompRowY;

                this.addPlayButton(
                    levelIndexString,
                    sLevelTitles[index],
                    currentPosition,
                    index);
            }
        }
        
        this.addMiscButton(
            sBackButtonWitdh,
            sBackButtonHeight,
            sBackButtonText,
            sBackButtonTextSize,
            sBackButtonTextOffset,
            sBackButtonPosition,
            () -> onBackClick());
        this.addMiscButton(
            sEditButtonWitdh,
            sEditButtonHeight,
            sEditButtonText,
            sEditButtonTextSize,
            sEditButtonTextOffset,
            sEditButtonPosition,
            () -> onEditClick());

        mButtonList.add(GraphicHelper.createButton(
            sResetSaveButtonText,
            sResetLevelButtonPosition.x,
            sResetLevelButtonPosition.y,
            sResetSaveButtonWidth,
            sResetSaveButtonHeight,
            () -> onResetSaveClick()));
        mButtonList.add(GraphicHelper.createButton(
            sUnlockLevelButtonText,
            sUnlockLevelButtonPosition.x,
            sUnlockLevelButtonPosition.y,
            sResetSaveButtonWidth,
            sResetSaveButtonHeight,
            () -> onUnLockSaveClick()));
        
        mButtonList.add(GraphicHelper.createButton(
            sRestoreProgressButtonText,
            sRestoreProgressButtonPosition.x,
            sRestoreProgressButtonPosition.y,
            sResetSaveButtonWidth,
            sResetSaveButtonHeight,
            () -> onRestoreClick()));

        this.addMiscButton(
            sSlideButtonWitdh,
            sSlideButtonHeight,
            sSlideButtonTextToRight,
            sSlideButtonTextSize,
            sSlideButtonTextOffset,
            sSlideButtonPositionRight,
            () -> onSlideClick());
    }

    private void startGame(int i)
    {
        sendMsg(MenuManager.getInstance(), SceneType.Game.ordinal());

        GameManager.getInstance().setNextLevel(i);
        GameManager.getInstance().startNewGame();
    }

    private void onBackClick()
    {
        sendMsg(MenuManager.getInstance(), SceneType.Menu.ordinal());
        sendMsg(MenuMain.getInstance(), SceneType.Menu.ordinal());
    }

    private void onEditClick()
    {
        sendMsg(MenuManager.getInstance(), SceneType.Edit.ordinal());
    }

    private void onResetSaveClick()
    {
        GameManager.getInstance().resetSave();
    }

    private void onUnLockSaveClick()
    {
        GameManager.getInstance().unlockAllLevels();
    }

    private void onRestoreClick()
    {
        GameManager.getInstance().restoreLevelProgress();

        if (GameManager.getInstance().isGameRuning())
        {
            sendMsg(MenuManager.getInstance(), SceneType.Game.ordinal());
        }
    }

    private void onSlideClick()
    {
        this.setState(sSlide);
    }

    @Override protected void initScene() 
    {
        mScene = SceneType.Level;
    }

    @Override protected void initBackground()
    {
        mBackGround = new GraphicBackGround();

        for (GameObject obj : MenuStart.getInstance().getBackGround().getObjectGroup())
        {
            if (obj instanceof ElementRain)
            {
                continue;
            }

            mBackGround.addObject(obj);
        }
    }

    @Override protected void onEnabled() 
    {
        super.onEnabled();

        GameCamera.getInstance().focus(new Vector2(325, 60), 5);
    }

    @Override protected void onDisabled()
    {
        setState(sWait);
        super.onDisabled();

        GameCamera.getInstance().resetFocus();
    }

    private void exeSlide()
    {
        if (isFirstStep())
        {
            mSlideDirection *= -1.0f;
            GraphicIconeButton slideButton = this.getSlideButton();

            if (mSlideDirection > 0.0f)
            {
                slideButton.setPosition(sSlideButtonPositionLeft);
                slideButton.setRotation(180.0f);
            }
            else
            {
                slideButton.setPosition(sSlideButtonPositionRight);
                slideButton.setRotation(0.0f);
            }
        }

        for (int i = 0; i < GameManager.getNumLevels(); i++)
        {
            int buttonSlideDepartureStep = (mSlideDirection > 0.0f ? i : GameManager.getNumLevels() - i - 1) * sSlideDepartureTimeOffset;

            if (this.isLessStep(buttonSlideDepartureStep))
            {
                continue;
            }

            int buttonAnimStep = this.getStep() - buttonSlideDepartureStep;
            float t = Math.clamp((float) (buttonAnimStep) / (float) (sSlideTime), 0.0f, 1.0f);

            if (mSlideDirection > 0.0f)
            {
                t = Easing.easeOutQuart(t);
            }
            else
            {
                t = Easing.easeInQuart(1.0f - t);
            }

            Vector2 newPos = Vector2.lerp(
                mButtonStartPositionList.get(i),
                mButtonEndPositionList.get(i),
                t);

            mButtonList.get(i).setPosition(newPos);
        }

        if (this.isAboveOrEqualStep(sAnimationTime))
        {
            this.setState(sWait);
        }
    }

    @Override protected void exeWait()
    {
        int unlockedLevel = GameManager.getInstance().getUnlockedLevelId();
        int numLevels = GameManager.getNumLevels();

        for (int i = 0; i < numLevels; i++)
        {
            GraphicPlayButton button = this.getPlayButton(i);

            if (i <= unlockedLevel)
            {
                button.unLock();
            }
            else
            {
                button.lock();
            }
        }
    }

    private void initButtonStartAndEndPositions(GraphicPlayButton button)
    {
        Vector2 position = button.getPosition();

        mButtonStartPositionList.addLast(new Vector2(position));
        mButtonEndPositionList.addLast(new Vector2(
            position.x - GraphicsModule.getWitdh(),
            position.y));
    }
}
