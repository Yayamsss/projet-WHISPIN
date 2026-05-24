import javafx.scene.paint.Color;

public class GraphicPlayButton extends GraphicButton
{
    private boolean mIsLocked;
    private GraphicText mDescription;
    private GraphicStarPlay mStars;
    private int mLevelId;

    /* Attribut pour le rectangle */
    private static final float sWitdh = 300;
    private static final float sHeight = 175;

    /* Attribut pour le texte */
    private static final float sTextSize = 40;
    private static final Vector2 sTextOffset = new Vector2(0, -35);
    private static final Color sLockedTitleColor = sTextColor.grayscale();
    private static final Color sUncompletedTitleColor = sLineColor.darker();
    private static final Color sCompletedTitleColor = Color.YELLOW;
    private static final Color sLastLevelTitleColor = Color.RED;

    private static final Color sLockedLinesColor = sLineColor.grayscale();

    /* Attribut pour la description */
    private static final Color sDescriptionColor = GraphicHelper.makeColorFromHex(0xe0c0ff);
    private static final Color sLockedDescriptionColor = sDescriptionColor.grayscale();
    private static final float sDescriptionSize = 18;

    private static final GameObjectState<GraphicPlayButton> sLocked = new GameObjectState<GraphicPlayButton>() {
        public void execute(GraphicPlayButton obj) { obj.exeLocked(); }
    };

    @Override public void onCreate()
    {
        mCallBack = null;
        mIsLocked = true;

        this.initShape(sWitdh, sHeight);
        this.initText(sTextSize);
        this.initLines();
    
        mTextShape.setOffset(sTextOffset.x, sTextOffset.y);

        mDescription = new GraphicText();
        mDescription.init(
            "", 
            this, 
            sDescriptionColor, 
            sLayer+1, 
            sDescriptionSize);

        mStars = GameLoop.getInstance().addObject(GraphicStarPlay.class);
        mStars.setButton(this);

        this.setWidth(sWitdh);
        this.setHeight(sHeight);
        this.setState(sLocked);
    }

    @Override protected void onEnabled()
    {
        this.setHidden(false);
        this.setState(mIsLocked ? sLocked : sWait);
    }

    public int getLevelId()
    {
        return mLevelId;
    }

    public void setDescriptionText(String _text)
    {
        mDescription.setText(_text);
    }

    public void setLevelId(int levelId)
    {
        mLevelId = levelId;
    }

    public void lock()
    {
        mIsLocked = true;
    }

    public void unLock()
    {
        mIsLocked = false;
    }

    @Override public void setHidden(boolean hidden)
    {
        super.setHidden(hidden);

        mDescription.setHiden(hidden);
        mStars.setHidden(hidden);
    }

    @Override public void setPosition(Vector2 newPosition)
    {
        super.setPosition(newPosition);
        mStars.setPosition(new Vector2(newPosition.x, newPosition.y + 50.0f));
    }

    @Override protected void exeWait() 
    {   
        if (isFirstStep())
        {            
            mTopLine.setColor(sLineColor);
            mBottomLine.setColor(sLineColor);
            mDescription.setColor(sDescriptionColor);
        }

        int unlockedLevel = GameManager.getInstance().getUnlockedLevelId();

        if (unlockedLevel > mLevelId)
        {
            mTextShape.setColor(sCompletedTitleColor);
        }
        else
        {
            mTextShape.setColor(
                mLevelId == GameManager.getNumLevels() - 1 ?
                    sLastLevelTitleColor :
                    sUncompletedTitleColor);
        }

        super.exeWait();

        if (mIsLocked)
        {
            this.setState(sLocked);
        }
    }

    private void exeLocked()
    {
        if (this.isFirstStep())
        {
            mTextShape.setColor(sLockedTitleColor);
            mTopLine.setColor(sLockedLinesColor);
            mBottomLine.setColor(sLockedLinesColor);
            mDescription.setColor(sLockedDescriptionColor);
        }

        if (!mIsLocked)
        {
            this.setState(sWait);
        }
    }
}
